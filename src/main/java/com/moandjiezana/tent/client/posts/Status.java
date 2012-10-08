package com.moandjiezana.tent.client.posts;

public class Status {
  
  public static final String URI = "https://tent.io/types/post/status/v0.1.0";
  
  private String text;
  private GeoJsonPoint location;

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