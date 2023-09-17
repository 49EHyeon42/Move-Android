package dev.ehyeon.moveapplication.ui.sign;

import android.app.Dialog;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.databinding.DataBindingUtil;

import dev.ehyeon.moveapplication.R;
import dev.ehyeon.moveapplication.databinding.SignActivityBinding;

public class SignActivity extends AppCompatActivity {

    private SignActivityBinding binding;

    private Dialog signUpDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.sign_activity);

        configSignUpDialog();

        binding.signActivitySignUpButton.setOnClickListener(ignored -> signUpDialog.show());
    }

    private void configSignUpDialog() {
        signUpDialog = new Dialog(this);
        signUpDialog.setContentView(R.layout.sign_up_dialog);
        signUpDialog.getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
    }
}
