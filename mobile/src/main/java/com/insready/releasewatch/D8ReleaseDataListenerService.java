package com.insready.releasewatch;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import retrofit.RestAdapter;

/**
 * Created by skyred on 12/20/14.
 */
public class D8ReleaseDataListenerService extends WearableListenerService {

  private GoogleApiClient mGoogleApiClient;
  private String mPeerId;
  private String GET_D8_RELEASE_DATA = "drupal8";
  private static final String TAG = "D8ListenerService";
  private static DrupalReleaseDateServices D8ReleaseDateAPI;

  @Override
  public void onMessageReceived(MessageEvent messageEvent) {
    if (messageEvent.getPath().equals(GET_D8_RELEASE_DATA)) {

      byte[] rawData = messageEvent.getData();
      // DataMap dateTimeRequested = DataMap.fromByteArray(rawData);

      Log.d(TAG, "Received watch face request message: " + new String(rawData));

      // Download ProjectStatus Data
      RestAdapter restAdapter = new RestAdapter.Builder()
              .setEndpoint("https://drupalreleasedate.com")
              .setLogLevel(RestAdapter.LogLevel.FULL)
              .build();
      DrupalReleaseDateServices D8ReleaseDateAPI = restAdapter.create(DrupalReleaseDateServices.class);
      int bugs = D8ReleaseDateAPI.projectStatus().getData().getCurrent().getCriticalBugs();
      Log.d(TAG, "Project Status Critical Bugs: " + bugs);
      String estimate = D8ReleaseDateAPI.releaseEstimate().getData().get(0).getEstimate();
      Log.d(TAG, "D8 Release Date Estimate: " + estimate);
    }
  }

}
