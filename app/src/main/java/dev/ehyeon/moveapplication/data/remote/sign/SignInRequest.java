package dev.ehyeon.moveapplication.data.remote.sign;

import androidx.annotation.NonNull;

public class SignInRequest {

    @NonNull
    private final String email;

    @NonNull
    private final String password;

    public SignInRequest(@NonNull String email, @NonNull String password) {
        this.email = email;
        this.password = password;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    @NonNull
    public String getPassword() {
        return password;
    }
}
