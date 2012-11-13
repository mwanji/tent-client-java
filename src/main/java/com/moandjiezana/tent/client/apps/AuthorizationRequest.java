package com.moandjiezana.tent.client.apps;

import com.moandjiezana.tent.client.internal.com.google.common.base.Joiner;

public class AuthorizationRequest {
  private static final String[] EMPTY_STRING_ARRAY = new String[0];

  private static final Joiner JOINER = Joiner.on(',').skipNulls();

  private final String clientId;
  
  private final String redirectUri;
  
  private String state;
  private String tentNotificationUrl;
  private String[] scopes = EMPTY_STRING_ARRAY;
  private String[] tentProfileInfoTypes = EMPTY_STRING_ARRAY;
  private String[] tentPostTypes = EMPTY_STRING_ARRAY;

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
    return JOINER.join(scopes);
  }

  public void setScope(String... scopes) {
    this.scopes = scopes;
  }

  public String getTentProfileInfoTypes() {
    return JOINER.join(tentProfileInfoTypes);
  }

  public void setTentProfileInfoTypes(String... tentProfileInfoTypes) {
    this.tentProfileInfoTypes = tentProfileInfoTypes;
  }

  public String getTentPostTypes() {
    return JOINER.join(tentPostTypes);
  }

  public void setTentPostTypes(String... tentPostTypes) {
    this.tentPostTypes = tentPostTypes;
  }

  public String getTentNotificationUrl() {
    return tentNotificationUrl;
  }

  public void setTentNotificationUrl(String tentNotificationUrl) {
    this.tentNotificationUrl = tentNotificationUrl;
  }
}
