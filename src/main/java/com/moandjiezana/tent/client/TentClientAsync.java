package com.moandjiezana.tent.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Throwables;
import com.google.common.net.HttpHeaders;
import com.moandjiezana.tent.client.apps.AuthorizationRequest;
import com.moandjiezana.tent.client.apps.RegistrationRequest;
import com.moandjiezana.tent.client.apps.RegistrationResponse;
import com.moandjiezana.tent.client.posts.Post;
import com.moandjiezana.tent.client.users.Following;
import com.moandjiezana.tent.client.users.Profile;
import com.moandjiezana.tent.oauth.AccessToken;
import com.moandjiezana.tent.oauth.RequestSigner;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Response;
import com.ning.http.client.providers.jdk.JDKAsyncHttpProvider;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * As this class is stateful, each instance should only be used for one entity
 * at a time.
 */
public class TentClientAsync {
  
  private static final SecureRandom random = new SecureRandom();
  private static final String TENT_REL_PROFILE = "https://tent.io/rels/profile";
  private static final Logger LOGGER = LoggerFactory.getLogger(TentClientAsync.class);
  private static final String TENT_MIME_TYPE = "application/vnd.tent.v0+json";

  private final AsyncHttpClient httpClient;
  private final ObjectMapper objectMapper = new ObjectMapper();

  private String[] servers;
  private List<String> profileUrls;
  private final String entityUrl;

  /**
   * Use the default constructor only to discover an entity.
   */
  public TentClientAsync(String entityUrl) {
    this.entityUrl = entityUrl;
    this.httpClient = new AsyncHttpClient(new JDKAsyncHttpProvider(new AsyncHttpClientConfig.Builder().build()));
  }

  public TentClientAsync(Profile profile, List<String> profileUrls) {
    this(profile.getCore().getEntity());
    servers = profile.getCore().getServers();
    this.profileUrls = profileUrls;
  }

  /**
   * Obtains the profile URLs for the given entity. All future method calls use
   * these URLs.
   * 
   * @param entityUrl
   * @param method
   *          can be HEAD or GET.
   * @return profile URLs, for convenience, as they are also stored internally.
   *         Empty if no profile URLs found.
   */
  public Future<List<String>> discover(String method) {
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
   * 
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
            profile.setCore(objectMapper.convertValue(json.get(Profile.Core.URI), Profile.Core.class));

            if (servers == null) {
              addServers(profile.getCore());
            }
          }

          if (json.has(Profile.Basic.URI)) {
            profile.setBasic(objectMapper.convertValue(json.get(Profile.Basic.URI), Profile.Basic.class));
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
      return httpClient.prepareGet(servers[0] + "/followings/" + following.getId()).addHeader("Accept", TENT_MIME_TYPE)
          .execute(new AsyncCompletionHandler<Following>() {
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

          return objectMapper.readValue(responseBody, new TypeReference<List<Post>>() {
          });
        }
      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Future<RegistrationResponse> register(RegistrationRequest registrationRequest) {
    StringWriter jsonWriter = new StringWriter();
    try {
      objectMapper.writeValue(jsonWriter, registrationRequest);

      return httpClient.preparePost(servers[0] + "/apps").addHeader("Content-Type", TENT_MIME_TYPE).addHeader("Accept", TENT_MIME_TYPE)
          .setBody(jsonWriter.toString()).execute(new AsyncCompletionHandler<RegistrationResponse>() {
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

  public String buildAuthorizationUrl(RegistrationResponse registrationResponse, AuthorizationRequest authorizationRequest) {
    return httpClient.prepareGet(servers[0] + "/oauth/authorize").addQueryParameter("client_id", registrationResponse.getId())
        .addQueryParameter("redirect_uri", authorizationRequest.getRedirectUri()).addQueryParameter("state", authorizationRequest.getState())
        .addQueryParameter("scope", authorizationRequest.getScope())
        .addQueryParameter("tent_profile_info_types", authorizationRequest.getTentProfileInfoTypes())
        .addQueryParameter("tent_post_types", authorizationRequest.getTentPostTypes())
        .addQueryParameter("tent_notification_url", authorizationRequest.getTentNotificationUrl()).build().getUrl();
  }
  
  public Future<AccessToken> getAccessToken(RegistrationResponse registrationResponse, String code) {
    long timestamp = new Date().getTime();
    String uri = "/apps/" + registrationResponse.getId() + "/authorizations";
    String nonce = new BigInteger(40, random).toString(32);
    String urlString = servers[0] + uri;
    
    HashMap<String, String> body = new HashMap<String, String>();
    body.put("code", code);
    body.put("token_type", "mac");
    StringWriter bodyJson = new StringWriter();
    
    try {
      URL url = new URL(urlString);
      String authHeader = RequestSigner.generateAuthorizationHeader(timestamp, nonce, "POST", uri, url.getHost(), url.getDefaultPort(), registrationResponse.getMacKey(), registrationResponse.getMacKeyId(), "HmacSHA256");

      objectMapper.writeValue(bodyJson, ObjectNode.class);
      
      return httpClient.preparePost(urlString)
        .addHeader("Accept", TENT_MIME_TYPE)
        .addHeader(HttpHeaders.CONTENT_TYPE, TENT_MIME_TYPE)
        .setBody(bodyJson.toString())
        .addHeader("Authorization", authHeader)
        .execute(new AsyncCompletionHandler<AccessToken>() {
          @Override
          public AccessToken onCompleted(Response response) throws Exception {
            String responseBody = response.getResponseBody();
            
            LOGGER.debug("TentClientAsync.getAccessToken()");
            LOGGER.debug(Integer.toString(response.getStatusCode()));
            LOGGER.debug(responseBody);
            
            return objectMapper.readValue(responseBody, AccessToken.class);
          }
        });
    } catch (IOException e) {
      throw Throwables.propagate(e);
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
