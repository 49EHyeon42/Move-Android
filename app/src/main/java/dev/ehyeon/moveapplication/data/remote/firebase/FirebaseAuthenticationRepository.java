package dev.ehyeon.moveapplication.data.remote.firebase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

public class FirebaseAuthenticationRepository {

    private final FirebaseAuth firebaseAuth;

    private final MutableLiveData<FirebaseUser> firebaseUser;
    private final MutableLiveData<String> idToken;

    @Inject
    public FirebaseAuthenticationRepository(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;

        firebaseUser = new MutableLiveData<>();
        idToken = new MutableLiveData<>();

        firebaseAuth.addAuthStateListener(fa -> firebaseUser.setValue(fa.getCurrentUser()));
        firebaseAuth.addIdTokenListener((FirebaseAuth.IdTokenListener) fa -> {
            if (fa.getCurrentUser() == null) {
                idToken.setValue(null);
            } else {
                fa.getCurrentUser()
                        .getIdToken(true)
                        .addOnCompleteListener(
                                task -> idToken.setValue(
                                        task.isSuccessful() ? task.getResult().getToken() : null));
            }
        });
    }

    public LiveData<FirebaseUser> getFirebaseUser() {
        return firebaseUser;
    }

    public MutableLiveData<String> getIdToken() {
        return idToken;
    }
}
