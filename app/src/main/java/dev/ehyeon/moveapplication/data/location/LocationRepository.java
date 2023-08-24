package dev.ehyeon.moveapplication.data.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import dev.ehyeon.moveapplication.data.ContextRepository;

public class LocationRepository implements ContextRepository {

    private static final long INTERVAL_MILLIS = 3000;

    private final MutableLiveData<LatLng> currentLatLngMutableLiveData;

    private final List<LatLng> latLngList;
    private final MutableLiveData<List<LatLng>> latLngListMutableLiveData;

    private Location previousLocation;
    private final MutableLiveData<Float> totalDistanceMutableLiveData;

    private final MutableLiveData<Float> averageSpeedMutableLiveData;

    private final List<Float> speedList;
    private final MutableLiveData<List<Float>> speedListMutableLiveData;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    public LocationRepository() {
        currentLatLngMutableLiveData = new MutableLiveData<>();

        latLngList = new ArrayList<>();
        latLngListMutableLiveData = new MutableLiveData<>(latLngList);

        totalDistanceMutableLiveData = new MutableLiveData<>();

        averageSpeedMutableLiveData = new MutableLiveData<>();

        speedList = new ArrayList<>();
        speedListMutableLiveData = new MutableLiveData<>(speedList);
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
                    // latitude, longitude
                    LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    currentLatLngMutableLiveData.setValue(newLatLng);

                    latLngList.add(newLatLng);
                    latLngListMutableLiveData.setValue(latLngList);

                    // distance
                    if (previousLocation != null) {
                        totalDistanceMutableLiveData.setValue(
                                totalDistanceMutableLiveData.getValue() == null ? 0 :
                                        totalDistanceMutableLiveData.getValue() + previousLocation.distanceTo(location));
                    }

                    previousLocation = location;

                    // Speed
                    float currentSpeed = meterPerSecondToKilometerPerHour(location.getSpeed());
                    float previousAverageSpeed = averageSpeedMutableLiveData.getValue() == null ? 0 : averageSpeedMutableLiveData.getValue();

                    averageSpeedMutableLiveData.setValue(previousAverageSpeed == 0 ? currentSpeed : (currentSpeed + previousAverageSpeed) / 2);

                    speedList.add(currentSpeed);
                    speedListMutableLiveData.setValue(speedList);
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

    public LiveData<Float> getAverageSpeedMutableLiveData() {
        return averageSpeedMutableLiveData;
    }

    public LiveData<List<Float>> getSpeedListLiveData() {
        return speedListMutableLiveData;
    }

    private float meterPerSecondToKilometerPerHour(float f) {
        return f * 3.6f;
    }
}
