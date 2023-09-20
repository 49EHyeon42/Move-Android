package dev.ehyeon.moveapplication.ui.sign;

import android.content.Intent;
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
import dev.ehyeon.moveapplication.data.remote.retrofit.sign.SignService;
import dev.ehyeon.moveapplication.databinding.SignActivityBinding;

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

            viewModel.signIn(binding.signActivityEmailEditText.getText().toString(), binding.signActivityPasswordEditText.getText().toString());
        });

        binding.signActivitySignUpButton.setOnClickListener(ignored -> startActivity(new Intent(this, SignUpActivity.class)));

        viewModel.getSignInStatusCode().observe(this, signInStatusCode -> {
            if (signInStatusCode == 200) {
                Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                finish();
            } else if (signInStatusCode == 400) {
                Toast.makeText(this, "부적절한 입력", Toast.LENGTH_SHORT).show();
            } else if (signInStatusCode == 401) {
                Toast.makeText(this, "등록된 계정 없음", Toast.LENGTH_SHORT).show();
            } else if (signInStatusCode == -1) {
                Toast.makeText(this, "네트워크 또는 서버 에러", Toast.LENGTH_SHORT).show();
            }

            binding.signActivitySignInButton.setEnabled(true);
        });
    }
}
