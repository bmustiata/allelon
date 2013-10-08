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
    private final Context context;

    public StreamNotification(android.content.Context context) {
        this.context = context;
    }

    public void showNotification(String text) {
        Intent resultIntent = new Intent(context, RadioActivity.class);
        PendingIntent playerActivity =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        Notification playingNotification = new NotificationCompat.Builder(context)
            .setOngoing(true)
            .setAutoCancel(false)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle("Allelon")
            .setContentText(text)
            .setContentIntent(playerActivity)
            .build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, playingNotification);
    }

    public void hideNotification() {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
    }


}
