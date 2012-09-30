package com.moandjiezana.tent.client;

import static com.github.restdriver.clientdriver.RestClientDriver.giveEmptyResponse;
import static com.github.restdriver.clientdriver.RestClientDriver.giveResponse;
import static com.github.restdriver.clientdriver.RestClientDriver.onRequestTo;
import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;

import com.github.restdriver.clientdriver.ClientDriverRequest.Method;
import com.github.restdriver.clientdriver.ClientDriverRule;
import com.moandjiezana.tent.client.apps.RegistrationRequest;
import com.moandjiezana.tent.client.posts.Mention;
import com.moandjiezana.tent.client.posts.Post;
import com.moandjiezana.tent.client.posts.Status;
import com.moandjiezana.tent.client.users.Profile;

import java.util.HashMap;
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

  @Test @Ignore
  public void explore() {
    TentClient tentClientSync = new TentClient("https://mwanji.tent.is");
    tentClientSync.discover();
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
    server.addExpectation(onRequestTo("/").withMethod(Method.HEAD), giveEmptyResponse().withContentType("text/html;charset=utf-8").withHeader("Link", "<" + profileUrl() + ">; rel=\"https://tent.io/rels/profile\""));
    
    List<String> profileUrls = tentClient().discover("HEAD").get();
    
    assertThat(profileUrls).containsOnly(profileUrl());
  }
  
  @Test
  public void should_discover_no_profile_url_when_none_in_header() throws Exception {
    server.addExpectation(onRequestTo("/").withMethod(Method.HEAD), giveEmptyResponse().withContentType("text/html;charset=utf-8"));
    
    List<String> profileUrls = tentClient().discover("HEAD").get();
    
    assertThat(profileUrls).isEmpty();
  }
  
  @Test
  public void should_discover_profile_url_from_tag() throws Exception {
    server.addExpectation(onRequestTo("/"), giveResponse("<html><head><link href=\"" + profileUrl() + "\" rel=\"https://tent.io/rels/profile\" /><link href=\"other_href\" rel=\"other_rel\" /></head><body></body></html>"));
    
    List<String> profileUrls = tentClient().discover("GET").get();
    
    assertThat(profileUrls).containsOnly(profileUrl());
  }
  
  @Test
  public void should_discover_no_profile_url_when_none_in_html() throws Exception {
    server.addExpectation(onRequestTo("/"), giveResponse("<html><head><link href=\"other_href\" rel=\"other_rel\" /></head><body></body></html>"));
    
    List<String> profileUrls = tentClient().discover("GET").get();
    
    assertThat(profileUrls).isEmpty();
  }
  
  @Test
  public void should_register_with_server() throws Exception {
    Profile profile = new Profile();
    Profile.Core core = new Profile.Core();
    core.setServers(new String[] { server.getBaseUrl() });
    profile.setCore(core);
    
    TentClientAsync tentClient = new TentClientAsync(profile, asList(profileUrl()));
    
    server.addExpectation(onRequestTo("/apps").withMethod(Method.POST), giveResponse("{\"name\": \"FooApp\",\"description\": \"Does amazing foos with your data\",\"url\": \"http://example.com\",\n\"icon\": \"http://example.com/icon.png\",\"redirect_uris\": [\"https://app.example.com/tent/callback\"],\"scopes\": {\"write_profile\": \"Uses an app profile section to describe foos\",\"read_followings\": \"Calculates foos based on your followings\"},\"id\": \"fbh9mv\",\"mac_key_id\": \"a:960fedee\",\"mac_key\": \"f7ef29fd0b7ec539f3f7f404aee0a866\",\"mac_algorithm\": \"hmac-sha-256\",\"authorizations\": []}"));
    
    tentClient.register(new RegistrationRequest("unit_test", "description", "test_url", new String[] {}, new HashMap<String, String>())).get();
  }

  private String profileUrl() {
    return server.getBaseUrl() + "/tent/profile";
  }
  
  private String profileJson() {
    return "{\"https://tent.io/types/info/basic/v0.1.0\":{\"name\":\"Mwanji Ezana\",\"avatar_url\":\"http://www.gravatar.com/avatar/ae8715093d8d4219507146ed34f0ed16.png\",\"birthdate\":\"\",\"location\":\"\",\"gender\":\"M\",\"bio\":\"\",\"permissions\":{\"public\":true}},\"https://tent.io/types/info/core/v0.1.0\":{\"entity\":\"" + server.getBaseUrl() + "\",\"licenses\":[],\"servers\":[\"https://mwanji.tent.is/tent\"],\"permissions\":{\"public\":true}}}";
  }
  
  private TentClientAsync tentClient() {
    return new TentClientAsync(server.getBaseUrl());
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
