package dev.ehyeon.moveapplication.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.lifecycle.MutableLiveData;

public class TrackingServiceConnection implements ServiceConnection {

    private final MutableLiveData<TrackingService> trackingServiceMutableLiveData;

    public TrackingServiceConnection() {
        trackingServiceMutableLiveData = new MutableLiveData<>();
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
