package dev.ehyeon.moveapplication.data.local.step;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SensorEventListener2Impl implements SensorEventListener2 {

    private final MutableLiveData<Integer> stepMutableLiveData;

    public SensorEventListener2Impl() {
        this.stepMutableLiveData = new MutableLiveData<>(0);
    }

    @Override
    public void onFlushCompleted(Sensor sensor) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (stepMutableLiveData.getValue() != null) {
            stepMutableLiveData.setValue(stepMutableLiveData.getValue() + (int) event.values[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void initializeStep() {
        stepMutableLiveData.setValue(0);
    }

    public LiveData<Integer> getStepLiveData() {
        return stepMutableLiveData;
    }
}
