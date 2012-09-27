package com.moandjiezana.tent.oauth;

import com.ning.http.client.Request;

import oauth.signpost.AbstractOAuthConsumer;
import oauth.signpost.http.HttpRequest;

public class AsyncHttpClientOAuthConsumer extends AbstractOAuthConsumer {

  public AsyncHttpClientOAuthConsumer(String consumerKey, String consumerSecret) {
    super(consumerKey, consumerSecret);
  }

  @Override
  protected HttpRequest wrap(Object request) {
    if (!(request instanceof Request)) {
      throw new IllegalArgumentException("The AsyncHttpClientOAuthConsumer expects requests of type com.ning.http.client.Request");
    }
    
    return new AsyncHttpClientRequestAdapter((Request) request);
  }
}
