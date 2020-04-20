package com.spring.quartz;

import org.springframework.beans.factory.annotation.Autowired;

public class QuartzStarter {
   
   @Autowired
   private QuartzService quartzService;
   
   //시작부분
   public void init() throws Exception {
      quartzService.clear();
      quartzService.addListener(new QuartzListener());
      quartzService.register();
      quartzService.start();
   }
   
   public void destroy() throws Exception {
      quartzService.shutdown();
   }
   
}
