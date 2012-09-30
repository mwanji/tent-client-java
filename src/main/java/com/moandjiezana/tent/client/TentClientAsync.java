package com.moandjiezana.tent.client;

import com.moandjiezana.tent.client.apps.RegistrationRequest;
import com.moandjiezana.tent.client.apps.RegistrationResponse;
import com.moandjiezana.tent.client.posts.Post;
import com.moandjiezana.tent.client.users.Following;
import com.moandjiezana.tent.client.users.Profile;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.ning.http.client.Response;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * As this class is stateful, each instance should only be used for one entity at a time.
 */
public class TentClientAsync {
  private static final String TENT_REL_PROFILE = "https://tent.io/rels/profile";
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
   * @param method can be HEAD or GET. 
   * @return profile URLs, for convenience, as they are also stored internally. Empty if no profile URLs found.
   */
  public Future<List<String>> discover(final String entityUrl, String method) {
    servers = null;
    profileUrls = null;
    try {
      BoundRequestBuilder request = method.equals("HEAD") ? httpClient.prepareHead(entityUrl) : httpClient.prepareGet(entityUrl);
      
      return request.addHeader("Accept", TENT_MIME_TYPE).execute(new AsyncCompletionHandler<List<String>>() {
        @Override
        public List<String> onCompleted(Response response) throws Exception {
          if (response.getStatusCode() == 405) {
            return Collections.emptyList();
          } else {
            addProfileUrls(response);
            
            return profileUrls;
          }
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
      return httpClient.prepareGet(profileUrls.get(0)).addHeader("Accept", TENT_MIME_TYPE).execute(new AsyncCompletionHandler<Profile>() {
        @Override
        public Profile onCompleted(Response response) throws Exception {
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
      return httpClient.prepareGet(servers[0] + "/followings").addHeader("Accept", TENT_MIME_TYPE).execute(new AsyncCompletionHandler<List<Following>>() {
        @Override
        public List<Following> onCompleted(Response response) throws Exception {
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
      return httpClient.prepareGet(servers[0] + "/followings/" + following.getId()).addHeader("Accept", TENT_MIME_TYPE).execute(new AsyncCompletionHandler<Following>() {
        @Override
        public Following onCompleted(Response response) throws Exception {
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
      return httpClient.prepareGet(servers[0] + "/posts").addHeader("Accept", TENT_MIME_TYPE).execute(new AsyncCompletionHandler<List<Post>>() {
        @Override
        public List<Post> onCompleted(Response response) throws Exception {
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
        .execute(new AsyncCompletionHandler<RegistrationResponse>() {
          @Override
          public RegistrationResponse onCompleted(Response response) throws Exception {
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
  
  private void addProfileUrls(Response response) throws IOException {
    profileUrls = new ArrayList<String>();
    List<String> headers = response.getHeaders("Link");
    
    if (headers != null) {
      for (String header : headers) {
        if (!header.contains("rel=\"" + TENT_REL_PROFILE + "\"")) {
          continue;
        }
        
        String[] rawLinks = header.split(",");
        
        for (String rawLink : rawLinks) {
          String[] urlAndRel = rawLink.split(";");
          if (urlAndRel.length == 2) {
            profileUrls.add(urlAndRel[0].substring(1, urlAndRel[0].length() - 1));
          }
        }
      }
    }
    
    if (profileUrls.isEmpty()) {
      Document document = Jsoup.parse(response.getResponseBody());
      
      Elements elements = document.select("head link[rel=" + TENT_REL_PROFILE + "]");
      
      for (Element element : elements) {
        profileUrls.add(element.attr("href"));
      }
    }
  }
}
