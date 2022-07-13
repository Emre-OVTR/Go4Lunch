package emre.turhal.myapplicationtest.models.googleplaces_gson;

import com.google.gson.annotations.SerializedName;

public class ResultSearch {

    @SerializedName("icon")
    private String mIcon;
    @SerializedName("id")
    private String mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("place_id")
    private String mPlaceId;


    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPlaceId() {
        return mPlaceId;
    }

}
