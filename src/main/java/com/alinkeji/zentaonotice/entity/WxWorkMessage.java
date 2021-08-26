package com.alinkeji.zentaonotice.entity;

import com.alibaba.fastjson.annotation.JSONField;
import java.util.List;

/**
 * @Description
 * @ClassName WxWorkMessage
 * @Author linrol
 * @date 2021年08月26日 13:17 Copyright (c) 2020, linrol@77hub.com All Rights Reserved.
 */
public class WxWorkMessage {

  static class Text {

    private String content;

    @JSONField(name = "mentioned_list")
    private List<String> mentionedList;

    @JSONField(name = "mentioned_mobile_list")
    private List<String> mentionedMobileList;

    public String getContent() {
      return content;
    }

    public void setContent(String content) {
      this.content = content;
    }

    public List<String> getMentionedList() {
      return mentionedList;
    }

    public void setMentionedList(List<String> mentionedList) {
      this.mentionedList = mentionedList;
    }

    public List<String> getMentionedMobileList() {
      return mentionedMobileList;
    }

    public void setMentionedMobileList(List<String> mentionedMobileList) {
      this.mentionedMobileList = mentionedMobileList;
    }
  }

  public static class Markdown {

    private String content;

    public String getContent() {
      return content;
    }

    public void setContent(String content) {
      this.content = content;
    }

    public static Markdown of(String content) {
      Markdown markdown = new Markdown();
      markdown.setContent(content);
      return markdown;
    }
  }

  @JSONField(name = "msgtype")
  private String msgType;

  private Text text;

  private Markdown markdown;

  public String getMsgType() {
    return msgType;
  }

  public void setMsgType(String msgType) {
    this.msgType = msgType;
  }

  public Text getText() {
    return text;
  }

  public void setText(Text text) {
    this.text = text;
  }

  public Markdown getMarkdown() {
    return markdown;
  }

  public void setMarkdown(Markdown markdown) {
    this.markdown = markdown;
  }

  public static WxWorkMessage ofText(Text text) {
    WxWorkMessage wxWorkMessage = new WxWorkMessage();
    wxWorkMessage.setMsgType("text");
    wxWorkMessage.setText(text);
    return wxWorkMessage;
  }

  public static WxWorkMessage ofMarkdown(Markdown markdown) {
    WxWorkMessage wxWorkMessage = new WxWorkMessage();
    wxWorkMessage.setMsgType("markdown");
    wxWorkMessage.setMarkdown(markdown);
    return wxWorkMessage;
  }
}
