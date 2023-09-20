package dev.ehyeon.moveapplication.ui.sign;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import dev.ehyeon.moveapplication.data.remote.retrofit.sign.SignInRequest;
import dev.ehyeon.moveapplication.data.remote.retrofit.sign.SignInResponse;
import dev.ehyeon.moveapplication.data.remote.retrofit.sign.SignService;
import dev.ehyeon.moveapplication.util.NonNullLiveData;
import dev.ehyeon.moveapplication.util.NonNullMutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class SignActivityViewModel extends ViewModel {

    private static final String TAG = "SignActivityViewModel";

    private final SignService signService;

    private final NonNullMutableLiveData<Integer> signInStatusCode = new NonNullMutableLiveData<>(0);

    @Inject
    public SignActivityViewModel(SignService signService) {
        this.signService = signService;
    }

    public void signIn(Context context, String email, String password) {
        signService.signIn(new SignInRequest(email, password)).enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                if (response.code() == 200) {
                    SharedPreferences sharedPreferences = context.getSharedPreferences("move", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("access token", response.body().getAccessToken());
                    editor.apply();

                    Log.i(TAG, "onResponse: " + sharedPreferences.getString("access token", null));
                }

                signInStatusCode.setValue(response.code());
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                signInStatusCode.setValue(-1);
            }
        });
    }

    public NonNullLiveData<Integer> getSignInStatusCode() {
        return signInStatusCode;
    }
}
