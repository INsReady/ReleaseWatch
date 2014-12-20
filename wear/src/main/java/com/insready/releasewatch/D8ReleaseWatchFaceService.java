package com.insready.releasewatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.Time;
import android.util.Log;
import android.view.SurfaceHolder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class D8ReleaseWatchFaceService extends CanvasWatchFaceService {
  private static final String TAG = "D8WatchFaceService";

  /**
   * Update rate in milliseconds for interactive mode. We update once a second to advance the
   * second hand.
   */
  private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);
  private String GET_D8_RELEASE_DATA = "drupal8";

  @Override
  public Engine onCreateEngine() {
    return new Engine();
  }

  private class Engine extends CanvasWatchFaceService.Engine implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    static final int MSG_UPDATE_TIME = 0;

    /* graphic objects */
    Bitmap mBackgroundBitmap;
    Bitmap mBackgroundScaledBitmap;
    Paint mHourPaint;
    Paint mMinutePaint;
    Paint mSecondPaint;
    Paint mTickPaint;

    boolean mMute;

    /* a time object */
    Time mTime;

    /**
     * Whether the display supports fewer bits for each color in ambient mode. When true, we
     * disable anti-aliasing in ambient mode.
     */
    boolean mLowBitAmbient;

    boolean mRegisteredTimeZoneReceiver = false;

    static final int MSG_LOAD_MEETINGS = 0;

    int mNumMeetings;

    GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(D8ReleaseWatchFaceService.this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
                    // Request access only to the Wearable API
            .addApi(Wearable.API)
            .build();

    @Override
    public void onConnected(Bundle bundle) {
      Log.d(TAG, "onConnected: " + bundle);
      // Now you can use the Data Layer API
    }

    @Override
    public void onConnectionSuspended(int i) {
      Log.d(TAG, "onConnectionSuspended: " + i);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
      Log.d(TAG, "onConnectionFailed: " + connectionResult);
    }

    /**
     * Handler to update the time once a second in interactive mode.
     */
    final Handler mUpdateTimeHandler = new Handler() {
      @Override
      public void handleMessage(Message message) {
        switch (message.what) {
          case MSG_UPDATE_TIME:
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
              Log.v(TAG, "updating time");
            }
            invalidate();
            if (shouldTimerBeRunning()) {
              long timeMs = System.currentTimeMillis();
              long delayMs = INTERACTIVE_UPDATE_RATE_MS
                      - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
              mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
            break;
        }
      }
    };

    private AsyncTask<Void, Void, Integer> mLoadMeetingsTask;

    /**
     * Handler to load the meetings once a minute in interactive mode.
     */
    final Handler mLoadMeetingsHandler = new Handler() {
      @Override
      public void handleMessage(Message message) {
        switch (message.what) {
          case MSG_LOAD_MEETINGS:
            cancelLoadMeetingTask();
            mLoadMeetingsTask = new LoadD8ReleaseInfoTask();
            mLoadMeetingsTask.execute();
            // the connected device to send the message to
            PendingResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient);

            nodes.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
              @Override
              public void onResult(final NodeApi.GetConnectedNodesResult result) {
                if (result.getStatus().isSuccess()) {
                  Log.d(TAG, "Connected Nodes: " + result.getNodes().size());
                }
                for (Node node : result.getNodes()) {
                  String msg = "Testing";
                  if (mGoogleApiClient == null) {
                    Log.d(TAG, "mGoogleApiClient initialization failed");
                  } else {
                    Log.d(TAG, "mGoogleApiClient initialization succeeded");
                  }

                  PendingResult msgResult = Wearable.MessageApi.sendMessage(
                          mGoogleApiClient, node.getId(), GET_D8_RELEASE_DATA, msg.getBytes());
                  msgResult.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(final MessageApi.SendMessageResult feedback) {
                      if (!feedback.getStatus().isSuccess()) {
                        Log.e(TAG, "ERROR: failed to send Message: " + feedback.getStatus());
                      }
                    }
                  });
                }
              }
            });

            break;
        }
      }
    };

    /* receiver to update the time zone */
    final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        mTime.clear(intent.getStringExtra("time-zone"));
        mTime.setToNow();
      }
    };

    @Override
    public void onCreate(SurfaceHolder holder) {
      if (Log.isLoggable(TAG, Log.DEBUG)) {
        Log.d(TAG, "onCreate");
      }
      super.onCreate(holder);

            /* configure the system UI */
      setWatchFaceStyle(new WatchFaceStyle.Builder(D8ReleaseWatchFaceService.this)
              .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
              .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
              .setShowSystemUiTime(false)
              .build());

            /* load the background image */
      Resources resources = D8ReleaseWatchFaceService.this.getResources();
      Drawable backgroundDrawable = resources.getDrawable(R.drawable.bg);
      mBackgroundBitmap = ((BitmapDrawable) backgroundDrawable).getBitmap();

            /* create graphic styles */
      mHourPaint = new Paint();
      mHourPaint.setARGB(255, 200, 200, 200);
      mHourPaint.setStrokeWidth(5.f);
      mHourPaint.setAntiAlias(true);
      mHourPaint.setStrokeCap(Paint.Cap.ROUND);

      mMinutePaint = new Paint();
      mMinutePaint.setARGB(255, 200, 200, 200);
      mMinutePaint.setStrokeWidth(3.f);
      mMinutePaint.setAntiAlias(true);
      mMinutePaint.setStrokeCap(Paint.Cap.ROUND);

      mSecondPaint = new Paint();
      mSecondPaint.setARGB(255, 255, 0, 0);
      mSecondPaint.setStrokeWidth(2.f);
      mSecondPaint.setAntiAlias(true);
      mSecondPaint.setStrokeCap(Paint.Cap.ROUND);

      mTickPaint = new Paint();
      mTickPaint.setARGB(100, 255, 255, 255);
      mTickPaint.setStrokeWidth(2.f);
      mTickPaint.setAntiAlias(true);

            /* allocate an object to hold the time */
      mTime = new Time();

    }

    @Override
    public void onDestroy() {
      mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
      super.onDestroy();
    }

    @Override
    public void onPropertiesChanged(Bundle properties) {
      super.onPropertiesChanged(properties);
      mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
      if (Log.isLoggable(TAG, Log.DEBUG)) {
        Log.d(TAG, "onPropertiesChanged: low-bit ambient = " + mLowBitAmbient);
      }
    }

    @Override
    public void onTimeTick() {
      super.onTimeTick();
      if (Log.isLoggable(TAG, Log.DEBUG)) {
        Log.d(TAG, "onTimeTick: ambient = " + isInAmbientMode());
      }
      invalidate();
    }

    @Override
    public void onAmbientModeChanged(boolean inAmbientMode) {
      super.onAmbientModeChanged(inAmbientMode);
      if (Log.isLoggable(TAG, Log.DEBUG)) {
        Log.d(TAG, "onAmbientModeChanged: " + inAmbientMode);
      }
      if (mLowBitAmbient) {
        boolean antiAlias = !inAmbientMode;
        mHourPaint.setAntiAlias(antiAlias);
        mMinutePaint.setAntiAlias(antiAlias);
        mSecondPaint.setAntiAlias(antiAlias);
        mTickPaint.setAntiAlias(antiAlias);
      }
      invalidate();

      // Whether the timer should be running depends on whether we're in ambient mode (as well
      // as whether we're visible), so we may need to start or stop the timer.
      updateTimer();
    }

    @Override
    public void onInterruptionFilterChanged(int interruptionFilter) {
      super.onInterruptionFilterChanged(interruptionFilter);
      boolean inMuteMode = (interruptionFilter == WatchFaceService.INTERRUPTION_FILTER_NONE);
      if (mMute != inMuteMode) {
        mMute = inMuteMode;
        mHourPaint.setAlpha(inMuteMode ? 100 : 255);
        mMinutePaint.setAlpha(inMuteMode ? 100 : 255);
        mSecondPaint.setAlpha(inMuteMode ? 80 : 255);
        invalidate();
      }
    }

    @Override
    public void onDraw(Canvas canvas, Rect bounds) {
      // Update the time
      mTime.setToNow();

      int width = bounds.width();
      int height = bounds.height();

      // Draw the background, scaled to fit.
      if (mBackgroundScaledBitmap == null
              || mBackgroundScaledBitmap.getWidth() != width
              || mBackgroundScaledBitmap.getHeight() != height) {
        mBackgroundScaledBitmap = Bitmap.createScaledBitmap(mBackgroundBitmap,
                width, height, true /* filter */);
      }
      canvas.drawBitmap(mBackgroundScaledBitmap, 0, 0, null);

      // Find the center. Ignore the window insets so that, on round watches with a
      // "chin", the watch face is centered on the entire screen, not just the usable
      // portion.
      float centerX = width / 2f;
      float centerY = height / 2f;

      // Draw the ticks.
      float innerTickRadius = centerX - 10;
      float outerTickRadius = centerX;
      for (int tickIndex = 0; tickIndex < 12; tickIndex++) {
        float tickRot = (float) (tickIndex * Math.PI * 2 / 12);
        float innerX = (float) Math.sin(tickRot) * innerTickRadius;
        float innerY = (float) -Math.cos(tickRot) * innerTickRadius;
        float outerX = (float) Math.sin(tickRot) * outerTickRadius;
        float outerY = (float) -Math.cos(tickRot) * outerTickRadius;
        canvas.drawLine(centerX + innerX, centerY + innerY,
                centerX + outerX, centerY + outerY, mTickPaint);
      }

      float secRot = mTime.second / 30f * (float) Math.PI;
      int minutes = mTime.minute;
      float minRot = minutes / 30f * (float) Math.PI;
      float hrRot = ((mTime.hour + (minutes / 60f)) / 6f) * (float) Math.PI;

      float secLength = centerX - 20;
      float minLength = centerX - 40;
      float hrLength = centerX - 80;

      if (!isInAmbientMode()) {
        float secX = (float) Math.sin(secRot) * secLength;
        float secY = (float) -Math.cos(secRot) * secLength;
        canvas.drawLine(centerX, centerY, centerX + secX, centerY + secY, mSecondPaint);
      }

      float minX = (float) Math.sin(minRot) * minLength;
      float minY = (float) -Math.cos(minRot) * minLength;
      canvas.drawLine(centerX, centerY, centerX + minX, centerY + minY, mMinutePaint);

      float hrX = (float) Math.sin(hrRot) * hrLength;
      float hrY = (float) -Math.cos(hrRot) * hrLength;
      canvas.drawLine(centerX, centerY, centerX + hrX, centerY + hrY, mHourPaint);
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
      super.onVisibilityChanged(visible);
      if (Log.isLoggable(TAG, Log.DEBUG)) {
        Log.d(TAG, "onVisibilityChanged: " + visible);
      }

      if (visible) {
        mGoogleApiClient.connect();
        registerReceiver();

        // Update time zone in case it changed while we weren't visible.
        mTime.clear(TimeZone.getDefault().getID());
        mTime.setToNow();
        mLoadMeetingsHandler.sendEmptyMessage(MSG_LOAD_MEETINGS);
      } else {
        unregisterReceiver();
        mLoadMeetingsHandler.removeMessages(MSG_LOAD_MEETINGS);
        cancelLoadMeetingTask();
        // TODO Need to do something to optimize mGoogleApiClient connection
      }

      // Whether the timer should be running depends on whether we're visible (as well as
      // whether we're in ambient mode), so we may need to start or stop the timer.
      updateTimer();
    }

    private void registerReceiver() {
      if (mRegisteredTimeZoneReceiver) {
        return;
      }
      mRegisteredTimeZoneReceiver = true;
      IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
      D8ReleaseWatchFaceService.this.registerReceiver(mTimeZoneReceiver, filter);
    }

    private void unregisterReceiver() {
      if (!mRegisteredTimeZoneReceiver) {
        return;
      }
      mRegisteredTimeZoneReceiver = false;
      D8ReleaseWatchFaceService.this.unregisterReceiver(mTimeZoneReceiver);
    }

    /**
     * Starts the {@link #mUpdateTimeHandler} timer if it should be running and isn't currently
     * or stops it if it shouldn't be running but currently is.
     */
    private void updateTimer() {
      if (Log.isLoggable(TAG, Log.DEBUG)) {
        Log.d(TAG, "updateTimer");
      }
      mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
      if (shouldTimerBeRunning()) {
        mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
      }
    }

    /**
     * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer should
     * only run when we're visible and in interactive mode.
     */
    private boolean shouldTimerBeRunning() {
      return isVisible() && !isInAmbientMode();
    }

    /**
     * Asynchronous task to load the meetings from the content provider and report the number of
     * meetings back via {@link #onMeetingsLoaded}.
     */
    private class LoadD8ReleaseInfoTask extends AsyncTask<Void, Void, Integer> {
      private PowerManager.WakeLock mWakeLock;

      @Override
      protected Integer doInBackground(Void... voids) {
     /*   PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK, "CalendarWatchFaceWakeLock");
        mWakeLock.acquire();

        long begin = System.currentTimeMillis();
        Uri.Builder builder =
                WearableCalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, begin);
        ContentUris.appendId(builder, begin + DateUtils.DAY_IN_MILLIS);
        final Cursor cursor = getContentResolver().query(builder.build(),
                null, null, null, null);
        int numMeetings = cursor.getCount();
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
          Log.v(TAG, "Num meetings: " + numMeetings);
        }
        return numMeetings;*/
        return 0;
      }

      @Override
      protected void onPostExecute(Integer result) {
        /* get the number of meetings and set the next timer tick */
        releaseWakeLock();
        onMeetingsLoaded(result);
      }

      @Override
      protected void onCancelled() {
        releaseWakeLock();
      }

      private void releaseWakeLock() {
        if (mWakeLock != null) {
          mWakeLock.release();
          mWakeLock = null;
        }
      }
    }

    private void onMeetingsLoaded(Integer result) {
      if (result != null) {
        mNumMeetings = result;
        invalidate();
      }
    }

    private void cancelLoadMeetingTask() {
      if (mLoadMeetingsTask != null) {
        mLoadMeetingsTask.cancel(true);
      }
    }

  }

}
