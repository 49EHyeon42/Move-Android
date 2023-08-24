package dev.ehyeon.moveapplication.data.stopwatch;

import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class StopwatchRepository {

    private final Handler handler;

    private final Runnable runnable;

    private final MutableLiveData<Integer> secondMutableLiveData;

    public StopwatchRepository() {
        handler = new Handler();

        runnable = new Runnable() {

            @Override
            public void run() {
                secondMutableLiveData.setValue((secondMutableLiveData.getValue() == null ? 0 : secondMutableLiveData.getValue()) + 1);

                handler.postDelayed(this, 1000);
            }
        };

        secondMutableLiveData = new MutableLiveData<>(0);
    }

    public void startStopwatch() {
        secondMutableLiveData.setValue(0);

        handler.post(runnable);
    }

    public void stopStopwatch() {
        handler.removeCallbacks(runnable);
    }

    public LiveData<Integer> getSecondLiveData() {
        return secondMutableLiveData;
    }
}
