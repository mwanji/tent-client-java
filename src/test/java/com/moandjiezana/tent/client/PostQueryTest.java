package com.moandjiezana.tent.client;

import static org.junit.Assert.assertEquals;

import com.moandjiezana.tent.client.posts.Post;
import com.moandjiezana.tent.client.posts.PostQuery;
import com.moandjiezana.tent.client.users.Profile;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

public class PostQueryTest {
  
  @Test
  public void should_restrict_on_post_type() throws Exception {
    PostQuery postQuery = new PostQuery().postTypes("http://a.status.type", "http://b.status.type");
    
    assertEquals("post_types=http://a.status.type,http://b.status.type", postQuery.toString());
  }
  
  @Test
  public void should_restrict_on_entity() throws Exception {
    PostQuery postQuery = new PostQuery().entity("https://www.example.com");
    
    assertEquals("entity=https://www.example.com", postQuery.toString());
  }
  
  @Test
  public void should_combine_restrictions() {
    PostQuery postQuery = new PostQuery().entity("https://www.example.com").postTypes("http://a.status.type", "http://b.status.type");
    
    assertEquals("post_types=http://a.status.type,http://b.status.type&entity=https://www.example.com", postQuery.toString());
  }
  
  @Test @Ignore
  public void explore() throws Exception {
    Profile profile = new Profile();
    Profile.Core core = new Profile.Core();
    core.setServers(new String[] { "https://javaapiclient.tent.is/tent" });
    profile.setCore(core);
    
    TentClient tentClient = new TentClient(profile);
    List<Post> essays = tentClient.getAsync().getPosts(new PostQuery().postTypes(Post.Types.essay("v0.1.0"))).get();
    
    for (Post essay : essays) {
      System.out.println(essay.getId() + " " + essay.getType());
    }
  }
}
