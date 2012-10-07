package com.moandjiezana.tent.client.posts.content;

import java.util.Map;

public class StatusContent implements PostContent {

  private String text;
  private Map<String, Object> location;

  @Override
  public String getType() {
    return "https://tent.io/types/post/status/v0.1.0";
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Map<String, Object> getLocation() {
    return location;
  }

  public void setLocation(Map<String, Object> location) {
    this.location = location;
  }
}
