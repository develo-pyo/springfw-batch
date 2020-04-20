package com.spring.quartz;

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

   /** on completed 
    * async taskExecutor 사용시 배치잡 수행 후 해당 메소드가 호출되는게 아닌
    * 멀티스레드에서 해당 메소드가 바로 호출되므로 배치잡 수행결과(메타데이터가 아닌 사용자정의 테이블에 배치 결과를 적재할 때)를 
    * 해당 메소드에서 처리하면 안된다.
    * 해당 부분을 유의할 것
    * 
    * sync 방식에서의 순서
    * scheduler listener 전처리 --> scheduler job --> batch job --> tasklet before step --> tasklet execute --> tasklet after step --> scheduler listener 후처리
    * async 방식에서의 순서
    * scheduler listener 전처리 --> scheduler job --> batch job --> scheduler listener 후처리
    *                                                           --> tasklet before step --> tasklet execute --> tasklet after step
    * */
   @Override
   public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
      logger.info("jobWasExecuted !!!");
      
   }
   
   
}
