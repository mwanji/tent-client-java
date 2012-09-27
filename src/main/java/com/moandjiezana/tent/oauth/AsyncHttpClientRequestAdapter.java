package com.moandjiezana.tent.oauth;

import com.ning.http.client.Request;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import oauth.signpost.http.HttpRequest;

public class AsyncHttpClientRequestAdapter implements HttpRequest {
  
  private final Request request;

  public AsyncHttpClientRequestAdapter(Request request) {
    this.request = request;
  }

  @Override
  public String getMethod() {
    return request.getMethod();
  }

  @Override
  public String getRequestUrl() {
    return request.getUrl();
  }

  @Override
  public void setRequestUrl(String url) {
    // can't
  }

  @Override
  public void setHeader(String name, String value) {
    request.getHeaders().add(name, value);
  }

  @Override
  public String getHeader(String name) {
    return request.getHeaders().getFirstValue(name);
  }

  @Override
  public Map<String, String> getAllHeaders() {
    HashMap<String, String> allHeaders = new HashMap<String, String>();
    
    for (Entry<String, List<String>> header : request.getHeaders()) {
      allHeaders.put(header.getKey(), header.getValue().get(0));
    }
    
    return allHeaders;
  }

  @Override
  public InputStream getMessagePayload() throws IOException {
    return null;
  }

  @Override
  public String getContentType() {
    return request.getHeaders().getFirstValue("Content-Type");
  }

  @Override
  public Object unwrap() {
    return request;
  }

}
