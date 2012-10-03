package com.moandjiezana.tent.client.apps;

import com.google.common.base.Joiner;



public class AuthorizationRequest {

  private static final Joiner JOINER = Joiner.on(',').skipNulls();

  private final String clientId;
  
  private final String redirectUri;
  
  private String state;
  private String scope;
  private String tentProfileInfoTypes;
  private String tentPostTypes;
  private String tentNotificationUrl;

  public AuthorizationRequest(String clientId, String redirectUri) {
    this.clientId = clientId;
    this.redirectUri = redirectUri;
  }

  public String getClientId() {
    return clientId;
  }
  
  public String getRedirectUri() {
    return redirectUri;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getScope() {
    return scope;
  }

  public void setScope(String... scopes) {
    this.scope = JOINER.join(scopes);
  }

  public String getTentProfileInfoTypes() {
    return tentProfileInfoTypes;
  }

  public void setTentProfileInfoTypes(String... tentProfileInfoTypes) {
    this.tentProfileInfoTypes = JOINER.join(tentProfileInfoTypes);
  }

  public String getTentPostTypes() {
    return tentPostTypes;
  }

  public void setTentPostTypes(String... tentPostTypes) {
    this.tentPostTypes = JOINER.join(tentPostTypes);
  }

  public String getTentNotificationUrl() {
    return tentNotificationUrl;
  }

  public void setTentNotificationUrl(String... tentNotificationUrl) {
    this.tentNotificationUrl = JOINER.join(tentNotificationUrl);
  }
}
