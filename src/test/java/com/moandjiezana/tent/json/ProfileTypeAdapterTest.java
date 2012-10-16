package com.moandjiezana.tent.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.moandjiezana.tent.client.users.Profile;

import java.util.HashMap;

import org.junit.Test;

public class ProfileTypeAdapterTest {
  private final Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).registerTypeAdapter(Profile.class, new ProfileTypeAdapter()).create();

  @Test
  public void should_deserialise_core_and_basic() {
    String serverJson = "{\"https://tent.io/types/info/basic/v0.1.0\":{\"name\":\"Mwanji Ezana\",\"avatar_url\":\"http://www.gravatar.com/avatar/ae8715093d8d4219507146ed34f0ed16.png\",\"birthdate\":\"\",\"location\":\"\",\"gender\":\"M\",\"bio\":\"Developer of the Tent Java client: https://github.com/mwanji/tent-client-java\",\"permissions\":{\"public\":true}},\"https://tent.io/types/info/core/v0.1.0\":{\"entity\":\"https://mwanji.tent.is\",\"licenses\":[],\"servers\":[\"https://mwanji.tent.is/tent\"],\"permissions\":{\"public\":true}}}";
    
    
    Profile profile = gson.fromJson(serverJson, Profile.class);
    
    assertEquals("Mwanji Ezana", profile.getBasic().getName());
    assertEquals("https://mwanji.tent.is", profile.getCore().getEntity());
  }
  
  @Test
  public void should_serialise_core_and_basic() {
    HashMap<String, String> permissions = new HashMap<String, String>();
    permissions.put("p1", "true");
    Profile.Core core = new Profile.Core();
    core.setPermissions(permissions);
    Profile.Basic basic = new Profile.Basic();
    basic.setPermissions(permissions);
    
    Profile profile = new Profile();
    profile.setCore(core);
    profile.setBasic(basic);
    
    String json = gson.toJson(profile);
    
    JsonParser parser = new JsonParser();
    JsonObject actual = parser.parse(json).getAsJsonObject();
    
    assertTrue(actual.getAsJsonObject(Profile.Core.URI).getAsJsonObject("permissions").getAsJsonPrimitive("p1").getAsBoolean());
    assertTrue(actual.getAsJsonObject(Profile.Basic.URI).getAsJsonObject("permissions").getAsJsonPrimitive("p1").getAsBoolean());
  }
  
}
