package com.moandjiezana.tent.oauth;

import com.google.common.base.Throwables;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class RequestSigner {

  private static final CharSequence EMPTY_STRING = "";
  private static final CharSequence CARRIAGE_RETURN = "\r\n";

  public static String generateAuthorizationHeader(long timestamp, String nonce, String httpMethod, String uri, String host, int port, String macKey, String macKeyId, String algorithm) {
    if (host.startsWith("http://")) {
      host = host.replace("http://", "");
    } else if (host.startsWith("https://")) {
      host = host.replace("https://", "");
    }
    
    String normalizedRequest = timestamp + "\n" + nonce + "\n" + httpMethod + "\n" + uri + "\n" + host + "\n" + port + "\n\n";
    System.out.println("Normalized request=\n" + normalizedRequest + "ended");
    
    SecretKeySpec spec = new SecretKeySpec(macKey.getBytes(), algorithm);
    try {
      Mac mac = Mac.getInstance(algorithm);
      mac.init(spec);
      byte[] macBytes = mac.doFinal(normalizedRequest.getBytes());

      return "MAC id=\"" + macKeyId + "\", ts=\"" + timestamp + "\", nonce=\"" + nonce + "\", mac=\"" + com.ning.http.util.Base64.encode(macBytes).replace(CARRIAGE_RETURN, EMPTY_STRING) + "\""; 
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }
}
