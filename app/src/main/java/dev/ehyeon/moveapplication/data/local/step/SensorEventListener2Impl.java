package dev.ehyeon.moveapplication.data.local.step;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;

import dev.ehyeon.moveapplication.util.NonNullLiveData;
import dev.ehyeon.moveapplication.util.NonNullMutableLiveData;

public class SensorEventListener2Impl implements SensorEventListener2 {

    private final NonNullMutableLiveData<Integer> stepNonNullMutableLiveData;

    public SensorEventListener2Impl() {
        this.stepNonNullMutableLiveData = new NonNullMutableLiveData<>(0);
    }

    @Override
    public void onFlushCompleted(Sensor sensor) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        stepNonNullMutableLiveData.setValue(stepNonNullMutableLiveData.getValue() + 1);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void initializeStep() {
        stepNonNullMutableLiveData.setValue(0);
    }

    public NonNullLiveData<Integer> getStepLiveData() {
        return stepNonNullMutableLiveData;
    }
}
