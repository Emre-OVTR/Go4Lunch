package emre.turhal.myapplicationtest.ui.map;

import static android.content.ContentValues.TAG;

import static emre.turhal.myapplicationtest.utils.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static emre.turhal.myapplicationtest.utils.ShowToastSnack.showToast;
import static emre.turhal.myapplicationtest.utils.UpdateMarkers.updateMarkers;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

import emre.turhal.myapplicationtest.ui.BaseFragment;
import emre.turhal.myapplicationtest.ui.restaurant_details.DetailsActivity;
import emre.turhal.myapplicationtest.ui.MainActivity;
import emre.turhal.myapplicationtest.R;
import emre.turhal.myapplicationtest.databinding.FragmentMapBinding;


public class MapFragment extends BaseFragment  implements OnMapReadyCallback {


    private FragmentMapBinding mBinding;
    private GoogleMap googleMap;
    private boolean locationPermissionGranted;
    private Location lastKnownLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private MainActivity mMainActivity;
    private String mLocation;


    public MapFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = FragmentMapBinding.inflate(getLayoutInflater());
        mMainActivity = (MainActivity) requireActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = mBinding.getRoot();
        mMainActivity.mLiveData.observe(getViewLifecycleOwner(), resultDetails -> getDeviceLocation());

        mBinding.fragmentMapFloatingActionBtn.setOnClickListener(v -> {
            getLocationPermission();
            getDeviceLocation();
        });
        requireActivity().setTitle(getString(R.string.Title_Hungry));
        setHasOptionsMenu(true);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mMapFragment != null) {
            mMapFragment.getMapAsync(this);
        }

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        this.googleMap = googleMap;

        getLocationPermission();
        updateLocationUi();
        getDeviceLocation();
    }

    private void getLocationPermission() {

        if (ContextCompat.checkSelfPermission(requireActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void updateLocationUi(){
        if (googleMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                googleMap.setOnMarkerClickListener(this::onClickMarker);
            } else {
                googleMap.setMyLocationEnabled(false);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        if (lastKnownLocation != null) {
                            lastKnownLocation = task.getResult();

                            mLocation = lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude();
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lastKnownLocation.getLatitude(),
                                            lastKnownLocation.getLongitude()), 15));
                        }else {
                            if (mMainActivity.mShareViewModel.getCurrentUserPosition() != null) {
                                defaultLocation = mMainActivity.mShareViewModel.getCurrentUserPosition();
                            }
                            googleMap.getUiSettings().setMyLocationButtonEnabled(false);

                            googleMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, 15));
                            googleMap.getUiSettings().setMyLocationButtonEnabled(false);


                            updateMarkers(googleMap, mMainActivity);
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                        googleMap.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, 15));
                        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private boolean onClickMarker(Marker marker) {
        if (marker.getTag() != null) {
            Log.e(TAG, "onClickMarker: " + marker.getTag());
            Intent intent = new Intent(getActivity(), DetailsActivity.class);
            intent.putExtra("PlaceDetailResult", marker.getTag().toString());
            startActivity(intent);
            return true;
        } else {
            Log.e(TAG, "onClickMarker: ERROR NO TAG");
            return false;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.activity_main_appbar, menu);
        SearchView mSearchView = new SearchView(Objects.requireNonNull(((MainActivity) requireContext()).getSupportActionBar()).getThemedContext());
        MenuItem item = menu.findItem(R.id.menu_activity_main_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        item.setActionView(mSearchView);
        mSearchView.setQueryHint(getResources().getString(R.string.search_hint));
        SearchManager mSearchManager = (SearchManager) requireContext().getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(mSearchManager.getSearchableInfo(((MainActivity) requireContext()).getComponentName()));
        mSearchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 2) {
                    mMainActivity.googleAutoCompleteSearch(query);
                    mSearchView.clearFocus();
                } else {
                    showToast(getContext(), getResources().getString(R.string.search_too_short), 1);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (query.length() > 2) {
                    mMainActivity.googleAutoCompleteSearch(query);
                } else if (query.length() == 0) {
                    mMainActivity.searchByCurrentPosition();
                }
                return false;
            }
        });
    }
}