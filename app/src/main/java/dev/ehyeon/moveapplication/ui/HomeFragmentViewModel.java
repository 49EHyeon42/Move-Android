package dev.ehyeon.moveapplication.ui;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import dev.ehyeon.moveapplication.broadcast.BaseBroadcastListener;
import dev.ehyeon.moveapplication.broadcast.BaseBroadcastReceiver;
import dev.ehyeon.moveapplication.data.local.record.Record;
import dev.ehyeon.moveapplication.data.local.record.RecordDao;
import dev.ehyeon.moveapplication.data.remote.record.RecordService;
import dev.ehyeon.moveapplication.data.remote.record.RegisterRecordRequest;
import dev.ehyeon.moveapplication.service.TrackingService;
import dev.ehyeon.moveapplication.service.TrackingServiceAction;
import dev.ehyeon.moveapplication.service.TrackingServiceConnection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class HomeFragmentViewModel extends ViewModel implements BaseBroadcastListener {

    private static final String TAG = "HomeFragmentViewModel";

    private final BaseBroadcastReceiver broadcastReceiver = new BaseBroadcastReceiver(this);
    private final TrackingServiceConnection serviceConnection = new TrackingServiceConnection();

    private final RecordDao recordDao;

    private Context context;

    private final RecordService recordService;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    String accessToken;

    @Inject
    public HomeFragmentViewModel(RecordDao recordDao, RecordService recordService) {
        this.recordDao = recordDao;
        this.recordService = recordService;
    }

    public void onCreateWithContext(@NonNull Context context) {
        this.context = context;

        registerLocalBroadcastReceiver(context);
        sendBroadcastOfTrackingServiceStatus(context);

        accessToken = context.getSharedPreferences("move", Context.MODE_PRIVATE).getString("access token", "");
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

        recordService.registerRecord(accessToken,
                new RegisterRecordRequest(
                        LocalDateTime.now().format(formatter),
                        serviceConnection.getTrackingServiceLiveData().getValue().getSecondLiveData().getValue(),
                        (int) (serviceConnection.getTrackingServiceLiveData().getValue().getTotalTravelDistanceLiveData().getValue() * 1),
                        (int) (serviceConnection.getTrackingServiceLiveData().getValue().getAverageSpeedLiveData().getValue() * 1),
                        serviceConnection.getTrackingServiceLiveData().getValue().getStepLiveData().getValue(),
                        serviceConnection.getTrackingServiceLiveData().getValue().getCalorieConsumptionLiveData().getValue()
                )).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "저장 완료", Toast.LENGTH_SHORT).show();
                } else {
                    Log.w(TAG, "searchRecordByDate failed, response code is " + response.code());

                    Toast.makeText(context, "잠시 후 다시 시도하십시오.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.w(TAG, t.getMessage());

                Toast.makeText(context, "잠시 후 다시 시도하십시오.", Toast.LENGTH_SHORT).show();
            }
        });

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
