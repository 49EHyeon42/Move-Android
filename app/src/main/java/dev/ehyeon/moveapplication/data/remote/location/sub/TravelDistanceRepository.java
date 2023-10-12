package dev.ehyeon.moveapplication.data.remote.location.sub;

import android.location.Location;

import dev.ehyeon.moveapplication.util.NonNullLiveData;
import dev.ehyeon.moveapplication.util.NonNullMutableLiveData;

public class TravelDistanceRepository {

    private final NonNullMutableLiveData<Float> totalTravelDistanceNonNullMutableLiveData;

    private Location previousLocation;

    public TravelDistanceRepository() {
        totalTravelDistanceNonNullMutableLiveData = new NonNullMutableLiveData<>(0f);
    }

    public void initializeTravelDistance() {
        totalTravelDistanceNonNullMutableLiveData.setValue(0f);

        previousLocation = null;
    }

    public void updateTravelDistance(Location currentLocation) {
        if (previousLocation != null) {
            totalTravelDistanceNonNullMutableLiveData.setValue(
                    totalTravelDistanceNonNullMutableLiveData.getValue() + previousLocation.distanceTo(currentLocation));
        }

        previousLocation = currentLocation;
    }

    public NonNullLiveData<Float> getTotalTravelDistanceLiveData() {
        return totalTravelDistanceNonNullMutableLiveData;
    }
}
