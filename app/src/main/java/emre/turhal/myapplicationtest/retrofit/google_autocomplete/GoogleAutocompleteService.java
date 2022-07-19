package emre.turhal.myapplicationtest.retrofit.google_autocomplete;

import static emre.turhal.myapplicationtest.utils.Constants.BASE_URL_GOOGLE_API;

import emre.turhal.myapplicationtest.models.autocomplete_gson.AutocompleteResult;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleAutocompleteService {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL_GOOGLE_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build();


    @GET("/maps/api/place/autocomplete/json")
    Call<AutocompleteResult> getAutoComplete(@Query("input") String input,
                                             @Query("types") String types,
                                             @Query("language") String language,
                                             @Query("location") String location,
                                             @Query("radius") int radius,
                                             @Query("strictbounds") boolean strictbounds,
                                             @Query("key") String key);
}
