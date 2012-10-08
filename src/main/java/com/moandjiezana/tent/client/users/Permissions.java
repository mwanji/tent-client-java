package com.moandjiezana.tent.client.users;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class Permissions {

  @SerializedName("public")
  private boolean _public;
  private Group[] groups;
  private Map<String, Boolean> entities;

  public boolean isPublic() {
    return _public;
  }

  public void setPublic(boolean _public) {
    this._public = _public;
  }

  public Map<String, Boolean> getEntities() {
    return entities;
  }

  public void setEntities(Map<String, Boolean> entities) {
    this.entities = entities;
  }

  public Group[] getGroups() {
    return groups;
  }

  public void setGroups(Group[] groups) {
    this.groups = groups;
  }
}
