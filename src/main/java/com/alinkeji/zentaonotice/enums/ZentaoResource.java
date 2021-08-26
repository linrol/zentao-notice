package com.alinkeji.zentaonotice.enums;

import com.alinkeji.zentaonotice.entity.BaseEntity;
import com.alinkeji.zentaonotice.entity.Bug;
import com.alinkeji.zentaonotice.entity.Task;

public enum ZentaoResource {

  BUG(Bug.class, "bugs", "http://zentao.77hub.com/zentao/bug-browse-7-0-bySearch-%s.json"),
  TASK(Task.class, "tasks", "http://zentao.77hub.com/zentao/project-task-445-bySearch-%s.json");

  private Class<? extends BaseEntity> clazz;
  private String field;
  private String url;

  <T extends BaseEntity> ZentaoResource(Class<T> clazz, String field, String url) {
    this.clazz = clazz;
    this.field = field;
    this.url = url;
  }

  public <T extends BaseEntity> Class<T> getClazz() {
    return (Class<T>) clazz;
  }

  public String getField() {
    return field;
  }

  public String getUrl() {
    return url;
  }

}
