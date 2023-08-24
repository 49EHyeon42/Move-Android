package dev.ehyeon.moveapplication.data.step;

import static android.hardware.SensorManager.SENSOR_DELAY_NORMAL;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import dev.ehyeon.moveapplication.data.ContextRepository;

public class StepRepository implements ContextRepository, SensorEventListener2 {

    private final MutableLiveData<Integer> stepMutableLiveData;

    private SensorManager sensorManager;

    public StepRepository() {
        stepMutableLiveData = new MutableLiveData<>(0);
    }

    @Override
    public void initializeContext(Context context) {
        if (context == null) {
            sensorManager = null;
            return;
        }

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public void startStepSensor() {
        if (sensorManager == null) {
            return;
        }

        initializeStep();

        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR), SENSOR_DELAY_NORMAL);
    }

    public void stopStepSensor() {
        if (sensorManager == null) {
            return;
        }

        sensorManager.unregisterListener(this);
    }

    public void initializeStep() {
        stepMutableLiveData.setValue(0);
    }

    public LiveData<Integer> getStepLiveData() {
        return stepMutableLiveData;
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
}
