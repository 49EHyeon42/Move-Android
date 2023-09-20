package dev.ehyeon.moveapplication.ui.sign;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import dev.ehyeon.moveapplication.data.remote.retrofit.sign.SignService;
import dev.ehyeon.moveapplication.data.remote.retrofit.sign.SignUpRequest;
import dev.ehyeon.moveapplication.util.NonNullLiveData;
import dev.ehyeon.moveapplication.util.NonNullMutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class SignUpActivityViewModel extends ViewModel {

    private static final String TAG = "SignUpActivityViewModel";

    private final SignService signService;

    private final NonNullMutableLiveData<Boolean> succeedsSignUp = new NonNullMutableLiveData<>(false);

    @Inject
    public SignUpActivityViewModel(SignService signService) {
        this.signService = signService;
    }

    public void signUp(String email, String password) {
        signService.signUp(new SignUpRequest(email, password)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "onResponse: ");
                } else {
                    Log.i(TAG, "onResponse: " + response.code() + " " + response.errorBody());
                }

                succeedsSignUp.setValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.i(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    public NonNullLiveData<Boolean> getSucceedsSignUp() {
        return succeedsSignUp;
    }
}
