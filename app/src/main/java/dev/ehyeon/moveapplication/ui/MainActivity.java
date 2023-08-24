package dev.ehyeon.moveapplication.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
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
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        fragments = new HashMap<>();
        fragments.put(R.id.menu_home, new HomeFragment());
        fragments.put(R.id.menu_profile, new SettingFragment());

        binding.activityMainBottomNavigationView.setOnItemSelectedListener(item -> showFragment(item.getItemId()));

        showFragment(R.id.menu_home);
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
