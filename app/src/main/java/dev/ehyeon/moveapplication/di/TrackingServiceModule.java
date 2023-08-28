package dev.ehyeon.moveapplication.di;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ServiceComponent;
import dev.ehyeon.moveapplication.data.local.step.SensorEventListener2Impl;
import dev.ehyeon.moveapplication.data.local.step.StepRepository;
import dev.ehyeon.moveapplication.data.local.stopwatch.StopwatchRepository;
import dev.ehyeon.moveapplication.data.remote.location.LocationRepository;
import dev.ehyeon.moveapplication.data.remote.location.sub.KilocalorieConsumptionRepository;
import dev.ehyeon.moveapplication.data.remote.location.sub.SpeedRepository;
import dev.ehyeon.moveapplication.data.remote.location.sub.TravelDistanceRepository;

@Module
@InstallIn(ServiceComponent.class)
public class TrackingServiceModule {

    @Provides
    public StopwatchRepository provideStopwatchRepository() {
        return new StopwatchRepository();
    }

    @Provides
    public TravelDistanceRepository provideTravelDistanceRepository() {
        return new TravelDistanceRepository();
    }

    @Provides
    public SpeedRepository provideSpeedRepository() {
        return new SpeedRepository();
    }

    @Provides
    public KilocalorieConsumptionRepository provideKilocalorieConsumptionRepository() {
        return new KilocalorieConsumptionRepository();
    }

    @Provides
    public LocationRepository provideLocationRepository(TravelDistanceRepository travelDistanceRepository, SpeedRepository speedRepository, KilocalorieConsumptionRepository kilocalorieConsumptionRepository) {
        return new LocationRepository(travelDistanceRepository, speedRepository, kilocalorieConsumptionRepository);
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
