package com.moandjiezana.tent.client;

import com.moandjiezana.tent.client.apps.RegistrationRequest;
import com.moandjiezana.tent.client.apps.RegistrationResponse;
import com.moandjiezana.tent.client.users.Following;
import com.moandjiezana.tent.client.users.Profile;

import java.util.List;

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
    try {
      return tentClientAsync.discover(entityUrl).get();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  public Profile getProfile() {
    try {
      return tentClientAsync.getProfile().get();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  public List<Following> getFollowings() {
    try {
      return tentClientAsync.getFollowings().get();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  public RegistrationResponse register(RegistrationRequest registrationRequest) {
    try {
      return tentClientAsync.register(registrationRequest).get();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
