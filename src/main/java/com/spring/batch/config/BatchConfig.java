package com.spring.batch.config;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SimpleThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

/** https://docs.spring.io/spring-batch/docs/current/reference/html/job.html */
@Configuration
public class BatchConfig {
   
   private static final String TABLE_PREFIX = "BATCH2_";
   private static final int THREAD_COUNT = 10;
   
   @Autowired
   private DataSource dataSource;
   @Autowired
   @Qualifier("txManager")
   private PlatformTransactionManager txManager; 
   
   /** job 메타데이터에 대한 CRUD 제공 */
   @Bean
   public JobRepository jobRepository() throws Exception {
      CustomJobRepositoryFactoryBean jfb = new CustomJobRepositoryFactoryBean();
      jfb.setTablePrefix(TABLE_PREFIX);
      jfb.setDataSource(dataSource);
      jfb.setTransactionManager(txManager);
      jfb.afterPropertiesSet();
      return (JobRepository) jfb.getObject();
   }
   
   /** job 실행시키는 런쳐 */
   @Bean
   public JobLauncher jobLauncher() throws Exception {
      SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
      jobLauncher.setJobRepository(jobRepository());
      jobLauncher.setTaskExecutor(simpleThreadPoolTaskExecutor());
      jobLauncher.afterPropertiesSet();
      return jobLauncher;
   }
   
   /** 비동기처리 및 job을 thread pool 로 동작시키기 위해 
    * (quartz thread count 와 일치시켜야 함) */
   @Bean
   public SimpleThreadPoolTaskExecutor simpleThreadPoolTaskExecutor() throws Exception {
      SimpleThreadPoolTaskExecutor stpte = new SimpleThreadPoolTaskExecutor();
      stpte.setThreadCount(THREAD_COUNT);
      return stpte;
   }
   
   /** 현재 실행중인 job 정보 및 job 제어 */
   @Bean
   public JobExplorer jobExplorer() throws Exception {
      JobExplorerFactoryBean jfb = new JobExplorerFactoryBean();
      jfb.setDataSource(dataSource);
      jfb.setTablePrefix(TABLE_PREFIX);
      jfb.afterPropertiesSet();
      return (JobExplorer) jfb.getObject();
   }
   
   /** 현재 실행중인 job 정보 및 job 제어 */
   @Bean
   public JobOperator jobOperator() throws Exception {
      SimpleJobOperator sjo = new SimpleJobOperator();
      sjo.setJobLauncher(jobLauncher());
      sjo.setJobRepository(jobRepository());
      sjo.setJobRegistry(jobRegistry());
      sjo.setJobExplorer(jobExplorer());
      return sjo;
   }
   
   @Bean
   public JobRegistryBeanPostProcessor JobRegistryBeanPostProcessor() throws Exception {
      JobRegistryBeanPostProcessor postProcessor = new JobRegistryBeanPostProcessor();
      postProcessor.setJobRegistry(jobRegistry());
      return postProcessor;
   }
   
   @Bean
   public JobRegistry jobRegistry() throws Exception {
      MapJobRegistry mjr = new MapJobRegistry();
      return mjr;
   }
   
}
