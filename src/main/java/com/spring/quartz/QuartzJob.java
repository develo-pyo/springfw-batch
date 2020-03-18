package com.spring.quartz;

import java.util.Iterator;
import java.util.Set;

import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.spring.quartz.config.QuartzConfig;
import com.spring.quartz.utils.BeanUtils;

//clustering 모드에선 아래 어노테이션이 동작하지 않음
//@DisallowConcurrentExecution
public class QuartzJob extends QuartzJobBean implements InterruptableJob {

   private static final Logger logger = LoggerFactory.getLogger(QuartzConfig.class);
   private static final String JOB_NM = "jobNm";
   
   private volatile boolean isJobInterrupted = false; 
   private volatile Thread currThread;
   
   @Autowired
   private JobLauncher jobLauncher;
   
   @Autowired
   private JobExplorer jobExplorer;
   
   @Override
   protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
      try {
         logger.info("executeInternal called ! ");
         
         String jobNm = context.getJobDetail().getJobDataMap().getString(JOB_NM);
         logger.info("{} started!", jobNm);
         
         JobParametersBuilder jpb = new JobParametersBuilder();
         jpb.addLong("currTime", System.currentTimeMillis());
         jobLauncher.run((Job)BeanUtils.getBean(jobNm), jpb.toJobParameters());
      } catch (Exception e) {
         logger.error("ex in job execute: {}", e.getMessage());
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
