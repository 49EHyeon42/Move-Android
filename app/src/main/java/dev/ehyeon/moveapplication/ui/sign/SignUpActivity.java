package dev.ehyeon.moveapplication.ui.sign;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import dagger.hilt.android.AndroidEntryPoint;
import dev.ehyeon.moveapplication.R;
import dev.ehyeon.moveapplication.databinding.SignUpActivityBinding;

@AndroidEntryPoint
public class SignUpActivity extends AppCompatActivity {

    private SignUpActivityBinding binding;

    private SignUpActivityViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.sign_up_activity);

        configActionBar();

        viewModel = new ViewModelProvider(this).get(SignUpActivityViewModel.class);

        binding.signUpActivitySignUpButton.setOnClickListener(view -> {
            view.setEnabled(false);

            viewModel.signUp(binding.signUpActivityEmailEditText.getText().toString(), binding.signUpActivityPasswordEditText.getText().toString());
        });

        viewModel.getSignUpStatusCode().observe(this, signUpStatusCode -> {
            if (signUpStatusCode == 200) {
                Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                finish();
            } else if (signUpStatusCode == 400) {
                Toast.makeText(this, "부적절한 입력", Toast.LENGTH_SHORT).show();
            } else if (signUpStatusCode == 409) {
                Toast.makeText(this, "중복된 이메일", Toast.LENGTH_SHORT).show();
            } else if (signUpStatusCode == -1) {
                Toast.makeText(this, "네트워크 또는 서버 에러", Toast.LENGTH_SHORT).show();
            }

            binding.signUpActivitySignUpButton.setEnabled(true);
        });
    }

    private void configActionBar() {
        setSupportActionBar(binding.signUpActivityToolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar == null) {
            return;
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sign Up Move");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
