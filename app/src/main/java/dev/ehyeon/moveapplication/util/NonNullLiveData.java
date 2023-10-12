package dev.ehyeon.moveapplication.util;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.Objects;

public class NonNullLiveData<T> extends LiveData<T> {

    public NonNullLiveData(@NonNull T value) {
        super(value);
    }

    @NonNull
    @Override
    public T getValue() {
        return Objects.requireNonNull(super.getValue());
    }

    @Override
    protected void postValue(@NonNull T value) {
        super.postValue(value);
    }

    @Override
    protected void setValue(@NonNull T value) {
        super.setValue(value);
    }
}
