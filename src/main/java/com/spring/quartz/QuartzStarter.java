package com.spring.quartz;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QuartzStarter {
   
   @Autowired
   private QuartzService quartzService;
   
   @PostConstruct
   public void init() throws Exception {
      System.out.println("init called !!!!");
      quartzService.clear();
      quartzService.addListener(new QuartzListener());
      quartzService.register();
      quartzService.start();
      
      System.out.println("start call ! ");
   }
   
   public void destroy() throws Exception {
      quartzService.shutdown();
      System.out.println("shutdown call ! ");
   }
   
}
