package com.moandjiezana.tent.client.apps;

public class Authorization {

  private String id;
  private String notificationUrl;
  private String[] postTypes;
  private String[] profileInfoTypes;
  private String[] scopes;

  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getNotificationUrl() {
    return notificationUrl;
  }
  public void setNotificationUrl(String notificationUrl) {
    this.notificationUrl = notificationUrl;
  }
  public String[] getPostTypes() {
    return postTypes;
  }
  public void setPostTypes(String[] postTypes) {
    this.postTypes = postTypes;
  }
  public String[] getProfileInfoTypes() {
    return profileInfoTypes;
  }
  public void setProfileInfoTypes(String[] profileInfoTypes) {
    this.profileInfoTypes = profileInfoTypes;
  }
  public String[] getScopes() {
    return scopes;
  }
  public void setScopes(String[] scopes) {
    this.scopes = scopes;
  }
}
