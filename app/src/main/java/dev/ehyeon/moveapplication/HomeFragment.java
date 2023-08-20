package dev.ehyeon.moveapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;

import dev.ehyeon.moveapplication.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private TrackingService trackingService;
    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            trackingService = ((TrackingService.TrackingBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // unbindService 가 아닌 의도치 않은 상황으로 연결이 끊겼을 때 호출
            trackingService = null;
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentHomeBinding fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false);

        SupportMapFragment googleMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragmentHome_supportGoogleMapFragment);

        if (googleMapFragment != null) {
            googleMapFragment.getMapAsync(this);
        }

        fragmentHomeBinding.fragmentHomeTrackingServiceButton.setOnClickListener(ignored -> {
            if (trackingService == null) {
                Intent intent = new Intent(getContext(), TrackingService.class);
                requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            } else {
                requireContext().unbindService(serviceConnection);
                trackingService = null;
            }
        });

        return fragmentHomeBinding.getRoot();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.5666612, 126.9783785), 10));
            return;
        }

        googleMap.setMyLocationEnabled(true);

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
            @NonNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                return null;
            }

            @Override
            public boolean isCancellationRequested() {
                return false;
            }
        }).addOnSuccessListener(location -> googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17)));
    }
}
