package emre.turhal.myapplicationtest.models.googleplaces_gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchPlace {

    @SerializedName("results")
    private List<ResultSearch> mResultSearches;

    public List<ResultSearch> getResultSearches() {
        return mResultSearches;
    }
}
