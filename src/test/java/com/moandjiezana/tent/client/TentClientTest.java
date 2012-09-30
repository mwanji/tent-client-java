package com.moandjiezana.tent.client;

import static com.github.restdriver.clientdriver.RestClientDriver.giveEmptyResponse;
import static com.github.restdriver.clientdriver.RestClientDriver.giveResponse;
import static com.github.restdriver.clientdriver.RestClientDriver.onRequestTo;
import static org.fest.assertions.Assertions.assertThat;

import com.github.restdriver.clientdriver.ClientDriverRequest.Method;
import com.github.restdriver.clientdriver.ClientDriverRule;
import com.moandjiezana.tent.client.posts.Mention;
import com.moandjiezana.tent.client.posts.Post;
import com.moandjiezana.tent.client.posts.Status;
import com.moandjiezana.tent.client.users.Profile;

import java.util.List;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;


/**
 * Unit test for simple App.
 */
public class TentClientTest {
  
  @Rule
  public final ClientDriverRule server = new ClientDriverRule();
  private final TentClientAsync tentClient = new TentClientAsync();

  @Test @Ignore
  public void explore() {
    TentClient tentClientSync = new TentClient();
    tentClientSync.discover("https://mwanji.tent.is");
    Profile profile = tentClientSync.getProfile();
//    
//    System.out.println(profile.getCore().getEntity());
//    System.out.println(profile.getBasic().getName());
//    System.out.println(profile.getBasic().getAvatarUrl());
//    
//    List<Post> posts = tentClient.getPosts();
//    
//    printPosts(posts);
//    
//    List<Following> followings = tentClient.getFollowings();
//    
//    for (Following following : followings) {
//      TentClient followingTentClient = new TentClient();
//      followingTentClient.discover(following.getEntity());
//      Profile followingProfile = followingTentClient.getProfile();
//      Following followingDetail = tentClient.getFollowing(following);
//      
//      System.out.println(following.getEntity() + " / id=" + following.getId());
//      System.out.println(followingProfile.getBasic().getName());
//      printPosts(followingTentClient.getPosts());
//    }
//    
//    HashMap<String, String> scopes = new HashMap<String, String>();
//    scopes.put("write_profile", "Not really used, just testing");
    
//    RegistrationResponse registrationResponse = tentClient.register(new RegistrationRequest("Java Client Test", "Running dev tests", "http://www.moandjiezana.com/tent-client-java", new String [] { "http://www.moandjiezana.com/tent-client-java" }, scopes));
//    
//    System.out.println(registrationResponse.getId());
//    System.out.println(registrationResponse.getMacKey());
//    System.out.println(registrationResponse.getMacKeyId());
//    System.out.println(registrationResponse.getMacAlgorithm());
  }
  
  @Test
  public void should_discover_profile_url_from_header() throws Exception {
    server.addExpectation(onRequestTo("/").withMethod(Method.HEAD), giveEmptyResponse().withContentType("text/html;charset=utf-8").withHeader("Link", "<" + server.getBaseUrl() + "/tent/profile>; rel=\"https://tent.io/rels/profile\""));
    
    List<String> profileUrls = tentClient.discover(server.getBaseUrl(), "HEAD").get();
    
    assertThat(profileUrls).containsOnly(profileUrl());
  }
  
  @Test
  public void should_discover_profile_url_from_tag() throws Exception {
    server.addExpectation(onRequestTo("/"), giveResponse("<html><head><link href=\"" + server.getBaseUrl() + "/tent/profile\" rel=\"https://tent.io/rels/profile\" /><link href=\"other_href\" rel=\"other_rel\" /></head><body></body></html>"));
    
    List<String> profileUrls = tentClient.discover(server.getBaseUrl(), "GET").get();
    
    assertThat(profileUrls).containsOnly(profileUrl());
  }

  private String profileUrl() {
    return server.getBaseUrl() + "/tent/profile";
  }
  
  private String profileJson() {
    return "{\"https://tent.io/types/info/basic/v0.1.0\":{\"name\":\"Mwanji Ezana\",\"avatar_url\":\"http://www.gravatar.com/avatar/ae8715093d8d4219507146ed34f0ed16.png\",\"birthdate\":\"\",\"location\":\"\",\"gender\":\"M\",\"bio\":\"\",\"permissions\":{\"public\":true}},\"https://tent.io/types/info/core/v0.1.0\":{\"entity\":\"" + server.getBaseUrl() + "\",\"licenses\":[],\"servers\":[\"https://mwanji.tent.is/tent\"],\"permissions\":{\"public\":true}}}";
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
