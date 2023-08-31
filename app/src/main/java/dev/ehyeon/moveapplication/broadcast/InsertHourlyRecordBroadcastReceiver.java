package dev.ehyeon.moveapplication.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class InsertHourlyRecordBroadcastReceiver extends BroadcastReceiver {

    private final InsertHourlyRecordBroadcastListener listener;

    public InsertHourlyRecordBroadcastReceiver(InsertHourlyRecordBroadcastListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        listener.onBroadcastReceive();
    }
}
