package com.moandjiezana.tent.client.users;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class Following {

  private String id;
  private String remoteId;
  private String entity;
  private Map<String, String> permissions;
  
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getRemoteId() {
    return remoteId;
  }
  @JsonProperty("remote_id")
  public void setRemoteId(String remoteId) {
    this.remoteId = remoteId;
  }
  public String getEntity() {
    return entity;
  }
  public void setEntity(String entity) {
    this.entity = entity;
  }
  public Map<String, String> getPermissions() {
    return permissions;
  }
  public void setPermissions(Map<String, String> permissions) {
    this.permissions = permissions;
  }
}
