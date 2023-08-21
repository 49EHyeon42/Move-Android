package dev.ehyeon.moveapplication;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import dev.ehyeon.moveapplication.data.step.StepRepository;

public class TrackingService extends Service {

    private final IBinder binder = new TrackingBinder();

    public class TrackingBinder extends Binder {

        TrackingService getService() {
            return TrackingService.this;
        }
    }

    private final StepRepository stepRepository = StepRepository.getInstance();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("TAG", "onBind: ");

        stepRepository.initializeContext(this);
        stepRepository.startSensor();

        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("TAG", "onUnbind: ");

        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stepRepository.stopSensor();
        stepRepository.initializeContext(null);

        Log.d("TAG", "onDestroy: ");
    }
}
