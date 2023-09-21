package dev.ehyeon.moveapplication.ui.sign;

import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import dev.ehyeon.moveapplication.data.remote.sign.SignService;
import dev.ehyeon.moveapplication.data.remote.sign.SignUpRequest;
import dev.ehyeon.moveapplication.util.NonNullLiveData;
import dev.ehyeon.moveapplication.util.NonNullMutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class SignUpActivityViewModel extends ViewModel {

    private static final String TAG = "SignUpActivityViewModel";

    private final SignService signService;

    private final NonNullMutableLiveData<Integer> signUpStatusCode = new NonNullMutableLiveData<>(0);

    @Inject
    public SignUpActivityViewModel(SignService signService) {
        this.signService = signService;
    }

    public void signUp(String email, String password) {
        signService.signUp(new SignUpRequest(email, password)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                signUpStatusCode.setValue(response.code());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                signUpStatusCode.setValue(-1);
            }
        });
    }

    public NonNullLiveData<Integer> getSignUpStatusCode() {
        return signUpStatusCode;
    }
}
