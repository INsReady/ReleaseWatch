package com.insready.releasewatch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProjectStatus {

  @Expose
  private String version;
  @Expose
  private String modified;
  @Expose
  private Data data;

  /**
   * @return The version
   */
  public String getVersion() {
    return version;
  }

  /**
   * @param version The version
   */
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * @return The modified
   */
  public String getModified() {
    return modified;
  }

  /**
   * @param modified The modified
   */
  public void setModified(String modified) {
    this.modified = modified;
  }

  /**
   * @return The data
   */
  public Data getData() {
    return data;
  }

  /**
   * @param data The data
   */
  public void setData(Data data) {
    this.data = data;
  }

}
