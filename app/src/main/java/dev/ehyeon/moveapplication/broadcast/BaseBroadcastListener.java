package dev.ehyeon.moveapplication.broadcast;

import android.content.Context;
import android.content.Intent;

public interface BaseBroadcastListener {

    void onBroadcastReceive(Context context, Intent intent);
}
