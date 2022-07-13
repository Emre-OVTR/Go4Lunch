package emre.turhal.myapplicationtest.utils;

import java.util.Objects;

import emre.turhal.myapplicationtest.MainActivity;
import emre.turhal.myapplicationtest.models.googleplaces_gson.ResultDetails;

public class DistanceTo {


    public static double distanceTo(ResultDetails resultDetails, MainActivity mMainActivity) {
        double lat1Rad = Math.toRadians(Objects.requireNonNull(mMainActivity.mShareViewModel.currentUserPosition.getValue()).latitude);
        double lat2Rad = Math.toRadians(resultDetails.getGeometry().getLocationModel().getLat());
        double deltaLonRad = Math.toRadians(resultDetails.getGeometry().getLocationModel().getLng() - mMainActivity.mShareViewModel.currentUserPosition.getValue().longitude);

        return 1000 * Math.acos(
                Math.sin(lat1Rad) * Math.sin(lat2Rad) +
                        Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.cos(deltaLonRad)
        ) * 6371;
    }
}
