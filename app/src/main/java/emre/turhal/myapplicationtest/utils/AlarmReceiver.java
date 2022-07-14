package emre.turhal.myapplicationtest.utils;

import static emre.turhal.myapplicationtest.utils.GetTodayDate.getTodayDate;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import emre.turhal.myapplicationtest.MainActivity;
import emre.turhal.myapplicationtest.R;
import emre.turhal.myapplicationtest.manager.BookingManager;
import emre.turhal.myapplicationtest.manager.UserManager;
import emre.turhal.myapplicationtest.models.googleplaces_gson.ResultDetails;
import emre.turhal.myapplicationtest.retrofit.GooglePlaceDetailsCalls;

public class AlarmReceiver extends BroadcastReceiver implements GooglePlaceDetailsCalls.Callbacks {

    String restaurantName;
    private NotificationCompat.Builder mBuilder;
    private List<String> mWorkmatesList;
    private Context mContext;
    private BookingManager mBookingManager = BookingManager.getInstance();
    private UserManager mUserManager =UserManager.getInstance();
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    public static final String NOTIFICATION_CHANNEL_NAME = "Go4Lunch";
    public static final int ALARM_TYPE_RTC = 100;



    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
        mWorkmatesList = new ArrayList<>();
        if (mUserManager.getCurrentUser() != null){

            mBookingManager.getBooking(mUserManager.getCurrentUser().getUid(), getTodayDate()).addOnCompleteListener(restaurantTask -> {
                if (restaurantTask.isSuccessful()){
                    if (!(restaurantTask.getResult().isEmpty())){
                        for (DocumentSnapshot restaurant : restaurantTask.getResult()) {
                            mBookingManager.getTodayBooking(Objects.requireNonNull((restaurant.getData()).get("restaurantId")).toString(), getTodayDate()).addOnCompleteListener(bookingTask -> {
                                if (bookingTask.isSuccessful()){
                                 for (QueryDocumentSnapshot booking : bookingTask.getResult()) {
                                     mUserManager.getWorkmate(Objects.requireNonNull(booking.getData().get("workmateUid")).toString()).addOnCompleteListener(userTask -> {
                                         if (userTask.isSuccessful()){
                                             if (!(Objects.requireNonNull(userTask.getResult().getData().get("uid").toString()).equals(mUserManager.getCurrentUser().getUid()))){
                                                 String username = Objects.requireNonNull(userTask.getResult().getData().get("name").toString());
                                                 mWorkmatesList.add(username);
                                             }
                                         }
                                         if (mWorkmatesList.size() == bookingTask.getResult().size() - 1){
                                             GooglePlaceDetailsCalls.fetchPlaceDetails(this, Objects.requireNonNull(restaurant.getData().get("restaurantId")).toString());
                                         }
                                     });
                                 }
                                }
                            });
                        }
                    }
                }
            });

        }
    }

    @Override
    public void onResponse(@Nullable ResultDetails resultDetails) {

        assert resultDetails != null;
        restaurantName = resultDetails.getName();
        sendNotification(MakeMessage.textMessage(mWorkmatesList, mContext, restaurantName));
    }

    public void sendNotification(String workmates){
        Intent resultIntent = new Intent(mContext, MainActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, ALARM_TYPE_RTC, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification repeatedNotification = buildLocalNotification(mContext, pendingIntent, workmates).build();
        android.app.NotificationManager notificationManager = (android.app.NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationManager.notify(ALARM_TYPE_RTC, repeatedNotification);

    }

    public NotificationCompat.Builder buildLocalNotification(Context mContext, PendingIntent pendingIntent, String workmates) {
        Log.e("TAG", "buildLocalNotification: USERS " + workmates);
        mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
        mBuilder.setSmallIcon(R.drawable.bowl);
        mBuilder.setContentTitle(mContext.getResources().getString(R.string.notification))
                .setContentText(MakeMessage.textMessage(mWorkmatesList, mContext, restaurantName))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(workmates))
                .setAutoCancel(true);
        return mBuilder;
    }

    @Override
    public void onFailure() {
        ShowToastSnack.showToast(mContext, "Error on retrieve restaurant's details", 0);
    }
}
