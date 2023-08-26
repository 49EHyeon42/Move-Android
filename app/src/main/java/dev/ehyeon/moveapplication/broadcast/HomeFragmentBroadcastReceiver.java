package dev.ehyeon.moveapplication.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import javax.inject.Inject;

import dev.ehyeon.moveapplication.service.TrackingServiceAction;

public class HomeFragmentBroadcastReceiver extends BroadcastReceiver {

    private final HomeFragmentBroadcastListener listener;

    @Inject
    public HomeFragmentBroadcastReceiver(HomeFragmentBroadcastListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(TrackingServiceAction.TRACKING_SERVICE_IS_RUNNING.getAction())) {
            listener.onBroadcastReceive();
        }
    }
}
