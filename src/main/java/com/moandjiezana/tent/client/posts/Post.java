package com.moandjiezana.tent.client.posts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moandjiezana.tent.client.users.Permissions;

import java.io.StringWriter;
import java.util.Map;

public class Post {

  private String id;
  private String entity;
  @JsonProperty("published_at")
  private int publishedAt;
  @JsonProperty("received_at")
  private int receivedAt;
  private Mention[] mentions;
  private String[] licenses;
  private String type;
  private Map<String, Object> content;
  private Attachment[] attachments;
  private App app;
  private Permissions permissions;
  private String version;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getEntity() {
    return entity;
  }

  public void setEntity(String entity) {
    this.entity = entity;
  }

  public int getPublishedAt() {
    return publishedAt;
  }

  public void setPublishedAt(int publishedAt) {
    this.publishedAt = publishedAt;
  }

  public int getReceivedAt() {
    return receivedAt;
  }

  public void setReceivedAt(int receivedAt) {
    this.receivedAt = receivedAt;
  }

  public Mention[] getMentions() {
    return mentions;
  }

  public void setMentions(Mention[] mentions) {
    this.mentions = mentions;
  }

  public String[] getLicenses() {
    return licenses;
  }

  public void setLicenses(String[] licenses) {
    this.licenses = licenses;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Map<String, Object> getContent() {
    return content;
  }
  
  @JsonIgnore
  public <T> T getContentAs(Class<T> contentClass) {
    StringWriter jsonWriter = new StringWriter();
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.writeValue(jsonWriter, getContent());
      return objectMapper.readValue(jsonWriter.toString(), contentClass);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void setContent(Map<String, Object> content) {
    this.content = content;
  }

  public Attachment[] getAttachments() {
    return attachments;
  }

  public void setAttachments(Attachment[] attachments) {
    this.attachments = attachments;
  }

  public App getApp() {
    return app;
  }

  public void setApp(App app) {
    this.app = app;
  }

  public Permissions getPermissions() {
    return permissions;
  }

  public void setPermissions(Permissions permissions) {
    this.permissions = permissions;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }
}
