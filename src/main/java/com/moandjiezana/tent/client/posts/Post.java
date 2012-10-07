package com.moandjiezana.tent.client.posts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moandjiezana.tent.client.posts.content.PostContent;
import com.moandjiezana.tent.client.users.Permissions;

import java.io.StringWriter;

public class Post {

  private String id;
  private String entity;
  @JsonProperty("published_at")
  private long publishedAt;
  @JsonProperty("received_at")
  private long receivedAt;
  @JsonProperty("updated_at")
  private long updatedAt;
  private Mention[] mentions;
  private String[] licenses;
  private String type;
  private PostContent content;
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

  public long getPublishedAt() {
    return publishedAt;
  }

  public void setPublishedAt(long publishedAt) {
    this.publishedAt = publishedAt;
  }

  public long getReceivedAt() {
    return receivedAt;
  }

  public void setReceivedAt(long receivedAt) {
    this.receivedAt = receivedAt;
  }

  public long getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(long updatedAt) {
    this.updatedAt = updatedAt;
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

  public PostContent getContent() {
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

  /**
   * Also sets the result of {@link PostContent#getType()} as type of the post
   * @param content
   */
  public void setContent(PostContent content) {
    this.content = content;
    setType(content.getType());
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
