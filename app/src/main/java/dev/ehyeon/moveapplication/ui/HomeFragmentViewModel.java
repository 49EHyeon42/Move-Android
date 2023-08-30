package dev.ehyeon.moveapplication.ui;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import dev.ehyeon.moveapplication.broadcast.BaseBroadcastListener;
import dev.ehyeon.moveapplication.broadcast.BaseBroadcastReceiver;
import dev.ehyeon.moveapplication.data.local.record.Record;
import dev.ehyeon.moveapplication.data.local.record.RecordDao;
import dev.ehyeon.moveapplication.service.TrackingService;
import dev.ehyeon.moveapplication.service.TrackingServiceAction;
import dev.ehyeon.moveapplication.service.TrackingServiceConnection;

@HiltViewModel
public class HomeFragmentViewModel extends ViewModel implements BaseBroadcastListener {

    private final BaseBroadcastReceiver broadcastReceiver = new BaseBroadcastReceiver(this);
    private final TrackingServiceConnection serviceConnection = new TrackingServiceConnection();

    private final RecordDao recordDao;

    private Context context;

    @Inject
    public HomeFragmentViewModel(RecordDao recordDao) {
        this.recordDao = recordDao;
    }

    public void onCreateWithContext(@NonNull Context context) {
        this.context = context;

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

    @Override
    public void onBroadcastReceive(Context context, Intent intent) {
        if (intent.getAction().equals(TrackingServiceAction.TRACKING_SERVICE_IS_RUNNING.getAction())) {
            bindService();
        }
    }

    public void bindService() {
        context.bindService(new Intent(context, TrackingService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void unbindService() {
        if (serviceConnection.getTrackingServiceLiveData().getValue() != null) {
            context.unbindService(serviceConnection);
        }
    }

    public void disconnectTrackingService() {
        serviceConnection.disconnectTrackingService();
    }

    public void startTrackingService() {
        if (context.startForegroundService(new Intent(context, TrackingService.class)) != null) {
            bindService();
        }
    }

    public void stopTrackingService(Bitmap bitmap) {
        if (serviceConnection.getTrackingServiceLiveData().getValue() == null) {
            return;
        }

        new Thread(() -> recordDao.insertRecord(new Record(
                System.currentTimeMillis(),
                serviceConnection.getTrackingServiceLiveData().getValue().getSecondLiveData().getValue(),
                serviceConnection.getTrackingServiceLiveData().getValue().getTotalTravelDistanceLiveData().getValue(),
                serviceConnection.getTrackingServiceLiveData().getValue().getAverageSpeedLiveData().getValue(),
                serviceConnection.getTrackingServiceLiveData().getValue().getStepLiveData().getValue(),
                serviceConnection.getTrackingServiceLiveData().getValue().getCalorieConsumptionLiveData().getValue(),
                bitmap))).start();

        unbindService();

        if (context.stopService(new Intent(context, TrackingService.class))) {
            disconnectTrackingService();
        }
    }

    public LiveData<TrackingService> getTrackingServiceLiveData() {
        return serviceConnection.getTrackingServiceLiveData();
    }

    public void onDestroyWithContext() {
        unbindService();

        unregisterLocalBroadcastReceiver(context);

        this.context = null;
    }

    private void unregisterLocalBroadcastReceiver(Context context) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
    }
}
