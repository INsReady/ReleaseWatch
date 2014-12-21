package com.insready.releasewatch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

  @Expose
  private Current current;
  @Expose
  private Day day;

  /**
   * @return The current
   */
  public Current getCurrent() {
    return current;
  }

  /**
   * @param current The current
   */
  public void setCurrent(Current current) {
    this.current = current;
  }

  /**
   * @return The day
   */
  public Day getDay() {
    return day;
  }

  /**
   * @param day The day
   */
  public void setDay(Day day) {
    this.day = day;
  }
}