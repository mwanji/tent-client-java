package com.moandjiezana.tent.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.moandjiezana.tent.client.users.Profile;

import java.lang.reflect.Type;

public class ProfileTypeAdapter implements JsonSerializer<Profile>, JsonDeserializer<Profile> {

  @Override
  public Profile deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    Profile profile = new Profile();
    JsonObject jsonObject = (JsonObject) json;

    if (jsonObject.has(Profile.Core.URI)) {
      profile.setCore((Profile.Core) context.deserialize(jsonObject.get(Profile.Core.URI), Profile.Core.class));
    }

    if (jsonObject.has(Profile.Basic.URI)) {
      profile.setBasic((Profile.Basic) context.deserialize(jsonObject.get(Profile.Basic.URI), Profile.Basic.class));
    }

    return profile;
  }

  @Override
  public JsonElement serialize(Profile profile, Type typeOfSrc, JsonSerializationContext context) {
    return context.serialize(profile.toMap());
  }

}
