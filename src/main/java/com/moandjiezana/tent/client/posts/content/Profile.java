package com.moandjiezana.tent.client.posts.content;

import com.moandjiezana.tent.client.posts.Post;

public class Profile implements PostContent {
  public static final String URI = Post.Types.profile("v0.1.0");
  
  private String action;
  private String[] types;
  
  @Override
  public String getType() {
    return URI;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public String[] getTypes() {
    return types;
  }

  public void setTypes(String[] types) {
    this.types = types;
  }
}
