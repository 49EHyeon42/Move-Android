package dev.ehyeon.moveapplication.broadcast;

import android.content.Context;
import android.content.Intent;

import dev.ehyeon.moveapplication.service.TrackingServiceAction;

public class TrackingServiceBroadcastReceiver extends android.content.BroadcastReceiver {

    private final TrackingServiceBroadcastListener listener;

    public TrackingServiceBroadcastReceiver(TrackingServiceBroadcastListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(TrackingServiceAction.IS_TRACKING_SERVICE_RUNNING.getAction())) {
            listener.onBroadcastReceive();
        }
    }
}
