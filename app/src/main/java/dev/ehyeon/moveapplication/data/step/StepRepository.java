package dev.ehyeon.moveapplication.data.step;

import static android.hardware.SensorManager.SENSOR_DELAY_NORMAL;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import androidx.lifecycle.LiveData;

import dev.ehyeon.moveapplication.data.ContextRepository;

public class StepRepository implements ContextRepository {

    private final SensorEventListener2Impl sensorEventListener;

    private SensorManager sensorManager;

    public StepRepository() {
        sensorEventListener = new SensorEventListener2Impl();
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

        sensorEventListener.initializeStep();

        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR), SENSOR_DELAY_NORMAL);
    }

    public void stopStepSensor() {
        if (sensorManager == null) {
            return;
        }

        sensorManager.unregisterListener(sensorEventListener);
    }

    public LiveData<Integer> getStepLiveData() {
        return sensorEventListener.getStepLiveData();
    }
}
