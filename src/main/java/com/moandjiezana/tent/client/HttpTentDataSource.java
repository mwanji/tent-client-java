package com.moandjiezana.tent.client;

import com.google.common.base.Charsets;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.moandjiezana.tent.client.apps.App;
import com.moandjiezana.tent.client.apps.AuthorizationRequest;
import com.moandjiezana.tent.client.apps.RegistrationRequest;
import com.moandjiezana.tent.client.apps.RegistrationResponse;
import com.moandjiezana.tent.client.internal.com.google.common.base.Throwables;
import com.moandjiezana.tent.client.posts.Post;
import com.moandjiezana.tent.client.posts.PostQuery;
import com.moandjiezana.tent.client.users.Following;
import com.moandjiezana.tent.client.users.Profile;
import com.moandjiezana.tent.json.ProfileTypeAdapter;
import com.moandjiezana.tent.json.TentGsonExclusionStrategy;
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
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
public class HttpTentDataSource implements TentDataSource {

  private static final String UTF_8 = Charsets.UTF_8.toString();
  private static final RequestSigner REQUEST_SIGNER = new RequestSigner();
  private static final String TENT_REL_PROFILE = "https://tent.io/rels/profile";
  private static final Logger LOGGER = LoggerFactory.getLogger(HttpTentDataSource.class);
  private static final String TENT_MIME_TYPE = "application/vnd.tent.v0+json";
  private static final Gson GSON = new GsonBuilder().setExclusionStrategies(new TentGsonExclusionStrategy()).setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).registerTypeAdapter(Profile.class, new ProfileTypeAdapter()).create();
  private static AsyncHttpClient defaultAsyncHttpClient = new AsyncHttpClient(new JDKAsyncHttpProvider(getDefaultAsyncHttpClientConfigBuilder().build()));

  private final AsyncHttpClient httpClient;

  private final String entityUrl;
  private List<String> profileUrls;
  private Profile profile;
  private AccessToken accessToken;
  private RegistrationResponse registrationResponse;

  public static AsyncHttpClientConfig.Builder getDefaultAsyncHttpClientConfigBuilder() {
    return new AsyncHttpClientConfig.Builder()
      .setFollowRedirects(true)
      .setRequestTimeoutInMs(60000);
  }

  public static void setDefaultAsyncHttpClient(AsyncHttpClient asyncHttpClient) {
    defaultAsyncHttpClient = asyncHttpClient;
  }

  /**
   * @param entityUrl An undiscovered entity.
   */
  public HttpTentDataSource(String entityUrl) {
    this(entityUrl, defaultAsyncHttpClient);
  }

  /**
   * @param profile A previously-discovered entity.
   */
  public HttpTentDataSource(Profile profile) {
    this(profile, defaultAsyncHttpClient);
  }

  public HttpTentDataSource(String entityUrl, AsyncHttpClient httpClient) {
    this.entityUrl = entityUrl;
    this.httpClient = httpClient;
  }

  public HttpTentDataSource(Profile profile, AsyncHttpClient httpClient) {
    this(profile.getCore().getEntity(), httpClient);
    this.profile = profile;
  }

  /* (non-Javadoc)
   * @see com.moandjiezana.tent.client.TentDataSource#discover(java.lang.String)
   */
  @Override
  public Future<List<String>> discover(String method) {
    profile = null;
    profileUrls = null;
    try {
      BoundRequestBuilder request = method.equals("HEAD") ? httpClient.prepareHead(entityUrl) : httpClient.prepareGet(entityUrl);

      return request.addHeader("Accept", TENT_MIME_TYPE).execute(new AsyncCompletionHandler<List<String>>() {
        @Override
        public List<String> onCompleted(Response response) throws Exception {
          if (response.getStatusCode() == 200 || response.getStatusCode() == 204) {
            addProfileUrls(response);

            return profileUrls;
          } else {
            return Collections.emptyList();
          }
        }
      });
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /* (non-Javadoc)
   * @see com.moandjiezana.tent.client.TentDataSource#getProfile()
   */
  @Override
  public Future<Profile> getProfile() {
    return getProfile(false);
  }

  /* (non-Javadoc)
   * @see com.moandjiezana.tent.client.TentDataSource#getProfile(boolean)
   */
  @Override
  public Future<Profile> getProfile(boolean force) {
    if (!force && profile != null) {
      return immediateFuture();
    }

    try {
      return httpClient.prepareGet(profileUrls.get(0)).addHeader("Accept", TENT_MIME_TYPE).addHeader("Content-Type", TENT_MIME_TYPE).execute(new AsyncCompletionHandler<Profile>() {

        @Override
        public Profile onCompleted(Response response) throws Exception {
          String responseBody = response.getResponseBody(UTF_8);
          LOGGER.debug(responseBody);

          profile = GSON.fromJson(responseBody, Profile.class);

          return profile;
        }
      });
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /* (non-Javadoc)
   * @see com.moandjiezana.tent.client.TentDataSource#getFollowings()
   */
  @Override
  public Future<List<Following>> getFollowings() {
    try {
      return httpClient.prepareGet(getServer() + "/followings").addHeader("Accept", TENT_MIME_TYPE).execute(new AsyncCompletionHandler<List<Following>>() {
        @Override
        public List<Following> onCompleted(Response response) throws Exception {
          String responseBody = response.getResponseBody(UTF_8);
          LOGGER.debug(responseBody);

          return GSON.fromJson(responseBody, new TypeToken<List<Following>>() {}.getType());
        }
      });
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /* (non-Javadoc)
   * @see com.moandjiezana.tent.client.TentDataSource#getFollowing(java.lang.String)
   */
  @Override
  public Future<Following> getFollowing(String id) {
    try {
      return httpClient.prepareGet(getServer() + "/followings/" + id).addHeader("Accept", TENT_MIME_TYPE)
          .execute(new AsyncCompletionHandler<Following>() {
            @Override
            public Following onCompleted(Response response) throws Exception {
              String responseBody = response.getResponseBody(UTF_8);
              LOGGER.debug(responseBody);

              return GSON.fromJson(responseBody, Following.class);
            }
          });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /* (non-Javadoc)
   * @see com.moandjiezana.tent.client.TentDataSource#getPosts()
   */
  @Override
  public Future<List<Post>> getPosts() {
    return getPosts(null);
  }

  /* (non-Javadoc)
   * @see com.moandjiezana.tent.client.TentDataSource#getPosts(com.moandjiezana.tent.client.posts.PostQuery)
   */
  @Override
  public Future<List<Post>> getPosts(PostQuery query) {
    try {
      String urlString = getServer() + "/posts";
      URL url = new URL(urlString);

      BoundRequestBuilder requestBuilder = httpClient.prepareGet(urlString).addHeader("Accept", TENT_MIME_TYPE);

      if (query != null) {
        FluentStringsMap queryStringParameters = new FluentStringsMap();

        for (Entry<String, String[]> entry : query.toMap().entrySet()) {
          queryStringParameters.add(entry.getKey(), entry.getValue());
        }

        requestBuilder.setQueryParameters(queryStringParameters);
        url = new URL(urlString + "?" + query);
      }

      if (isAuthorized()) {
        requestBuilder.addHeader("Authorization", REQUEST_SIGNER.generateAuthorizationHeader("GET", url, accessToken));
      }
      return requestBuilder.execute(new AsyncCompletionHandler<List<Post>>() {
        @Override
        public List<Post> onCompleted(Response response) throws Exception {
          String responseBody = response.getResponseBody(UTF_8);
          LOGGER.debug(responseBody);

          if (response.getStatusCode() != 200) {
            return null;
          }

          return GSON.fromJson(responseBody, new TypeToken<List<Post>>() {}.getType());
        }
      });
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /* (non-Javadoc)
   * @see com.moandjiezana.tent.client.TentDataSource#getPost(java.lang.String)
   */
  @Override
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
          String responseBody = response.getResponseBody(UTF_8);
          LOGGER.debug(responseBody);

          return GSON.fromJson(responseBody, Post.class);
        }
      });
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  /* (non-Javadoc)
   * @see com.moandjiezana.tent.client.TentDataSource#write(com.moandjiezana.tent.client.posts.Post)
   */
  @Override
  public Future<Post> write(Post post) {
    try {
      URL url = new URL(getServer() + "/posts");
      LOGGER.debug("WRITE: " + GSON.toJson(post));
      return httpClient.preparePost(getServer() + "/posts")
          .setBodyEncoding(UTF_8)
          .addHeader("Content-Type", TENT_MIME_TYPE)
          .addHeader("Accept", TENT_MIME_TYPE)
          .addHeader("Authorization", REQUEST_SIGNER.generateAuthorizationHeader("POST", url, accessToken))
          .setBody(GSON.toJson(post))
          .execute(new AsyncCompletionHandler<Post>() {
            @Override
            public Post onCompleted(Response response) throws Exception {
              String responseBody = response.getResponseBody(UTF_8);
              LOGGER.debug(response.getStatusCode() + " " + response.getStatusText());
              LOGGER.debug(responseBody);

              return GSON.fromJson(responseBody, Post.class);
            }
          });
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  @Override
  public Future<Post> put(Post post) {
    String urlString = getServer() + "/posts/" + post.getId();
    try {
      return httpClient.preparePut(urlString).addHeader("Content-Type", TENT_MIME_TYPE)
        .addHeader("Accept", TENT_MIME_TYPE)
        .addHeader("Authorization", REQUEST_SIGNER.generateAuthorizationHeader("PUT", new URL(urlString), accessToken))
        .setBody(GSON.toJson(post))
        .execute(new AsyncCompletionHandler<Post>() {
          @Override
          public Post onCompleted(Response response) throws Exception {
            String responseBody = response.getResponseBody();
            LOGGER.debug(responseBody);


            return GSON.fromJson(responseBody, Post.class);
          }
        });
    } catch (Exception e) {
      throw Throwables.propagate(Throwables.getRootCause(e));
    }
  }

  /* (non-Javadoc)
   * @see com.moandjiezana.tent.client.TentDataSource#deletePost(java.lang.String)
   */
  @Override
  public Future<Boolean> deletePost(String id) {
    String urlString = getServer() + "/posts/" + id;
    try {
      return httpClient.prepareDelete(urlString)
        .addHeader("Content-Type", TENT_MIME_TYPE)
        .addHeader("Accept", TENT_MIME_TYPE)
        .addHeader("Authorization", REQUEST_SIGNER.generateAuthorizationHeader("DELETE", new URL(urlString), accessToken))
        .execute(new AsyncCompletionHandler<Boolean>() {
          @Override
          public Boolean onCompleted(Response response) throws Exception {
            return response.getStatusCode() == 200;
          }
        });
    } catch (Exception e) {
      throw Throwables.propagate(Throwables.getRootCause(e));
    }
  }

  /* (non-Javadoc)
   * @see com.moandjiezana.tent.client.TentDataSource#register(com.moandjiezana.tent.client.apps.RegistrationRequest)
   */
  @Override
  public Future<RegistrationResponse> register(RegistrationRequest registrationRequest) {
    try {
      return httpClient.preparePost(getServer() + "/apps").addHeader("Content-Type", TENT_MIME_TYPE).addHeader("Accept", TENT_MIME_TYPE)
          .setBody(GSON.toJson(registrationRequest)).execute(new AsyncCompletionHandler<RegistrationResponse>() {
            @Override
            public RegistrationResponse onCompleted(Response response) throws Exception {
              String responseBody = response.getResponseBody(UTF_8);
              LOGGER.debug(responseBody);
              RegistrationResponse registrationResponse = GSON.fromJson(responseBody, RegistrationResponse.class);
              setRegistrationResponse(registrationResponse);

              return registrationResponse;
            }
          });
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Future<App> getApp() {
    String urlString = getServer() + "/apps/" + registrationResponse.getId();
    try {
      return httpClient.prepareGet(urlString)
          .addHeader("Accept", TENT_MIME_TYPE)
           .addHeader("Authorization", REQUEST_SIGNER.generateAuthorizationHeader("GET", new URL(urlString), registrationResponse))
           .execute(new AsyncCompletionHandler<App>() {
            @Override
            public App onCompleted(Response response) throws Exception {
              if (response.getStatusCode() != 200) {
                LOGGER.debug(response.getResponseBody());
                return null;
              }
              return GSON.fromJson(response.getResponseBody(), App.class);
            }
          });
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /* (non-Javadoc)
   * @see com.moandjiezana.tent.client.TentDataSource#buildAuthorizationUrl(com.moandjiezana.tent.client.apps.AuthorizationRequest)
   */
  @Override
  public String buildAuthorizationUrl(AuthorizationRequest authorizationRequest) {
    return httpClient.prepareGet(getServer() + "/oauth/authorize").addQueryParameter("client_id", registrationResponse.getId())
        .addQueryParameter("redirect_uri", authorizationRequest.getRedirectUri()).addQueryParameter("state", authorizationRequest.getState())
        .addQueryParameter("scope", authorizationRequest.getScope())
        .addQueryParameter("tent_profile_info_types", authorizationRequest.getTentProfileInfoTypes())
        .addQueryParameter("tent_post_types", authorizationRequest.getTentPostTypes())
        .addQueryParameter("tent_notification_url", authorizationRequest.getTentNotificationUrl()).build().getUrl();
  }

  /* (non-Javadoc)
   * @see com.moandjiezana.tent.client.TentDataSource#getAccessToken(java.lang.String)
   */
  @Override
  public Future<AccessToken> getAccessToken(String code) {
    String uri = "/apps/" + registrationResponse.getId() + "/authorizations";
    String urlString = getServer() + uri;

    HashMap<String, String> body = new HashMap<String, String>();
    body.put("code", code);
    body.put("token_type", "mac");

    try {
      URL url = new URL(urlString);
      AccessToken tempToken = new AccessToken();
      tempToken.setAccessToken(registrationResponse.getMacKeyId());
      tempToken.setMacKey(registrationResponse.getMacKey());
      tempToken.setMacAlgorithm(registrationResponse.getMacAlgorithm());
      String authHeader = REQUEST_SIGNER.generateAuthorizationHeader("POST", url, tempToken);

      return httpClient.preparePost(urlString)
        .addHeader("Accept", TENT_MIME_TYPE)
        .addHeader("Content-Type", TENT_MIME_TYPE)
        .addHeader("Authorization", authHeader)
        .setBody(GSON.toJson(body))
        .execute(new AsyncCompletionHandler<AccessToken>() {
          @Override
          public AccessToken onCompleted(Response response) throws Exception {
            String responseBody = response.getResponseBody(UTF_8);

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

  /* (non-Javadoc)
   * @see com.moandjiezana.tent.client.TentDataSource#setAccessToken(com.moandjiezana.tent.oauth.AccessToken)
   */
  @Override
  public void setAccessToken(AccessToken accessToken) {
    this.accessToken = accessToken;
  }

  /* (non-Javadoc)
   * @see com.moandjiezana.tent.client.TentDataSource#setRegistrationResponse(com.moandjiezana.tent.client.apps.RegistrationResponse)
   */
  @Override
  public void setRegistrationResponse(RegistrationResponse registration) {
    this.registrationResponse = registration;
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
      Document document = Jsoup.parse(response.getResponseBody(UTF_8));

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

  private Future<Profile> immediateFuture() {
    return new Future<Profile>() {
      @Override
      public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
      }

      @Override
      public boolean isCancelled() {
        return false;
      }

      @Override
      public boolean isDone() {
        return true;
      }

      @Override
      public Profile get() throws InterruptedException, ExecutionException {
        return profile;
      }

      @Override
      public Profile get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return profile;
      }
    };
  }
}
