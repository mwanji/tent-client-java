package com.moandjiezana.tent.json;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class TentGsonExclusionStrategy implements ExclusionStrategy {

  @Override
  public boolean shouldSkipField(FieldAttributes f) {
    return f.getAnnotation(ExcludeFromJson.class) != null;
  }

  @Override
  public boolean shouldSkipClass(Class<?> clazz) {
    return false;
  }

}
