package com.moandjiezana.tent.client.posts;

public class Mention {
  private String entity;
  private String post;
  
  public Mention() {
  }

  public Mention(String entity) {
    this.entity = entity;
  }

  public Mention(String entity, String post) {
    this.entity = entity;
    this.post = post;
  }

  public String getEntity() {
    return entity;
  }

  public void setEntity(String entity) {
    this.entity = entity;
  }

  public String getPost() {
    return post;
  }

  public void setPost(String post) {
    this.post = post;
  }
}