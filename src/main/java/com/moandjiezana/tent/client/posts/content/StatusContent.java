package com.moandjiezana.tent.client.posts.content;

import com.moandjiezana.tent.client.posts.GeoJsonPoint;
import com.moandjiezana.tent.client.posts.Post;

public class StatusContent implements PostContent {
  private static final String URI = Post.Types.status("v0.1.0");

  private String text;
  private GeoJsonPoint location;
  
  public StatusContent() {
  }

  public StatusContent(String text) {
    this.text = text;
  }

  public StatusContent(String text, GeoJsonPoint location) {
    this.text = text;
    this.location = location;
  }

  @Override
  public String getType() {
    return URI;
  }
  
  public String getText() {
    return text;
  }
  
  public void setText(String text) {
    this.text = text;
  }

  public GeoJsonPoint getLocation() {
    return location;
  }

  public void setLocation(GeoJsonPoint location) {
    this.location = location;
  }
}
