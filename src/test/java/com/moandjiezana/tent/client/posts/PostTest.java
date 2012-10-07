package com.moandjiezana.tent.client.posts;

import static org.junit.Assert.assertEquals;

import com.moandjiezana.tent.client.posts.content.PostContent;

import org.junit.Test;

public class PostTest {

  @Test
  public void set_content_should_get_type_from_post_content() {
    PostContent postContent = new PostContent() {
      @Override
      public String getType() {
        return "my custom type";
      }
    };
    
    Post post = new Post();
    post.setContent(postContent);
    
    assertEquals(postContent.getType(), post.getType());
  }
}
