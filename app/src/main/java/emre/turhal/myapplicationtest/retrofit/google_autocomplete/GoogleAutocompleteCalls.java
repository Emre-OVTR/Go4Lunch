package emre.turhal.myapplicationtest.retrofit.google_autocomplete;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

import emre.turhal.myapplicationtest.models.autocomplete_gson.AutocompleteResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoogleAutocompleteCalls {

    private static final String types = "establishment";
    static String language = "fr";
    public static final int RADIUS = 6800;

    public static void fetchAutoCompleteResult(GoogleAutocompleteCalls.Callbacks callbacks, String input, String location) {

        final WeakReference<Callbacks> callbacksWeakReference = new WeakReference<>(callbacks);

        GoogleAutocompleteService googleAutoComplete = GoogleAutocompleteService.retrofit.create(GoogleAutocompleteService.class);

        Call<AutocompleteResult> call = googleAutoComplete.getAutoComplete(input, types, language, location, RADIUS, true, "AIzaSyCVhZiXlooLrQpsEFlJ62T088ClLBUFRIQ");
        call.enqueue(new Callback<AutocompleteResult>() {

            @Override
            public void onResponse(@NotNull Call<AutocompleteResult> call, @NotNull Response<AutocompleteResult> response) {
                if (callbacksWeakReference.get() != null)
                    callbacksWeakReference.get().onResponse(response.body());
            }

            @Override
            public void onFailure(@NotNull Call<AutocompleteResult> call, @NotNull Throwable t) {
                if (callbacksWeakReference.get() != null) callbacksWeakReference.get().onFailure();
            }
        });
    }

    public interface Callbacks {
        void onResponse(@Nullable AutocompleteResult autoCompleteResult);

        void onFailure();
    }
}
