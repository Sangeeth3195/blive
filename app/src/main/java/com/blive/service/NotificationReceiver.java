package com.blive.service;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import com.blive.R;

import static com.blive.BLiveApplication.CHANNEL_ID;

public class NotificationReceiver extends BroadcastReceiver {
    Context context;
    NotificationManagerCompat notificationManager;
    private String channelUrl ="";
    private CharSequence message="";
    @Override
    public void onReceive(Context context, Intent intent) {

        this.context  = context;
        getMessageText(context, intent);
    }

    private CharSequence getMessageText(Context context, Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
         channelUrl =   intent.getStringExtra("channelUrl");
        if (remoteInput != null) {
             message = remoteInput.getCharSequence("key_text_reply");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Notification repliedNotification = new Notification.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.send)
                        .setContentText(message)
                        .build();

                notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(35667, repliedNotification);
            }
        }
        return null;
    }

}
