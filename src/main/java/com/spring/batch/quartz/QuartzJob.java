package com.spring.batch.quartz;

import java.io.File;

import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.spring.batch.quartz.config.QuartzConfig;

public class QuartzJob extends QuartzJobBean implements InterruptableJob {

   private static final Logger logger = LoggerFactory.getLogger(QuartzConfig.class);
   
   private volatile boolean isJobInterrupted = false; 
   private volatile Thread currThread;
   
   @Override
   protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
      try {
         
         JobKey jk = context.getJobDetail().getKey();
         logger.info("-----------------------------------------");
         
         if(!isJobInterrupted) {
            currThread = Thread.currentThread();
            
            int cnt = 20;
            while(cnt > 0){
               logger.info("cnt : " + cnt);
               
//               File f = new File("/jb_log/schdulerTest_"+cnt);
//               f.createNewFile();
               
               Thread.sleep(800L);
               cnt--;
            }
         }
         
         logger.info("-----------------------------------------");
      } catch (Exception e) {
         logger.error("ex in job execute: {"+e.getMessage()+"}");
      }
   }

   @Override
   public void interrupt() throws UnableToInterruptJobException {
      isJobInterrupted = true;
      if(currThread != null) {
         logger.info("interrupting-{"+currThread.getName()+"}");
         currThread.interrupt();
      }
   }
   
}
