package emre.turhal.myapplicationtest.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class GetTodayDate {

    public static String getTodayDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sfDate = new SimpleDateFormat("dd-MM-yy", Locale.getDefault());
        return sfDate.format(c.getTime());
    }
}