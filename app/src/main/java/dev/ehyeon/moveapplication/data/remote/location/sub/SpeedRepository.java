package dev.ehyeon.moveapplication.data.remote.location.sub;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import dev.ehyeon.moveapplication.util.NonNullMutableLiveData;

public class SpeedRepository {

    private final List<Float> speedList;

    private final NonNullMutableLiveData<Float> averageSpeedNonNullMutableLiveData;

    public SpeedRepository() {
        speedList = new ArrayList<>();

        averageSpeedNonNullMutableLiveData = new NonNullMutableLiveData<>(0f);
    }

    public void initializeSpeed() {
        speedList.clear();

        averageSpeedNonNullMutableLiveData.setValue(0f);
    }

    public void updateSpeed(float currentSpeed) {
        currentSpeed = meterPerSecondToKilometerPerHour(currentSpeed);

        speedList.add(currentSpeed);

        averageSpeedNonNullMutableLiveData.setValue(speedList.size() == 1 ?
                currentSpeed :
                // 평균 필터 알고리즘 (Average Filter Algorithm)
                // Avg(N) = (N - 1) / N * Avg(N - 1) + 1 / N * newValue)
                (speedList.size() - 1f) / speedList.size() * averageSpeedNonNullMutableLiveData.getValue() + 1f / speedList.size() * currentSpeed);
    }

    private float meterPerSecondToKilometerPerHour(float f) {
        return f * 3.6f;
    }

    public LiveData<Float> getAverageSpeedLiveData() {
        return averageSpeedNonNullMutableLiveData;
    }
}
