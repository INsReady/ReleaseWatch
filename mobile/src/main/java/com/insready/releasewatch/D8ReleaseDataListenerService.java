package com.insready.releasewatch;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by skyred on 12/20/14.
 */
public class D8ReleaseDataListenerService extends WearableListenerService {

  private GoogleApiClient mGoogleApiClient;
  private String mPeerId;
  private String GET_D8_RELEASE_DATA = "drupal8";
  private static final String TAG = "D8ListenerService";

  @Override
  public void onMessageReceived(MessageEvent messageEvent) {
    if (messageEvent.getPath().equals(GET_D8_RELEASE_DATA)) {

      byte[] rawData = messageEvent.getData();
      DataMap dateTimeRequested = DataMap.fromByteArray(rawData);

      Log.d(TAG, "Received watch face request message: " + dateTimeRequested);
    }
  }

}
