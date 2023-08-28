package dev.ehyeon.moveapplication.service;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.LiveData;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import dev.ehyeon.moveapplication.MoveApplication;
import dev.ehyeon.moveapplication.R;
import dev.ehyeon.moveapplication.broadcast.BaseBroadcastListener;
import dev.ehyeon.moveapplication.broadcast.BaseBroadcastReceiver;
import dev.ehyeon.moveapplication.data.local.record.Record;
import dev.ehyeon.moveapplication.data.local.record.RecordDao;
import dev.ehyeon.moveapplication.data.local.step.StepRepository;
import dev.ehyeon.moveapplication.data.local.stopwatch.StopwatchRepository;
import dev.ehyeon.moveapplication.data.remote.location.LocationRepository;

@AndroidEntryPoint
public class TrackingService extends LifecycleService implements BaseBroadcastListener {

    @Inject
    protected RecordDao recordDao;

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
        locationRepository.startLocationUpdate(this);
        stepRepository.startStepUpdate(this);

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

    public LiveData<Float> getTotalTravelDistanceLiveData() {
        return locationRepository.getTotalTravelDistanceLiveData();
    }

    public LiveData<Float> getAverageSpeedLiveData() {
        return locationRepository.getAverageSpeedLiveData();
    }

    public LiveData<Integer> getStepLiveData() {
        return stepRepository.getStepLiveData();
    }

    public LiveData<Float> getKilocalorieConsumptionLiveData() {
        return locationRepository.getKilocalorieConsumptionLiveData();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("TAG", "onUnbind: ");

        new Thread(() -> recordDao.insertRecord(new Record(
                SystemClock.elapsedRealtime(),
                Objects.requireNonNull(stopwatchRepository.getSecondLiveData().getValue()),
                Objects.requireNonNull(locationRepository.getTotalTravelDistanceLiveData().getValue()),
                Objects.requireNonNull(locationRepository.getAverageSpeedLiveData().getValue()),
                Objects.requireNonNull(stepRepository.getStepLiveData().getValue()),
                Objects.requireNonNull(locationRepository.getKilocalorieConsumptionLiveData().getValue())))).start();

        Log.i("QQQ", "onBind: 저장");

        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i("TAG", "onDestroy: ");

        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);

        stopwatchRepository.stopStopwatch();
        locationRepository.stopLocationUpdate(this);
        stepRepository.stopStepUpdate(this);
    }
}
