package dev.ehyeon.moveapplication.ui;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Locale;

import dev.ehyeon.moveapplication.R;
import dev.ehyeon.moveapplication.databinding.FragmentHomeBinding;
import dev.ehyeon.moveapplication.service.TrackingService;
import dev.ehyeon.moveapplication.service.TrackingServiceAction;
import dev.ehyeon.moveapplication.service.TrackingServiceBinder;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TrackingServiceAction.TRACKING_SERVICE_IS_RUNNING.getAction())) {
                requireContext().bindService(new Intent(getActivity(), TrackingService.class), serviceConnection, Context.BIND_AUTO_CREATE);
            }
        }
    };

    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            trackingServiceMutableLiveData.setValue(((TrackingServiceBinder) service).getTrackingService());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            trackingServiceMutableLiveData.setValue(null);
        }
    };

    private FragmentHomeBinding binding;

    private MutableLiveData<TrackingService> trackingServiceMutableLiveData;

    private Polyline googleMapPolyline;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        trackingServiceMutableLiveData = new MutableLiveData<>();

        LocalBroadcastManager
                .getInstance(requireContext())
                .registerReceiver(broadcastReceiver,
                        new IntentFilter(TrackingServiceAction.TRACKING_SERVICE_IS_RUNNING.getAction()));

        LocalBroadcastManager
                .getInstance(requireContext())
                .sendBroadcast(new Intent(TrackingServiceAction.IS_TRACKING_SERVICE_RUNNING.getAction()));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        SupportMapFragment googleMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragmentHome_supportGoogleMapFragment);

        if (googleMapFragment != null) {
            googleMapFragment.getMapAsync(this);
        }

        binding.fragmentHomeTrackingServiceButton.setOnClickListener(ignored -> {
            Intent trackingServiceIntent = new Intent(requireContext(), TrackingService.class);

            if (trackingServiceMutableLiveData.getValue() != null) {
                requireContext().unbindService(serviceConnection);

                if (requireContext().stopService(trackingServiceIntent)) {
                    trackingServiceMutableLiveData.setValue(null);
                }
            } else {
                if (requireContext().startForegroundService(trackingServiceIntent) != null) {
                    requireContext().bindService(trackingServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
                }
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.5666612, 126.9783785), 10));
            return;
        }

        googleMapPolyline = googleMap.addPolyline(new PolylineOptions());

        googleMap.setMyLocationEnabled(true);

        LocationServices.getFusedLocationProviderClient(requireContext())
                .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener(location ->
                        googleMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                        location == null ?
                                                new LatLng(37.5666612, 126.9783785) :
                                                new LatLng(location.getLatitude(), location.getLongitude()), 17)));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        trackingServiceMutableLiveData.observe(getViewLifecycleOwner(), trackingService -> {
            if (trackingService == null) {
                return;
            }

            trackingService.getSecondLiveData().observe(trackingService, second ->
                    binding.fragmentHomeTimeTextView
                            .setText(String.format(Locale.getDefault(), "경과 시간 %02d:%02d:%02d", second / 3600, (second % 3600) / 60, second % 60)));

            trackingService.getLatLngListLiveData().observe(trackingService, latLngs -> googleMapPolyline.setPoints(latLngs));

            trackingService.getTotalDistanceLiveData().observe(trackingService, totalDistance ->
                    binding.fragmentHomeTotalDistanceTextView
                            .setText(String.format(Locale.getDefault(), "총 이동 거리 %.0f m", totalDistance)));

            trackingService.getAverageSpeedMutableLiveData().observe(trackingService, averageSpeed ->
                    binding.fragmentHomeAverageSpeedTextView
                            .setText(String.format(Locale.getDefault(), "평균 속력 %.1f km/h", averageSpeed)));

            trackingService.getStepLiveData().observe(trackingService, step ->
                    binding.fragmentHomeStepTextView.setText(String.format(Locale.getDefault(), "걸음 수 %,d", step)));
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver);
    }
}
