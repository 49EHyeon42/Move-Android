package dev.ehyeon.moveapplication.ui;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import dev.ehyeon.moveapplication.broadcast.HomeFragmentBroadcastListener;
import dev.ehyeon.moveapplication.broadcast.HomeFragmentBroadcastReceiver;
import dev.ehyeon.moveapplication.service.TrackingService;
import dev.ehyeon.moveapplication.service.TrackingServiceAction;
import dev.ehyeon.moveapplication.service.TrackingServiceConnection;

public class HomeFragmentViewModel extends ViewModel implements HomeFragmentBroadcastListener {

    private Context context;
    private HomeFragmentBroadcastReceiver broadcastReceiver;
    private TrackingServiceConnection serviceConnection;

    public void onCreateWithContext(@NonNull Context context) {
        this.context = context;
        // TODO refactor
        this.broadcastReceiver = new HomeFragmentBroadcastReceiver(this);
        this.serviceConnection = new TrackingServiceConnection();

        registerLocalBroadcastReceiver(context);
        sendBroadcastOfTrackingServiceStatus(context);
    }

    private void checkBroadcastReceiverIsNull() {
        if (broadcastReceiver == null) {
            throw new NullPointerException("update broadcast receiver, broadcast receiver is null.");
        }
    }

    private void checkServiceConnectionIsNull() {
        if (serviceConnection == null) {
            throw new NullPointerException("update service connection, server connection is null.");
        }
    }

    private void registerLocalBroadcastReceiver(Context context) {
        checkBroadcastReceiverIsNull();

        LocalBroadcastManager
                .getInstance(context)
                .registerReceiver(broadcastReceiver, new IntentFilter(TrackingServiceAction.TRACKING_SERVICE_IS_RUNNING.getAction()));
    }

    private void sendBroadcastOfTrackingServiceStatus(Context context) {
        LocalBroadcastManager
                .getInstance(context)
                .sendBroadcast(new Intent(TrackingServiceAction.IS_TRACKING_SERVICE_RUNNING.getAction()));
    }

    @Override
    public void onBroadcastReceive() {
        bindService();
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
        checkBroadcastReceiverIsNull();

        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
    }
}
