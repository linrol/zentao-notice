package com.alinkeji.zentaonotice.util;

import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @ClassName ShutdownContext
 * @Author linrol
 * @date 2021年08月26日 23:36 Copyright (c) 2020, linrol@77hub.com All Rights Reserved.
 */
@Component
@ConfigurationProperties(prefix = "spring")
public class ShutdownContext implements ApplicationContextAware {

  private ConfigurableApplicationContext context;

  public void showdown() {
    if (null != context) {
      context.close();
    }
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    if (applicationContext instanceof ConfigurableApplicationContext) {
      this.context = (ConfigurableApplicationContext) applicationContext;
    }

  }
}
