package com.spring.quartz;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//시작부분
@Component
public class QuartzStarter {
   
   @Autowired
   private QuartzService quartzService;
   
   //시작부분
   @PostConstruct
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
