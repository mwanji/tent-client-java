package com.moandjiezana.tent.client;

import static com.github.restdriver.clientdriver.RestClientDriver.giveEmptyResponse;
import static com.github.restdriver.clientdriver.RestClientDriver.giveResponse;
import static com.github.restdriver.clientdriver.RestClientDriver.onRequestTo;
import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.restdriver.clientdriver.ClientDriverRequest.Method;
import com.github.restdriver.clientdriver.ClientDriverRule;
import com.moandjiezana.tent.client.apps.AuthorizationRequest;
import com.moandjiezana.tent.client.apps.RegistrationRequest;
import com.moandjiezana.tent.client.apps.RegistrationResponse;
import com.moandjiezana.tent.client.internal.com.google.common.base.Joiner;
import com.moandjiezana.tent.client.posts.Mention;
import com.moandjiezana.tent.client.posts.Post;
import com.moandjiezana.tent.client.posts.content.StatusContent;
import com.moandjiezana.tent.client.users.Permissions;
import com.moandjiezana.tent.client.users.Profile;
import com.moandjiezana.tent.oauth.AccessToken;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
  public void discover() {
    TentClient tentClient = new TentClient("http://moandjiezana.com");
    tentClient.discover();
    Profile profile = tentClient.getProfile();
    
    List<Post> posts = tentClient.getPosts();
    
    printPosts(posts);
  }
  
  @Test @Ignore
  public void discover_redirected_entity() {
    TentClient tentClient = new TentClient("http://longearstestaccount.tumblr.com");
    tentClient.discover();
    Profile profile = tentClient.getProfile();
    
    List<Post> posts = tentClient.getPosts();
    
    printPosts(posts);
  }

  @Test @Ignore
  public void register() throws Exception {
    TentClient tentClient = new TentClient("https://javaapiclient.tent.is");
    tentClient.discover();
    Profile profile = tentClient.getProfile();
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
    
    HashMap<String, String> scopes = new HashMap<String, String>();
    scopes.put("write_profile", "Not really used, just testing");
    
    RegistrationRequest registrationRequest = new RegistrationRequest("TentClient for Java", "Running dev tests", "http://www.moandjiezana.com/tent-client-java", new String [] { "http://www.moandjiezana.com/tent-test/index.php" }, scopes);
    RegistrationResponse registrationResponse = tentClient.register(registrationRequest);
    
    System.out.println("id=" + registrationResponse.getId());
    System.out.println("mac_key_id=" + registrationResponse.getMacKeyId());
    System.out.println("mac_key=" + registrationResponse.getMacKey());
    System.out.println("mac_algorithm=" + registrationResponse.getMacAlgorithm());

    AuthorizationRequest authorizationRequest = new AuthorizationRequest(registrationResponse.getId(), registrationRequest.getRedirectUris()[0]);
    authorizationRequest.setScope("write_profile");
    authorizationRequest.setState("myState");
    authorizationRequest.setTentPostTypes(Post.Types.status("v0.1.0"));
    authorizationRequest.setTentProfileInfoTypes(Profile.Core.URI, Profile.Basic.URI);
    
    String authorizationUrl = tentClient.getAsync().buildAuthorizationUrl(registrationResponse, authorizationRequest);
    System.out.println("Auth URL: " + authorizationUrl);
    Desktop.getDesktop().browse(new URI(authorizationUrl));
  }
  
  @Test @Ignore
  public void post_as_java_client() throws JsonGenerationException, JsonMappingException, IOException, IllegalArgumentException, InterruptedException, ExecutionException {
    long time = System.currentTimeMillis() / 1000;
    
    TentClientAsync tentClient = new TentClientAsync("https://javaapiclient.tent.is");
    tentClient.discover("HEAD").get();
    tentClient.getProfile().get();
    
    AccessToken accessToken = new AccessToken();
    accessToken.setAccessToken("YOUR API ID HERE");
    accessToken.setMacKey("YOUR SECRET HERE");
    accessToken.setMacAlgorithm("hmac-sha-256");
    tentClient.setAccessToken(accessToken);
    
    Post post = new Post();
    post.setPublishedAt(time);
    Permissions permissions = new Permissions();
    permissions.setPublic(true);
    post.setPermissions(permissions);
//    Mention mention = new Mention();
//    mention.setEntity("https://mwanji.tent.is");
//    post.setMentions(new Mention[] { mention });
    post.setLicenses(new String[] { "http://creativecommons.org/licenses/by/3.0/" });
    StatusContent statusContent = new StatusContent();
    statusContent.setText("Is this thing still on?");
    post.setContent(statusContent);
    
    Post returnedPost = tentClient.write(post).get();
    
    System.out.println("post ID=" + returnedPost.getId());
    System.out.println("content=" + returnedPost.getContentAs(StatusContent.class).getText());
  }
  
  @Test @Ignore
  public void auth() throws Exception {
    TentClient tentClient = new TentClient("https://javaapiclient.tent.is/");
    tentClient.discover();
    tentClient.getProfile();
    
    HashMap<String, String> scopes = new HashMap<String, String>();
    scopes.put("write_posts", "Mostly test posts.");
    scopes.put("read_followings", "To see if it works");
    
    RegistrationRequest registrationRequest = new RegistrationRequest("TentClient for Java", "Running dev tests", "http://www.moandjiezana.com/tent-client-java", new String [] { "http://www.moandjiezana.com/tent-test/index.php" }, scopes);
    RegistrationResponse registrationResponse = tentClient.register(registrationRequest);
    
    System.out.println("mac_key=" + registrationResponse.getMacKey());
    System.out.println("mac_key_id=" + registrationResponse.getMacKeyId());
    
    AuthorizationRequest authorizationRequest = new AuthorizationRequest(registrationResponse.getId(), "http://www.moandjiezana.com/tent-test/index.php");
    authorizationRequest.setScope(Joiner.on(',').join(registrationRequest.getScopes().keySet()));
    authorizationRequest.setState("myState");
    authorizationRequest.setTentPostTypes(Post.Types.status("v0.1.0"));
    authorizationRequest.setTentProfileInfoTypes(Profile.Core.URI, Profile.Basic.URI);
    
    String authorizationUrl = tentClient.getAsync().buildAuthorizationUrl(registrationResponse, authorizationRequest);
    System.out.println("Auth URL: " + authorizationUrl);
    Desktop.getDesktop().browse(new URI(authorizationUrl));
    
    System.out.println("Code?");
    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
    String code = bufferRead.readLine();
    
    AccessToken accessToken = tentClient.getAsync().getAccessToken(registrationResponse, code).get();
    
    System.out.println("Access Token");
    System.out.println("access_token=" + accessToken.getAccessToken());
    System.out.println("mac_key=" + accessToken.getMacKey());
  }
  
  @Test
  public void should_discover_profile_url_from_header() throws Exception {
    server.addExpectation(onRequestTo("/").withMethod(Method.HEAD), giveEmptyResponse().withContentType("text/html;charset=utf-8").withHeader("Link", "<" + profileUrl() + ">; rel=\"https://tent.io/rels/profile\""));
    
    List<String> profileUrls = tentClient().discover("HEAD").get();
    
    assertThat(profileUrls).containsOnly(profileUrl());
  }
  
  @Test
  public void should_discover_relative_profile_url_from_header() throws Exception {
    server.addExpectation(onRequestTo("/").withMethod(Method.HEAD), giveEmptyResponse().withContentType("text/html;charset=utf-8").withHeader("Link", "</tent/profile>; rel=\"https://tent.io/rels/profile\""));
    
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
  
  @Test
  public void get_posts_should_accept_null_query() throws Exception {
    Profile profile = new Profile();
    Profile.Core core = new Profile.Core();
    core.setServers(new String[] { server.getBaseUrl() });
    profile.setCore(core);
    
    TentClientAsync tentClient = new TentClientAsync(profile, asList(profileUrl()));
    
    server.addExpectation(onRequestTo("/posts"), giveEmptyResponse());

    tentClient.getPosts(null).get();
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
      if (post.getType().equals(Post.Types.status("v0.1.0"))) {
        StatusContent status = post.getContentAs(StatusContent.class);
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
