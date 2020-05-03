package com.spring.quartz;

import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
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
   
   @Override
   protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
      try {
         logger.info("executeInternal called ! ");
         
         String jobNm = context.getJobDetail().getJobDataMap().getString(JOB_NM);
         logger.info("{} started!", jobNm);
         
         //job 내의 파라미터가 모두 동일한 경우 1회만 실행되고 중복 job 으로 분류되어 실행이 불가.
         //currentTime을 파라미터에 추가하여 이를 방지.
         JobParametersBuilder jpb = new JobParametersBuilder();
         jpb.addLong("currTime", System.currentTimeMillis());
         

         //sync executor, non clustering 환경에서의 중복 실행 방지
         //1. sync 방식일 경우 scheduler.getCurrentlyExecutingJobs() 로 현재 실행중인 schedule 확인이 가능. 이를 이용하여 분기처리 
         //   위 메소드는 내부적으로 %prefix%fired_trigger 메타테이블 조회
         //2. clustering 방식이 아닌 경우 @DisallowConcurrentExecution 사용 하여 중복실행을 막을 수 있음
        
         
         //async executor, clustering 환경에서 스케쥴 중복 실행 방지
         //문제 
         //1. async executor 사용시 schedule fire와 동시에 state가 complete 처리가 되어버린다
         //%prefix%fired_trigger 메타테이블을 통한 조회, getCurrentlyExecutingJobs() 사용이 불가 (조회결과가 없음)
         //2. @DisallowConcurrentExecution 도 사용이 불가 (내부적으로 위와 같이 동작)
         //대안
         //1. schedule 실행상태를 기록하기 위한 별도의 table 생성
         //   CREATE SCHEDULE_STATE (SEQ, SERVER_NM, SCHEDULE_NO, STATE) ... 생략
         //2. schedule 실행 직전 레코드 삽입 
         //   scheduler listener jobToBeExecuted()에서 실행상태 EXECUTING으로 INSERT
         //3. batch job 실행 완료 후 실행상태 update
         //   batch job StepExecutionListener @after 에서 실행상태 COMPLETE로 UPDATE 
         //4. schedule job 실행시 위 테이블에서 schedule 실행상태를 확인 후 실행 혹은 건너뛰기 
         //   건너뛸 경우 SKIP으로 UPDATE
         //* 서버가 중간에 내려가는 경우 현재 서버에서 실행중인 schedule 들의 상태를 중단 상태로 수정 
         //   bean @PreDestory 내에서 update 쿼리 수행
         //   UPDATE SCHEDULE_STATE SET STATE = 'ABANDONED' WHERE SERVER_NM = ? AND STATE = 'EXECUTING' 
         
         //async executor, clustering + JNDI 환경에서 스케쥴 중복 실행 방지 처리방법
         //문제
         //bean Destroy 시점보다 JNDI Destroy 시점이 더 빠름
         //JNDI 는 WAS 쪽 설정이므로 APP단에서 @Order, @DependsOn 등을 사용한 주입순서 변경 불가 (다른 방법이 존재할 수 있겠으나 리서칭 실패)
         //대안
         //1. 서버가 런칭될 때 현재 서버 내의 EXECUTING 상태의 schedule 들을 중단 상태로 수정
         //   bean @PostConstruct 내에서 중단상태로 수정
         //   UPDATE SCHEDULE_STATE SET STATE = 'ABANDONED' WHERE SERVER_NM = ? AND STATE = 'EXECUTING'
         //2. schedule job 실행시 실행중인 스케쥴 조회 + 서버 상태 조회 로 스케쥴 중복 체크
         //   SELECT SERVER_NM FROM SCHEDULE_STATE WHERE SCHEDULE_NO = ? AND SERVER_NM = ?  ORDER BY START_TIME desc LIMIT 1,1
         //     ㄴ위 쿼리에서 2번째 데이터를 뽑아오는 이유는 시작시각 기준 내림차순 정렬시 첫번째 데이터는 현재 실행하려는 스케쥴NO의 레코드, 
         //     ㄴ두번째가 가장 마지막에 실행한 현재 스케쥴NO의 레코드이므로..
         //   SELECT 
         //          IFNULL((SELECT 1
         //                    FROM %prefix%_scheduler_state ss
         //                   WHERE 1=1
         //                     AND INSTANCE_NAME = #{serverNm}
         //                     AND last_checkin_time >= #{currTime}-ss.CHECKIN_INTERVAL
         //                 ), 0) AS result
         //     ㄴ위 쿼리로 scheduler_state 메타테이블에서 server 상태 조회하여 해당 서버가 alive 상태인지 판별이 가능
         
         jobLauncher.run((Job)BeanUtils.getBean(jobNm), jpb.toJobParameters());  //batch job 실행
         
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
