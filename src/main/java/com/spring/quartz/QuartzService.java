package com.spring.quartz;

import javax.annotation.PostConstruct;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

@Service("quartzService")
public class QuartzService {
   
   private static final Logger logger = LoggerFactory.getLogger(QuartzService.class);
   
   private static final String JOB_NM = "jobNm";
   
   @Autowired
   private SchedulerFactoryBean schedulerFactoryBean;

//   @Autowired
//   private QuartzDAO quartzDAO;
   
   private Scheduler scheduler = null;
   
   @PostConstruct
   public void init(){
      scheduler = schedulerFactoryBean.getScheduler();
      
      //앱이 실행된 서버(스케쥴러) 내의 STARTED 상태 JOB들을 제거
//      quartzDAO.removeStartedJobs();
   }
   
   
   /** 스케쥴러에 스케쥴 등록 */
   public void register() throws Exception {
      //실제 서비스에선 job이름과 cron을 DB에서 관리하도록 처리
      //ADM 화면에서 관리될 수 있도록 설계 및 구현
      String jobNm = "sampleJob";
//      String cron = "0/5 * * * * ?"; //매 5초
      String cron = "0/30 * * * * ?";  //매 30초
      
      JobDetail jobDetail = this.createJobDetail(jobNm);
      CronTrigger cronTrigger = this.createCronTrigger(jobNm, cron);
      scheduler.scheduleJob(jobDetail, cronTrigger);
   }
   
   /** JobDetail 생성 */
   private JobDetail createJobDetail(String jobNm) {
      
      JobDetail jobDetail = JobBuilder.newJob(QuartzJob.class)
            .withIdentity(jobNm)
            .build();
      jobDetail.getJobDataMap().put(JOB_NM, jobNm);
      
      return jobDetail;
   }
   
   /** CronTrigger 생성 */
   private CronTrigger createCronTrigger(String jobNm, String cron) {
      return TriggerBuilder.newTrigger()
            .withIdentity(new JobKey(jobNm).getName())
            .withSchedule(CronScheduleBuilder.cronSchedule(cron))
            .build();
   }
   
   /** 스케쥴러 시작 */
   public void start() throws SchedulerException {
      if(scheduler != null && !scheduler.isStarted()) {
         scheduler.start();
      }
   }
   
   /** 스케쥴러 종료 */
   public void shutdown() throws SchedulerException, InterruptedException {
      if(scheduler != null && !scheduler.isShutdown()) {
         scheduler.shutdown();
      }
   }
   
   /** 스케쥴러 클리어 */
   public void clear() throws SchedulerException {
      scheduler.clear();
   }
   
   /** 스케쥴러 리스너 등록 */
   public void addListener(JobListener jobListener) throws SchedulerException {
      scheduler.getListenerManager().addJobListener(jobListener);
   }
   
   
}