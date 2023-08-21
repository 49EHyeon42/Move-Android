package dev.ehyeon.moveapplication.data.stopwatch;

import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

// TODO refactor, 동시성 문제 있을 수 있음
public class StopwatchRepository {

    private final Handler handler;
    private final Runnable runnable;

    private final MutableLiveData<Integer> secondMutableLiveData;
    private final MutableLiveData<Integer> minuteMutableLiveData;
    private final MutableLiveData<Integer> hourMutableLiveData;

    private boolean isWorking;

    public StopwatchRepository() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                secondMutableLiveData.setValue((secondMutableLiveData.getValue() == null ? 0 : secondMutableLiveData.getValue()) + 1);

                if (secondMutableLiveData.getValue() >= 60) {
                    minuteMutableLiveData.setValue((minuteMutableLiveData.getValue() == null ? 0 : minuteMutableLiveData.getValue()) + 1);

                    if (minuteMutableLiveData.getValue() >= 60) {
                        hourMutableLiveData.setValue((hourMutableLiveData.getValue() == null ? 0 : hourMutableLiveData.getValue()) + 1);
                        minuteMutableLiveData.setValue(0);
                    }

                    secondMutableLiveData.setValue(0);
                }

                handler.postDelayed(this, 1000);
            }
        };

        secondMutableLiveData = new MutableLiveData<>(0);
        minuteMutableLiveData = new MutableLiveData<>(0);
        hourMutableLiveData = new MutableLiveData<>(0);

        isWorking = false;
    }

    public void startStopwatch() {
        if (isWorking) {
            return;
        }
        secondMutableLiveData.setValue(0);
        minuteMutableLiveData.setValue(0);
        hourMutableLiveData.setValue(0);

        handler.post(runnable);

        isWorking = true;
    }

    public void stopStopwatch() {
        if (!isWorking) {
            return;
        }

        handler.removeCallbacks(runnable);

        isWorking = false;
    }

    public LiveData<Integer> getSecondLiveData() {
        return secondMutableLiveData;
    }

    public LiveData<Integer> getMinuteLiveData() {
        return hourMutableLiveData;
    }

    public LiveData<Integer> getHourLiveData() {
        return hourMutableLiveData;
    }
}
