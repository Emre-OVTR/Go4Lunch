package emre.turhal.myapplicationtest;

import static emre.turhal.myapplicationtest.utils.GetTodayDate.getTodayDate;
import static emre.turhal.myapplicationtest.utils.ShowToastSnack.showToast;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import emre.turhal.myapplicationtest.manager.BookingManager;
import emre.turhal.myapplicationtest.manager.UserManager;
import emre.turhal.myapplicationtest.models.autocomplete_gson.AutocompleteResult;
import emre.turhal.myapplicationtest.models.googleplaces_gson.ResultDetails;
import emre.turhal.myapplicationtest.models.googleplaces_gson.ResultSearch;
import emre.turhal.myapplicationtest.restaurant_details.DetailsActivity;
import emre.turhal.myapplicationtest.retrofit.GooglePlaceDetailsCalls;
import emre.turhal.myapplicationtest.retrofit.GooglePlacesCalls;
import emre.turhal.myapplicationtest.retrofit.google_autocomplete.GoogleAutocompleteCalls;
import emre.turhal.myapplicationtest.utils.DistanceTo;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleAutocompleteCalls.Callbacks, GooglePlacesCalls.Callbacks, GooglePlaceDetailsCalls.Callbacks {


    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private UserManager mUserManager = UserManager.getInstance();

    private ImageView imageProfileView;
    private TextView emailUser;
    private TextView nameUser;
    private ImageView backgroundView;

    private final List<ResultDetails> mResultDetailsList = new ArrayList<>();
    public SharedViewModel mShareViewModel;
    // LIVEDATA
    public MutableLiveData<List<ResultDetails>> mLiveData = new MutableLiveData<>();
    private int resultSize;
    private BookingManager mBookingManager = BookingManager.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mShareViewModel = new ViewModelProvider(this).get(SharedViewModel.class);


        this.configureToolBar();
        this.configureDrawerLayout();
        this.configureNavigationView();
        this.configureBottomNavigationView();

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        } else {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    double currentLatitude = location.getLatitude();
                    double currentLongitude = location.getLongitude();
                    mShareViewModel.updateCurrentUserPosition(new LatLng(currentLatitude, currentLongitude));
                    GooglePlacesCalls.fetchNearbyRestaurants(this, mShareViewModel.getCurrentUserPositionFormatted());
                }
            });
        }

        this.updateUI();

    }

    private void configureDrawerLayout() {
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureToolBar() {
        toolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
    }

    private void configureNavigationView() {

        NavigationView navigationView = findViewById(R.id.activity_main_nav_view);
        final View headerLayout = navigationView.getHeaderView(0);
        imageProfileView = headerLayout.findViewById(R.id.imageProfileView);
        emailUser = headerLayout.findViewById(R.id.emailUser);
        nameUser = headerLayout.findViewById(R.id.nameUser);
        backgroundView = headerLayout.findViewById(R.id.background_header_drawer);
        navigationView.setNavigationItemSelectedListener(this);


    }


    private void onComplete(Task<QuerySnapshot> bookingTask) {

        if (!bookingTask.isSuccessful()) {
            return;
        }
        if (!bookingTask.getResult().isEmpty()) {
            Map<String, Object> extra = new HashMap<>();
            for (QueryDocumentSnapshot booking : bookingTask.getResult()) {
                extra.put("PlaceDetailResult", booking.getData().get("restaurantId"));
            }
            Intent intent = new Intent(this, DetailsActivity.class);
            for (String key : extra.keySet()) {
                String value = (String) extra.get(key);
                intent.putExtra(key, value);
            }
            startActivity(intent);
        } else {
            showToast(this, getResources().getString(R.string.no_restaurant_booked), 0);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_logout)

            mUserManager.signOut(this).addOnSuccessListener(
                    aVoid -> {
                        Intent intent = new Intent(this, LoginActivity.class);
                        startActivity(intent);
                    });
        if (id == R.id.nav_lunch)

            mBookingManager.getBooking(Objects.requireNonNull(mUserManager.getCurrentUser()).getUid(), getTodayDate()).addOnCompleteListener(this::onComplete);


        return true;

    }

    private void updateUI() {

        Glide.with(this).load(Objects.requireNonNull(mUserManager.getCurrentUser()).getPhotoUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(imageProfileView);
        nameUser.setText(mUserManager.getCurrentUser().getDisplayName());
        emailUser.setText(mUserManager.getCurrentUser().getEmail());

    }

    private void configureBottomNavigationView() {

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

    }

    @Override
    public void onResponse(@Nullable List<ResultSearch> resultSearchList) {

        mResultDetailsList.clear();

        for (int i = 0; i < Objects.requireNonNull(resultSearchList).size(); i++) {
            GooglePlaceDetailsCalls.fetchPlaceDetails(this, resultSearchList.get(i).getPlaceId());
        }
        resultSize = resultSearchList.size();

    }

    @Override
    public void onResponse(@Nullable ResultDetails resultDetails) {

        assert resultDetails != null;
        int distance = (int) Math.round(DistanceTo.distanceTo(resultDetails, this));
        resultDetails.setDistance(distance);
        if (resultDetails.getTypes().contains("restaurant")) {
            mResultDetailsList.add(resultDetails);
            mLiveData.setValue(mResultDetailsList);
        } else {
            resultSize--;
        }
        if (mResultDetailsList.size() == resultSize) {
            mLiveData.setValue(mResultDetailsList);
        }
    }

    @Override
    public void onResponse(@Nullable AutocompleteResult autoCompleteResult) {
        assert autoCompleteResult != null;
        resultSize = autoCompleteResult.getPredictions().size();
        AutoCompleteToDetails(autoCompleteResult);

    }

    private void AutoCompleteToDetails(AutocompleteResult autoCompleteResult) {
        mResultDetailsList.clear();
        for (int i = 0; i < autoCompleteResult.getPredictions().size(); i++) {
            GooglePlaceDetailsCalls.fetchPlaceDetails(this, autoCompleteResult.getPredictions().get(i).getPlaceId());
        }
    }

    public void googleAutoCompleteSearch(String query) {
        GoogleAutocompleteCalls.fetchAutoCompleteResult(this, query, mShareViewModel.getCurrentUserPositionFormatted());
    }

    @Override
    public void onFailure() {

    }

    public void searchByCurrentPosition() {
        GooglePlacesCalls.fetchNearbyRestaurants(this, mShareViewModel.getCurrentUserPositionFormatted());
    }
}

