package dev.ehyeon.moveapplication.di;

import androidx.fragment.app.Fragment;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.FragmentComponent;
import dev.ehyeon.moveapplication.broadcast.HomeFragmentBroadcastReceiver;
import dev.ehyeon.moveapplication.service.TrackingServiceConnection;
import dev.ehyeon.moveapplication.ui.HomeFragment;

@Module
@InstallIn(FragmentComponent.class)
public class HomeFragmentModule {

    @Provides
    public HomeFragment provideHomeFragment(Fragment fragment) {
        return (HomeFragment) fragment;
    }

    @Provides
    public TrackingServiceConnection provideServiceConnection() {
        return new TrackingServiceConnection();
    }

    @Provides
    public HomeFragmentBroadcastReceiver provideBroadcastReceiver(HomeFragment homeFragment) {
        return new HomeFragmentBroadcastReceiver(homeFragment);
    }
}
