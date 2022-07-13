package emre.turhal.myapplicationtest.restaurant_details;

import static emre.turhal.myapplicationtest.utils.DisplayRating.displayRating;
import static emre.turhal.myapplicationtest.utils.GetTodayDate.getTodayDate;
import static emre.turhal.myapplicationtest.utils.ShowToastSnack.showToast;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import emre.turhal.myapplicationtest.R;
import emre.turhal.myapplicationtest.databinding.ActivityDetailsBinding;
import emre.turhal.myapplicationtest.manager.BookingManager;
import emre.turhal.myapplicationtest.manager.UserManager;
import emre.turhal.myapplicationtest.models.Workmate;
import emre.turhal.myapplicationtest.models.googleplaces_gson.ResultDetails;
import emre.turhal.myapplicationtest.retrofit.GooglePlaceDetailsCalls;

public class DetailsActivity extends AppCompatActivity implements GooglePlaceDetailsCalls.Callbacks, View.OnClickListener {

    ActivityDetailsBinding mBinding;
    private final List<Workmate> mWorkmates = new ArrayList<>();
    private ResultDetails mResultDetails;
    private Restaurant_Details_RecyclerViewAdapter mAdapter;
    public static final int MAX_WIDTH_LARGE = 400;
    public static final int MAX_HEIGHT_LARGE = 400;
    public static final String BASE_URL_PLACE_PHOTO = "https://maps.googleapis.com/maps/api/place/photo";
    private BookingManager mBookingManager = BookingManager.getInstance();
    private UserManager mUserManager = UserManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mBinding = ActivityDetailsBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);
        configureToolbar();
        configureRecyclerView();
        requestRetrofit();
        setFloatingListener();

    }

    private void configureRecyclerView() {
        this.mAdapter = new Restaurant_Details_RecyclerViewAdapter(mWorkmates);
        this.mBinding.restaurantRecyclerView.setAdapter(mAdapter);
        this.mBinding.restaurantRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void Update_Booking_RecyclerView(String restaurantPlaceId) {
        mWorkmates.clear();
        mBookingManager.getTodayBooking(restaurantPlaceId, getTodayDate()).addOnCompleteListener(restaurantTask -> {
            if (restaurantTask.isSuccessful()) {
                if (restaurantTask.getResult().isEmpty()) {
                    mAdapter.notifyDataSetChanged();
                } else {
                    for (QueryDocumentSnapshot restaurant : restaurantTask.getResult()) {
                        mUserManager.getWorkmate(Objects.requireNonNull(restaurant.getData().get("workmateUid")).toString()).addOnCompleteListener(workmateTask -> {
                            if (workmateTask.isSuccessful()) {
                                String name = Objects.requireNonNull(workmateTask.getResult().getData()).get("name").toString();
                                String uid = Objects.requireNonNull(workmateTask.getResult().getData().get("uid")).toString();
                                String urlPicture = Objects.requireNonNull(workmateTask.getResult().getData().get("urlPicture")).toString();
                                Workmate workmateToAdd = new Workmate(urlPicture,name, uid );
                                mWorkmates.add(workmateToAdd);
                            }
                            mAdapter.notifyDataSetChanged();
                        });
                    }
                }
            }
        });
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

    @Nullable
    private FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    private void requestRetrofit() {
        String result = getIntent().getStringExtra("PlaceDetailResult");
        Log.e("TAG", "retrieveObject: " + result);
        GooglePlaceDetailsCalls.fetchPlaceDetails(this, result);
    }

    private void updateUI(ResultDetails resultDetails) {


        if (getCurrentUser() != null) {
            checkBooked(resultDetails.getName(), resultDetails.getPlaceId(), getCurrentUser().getUid(), false);

        }


        if (resultDetails.getPhotos() != null) {
            Glide.with(this).load(BASE_URL_PLACE_PHOTO + "?maxwidth=" + MAX_WIDTH_LARGE + "&maxheight=" + MAX_HEIGHT_LARGE + "&photoreference=" + resultDetails.getPhotos().get(0)
                    .getPhotoReference() + "&key=" + "AIzaSyCVhZiXlooLrQpsEFlJ62T088ClLBUFRIQ").into(mBinding.imageView);


        } else {
            Glide.with(mBinding.imageView).load(R.drawable.ic_no_image_available)
                    .apply(new RequestOptions()

                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .override(Target.SIZE_ORIGINAL))
                    .into(mBinding.imageView);
        }


        mBinding.restaurantName.setText(resultDetails.getName());
        mBinding.restaurantAddress.setText(resultDetails.getVicinity());
        Update_Booking_RecyclerView(mResultDetails.getPlaceId());
        displayRating(resultDetails, mBinding.itemRatingBar);

    }

    private void setFloatingListener() {
        mBinding.floatingActionButton.setOnClickListener(view -> bookThisRestaurant());
    }

    private void bookThisRestaurant() {
        String workmateUid = Objects.requireNonNull(getCurrentUser()).getUid();
        String restaurantPlaceId = mResultDetails.getPlaceId();
        String restaurantName = mResultDetails.getName();
        checkBooked(restaurantName, restaurantPlaceId, workmateUid, true);
    }

    private void checkBooked(String restaurantName, String restaurantPlaceId, String workmateUid, Boolean tryingToBook) {
        mBookingManager.getBooking(workmateUid, getTodayDate()).addOnCompleteListener(restaurantTask -> {
            if (restaurantTask.isSuccessful()) {
                if (restaurantTask.getResult().size() == 1) {
                    for (QueryDocumentSnapshot restaurant : restaurantTask.getResult()) {
                        if (Objects.equals(restaurant.getData().get("restaurantName"), restaurantName)) {
                            displayFloating((R.drawable.ic_clear_black_24dp), getResources().getColor(R.color.colorError));
                            if (tryingToBook) {
                                Booking_Firebase(restaurantName, restaurantPlaceId, workmateUid, restaurant.getId(), false, false, true);
                                showToast(this, getResources().getString(R.string.cancel_booking), Toast.LENGTH_SHORT);
                            }
                        } else {
                            displayFloating((R.drawable.ic_check_circle_black_24dp), getResources().getColor(R.color.colorGreen));
                            if (tryingToBook) {
                                Booking_Firebase(restaurantName, restaurantPlaceId, workmateUid, restaurant.getId(), false, true, false);
                                showToast(this, getResources().getString(R.string.modify_booking), Toast.LENGTH_SHORT);
                            }
                        }
                    }
                } else {
                    displayFloating((R.drawable.ic_check_circle_black_24dp), getResources().getColor(R.color.colorGreen));
                    if (tryingToBook) {
                        Booking_Firebase(restaurantName, restaurantPlaceId, workmateUid, null, true, false, false);
                        showToast(this, getResources().getString(R.string.new_booking), Toast.LENGTH_SHORT);
                    }
                }
            }
        });
    }

    private void Booking_Firebase(String restaurantName, String restaurantPlaceId, String workmateUid, String bookingId, boolean toCreate, boolean toUpdate, boolean toDelete) {
        if (toUpdate) {
            mBookingManager.deleteBooking(bookingId);
            mBookingManager.createBooking(restaurantName, restaurantPlaceId, workmateUid, getTodayDate()).addOnFailureListener(onFailureListener());
            displayFloating((R.drawable.ic_clear_black_24dp), getResources().getColor(R.color.colorError));

        } else {
            if (toCreate) {
                mBookingManager.createBooking(restaurantName, restaurantPlaceId, workmateUid, getTodayDate()).addOnFailureListener(onFailureListener());
                displayFloating((R.drawable.ic_clear_black_24dp), getResources().getColor(R.color.colorError));
            } else if (toDelete) {
                mBookingManager.deleteBooking(bookingId);
                displayFloating((R.drawable.ic_check_circle_black_24dp), getResources().getColor(R.color.colorGreen));
            }
        }
        Update_Booking_RecyclerView(mResultDetails.getPlaceId());

    }

    protected OnFailureListener onFailureListener() {
        return e -> Toast.makeText(getApplicationContext(), getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();
    }

    private void displayFloating(int icon, int color) {
        Drawable mDrawable = Objects.requireNonNull(ContextCompat.getDrawable(getBaseContext(), icon)).mutate();
        mDrawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        mBinding.floatingActionButton.setImageDrawable(mDrawable);
    }


    @Override
    public void onResponse(@Nullable ResultDetails resultDetails) {
        mResultDetails = resultDetails;
        updateUI(resultDetails);

    }

    @Override
    public void onFailure() {

    }

    @Override
    public void onClick(View v) {

    }
}