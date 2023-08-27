package dev.ehyeon.moveapplication.data.local.record;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Record {

    @PrimaryKey
    private Long id;

    @NonNull
    private Integer second;

    @NonNull
    private Float totalDistance;

    @NonNull
    private Float averageSpeed;

    @NonNull
    private Integer step;

    @NonNull
    private Float kiloCalorie;

    public Record(Long id, @NonNull Integer second, @NonNull Float totalDistance, @NonNull Float averageSpeed, @NonNull Integer step, @NonNull Float kiloCalorie) {
        this.id = id;
        this.second = second;
        this.totalDistance = totalDistance;
        this.averageSpeed = averageSpeed;
        this.step = step;
        this.kiloCalorie = kiloCalorie;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NonNull
    public Integer getSecond() {
        return second;
    }

    public void setSecond(@NonNull Integer second) {
        this.second = second;
    }

    @NonNull
    public Float getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(@NonNull Float totalDistance) {
        this.totalDistance = totalDistance;
    }

    @NonNull
    public Float getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(@NonNull Float averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    @NonNull
    public Integer getStep() {
        return step;
    }

    public void setStep(@NonNull Integer step) {
        this.step = step;
    }

    @NonNull
    public Float getKiloCalorie() {
        return kiloCalorie;
    }

    public void setKiloCalorie(@NonNull Float kiloCalorie) {
        this.kiloCalorie = kiloCalorie;
    }
}
