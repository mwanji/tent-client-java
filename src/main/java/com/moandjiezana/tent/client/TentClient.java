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
  private final TentClientAsync tentClientAsync;
  
  /**
   * Use the default constructor only to discover an entity.
   */
  public TentClient(String entityUrl) {
    tentClientAsync = new TentClientAsync(entityUrl);
  }
  
  public TentClient(Profile profile, List<String> profileUrls) {
    tentClientAsync = new TentClientAsync(profile, profileUrls);
  }
  
  /**
   * Obtains the profile URLs for the given entity, first by HEAD, then by GET, if necessary.
   */
  public List<String> discover() {
    List<String> profileUrls = waitFor(tentClientAsync.discover("HEAD"));
    
    if (!profileUrls.isEmpty()) {
      return profileUrls;
    }
    
    return waitFor(tentClientAsync.discover("GET"));
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
  
  public Post write(Post post) {
    return waitFor(tentClientAsync.write(post));
  }
  
  public RegistrationResponse register(RegistrationRequest registrationRequest) {
    return waitFor(tentClientAsync.register(registrationRequest));
  }
  
  public TentClientAsync getAsync() {
    return tentClientAsync;
  }
  
  private <T> T waitFor(Future<T> future) {
    try {
      return future.get();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
