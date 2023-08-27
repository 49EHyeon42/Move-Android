package dev.ehyeon.moveapplication.data.local.stopwatch;

import android.os.Handler;

import androidx.lifecycle.LiveData;

import dev.ehyeon.moveapplication.util.NonNullMutableLiveData;

public class StopwatchRepository {

    private final Handler handler;

    private final Runnable runnable;

    private final NonNullMutableLiveData<Integer> secondNonNullMutableLiveData;

    public StopwatchRepository() {
        handler = new Handler();

        runnable = new Runnable() {

            @Override
            public void run() {
                secondNonNullMutableLiveData.setValue(secondNonNullMutableLiveData.getValue() + 1);

                handler.postDelayed(this, 1000);
            }
        };

        secondNonNullMutableLiveData = new NonNullMutableLiveData<>(0);
    }

    public void startStopwatch() {
        initializeSecond();

        handler.post(runnable);
    }

    public void stopStopwatch() {
        handler.removeCallbacks(runnable);
    }

    private void initializeSecond() {
        secondNonNullMutableLiveData.setValue(0);
    }

    public LiveData<Integer> getSecondLiveData() {
        return secondNonNullMutableLiveData;
    }
}
