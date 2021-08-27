package com.alinkeji.zentaonotice.enums;

import com.alinkeji.zentaonotice.entity.BaseEntity;
import com.alinkeji.zentaonotice.entity.Bug;
import com.alinkeji.zentaonotice.entity.Task;
import java.util.Arrays;
import java.util.Stack;
import java.util.function.Predicate;

public enum ZentaoResource {

  BUG(Bug.class, "bugs", "http://zentao.77hub.com/zentao/bug-browse-7-0-bySearch-%s.json"),
  TASK(Task.class, "tasks", "http://zentao.77hub.com/zentao/project-task-445-bySearch-%s.json");

  private Class<? extends BaseEntity> clazz;
  private String field;
  private String url;
  private Stack<Predicate<? extends BaseEntity>> predicateStack;

  <T extends BaseEntity> ZentaoResource(Class<T> clazz, String field, String url) {
    this.clazz = clazz;
    this.field = field;
    this.url = url;
    this.predicateStack = new Stack<>();
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

  public <T extends BaseEntity> ZentaoResource ofPredicates(Predicate<T>... predicates) {
    predicateStack.addAll(Arrays.asList(predicates));
    return this;
  }

  public <T extends BaseEntity> Stack<Predicate<T>> getPredicateStack() {
    Stack<Predicate<T>> stack = new Stack<>();
    for (Predicate<? extends BaseEntity> predicate : predicateStack) {
      stack.add((Predicate<T>) predicate);
    }
    return stack;
  }

}
