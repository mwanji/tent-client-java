package com.moandjiezana.tent.oauth;

public class AccessTokenRequest {

  private String code;
  private String tokenType = "mac";

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getTokenType() {
    return tokenType;
  }

  public void setTokenType(String tokenType) {
    this.tokenType = tokenType;
  }
}
