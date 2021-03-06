package com.moandjiezana.tent.client.users;

import java.util.Map;

public class Following {

  private String id;
  private String remoteId;
  private String entity;
  private Map<String, Object> permissions;
  
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getRemoteId() {
    return remoteId;
  }
  public void setRemoteId(String remoteId) {
    this.remoteId = remoteId;
  }
  public String getEntity() {
    return entity;
  }
  public void setEntity(String entity) {
    this.entity = entity;
  }
  public Map<String, Object> getPermissions() {
    return permissions;
  }
  public void setPermissions(Map<String, Object> permissions) {
    this.permissions = permissions;
  }
}
