package dev.ehyeon.moveapplication.di;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ServiceComponent;
import dev.ehyeon.moveapplication.data.step.StepRepository;

@Module
@InstallIn(ServiceComponent.class)
public class TrackingServiceModule {

    @Provides
    public StepRepository provideStepRepository() {
        return new StepRepository();
    }
}
