package com.blive.chat.chatservice;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.blive.BLiveApplication;
import com.blive.R;
import com.blive.chat.chatmodels.ChatUser;
import com.blive.chat.chatutil.ChatUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import io.realm.Realm;

public class FetchMyUsersService extends IntentService {
    private static String EXTRA_PARAM1 = "my_id";
    private static String EXTRA_PARAM2 = "token";
    private ArrayList<ChatUser> myUsers;
    private String myId, idToken;
    public static boolean STARTED = false;
    private Realm rChatDb;

    public FetchMyUsersService() {
        super("FetchMyUsersService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }

    public static void startMyUsersService(Context context, String myId, String idToken) {
        Intent intent = new Intent(context, FetchMyUsersService.class);
        intent.putExtra(EXTRA_PARAM1, myId);
        intent.putExtra(EXTRA_PARAM2, idToken);
        try {
            context.startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onHandleIntent(Intent intent) {
Log.i("autolog", "intent: " + intent);
        STARTED = true;
        myId = intent.getStringExtra(EXTRA_PARAM1);
        idToken = intent.getStringExtra(EXTRA_PARAM2);
        rChatDb = ChatUtils.getRealmInstance();
        registerUserUpdates();
        STARTED = false;
    }

    private void broadcastMyUsers() {
        Log.i("autolog", "myUsers: " + myUsers);

        if (this.myUsers != null) {
            Intent intent = new Intent(ChatUtils.BROADCAST_MY_USERS);
            intent.putParcelableArrayListExtra("data", this.myUsers);
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
            localBroadcastManager.sendBroadcast(intent);
        }
    }

     private void startMyOwnForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String NOTIFICATION_CHANNEL_ID = getPackageName();
            String channelName = "My Background Service";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("App is running in background")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
            startForeground(2, notification);
        }
    }


    private void registerUserUpdates() {
        myUsers = new ArrayList<>();
        BLiveApplication.getUserRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatUser user = snapshot.getValue(ChatUser.class);
                    myUsers.add(user);
                }
                Collections.sort(myUsers, (user1, user2) ->
                        user1.getNameToDisplay().compareToIgnoreCase(user2.getNameToDisplay()));
                broadcastMyUsers();
                Log.i("autolog", "myUsers: " + myUsers);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
