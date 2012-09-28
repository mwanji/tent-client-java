package com.moandjiezana.tent.http;

import com.ning.http.client.AsyncHandler;
import com.ning.http.client.HttpResponseBodyPart;
import com.ning.http.client.HttpResponseHeaders;
import com.ning.http.client.HttpResponseStatus;
import com.ning.http.client.Response;

public abstract class SimpleAsyncHandler<T> implements AsyncHandler<T> {
  private final Response.ResponseBuilder builder = new Response.ResponseBuilder();
  
  @Override
  public void onThrowable(Throwable t) {
    throw new RuntimeException(t);
  }

  @Override
  public com.ning.http.client.AsyncHandler.STATE onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
    builder.accumulate(bodyPart);
    return STATE.CONTINUE;
  }

  @Override
  public com.ning.http.client.AsyncHandler.STATE onStatusReceived(HttpResponseStatus responseStatus) throws Exception {
    builder.accumulate(responseStatus);
    return STATE.CONTINUE;
  }

  @Override
  public com.ning.http.client.AsyncHandler.STATE onHeadersReceived(HttpResponseHeaders headers) throws Exception {
    builder.accumulate(headers);
    return STATE.CONTINUE;
  }
  
  @Override
  public final T onCompleted() throws Exception {
    return doOnCompleted(builder.build());
  }
  
  protected abstract T doOnCompleted(Response response) throws Exception;
}
