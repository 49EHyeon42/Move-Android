package dev.ehyeon.moveapplication.ui;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import dev.ehyeon.moveapplication.broadcast.HomeFragmentBroadcastReceiver;
import dev.ehyeon.moveapplication.service.TrackingService;
import dev.ehyeon.moveapplication.service.TrackingServiceAction;
import dev.ehyeon.moveapplication.service.TrackingServiceConnection;

public class HomeFragmentViewModel extends ViewModel {

    private Context context;
    private HomeFragmentBroadcastReceiver broadcastReceiver;
    private TrackingServiceConnection serviceConnection;

    public void onCreateWithContext(@NonNull Context context,
                                    @NonNull HomeFragmentBroadcastReceiver broadcastReceiver,
                                    @NonNull TrackingServiceConnection serviceConnection) {
        this.context = context;
        this.broadcastReceiver = broadcastReceiver;
        this.serviceConnection = serviceConnection;

        registerLocalBroadcastReceiver(context);
        sendBroadcastOfTrackingServiceStatus(context);
    }

    private void registerLocalBroadcastReceiver(Context context) {
        LocalBroadcastManager
                .getInstance(context)
                .registerReceiver(broadcastReceiver, new IntentFilter(TrackingServiceAction.TRACKING_SERVICE_IS_RUNNING.getAction()));
    }

    private void sendBroadcastOfTrackingServiceStatus(Context context) {
        LocalBroadcastManager
                .getInstance(context)
                .sendBroadcast(new Intent(TrackingServiceAction.IS_TRACKING_SERVICE_RUNNING.getAction()));
    }

    private void checkServiceConnectionIsNull() {
        if (serviceConnection == null) {
            throw new NullPointerException("update service connection, server connection is null.");
        }
    }

    public void bindService() {
        checkServiceConnectionIsNull();

        context.bindService(new Intent(context, TrackingService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void unbindService() {
        checkServiceConnectionIsNull();

        context.unbindService(serviceConnection);
    }

    public void disconnectTrackingService() {
        checkServiceConnectionIsNull();

        serviceConnection.disconnectTrackingService();
    }

    public LiveData<TrackingService> getTrackingServiceLiveData() {
        return serviceConnection == null ? new MutableLiveData<>(null) : serviceConnection.getTrackingServiceLiveData();
    }

    public void onDestroyWithContext(@NonNull Context context) {
        this.context = null;

        unregisterLocalBroadcastReceiver(context);
    }

    private void unregisterLocalBroadcastReceiver(Context context) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
    }
}
