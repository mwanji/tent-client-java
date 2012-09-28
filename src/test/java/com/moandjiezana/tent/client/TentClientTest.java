package com.moandjiezana.tent.client;

import com.moandjiezana.tent.client.apps.RegistrationRequest;
import com.moandjiezana.tent.client.apps.RegistrationResponse;
import com.moandjiezana.tent.client.users.Following;
import com.moandjiezana.tent.client.users.Profile;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;


/**
 * Unit test for simple App.
 */
public class TentClientTest {

  @Test
  public void discover() {
    TentClient tentClient = new TentClient();
    tentClient.discover("https://mwanji.tent.is");
    Profile profile = tentClient.getProfile();
    
    System.out.println(profile.getCore().getEntity());
    System.out.println(profile.getBasic().getName());
    System.out.println(profile.getBasic().getAvatarUrl());
    
    List<Following> followings = tentClient.getFollowings();
    
    for (Following following : followings) {
      System.out.println(following.getEntity() + " / " + following.getPermissions());
    }
    
    HashMap<String, String> scopes = new HashMap<String, String>();
    scopes.put("write_profile", "Not really used, just testing");
    
    RegistrationResponse registrationResponse = tentClient.register(new RegistrationRequest("Java Client Test", "Running dev tests", "http://www.moandjiezana.com/tent-client-java", new String [] { "http://www.moandjiezana.com/tent-client-java" }, scopes));
    
    System.out.println(registrationResponse.getId());
    System.out.println(registrationResponse.getMacKey());
    System.out.println(registrationResponse.getMacKeyId());
    System.out.println(registrationResponse.getMacAlgorithm());
  }
}
