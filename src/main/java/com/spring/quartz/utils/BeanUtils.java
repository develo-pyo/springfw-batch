package com.spring.quartz.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

// bean에 ApplicationContext 정보 주입
@Component
public class BeanUtils implements ApplicationContextAware {
   private static ApplicationContext context;

   @Override
   public void setApplicationContext(ApplicationContext applicationContext) {
      // TODO Auto-generated method stub
      context = applicationContext;
   }
   
   public static <T> T getBean(Class<T> beanClass) {
      return context.getBean(beanClass);
   }
   
   public static Object getBean(String beanName) {
      return context.getBean(beanName);
   }
}
