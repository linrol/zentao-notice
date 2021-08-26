package com.alinkeji.zentaonotice.entity;

import com.alibaba.fastjson.annotation.JSONField;
import java.util.Date;

/**
 * @Description
 * @ClassName Task
 * @Author linrol
 * @date 2021年08月26日 11:31 Copyright (c) 2020, linrol@77hub.com All Rights Reserved.
 */
public class Task extends BaseEntity {

  private static final String taskView = "http://zentao.77hub.com/zentao/task-view-%s.html";

  /**
   * 截止日期
   */
  @JSONField(name = "deadline")
  private Date deadLineTime;

  public Date getDeadLineTime() {
    return deadLineTime;
  }

  public void setDeadLineTime(Date deadLineTime) {
    this.deadLineTime = deadLineTime;
  }

  @JSONField(name = "name")
  @Override
  public void setTitle(String title) {
    super.setTitle(title);
  }

  @Override
  public String getViewUrl() {
    return String.format(taskView, this.getId());
  }
}
