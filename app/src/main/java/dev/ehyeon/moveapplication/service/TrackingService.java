package dev.ehyeon.moveapplication.service;

import android.app.Notification;
import android.content.BroadcastReceiver;
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

import dev.ehyeon.moveapplication.MoveApplication;
import dev.ehyeon.moveapplication.R;
import dev.ehyeon.moveapplication.data.step.StepRepository;

public class TrackingService extends LifecycleService {

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TrackingServiceAction.IS_TRACKING_SERVICE_RUNNING.getAction())) {
                Intent responseIntent = new Intent(TrackingServiceAction.TRACKING_SERVICE_IS_RUNNING.getAction());
                LocalBroadcastManager.getInstance(TrackingService.this).sendBroadcast(responseIntent);
            }
        }
    };

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
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.i("TAG", "onStartCommand: ");

        startForeground(1, buildNotification());

        return START_NOT_STICKY;
    }

    private Notification buildNotification() {
        return new Notification.Builder(this, ((MoveApplication) getApplication()).getChannelId())
                .setContentTitle("Test Service")
                .setContentText("Test Service Running")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .build();
    }

    private StepRepository stepRepository;

    @Nullable
    @Override
    public IBinder onBind(@NonNull Intent intent) {
        super.onBind(intent);

        stepRepository = StepRepository.getInstance();
        stepRepository.initializeContext(this);
        stepRepository.startSensor();

        return new TrackingServiceBinder(this);
    }

    public LiveData<Integer> getStep() {
        return stepRepository.getStep();
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

        stepRepository.stopSensor();
    }
}
