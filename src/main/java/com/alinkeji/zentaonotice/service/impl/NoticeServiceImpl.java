package com.alinkeji.zentaonotice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alinkeji.zentaonotice.ZentaoNoticeApplication;
import com.alinkeji.zentaonotice.entity.BaseEntity;
import com.alinkeji.zentaonotice.entity.Bug;
import com.alinkeji.zentaonotice.entity.Task;
import com.alinkeji.zentaonotice.entity.User;
import com.alinkeji.zentaonotice.entity.WxWorkMessage;
import com.alinkeji.zentaonotice.entity.WxWorkMessage.Markdown;
import com.alinkeji.zentaonotice.enums.ZentaoResource;
import com.alinkeji.zentaonotice.service.NoticeService;
import com.alinkeji.zentaonotice.service.ZentaoService;
import com.alinkeji.zentaonotice.service.ZentaoService.ListResourceHandler;
import com.alinkeji.zentaonotice.util.DateUtils;
import com.alinkeji.zentaonotice.util.HttpClientUtils;
import com.alinkeji.zentaonotice.util.RedisUtil;
import com.alinkeji.zentaonotice.util.ShutdownContext;
import com.alinkeji.zentaonotice.util.SpringContextUtils;
import com.google.common.collect.Lists;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @ClassName NoticeServiceImpl
 * @Author linrol
 * @date 2021年08月26日 21:17 Copyright (c) 2020, linrol@77hub.com All Rights Reserved.
 */
@Component
public class NoticeServiceImpl implements NoticeService, ApplicationRunner {

  private Logger logger = LoggerFactory.getLogger(NoticeServiceImpl.class);

  private static final String wxWorkWebHook = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=%s";

  public static List<User> userList = Lists.newLinkedList();

  static {
    User user1 = User.of("胡敬华", "18181998760", "708", "722");
    User user2 = User.of("罗林", "18883754124", "709", "721");
    User user3 = User.of("曾天保", "15386533091", "710", "723");
    User user4 = User.of("龚建平", "13541201025", "711", "724");
    User user5 = User.of("陈涛", "18502819032", "712", "725");
    User user6 = User.of("刘睿", "17608091991", "713", "726");
    User user7 = User.of("王书硕", "17608091991", "714", "727");
    User user8 = User.of("丁鑫", "17512545261", "715", "728");
    User user9 = User.of("杨志强", "18581584998", "716", "729");
    User user10 = User.of("那超", "15680033681", "717", "730");
    userList.add(user1);
    userList.add(user2);
    userList.add(user3);
    userList.add(user4);
    userList.add(user5);
    userList.add(user6);
    userList.add(user7);
    userList.add(user8);
    userList.add(user9);
    userList.add(user10);
  }


  @Autowired
  private ShutdownContext shutdownContext;

  @Autowired
  private ZentaoService zentaoService;

  @Autowired
  private RedisUtil redisUtil;

  @Value("${notice.group}")
  private String noticeGroup;

  private boolean noticeStatus = false;

  /**
   * 一次性通知
   *
   * @return
   */
  @PostConstruct
  public boolean noticeOneTime() {
    boolean noticeBug = this.notice2WxWork(ZentaoResource.BUG, new ListResourceHandler() {
      @Override
      public <T extends BaseEntity> List<T> filter(List<T> list) {
        return list.stream().filter(f -> {
          Bug bug = (Bug) f;
          return bug.getCreatedTime().after(DateUtils.getOfDayFirst(new Date()));
        }).collect(Collectors.toList());
      }
    });

    boolean noticeTask = this.notice2WxWork(ZentaoResource.TASK, new ListResourceHandler() {
      @Override
      public <T extends BaseEntity> List<T> filter(List<T> list) {
        return list.stream().filter(f -> {
          Task task = (Task) f;
          return new Date().after(task.getDeadLineTime());
        }).collect(Collectors.toList());
      }
    });
    if (noticeBug && noticeTask) {
      return true;
    }
    return false;
  }

  public <T extends BaseEntity> boolean notice2WxWork(ZentaoResource zentaoResource,
      ListResourceHandler listResourceFilter) {
    String contextTitle = "姓 名\t\t类型\t\t\t标题\n\n";
    String context = userList.stream().map(user -> {
      String resourceUrlId = user.getResourceUrlId(zentaoResource);
      String requestUrl = String.format(zentaoResource.getUrl(), resourceUrlId);
      List<T> resourceList = zentaoService.getResourceList(requestUrl, zentaoResource);
      resourceList = listResourceFilter.filter(resourceList);
      if (resourceList.isEmpty()) {
        return null;
      }
      String userLines = resourceList.stream().map(resource -> {
        String userName = user.getName();
        return resource.getMarkDown(userName);
      }).collect(Collectors.joining());
      return userLines + "\n";
    }).filter(Objects::nonNull).collect(Collectors.joining());
    if (StringUtils.isBlank(context)) {
      if (zentaoResource.equals(ZentaoResource.BUG)) {
        context = "enjoy！今日没有激活的Bug";
      }
      if (zentaoResource.equals(ZentaoResource.TASK)) {
        context = "enjoy！今日没有未完成的任务";
      }
    }
    Markdown markdown = Markdown.of(contextTitle + context);
    WxWorkMessage wxWorkMessage = WxWorkMessage.ofMarkdown(markdown);
    String pushMessage = JSON.toJSONString(wxWorkMessage);
    JSONObject jsonObject = JSON.parseObject(pushMessage);
    String hookKey = getWxWorkWebHookKey(noticeGroup);
    String post = HttpClientUtils.post(String.format(wxWorkWebHook, hookKey), jsonObject);
    noticeStatus = true;
    logger.info("pre exist，done post: {}", post);
    return true;
  }

  /**
   * 获取企业微信WebHook的key
   *
   * @param noticeGroup
   * @return
   */
  private String getWxWorkWebHookKey(String noticeGroup) {
    return redisUtil.hget("WxWebHookKey", noticeGroup).toString();
  }

  @Override
  public void run(ApplicationArguments args) {
    if (noticeStatus) {
      shutdownContext.showdown();
    }
  }
}
