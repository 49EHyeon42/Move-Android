package dev.ehyeon.moveapplication.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import dev.ehyeon.moveapplication.R;
import dev.ehyeon.moveapplication.data.remote.firebase.FirebaseAuthenticationRepository;
import dev.ehyeon.moveapplication.databinding.FragmentSettingBinding;

public class SettingFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuthenticationRepository firebaseAuthenticationRepository;

    private FragmentSettingBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthenticationRepository = new FirebaseAuthenticationRepository(FirebaseAuth.getInstance());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingBinding.inflate(inflater, container, false);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            firebaseAuth.signInWithCredential(
                                    GoogleAuthProvider.getCredential(
                                            GoogleSignIn.getSignedInAccountFromIntent(result.getData())
                                                    .getResult(ApiException.class).getIdToken(), null));

                            binding.fragmentSettingSignInButton.setEnabled(false);
                        } catch (ApiException e) {
                            Toast.makeText(requireContext(), "Failed Google Login", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        binding.fragmentSettingSignInButton.setOnClickListener(ignored -> activityResultLauncher.launch(
                GoogleSignIn.getClient(requireContext(),
                        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken(getString(R.string.default_web_client_id))
                                .requestEmail()
                                .build()).getSignInIntent()));

        firebaseAuthenticationRepository.getFirebaseUser().observe(getViewLifecycleOwner(), firebaseUser ->
                binding.fragmentSettingEmailTextView.setText(firebaseUser == null ? "null" : firebaseUser.getEmail()));

        return binding.getRoot();
    }
}
