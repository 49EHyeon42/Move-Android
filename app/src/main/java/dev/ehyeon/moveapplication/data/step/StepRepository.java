package dev.ehyeon.moveapplication.data.step;

import static android.hardware.SensorManager.SENSOR_DELAY_NORMAL;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class StepRepository implements SensorEventListener2 {

    private final SensorManager sensorManager;
    private final MutableLiveData<Integer> step;

    public StepRepository(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
        this.step = new MutableLiveData<>(0);
    }

    public void startSensor() {
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR), SENSOR_DELAY_NORMAL);
    }

    public void stopSensor() {
        sensorManager.unregisterListener(this);
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
