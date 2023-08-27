package dev.ehyeon.moveapplication.data.local.step;

import static android.hardware.SensorManager.SENSOR_DELAY_NORMAL;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import javax.inject.Inject;

public class StepRepository {

    private final SensorEventListener2Impl sensorEventListener2Impl;

    @Inject
    public StepRepository(SensorEventListener2Impl sensorEventListener2Impl) {
        this.sensorEventListener2Impl = sensorEventListener2Impl;
    }

    public void startStepSensor(@NonNull Context context) {
        sensorEventListener2Impl.initializeStep();

        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(sensorEventListener2Impl, sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR), SENSOR_DELAY_NORMAL);
    }

    public void stopStepSensor(@NonNull Context context) {
        ((SensorManager) context.getSystemService(Context.SENSOR_SERVICE)).unregisterListener(sensorEventListener2Impl);
    }

    public LiveData<Integer> getStepLiveData() {
        return sensorEventListener2Impl.getStepLiveData();
    }
}
