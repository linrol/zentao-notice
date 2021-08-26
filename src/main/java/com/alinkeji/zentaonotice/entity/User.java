package com.alinkeji.zentaonotice.entity;

import com.alinkeji.zentaonotice.enums.ZentaoResource;

/**
 * @Description
 * @ClassName User
 * @Author linrol
 * @date 2021年08月26日 13:01 Copyright (c) 2020, linrol@77hub.com All Rights Reserved.
 */
public class User {

  private String name;

  private String phone;

  private String bugUrlId;

  private String taskUrlId;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getBugUrlId() {
    return bugUrlId;
  }

  public void setBugUrlId(String bugUrlId) {
    this.bugUrlId = bugUrlId;
  }

  public String getTaskUrlId() {
    return taskUrlId;
  }

  public void setTaskUrlId(String taskUrlId) {
    this.taskUrlId = taskUrlId;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public static User of(String name, String phone, String bugUrlId, String taskUrlId) {
    User user = new User();
    user.setName(name);
    user.setPhone(phone);
    user.setBugUrlId(bugUrlId);
    user.setTaskUrlId(taskUrlId);
    return user;
  }

  public String getResourceUrlId(ZentaoResource zentaoResource) {
    if (ZentaoResource.BUG.equals(zentaoResource)) {
      return getBugUrlId();
    }
    if (ZentaoResource.TASK.equals(zentaoResource)) {
      return getTaskUrlId();
    }
    throw new UnsupportedOperationException("不支持的资源");
  }
}
