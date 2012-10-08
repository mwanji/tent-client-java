package com.moandjiezana.tent.client.posts;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.moandjiezana.tent.client.posts.content.PostContent;
import com.moandjiezana.tent.client.users.Permissions;

import java.util.Map;

public class Post {
  
  private static final Gson gson = new Gson();

  private String id;
  private String entity;
  private long publishedAt;
  private long receivedAt;
  private long updatedAt;
  private Mention[] mentions;
  private String[] licenses;
  private String type;
  private Map<String, Object> content;
  @Expose(serialize = false, deserialize = false)
  private PostContent postContent;
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

  public <T extends PostContent> T getContentAs(Class<T> contentClass) {
    if (postContent == null || !contentClass.isAssignableFrom(postContent.getClass())) {
      postContent = gson.fromJson(gson.toJson(content), contentClass);
    }
    return contentClass.cast(postContent);
  }

  /**
   * Also sets the result of {@link PostContent#getType()} as type of the post
   * @param content
   */
  public void setContent(PostContent content) {
    this.postContent = content;
    this.content = gson.fromJson(gson.toJson(content), new TypeToken<Map<String, Object>>() {}.getType());
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
