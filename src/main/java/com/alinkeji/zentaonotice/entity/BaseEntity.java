package com.alinkeji.zentaonotice.entity;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * @Description
 * @ClassName ZentaoBase
 * @Author linrol
 * @date 2021年08月26日 12:47 Copyright (c) 2020, linrol@77hub.com All Rights Reserved.
 */
public class BaseEntity {

  @JSONField(name = "id")
  private String id;

  private String title;

  private JSONObject rawContext;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title.replace(" ", "");
  }

  public JSONObject getRawContext() {
    return rawContext;
  }

  public void setRawContext(JSONObject rawContext) {
    this.rawContext = rawContext;
  }

  public BaseEntity of(Object object) {
    this.setRawContext((JSONObject) object);
    return this;
  }

  public String getTitle(int maxLength) {
    if (title.length() > maxLength) {
      return title.substring(0, maxLength) + "...";
    }
    return title;
  }

  public String getMarkDown(String userName) {
    String className = this.getClass().getSimpleName();
    return userName + "\t\t" + className + "#" + getId() + "\t\t[" + getTitle(10) + "]("
        + getViewUrl() + ")\n";
  }

  protected String getViewUrl() {
    return null;
  }
}
