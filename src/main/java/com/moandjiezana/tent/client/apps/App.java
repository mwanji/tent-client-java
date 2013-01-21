package com.moandjiezana.tent.client.apps;

import java.util.Map;

public class App {

  private String id;
  private String name;
  private String description;
  private String url;
  private String icon;
  private String[] redirectUris;
  private Map<String, String> scopes;
  private Authorization[] authorizations;
  private long createdAt;

  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
  public String getUrl() {
    return url;
  }
  public void setUrl(String url) {
    this.url = url;
  }
  public String getIcon() {
    return icon;
  }
  public void setIcon(String icon) {
    this.icon = icon;
  }
  public String[] getRedirectUris() {
    return redirectUris;
  }
  public void setRedirectUris(String[] redirectUris) {
    this.redirectUris = redirectUris;
  }
  public Map<String, String> getScopes() {
    return scopes;
  }
  public void setScopes(Map<String, String> scopes) {
    this.scopes = scopes;
  }
  public Authorization[] getAuthorizations() {
    return authorizations;
  }
  public void setAuthorizations(Authorization[] authorizations) {
    this.authorizations = authorizations;
  }
  public long getCreatedAt() {
    return createdAt;
  }
  public void setCreatedAt(long createdAt) {
    this.createdAt = createdAt;
  }
}
