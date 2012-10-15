package com.moandjiezana.tent.client;

import com.moandjiezana.tent.client.apps.AuthorizationRequest;
import com.moandjiezana.tent.client.apps.RegistrationRequest;
import com.moandjiezana.tent.client.apps.RegistrationResponse;
import com.moandjiezana.tent.client.posts.Post;
import com.moandjiezana.tent.client.posts.PostQuery;
import com.moandjiezana.tent.client.users.Following;
import com.moandjiezana.tent.client.users.Profile;
import com.moandjiezana.tent.oauth.AccessToken;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

/**
 * A blocking, higher-level wrapper around {@link TentClientAsync}
 */
public class TentClient {
  private final TentClientAsync tentClientAsync;
  private List<String> profileUrls = Collections.<String>emptyList();
  
  /**
   * Use the default constructor only to discover an entity.
   */
  public TentClient(String entityUrl) {
    tentClientAsync = new TentClientAsync(entityUrl);
  }
  
  public TentClient(Profile profile) {
    tentClientAsync = new TentClientAsync(profile);
  }
  
  /**
   * Obtains the profile URLs for the given entity, first by HEAD, then by GET, if necessary.
   */
  public List<String> discover() {
    profileUrls = waitFor(tentClientAsync.discover("HEAD"));
    
    if (profileUrls.isEmpty()) {
      profileUrls = waitFor(tentClientAsync.discover("GET"));
    }
    
    return profileUrls;
  }
  
  /**
   * If discovery has not been performed, will do it first.
   * @return
   */
  public Profile getProfile() {
    if (profileUrls.isEmpty()) {
      discover();
    }
    
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
  
  public List<Post> getPosts(PostQuery query) {
    return waitFor(tentClientAsync.getPosts(query));
  }

  public Post getPost(String id) {
    return waitFor(tentClientAsync.getPost(id));
  }
  
  public Post write(Post post) {
    return waitFor(tentClientAsync.write(post));
  }
  
  public RegistrationResponse register(RegistrationRequest registrationRequest) {
    return waitFor(tentClientAsync.register(registrationRequest));
  }
  
  public String buildAuthorizationUrl(AuthorizationRequest authorizationRequest) {
    return tentClientAsync.buildAuthorizationUrl(authorizationRequest);
  }
  
  public AccessToken getAccessToken(RegistrationResponse registrationResponse, String code) {
    return waitFor(tentClientAsync.getAccessToken(code));
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
