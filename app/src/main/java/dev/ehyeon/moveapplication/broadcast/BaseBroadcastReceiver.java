package dev.ehyeon.moveapplication.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BaseBroadcastReceiver extends BroadcastReceiver {

    private final BaseBroadcastListener listener;

    public BaseBroadcastReceiver(BaseBroadcastListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        listener.onBroadcastReceive(context, intent);
    }
}
