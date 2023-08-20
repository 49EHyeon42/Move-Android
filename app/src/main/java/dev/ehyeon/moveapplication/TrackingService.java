package dev.ehyeon.moveapplication;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class TrackingService extends Service {

    private final IBinder binder = new TrackingBinder();

    public class TrackingBinder extends Binder {

        TrackingService getService() {
            return TrackingService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("TAG", "onBind: ");
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
        Log.d("TAG", "onDestroy: ");
    }
}
