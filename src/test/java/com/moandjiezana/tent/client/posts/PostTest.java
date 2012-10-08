package com.moandjiezana.tent.client.posts;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import com.moandjiezana.tent.client.posts.content.PostContent;
import com.moandjiezana.tent.client.posts.content.StatusContent;

import org.junit.Test;

public class PostTest {

  @Test
  public void content_should_determine_post_type() {
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
  
  @Test
  public void should_return_content_as_requested_type() {
    Post post = new Gson().fromJson("{content:{text:\"my text\"}}", Post.class);
    
    assertEquals("my text", post.getContentAs(StatusContent.class).getText());
  }
}
