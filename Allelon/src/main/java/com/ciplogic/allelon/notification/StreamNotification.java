package com.ciplogic.allelon.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.ciplogic.allelon.AllelonActivity;
import com.ciplogic.allelon.R;

public class StreamNotification {
    private final Context context;

    public StreamNotification(Context context) {
        this.context = context;
    }

    public void showNotification(String text) {
        if (context == null) {
            return; // FIXME: I should probably get the intent context at this stage.
        }

        Notification playingNotification = buildNotification(text);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, playingNotification);
    }

    public Notification buildNotification(String text) {
        if (context == null) {
            return null; // FIXME: I should probably get the intent context at this stage.
        }

        Intent resultIntent = new Intent(context, AllelonActivity.class);
        PendingIntent playerActivity =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        return new NotificationCompat.Builder(context)
                .setOngoing(true)
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.ic_launcher_transparent)
                .setContentTitle("Allelon")
                .setContentText(text)
                .setContentIntent(playerActivity)
                .build();
    }

    public void hideNotification() {
        if (context == null) {
            return; // FIXME: I should probably get the intent context at this stage.
        }

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
    }
}
