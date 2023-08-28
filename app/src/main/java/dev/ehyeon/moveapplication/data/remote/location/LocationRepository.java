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

import dev.ehyeon.moveapplication.data.remote.location.sub.SpeedRepository;
import dev.ehyeon.moveapplication.util.NonNullMutableLiveData;

// TODO refactor
public class LocationRepository {

    private static final long INTERVAL_MILLIS = 1000;

    private final SpeedRepository speedRepository;

    private final List<LatLng> latLngList;
    private final NonNullMutableLiveData<List<LatLng>> latLngListNonNullMutableLiveData;

    private Location previousLocation;
    private final NonNullMutableLiveData<Float> totalDistanceNonNullMutableLiveData;

    private final NonNullMutableLiveData<Float> calorieConsumptionNonNullMutableLiveData;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    @Inject
    public LocationRepository(SpeedRepository speedRepository) {
        this.speedRepository = speedRepository;

        latLngList = new ArrayList<>();
        latLngListNonNullMutableLiveData = new NonNullMutableLiveData<>(latLngList);

        totalDistanceNonNullMutableLiveData = new NonNullMutableLiveData<>(0f);

        calorieConsumptionNonNullMutableLiveData = new NonNullMutableLiveData<>(0f);
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

                    // Speed
                    float currentSpeedPerSecond = location.getSpeed();

                    speedRepository.updateSpeed(currentSpeedPerSecond);

                    // Calorie
                    calorieConsumptionNonNullMutableLiveData.setValue(
                            calorieConsumptionNonNullMutableLiveData.getValue() + getCaloriePerSecond(currentSpeedPerSecond));
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

    public LiveData<Float> getCalorieConsumptionLiveData() {
        return calorieConsumptionNonNullMutableLiveData;
    }

    private float getCaloriePerSecond(float speed) {
        // 1분당 MET * 1분당 산소 섭취(3.5ml) * 몸무게(65 가정) / 1000 * 5
        return getMetPerSecond(speed) * 0.058f * 65 / 200;
    }

    private float getMetPerSecond(float speed) {
        if (speed < 0) {
            return 0;
        } else if (speed <= 3) {
            return 0.833f;
        } else if (speed <= 4) {
            return 1.111f;
        } else if (speed <= 5) {
            return 1.389f;
        } else if (speed <= 6) {
            return 1.667f;
        } else if (speed <= 7) {
            return 1.944f;
        } else if (speed <= 8) {
            return 2.222f;
        } else if (speed <= 9) {
            return 2.5f;
        } else if (speed <= 10) {
            return 2.778f;
        } else if (speed <= 11) {
            return 3.056f;
        } else if (speed <= 12) {
            return 3.333f;
        } else if (speed <= 13) {
            return 3.611f;
        } else if (speed <= 14) {
            return 3.889f;
        } else if (speed <= 15) {
            return 4.167f;
        }
        return 4.5f;
    }
}
