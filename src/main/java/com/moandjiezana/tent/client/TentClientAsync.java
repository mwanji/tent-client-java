package com.moandjiezana.tent.client;

import com.moandjiezana.tent.client.apps.RegistrationRequest;
import com.moandjiezana.tent.client.apps.RegistrationResponse;
import com.moandjiezana.tent.client.posts.Post;
import com.moandjiezana.tent.client.users.Following;
import com.moandjiezana.tent.client.users.Profile;
import com.moandjiezana.tent.http.SimpleAsyncHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * As this class is stateful, each instance should only be used for one entity at a time.
 */
public class TentClientAsync {
  private static final Logger LOGGER = LoggerFactory.getLogger(TentClientAsync.class);
  private static final String TENT_MIME_TYPE = "application/vnd.tent.v0+json";
  
  private final AsyncHttpClient httpClient = new AsyncHttpClient();
  private final ObjectMapper objectMapper = new ObjectMapper();

  private String[] servers;
  private List<String> profileUrls;
  
  /**
   * Use the default constructor only to discover an entity.
   */
  public TentClientAsync() {}
  
  /**
   * Obtains the profile URLs for the given entity.
   * All future method calls use these URLs.
   * @param entityUrl
   * @return 
   */
  public Future<List<String>> discover(String entityUrl) {
    try {
      return httpClient.prepareHead(entityUrl).addHeader("Accept", TENT_MIME_TYPE).execute(new SimpleAsyncHandler<List<String>>() {
        @Override
        protected List<String> doOnCompleted(Response response) throws Exception {
          addProfileUrls(response);
          servers = null;
          
          return profileUrls;
        }
      });
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  /**
   * Sets the API roots if necessary.
   * @return
   */
  public Future<Profile> getProfile() {
    try {
      return httpClient.prepareGet(profileUrls.get(0)).addHeader("Accept", TENT_MIME_TYPE).execute(new SimpleAsyncHandler<Profile>() {
        @Override
        protected Profile doOnCompleted(Response response) throws Exception {
          String responseBody = response.getResponseBody();
          LOGGER.debug(responseBody);
          JsonNode json = objectMapper.readValue(responseBody, JsonNode.class);
          
          Profile profile = new Profile();
          
          if (json.has(Profile.Core.URI)) {
            profile.setCore(objectMapper.readValue(json.get(Profile.Core.URI), Profile.Core.class));
            
            if (servers == null) {
              addServers(profile.getCore());
            }
          }
          
          if (json.has(Profile.Basic.URI)) {
            profile.setBasic(objectMapper.readValue(json.get(Profile.Basic.URI), Profile.Basic.class));
          }
          
          return profile;
        }
      });
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  public Future<List<Following>> getFollowings() {
    try {
      return httpClient.prepareGet(servers[0] + "/followings").addHeader("Accept", TENT_MIME_TYPE).execute(new SimpleAsyncHandler<List<Following>>() {
        @Override
        protected List<Following> doOnCompleted(Response response) throws Exception {
          String responseBody = response.getResponseBody();
          LOGGER.debug(responseBody);
          
          return objectMapper.readValue(responseBody, new TypeReference<List<Following>>() {});
        }
      });
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public Future<Following> getFollowing(Following following) {
    try {
      return httpClient.prepareGet(servers[0] + "/followings/" + following.getId()).addHeader("Accept", TENT_MIME_TYPE).execute(new SimpleAsyncHandler<Following>() {
        @Override
        protected Following doOnCompleted(Response response) throws Exception {
          String responseBody = response.getResponseBody();
          LOGGER.debug(responseBody);
          
          return objectMapper.readValue(responseBody, Following.class);
        }
      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  public Future<List<Post>> getPosts() {
    try {
      return httpClient.prepareGet(servers[0] + "/posts").addHeader("Accept", TENT_MIME_TYPE).execute(new SimpleAsyncHandler<List<Post>>() {
        @Override
        protected List<Post> doOnCompleted(Response response) throws Exception {
          String responseBody = response.getResponseBody();
          LOGGER.debug(responseBody);
          
          return objectMapper.readValue(responseBody, new TypeReference<List<Post>>() {});
        }
      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  public Future<RegistrationResponse> register(RegistrationRequest registrationRequest) {
    StringWriter jsonWriter = new StringWriter();
    try {
      new ObjectMapper().writeValue(jsonWriter, registrationRequest);
      
      return httpClient.preparePost(servers[0] + "/apps")
        .addHeader("Content-Type", TENT_MIME_TYPE)
        .addHeader("Accept", TENT_MIME_TYPE)
        .setBody(jsonWriter.toString())
        .execute(new SimpleAsyncHandler<RegistrationResponse>() {
          @Override
          protected RegistrationResponse doOnCompleted(Response response) throws Exception {
            String responseBody = response.getResponseBody();
            LOGGER.debug(responseBody);
            return objectMapper.readValue(responseBody, RegistrationResponse.class);
          }
        });
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  private void addServers(Profile.Core core) {
    servers = core.getServers();
  }
  
  private void addProfileUrls(Response response) {
    profileUrls = new ArrayList<String>();
    String header = response.getHeader("Link");
    String[] rawLinks = header.split(",");
    
    for (String rawLink : rawLinks) {
      String[] urlAndRel = rawLink.split(";");
      if (urlAndRel.length == 2 && urlAndRel[1].equals(" rel=\"https://tent.io/rels/profile\"")) {
        profileUrls.add(urlAndRel[0].substring(1, urlAndRel[0].length() - 1));
      }
    }
  }
}
