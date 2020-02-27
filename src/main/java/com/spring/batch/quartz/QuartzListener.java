package com.spring.batch.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuartzListener implements JobListener {
   
   private static final Logger logger = LoggerFactory.getLogger(QuartzListener.class);
   
   @Override
   public String getName() {
      return this.getClass().getName();
   }

   /** on started */
   @Override
   public void jobToBeExecuted(JobExecutionContext context) {
      logger.info("jobToBeExecuted !!!");
   }
 
   /** on job failed */
   @Override
   public void jobExecutionVetoed(JobExecutionContext context) {
      logger.info("jobExecutionVetoed !!!");
   }

   /** on completed */
   @Override
   public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
      logger.info("jobWasExecuted !!!");
   }
   
   
}
