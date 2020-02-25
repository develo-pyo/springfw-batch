package com.spring.batch.comm.quartz;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import com.spring.batch.comm.quartz.config.QuartzConfig;

@Service("quartzService")
public class QuartzService {
   private static final Logger logger = LoggerFactory.getLogger(QuartzService.class);
   
   @Autowired
   private SchedulerFactoryBean schedulerFactoryBean;
   
   public void register() throws Exception {
      Scheduler scheduler = schedulerFactoryBean.getScheduler();
      JobDetail jobDetail = this.createJobDetail();
      CronTrigger cronTrigger = this.createCronTrigger();
      scheduler.scheduleJob(jobDetail, cronTrigger);
   }
   
   private JobDetail createJobDetail() {
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
      Scheduler scheduler = schedulerFactoryBean.getScheduler();
      if(scheduler != null && !scheduler.isStarted()) {
         scheduler.start();
      }
   }
   
   public void shutdown() throws SchedulerException, InterruptedException {
      Scheduler scheduler = schedulerFactoryBean.getScheduler();
      if(scheduler != null && !scheduler.isShutdown()) {
         scheduler.shutdown();
      }
   }
   
   public void removeJob() throws SchedulerException {
      Scheduler scheduler = schedulerFactoryBean.getScheduler();
      scheduler.deleteJob(new JobKey("sampleJob"));
   }
   
}