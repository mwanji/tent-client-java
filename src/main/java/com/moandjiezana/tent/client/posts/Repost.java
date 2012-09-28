package com.moandjiezana.tent.client.posts;

public class Repost {
  public static final String URI = "https://tent.io/types/post/repost/v0.1.0";
  
  private String entity;
  private String id;

  public String getEntity() {
    return entity;
  }

  public void setEntity(String entity) {
    this.entity = entity;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}