package com.alinkeji.zentaonotice.entity;

import com.alibaba.fastjson.annotation.JSONField;
import java.util.Date;
import java.util.function.Predicate;

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

  /**
   * 过滤已延期的任务，当前时间大于截止日期
   *
   * @return
   */
  public static Predicate<Task> filterDelay(boolean includeCancelled, boolean includeClosed) {
    if (!includeCancelled && !includeClosed) {
      // 不包含已取消和已关闭
      return task -> new Date().after(task.getDeadLineTime()) &&
          !task.isCancel() && !task.isClosed();
    } else if (includeCancelled && includeClosed) {
      // 包含已取消和已关闭
      return task -> new Date().after(task.getDeadLineTime());
    } else if (!includeCancelled) {
      // 不包含已取消
      return task -> new Date().after(task.getDeadLineTime()) && !task.isCancel();
    }
    // 不包含已关闭
    return task -> new Date().after(task.getDeadLineTime()) && !task.isClosed();
  }
}
