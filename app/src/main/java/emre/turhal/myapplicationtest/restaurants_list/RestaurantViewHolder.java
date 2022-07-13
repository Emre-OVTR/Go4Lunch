package emre.turhal.myapplicationtest.restaurants_list;

import static emre.turhal.myapplicationtest.utils.FormatTime.formatTime;

import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import java.util.Calendar;

import emre.turhal.myapplicationtest.R;
import emre.turhal.myapplicationtest.databinding.FragmentRestaurantItemBinding;
import emre.turhal.myapplicationtest.models.googleplaces_gson.ResultDetails;
import emre.turhal.myapplicationtest.utils.DisplayRating;

public class RestaurantViewHolder extends RecyclerView.ViewHolder {

    FragmentRestaurantItemBinding mBinding;
    public static final String OPEN = "OPEN";
    public static final String CLOSED = "CLOSED";
    public static final String CLOSING_SOON = "CLOSING_SOON";
    public static final String OPENING_HOURS_NOT_KNOW = "OPENING_HOURS_NOT_KNOW";
    public static final String BASE_URL_PLACE_PHOTO = "https://maps.googleapis.com/maps/api/place/photo";
    public static final int MAX_WIDTH = 200;
    public static final int MAX_HEIGHT = 200;

    public RestaurantViewHolder(@NonNull FragmentRestaurantItemBinding itemView) {
        super(itemView.getRoot());
        mBinding = itemView;
    }

    public void updateWithData(ResultDetails resultDetails, String mLocation) {

        RequestManager glide = Glide.with(itemView);


        mBinding.nameRestaurant.setText(resultDetails.getName());
        mBinding.adressRestaurant.setText(resultDetails.getVicinity());
        mBinding.distanceRestaurant.setText(itemView.getResources().getString(R.string.unit_distance, String.valueOf(resultDetails.getDistance())));
        DisplayRating.displayRating(resultDetails, mBinding.starRestaurant);

        if (!(resultDetails.getPhotos() == null)) {
            if (!(resultDetails.getPhotos().isEmpty())) {
                glide.load(BASE_URL_PLACE_PHOTO + "?maxwidth=" + MAX_WIDTH + "&maxheight=" + MAX_HEIGHT + "&photoreference=" + resultDetails.getPhotos().get(0)
                                .getPhotoReference() + "&key=" + "AIzaSyCVhZiXlooLrQpsEFlJ62T088ClLBUFRIQ")
                        .apply(RequestOptions.centerCropTransform()).into(mBinding.itemAvatarRestaurant);

                }
            } else {
                glide.load(R.drawable.ic_no_image_available).apply(RequestOptions.centerCropTransform()).into(mBinding.itemAvatarRestaurant);
            }

        if (resultDetails.getOpeningHours() != null) {
            if (resultDetails.getOpeningHours().getOpenNow().toString().equals("false")) {
                displayOpeningHour(CLOSED, null);
            } else {
                getOpeningHoursInfo(resultDetails);
            }
        } else {
            displayOpeningHour(OPENING_HOURS_NOT_KNOW, null);
        }



        }



    private void getOpeningHoursInfo(ResultDetails resultDetails) {
        int[] daysArray = {0, 1, 2, 3, 4, 5, 6};

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minOfDay = calendar.get(Calendar.MINUTE);
        if (minOfDay < 10) {
            minOfDay = '0' + minOfDay;
        }
        String currentHourString = hourOfDay + String.valueOf(minOfDay);
        int currentHour = Integer.parseInt(currentHourString);

        for (int i = 0; i < resultDetails.getOpeningHours().getPeriods().size(); i++) {
            if (resultDetails.getOpeningHours().getPeriods().get(i).getOpen().getDay() == daysArray[day] && resultDetails.getOpeningHours().getPeriods().get(i).getClose() != null) {
                String closeHour = resultDetails.getOpeningHours().getPeriods().get(i).getClose().getTime();
                if (currentHour < Integer.parseInt(closeHour) || daysArray[day] < resultDetails.getOpeningHours().getPeriods().get(i).getClose().getDay()) {
                    int timeDifference = Integer.parseInt(closeHour) - currentHour;
                    if (timeDifference <= 30 && daysArray[day] == resultDetails.getOpeningHours().getPeriods().get(i).getClose().getDay()) {
                        displayOpeningHour(CLOSING_SOON, closeHour);
                    } else {
                        displayOpeningHour( OPEN, resultDetails.getOpeningHours().getPeriods().get(i).getClose().getTime());
                    }
                }
                break;
            }
        }
    }

    private void displayOpeningHour(String type, String hour) {
        switch (type) {
            case OPEN:
                mBinding.OpenHour.setText(itemView.getResources().getString(R.string.open_until, formatTime(itemView.getContext(), hour)));
                mBinding.OpenHour.setTextColor(itemView.getContext().getResources().getColor(R.color.colorGreen));
                mBinding.OpenHour.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                break;
            case CLOSED:
                mBinding.OpenHour.setText(R.string.restaurant_closed);
                mBinding.OpenHour.setTextColor(itemView.getContext().getResources().getColor(R.color.colorError));
                mBinding.OpenHour.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                break;
            case CLOSING_SOON:
                mBinding.OpenHour.setText(itemView.getResources().getString(R.string.closing_soon, formatTime(itemView.getContext(), hour)));
                mBinding.OpenHour.setTextColor(itemView.getContext().getResources().getColor(R.color.colorCloseSoon));
                mBinding.OpenHour.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                break;
            case OPENING_HOURS_NOT_KNOW:
                mBinding.OpenHour.setText(R.string.restaurant_opening_not_know);
                mBinding.OpenHour.setTextColor(itemView.getContext().getResources().getColor(R.color.colorCloseSoon));
                mBinding.OpenHour.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                break;
        }
    }


    }
