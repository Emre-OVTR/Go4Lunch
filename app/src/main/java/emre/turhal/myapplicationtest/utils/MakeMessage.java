package emre.turhal.myapplicationtest.utils;

import android.content.Context;

import java.util.List;

import emre.turhal.myapplicationtest.R;

public class MakeMessage {

    public static StringBuilder makeMessage(List<String> workmatesList) {
        StringBuilder mStringBuilder = new StringBuilder();
        for (int i = 0; i < workmatesList.size(); i++) {
            mStringBuilder.append(workmatesList.get(i));
            // tant qu'on est pas au dernier membre de la liste on rajoute la virgule
            if (!(i == workmatesList.size() - 1)) {
                mStringBuilder.append(", ");
            } else {
                mStringBuilder.append(".");
            }
        }
        return mStringBuilder;
    }

    public static String textMessage(List<String> workmatesList, Context mContext, String restaurantName) {

        return workmatesList.size() > 0 ? mContext.getResources().getString(
                R.string.notif1) + " " + restaurantName + ". " + mContext.getResources().getString(
                R.string.notif2) + " " + makeMessage(workmatesList) : mContext.getResources().getString(
                R.string.notif1) + " " + restaurantName + ". " + mContext.getResources().getString(
                R.string.notif2) + " " + mContext.getResources().getString(
                R.string.notification_no_workmates);

    }
}
