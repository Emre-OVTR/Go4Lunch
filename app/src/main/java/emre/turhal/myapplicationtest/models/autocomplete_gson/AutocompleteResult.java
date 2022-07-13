package emre.turhal.myapplicationtest.models.autocomplete_gson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AutocompleteResult {
    @SerializedName("predictions")
    @Expose
    private List<Prediction> predictions;

    public List<Prediction> getPredictions() {
        return predictions;
    }
}
