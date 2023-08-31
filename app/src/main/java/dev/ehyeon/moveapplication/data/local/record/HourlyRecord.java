package dev.ehyeon.moveapplication.data.local.record;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class HourlyRecord {

    @PrimaryKey
    @NonNull
    private final Long id;

    @NonNull
    private final Float totalTravelDistance;

    @NonNull
    private final Float averageSpeed;

    @NonNull
    private final Integer step;

    @NonNull
    private final Float calorieConsumption;

    public HourlyRecord(@NonNull Long id, @NonNull Float totalTravelDistance, @NonNull Float averageSpeed, @NonNull Integer step, @NonNull Float calorieConsumption) {
        this.id = id;
        this.totalTravelDistance = totalTravelDistance;
        this.averageSpeed = averageSpeed;
        this.step = step;
        this.calorieConsumption = calorieConsumption;
    }

    @NonNull
    public Long getId() {
        return id;
    }

    @NonNull
    public Float getTotalTravelDistance() {
        return totalTravelDistance;
    }

    @NonNull
    public Float getAverageSpeed() {
        return averageSpeed;
    }

    @NonNull
    public Integer getStep() {
        return step;
    }

    @NonNull
    public Float getCalorieConsumption() {
        return calorieConsumption;
    }
}
