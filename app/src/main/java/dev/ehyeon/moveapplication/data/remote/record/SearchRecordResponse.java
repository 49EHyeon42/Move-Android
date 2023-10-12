package dev.ehyeon.moveapplication.data.remote.record;

public class SearchRecordResponse {

    private String savedDate;
    private int elapsedTime;
    private int totalTravelDistance;
    private float averageSpeed;
    private int step;
    private float calorieConsumption;

    public String getSavedDate() {
        return savedDate;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public int getTotalTravelDistance() {
        return totalTravelDistance;
    }

    public float getAverageSpeed() {
        return averageSpeed;
    }

    public int getStep() {
        return step;
    }

    public float getCalorieConsumption() {
        return calorieConsumption;
    }
}
