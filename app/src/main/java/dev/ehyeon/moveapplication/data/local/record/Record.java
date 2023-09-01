package dev.ehyeon.moveapplication.data.local.record;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import dev.ehyeon.moveapplication.room.BitmapConverter;

@Entity
@TypeConverters(value = BitmapConverter.class)
public class Record {

    @PrimaryKey
    @NonNull
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

    @NonNull
    private final Bitmap image;

    public Record(@NonNull Long id, @NonNull Integer elapsedTime, @NonNull Float totalTravelDistance, @NonNull Float averageSpeed, @NonNull Integer step, @NonNull Float calorieConsumption, @NonNull Bitmap image) {
        this.id = id;
        this.elapsedTime = elapsedTime;
        this.totalTravelDistance = totalTravelDistance;
        this.averageSpeed = averageSpeed;
        this.step = step;
        this.calorieConsumption = calorieConsumption;
        this.image = image;
    }

    @NonNull
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

    @NonNull
    public Bitmap getImage() {
        return image;
    }
}
