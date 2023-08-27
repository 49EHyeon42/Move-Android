package dev.ehyeon.moveapplication.di;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ServiceComponent;
import dev.ehyeon.moveapplication.data.remote.location.LocationRepository;
import dev.ehyeon.moveapplication.data.local.step.SensorEventListener2Impl;
import dev.ehyeon.moveapplication.data.local.step.StepRepository;
import dev.ehyeon.moveapplication.data.local.stopwatch.StopwatchRepository;

@Module
@InstallIn(ServiceComponent.class)
public class TrackingServiceModule {

    @Provides
    public StopwatchRepository provideStopwatchRepository() {
        return new StopwatchRepository();
    }

    @Provides
    public LocationRepository provideLocationRepository() {
        return new LocationRepository();
    }

    @Provides
    public SensorEventListener2Impl provideSensorEventListener2Impl() {
        return new SensorEventListener2Impl();
    }

    @Provides
    public StepRepository provideStepRepository(SensorEventListener2Impl sensorEventListener2Impl) {
        return new StepRepository(sensorEventListener2Impl);
    }
}
