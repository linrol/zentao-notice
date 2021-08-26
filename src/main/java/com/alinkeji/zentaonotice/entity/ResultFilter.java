package com.alinkeji.zentaonotice.entity;

import java.util.List;

public interface ResultFilter {

  /**
   * 过滤
   *
   * @param <T>
   * @return
   */
  public <T extends BaseEntity> List<T> filter();
}
