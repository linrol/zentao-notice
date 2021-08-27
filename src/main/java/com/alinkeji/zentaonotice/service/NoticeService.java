package com.alinkeji.zentaonotice.service;

import com.alinkeji.zentaonotice.entity.BaseEntity;
import com.alinkeji.zentaonotice.enums.ZentaoResource;

public interface NoticeService {


  /**
   * 通知到企业微信
   *
   * @param zentaoResource
   * @param <T>
   */
  public <T extends BaseEntity> boolean notice2WxWork(ZentaoResource zentaoResource);

}
