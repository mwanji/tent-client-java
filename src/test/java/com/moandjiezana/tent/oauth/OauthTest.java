package com.moandjiezana.tent.oauth;

import org.junit.Test;

public class OauthTest {

  private static final String MAC_KEY = "489dks293j39";
  private static final String MAC_KEY_ID = "h480djs93hd8";
  private static final long TIMESTAMP = 1336363200;
  private static final String NONCE = "dj83hs9s";

  @Test
  public void should_generate_authorization_header_with_sha256() {
    String macKeyId = MAC_KEY_ID;
    String macKey = MAC_KEY;
    
//    String header = RequestSigner.generateAuthorizationHeader(TIMESTAMP, NONCE, "POST", "/resource/1?b=1&a=2", "example.com", 80, macKey, macKeyId, "HmacSHA256");
//    
//    assertEquals("MAC id=\"h480djs93hd8\", ts=\"1336363200\", nonce=\"dj83hs9s\", mac=\"Xt51rtHY5F+jxKXMCoiKgXa3geofWW/7RANCXB1yu08=\"", header);
  }

  @Test
  public void should_strip_scheme_from_host() {
//    String header = RequestSigner.generateAuthorizationHeader(TIMESTAMP, NONCE, "POST", "/resource/1?b=1&a=2", "https://example.com", 80, MAC_KEY, MAC_KEY_ID, "HmacSHA256");
//    
//    assertEquals("MAC id=\"h480djs93hd8\", ts=\"1336363200\", nonce=\"dj83hs9s\", mac=\"Xt51rtHY5F+jxKXMCoiKgXa3geofWW/7RANCXB1yu08=\"", header);
  }
  
  @Test
  public void should_sign_a_get_with_no_body() {
//    String header = RequestSigner.generateAuthorizationHeader(TIMESTAMP, NONCE, "GET", "/resource/1?b=1&a=2", "example.com", 80, MAC_KEY, MAC_KEY_ID, "HmacSHA1");
//    
//    assertEquals("MAC id=\"h480djs93hd8\", ts=\"1336363200\", nonce=\"dj83hs9s\", mac=\"6T3zZzy2Emppni6bzL7kdRxUWL4=\"", header);
  }
  
  @Test
  public void should_sign_a_post_with_no_body() {
//    String header = RequestSigner.generateAuthorizationHeader(TIMESTAMP, NONCE, "POST", "/resource/1?b=1&a=2", "example.com", 80, MAC_KEY, MAC_KEY_ID, "HmacSHA1");
//    
//    assertEquals("MAC id=\"h480djs93hd8\", ts=\"1336363200\", nonce=\"dj83hs9s\", mac=\"SIBz/j9mI1Ba2Y+10wdwbQGv2Yk=\"", header);
  }
}
