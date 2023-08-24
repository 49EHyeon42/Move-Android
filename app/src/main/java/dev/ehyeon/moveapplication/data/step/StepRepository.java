package dev.ehyeon.moveapplication.data.step;

import static android.hardware.SensorManager.SENSOR_DELAY_NORMAL;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import androidx.lifecycle.LiveData;

import javax.inject.Inject;

import dev.ehyeon.moveapplication.data.ContextRepository;

public class StepRepository implements ContextRepository {

    private final SensorEventListener2Impl sensorEventListener2Impl;

    private SensorManager sensorManager;

    @Inject
    public StepRepository(SensorEventListener2Impl sensorEventListener2Impl) {
        this.sensorEventListener2Impl = sensorEventListener2Impl;
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

        sensorEventListener2Impl.initializeStep();

        sensorManager.registerListener(sensorEventListener2Impl, sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR), SENSOR_DELAY_NORMAL);
    }

    public void stopStepSensor() {
        if (sensorManager == null) {
            return;
        }

        sensorManager.unregisterListener(sensorEventListener2Impl);
    }

    public LiveData<Integer> getStepLiveData() {
        return sensorEventListener2Impl.getStepLiveData();
    }
}
