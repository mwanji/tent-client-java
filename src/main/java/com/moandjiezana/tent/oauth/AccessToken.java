package com.moandjiezana.tent.oauth;


public class AccessToken {

  private String accessToken;
  
  private String macKey;
  
  private String macAlgorithm;
  
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
