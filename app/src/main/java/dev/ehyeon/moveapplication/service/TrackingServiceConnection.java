package dev.ehyeon.moveapplication.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;

public class TrackingServiceConnection implements ServiceConnection {

    private final MutableLiveData<TrackingService> trackingServiceMutableLiveData;

    @Inject
    public TrackingServiceConnection() {
        trackingServiceMutableLiveData = new MutableLiveData<>(null);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        trackingServiceMutableLiveData.setValue(((TrackingServiceBinder) service).getTrackingService());
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        trackingServiceMutableLiveData.setValue(null);
    }

    public MutableLiveData<TrackingService> getTrackingServiceLiveData() {
        return trackingServiceMutableLiveData;
    }

    public void disconnectTrackingService() {
        trackingServiceMutableLiveData.setValue(null);
    }
}
