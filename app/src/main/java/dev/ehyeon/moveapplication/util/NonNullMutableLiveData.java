package dev.ehyeon.moveapplication.util;

import androidx.annotation.NonNull;

import java.util.Objects;

public class NonNullMutableLiveData<T> extends NonNullLiveData<T> {

    public NonNullMutableLiveData(@NonNull T value) {
        super(value);
    }

    @NonNull
    @Override
    public T getValue() {
        return Objects.requireNonNull(super.getValue());
    }

    @Override
    public void postValue(@NonNull T value) {
        super.postValue(value);
    }

    @Override
    public void setValue(@NonNull T value) {
        super.setValue(value);
    }
}
