package com.insready.releasewatch;

import retrofit.http.GET;

/**
 * Created by skyred on 12/21/14.
 */
public interface DrupalReleaseDateServices {
  @GET("/data/historical-samples.json")
  ProjectStatus projectStatus();

  @GET("/data/estimates.json?limit=1")
  ReleaseEstimate releaseEstimate();
}
