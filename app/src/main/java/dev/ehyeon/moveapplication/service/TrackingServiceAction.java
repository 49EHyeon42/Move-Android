package dev.ehyeon.moveapplication.service;

public enum TrackingServiceAction {

    IS_TRACKING_SERVICE_RUNNING("dev.ehyeon.moveapplication.service.ACTION_IS_TRACKING_SERVICE_RUNNING"),
    TRACKING_SERVICE_IS_RUNNING("dev.ehyeon.moveapplication.service.ACTION_TRACKING_SERVICE_IS_RUNNING");

    private final String action;

    TrackingServiceAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }
}
