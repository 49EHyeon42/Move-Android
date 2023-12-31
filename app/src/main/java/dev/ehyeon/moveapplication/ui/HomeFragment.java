package dev.ehyeon.moveapplication.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import dev.ehyeon.moveapplication.MoveApplication;
import dev.ehyeon.moveapplication.R;
import dev.ehyeon.moveapplication.data.remote.visited_move_stop.SaveOrUpdateVisitedMoveStopRequest;
import dev.ehyeon.moveapplication.data.remote.visited_move_stop.SearchVisitedMoveStopResponse;
import dev.ehyeon.moveapplication.data.remote.visited_move_stop.VisitedMoveStopService;
import dev.ehyeon.moveapplication.databinding.FragmentHomeBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleMap.SnapshotReadyCallback {

    private static final String TAG = "HomeFragment";

    @Inject
    protected VisitedMoveStopService visitedMoveStopService;

    private HomeFragmentViewModel viewModel;
    private FragmentHomeBinding binding;

    private String accessToken;

    private GoogleMap googleMap;
    private final List<Marker> markers = new ArrayList<>();

    private final Set<String> visitedMarkerNames = new HashSet<>();

    private Polyline googleMapPolyline;

    private double latitude1;
    private double longitude1;
    private double latitude2;
    private double longitude2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(HomeFragmentViewModel.class);

        viewModel.onCreateWithContext(requireContext());

        accessToken = requireActivity().getSharedPreferences("move", Context.MODE_PRIVATE).getString("access token", "");
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

            latitude1 = southwest.latitude;
            longitude1 = southwest.longitude;
            latitude2 = northeast.latitude;
            longitude2 = northeast.longitude;

            visitedMoveStopService.searchVisitedMoveStop(
                            accessToken,
                            southwest.latitude, southwest.longitude, northeast.latitude, northeast.longitude)
                    .enqueue(new Callback<List<SearchVisitedMoveStopResponse>>() {
                        @Override
                        public void onResponse(Call<List<SearchVisitedMoveStopResponse>> call, Response<List<SearchVisitedMoveStopResponse>> response) {
                            if (response.body() == null) {
                                Log.i(TAG, "onResponse: response body is null " + response.code());

                                return;
                            }

                            for (SearchVisitedMoveStopResponse searchVisitedMoveStopResponse : response.body()) {
                                Marker marker;

                                if (searchVisitedMoveStopResponse.isVisited()) {
                                    if (!visitedMarkerNames.contains(searchVisitedMoveStopResponse.getName())) {
                                        visitedMarkerNames.add(searchVisitedMoveStopResponse.getName());

                                        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                            return;
                                        }

                                        Notification notification = new Notification.Builder(requireContext(), ((MoveApplication) requireActivity().getApplication()).getVisitedMoveStopChannelId())
                                                .setContentTitle("Move")
                                                .setContentText(searchVisitedMoveStopResponse.getName() + " 방문!")
                                                .setSmallIcon(R.drawable.ic_launcher_background)
                                                .setAutoCancel(true)
                                                .build();

                                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());

                                        notificationManager.notify(49, notification);
                                    }

                                    marker = googleMap.addMarker(new MarkerOptions()
                                            .title(searchVisitedMoveStopResponse.getName())
                                            .position(new LatLng(searchVisitedMoveStopResponse.getLatitude(), searchVisitedMoveStopResponse.getLongitude()))
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.visited_marker)));
                                } else {
                                    visitedMarkerNames.remove(searchVisitedMoveStopResponse.getName());

                                    marker = googleMap.addMarker(new MarkerOptions()
                                            .title(searchVisitedMoveStopResponse.getName())
                                            .position(new LatLng(searchVisitedMoveStopResponse.getLatitude(), searchVisitedMoveStopResponse.getLongitude())));
                                }

                                markers.add(marker);
                            }
                        }

                        @Override
                        public void onFailure(Call<List<SearchVisitedMoveStopResponse>> call, Throwable t) {
                            Log.i(TAG, "onFailure: 맵 이동 실패");
                        }
                    });
        });

        googleMap.setOnMarkerClickListener(marker -> {
            Toast.makeText(requireContext(), marker.getTitle(), Toast.LENGTH_SHORT).show();

            return true;
        });

        googleMapPolyline = googleMap.addPolyline(new PolylineOptions());

        googleMap.setMyLocationEnabled(true);

        LocationServices.getFusedLocationProviderClient(requireContext())
                .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        Log.i(TAG, "latitude: " + location.getLatitude() + ", longitude: " + location.getLongitude());
                    }

                    googleMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                    location == null ?
                                            new LatLng(37.5666612, 126.9783785) :
                                            new LatLng(location.getLatitude(), location.getLongitude()), 17));
                });

        LocationServices.getFusedLocationProviderClient(requireContext())
                .requestLocationUpdates(
                        new LocationRequest.Builder(3000).build(), new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                Location location = locationResult.getLastLocation();

                                if (location != null) {
                                    visitedMoveStopService.saveOrUpdateVisitedMoveStop(accessToken, new SaveOrUpdateVisitedMoveStopRequest(
                                                    location.getLatitude(), location.getLongitude(),
                                                    latitude1, longitude1,
                                                    latitude2, longitude2))
                                            .enqueue(new Callback<Void>() {
                                                @Override
                                                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                                                    Log.i(TAG, "saveOrUpdateVisitedMoveStop code is " + response.code());
                                                }

                                                @Override
                                                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                                                    Log.i(TAG, "saveOrUpdateVisitedMoveStop: 통신 실패");
                                                }
                                            });
                                }
                            }
                        }, null);
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
                } else if (latLngs.size() > 1) {
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
