package dev.ehyeon.moveapplication.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.HashMap;
import java.util.Map;

import dagger.hilt.android.AndroidEntryPoint;
import dev.ehyeon.moveapplication.R;
import dev.ehyeon.moveapplication.databinding.ActivityMainBinding;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private Map<Integer, Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // TODO refactor
        checkPermission();

        // TODO refactor, 회전시 fragment 중복 문제 발생
        fragments = new HashMap<>();
        fragments.put(R.id.menu_home, new HomeFragment());
        fragments.put(R.id.menu_setting, new SettingFragment());

        binding.activityMainBottomNavigationView.setOnItemSelectedListener(item -> showFragment(item.getItemId()));

        showFragment(R.id.menu_home);
    }

    private void checkPermission() {
        registerForActivityResult(new ActivityResultContracts
                        .RequestMultiplePermissions(), result -> {
                    Boolean fineLocationGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarseLocationGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_COARSE_LOCATION, false);

                    if (!((fineLocationGranted != null && fineLocationGranted) || (coarseLocationGranted != null && coarseLocationGranted))) {
                        new AlertDialog.Builder(this)
                                .setTitle("Request permission")
                                .setMessage("Permission are required to use this application.")
                                .setCancelable(false)
                                .setPositiveButton("OK", (dialog, which) -> {
                                    Intent intent = new Intent(
                                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            Uri.fromParts("package", getPackageName(), null));

                                    startActivity(intent);

                                    finish();
                                }).create().show();
                    }
                }
        ).launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    private boolean showFragment(int itemId) {
        for (int id : fragments.keySet()) {
            Fragment fragment = fragments.get(id);

            if (fragment == null) {
                return false;
            }

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            if (itemId == id) {
                if (!fragment.isAdded()) {
                    fragmentTransaction.add(R.id.activityMain_frameLayout, fragment);
                }

                fragmentTransaction.show(fragment);
            } else {
                fragmentTransaction.hide(fragment);
            }

            fragmentTransaction.commit();
        }

        return true;
    }
}
