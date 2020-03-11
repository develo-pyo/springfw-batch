package com.spring.quartz;

import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.spring.batch.job.SampleJob;
import com.spring.quartz.config.QuartzConfig;
import com.spring.quartz.utils.BeanUtils;

//@DisallowConcurrentExecution
public class QuartzJob extends QuartzJobBean implements InterruptableJob {

   private static final Logger logger = LoggerFactory.getLogger(QuartzConfig.class);
   
   private volatile boolean isJobInterrupted = false; 
   private volatile Thread currThread;
   
   @Autowired
   private JobLauncher jobLauncher;
   
   @Override
   protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
      try {
         logger.info("executeInternal called ! ");
         
         JobParametersBuilder jpb = new JobParametersBuilder();
         jpb.addLong("currTime", System.currentTimeMillis());
         jobLauncher.run( (Job)BeanUtils.getBean("sampleJob"), jpb.toJobParameters());
         
      } catch (Exception e) {
         logger.error("ex in job execute: {"+e.getMessage()+"}");
      }
   }
   
//   @Override
//   protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
//      try {
//         JobKey jk = context.getJobDetail().getKey();
//         logger.info("-----------------------------------------");
//
//         if(!isJobInterrupted) {
//            currThread = Thread.currentThread();
//            
//            int cnt = 20;
//            while(cnt > 0){
//               logger.info("job is running... will be finished in " + cnt + " sec");
//               Thread.sleep(1000L);
//               cnt--;
//            }
//         }
//         
//         logger.info("-----------------------------------------");
//      } catch (Exception e) {
//         logger.error("ex in job execute: {"+e.getMessage()+"}");
//      }
//   }

   @Override
   public void interrupt() throws UnableToInterruptJobException {
      isJobInterrupted = true;
      if(currThread != null) {
         logger.info("interrupting-{"+currThread.getName()+"}");
         currThread.interrupt();
      }
   }
   
}
