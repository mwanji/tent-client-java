package com.moandjiezana.tent.client;

import com.moandjiezana.tent.client.users.Following;
import com.moandjiezana.tent.client.users.Profile;

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
  }
}
