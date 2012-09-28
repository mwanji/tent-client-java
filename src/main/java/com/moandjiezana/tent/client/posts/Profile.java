package com.moandjiezana.tent.client.posts;

public class Profile {
  public static final String URI = "https://tent.io/types/post/profile/v0.1.0";
  
  private String action;
  private String[] types;

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
