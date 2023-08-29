package dev.ehyeon.moveapplication.data.local.record;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Record {

    @PrimaryKey
    private final Long id;

    @NonNull
    private final Integer elapsedTime; // 경과 시간, 단위: second

    @NonNull
    private final Float totalTravelDistance; // 총 이동 거리, 단위: meter

    @NonNull
    private final Float averageSpeed; // 평균 속력, 단위: km/h

    @NonNull
    private final Integer step; // 걸음수

    @NonNull
    private final Float calorieConsumption; // 칼로리 소모량, 단위: kiloCalorie

    public Record(Long id, @NonNull Integer elapsedTime, @NonNull Float totalTravelDistance, @NonNull Float averageSpeed, @NonNull Integer step, @NonNull Float calorieConsumption) {
        this.id = id;
        this.elapsedTime = elapsedTime;
        this.totalTravelDistance = totalTravelDistance;
        this.averageSpeed = averageSpeed;
        this.step = step;
        this.calorieConsumption = calorieConsumption;
    }

    public Long getId() {
        return id;
    }

    @NonNull
    public Integer getElapsedTime() {
        return elapsedTime;
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
