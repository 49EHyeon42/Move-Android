package dev.ehyeon.moveapplication.ui.sign;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import dev.ehyeon.moveapplication.R;
import dev.ehyeon.moveapplication.data.remote.sign.SignService;
import dev.ehyeon.moveapplication.databinding.SignActivityBinding;
import dev.ehyeon.moveapplication.ui.MainActivity;

@AndroidEntryPoint
public class SignActivity extends AppCompatActivity {

    @Inject
    protected SignService signService;

    private SignActivityBinding binding;

    private SignActivityViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.sign_activity);

        viewModel = new ViewModelProvider(this).get(SignActivityViewModel.class);

        binding.signActivitySignInButton.setOnClickListener(view -> {
            view.setEnabled(false);

            viewModel.signIn(this, binding.signActivityEmailEditText.getText().toString(), binding.signActivityPasswordEditText.getText().toString());
        });

        binding.signActivitySignUpButton.setOnClickListener(ignored -> startActivity(new Intent(this, SignUpActivity.class)));

        viewModel.getSignInStatusCode().observe(this, signInStatusCode -> {
            if (signInStatusCode == 200) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(intent);
            } else if (signInStatusCode == 400) {
                Toast.makeText(this, "부적절한 입력", Toast.LENGTH_SHORT).show();
            } else if (signInStatusCode == 401) {
                Toast.makeText(this, "등록된 계정 없음", Toast.LENGTH_SHORT).show();
            } else if (signInStatusCode == -1) {
                Toast.makeText(this, "네트워크 또는 서버 에러", Toast.LENGTH_SHORT).show();
            }

            binding.signActivitySignInButton.setEnabled(true);
        });

        SharedPreferences sharedPreferences = getSharedPreferences("move", Context.MODE_PRIVATE);

        if (sharedPreferences.getString("access token", null) != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
        }
    }
}
