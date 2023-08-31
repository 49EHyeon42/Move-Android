package dev.ehyeon.moveapplication.service;

public enum TrackingServiceAction {

    IS_TRACKING_SERVICE_RUNNING("dev.ehyeon.moveapplication.service.ACTION_IS_TRACKING_SERVICE_RUNNING"),
    TRACKING_SERVICE_IS_RUNNING("dev.ehyeon.moveapplication.service.ACTION_TRACKING_SERVICE_IS_RUNNING"),
    INSERT_HOURLY_RECORD("dev.ehyeon.moveapplication.service.ACTION_INSERT_HOURLY_RECORD");

    private final String action;

    TrackingServiceAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }
}
