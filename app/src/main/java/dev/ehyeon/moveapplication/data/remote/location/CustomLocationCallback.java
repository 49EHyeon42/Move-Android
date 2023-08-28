package dev.ehyeon.moveapplication.data.remote.location;

import android.location.Location;

import androidx.annotation.NonNull;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import dev.ehyeon.moveapplication.data.remote.location.sub.KilocalorieConsumptionRepository;
import dev.ehyeon.moveapplication.data.remote.location.sub.SpeedRepository;
import dev.ehyeon.moveapplication.data.remote.location.sub.TravelDistanceRepository;
import dev.ehyeon.moveapplication.util.NonNullMutableLiveData;

public class CustomLocationCallback extends LocationCallback {

    private final List<LatLng> latLngList;
    private final NonNullMutableLiveData<List<LatLng>> latLngListNonNullMutableLiveData;
    private final TravelDistanceRepository travelDistanceRepository;
    private final SpeedRepository speedRepository;
    private final KilocalorieConsumptionRepository kilocalorieConsumptionRepository;

    public CustomLocationCallback(List<LatLng> latLngList,
                                  NonNullMutableLiveData<List<LatLng>> latLngListNonNullMutableLiveData,
                                  TravelDistanceRepository travelDistanceRepository,
                                  SpeedRepository speedRepository,
                                  KilocalorieConsumptionRepository kilocalorieConsumptionRepository) {
        this.latLngList = latLngList;
        this.latLngListNonNullMutableLiveData = latLngListNonNullMutableLiveData;
        this.travelDistanceRepository = travelDistanceRepository;
        this.speedRepository = speedRepository;
        this.kilocalorieConsumptionRepository = kilocalorieConsumptionRepository;
    }

    @Override
    public void onLocationResult(@NonNull LocationResult locationResult) {
        Location location = locationResult.getLastLocation();

        if (location != null) {
            LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            // latLng list
            latLngList.add(newLatLng);
            latLngListNonNullMutableLiveData.setValue(latLngList);

            // distance
            travelDistanceRepository.updateTravelDistance(location);

            float currentSpeed = location.getSpeed();

            // Speed
            speedRepository.updateSpeed(currentSpeed);

            // kilocalorie, 65kg 가정
            kilocalorieConsumptionRepository.updateKilocalorieConsumption(65, currentSpeed);
        }
    }
}
