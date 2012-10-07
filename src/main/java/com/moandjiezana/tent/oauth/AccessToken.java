package com.moandjiezana.tent.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccessToken {

  @JsonProperty("access_token")
  private String accessToken;
  
  @JsonProperty("mac_key")
  private String macKey;
  
  @JsonProperty("mac_algorithm")
  private String macAlgorithm;
  
  @JsonProperty("token_type")
  private String tokenType;

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
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
  
  public String getMacAlgorithmForJava() {
    if ("hmac-sha-256".equals(getMacAlgorithm())) {
      return "HmacSHA256";
    }
    
    throw new IllegalArgumentException("Unknown Mac algorithm " + getMacAlgorithm());
  }

  public void setMacAlgorithm(String macAlgorithm) {
    this.macAlgorithm = macAlgorithm;
  }

  public String getTokenType() {
    return tokenType;
  }

  public void setTokenType(String tokenType) {
    this.tokenType = tokenType;
  }
}
