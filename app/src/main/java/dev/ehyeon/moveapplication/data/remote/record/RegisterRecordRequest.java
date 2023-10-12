package dev.ehyeon.moveapplication.data.remote.record;

import androidx.annotation.NonNull;

public class RegisterRecordRequest {

    @NonNull
    private final String savedDate;

    private final int elapsedTime;

    private final int totalTravelDistance;

    private final int averageSpeed;

    private final int step;

    private final float calorieConsumption;

    public RegisterRecordRequest(@NonNull String savedDate, int elapsedTime, int totalTravelDistance, int averageSpeed, int step, float calorieConsumption) {
        this.savedDate = savedDate;
        this.elapsedTime = elapsedTime;
        this.totalTravelDistance = totalTravelDistance;
        this.averageSpeed = averageSpeed;
        this.step = step;
        this.calorieConsumption = calorieConsumption;
    }

    @NonNull
    public String getSavedDate() {
        return savedDate;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public int getTotalTravelDistance() {
        return totalTravelDistance;
    }

    public int getAverageSpeed() {
        return averageSpeed;
    }

    public int getStep() {
        return step;
    }

    public float getCalorieConsumption() {
        return calorieConsumption;
    }
}
