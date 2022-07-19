package emre.turhal.myapplicationtest.retrofit;

import static emre.turhal.myapplicationtest.utils.Constants.BASE_URL_GOOGLE_API;

import emre.turhal.myapplicationtest.models.googleplaces_gson.PlaceDetails;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface GooglePlaceDetailsService {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL_GOOGLE_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build();


    @GET("/maps/api/place/details/json")
    Call<PlaceDetails> getDetails(@Query("place_id") String place_id,
                                  @Query("key") String key);
}
