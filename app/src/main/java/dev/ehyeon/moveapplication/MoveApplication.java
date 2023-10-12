package dev.ehyeon.moveapplication;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class MoveApplication extends Application {

    private String channelId;
    private String visitedMoveStopChannelId;

    @Override
    public void onCreate() {
        super.onCreate();

        channelId = "channelId";
        visitedMoveStopChannelId = "visitedMoveStopChannelId";

        List<NotificationChannel> notificationChannels = new ArrayList<>();

        notificationChannels.add(new NotificationChannel(
                channelId,
                "channelName",
                NotificationManager.IMPORTANCE_DEFAULT
        ));

        NotificationChannel visitedMoveStopChannel = new NotificationChannel(
                visitedMoveStopChannelId,
                "visitedMoveStopChannel",
                NotificationManager.IMPORTANCE_HIGH
        );

        visitedMoveStopChannel.enableVibration(true);

        notificationChannels.add(visitedMoveStopChannel);

        getSystemService(NotificationManager.class).createNotificationChannels(notificationChannels);
    }

    public String getChannelId() {
        return channelId;
    }

    public String getVisitedMoveStopChannelId() {
        return visitedMoveStopChannelId;
    }
}
