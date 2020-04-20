package com.spring.quartz.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import com.spring.quartz.QuartzStarter;

@Configuration
public class QuartzConfig {
   
   private static final Logger logger = LoggerFactory.getLogger(QuartzConfig.class);
   
   @Autowired
   private DataSource dataSource;
   
   @Autowired
   private PlatformTransactionManager transactionManager;
   
   @Autowired
   private ApplicationContext applicationContext;
   
   @Bean
   public SchedulerFactoryBean schedulerFactory() throws SchedulerException {
       logger.info("SchedulerFactoryBean created!");
       SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
       AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
       jobFactory.setApplicationContext(applicationContext);
       schedulerFactoryBean.setJobFactory(jobFactory);
       schedulerFactoryBean.setTransactionManager(transactionManager);
       schedulerFactoryBean.setDataSource(dataSource);
       schedulerFactoryBean.setOverwriteExistingJobs(true);
       schedulerFactoryBean.setAutoStartup(true);
       schedulerFactoryBean.setQuartzProperties(quartzProperties());
       
       return schedulerFactoryBean;
   }


   @Bean
   public Properties quartzProperties() {
       PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
       propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));

       Properties properties = null;
       try {
           propertiesFactoryBean.afterPropertiesSet();
           properties = propertiesFactoryBean.getObject();
       } catch (Exception e) {
          logger.warn("Cannot load quartz.properties");
       }
       return properties;
   }
   
   //https://advenoh.tistory.com/55
   //quartz job gracefully stop
   //scheduler만 shutdown 될 경우 gracefully stop이 의미있지만, was자체를 내려버리는 경우 의미가 없음
   @Bean
   public SmartLifecycle gracefulShutdownHookForQuartz(@Qualifier("schedulerFactory") SchedulerFactoryBean schedulerFactoryBean) {
      return new SmartLifecycle() {
         private boolean isRunning = false;
    
         @Override
         public boolean isAutoStartup() {
            return true;
         }
    
         @Override
         public void stop(Runnable callback) {
            stop();
            logger.info("Spring container is shutting down.");
            callback.run();
         }
    
         @Override
         public void start() {
            logger.info("Quartz Graceful Shutdown Hook started.");
            isRunning = true;
         }
    
         @Override
         public void stop() {
            isRunning = false;
            try {
               logger.info("Quartz Graceful Shutdown...");
               interruptJobs(schedulerFactoryBean);
               schedulerFactoryBean.destroy();
            } catch (SchedulerException e) {
               try {
                  logger.info("Error shutting down Quartz: ", e);
                  schedulerFactoryBean.getScheduler().shutdown(false);
               } catch (SchedulerException ex) {
                  logger.error("Unable to shutdown the Quartz scheduler.", ex);
               }
            }
         }

         @Override
         public boolean isRunning() {
            return isRunning;
         }
    
         @Override
         public int getPhase() {
            return Integer.MAX_VALUE;
         }
      };
   }
   
   
   private void interruptJobs(SchedulerFactoryBean schedulerFactoryBean) throws SchedulerException {
      Scheduler scheduler = schedulerFactoryBean.getScheduler();
      for (JobExecutionContext jobExecutionContext : scheduler.getCurrentlyExecutingJobs()) {
         final JobDetail jobDetail = jobExecutionContext.getJobDetail();
         logger.info("interrupting job :: jobKey : {}", jobDetail.getKey());
         scheduler.interrupt(jobDetail.getKey());
      }
   }
   
   @Bean(initMethod="init", destroyMethod="destroy")
   public QuartzStarter quartzStarter() {
      return new QuartzStarter();
   }
   
}
