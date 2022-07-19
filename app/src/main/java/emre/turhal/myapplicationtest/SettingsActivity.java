package emre.turhal.myapplicationtest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import emre.turhal.myapplicationtest.databinding.ActivitySettingsBinding;
import emre.turhal.myapplicationtest.manager.UserManager;
import emre.turhal.myapplicationtest.utils.NotificationManager;

public class SettingsActivity extends AppCompatActivity {

    protected SharedViewModel mSharedViewModel;
    private NotificationManager mNotificationManager;
    private ActivitySettingsBinding mBinding;
    private UserManager mUserManager = UserManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySettingsBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);
        mSharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        configureToolbar();
        retrieveUserSettings();
        setListenerAndFilters();
        createNotificationManager();

    }

    private void configureToolbar() {
        setSupportActionBar(mBinding.activityMainToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void retrieveUserSettings() {
        if (getCurrentUser() != null) {
            mUserManager.getWorkmateCollection().document(getCurrentUser().getUid()).addSnapshotListener((documentSnapshot, e) -> {
                if (e != null) {
                    Log.e("TAG", "Listen failed.", e);
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Log.e("TAG", "Current data: " + documentSnapshot.getData());
                    mBinding.settingsSwitch.setChecked(Objects.equals(Objects.requireNonNull(documentSnapshot.getData()).get("notification"), true));
                    if (Objects.equals(documentSnapshot.getData().get("notification"), true)) {
                        mNotificationManager.createNotification();
                    }
                } else {
                    Log.e("TAG", "Current data: null");
                }
            });
        }
    }

    private void setListenerAndFilters() {
        mBinding.settingsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed() && buttonView.isChecked()) {
                mUserManager.updateUserSettings(Objects.requireNonNull(getCurrentUser()).getUid(), true);
                Toast.makeText(getApplication(), "NOTIFICATIONS ON", Toast.LENGTH_SHORT).show();
                mNotificationManager.createNotification();


            } else if (!buttonView.isChecked()) {
                mUserManager.updateUserSettings(Objects.requireNonNull(getCurrentUser()).getUid(), false);
                Toast.makeText(getApplication(), "NOTIFICATIONS OFF", Toast.LENGTH_SHORT).show();
                mNotificationManager.cancelAlarm();
            }
        });
    }

    private void createNotificationManager() {
        mNotificationManager = new NotificationManager(getBaseContext());
    }

    @Nullable
    private FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }
}