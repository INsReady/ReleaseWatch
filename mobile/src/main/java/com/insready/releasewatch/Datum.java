package com.insready.releasewatch;

import com.google.gson.annotations.Expose;

public class Datum {

  @Expose
  private String when;
  @Expose
  private String estimate;

  /**
   * @return The when
   */
  public String getWhen() {
    return when;
  }

  /**
   * @param when The when
   */
  public void setWhen(String when) {
    this.when = when;
  }

  /**
   * @return The estimate
   */
  public String getEstimate() {
    return estimate;
  }

  /**
   * @param estimate The estimate
   */
  public void setEstimate(String estimate) {
    this.estimate = estimate;
  }

}