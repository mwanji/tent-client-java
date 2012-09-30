package com.moandjiezana.tent.client;

import com.moandjiezana.tent.client.apps.RegistrationRequest;
import com.moandjiezana.tent.client.apps.RegistrationResponse;
import com.moandjiezana.tent.client.posts.Post;
import com.moandjiezana.tent.client.users.Following;
import com.moandjiezana.tent.client.users.Profile;

import java.util.List;
import java.util.concurrent.Future;

/**
 * A synchronous wrapper around {@link TentClientAsync}
 */
public class TentClient {
  private final TentClientAsync tentClientAsync = new TentClientAsync();
  
  /**
   * Use the default constructor only to discover an entity.
   */
  public TentClient() {}
  
  public List<String> discover(String entityUrl) {
    List<String> profileUrls = waitFor(tentClientAsync.discover(entityUrl, "HEAD"));
    
    if (!profileUrls.isEmpty()) {
      return profileUrls;
    }
    
    return waitFor(tentClientAsync.discover(entityUrl, "GET"));
  }
  
  public Profile getProfile() {
    return waitFor(tentClientAsync.getProfile());
  }
  
  public List<Following> getFollowings() {
    return waitFor(tentClientAsync.getFollowings());
  }
  
  public Following getFollowing(Following following) {
    return waitFor(tentClientAsync.getFollowing(following));
  }
  
  public List<Post> getPosts() {
    return waitFor(tentClientAsync.getPosts());
  }
  
  public RegistrationResponse register(RegistrationRequest registrationRequest) {
    return waitFor(tentClientAsync.register(registrationRequest));
  }
  
  private <T> T waitFor(Future<T> future) {
    try {
      return future.get();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
