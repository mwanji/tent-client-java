package com.moandjiezana.tent.client;

import com.moandjiezana.tent.client.posts.Mention;
import com.moandjiezana.tent.client.posts.Post;
import com.moandjiezana.tent.client.posts.Status;
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
  public void explore() {
    TentClient tentClient = new TentClient();
    tentClient.discover("https://mwanji.tent.is");
    Profile profile = tentClient.getProfile();
    
    System.out.println(profile.getCore().getEntity());
    System.out.println(profile.getBasic().getName());
    System.out.println(profile.getBasic().getAvatarUrl());
    
    List<Post> posts = tentClient.getPosts();
    
    printPosts(posts);
    
    List<Following> followings = tentClient.getFollowings();
    
    for (Following following : followings) {
      TentClient followingTentClient = new TentClient();
      followingTentClient.discover(following.getEntity());
      Profile followingProfile = followingTentClient.getProfile();
      Following followingDetail = tentClient.getFollowing(following);
      
      System.out.println(following.getEntity() + " / id=" + following.getId());
      System.out.println(followingProfile.getBasic().getName());
      printPosts(followingTentClient.getPosts());
    }
    
    HashMap<String, String> scopes = new HashMap<String, String>();
    scopes.put("write_profile", "Not really used, just testing");
    
//    RegistrationResponse registrationResponse = tentClient.register(new RegistrationRequest("Java Client Test", "Running dev tests", "http://www.moandjiezana.com/tent-client-java", new String [] { "http://www.moandjiezana.com/tent-client-java" }, scopes));
//    
//    System.out.println(registrationResponse.getId());
//    System.out.println(registrationResponse.getMacKey());
//    System.out.println(registrationResponse.getMacKeyId());
//    System.out.println(registrationResponse.getMacAlgorithm());
  }

  private void printPosts(List<Post> posts) {
    for (Post post : posts) {
      if (post.getType().equals(Status.URI)) {
        Status status = post.getContentAs(Status.class);
        System.out.println(status.getText());
        if (post.getMentions().length > 0) {
          System.out.println("Mentions: ");
          for (Mention mention : post.getMentions()) {
            System.out.println("\t" + mention.getEntity() + " @ " + mention.getPost());
          }
        }
        System.out.println("from " + post.getApp().getName());
      }
    }
  }
}
