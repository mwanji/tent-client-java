package com.moandjiezana.tent.client.apps;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistrationResponse {

  private String id;
  private String macKeyId;
  private String macKey;
  private String macAlgorithm;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getMacKeyId() {
    return macKeyId;
  }

  @JsonProperty("mac_key_id")
  public void setMacKeyId(String macKeyId) {
    this.macKeyId = macKeyId;
  }

  public String getMacKey() {
    return macKey;
  }

  @JsonProperty("mac_key")
  public void setMacKey(String macKey) {
    this.macKey = macKey;
  }

  public String getMacAlgorithm() {
    return macAlgorithm;
  }

  @JsonProperty("mac_algorithm")
  public void setMacAlgorithm(String macAlgorithm) {
    this.macAlgorithm = macAlgorithm;
  }
}
