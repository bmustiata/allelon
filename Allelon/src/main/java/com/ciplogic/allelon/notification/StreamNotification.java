package com.ciplogic.allelon.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.ciplogic.allelon.PlayActivity;
import com.ciplogic.allelon.R;

public class StreamNotification {
    public StreamNotification() {
    }

    public void showNotification(String text) {
        if (PlayActivity.INSTANCE == null) {
            return; // FIXME: I should probably get the intent context at this stage.
        }

        Notification playingNotification = buildNotification(text);

        NotificationManager notificationManager =
                (NotificationManager) PlayActivity.INSTANCE.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, playingNotification);
    }

    public Notification buildNotification(String text) {
        if (PlayActivity.INSTANCE == null) {
            return null; // FIXME: I should probably get the intent context at this stage.
        }

        Intent resultIntent = new Intent(PlayActivity.INSTANCE, PlayActivity.class);
        PendingIntent playerActivity =
                PendingIntent.getActivity(
                        PlayActivity.INSTANCE,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        return new NotificationCompat.Builder(PlayActivity.INSTANCE)
                .setOngoing(true)
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Allelon")
                .setContentText(text)
                .setContentIntent(playerActivity)
                .build();
    }

    public void hideNotification() {
        if (PlayActivity.INSTANCE == null) {
            return; // FIXME: I should probably get the intent context at this stage.
        }

        NotificationManager notificationManager =
                (NotificationManager) PlayActivity.INSTANCE.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
    }


}
