package com.moandjiezana.tent.client.posts;

import com.google.common.base.Strings;
import com.moandjiezana.tent.client.internal.com.google.common.base.Joiner;

import java.util.HashMap;
import java.util.Map;

public class PostQuery {

  private static final Joiner COMMAS = Joiner.on(',').skipNulls();
  private static final Joiner AMPERSANDS = Joiner.on('&').skipNulls();

  private String[] postTypes;
  private String entity;
  
  public PostQuery postTypes(String... postTypes) {
    this.postTypes = postTypes;
    
    return this;
  }
  
  public PostQuery entity(String entity) {
    this.entity = entity;
    
    return this;
  }
  
  public Map<String, String[]> toMap() {
    HashMap<String, String[]> map = new HashMap<String, String[]>();
    
    if (postTypes != null) {
      map.put("post_types", postTypes);
    }
    
    if (entity != null) {
      map.put("entity", new String[] { entity });
    }
    
    return map;
  }
  
  /**
   * @return unencoded query string parameters. Does not start with a question mark.
   */
  @Override
  public String toString() {
    String postTypesQuery = null;
    if (postTypes != null && postTypes.length > 0) {
      postTypesQuery = "post_types=" + COMMAS.join(postTypes);
    }
    
    String entityQuery = !Strings.isNullOrEmpty(entity) ? "entity=" + entity : null;
    
    return AMPERSANDS.join(postTypesQuery, entityQuery);
  }

  public String[] getPostTypes() {
    return postTypes;
  }
}
