package emre.turhal.myapplicationtest.models.googleplaces_gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class OpeningHours {
    @SerializedName("open_now")
    private Boolean mOpenNow;
    @SerializedName("periods")
    private List<Period> mPeriods;


    public Boolean getOpenNow() {
        return mOpenNow;
    }

    public List<emre.turhal.myapplicationtest.models.googleplaces_gson.Period> getPeriods() {
        return mPeriods;
    }


}