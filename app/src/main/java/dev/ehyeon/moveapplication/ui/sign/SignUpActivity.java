package dev.ehyeon.moveapplication.ui.sign;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import dev.ehyeon.moveapplication.R;
import dev.ehyeon.moveapplication.databinding.SignUpActivityBinding;

public class SignUpActivity extends AppCompatActivity {

    private SignUpActivityBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.sign_up_activity);

        configActionBar();
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
