package com.ciplogic.allelon.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.ciplogic.allelon.R;
import com.ciplogic.allelon.RadioActivity;

public class StreamNotification {
    public StreamNotification() {
    }

    public void showNotification(String text) {
        Intent resultIntent = new Intent(RadioActivity.INSTANCE, RadioActivity.class);
        PendingIntent playerActivity =
                PendingIntent.getActivity(
                        RadioActivity.INSTANCE,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        Notification playingNotification = new NotificationCompat.Builder(RadioActivity.INSTANCE)
            .setOngoing(true)
            .setAutoCancel(false)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle("Allelon")
            .setContentText(text)
            .setContentIntent(playerActivity)
            .build();

        NotificationManager notificationManager =
                (NotificationManager) RadioActivity.INSTANCE.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, playingNotification);
    }

    public void hideNotification() {
        NotificationManager notificationManager =
                (NotificationManager) RadioActivity.INSTANCE.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
    }


}
