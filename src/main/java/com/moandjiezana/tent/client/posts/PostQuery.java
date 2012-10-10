package com.moandjiezana.tent.client.posts;

import com.moandjiezana.tent.client.internal.com.google.common.base.Joiner;

public class PostQuery {

  private static final Joiner COMMAS = Joiner.on(',').skipNulls();
  private static final Joiner AMPERSANDS = Joiner.on('&').skipNulls();

  private String[] postTypes;
  
  public PostQuery postTypes(String... postTypes) {
    this.postTypes = postTypes;
    
    return this;
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
    
    return AMPERSANDS.join(postTypesQuery, null);
  }

  public String[] getPostTypes() {
    return postTypes;
  }
}
