package com.moandjiezana.tent.oauth;

import com.moandjiezana.tent.client.apps.RegistrationResponse;
import com.moandjiezana.tent.client.internal.com.google.common.base.Throwables;
import com.ning.http.util.Base64;
import com.ning.http.util.UTF8UrlEncoder;

import java.net.URL;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class RequestSigner {
  private static final Random RANDOM = new Random();

  public String generateAuthorizationHeader(String httpMethod, URL url, AccessToken accessToken) {
      return generateAuthorizationHeader(httpMethod, url, accessToken.getAccessToken(), accessToken.getMacKey());
  }

  public String generateAuthorizationHeader(String httpMethod, URL url, RegistrationResponse registrationResponse) {
    return generateAuthorizationHeader(httpMethod, url, registrationResponse.getMacKeyId(), registrationResponse.getMacKey());
  }

  protected long generateTs () {
    return (System.currentTimeMillis() / 1000);
  }

  protected String generateNonce () {
    return Long.toHexString(RANDOM.nextLong());
  }

  private static String generateMac (String base, String key) {
    try {
      SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "HmacSHA256");
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(keySpec);

      return Base64.encode(mac.doFinal(base.getBytes()));
    }
    catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  private String generateAuthorizationHeader(String httpMethod, URL url, String clientId, String secret) {
    int port = url.getPort() > -1 ? url.getPort() : url.getDefaultPort();

    String ts = Long.toString(generateTs());
    String nonce = generateNonce();

    StringBuilder sb = new StringBuilder()
      .append(ts).append("\n")
      .append(nonce).append("\n")
      .append(httpMethod).append("\n")
      .append(url.getPath());

    if (url.getQuery() != null) {

      String encodedQuery = UTF8UrlEncoder.encode(url.getQuery()).replaceAll("%3D", "=").replaceAll("%26", "&");
      sb.append("?").append(encodedQuery);
    }

    sb.append("\n")
      .append(url.getHost()).append("\n")
      .append(port).append("\n")
      .append("").append("\n"); // empty ext field;

    String sigBase = sb.toString();
    String mac = generateMac(sigBase, secret);

    String macFormat = "MAC id=\"%s\", ts=\"%s\", nonce=\"%s\", mac=\"%s\"";

    return String.format(macFormat, clientId, ts, nonce, mac);

  }
}
