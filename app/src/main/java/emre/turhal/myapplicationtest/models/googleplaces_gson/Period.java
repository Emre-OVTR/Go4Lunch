package emre.turhal.myapplicationtest.models.googleplaces_gson;


import com.google.gson.annotations.SerializedName;


public class Period {
    @SerializedName("close")
    private Close mClose;
    @SerializedName("open")
    private Open mOpen;

    public Close getClose() {
        return mClose;
    }


    public Open getOpen() {
        return mOpen;
    }


}
