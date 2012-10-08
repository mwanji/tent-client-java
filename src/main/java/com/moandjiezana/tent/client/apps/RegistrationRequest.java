package com.moandjiezana.tent.client.apps;

import java.util.Map;

public class RegistrationRequest {

  private final String name;
  private final String description;
  private final String url;
  private final String[] redirectUris;
  private final Map<String, String> scopes;
  private String icon;

  public RegistrationRequest(String name, String description, String url, String[] redirectUris, Map<String, String> scopes) {
    this.name = name;
    this.description = description;
    this.url = url;
    this.redirectUris = redirectUris;
    this.scopes = scopes;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getUrl() {
    return url;
  }

  public String[] getRedirectUris() {
    return redirectUris;
  }

  public Map<String, String> getScopes() {
    return scopes;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }
}
