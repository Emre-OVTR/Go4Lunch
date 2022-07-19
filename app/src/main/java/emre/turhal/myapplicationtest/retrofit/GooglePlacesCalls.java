package emre.turhal.myapplicationtest.retrofit;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.List;

import emre.turhal.myapplicationtest.models.googleplaces_gson.ResultSearch;
import emre.turhal.myapplicationtest.models.googleplaces_gson.SearchPlace;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GooglePlacesCalls {

        public static void fetchNearbyRestaurants(Callbacks callbacks, String location) {

            final WeakReference<Callbacks> callbacksWeakReference = new WeakReference<>(callbacks);
            GooglePlacesService googlePlacesService = GooglePlacesService.retrofit.create(GooglePlacesService.class);
            Call<SearchPlace> call = googlePlacesService.getNearbyRestaurants(location, "distance", "restaurant", "AIzaSyCVhZiXlooLrQpsEFlJ62T088ClLBUFRIQ");
            call.enqueue(new Callback<SearchPlace>() {

                @Override
                public void onResponse(@NotNull Call<SearchPlace> call, @NotNull Response<SearchPlace> response) {
                    if (callbacksWeakReference.get() != null) {
                        SearchPlace searchPlace = response.body();
                        assert searchPlace != null;
                        List<ResultSearch> resultSearchList = searchPlace.getResultSearches();
                        callbacksWeakReference.get().onResponse(resultSearchList);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<SearchPlace> call, @NotNull Throwable t) {
                    if (callbacksWeakReference.get() != null) callbacksWeakReference.get().onFailure();
                }
            });
        }

        public interface Callbacks {

            void onResponse(@Nullable List<ResultSearch> resultSearchList);

            void onFailure();
        }
}

