package com.insready.releasewatch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Day {

  @SerializedName("beta_blockers")
  @Expose
  private Integer betaBlockers;
  @SerializedName("critical_bugs")
  @Expose
  private Integer criticalBugs;
  @SerializedName("critical_rtbc")
  @Expose
  private Integer criticalRtbc;
  @SerializedName("critical_tasks")
  @Expose
  private Integer criticalTasks;
  @SerializedName("major_bugs")
  @Expose
  private Integer majorBugs;
  @SerializedName("major_rtbc")
  @Expose
  private Integer majorRtbc;
  @SerializedName("major_tasks")
  @Expose
  private Integer majorTasks;
  @SerializedName("normal_bugs")
  @Expose
  private Integer normalBugs;
  @SerializedName("normal_tasks")
  @Expose
  private Integer normalTasks;
  @Expose
  private Integer rtbc;

  /**
   * @return The betaBlockers
   */
  public Integer getBetaBlockers() {
    return betaBlockers;
  }

  /**
   * @param betaBlockers The beta_blockers
   */
  public void setBetaBlockers(Integer betaBlockers) {
    this.betaBlockers = betaBlockers;
  }

  /**
   * @return The criticalBugs
   */
  public Integer getCriticalBugs() {
    return criticalBugs;
  }

  /**
   * @param criticalBugs The critical_bugs
   */
  public void setCriticalBugs(Integer criticalBugs) {
    this.criticalBugs = criticalBugs;
  }

  /**
   * @return The criticalRtbc
   */
  public Integer getCriticalRtbc() {
    return criticalRtbc;
  }

  /**
   * @param criticalRtbc The critical_rtbc
   */
  public void setCriticalRtbc(Integer criticalRtbc) {
    this.criticalRtbc = criticalRtbc;
  }

  /**
   * @return The criticalTasks
   */
  public Integer getCriticalTasks() {
    return criticalTasks;
  }

  /**
   * @param criticalTasks The critical_tasks
   */
  public void setCriticalTasks(Integer criticalTasks) {
    this.criticalTasks = criticalTasks;
  }

  /**
   * @return The majorBugs
   */
  public Integer getMajorBugs() {
    return majorBugs;
  }

  /**
   * @param majorBugs The major_bugs
   */
  public void setMajorBugs(Integer majorBugs) {
    this.majorBugs = majorBugs;
  }

  /**
   * @return The majorRtbc
   */
  public Integer getMajorRtbc() {
    return majorRtbc;
  }

  /**
   * @param majorRtbc The major_rtbc
   */
  public void setMajorRtbc(Integer majorRtbc) {
    this.majorRtbc = majorRtbc;
  }

  /**
   * @return The majorTasks
   */
  public Integer getMajorTasks() {
    return majorTasks;
  }

  /**
   * @param majorTasks The major_tasks
   */
  public void setMajorTasks(Integer majorTasks) {
    this.majorTasks = majorTasks;
  }

  /**
   * @return The normalBugs
   */
  public Integer getNormalBugs() {
    return normalBugs;
  }

  /**
   * @param normalBugs The normal_bugs
   */
  public void setNormalBugs(Integer normalBugs) {
    this.normalBugs = normalBugs;
  }

  /**
   * @return The normalTasks
   */
  public Integer getNormalTasks() {
    return normalTasks;
  }

  /**
   * @param normalTasks The normal_tasks
   */
  public void setNormalTasks(Integer normalTasks) {
    this.normalTasks = normalTasks;
  }

  /**
   * @return The rtbc
   */
  public Integer getRtbc() {
    return rtbc;
  }

  /**
   * @param rtbc The rtbc
   */
  public void setRtbc(Integer rtbc) {
    this.rtbc = rtbc;
  }

}