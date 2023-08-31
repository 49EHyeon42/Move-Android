package dev.ehyeon.moveapplication.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import dev.ehyeon.moveapplication.MoveApplication;
import dev.ehyeon.moveapplication.R;
import dev.ehyeon.moveapplication.broadcast.BaseBroadcastListener;
import dev.ehyeon.moveapplication.broadcast.BaseBroadcastReceiver;
import dev.ehyeon.moveapplication.broadcast.InsertHourlyRecordBroadcastListener;
import dev.ehyeon.moveapplication.broadcast.InsertHourlyRecordBroadcastReceiver;
import dev.ehyeon.moveapplication.data.local.record.HourlyRecord;
import dev.ehyeon.moveapplication.data.local.record.HourlyRecordDao;
import dev.ehyeon.moveapplication.data.local.step.StepRepository;
import dev.ehyeon.moveapplication.data.local.stopwatch.StopwatchRepository;
import dev.ehyeon.moveapplication.data.remote.location.LocationRepository;
import dev.ehyeon.moveapplication.util.NonNullLiveData;

// TODO refactor
@AndroidEntryPoint
public class TrackingService extends LifecycleService implements BaseBroadcastListener, InsertHourlyRecordBroadcastListener {

    @Inject
    protected StopwatchRepository stopwatchRepository;
    @Inject
    protected LocationRepository locationRepository;
    @Inject
    protected StepRepository stepRepository;

    @Inject
    protected HourlyRecordDao hourlyRecordDao;

    private final BaseBroadcastReceiver broadcastReceiver = new BaseBroadcastReceiver(this);

    private final BroadcastReceiver insertHourlyRecordBroadcastReceiver = new InsertHourlyRecordBroadcastReceiver(this);

    private PendingIntent pendingIntent;

    @Override
    public void onCreate() {
        super.onCreate();

        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(broadcastReceiver,
                        new IntentFilter(TrackingServiceAction.IS_TRACKING_SERVICE_RUNNING.getAction()));

        registerReceiver(insertHourlyRecordBroadcastReceiver,
                new IntentFilter(TrackingServiceAction.INSERT_HOURLY_RECORD.getAction()));
    }

    @Override
    public void onBroadcastReceive(Context context, Intent intent) {
        if (intent.getAction().equals(TrackingServiceAction.IS_TRACKING_SERVICE_RUNNING.getAction())) {
            Intent responseIntent = new Intent(TrackingServiceAction.TRACKING_SERVICE_IS_RUNNING.getAction());
            LocalBroadcastManager.getInstance(TrackingService.this).sendBroadcast(responseIntent);
        }
    }

    @Override
    public void onBroadcastReceive() {
        insertOrUpdateHourlyRecord();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        startForeground(1, buildNotification());

        stopwatchRepository.startStopwatch();
        locationRepository.startLocationUpdate(this);
        stepRepository.startStepUpdate(this);

        setAlarmBroadcastReceiver();

        return START_NOT_STICKY;
    }

    private Notification buildNotification() {
        return new Notification.Builder(this, ((MoveApplication) getApplication()).getChannelId())
                .setContentTitle("Test Service")
                .setContentText("Test Service Running")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .build();
    }

    private void setAlarmBroadcastReceiver() {
        AlarmManager alarmManager = getSystemService(AlarmManager.class);

        Intent intent = new Intent(TrackingServiceAction.INSERT_HOURLY_RECORD.getAction());

        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 60 * 1000, 60 * 1000, pendingIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(@NonNull Intent intent) {
        super.onBind(intent);

        return new TrackingServiceBinder(this);
    }

    public NonNullLiveData<Integer> getSecondLiveData() {
        return stopwatchRepository.getSecondLiveData();
    }

    public NonNullLiveData<List<LatLng>> getLatLngListLiveData() {
        return locationRepository.getLatLngListLiveData();
    }

    public NonNullLiveData<Float> getTotalTravelDistanceLiveData() {
        return locationRepository.getTotalTravelDistanceLiveData();
    }

    public NonNullLiveData<Float> getAverageSpeedLiveData() {
        return locationRepository.getAverageSpeedLiveData();
    }

    public NonNullLiveData<Integer> getStepLiveData() {
        return stepRepository.getStepLiveData();
    }

    public NonNullLiveData<Float> getCalorieConsumptionLiveData() {
        return locationRepository.getCalorieConsumptionLiveData();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);

        stopwatchRepository.stopStopwatch();
        locationRepository.stopLocationUpdate(this);
        stepRepository.stopStepUpdate(this);

        insertOrUpdateHourlyRecord();

        getSystemService(AlarmManager.class).cancel(pendingIntent);

        unregisterReceiver(insertHourlyRecordBroadcastReceiver);
    }

    private boolean existsPreviousHourlyRecord;
    private float previousTotalTravelDistance;
    private float previousAverageSpeed;
    private int previousStep;
    private float previousCalorieConsumption;

    private void insertOrUpdateHourlyRecord() {
        long id = truncateToHour(System.currentTimeMillis());

        float totalTravelDistance1 = getTotalTravelDistanceLiveData().getValue();
        float averageSpeed1 = getAverageSpeedLiveData().getValue();
        int step1 = getStepLiveData().getValue();
        float calorieConsumption1 = getCalorieConsumptionLiveData().getValue();

        float totalTravelDistance2 = totalTravelDistance1 - previousTotalTravelDistance;
        float averageSpeed2 = averageSpeed1 - previousAverageSpeed;
        int step2 = step1 - previousStep;
        float calorieConsumption2 = calorieConsumption1 - previousCalorieConsumption;

        Handler handler = new Handler();

        new Thread(() -> {
            boolean result = hourlyRecordDao.existsHourlyRecordById(id);

            handler.post(() -> {
                if (result) {
                    if (existsPreviousHourlyRecord) {
                        new Thread(() -> hourlyRecordDao.updateHourlyRecordById(
                                id,
                                totalTravelDistance2,
                                averageSpeed2,
                                step2,
                                calorieConsumption2)).start();
                    } else {
                        new Thread(() -> hourlyRecordDao.updateHourlyRecordById(
                                id,
                                totalTravelDistance1,
                                averageSpeed1,
                                step1,
                                calorieConsumption1)).start();
                    }
                } else {
                    if (existsPreviousHourlyRecord) {
                        new Thread(() -> hourlyRecordDao.insertHourlyRecord(new HourlyRecord(
                                id,
                                totalTravelDistance2,
                                averageSpeed2,
                                step2,
                                calorieConsumption2))).start();
                    } else {
                        new Thread(() -> hourlyRecordDao.insertHourlyRecord(new HourlyRecord(
                                id,
                                totalTravelDistance1,
                                averageSpeed1,
                                step1,
                                calorieConsumption1))).start();
                    }
                }

                existsPreviousHourlyRecord = true;

                previousTotalTravelDistance = totalTravelDistance1;
                previousAverageSpeed = averageSpeed1;
                previousStep = step1;
                previousCalorieConsumption = calorieConsumption1;
            });
        }).start();
    }

    private long truncateToHour(long milliseconds) {
        return (milliseconds / 3600000) * 3600000;
    }
}
