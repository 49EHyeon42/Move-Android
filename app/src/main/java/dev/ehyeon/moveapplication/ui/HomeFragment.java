package dev.ehyeon.moveapplication.ui;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import dev.ehyeon.moveapplication.R;
import dev.ehyeon.moveapplication.data.remote.movestop.MoveStopResponse;
import dev.ehyeon.moveapplication.data.remote.movestop.MoveStopService;
import dev.ehyeon.moveapplication.databinding.FragmentHomeBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleMap.SnapshotReadyCallback {

    private static final String TAG = "HomeFragment";

    @Inject
    protected MoveStopService moveStopService;

    private HomeFragmentViewModel viewModel;
    private FragmentHomeBinding binding;

    private GoogleMap googleMap;
    private final List<Marker> markers = new ArrayList<>();

    private Polyline googleMapPolyline;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(HomeFragmentViewModel.class);

        viewModel.onCreateWithContext(requireContext());
    }

    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        SupportMapFragment googleMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragmentHome_supportGoogleMapFragment);

        if (googleMapFragment != null) {
            googleMapFragment.getMapAsync(this);
        }

        binding.fragmentHomeTrackingServiceButton.setOnClickListener(ignored -> {
            if (viewModel.getTrackingServiceLiveData().getValue() == null) {
                // 지동 회전 제한
                googleMap.getUiSettings().setRotateGesturesEnabled(false);
                // 지도 이동 제한
                googleMap.getUiSettings().setScrollGesturesEnabled(false);
                // 지도 줌 제한
                googleMap.getUiSettings().setZoomGesturesEnabled(false);

                LocationServices.getFusedLocationProviderClient(requireContext())
                        .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                        .addOnSuccessListener(location ->
                                googleMap.moveCamera(
                                        CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17)));

                viewModel.startTrackingService();
            } else {
                binding.fragmentHomeTrackingServiceButton.setEnabled(false);

                updateGoogleMapByCenterLatLng();

                googleMap.snapshot(this);
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

        this.googleMap = googleMap;

        googleMap.setOnCameraMoveStartedListener(i -> {
            for (Marker marker : markers) {
                marker.remove();
            }

            markers.clear();
        });

        googleMap.setOnCameraIdleListener(() -> {
            LatLng northeast = googleMap.getProjection().getVisibleRegion().latLngBounds.northeast;
            LatLng southwest = googleMap.getProjection().getVisibleRegion().latLngBounds.southwest;

            // TODO refactor, API 호출이 너무 많음
            moveStopService.getMoveStop(southwest.latitude, southwest.latitude, northeast.latitude, northeast.longitude)
                    .enqueue(new Callback<List<MoveStopResponse>>() {
                        @Override
                        public void onResponse(Call<List<MoveStopResponse>> call, Response<List<MoveStopResponse>> response) {
                            for (MoveStopResponse moveStopResponse : response.body()) {
                                Marker marker = googleMap.addMarker(new MarkerOptions()
                                        .title(moveStopResponse.getName())
                                        .position(new LatLng(moveStopResponse.getLatitude(), moveStopResponse.getLongitude())));

                                markers.add(marker);
                            }
                        }

                        @Override
                        public void onFailure(Call<List<MoveStopResponse>> call, Throwable t) {
                            Log.i(TAG, "onFailure: 맵 이동 실패");
                        }
                    });
        });

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

    // TODO fix: java.lang.IllegalStateException: no included points
    private void updateGoogleMapByCenterLatLng() {
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        for (LatLng latLng : googleMapPolyline.getPoints()) {
            boundsBuilder.include(latLng);
        }

        LatLng centerLatLng = boundsBuilder.build().getCenter();

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng, 17));
    }

    @Override
    public void onSnapshotReady(@Nullable Bitmap bitmap) {
        // TODO refactor, bitmap이 늦게 들어옴, 비동기 처리를 동기 처리로 변환 필요
        viewModel.stopTrackingService(bitmap);

        // 지동 회전 제한 해제
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        // 지도 이동 제한 해제
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        // 지도 줌 제한 해제
        googleMap.getUiSettings().setZoomGesturesEnabled(true);

        binding.fragmentHomeTrackingServiceButton.setEnabled(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO refactor
        viewModel.getTrackingServiceLiveData().observe(getViewLifecycleOwner(), trackingService -> {
            if (trackingService == null) {
                return;
            }

            trackingService.getLatLngListLiveData().observe(trackingService, latLngs -> {
                if (latLngs.size() == 1) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get(0), 17));
                } else if (latLngs.size() > 1){
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get(latLngs.size() - 1), 17));
                }
            });


            if (googleMapPolyline != null) {
                trackingService.getLatLngListLiveData().observe(trackingService, latLngs -> googleMapPolyline.setPoints(latLngs));
            }

            trackingService.getSecondLiveData().observe(trackingService, second ->
                    binding.fragmentHomeElapsedTimeTextView
                            .setText(String.format(Locale.getDefault(), "경과 시간 %02d:%02d:%02d", second / 3600, (second % 3600) / 60, second % 60)));

            trackingService.getTotalTravelDistanceLiveData().observe(trackingService, totalDistance ->
                    binding.fragmentHomeTotalTravelDistanceTextView
                            .setText(String.format(Locale.getDefault(), "총 이동 거리 %.0f m", totalDistance)));

            trackingService.getAverageSpeedLiveData().observe(trackingService, averageSpeed ->
                    binding.fragmentHomeAverageSpeedTextView
                            .setText(String.format(Locale.getDefault(), "평균 속력 %.1f km/h", averageSpeed)));

            trackingService.getStepLiveData().observe(trackingService, step ->
                    binding.fragmentHomeStepTextView.setText(String.format(Locale.getDefault(), "걸음 수 %,d", step)));

            trackingService.getCalorieConsumptionLiveData().observe(trackingService, calorie ->
                    binding.fragmentHomeCalorieConsumptionTextView.setText(String.format(Locale.getDefault(), "칼로리 소모량 %.1f kcal", calorie)));
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        viewModel.onDestroyWithContext();
    }
}
