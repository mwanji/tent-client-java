package com.moandjiezana.tent.client.apps;


public class RegistrationResponse {

  private String id;
  private String macKeyId;
  private String macKey;
  private String macAlgorithm;
  private String[] redirectUris;

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
}
