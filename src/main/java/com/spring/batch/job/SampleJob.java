package com.spring.batch.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class SampleJob implements Tasklet, StepExecutionListener{
   
   private static final Logger logger = LoggerFactory.getLogger(SampleJob.class);

   @Override
   public void beforeStep(StepExecution stepExecution) {
      logger.info("beforeStep in sampleJob");
   }

   @Override
   public ExitStatus afterStep(StepExecution stepExecution) {
      logger.info("afterStep in sampleJob");
      return null;
   }
      
   @Override
   public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
      logger.info("execute in sampleJob");
      int cnt = 40;  //40초 동안 실행
      while(cnt > 0){
         logger.info("job is running... will be finished in " + cnt + " sec");
         Thread.sleep(1000L);
         cnt--;
      }
      return RepeatStatus.FINISHED;
   }
}
