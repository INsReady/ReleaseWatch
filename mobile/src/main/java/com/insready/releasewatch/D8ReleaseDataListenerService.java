package com.insready.releasewatch;

import android.support.wearable.companion.WatchFaceCompanion;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.Date;

import retrofit.RestAdapter;

/**
 * Created by skyred on 12/20/14.
 */
public class D8ReleaseDataListenerService extends WearableListenerService {

  private String GET_D8_RELEASE_DATA = "/host/getD8";
  private String UPDATE_D8_RELEASE_DATA = "/wearable/updateD8";
  private static final String TAG = "D8ListenerService";
  private static DrupalReleaseDateServices D8ReleaseDateAPI;

  @Override
  public void onMessageReceived(MessageEvent messageEvent) {
    if (messageEvent.getPath().equals(GET_D8_RELEASE_DATA)) {

      // Download ProjectStatus Data
      RestAdapter restAdapter = new RestAdapter.Builder()
              .setEndpoint("https://drupalreleasedate.com")
              .setLogLevel(RestAdapter.LogLevel.BASIC)
              .build();
      DrupalReleaseDateServices D8ReleaseDateAPI = restAdapter.create(DrupalReleaseDateServices.class);
      Current currentStatus = D8ReleaseDateAPI.projectStatus().getData().getCurrent();
      Day yesterdayStatus = D8ReleaseDateAPI.projectStatus().getData().getDay();
      int currentCriticalNum = currentStatus.getCriticalBugs() + currentStatus.getCriticalTasks() + currentStatus.getCriticalRtbc();
      int currentMajorNum = currentStatus.getMajorBugs() + currentStatus.getMajorTasks() + currentStatus.getMajorRtbc();
      int yesterdayCriticalNum = yesterdayStatus.getCriticalBugs() + yesterdayStatus.getCriticalTasks() + yesterdayStatus.getCriticalRtbc();
      int yesterdayMajorNum = yesterdayStatus.getMajorBugs() + yesterdayStatus.getMajorTasks() + yesterdayStatus.getMajorRtbc();
      String estimate = D8ReleaseDateAPI.releaseEstimate().getData().get(0).getEstimate();

      // Send the data to Wearable
      GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
              .addApi(Wearable.API)
              .build();
      googleApiClient.connect();

      PutDataMapRequest dataMap = PutDataMapRequest.create(UPDATE_D8_RELEASE_DATA);
      dataMap.getDataMap().putInt("CurrentCritical", currentCriticalNum);
      dataMap.getDataMap().putInt("CurrentMajor", currentMajorNum);
      dataMap.getDataMap().putInt("fromYesterday", currentCriticalNum - yesterdayCriticalNum);
      dataMap.getDataMap().putInt("YesterdayMajor", currentMajorNum - yesterdayMajorNum);
      dataMap.getDataMap().putString("Estimate", estimate);
      dataMap.getDataMap().putLong("Timestamp", new Date().getTime());
      PutDataRequest request = dataMap.asPutDataRequest();
      DataApi.DataItemResult result = Wearable.DataApi
              .putDataItem(googleApiClient, request).await();

      if (result.getStatus().isSuccess()) {
        Log.d(TAG, "putDataItem result status: " + result.getStatus());
        Log.d(TAG, "Data item set: " + result.getDataItem().getUri());
      } else {
        Log.d(TAG, "Project Status Current Criticals: " + currentCriticalNum);
        Log.d(TAG, "D8 Release Date Estimate: " + estimate);
      }
    }
  }

}
