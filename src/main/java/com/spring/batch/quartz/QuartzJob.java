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
         System.out.println("-----------------------------------------");
         
         if(!isJobInterrupted) {
            currThread = Thread.currentThread();
            
            System.out.println("not interrupted !");
            System.out.println("so, execute job !!");
            
            int cnt = 20;
            while(cnt > 0){
               System.out.println("execute job ! inner loop");
               System.out.println("jk nm : " + jk.getName());
               System.out.println("cnt : " + cnt);
               
               File f = new File("/jb_log/schdulerTest_"+cnt);
               f.createNewFile();
               
               Thread.sleep(800L);
               cnt--;
            }
         }
         
         System.out.println("-----------------------------------------");
      } catch (Exception e) {
         System.out.println("ex in job execute: {"+e.getMessage()+"}");
      }
   }

   @Override
   public void interrupt() throws UnableToInterruptJobException {
      isJobInterrupted = true;
      if(currThread != null) {
         System.out.println("interrupting-{"+currThread.getName()+"}");
         currThread.interrupt();
      }
   }
   
}
