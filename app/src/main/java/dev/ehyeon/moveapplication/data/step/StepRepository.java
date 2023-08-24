package dev.ehyeon.moveapplication.data.step;

import static android.hardware.SensorManager.SENSOR_DELAY_NORMAL;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class StepRepository implements SensorEventListener2 {

    private final MutableLiveData<Integer> step;

    private SensorManager sensorManager;

    public StepRepository() {
        step = new MutableLiveData<>(0);
    }

    public void initializeContext(Context context) {
        if (context == null) {
            sensorManager = null;
            return;
        }

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public void startSensor() {
        if (sensorManager == null) {
            return;
        }

        initializeStep();

        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR), SENSOR_DELAY_NORMAL);
    }

    public void stopSensor() {
        if (sensorManager == null) {
            return;
        }

        sensorManager.unregisterListener(this);
    }

    public void initializeStep() {
        step.setValue(0);
    }

    public LiveData<Integer> getStep() {
        return step;
    }

    @Override
    public void onFlushCompleted(Sensor sensor) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (step.getValue() != null) {
            step.setValue(step.getValue() + (int) event.values[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
