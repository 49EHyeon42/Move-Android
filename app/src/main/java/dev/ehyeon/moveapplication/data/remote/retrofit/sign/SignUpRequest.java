package dev.ehyeon.moveapplication.data.remote.retrofit.sign;

import androidx.annotation.NonNull;

public class SignUpRequest {

    @NonNull
    private final String email;

    @NonNull
    private final String password;

    public SignUpRequest(@NonNull String email, @NonNull String password) {
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
