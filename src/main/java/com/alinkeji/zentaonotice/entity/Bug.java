package com.alinkeji.zentaonotice.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alinkeji.zentaonotice.util.DateUtils;
import java.util.Date;
import java.util.function.Predicate;

/**
 * @Description
 * @ClassName Bug
 * @Author linrol
 * @date 2021年08月26日 11:30 Copyright (c) 2020, linrol@77hub.com All Rights Reserved.
 */
public class Bug extends BaseEntity {

  private static final String bugView = "http://zentao.77hub.com/zentao/bug-view-%s.html";

  @JSONField(name = "openedDate")
  private Date createdTime;

  public Date getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(Date createdTime) {
    this.createdTime = createdTime;
  }

  @JSONField(name = "title")
  @Override
  public void setTitle(String title) {
    super.setTitle(title);
  }

  @Override
  public String getViewUrl() {
    return String.format(bugView, this.getId());
  }

  /**
   * 过滤今天创建的bug
   *
   * @return
   */
  public static Predicate<Bug> filterTodayCreated() {
    return bug -> bug.getCreatedTime().after(DateUtils.getOfDayFirst(new Date()));
  }

}
