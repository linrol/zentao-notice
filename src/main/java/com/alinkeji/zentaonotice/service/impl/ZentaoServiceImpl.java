package com.alinkeji.zentaonotice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
import java.util.Stack;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
  public boolean userLogin() {
    if (zentaosid == null) {
      JSONObject sessionResult = JSON.parseObject(HttpClientUtils.get(getSessionIdUrl, null));
      zentaosid = sessionResult.getJSONObject("data").getString("sessionID");
    }
    JSONObject loginResult = JSON.parseObject(
        HttpClientUtils.get(String.format(loginUrl, user, password, zentaosid), null));
    if (!loginResult.getString("status").equals("success")) {
      logger.error("user：{} password：{}，登录失败：{}", user, password, loginResult.toJSONString());
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
      userLogin();
    }
    url += "?zentaosid=" + zentaosid;
    JSONObject result = JSON.parseObject(HttpClientUtils.get(url, null));
    if (!result.getString("status").equals("success")) {
      return Collections.emptyList();
    }
    List<T> list = Lists.newArrayList();
    JSONObject data = result.getJSONObject("data");
    String resourceField = zentaoResource.getField();
    Object resource = data.get(resourceField);
    if (resource == null) {
      return Collections.emptyList();
    }
    if (resource instanceof JSONObject) {
      JSONObject resourceJson = (JSONObject) resource;
      for (Map.Entry<String, Object> entry : resourceJson.entrySet()) {
        JSONObject jsonObject = (JSONObject) entry.getValue();
        T entity = (T) jsonObject.toJavaObject(zentaoResource.getClazz()).of(jsonObject);
        list.add(entity);
      }
    }
    if (resource instanceof JSONArray) {
      JSONArray resourceJson = (JSONArray) resource;
      resourceJson.forEach(object -> {
        JSONObject jsonObject = (JSONObject) object;
        T entity = (T) jsonObject.toJavaObject(zentaoResource.getClazz()).of(jsonObject);
        list.add(entity);
      });
    }
    Stream<T> stream = filters(list.stream(), zentaoResource.getPredicateStack(), 0);
    return stream.collect(Collectors.toList());
  }

  private <T> Stream<T> filters(Stream<T> stream, Stack<Predicate<T>> stack, int index) {
    if (index >= stack.size()) {
      return stream;
    }
    return filters(stream.filter(stack.peek()), stack, ++index);
  }
}
