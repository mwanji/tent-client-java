package com.moandjiezana.tent.client.posts.content;

import java.util.List;

public class EssayContent implements PostContent {

  private String title;
  private String excerpt;
  private String body;
  private List<String> tags;

  @Override
  public String getType() {
    return "https://tent.io/types/post/essay/v0.1.0";
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getExcerpt() {
    return excerpt;
  }

  public void setExcerpt(String excerpt) {
    this.excerpt = excerpt;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }
}
