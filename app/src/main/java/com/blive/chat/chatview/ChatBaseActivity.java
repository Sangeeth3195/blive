package com.blive.chat.chatview;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.blive.chat.chatmodels.ChatUser;
import com.blive.chat.chatutil.ChatUtils;
import com.blive.chat.chatservice.FirebaseChatService;
import com.blive.chat.chatmodels.Contact;

import java.util.ArrayList;

import io.realm.Realm;

public abstract class ChatBaseActivity extends AppCompatActivity implements ServiceConnection {
    protected String[] permissionsRecord = {Manifest.permission.VIBRATE, Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    protected String[] permissionsStorage = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    protected String[] permissionsCamera = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    protected ChatUser userMe, user;
    protected ChatUtils helper;
    protected Realm rChatDb;

    private BroadcastReceiver userReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(ChatUtils.BROADCAST_USER)) {
                ChatUser user = intent.getParcelableExtra("data");
                String what = intent.getStringExtra("what");
                switch (what) {
                    case "added":
                        userAdded(user);
                        break;
                    case "changed":
                        userUpdated(user);
                        Intent local = new Intent("custom-event-name");
                        local.putExtra("status", "");
                        LocalBroadcastManager.getInstance(ChatBaseActivity.this).sendBroadcast(local);
                        break;
                }
            }
        }
    };


    private BroadcastReceiver myUsersReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<ChatUser> myUsers = intent.getParcelableArrayListExtra("data");
            if (myUsers != null) {
                myUsersResult(myUsers);
            }
        }
    };

    private BroadcastReceiver myContactsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Contact> myContacts = intent.getParcelableArrayListExtra("data");
            if (myContacts != null) {
                myContactsResult(myContacts);
            }
        }
    };

    abstract void myUsersResult(ArrayList<ChatUser> myUsers);

    abstract void myContactsResult(ArrayList<Contact> myContacts);

    abstract void userAdded(ChatUser valueUser);

    abstract void userUpdated(ChatUser valueUser);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new ChatUtils(this);
        userMe = helper.getLoggedInUser();
        Realm.init(this);
        rChatDb = ChatUtils.getRealmInstance();

        Intent intent = new Intent(this, FirebaseChatService.class);

        try {
            startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(userReceiver, new IntentFilter(ChatUtils.BROADCAST_USER));
        localBroadcastManager.registerReceiver(myContactsReceiver, new IntentFilter(ChatUtils.BROADCAST_MY_CONTACTS));
        localBroadcastManager.registerReceiver(myUsersReceiver, new IntentFilter(ChatUtils.BROADCAST_MY_USERS));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.unregisterReceiver(userReceiver);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }


    protected boolean permissionsAvailable(String[] permissions) {
        boolean granted = true;
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                granted = false;
                break;
            }
        }
        return granted;
    }
}
