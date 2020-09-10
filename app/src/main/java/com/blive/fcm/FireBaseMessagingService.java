package com.blive.fcm;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.blive.activity.ActivityBlocked;
import com.blive.activity.ActivityHome;
import com.blive.activity.ActivityNotification;
import com.blive.activity.ActivitySignIn;
import com.blive.BLiveApplication;
import com.blive.agora.rtmChat.ChatManager;
import com.blive.constant.Constants_api;
import com.blive.db.SqlDb;
import com.blive.model.FCMModel;
import com.blive.model.User;
import com.blive.R;
import com.blive.service.NotificationReceiver;
import com.blive.session.SessionLogin;
import com.blive.session.SessionManager;
import com.blive.session.SessionUser;
import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import im.delight.android.webview.AdvancedWebView;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmChannel;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmMessage;
import io.agora.rtm.RtmStatusCode;

/**
 * Created by sans on 08/11/18.
 */
public class FireBaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = FireBaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;
    // notification
    NotificationCompat.Builder mBuilder;
    NotificationManagerCompat notificationManager;
    int notificationId = 35667;
    String CHANNEL_ID = "my_channel_id";
    // Key for the string that's delivered in the action's intent.
    private static final String KEY_TEXT_REPLY = "key_text_reply";

    private RtmClient mRtmClient;
    private ChatManager mChatManager;
    private RtmChannel mRtmChannel;
    SessionManager sessionManager;
    SqlDb sqlDb;
    String loadurl = "";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sqlDb = new SqlDb(this);
        sessionManager = new SessionManager(this);
        Log.e(TAG, "ExceptionData: " + remoteMessage.getData());
        /*Log.e(TAG, "ExceptionData: " + remoteMessage.getNotification().getImageUrl());*/

        if (remoteMessage != null) {
            if (remoteMessage.getNotification() != null) {
                Log.d(TAG, "onMessageReceived: "+remoteMessage.getNotification());
                if (remoteMessage.getNotification().getTitle().equalsIgnoreCase("pkSpecialGiftSent")) {
                    try {
                        Intent intent = new Intent("pkSpecialGiftSent");
                        intent.putExtra("data", remoteMessage.getNotification().getBody());
                        Log.d("NotifTestApp:", "sending broadcast" + intent.toString());
                        LocalBroadcastManager.getInstance(BLiveApplication.getStaticContext()).sendBroadcast(intent);
                    } catch (Exception e) {
                        Log.d(TAG, "onMessageReceived: " + e.getMessage());
                    }
                } else if (remoteMessage.getNotification().getBody().contains("User Notification Of BLIVE")) {
                    String[] separated = remoteMessage.getNotification().getBody().split(" -- ");
                    loadurl = (separated[1]);
                }
            }
        }
        if (remoteMessage.getData().size() > 0) {
            try {
                // Check if message contains a notification payload.
                if (remoteMessage.getNotification() != null) {
                    try {
                        JSONObject jsonData = new JSONObject(remoteMessage.getData().toString());
                        handleNotification(jsonData, remoteMessage.getNotification().getBody());
                        Log.e(TAG, "100 Message Received: " + remoteMessage.getNotification().getBody());
                        Map<String, String> data = remoteMessage.getData();

                        String title = data.get("title");
                        String message = data.get("body");
                        String image = data.get("image");
                        Log.d("firebasedata", title);
                        Log.d("firebasedata", message);
                        Log.d("firebasedata", image);

                        showSmallNotification(title, message, image);
                        Log.e(TAG, "No Exception: " + jsonData + "  " + remoteMessage.getNotification().getBody());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "ExceptionOccuredJson: " + e.getMessage());
                    }
                }

                // Check if message contains a data payload.
                if (remoteMessage.getData().size() > 0) {
                    try {
//                        JSONObject jsonData = new JSONObject(remoteMessage.getData().toString());
//                        handleDataMessage(jsonData);

                        Map<String, String> data = remoteMessage.getData();

                        String title = data.get("title");
                        String message = data.get("body");
                        String image = data.get("image");
                        Log.d("c title", title);
                        Log.d("firebasedata message", message);
                        Log.d("firebasedata", image);
//                        sendnoti(title, message, image);
//                        showSmallNotification(title, message, image);


//                        if (message.equalsIgnoreCase("!00th fREe gIFt ReAcHeD")) {
//                            Log.d("message",message);
//                            handleNotificationmessage(message);
//
//                        }
                        notificationDialog(title, message, image);

                        int count = Integer.parseInt(sessionManager.getSessionStringValue("notification", "notification"));
                        count = count + 1;
                        sessionManager.storeSessionStringvalue("notification", "notification", String.valueOf(count));
//                        Log.d("jsonData",jsonData.toString());
//                        Log.d("fires",jsonData.getString("body"));


//                        handleNotification(jsonData, message);
                        if (image.equalsIgnoreCase("no")) {
                            image = "";
                        }

                        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                        Log.i("autolog", "currentDateTimeString: " + currentDateTimeString);

                        FCMModel fcmModel = new FCMModel();
                        fcmModel.setNotificationTitle(title);
                        fcmModel.setNotificationImage(image);
                        fcmModel.setDate(currentDateTimeString);
                        fcmModel.setNotificationcontent(message);


                        sqlDb.insertNotificationData(fcmModel);
                    } catch (Exception e) {
                        Log.e(TAG, "ExceptionOccured: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "ExceptionOccuredMain: " + e.getMessage());
            }
        }
        if (remoteMessage.getNotification() != null) {
            try {
                JSONObject jsonData = new JSONObject(remoteMessage.getData().toString());
                handleNotification(jsonData, remoteMessage.getNotification().getBody());
                Log.e(TAG, "No Exception: " + jsonData + "  " + remoteMessage.getNotification().getBody());
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "ExceptionOccuredJson: " + e.getMessage());
            }
        }
    }

    @Override
    public void onNewToken(String refreshedToken) {
        storeRegIdInPref(refreshedToken);
        Log.d("refreshedToken", refreshedToken);
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.apply();
    }

    private void handleNotification(JSONObject json, String message) {
        Log.d("json", json.toString());
        Log.d("message", message);
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            if (message.contains("moved to level number")) {
                try {
                    BLiveApplication.getCurrentActivity().runOnUiThread(() -> {
                        String level = SessionUser.getUser().getLevel();
                        try {
                            level = json.getString("level");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        User user = SessionUser.getUser();
                        user.setLevel(String.valueOf(level));
                        SessionUser.saveUser(user);
                        Dialog alertDialog = new Dialog(BLiveApplication.getCurrentActivity());
                        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertDialog.setContentView(R.layout.alert_level_up);

                        TextView tv = alertDialog.findViewById(R.id.tv);
                        ImageView iv = alertDialog.findViewById(R.id.iv);

                        Glide.with(BLiveApplication.getCurrentActivity())
                                .load(Constants_api.levelUp)
                                .into(iv);

                        tv.setText(message);

                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertDialog.show();

                        new Handler().postDelayed(alertDialog::dismiss, 2000);
                    });
                } catch (Exception e) {

                }
            } else if (message.contains("NeW LogiN ThrOugH MoBiLe")) {
                Log.e(TAG, "Mobile LOgin : " + message);
                try {
                    BLiveApplication.getCurrentActivity().runOnUiThread(() -> {
                        Dialog alertDialog = new Dialog(BLiveApplication.getCurrentActivity());
                        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertDialog.setContentView(R.layout.alert_reached);

                        Log.e(TAG, "handleNotification: 1 " + "NeW LogiN ThrOugH MoBiL");

                        ImageView iv = alertDialog.findViewById(R.id.iv);

                        Glide.with(BLiveApplication.getCurrentActivity())
                                .load(Constants_api.new_User_Mobile)
                                .into(iv);

                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertDialog.show();

                        new Handler().postDelayed(alertDialog::dismiss, 10000);
                    });
                } catch (Exception e) {
                    Log.e(TAG, "handleNotification: " + e);
                }
            } else if (message.contains("!00th fREe gIFt ReAcHeD")) {
                Log.e(TAG, "fREe gIFt ReAcHeD: " + message);
                try {
                    BLiveApplication.getCurrentActivity().runOnUiThread(() -> {
                        Dialog alertDialog = new Dialog(BLiveApplication.getCurrentActivity());
                        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertDialog.setContentView(R.layout.alert_reached);

                        ImageView iv = alertDialog.findViewById(R.id.iv);

                        Glide.with(BLiveApplication.getCurrentActivity())
                                .load(Constants_api.hun_Free_Gift_Acheived)
                                .into(iv);

                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertDialog.show();

                        new Handler().postDelayed(alertDialog::dismiss, 10000);
                    });
                } catch (Exception e) {
                    Log.e(TAG, "handleNotification: " + e);
                }
            } else if (message.contains("You are forced to logged out")) {
                try {
                    BLiveApplication.getCurrentActivity().runOnUiThread(() -> {
                        SessionLogin.clearLoginSession();
                        Intent intent = new Intent(getApplicationContext(), ActivitySignIn.class);
                        intent.putExtra("logout", "yes");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(intent);
                    });
                } catch (Exception e) {
                    Crashlytics.logException(e);
                }
            } else if (message.contains("User Notification Of BLIVE")) {
                try {
                    BLiveApplication.getCurrentActivity().runOnUiThread(() -> {
                        Dialog alertDialog = new Dialog(BLiveApplication.getCurrentActivity());
                        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertDialog.setContentView(R.layout.alert_notification);

                        final AdvancedWebView wv = alertDialog.findViewById(R.id.webview);
                        final ImageView close_web = alertDialog.findViewById(R.id.close_web);
                        wv.setBackgroundColor(Color.TRANSPARENT);

                        close_web.setOnClickListener(v -> alertDialog.dismiss());

                        String url = Constants_api.notification;
                        wv.loadUrl(url);

                        wv.setWebViewClient(new WebViewClient() {

                            @Override
                            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                            }

                            @Override
                            public void onPageFinished(WebView view, String url) {
                                super.onPageFinished(view, url);
                                Log.i("autolog", "url: " + url);
                            }

                            @Override
                            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                                Log.i("WebViewerror", "error: " + error);
                            }

                            @Override
                            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                                Log.i("autolog", "error: " + error);
                                final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                                builder.setMessage(R.string.notification_error_ssl_cert_invalid);
                                builder.setPositiveButton("continue", (dialog, which) -> handler.proceed());
                                builder.setNegativeButton("cancel", (dialog, which) -> handler.cancel());
                                final AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        });
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertDialog.show();
                    });
                } catch (Exception e) {
                    Log.e(TAG, "handleNotification: " + e);
                }
            } else if (message.contains("Admin has blocked you")) {
                mChatManager = BLiveApplication.getInstance().getChatManager();
                mRtmClient = mChatManager.getRtmClient();
                mRtmChannel = mChatManager.getRtmChannel();
                if (SessionUser.getIsScreenSharing()) {
                    try {
                        sendChannelMessage(SessionUser.getUser().getUser_id() + "00" + SessionUser.getUser().getName() + " Broadcast has ended");
                        sendChannelMessage("AdMIn hA$ bLoCkEd yOu tEmPoRAriLy");
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                    }
                }
                Intent intent = new Intent(BLiveApplication.getCurrentActivity(), ActivityBlocked.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                BLiveApplication.getCurrentActivity().finish();
                BLiveApplication.getCurrentActivity().startActivity(intent);
            } else if (message.contains("PKRequest")) {
                Log.d(TAG, "PK: ");
                ActivityManager am = (ActivityManager) BLiveApplication.getCurrentActivity().getSystemService(Context.ACTIVITY_SERVICE);
                ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
                Log.i("autolog", "cn: " + cn);

                Intent intent = new Intent("pkGuestrequest");
                intent.putExtra("data", message);
                Log.d("NotifTestApp:", "sending broadcast" + intent.toString());
                LocalBroadcastManager.getInstance(BLiveApplication.getStaticContext()).sendBroadcast(intent);

            } else if (message.contains("pkGuestAccept")) {
                Log.d(TAG, "PK: ");
                ActivityManager am = (ActivityManager) BLiveApplication.getCurrentActivity().getSystemService(Context.ACTIVITY_SERVICE);
                ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
                Log.i("autolog", "cn: " + cn);

                Intent intent = new Intent("pkGuestAccept");
                intent.putExtra("data", message);
                Log.d("NotifTestApp:", "sending broadcast" + intent.toString());
                LocalBroadcastManager.getInstance(BLiveApplication.getStaticContext()).sendBroadcast(intent);

            } else if (message.contains("PK_REJECTED")) {
                Log.d(TAG, "PK: ");
                ActivityManager am = (ActivityManager) BLiveApplication.getCurrentActivity().getSystemService(Context.ACTIVITY_SERVICE);
                ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
                Log.i("autolog", "cn: " + cn);
                Intent intent = new Intent("pkReject");
                Log.d("NotifTestApp:", "sending broadcast" + intent.toString());
                LocalBroadcastManager.getInstance(BLiveApplication.getStaticContext()).sendBroadcast(intent);

            } else {
                Log.e("notificationCls", "notificationCls " + message);
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                //  Intent pushNotification = new Intent(BLiveApplication.getCurrentActivity(),ActivityHome.class);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            }
        } else {
            // If the app is in background, fireBase itself handles the notification
        }
    }

    private void handleNotificationmessage(String message) {

        Log.d("message", message);
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            if (message.contains("NeW LogiN ThrOugH MoBiLe")) {
                Log.e(TAG, "Mobile LOgin : " + message);
                try {
                    BLiveApplication.getCurrentActivity().runOnUiThread(() -> {
                        Dialog alertDialog = new Dialog(BLiveApplication.getCurrentActivity());
                        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertDialog.setContentView(R.layout.alert_reached);

                        Log.e(TAG, "handleNotification: 1 " + "NeW LogiN ThrOugH MoBiL");

                        ImageView iv = alertDialog.findViewById(R.id.iv);

                        Glide.with(BLiveApplication.getCurrentActivity())
                                .load(Constants_api.new_User_Mobile)
                                .into(iv);

                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertDialog.show();

                        new Handler().postDelayed(alertDialog::dismiss, 10000);
                    });
                } catch (Exception e) {
                    Log.e(TAG, "handleNotification: " + e);
                }
            } else if (message.contains("!00th fREe gIFt ReAcHeD")) {
                Log.e(TAG, "fREe gIFt ReAcHeD: " + message);
                try {
                    BLiveApplication.getCurrentActivity().runOnUiThread(() -> {
                        Dialog alertDialog = new Dialog(BLiveApplication.getCurrentActivity());
                        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertDialog.setContentView(R.layout.alert_reached);

                        ImageView iv = alertDialog.findViewById(R.id.iv);

                        Glide.with(BLiveApplication.getCurrentActivity())
                                .load(Constants_api.hun_Free_Gift_Acheived)
                                .into(iv);

                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertDialog.show();

                        new Handler().postDelayed(alertDialog::dismiss, 10000);
                    });
                } catch (Exception e) {
                    Log.e(TAG, "handleNotification: " + e);
                }
            } else if (message.contains("You are forced to logged out")) {
                try {
                    BLiveApplication.getCurrentActivity().runOnUiThread(() -> {
                        SessionLogin.clearLoginSession();
                        Intent intent = new Intent(getApplicationContext(), ActivitySignIn.class);
                        intent.putExtra("logout", "yes");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(intent);
                    });
                } catch (Exception e) {
                    Crashlytics.logException(e);
                }
            } else if (message.contains("Admin has blocked you")) {
                mChatManager = BLiveApplication.getInstance().getChatManager();
                mRtmClient = mChatManager.getRtmClient();
                mRtmChannel = mChatManager.getRtmChannel();
                if (SessionUser.getIsScreenSharing()) {
                    try {
                        sendChannelMessage(SessionUser.getUser().getUser_id() + "00" + SessionUser.getUser().getName() + " Broadcast has ended");
                        sendChannelMessage("AdMIn hA$ bLoCkEd yOu tEmPoRAriLy");
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                    }
                }
                Intent intent = new Intent(BLiveApplication.getCurrentActivity(), ActivityBlocked.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                BLiveApplication.getCurrentActivity().finish();
                BLiveApplication.getCurrentActivity().startActivity(intent);
            } else {
                Log.e("notificationCls", "notificationCls " + message);
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                //  Intent pushNotification = new Intent(BLiveApplication.getCurrentActivity(),ActivityHome.class);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            }
        } else {
            // If the app is in background, fireBase itself handles the notification
        }
    }

    private void sendChannelMessage(String msg) {
        // step 1: create a message
        RtmMessage message = mRtmClient.createMessage();
        message.setText(msg);
        // step 2: send message to channel

        mRtmChannel.sendMessage(message, new ResultCallback<Void>() {

            @Override
            public void onSuccess(Void aVoid) {
                Log.e(TAG, "onMessageSendSuccess: ");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                // refer to RtmStatusCode.ChannelMessageState for the message state
                Log.e(TAG, "onMessageSendFailure: ");
                final int errorCode = errorInfo.getErrorCode();
                BLiveApplication.getCurrentActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (errorCode) {
                            case RtmStatusCode.ChannelMessageError.CHANNEL_MESSAGE_ERR_TIMEOUT:
                            case RtmStatusCode.ChannelMessageError.CHANNEL_MESSAGE_ERR_FAILURE:
                                Toast.makeText(BLiveApplication.getCurrentActivity(), getString(R.string.send_msg_failed), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
            }
        });
    }

    private void handleDataMessage(JSONObject json) {
        Log.d("json", json.toString());
        try {
//            JSONObject data = json.getJSONObject("data");

            String title = json.getString("title");
            String message = json.getString("body");
//            boolean isBackground = json.getBoolean("is_background");
            String imageUrl = json.getString("image");
            String timestamp = "";
//            JSONObject payload = json.getJSONObject("payload");

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), ActivityHome.class);
                resultIntent.putExtra("message", message);

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }

    public void showNotification(String profilePic, String name, String message, String channelUrl) {
        notificationManager = NotificationManagerCompat.from(this);
        createNotificationChannel();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                    .setLabel("Reply")
                    .build();
            Intent intent = null;
            intent = new Intent(FireBaseMessagingService.this, NotificationReceiver.class)
                    .putExtra("channelUrl", channelUrl)
                    .putExtra("msgReply", 111);

            // Build a PendingIntent for the reply action to trigger.
            PendingIntent replyPendingIntent =
                    PendingIntent.getBroadcast(getApplicationContext(),
                            111, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

            // Create the reply action and add the remote input.
            NotificationCompat.Action action =
                    new NotificationCompat.Action.Builder(R.drawable.send,
                            "Reply", replyPendingIntent)
                            .addRemoteInput(remoteInput)
                            .build();

            mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(name)
                    .setContentText(message)
                    .setChannelId(CHANNEL_ID)
                    .addAction(action)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);
            notificationManager.notify(notificationId, mBuilder.build());
        } else {
            mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(name)
                    .setContentText(message)
                    .setChannelId(CHANNEL_ID)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);
            notificationManager.notify(notificationId, mBuilder.build());
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);

            Log.e("Showing Notification", "channel");
        }
    }

    public void clearNotification() {
        notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancelAll();
    }

    private void showSmallNotification(String title, String message, String image) {
        Log.i("autolog", "title: " + title);

        if (image.equalsIgnoreCase("no")) {

        } else {
            Bitmap imagebit = getBitmapFromURL(image);
        }

        Intent resultIntent = new Intent(getApplicationContext(), ActivityNotification.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        resultIntent.putExtra("title", title);
        resultIntent.putExtra("message", message);

        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0 /* Request code */, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.custom_notification_small);
        RemoteViews notificationLayoutExpanded = new RemoteViews(getPackageName(), R.layout.custom_notification);
        notificationLayout.setTextViewText(R.id.title, title);
        notificationLayout.setImageViewResource(R.id.image_app, R.drawable.logo_small);
        notificationLayout.setTextViewText(R.id.text, message);
        notificationLayoutExpanded.setTextViewText(R.id.title, title);
        notificationLayoutExpanded.setImageViewResource(R.id.image_pic, R.drawable.logo_small);
        notificationLayoutExpanded.setTextViewText(R.id.text, message);

        mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        mBuilder
                .setContentIntent(resultPendingIntent)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationLayout)
                .setCustomBigContentView(notificationLayoutExpanded)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(message)
                .build();
        Log.i("autolog", "mBuilder: " + mBuilder.getExtras().toString());
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(Config.NOTIFICATION_ID, mBuilder.build());
    }

    //    void sendnoti(String title, String message, String image){
//        Log.i("autolog", "mBuilder: " + mBuilder.getExtras().toString());
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//        builder.setSmallIcon(android.R.drawable.ic_dialog_alert);
//        Intent intent = new Intent(getApplicationContext(), ActivityNotification.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
//        builder.setContentIntent(pendingIntent);
//        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
//        builder.setContentTitle(title);
//        builder.setContentText(message);
//        builder.setSubText(image);
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//        // Will display the notification in the notification bar
//        notificationManager.notify(1, builder.build());
//    }
    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void notificationDialog(String title, String message, String image) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = CHANNEL_ID;
        Intent resultIntent = new Intent(getApplicationContext(), ActivityNotification.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        resultIntent.putExtra("title", title);
        resultIntent.putExtra("message", message);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0 /* Request code */, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.custom_notification_small);
        RemoteViews notificationLayoutExpanded = new RemoteViews(getPackageName(), R.layout.custom_notification);
        notificationLayout.setTextViewText(R.id.title, title);
        notificationLayout.setImageViewBitmap(R.id.image_app, getBitmapFromURL(image));
        notificationLayout.setTextViewText(R.id.text, message);
        notificationLayoutExpanded.setTextViewText(R.id.title, title);
        notificationLayoutExpanded.setImageViewBitmap(R.id.image_pic, getBitmapFromURL(image));
        notificationLayoutExpanded.setTextViewText(R.id.text, message);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_MAX);
            // Configure the notification channel.
            notificationChannel.setDescription(message);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationLayout)
                .setCustomBigContentView(notificationLayoutExpanded)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(title)
                .setContentText(message)
                .setContentInfo(image);
        notificationManager.notify(1, notificationBuilder.build());
    }

}