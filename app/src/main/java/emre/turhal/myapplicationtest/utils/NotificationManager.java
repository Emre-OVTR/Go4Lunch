package emre.turhal.myapplicationtest.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class NotificationManager {

    private Context mContext;
    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;
    public static final int ALARM_TYPE_RTC = 100;

    public NotificationManager(Context context) {
        mContext = context;
    }

    public void createNotification(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 30);
        Intent intent = new Intent(mContext, AlarmReceiver.class);
        mPendingIntent = PendingIntent.getBroadcast(mContext, ALARM_TYPE_RTC, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000 * 2, AlarmManager.INTERVAL_FIFTEEN_MINUTES, mPendingIntent);
    }
}