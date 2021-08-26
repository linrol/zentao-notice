package com.alinkeji.zentaonotice.entity;

import com.alibaba.fastjson.annotation.JSONField;
import java.util.Date;
import java.util.List;
import org.yaml.snakeyaml.events.Event.ID;

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

}
