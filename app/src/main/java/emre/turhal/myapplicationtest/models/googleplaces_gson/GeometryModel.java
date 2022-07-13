package emre.turhal.myapplicationtest.models.googleplaces_gson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GeometryModel {
    @SerializedName("location")
    @Expose

    private LocationModel mLocationModel;

    public LocationModel getLocationModel() {
        return mLocationModel;
    }

    public void setLocationModel(LocationModel locationModel) {
        mLocationModel = locationModel;
    }
}
