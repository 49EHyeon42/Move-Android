package dev.ehyeon.moveapplication.data.remote.location.sub;

import dev.ehyeon.moveapplication.util.NonNullLiveData;
import dev.ehyeon.moveapplication.util.NonNullMutableLiveData;

public class KilocalorieConsumptionRepository {

    private final NonNullMutableLiveData<Float> kilocalorieConsumptionNonNullMutableLiveData;

    public KilocalorieConsumptionRepository() {
        kilocalorieConsumptionNonNullMutableLiveData = new NonNullMutableLiveData<>(0f);
    }

    public void initializeKilocalorieConsumption() {
        kilocalorieConsumptionNonNullMutableLiveData.setValue(0f);
    }

    public void updateKilocalorieConsumption(float weight, float currentSpeed) {
        // 1초당 kcal 소모량 = MET * 1초당 산소 섭취(0.058ml) * 몸무게 / 1000 * 5
        kilocalorieConsumptionNonNullMutableLiveData.setValue(weight * getMet(currentSpeed) * 0.00029f);
    }

    private float getMet(float currentSpeed) {
        if (currentSpeed < 0) {
            return 0;
        } else if (currentSpeed <= 3) {
            return 0.833f;
        } else if (currentSpeed <= 4) {
            return 1.111f;
        } else if (currentSpeed <= 5) {
            return 1.389f;
        } else if (currentSpeed <= 6) {
            return 1.667f;
        } else if (currentSpeed <= 7) {
            return 1.944f;
        } else if (currentSpeed <= 8) {
            return 2.222f;
        } else if (currentSpeed <= 9) {
            return 2.5f;
        } else if (currentSpeed <= 10) {
            return 2.778f;
        } else if (currentSpeed <= 11) {
            return 3.056f;
        } else if (currentSpeed <= 12) {
            return 3.333f;
        } else if (currentSpeed <= 13) {
            return 3.611f;
        } else if (currentSpeed <= 14) {
            return 3.889f;
        } else if (currentSpeed <= 15) {
            return 4.167f;
        }
        return 4.5f;
    }

    public NonNullLiveData<Float> getKilocalorieConsumptionLiveData() {
        return kilocalorieConsumptionNonNullMutableLiveData;
    }
}
