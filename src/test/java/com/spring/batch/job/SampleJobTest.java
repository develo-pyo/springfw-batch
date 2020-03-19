package com.spring.batch.job;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import config.ExtSpringJUnit4ClassRunner;

//@RunWith(SpringJUnit4ClassRunner.class)
@RunWith(ExtSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { 
                                      "classpath:batch/job/SampleJob.xml"
                                    , "classpath:test-*.xml"
                                    , "classpath:context/spring/context-mybatis.xml"
                                    , "classpath:context/spring/context-datasource.xml"
                                   })

//* 테스트 주입설정 주의사항
//  org.springframework.core.task.SimpleAsyncTaskExecutor 사용시 async로 배치가 동작하므로 테스트시 Assert 를 사용한 결과값 확인 불가
//  junit test 는 test xml 을 따로 두어 asyncTaskExecutor 제거
public class SampleJobTest {

   private static final Logger LOGGER = LoggerFactory.getLogger(SampleJobTest.class);
   
   @Autowired
   private JobLauncherTestUtils jobLauncherTestUtils;
   
   @Test
   public void test() {
      JobExecution jobExecution = null;
      
      try {
         jobExecution = jobLauncherTestUtils.launchJob();
      } catch(Exception e) {
         e.printStackTrace();
      }
      
      Assert.assertEquals(ExitStatus.COMPLETED.getExitCode(), jobExecution.getExitStatus().getExitCode());
   }

}
