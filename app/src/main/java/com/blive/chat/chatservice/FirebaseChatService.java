package com.blive.chat.chatservice;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.blive.BLiveApplication;
import com.blive.R;
import com.blive.chat.chatmodels.Attachment;
import com.blive.chat.chatmodels.AttachmentTypes;
import com.blive.chat.chatmodels.Chat;
import com.blive.chat.chatmodels.ChatUser;
import com.blive.chat.chatmodels.Message;
import com.blive.chat.chatutil.ChatUtils;
import com.blive.chat.chatutil.FirebaseUploader;
import com.blive.chat.chatview.FireBaseChatActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

public class FirebaseChatService extends Service {
    private static final String CHANNEL_ID_MAIN = "my_channel_01";
    private static final String CHANNEL_ID_USER = "my_channel_03";
    private ChatUtils helper;
    private String myId;
    private Realm rChatDb;
    private HashMap<String, ChatUser> userHashMap = new HashMap<>();
    private ChatUser userMe;
    String replyId = "0";

    public FirebaseChatService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_MAIN,
                    "BLive chat service", NotificationManager.IMPORTANCE_LOW);
            channel.setSound(null, null);
            try {
                ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_MAIN)
                .setPriority(Notification.PRIORITY_MIN)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.app_name))
                .setSound(null)
                .build();
        startForeground(1, notification);
        LocalBroadcastManager.getInstance(this).registerReceiver(uploadAndSendReceiver, new IntentFilter(ChatUtils.UPLOAD_AND_SEND));
        LocalBroadcastManager.getInstance(
                this).registerReceiver(logoutReceiver, new IntentFilter(ChatUtils.BROADCAST_LOGOUT));
    }

    private BroadcastReceiver logoutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopForeground(true);
            stopSelf();
        }
    };

    private BroadcastReceiver uploadAndSendReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(ChatUtils.UPLOAD_AND_SEND)) {
                Attachment attachment = intent.getParcelableExtra("attachment");
                int type = intent.getIntExtra("attachment_type", -1);
                String attachmentFilePath = intent.getStringExtra("attachment_file_path");
                String attachmentChatChild = intent.getStringExtra("attachment_chat_child");
                String attachmentRecipientId = intent.getStringExtra("attachment_recipient_id");
                replyId = intent.getStringExtra("attachment_reply_id");
                uploadAndSend(new File(attachmentFilePath), attachment, type, attachmentChatChild, attachmentRecipientId);
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!ChatUser.validate(userMe)) {
            initVars();
            if (ChatUser.validate(userMe)) {
                myId = userMe.getId();
                rChatDb = ChatUtils.getRealmInstance();
                registerUserUpdates();
            } else {
                stopForeground(true);
                stopSelf();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void initVars() {
        helper = new ChatUtils(this);
        Realm.init(this);
        userMe = helper.getLoggedInUser();
    }

    private void restartService() {
        if (new ChatUtils(this).isLoggedIn()) {
            Intent intent = new Intent(this, FirebaseChatService.class);
            PendingIntent pendingIntent = PendingIntent.getService(this, 99,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 500, pendingIntent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(uploadAndSendReceiver);
        restartService();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        restartService();
        super.onTaskRemoved(rootIntent);
    }

    private void uploadAndSend(final File fileToUpload, final Attachment attachment,
                               final int attachmentType, final String chatChild, final String recipientId) {
        try {
            if (!fileToUpload.exists())
                return;

            final String fileName = Uri.fromFile(fileToUpload).getLastPathSegment();
            final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                    .child(getString(R.string.app_name)).child(AttachmentTypes.getTypeName(attachmentType)).child(fileName);

            storageReference.getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        Attachment attachment1 = attachment;
                        if (attachment1 == null) attachment1 = new Attachment();
                        attachment1.setName(fileName);
                        attachment1.setUrl(uri.toString());
                        attachment1.setBytesCount(fileToUpload.length());
                        sendMessage(attachmentType, attachment1, chatChild, recipientId);
                    })

                    .addOnFailureListener(exception -> {
                        FirebaseUploader firebaseUploader = new FirebaseUploader(new FirebaseUploader.UploadListener() {
                            @Override
                            public void onUploadFail(String message) {
                                Log.e("DatabaseException", message);
                            }

                            @Override
                            public void onUploadSuccess(String downloadUrl) {
                                Attachment attachment1 = attachment;
                                if (attachment1 == null) attachment1 = new Attachment();
                                attachment1.setName(fileToUpload.getName());
                                attachment1.setUrl(downloadUrl);
                                attachment1.setBytesCount(fileToUpload.length());
                                sendMessage(attachmentType, attachment1, chatChild, recipientId);
                            }

                            @Override
                            public void onUploadProgress(int progress) {

                            }

                            @Override
                            public void onUploadCancelled() {

                            }
                        }, storageReference);

                        firebaseUploader.uploadOthers(getApplicationContext(), fileToUpload);
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(@AttachmentTypes.AttachmentType int attachmentType,
                             Attachment attachment, String chatChild, String userOrGroupId) {
        try {
            Message message = new Message();
            message.setAttachmentType(attachmentType);
            if (attachmentType != AttachmentTypes.NONE_TEXT)
                message.setAttachment(attachment);
            message.setBody(null);
            message.setDate(System.currentTimeMillis());
            message.setSenderId(userMe.getId());
            message.setSenderName(userMe.getName());
            message.setSent(true);
            message.setDelivered(false);
            message.setRecipientId(userOrGroupId);
            message.setId(BLiveApplication.getChatRef().child(chatChild).push().getKey());
            message.setReplyId(replyId);
            BLiveApplication.getChatRef().child(chatChild).child(message.getId()).setValue(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerUserUpdates() {
        BLiveApplication.getUserRef().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    ChatUser user = dataSnapshot.getValue(ChatUser.class);
                    if (ChatUser.validate(user)) {
                        if (!userHashMap.containsKey(user.getId())) {
                            userHashMap.put(user.getId(), user);
                            broadcastUser("added", user);
                            registerChatUpdates(true, user.getId());
                        }
                    }
                } catch (Exception ex) {
                    Log.e("USER", "invalid user");
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                try {
                    ChatUser user = dataSnapshot.getValue(ChatUser.class);
                    if (ChatUser.validate(user)) {
                        broadcastUser("changed", user);
                        updateUserInDb(user);
                    }
                } catch (Exception ex) {
                    Log.e("USER", "invalid user");
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void updateUserInDb(final ChatUser value) {
        if (!TextUtils.isEmpty(myId)) {
            final Chat chat = rChatDb.where(Chat.class).equalTo("myId", myId)
                    .equalTo("userId", value.getId()).findFirst();
            if (chat != null) {
                rChatDb.executeTransaction(realm -> {
                    ChatUser updated = rChatDb.copyToRealm(value);
                    updated.setNameToDisplay(chat.getUser().getNameToDisplay());
                    chat.setUser(updated);
                });
            }
        }
    }

    private void registerChatUpdates(boolean register, String id) {
        if (!TextUtils.isEmpty(myId) && !TextUtils.isEmpty(id)) {
            DatabaseReference idChatRef = BLiveApplication.getChatRef().child(ChatUtils.getChatChild(myId, id));
            if (register) {
                idChatRef.addChildEventListener(chatUpdateListener);
            } else {
                idChatRef.removeEventListener(chatUpdateListener);
            }
        }
    }

    private ChildEventListener chatUpdateListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            try {
                Message message = dataSnapshot.getValue(Message.class);
                if (message != null && message.getId() != null) {
                    Message result = rChatDb.where(Message.class).equalTo("id", message.getId()).findFirst();
                    if (result == null && !TextUtils.isEmpty(myId) && helper.isLoggedIn()) {
                        saveMessage(message);
                        if (!message.getSenderId().equals(myId) && !message.isDelivered())
                            BLiveApplication.getChatRef().child(dataSnapshot.getRef().getParent().getKey())
                                    .child(message.getId()).child("delivered").setValue(true);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            try {
                final Message message = dataSnapshot.getValue(Message.class);
                if (message != null && message.getId() != null) {
                    final Message result = rChatDb.where(Message.class).equalTo("id", message.getId()).findFirst();
                    if (result != null) {
                        rChatDb.executeTransaction(realm -> {
                            result.setReadMsg(message.isReadMsg());
                            result.setDelivered(message.isDelivered());
                            result.setDelete(message.getDelete());
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            try {
                Message message = dataSnapshot.getValue(Message.class);
                if (message != null && message.getId() != null) {
                    ChatUtils.deleteMessageFromRealm(rChatDb, message.getId());
                    String userOrGroupId = myId.equals(message.getSenderId()) ? message.getRecipientId() : message.getSenderId();
                    final Chat chat = ChatUtils.getChat(rChatDb, myId, userOrGroupId).findFirst();
                    if (chat != null) {
                        rChatDb.executeTransaction(realm -> {
                            RealmList<Message> realmList = chat.getMessages();
                            if (realmList.size() == 0)
                                RealmObject.deleteFromRealm(chat);
                            else {
                                chat.setLastMessage(realmList.get(realmList.size() - 1).getBody());
                                chat.setTimeUpdated(realmList.get(realmList.size() - 1).getDate());
                            }
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private void saveMessage(final Message message) {
        try {
            if (message.getAttachment() != null && !TextUtils.isEmpty(message.getAttachment().getUrl())
                    && !TextUtils.isEmpty(message.getAttachment().getName())) {
                String idToCompare = "loading" + message.getAttachment().getBytesCount() + message.getAttachment().getName();
                ChatUtils.deleteMessageFromRealm(rChatDb, idToCompare);
            }

            final String userOrGroupId = myId.equals(message.getSenderId()) ? message.getRecipientId()
                    : message.getSenderId();
            final Chat[] chat = {ChatUtils.getChat(rChatDb, myId, userOrGroupId).findFirst()};
            rChatDb.executeTransaction(realm -> {
                if (chat[0] == null) {
                    chat[0] = rChatDb.createObject(Chat.class);
                    chat[0].setUser(rChatDb.copyToRealm(userHashMap.get(userOrGroupId)));
                    chat[0].setUserId(userOrGroupId);
                    chat[0].setGroupId(null);
                    chat[0].setMessages(new RealmList<>());
                    chat[0].setLastMessage(message.getBody());
                    chat[0].setMyId(myId);
                    chat[0].setTimeUpdated(message.getDate());
                }
                if (!message.getSenderId().equals(myId))
                    chat[0].setRead(false);
                chat[0].setTimeUpdated(message.getDate());
                chat[0].getMessages().add(message);
                chat[0].setLastMessage(message.getBody());
            });

            if (!message.isDelivered() && !message.getSenderId().equals(myId)
                    && !helper.isUserMute(message.getSenderId()) &&
                    (ChatUtils.CURRENT_CHAT_ID == null || !ChatUtils.CURRENT_CHAT_ID.equals(userOrGroupId))) {
                Intent chatActivity = null;
                chatActivity = FireBaseChatActivity.newIntent(this, null, chat[0].getUser());
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addNextIntentWithParentStack(chatActivity);
                PendingIntent pendingIntent = stackBuilder.getPendingIntent(99, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder notificationBuilder = null;
                String channelId = CHANNEL_ID_USER;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(channelId, "New message notification", NotificationManager.IMPORTANCE_DEFAULT);
                    if (notificationManager != null) {
                        notificationManager.createNotificationChannel(channel);
                    }
                    notificationBuilder = new NotificationCompat.Builder(this, channelId);
                } else {
                    notificationBuilder = new NotificationCompat.Builder(this);
                }
                String name = "";
                try {
                    name = userHashMap.get(chat[0].getUser().getName()).getNameToDisplay();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(name)
                        .setContentText(message.getBody())
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
                int msgId = 0;
                try {
                    msgId = Integer.parseInt(message.getSenderId());
                } catch (NumberFormatException ex) {
                    msgId = Integer.parseInt(message.getSenderId().substring(message.getSenderId().length() / 2));
                }
                notificationManager.notify(msgId, notificationBuilder.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void broadcastUser(String what, ChatUser value) {
        Intent intent = new Intent(ChatUtils.BROADCAST_USER);
        intent.putExtra("data", value);
        intent.putExtra("what", what);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.sendBroadcast(intent);
    }
}
