package dev.ehyeon.moveapplication.data.remote.record;

public class SearchTotalRecordResponse {

    private int totalMileage;
    private double totalTravelDistance;
    private double totalStep;

    public int getTotalMileage() {
        return totalMileage;
    }

    public double getTotalTravelDistance() {
        return totalTravelDistance;
    }

    public double getTotalStep() {
        return totalStep;
    }
}
