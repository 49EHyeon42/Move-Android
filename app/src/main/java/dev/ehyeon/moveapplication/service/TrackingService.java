package dev.ehyeon.moveapplication.service;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import dev.ehyeon.moveapplication.MoveApplication;
import dev.ehyeon.moveapplication.R;

public class TrackingService extends Service {

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
    public int onStartCommand(Intent intent, int flags, int startId) {
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new TrackingServiceBinder(this);
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
    }
}
