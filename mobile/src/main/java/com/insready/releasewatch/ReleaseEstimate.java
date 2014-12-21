package com.insready.releasewatch;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class ReleaseEstimate {

  @Expose
  private String version;
  @Expose
  private String modified;
  @Expose
  private Integer limit;
  @Expose
  private List<Datum> data = new ArrayList<Datum>();

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
   * @return The limit
   */
  public Integer getLimit() {
    return limit;
  }

  /**
   * @param limit The limit
   */
  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  /**
   * @return The data
   */
  public List<Datum> getData() {
    return data;
  }

  /**
   * @param data The data
   */
  public void setData(List<Datum> data) {
    this.data = data;
  }

}