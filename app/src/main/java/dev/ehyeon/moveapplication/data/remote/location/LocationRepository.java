package dev.ehyeon.moveapplication.data.remote.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dev.ehyeon.moveapplication.data.remote.location.sub.KilocalorieConsumptionRepository;
import dev.ehyeon.moveapplication.data.remote.location.sub.SpeedRepository;
import dev.ehyeon.moveapplication.util.NonNullMutableLiveData;

// TODO refactor
public class LocationRepository {

    private static final long INTERVAL_MILLIS = 1000;

    private final SpeedRepository speedRepository;
    private final KilocalorieConsumptionRepository kilocalorieConsumptionRepository;

    private final List<LatLng> latLngList;
    private final NonNullMutableLiveData<List<LatLng>> latLngListNonNullMutableLiveData;

    private Location previousLocation;
    private final NonNullMutableLiveData<Float> totalDistanceNonNullMutableLiveData;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    @Inject
    public LocationRepository(SpeedRepository speedRepository, KilocalorieConsumptionRepository kilocalorieConsumptionRepository) {
        this.speedRepository = speedRepository;
        this.kilocalorieConsumptionRepository = kilocalorieConsumptionRepository;

        latLngList = new ArrayList<>();
        latLngListNonNullMutableLiveData = new NonNullMutableLiveData<>(latLngList);

        totalDistanceNonNullMutableLiveData = new NonNullMutableLiveData<>(0f);
    }

    public void initializeContext(Context context) {
        if (context == null) {
            fusedLocationProviderClient = null;

            locationRequest = null;
            locationCallback = null;
            return;
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        locationRequest = new LocationRequest.Builder(INTERVAL_MILLIS).build();
        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();

                if (location != null) {
                    LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    latLngList.add(newLatLng);
                    latLngListNonNullMutableLiveData.setValue(latLngList);

                    // distance
                    if (previousLocation != null) {
                        totalDistanceNonNullMutableLiveData.setValue(
                                totalDistanceNonNullMutableLiveData.getValue() + previousLocation.distanceTo(location));
                    }

                    previousLocation = location;

                    float currentSpeed = location.getSpeed();

                    // Speed
                    speedRepository.updateSpeed(currentSpeed);

                    // kilocalorie, 65kg 가정
                    kilocalorieConsumptionRepository.updateKilocalorieConsumption(65, currentSpeed);
                }
            }
        };
    }

    @SuppressLint("MissingPermission")
    public void startLocationSensor() {
        initAll();

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    public void stopLocationSensor() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    public void initAll() {
        latLngList.clear();
        previousLocation = null;
        speedRepository.initializeSpeed();
        kilocalorieConsumptionRepository.initializeKilocalorieConsumption();
    }

    public LiveData<List<LatLng>> getLatLngListLiveData() {
        return latLngListNonNullMutableLiveData;
    }

    public LiveData<Float> getTotalDistanceLiveData() {
        return totalDistanceNonNullMutableLiveData;
    }

    public LiveData<Float> getAverageSpeedLiveData() {
        return speedRepository.getAverageSpeedLiveData();
    }

    public LiveData<Float> getKilocalorieConsumptionLiveData() {
        return kilocalorieConsumptionRepository.getKilocalorieConsumptionLiveData();
    }
}
