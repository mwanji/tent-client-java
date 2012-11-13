package com.moandjiezana.tent.client.posts.content;

import com.moandjiezana.tent.client.posts.Post;

public class Repost implements PostContent {
  private static final String URI = Post.Types.repost("v0.1.0");
  
  private String entity;
  private String id;

  public Repost(String entity, String id) {
    this.entity = entity;
    this.id = id;
  }
  
  public Repost() {}

  @Override
  public String getType() {
    return URI;
  }

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