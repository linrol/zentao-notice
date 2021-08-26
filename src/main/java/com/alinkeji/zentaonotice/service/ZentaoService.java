package com.alinkeji.zentaonotice.service;

import com.alinkeji.zentaonotice.entity.BaseEntity;
import com.alinkeji.zentaonotice.entity.Bug;
import com.alinkeji.zentaonotice.entity.Task;
import com.alinkeji.zentaonotice.enums.ZentaoResource;
import java.util.List;

public interface ZentaoService {

  /**
   * 用户登录
   *
   * @param account
   * @param password
   * @return
   */
  public boolean userLogin(String account, String password);

  /**
   * 获取bug列表
   *
   * @param url
   * @return
   */
  public List<Bug> getBugList(String url);

  /**
   * 获取task列表
   *
   * @param url
   * @return
   */
  public List<Task> getTaskList(String url);

  /**
   * 获取禅道资源集合
   *
   * @param url
   * @param zentaoResource
   * @param <T>
   * @return
   */
  public <T extends BaseEntity> List<T> getResourceList(String url, ZentaoResource zentaoResource);

  public interface ListResourceHandler {

    /**
     * 过滤
     *
     * @return
     */
    public <T extends BaseEntity> List<T> filter(List<T> list);
  }


}
