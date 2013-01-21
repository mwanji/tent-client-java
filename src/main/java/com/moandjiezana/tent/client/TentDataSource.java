package com.moandjiezana.tent.client;

import com.moandjiezana.tent.client.apps.App;
import com.moandjiezana.tent.client.apps.AuthorizationRequest;
import com.moandjiezana.tent.client.apps.RegistrationRequest;
import com.moandjiezana.tent.client.apps.RegistrationResponse;
import com.moandjiezana.tent.client.posts.Post;
import com.moandjiezana.tent.client.posts.PostQuery;
import com.moandjiezana.tent.client.users.Following;
import com.moandjiezana.tent.client.users.Profile;
import com.moandjiezana.tent.oauth.AccessToken;

import java.util.List;
import java.util.concurrent.Future;

public interface TentDataSource {

  /**
   * Obtains the profile URLs for the given entity. All future method calls use
   * these URLs.
   *
   * @param method
   *          can be HEAD or GET.
   * @return profile URLs, for convenience, as they are also stored internally.
   *         Empty if no profile URLs found.
   */
  Future<List<String>> discover(String method);

  /**
   * Sets the API roots if necessary.
   *
   * @return
   */
  Future<Profile> getProfile();

  Future<Profile> getProfile(boolean force);

  Future<List<Following>> getFollowings();

  Future<Following> getFollowing(String id);

  Future<List<Post>> getPosts();

  Future<List<Post>> getPosts(PostQuery query);

  Future<Post> getPost(String id);

  Future<Post> write(Post post);

  Future<Boolean> deletePost(String id);

  Future<RegistrationResponse> register(RegistrationRequest registrationRequest);

  /**
   * @return An absolute URL that the user can be redirected to to authorise the app.
   */
  String buildAuthorizationUrl(AuthorizationRequest authorizationRequest);

  Future<AccessToken> getAccessToken(String code);

  void setAccessToken(AccessToken accessToken);

  void setRegistrationResponse(RegistrationResponse registration);

  Future<Post> put(Post post);

  Future<App> getApp();

}