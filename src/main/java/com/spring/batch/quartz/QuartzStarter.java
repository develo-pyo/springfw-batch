package com.spring.batch.quartz;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QuartzStarter {
   
   @Autowired
   private QuartzService quartzService;
   
   public void setQuartzService(QuartzService quartzService) {
      this.quartzService = quartzService;
   }

   @PostConstruct
   public void init() throws Exception {
      quartzService.removeJob();
      quartzService.register();
      quartzService.start();
      System.out.println("start call ! ");
   }
   
   
   public void destroy() throws Exception {
      quartzService.shutdown();
      System.out.println("shutdown call ! ");
   }
   
}
