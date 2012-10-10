package com.moandjiezana.tent.client;

import com.google.common.base.Throwables;
import com.google.common.net.HttpHeaders;
import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.Futures;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.moandjiezana.tent.client.apps.AuthorizationRequest;
import com.moandjiezana.tent.client.apps.RegistrationRequest;
import com.moandjiezana.tent.client.apps.RegistrationResponse;
import com.moandjiezana.tent.client.posts.Post;
import com.moandjiezana.tent.client.posts.PostQuery;
import com.moandjiezana.tent.client.users.Following;
import com.moandjiezana.tent.client.users.Profile;
import com.moandjiezana.tent.oauth.AccessToken;
import com.moandjiezana.tent.oauth.RequestSigner;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.FluentStringsMap;
import com.ning.http.client.Response;
import com.ning.http.client.providers.jdk.JDKAsyncHttpProvider;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
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
  
  private static final RequestSigner REQUEST_SIGNER = new RequestSigner();
  private static final String TENT_REL_PROFILE = "https://tent.io/rels/profile";
  private static final Logger LOGGER = LoggerFactory.getLogger(TentClientAsync.class);
  private static final String TENT_MIME_TYPE = "application/vnd.tent.v0+json";
  private static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

  private final AsyncHttpClient httpClient;

  private final String entityUrl;
  private List<String> profileUrls;
  private Profile profile;
  private AccessToken accessToken;

  /**
   * Use the default constructor only to discover an entity.
   */
  public TentClientAsync(String entityUrl) {
    this.entityUrl = entityUrl;
    this.httpClient = new AsyncHttpClient(new JDKAsyncHttpProvider(new AsyncHttpClientConfig.Builder().setRequestTimeoutInMs(10000).build()));
  }

  public TentClientAsync(Profile profile, List<String> profileUrls) {
    this(profile.getCore().getEntity());
    this.profile = profile;
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
    profile = null;
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
    if (profile != null) {
      return Futures.immediateFuture(profile);
    }
    
    try {
      return httpClient.prepareGet(profileUrls.get(0)).addHeader("Accept", TENT_MIME_TYPE).execute(new AsyncCompletionHandler<Profile>() {

        @Override
        public Profile onCompleted(Response response) throws Exception {
          String responseBody = response.getResponseBody();
          LOGGER.debug(responseBody);
          JsonParser parser = new JsonParser();
          JsonObject json = parser.parse(responseBody).getAsJsonObject();

          profile = new Profile();

          if (json.has(Profile.Core.URI)) {
            profile.setCore(GSON.fromJson(json.get(Profile.Core.URI), Profile.Core.class));
          }

          if (json.has(Profile.Basic.URI)) {
            profile.setBasic(GSON.fromJson(json.get(Profile.Basic.URI), Profile.Basic.class));
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
      return httpClient.prepareGet(getServer() + "/followings").addHeader("Accept", TENT_MIME_TYPE).execute(new AsyncCompletionHandler<List<Following>>() {
        @Override
        public List<Following> onCompleted(Response response) throws Exception {
          String responseBody = response.getResponseBody();
          LOGGER.debug(responseBody);

          return GSON.fromJson(responseBody, new TypeToken<List<Following>>() {}.getType());
        }
      });
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public Future<Following> getFollowing(Following following) {
    try {
      return httpClient.prepareGet(getServer() + "/followings/" + following.getId()).addHeader("Accept", TENT_MIME_TYPE)
          .execute(new AsyncCompletionHandler<Following>() {
            @Override
            public Following onCompleted(Response response) throws Exception {
              String responseBody = response.getResponseBody();
              LOGGER.debug(responseBody);

              return GSON.fromJson(responseBody, Following.class);
            }
          });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Future<List<Post>> getPosts() {
    return getPosts(null);
  }

  public Future<List<Post>> getPosts(PostQuery query) {
    try {
      String urlString = getServer() + "/posts";
      URL url = new URL(urlString);
      
      BoundRequestBuilder requestBuilder = httpClient.prepareGet(urlString).addHeader("Accept", TENT_MIME_TYPE);
      if (isAuthorized()) {
        requestBuilder.addHeader("Authorization", REQUEST_SIGNER.generateAuthorizationHeader("GET", url, accessToken));
      }
      
      if (query != null) {
        FluentStringsMap queryStringParameters = new FluentStringsMap();
        queryStringParameters.add("post_types", query.getPostTypes());
        
        requestBuilder.setQueryParameters(queryStringParameters);
      }
      
      return requestBuilder.execute(new AsyncCompletionHandler<List<Post>>() {
        @Override
        public List<Post> onCompleted(Response response) throws Exception {
          String responseBody = response.getResponseBody();
          LOGGER.debug(responseBody);

          return GSON.fromJson(responseBody, new TypeToken<List<Post>>() {}.getType());
        }
      });
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public Future<Post> getPost(String id) {
    try {
      String urlString = getServer() + "/posts/" + id;
      URL url = new URL(urlString);
      
      BoundRequestBuilder requestBuilder = httpClient.prepareGet(urlString).addHeader("Accept", TENT_MIME_TYPE);
      if (isAuthorized()) {
        requestBuilder.addHeader("Authorization", REQUEST_SIGNER.generateAuthorizationHeader("GET", url, accessToken));
      }
      
      return requestBuilder.execute(new AsyncCompletionHandler<Post>() {
        @Override
        public Post onCompleted(Response response) throws Exception {
          String responseBody = response.getResponseBody();
          LOGGER.debug(responseBody);

          return GSON.fromJson(responseBody, Post.class);
        }
      });
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  public Future<Post> write(Post post) {
    try {
      URL url = new URL(getServer() + "/posts");
      return httpClient.preparePost(getServer() + "/posts")
          .addHeader("Content-Type", TENT_MIME_TYPE)
          .addHeader("Accept", TENT_MIME_TYPE)
          .addHeader("Authorization", REQUEST_SIGNER.generateAuthorizationHeader("POST", url, accessToken))
          .setBody(GSON.toJson(post))
          .execute(new AsyncCompletionHandler<Post>() {
            @Override
            public Post onCompleted(Response response) throws Exception {
              String responseBody = response.getResponseBody();
              LOGGER.debug(response.getStatusCode() + " " + response.getStatusText());
              LOGGER.debug(responseBody);
              
              return GSON.fromJson(responseBody, Post.class);
            }
          });
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  public Future<RegistrationResponse> register(RegistrationRequest registrationRequest) {
    try {

      return httpClient.preparePost(getServer() + "/apps").addHeader("Content-Type", TENT_MIME_TYPE).addHeader("Accept", TENT_MIME_TYPE)
          .setBody(GSON.toJson(registrationRequest)).execute(new AsyncCompletionHandler<RegistrationResponse>() {
            @Override
            public RegistrationResponse onCompleted(Response response) throws Exception {
              String responseBody = response.getResponseBody();
              LOGGER.debug(responseBody);
              return GSON.fromJson(responseBody, RegistrationResponse.class);
            }
          });
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * @return An absolute URL that the user can be redirected to to authorise the app.
   */
  public String buildAuthorizationUrl(RegistrationResponse registrationResponse, AuthorizationRequest authorizationRequest) {
    return httpClient.prepareGet(getServer() + "/oauth/authorize").addQueryParameter("client_id", registrationResponse.getId())
        .addQueryParameter("redirect_uri", authorizationRequest.getRedirectUri()).addQueryParameter("state", authorizationRequest.getState())
        .addQueryParameter("scope", authorizationRequest.getScope())
        .addQueryParameter("tent_profile_info_types", authorizationRequest.getTentProfileInfoTypes())
        .addQueryParameter("tent_post_types", authorizationRequest.getTentPostTypes())
        .addQueryParameter("tent_notification_url", authorizationRequest.getTentNotificationUrl()).build().getUrl();
  }
  
  public Future<AccessToken> getAccessToken(RegistrationResponse registrationResponse, String code) {
    String uri = "/apps/" + registrationResponse.getId() + "/authorizations";
    String urlString = getServer() + uri;
    
    HashMap<String, String> body = new HashMap<String, String>();
    body.put("code", code);
    body.put("token_type", "mac");
    
    try {
      URL url = new URL(urlString);
      AccessToken tempToken = new AccessToken();
      tempToken.setAccessToken(registrationResponse.getId());
      tempToken.setMacKey(registrationResponse.getMacKey());
      tempToken.setMacAlgorithm(registrationResponse.getMacAlgorithm());
      String authHeader = REQUEST_SIGNER.generateAuthorizationHeader("POST", url, tempToken);

      return httpClient.preparePost(urlString)
        .addHeader("Accept", TENT_MIME_TYPE)
        .addHeader(HttpHeaders.CONTENT_TYPE, TENT_MIME_TYPE)
        .addHeader("Authorization", authHeader)
        .setBody(GSON.toJson(body))
        .execute(new AsyncCompletionHandler<AccessToken>() {
          @Override
          public AccessToken onCompleted(Response response) throws Exception {
            String responseBody = response.getResponseBody();
            
            LOGGER.debug("TentClientAsync.getAccessToken()");
            LOGGER.debug(Integer.toString(response.getStatusCode()));
            LOGGER.debug(responseBody);
            
            accessToken = GSON.fromJson(responseBody, AccessToken.class);
            
            return accessToken;
          }
        });
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }
  
  public void setAccessToken(AccessToken accessToken) {
    this.accessToken = accessToken;
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
            String profileUrl = urlAndRel[0].substring(1, urlAndRel[0].length() - 1);
            profileUrls.add(makeAbsoluteUrl(profileUrl));
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
  
  private String makeAbsoluteUrl(String url) {
    if (url.startsWith("http://") || url.startsWith("https://")) {
      return url;
    }
    return entityUrl + url;
  }

  private String getServer() {
    return profile.getCore().getServers()[0];
  }

  private boolean isAuthorized() {
    return accessToken != null;
  }
}
