package dev.ehyeon.moveapplication.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import dagger.hilt.android.AndroidEntryPoint;
import dev.ehyeon.moveapplication.R;
import dev.ehyeon.moveapplication.databinding.ActivityMainBinding;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // TODO refactor
        checkPermission();

        binding.activityMainBottomNavigationView.setOnItemSelectedListener(item -> changeFragment(item.getItemId()));

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.activityMain_frameLayout);

        if (currentFragment == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activityMain_frameLayout, new HomeFragment(), "homeFragment")
                    .commit();
        }
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

    private boolean changeFragment(int itemId) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (itemId == R.id.menu_home) {
            showFragment(fragmentTransaction, "homeFragment");
            hideFragment(fragmentTransaction, "statisticFragment");
            hideFragment(fragmentTransaction, "settingFragment");
        } else if (itemId == R.id.menu_statistic) {
            showFragment(fragmentTransaction, "statisticFragment");
            hideFragment(fragmentTransaction, "homeFragment");
            hideFragment(fragmentTransaction, "settingFragment");
        } else if (itemId == R.id.menu_setting) {
            showFragment(fragmentTransaction, "settingFragment");
            hideFragment(fragmentTransaction, "homeFragment");
            hideFragment(fragmentTransaction, "statisticFragment");
        }

        fragmentTransaction.commit();

        return true;
    }

    private void showFragment(FragmentTransaction fragmentTransaction, String fragmentTag) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);

        if (fragment == null) {
            if (fragmentTag.equals("homeFragment")) {
                fragment = new HomeFragment();
            } else if (fragmentTag.equals("statisticFragment")) {
                fragment = new StatisticFragment();
            } else { // fragmentTag.equals("settingFragment")
                fragment = new SettingFragment();
            }
        }

        if (!fragment.isAdded()) {
            fragmentTransaction.add(R.id.activityMain_frameLayout, fragment, fragmentTag);
        }

        fragmentTransaction.show(fragment);
    }

    private void hideFragment(FragmentTransaction fragmentTransaction, String fragmentTag) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);

        if (fragment == null) {
            return;
        }

        fragmentTransaction.hide(fragment);
    }
}
