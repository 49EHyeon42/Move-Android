package dev.ehyeon.moveapplication.data.location;

import android.annotation.SuppressLint;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class LocationRepository {

    private static final long INTERVAL_MILLIS = 1000;

    private final FusedLocationProviderClient fusedLocationProviderClient;

    private final LocationRequest locationRequest;
    private final LocationCallback locationCallback;

    private final MutableLiveData<LatLng> currentLatLngMutableLiveData;

    private final List<LatLng> latLngList;
    private final MutableLiveData<List<LatLng>> latLngListMutableLiveData;

    private Location previousLocation;
    private final MutableLiveData<Float> totalDistanceMutableLiveData;

    private final MutableLiveData<Float> topSpeedMutableLiveData;

    private final MutableLiveData<Float> currentSpeedMutableLiveData;

    private final MutableLiveData<Float> averageSpeedMutableLiveData;

    private final List<Float> speedList;
    private final MutableLiveData<List<Float>> speedListMutableLiveData;

    public LocationRepository(FusedLocationProviderClient fusedLocationProviderClient) {
        this.fusedLocationProviderClient = fusedLocationProviderClient;

        locationRequest = new LocationRequest.Builder(INTERVAL_MILLIS).build();
        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();

                if (location != null) {
                    // latitude, longitude
                    LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    currentLatLngMutableLiveData.setValue(newLatLng);

                    latLngList.add(newLatLng);
                    latLngListMutableLiveData.setValue(latLngList);

                    // distance
                    if (previousLocation != null) {
                        totalDistanceMutableLiveData.setValue(
                                totalDistanceMutableLiveData.getValue() == null ? 0f :
                                        roundToN(1, totalDistanceMutableLiveData.getValue() + previousLocation.distanceTo(location)));
                    }

                    previousLocation = location;

                    // Speed
                    float currentSpeed = roundToN(3, meterPerSecondToKilometerPerHour(location.getSpeed()));
                    float previousMaxSpeed = topSpeedMutableLiveData.getValue() == null ? 0f : topSpeedMutableLiveData.getValue();
                    float previousAverageSpeed = averageSpeedMutableLiveData.getValue() == null ? 0f : averageSpeedMutableLiveData.getValue();

                    if (currentSpeed > previousMaxSpeed) {
                        topSpeedMutableLiveData.setValue(currentSpeed);
                    }

                    currentSpeedMutableLiveData.setValue(currentSpeed);

                    averageSpeedMutableLiveData.setValue(
                            previousAverageSpeed == 0f ? currentSpeed : roundToN(3, (currentSpeed + previousAverageSpeed) / 2));

                    speedList.add(currentSpeed);
                    speedListMutableLiveData.setValue(speedList);
                }
            }
        };

        currentLatLngMutableLiveData = new MutableLiveData<>();

        latLngList = new ArrayList<>();
        latLngListMutableLiveData = new MutableLiveData<>(latLngList);

        totalDistanceMutableLiveData = new MutableLiveData<>();

        currentSpeedMutableLiveData = new MutableLiveData<>();

        topSpeedMutableLiveData = new MutableLiveData<>();

        averageSpeedMutableLiveData = new MutableLiveData<>();

        speedList = new ArrayList<>();
        speedListMutableLiveData = new MutableLiveData<>(speedList);
    }

    @SuppressLint("MissingPermission")
    public void startSensor() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    public void stopSensor() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        initAll();
    }

    public void initAll() {
        latLngList.clear();
        previousLocation = null;
        speedList.clear();
    }

    public LiveData<LatLng> getCurrentLatLngLiveData() {
        return currentLatLngMutableLiveData;
    }

    public LiveData<List<LatLng>> getLatLngListLiveData() {
        return latLngListMutableLiveData;
    }

    public LiveData<Float> getTotalDistanceLiveData() {
        return totalDistanceMutableLiveData;
    }

    public LiveData<Float> getTopSpeedLiveData() {
        return topSpeedMutableLiveData;
    }

    public LiveData<Float> getCurrentSpeedLiveData() {
        return currentSpeedMutableLiveData;
    }

    public LiveData<Float> getAverageSpeedMutableLiveData() {
        return averageSpeedMutableLiveData;
    }

    public LiveData<List<Float>> getSpeedListLiveData() {
        return speedListMutableLiveData;
    }

    private float meterPerSecondToKilometerPerHour(float f) {
        return (float) (f * 3.6);
    }

    private float roundToN(int n, float f) {
        float pow = (float) Math.pow(10, n);
        return Math.round(f * pow) / pow;
    }
}
