package com.moandjiezana.tent.client.users;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

public class Profile {

  public static class Core {
    
    public static final String URI = "https://tent.io/types/info/core/v0.1.0";
    
    private String entity;
    private String[] licenses;
    private String[] servers;
    private Map<String, String> permissions;
    
    public String getEntity() {
      return entity;
    }
    public void setEntity(String entity) {
      this.entity = entity;
    }
    public String[] getLicenses() {
      return licenses;
    }
    public void setLicenses(String[] licenses) {
      this.licenses = licenses;
    }
    public String[] getServers() {
      return servers;
    }
    public void setServers(String[] servers) {
      this.servers = servers;
    }
    public Map<String, String> getPermissions() {
      return permissions;
    }
    public void setPermissions(Map<String, String> permissions) {
      this.permissions = permissions;
    }
  }
  
  public static class Basic {
    
    public static final String URI = "https://tent.io/types/info/basic/v0.1.0";

    private String name;
    private String avatarUrl;
    private String birthdate;
    private String location;
    private String gender;
    private String bio;
    private Map<String, String> permissions;
    
    public String getName() {
      return name;
    }
    public void setName(String name) {
      this.name = name;
    }
    public String getAvatarUrl() {
      return avatarUrl;
    }
    
    @JsonProperty("avatar_url")
    public void setAvatarUrl(String avatarUrl) {
      this.avatarUrl = avatarUrl;
    }
    public String getBirthdate() {
      return birthdate;
    }
    public void setBirthdate(String birthdate) {
      this.birthdate = birthdate;
    }
    public String getLocation() {
      return location;
    }
    public void setLocation(String location) {
      this.location = location;
    }
    public String getGender() {
      return gender;
    }
    public void setGender(String gender) {
      this.gender = gender;
    }
    public String getBio() {
      return bio;
    }
    public void setBio(String bio) {
      this.bio = bio;
    }
    public Map<String, String> getPermissions() {
      return permissions;
    }
    public void setPermissions(Map<String, String> permissions) {
      this.permissions = permissions;
    }
  }
  
  private Profile.Core core;
  private Profile.Basic basic;
  
  public Profile.Core getCore() {
    return core;
  }
  
  public void setCore(Profile.Core core) {
    this.core = core;
  }

  public Profile.Basic getBasic() {
    return basic;
  }

  public void setBasic(Profile.Basic basic) {
    this.basic = basic;
  }
}
