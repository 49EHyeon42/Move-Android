package dev.ehyeon.moveapplication.data.remote.location;

import android.annotation.SuppressLint;
import android.content.Context;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dev.ehyeon.moveapplication.data.remote.location.sub.CalorieConsumptionRepository;
import dev.ehyeon.moveapplication.data.remote.location.sub.SpeedRepository;
import dev.ehyeon.moveapplication.data.remote.location.sub.TravelDistanceRepository;
import dev.ehyeon.moveapplication.util.NonNullLiveData;
import dev.ehyeon.moveapplication.util.NonNullMutableLiveData;

public class LocationRepository {

    private static final long INTERVAL_MILLIS = 1000;

    private final TravelDistanceRepository travelDistanceRepository;
    private final SpeedRepository speedRepository;
    private final CalorieConsumptionRepository calorieConsumptionRepository;

    private final List<LatLng> latLngList;
    private final NonNullMutableLiveData<List<LatLng>> latLngListNonNullMutableLiveData;

    private final LocationRequest locationRequest;
    private final LocationCallback locationCallback;

    @Inject
    public LocationRepository(TravelDistanceRepository travelDistanceRepository,
                              SpeedRepository speedRepository,
                              CalorieConsumptionRepository calorieConsumptionRepository) {
        this.travelDistanceRepository = travelDistanceRepository;
        this.speedRepository = speedRepository;
        this.calorieConsumptionRepository = calorieConsumptionRepository;

        latLngList = new ArrayList<>();
        latLngListNonNullMutableLiveData = new NonNullMutableLiveData<>(latLngList);

        locationRequest = new LocationRequest.Builder(INTERVAL_MILLIS).build();
        locationCallback = new CustomLocationCallback(latLngList, latLngListNonNullMutableLiveData,
                travelDistanceRepository,
                speedRepository,
                calorieConsumptionRepository);
    }

    @SuppressLint("MissingPermission")
    public void startLocationUpdate(Context context) {
        initializeAll();

        LocationServices.getFusedLocationProviderClient(context).requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void initializeAll() {
        latLngList.clear();
        travelDistanceRepository.initializeTravelDistance();
        speedRepository.initializeSpeed();
        calorieConsumptionRepository.initializeCalorieConsumption();
    }

    public void stopLocationUpdate(Context context) {
        LocationServices.getFusedLocationProviderClient(context).removeLocationUpdates(locationCallback);
    }

    public NonNullLiveData<List<LatLng>> getLatLngListLiveData() {
        return latLngListNonNullMutableLiveData;
    }

    public NonNullLiveData<Float> getTotalTravelDistanceLiveData() {
        return travelDistanceRepository.getTotalTravelDistanceLiveData();
    }

    public NonNullLiveData<Float> getAverageSpeedLiveData() {
        return speedRepository.getAverageSpeedLiveData();
    }

    public NonNullLiveData<Float> getCalorieConsumptionLiveData() {
        return calorieConsumptionRepository.getCalorieConsumptionLiveData();
    }
}
