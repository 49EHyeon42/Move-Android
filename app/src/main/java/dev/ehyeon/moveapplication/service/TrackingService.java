package dev.ehyeon.moveapplication.service;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.LiveData;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import dev.ehyeon.moveapplication.MoveApplication;
import dev.ehyeon.moveapplication.R;
import dev.ehyeon.moveapplication.broadcast.BaseBroadcastListener;
import dev.ehyeon.moveapplication.broadcast.BaseBroadcastReceiver;
import dev.ehyeon.moveapplication.data.remote.location.LocationRepository;
import dev.ehyeon.moveapplication.data.local.step.StepRepository;
import dev.ehyeon.moveapplication.data.local.stopwatch.StopwatchRepository;

@AndroidEntryPoint
public class TrackingService extends LifecycleService implements BaseBroadcastListener {

    @Inject
    protected StopwatchRepository stopwatchRepository;
    @Inject
    protected LocationRepository locationRepository;
    @Inject
    protected StepRepository stepRepository;

    private final BaseBroadcastReceiver broadcastReceiver = new BaseBroadcastReceiver(this);

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("TAG", "onCreate: ");

        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(broadcastReceiver,
                        new IntentFilter(TrackingServiceAction.IS_TRACKING_SERVICE_RUNNING.getAction()));

        locationRepository.initializeContext(this);
        stepRepository.initializeContext(this);
    }

    @Override
    public void onBroadcastReceive(Context context, Intent intent) {
        if (intent.getAction().equals(TrackingServiceAction.IS_TRACKING_SERVICE_RUNNING.getAction())) {
            Intent responseIntent = new Intent(TrackingServiceAction.TRACKING_SERVICE_IS_RUNNING.getAction());
            LocalBroadcastManager.getInstance(TrackingService.this).sendBroadcast(responseIntent);
        }
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.i("TAG", "onStartCommand: ");

        startForeground(1, buildNotification());

        stopwatchRepository.startStopwatch();
        locationRepository.startLocationSensor();
        stepRepository.startStepSensor();

        return START_NOT_STICKY;
    }

    private Notification buildNotification() {
        return new Notification.Builder(this, ((MoveApplication) getApplication()).getChannelId())
                .setContentTitle("Test Service")
                .setContentText("Test Service Running")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .build();
    }

    @Nullable
    @Override
    public IBinder onBind(@NonNull Intent intent) {
        super.onBind(intent);

        return new TrackingServiceBinder(this);
    }

    public LiveData<Integer> getSecondLiveData() {
        return stopwatchRepository.getSecondLiveData();
    }

    public LiveData<List<LatLng>> getLatLngListLiveData() {
        return locationRepository.getLatLngListLiveData();
    }

    public LiveData<Float> getTotalDistanceLiveData() {
        return locationRepository.getTotalDistanceLiveData();
    }

    public LiveData<Float> getAverageSpeedMutableLiveData() {
        return locationRepository.getAverageSpeedMutableLiveData();
    }

    public LiveData<Integer> getStepLiveData() {
        return stepRepository.getStepLiveData();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("TAG", "onUnbind: ");

        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i("TAG", "onDestroy: ");

        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);

        stopwatchRepository.stopStopwatch();
        locationRepository.stopLocationSensor();
        stepRepository.stopStepSensor();
    }
}
