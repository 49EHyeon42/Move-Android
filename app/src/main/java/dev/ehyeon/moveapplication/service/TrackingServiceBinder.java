package dev.ehyeon.moveapplication.service;

import android.os.Binder;

public class TrackingServiceBinder extends Binder {

    private final TrackingService trackingService;

    public TrackingServiceBinder(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    public TrackingService getTrackingService() {
        return trackingService;
    }
}
