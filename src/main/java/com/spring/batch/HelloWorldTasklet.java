package com.spring.batch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class HelloWorldTasklet implements Tasklet{

   @Override
   public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
      
      System.out.println("start ");
      
      
      System.out.println("finish ");
      
      return null;
   }
   
   
}
