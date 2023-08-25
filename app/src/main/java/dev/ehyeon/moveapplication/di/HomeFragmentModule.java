package dev.ehyeon.moveapplication.di;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.FragmentComponent;
import dev.ehyeon.moveapplication.service.TrackingServiceConnection;

@Module
@InstallIn(FragmentComponent.class)
public class HomeFragmentModule {

    @Provides
    public TrackingServiceConnection provideServiceConnection() {
        return new TrackingServiceConnection();
    }
}
