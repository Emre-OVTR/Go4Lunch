package emre.turhal.myapplicationtest.retrofit.googleplace;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

import emre.turhal.myapplicationtest.BuildConfig;
import emre.turhal.myapplicationtest.models.googleplaces_gson.PlaceDetails;
import emre.turhal.myapplicationtest.models.googleplaces_gson.ResultDetails;
import emre.turhal.myapplicationtest.retrofit.googleplace.GooglePlaceDetailsService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GooglePlaceDetailsCalls {

    public static void fetchPlaceDetails(Callbacks callbacks, String place_id) {

        final WeakReference<Callbacks> callbacksWeakReference = new WeakReference<>(callbacks);
        GooglePlaceDetailsService googlePlaceDetailsService = GooglePlaceDetailsService.retrofit.create(GooglePlaceDetailsService.class);
        Call<PlaceDetails> call = googlePlaceDetailsService.getDetails(place_id, BuildConfig.api_key);
        call.enqueue(new Callback<PlaceDetails>() {

            @Override
            public void onResponse(@NotNull Call<PlaceDetails> call, @NotNull Response<PlaceDetails> response) {
                if (callbacksWeakReference.get() != null) {
                    PlaceDetails placeDetails = response.body();
                    assert placeDetails != null;
                    ResultDetails resultDetails = placeDetails.getResultDetails();
                    callbacksWeakReference.get().onResponse(resultDetails);
                }
            }

            @Override
            public void onFailure(@NotNull Call<PlaceDetails> call, @NotNull Throwable t) {
                if (callbacksWeakReference.get() != null) callbacksWeakReference.get().onFailure();
            }
        });
    }

    public interface Callbacks {
        void onResponse(@Nullable ResultDetails resultDetails);

        void onFailure();
    }
}