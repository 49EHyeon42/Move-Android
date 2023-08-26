package dev.ehyeon.moveapplication.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private HomeFragmentViewModel viewModel;
    private FragmentHomeBinding binding;

    private Polyline googleMapPolyline;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(HomeFragmentViewModel.class);

        viewModel.onCreateWithContext(requireContext());
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

            if (viewModel.getTrackingServiceLiveData().getValue() != null) {
                viewModel.unbindService();

                if (requireContext().stopService(trackingServiceIntent)) {
                    viewModel.disconnectTrackingService();
                }
            } else {
                if (requireContext().startForegroundService(trackingServiceIntent) != null) {
                    viewModel.bindService();
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

        // TODO refactor
        viewModel.getTrackingServiceLiveData().observe(getViewLifecycleOwner(), trackingService -> {
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

        viewModel.onDestroyWithContext(requireContext());
    }
}
