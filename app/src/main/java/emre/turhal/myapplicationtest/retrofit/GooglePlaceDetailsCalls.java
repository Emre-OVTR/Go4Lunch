package emre.turhal.myapplicationtest.retrofit;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

import emre.turhal.myapplicationtest.models.googleplaces_gson.PlaceDetails;
import emre.turhal.myapplicationtest.models.googleplaces_gson.ResultDetails;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// classe exécutant notre appel vers l'API googlePlaceDetailsService en arrière plan.
public class GooglePlaceDetailsCalls {

    private static final String API_KEY = "AIzaSyBaoaE_1CRPj1DA1shx-X4xoN-nTMMxAN4";

    // 2 - Public method to start fetching nearby restaurants
    public static void fetchPlaceDetails(Callbacks callbacks, String place_id) {

        // 2.1 - Create a weak reference to callback (avoid memory leaks)
        final WeakReference<Callbacks> callbacksWeakReference = new WeakReference<>(callbacks);

        // 2.2 - Get a Retrofit instance and the related endpoints
        GooglePlaceDetailsService googlePlaceDetailsService = GooglePlaceDetailsService.retrofit.create(GooglePlaceDetailsService.class);

        // 2.3 - Create the call on googlePlaceSearchService

        Call<PlaceDetails> call = googlePlaceDetailsService.getDetails(place_id, API_KEY);

        // 2.4 - Start the call
        call.enqueue(new Callback<PlaceDetails>() {

            @Override
            public void onResponse(@NotNull Call<PlaceDetails> call, @NotNull Response<PlaceDetails> response) {

                // 2.5 - Call the proper callback used in controller
                if (callbacksWeakReference.get() != null) {
                    PlaceDetails placeDetails = response.body();
                    assert placeDetails != null;
                    ResultDetails resultDetails = placeDetails.getResultDetails();
                    callbacksWeakReference.get().onResponse(resultDetails);
                }
            }

            @Override
            public void onFailure(@NotNull Call<PlaceDetails> call, @NotNull Throwable t) {

                // 2.5 - Call the proper callback used in controller
                if (callbacksWeakReference.get() != null) callbacksWeakReference.get().onFailure();
            }
        });
    }

    // 1 - Creating a callback
    public interface Callbacks {
        void onResponse(@Nullable ResultDetails resultDetails);

        void onFailure();
    }
}