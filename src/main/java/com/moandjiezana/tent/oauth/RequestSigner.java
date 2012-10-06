package com.moandjiezana.tent.oauth;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;

public class RequestSigner {

  private static final Joiner NEW_LINES = Joiner.on("\n");

  public static String generateAuthorizationHeader(long timestamp, String nonce, String httpMethod, String uri, String host, int port, String macKey, String macKeyId, String algorithm) {
    if (host.startsWith("http://")) {
      host = host.replace("http://", "");
    } else if (host.startsWith("https://")) {
      host = host.replace("https://", "");
    }
    
    String normalizedRequest = normalizeRequest(timestamp, nonce, httpMethod, uri, host, port);
    SecretKeySpec spec = new SecretKeySpec(macKey.getBytes(Charsets.UTF_8), algorithm);
    try {
      Mac hmacSha256 = Mac.getInstance(algorithm);
      hmacSha256.init(spec);
      byte[] macBytes = hmacSha256.doFinal(normalizedRequest.getBytes(Charsets.UTF_8));

      String mac = Base64.encodeBase64String(macBytes);
      
      return "MAC id=\"" + macKeyId + "\", ts=\"" + timestamp + "\", nonce=\"" + nonce + "\", mac=\"" + mac + "\""; 
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  private static String normalizeRequest(long timestamp, String nonce, String httpMethod, String uri, String host, int port) {
    return NEW_LINES.join(timestamp, nonce, httpMethod.toUpperCase(), uri, host, port, "", "");
  }
}
