package com.spring.batch.quartz;

import javax.annotation.PostConstruct;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

@Service("quartzService")
public class QuartzService {
   private static final Logger logger = LoggerFactory.getLogger(QuartzService.class);
   
   @Autowired
   private SchedulerFactoryBean schedulerFactoryBean;
   
   private Scheduler scheduler = null;
   
   @PostConstruct
   public void init(){
      scheduler = schedulerFactoryBean.getScheduler();
   }
   
   public void register() throws Exception {
      JobDetail jobDetail = this.createJobDetail();
      CronTrigger cronTrigger = this.createCronTrigger();
      scheduler.scheduleJob(jobDetail, cronTrigger);
   }
   
   private JobDetail createJobDetail() {
      logger.info("!!!!!! called createJobdetail");
      JobDetail jobDetail = JobBuilder.newJob(QuartzJob.class)
            .withIdentity("sampleJob")
            .build();
      
//      jobDetail.getJobDataMap().put("job" , vo.getJob());
//      jobDetail.getJobDataMap().put("params" , vo.getParams());
      
      return jobDetail;
   }
   
   private CronTrigger createCronTrigger() {
      return TriggerBuilder.newTrigger()
            .withIdentity(new JobKey("sampleJob").getName())
            .withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ?"))
            .build();
   }
   
   public void start() throws SchedulerException {
      if(scheduler != null && !scheduler.isStarted()) {
         scheduler.start();
      }
   }
   
   public void shutdown() throws SchedulerException, InterruptedException {
      if(scheduler != null && !scheduler.isShutdown()) {
         scheduler.shutdown();
      }
   }
   
   public void deleteJob() throws SchedulerException {
      scheduler.deleteJob(new JobKey("sampleJob"));
   }
   
   public void addListener(JobListener jobListener) throws SchedulerException {
      scheduler.getListenerManager().addJobListener(jobListener);
   }
}