package dev.ehyeon.moveapplication.ui.sign;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.databinding.DataBindingUtil;

import dev.ehyeon.moveapplication.R;
import dev.ehyeon.moveapplication.databinding.SignActivityBinding;

public class SignActivity extends AppCompatActivity {

    private SignActivityBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.sign_activity);
    }
}
