package com.moandjiezana.tent.client;

import com.moandjiezana.tent.client.apps.RegistrationRequest;
import com.moandjiezana.tent.client.users.Following;
import com.moandjiezana.tent.client.users.Profile;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 * As this class is stateful, each instance should only be used for one entity at a time.
 */
public class TentClient {
  
  private static final String TENT_MIME_TYPE = "application/vnd.tent.v0+json";
  private final AsyncHttpClient httpClient = new AsyncHttpClient();
  private final ObjectMapper objectMapper = new ObjectMapper();

  private String[] servers;
  private List<String> profileUrls;
  
  /**
   * Use the default constructor only to discover an entity.
   */
  public TentClient() {}
  
  /**
   * Obtains the profile URLs and API roots for the given entity.
   * All future method calls use these URLs.
   * @param entityUrl
   */
  public void discover(String entityUrl) {
    try {
      Response response = httpClient.prepareHead(entityUrl).addHeader("Accept", TENT_MIME_TYPE).execute().get();
      addProfileUrls(response);
      
      Profile profile = getProfile();
      addServers(profile.getCore());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  public Profile getProfile() {
    try {
      Response response = httpClient.prepareGet(profileUrls.get(0)).addHeader("Accept", TENT_MIME_TYPE).execute().get();
      
      String responseBody = response.getResponseBody();
      JsonNode json = objectMapper.readValue(responseBody, JsonNode.class);
      
      Profile profile = new Profile();
      
      if (json.has(Profile.Core.URI)) {
        profile.setCore(objectMapper.readValue(json.get(Profile.Core.URI), Profile.Core.class));
      }
      
      if (json.has(Profile.Basic.URI)) {
        profile.setBasic(objectMapper.readValue(json.get(Profile.Basic.URI), Profile.Basic.class));
      }
      
      return profile;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  public List<Following> getFollowings() {
    try {
      Response response = httpClient.prepareGet(servers[0] + "/followings").addHeader("Accept", TENT_MIME_TYPE).execute().get();
      return objectMapper.readValue(response.getResponseBody(), new TypeReference<List<Following>>() {});
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  public void register(RegistrationRequest registrationRequest) {
    StringWriter jsonWriter = new StringWriter();
    try {
      new ObjectMapper().writeValue(jsonWriter, registrationRequest);
      
      httpClient.preparePost(servers[0] + "/apps")
        .addHeader("Content-Type", TENT_MIME_TYPE)
        .addHeader("Accept", TENT_MIME_TYPE)
        .setBody(jsonWriter.toString())
        .execute();
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
