package com.spring.batch.comm.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.spring.batch.comm.quartz.config.QuartzConfig;

public class QuartzJob extends QuartzJobBean {

   private static final Logger logger = LoggerFactory.getLogger(QuartzConfig.class);
   
   @Override
   protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
      try {
         
         logger.info("execute job !");
         
      } catch (Exception e) {
         logger.error("ex in job execute: {}", e);
      }
   }
}
