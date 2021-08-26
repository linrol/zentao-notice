package com.alinkeji.zentaonotice.service;

import com.alinkeji.zentaonotice.entity.BaseEntity;
import com.alinkeji.zentaonotice.enums.ZentaoResource;
import com.alinkeji.zentaonotice.service.ZentaoService.ListResourceHandler;

public interface NoticeService {


  /**
   * 通知到企业微信
   *
   * @param zentaoResource
   * @param listResourceFilter
   * @param <T>
   */
  public <T extends BaseEntity> boolean notice2WxWork(ZentaoResource zentaoResource,
      ListResourceHandler listResourceFilter);

}
