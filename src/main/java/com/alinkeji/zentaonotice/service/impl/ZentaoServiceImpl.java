package com.alinkeji.zentaonotice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alinkeji.zentaonotice.entity.BaseEntity;
import com.alinkeji.zentaonotice.entity.Bug;
import com.alinkeji.zentaonotice.entity.Task;
import com.alinkeji.zentaonotice.enums.ZentaoResource;
import com.alinkeji.zentaonotice.service.ZentaoService;
import com.alinkeji.zentaonotice.util.HttpClientUtils;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @ClassName ZentaoServiceImpl
 * @Author linrol
 * @date 2021年08月26日 00:29 Copyright (c) 2020, linrol@77hub.com All Rights Reserved.
 */
@Component
public class ZentaoServiceImpl implements ZentaoService {

  private Logger logger = LoggerFactory.getLogger(ZentaoServiceImpl.class);

  private String zentaosid;

  private static final String getSessionIdUrl = "http://zentao.77hub.com/zentao/api-getsessionid.json";

  private static final String loginUrl = "http://zentao.77hub.com/zentao/user-login.json?account=%s&password=%s&zentaosid=%s";

  @Value("${zentao.user}")
  private String user;

  @Value("${zentao.password}")
  private String password;

  @Override
  @PostConstruct
  public boolean userLogin(String account, String password) {
    if (zentaosid == null) {
      JSONObject sessionResult = JSON.parseObject(HttpClientUtils.get(getSessionIdUrl, null));
      zentaosid = sessionResult.getJSONObject("data").getString("sessionID");
    }
    JSONObject loginResult = JSON.parseObject(
        HttpClientUtils.get(String.format(loginUrl, account, password, zentaosid), null));
    if (!loginResult.getString("status").equals("success")) {
      logger.error("登录失败：{}", loginResult.toJSONString());
      return false;
    }
    return true;
  }

  @Override
  public List<Bug> getBugList(String url) {
    return getResourceList(url, ZentaoResource.BUG);
  }

  @Override
  public List<Task> getTaskList(String url) {
    return getResourceList(url, ZentaoResource.TASK);
  }

  public <T extends BaseEntity> List<T> getResourceList(String url, ZentaoResource zentaoResource) {
    if (zentaosid == null) {
      userLogin(user, password);
    }
    url += "?zentaosid=" + zentaosid;
    JSONObject result = JSON.parseObject(HttpClientUtils.get(url, null));
    if (!result.getString("status").equals("success")) {
      return Collections.emptyList();
    }
    List<T> list = Lists.newArrayList();
    JSONObject data = result.getJSONObject("data");
    if (zentaoResource.equals(ZentaoResource.BUG)) {
      data.getJSONArray(zentaoResource.getField()).forEach(object -> {
        JSONObject jsonObject = (JSONObject) object;
        T entity = (T) jsonObject.toJavaObject(zentaoResource.getClazz()).of(jsonObject);
        list.add(entity);
      });
    } else {
      Object fieldJson = data.get(zentaoResource.getField());
      if (fieldJson instanceof JSONObject) {
        for (Map.Entry<String, Object> entry : ((JSONObject) fieldJson).entrySet()) {
          JSONObject jsonObject = (JSONObject) entry.getValue();
          T entity = (T) jsonObject.toJavaObject(zentaoResource.getClazz()).of(jsonObject);
          list.add(entity);
        }
      }
    }
    return list;
  }
}
