package com.moandjiezana.tent.client.apps;

import java.util.Map;

public class RegistrationResponse {

  private String id;
  private String macKeyId;
  private String macKey;
  private String macAlgorithm;
  private String[] redirectUris;

  private String name;
  private String description;
  private String url;
  private Map<String, String> scopes;
  private String icon;
  
  public RegistrationResponse(RegistrationRequest registrationRequest) {
    this.name = registrationRequest.getName();
    this.description = registrationRequest.getDescription();
    this.url = registrationRequest.getUrl();
    this.scopes = registrationRequest.getScopes();
    this.icon = registrationRequest.getIcon();
    this.redirectUris = registrationRequest.getRedirectUris();
  }

  public RegistrationResponse() {}
  
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getMacKeyId() {
    return macKeyId;
  }

  public void setMacKeyId(String macKeyId) {
    this.macKeyId = macKeyId;
  }

  public String getMacKey() {
    return macKey;
  }

  public void setMacKey(String macKey) {
    this.macKey = macKey;
  }

  public String getMacAlgorithm() {
    return macAlgorithm;
  }

  public void setMacAlgorithm(String macAlgorithm) {
    this.macAlgorithm = macAlgorithm;
  }

  public String[] getRedirectUris() {
    return redirectUris;
  }

  public void setRedirectUris(String[] redirectUris) {
    this.redirectUris = redirectUris;
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

  public Map<String, String> getScopes() {
    return scopes;
  }

  public void setScopes(Map<String, String> scopes) {
    this.scopes = scopes;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }
}
