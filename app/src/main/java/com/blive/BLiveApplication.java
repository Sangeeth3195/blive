package com.blive;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.multidex.MultiDex;

import com.blive.agora.WorkerThread;
import com.blive.agora.rtmChat.ChatManager;
import com.blive.chat.chatutil.ChatUtils;
import com.crashlytics.android.Crashlytics;
import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.ILoadCallback;
import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import static com.blive.chat.chatutil.ChatUtils.REF_CHAT;
import static com.blive.chat.chatutil.ChatUtils.REF_USER;

/**
 * Created by sans on 13-08-2018.
 **/

public class BLiveApplication extends Application {

    private WorkerThread mWorkerThread;
    private static BLiveApplication mInstance;
    public static final String TAG = BLiveApplication.class
            .getSimpleName();
    public static final String CHANNEL_ID = "BLiveApplication";
    private static Activity activity = null;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAnalytics mFirebaseAnalytics;
    private ChatManager mChatManager;

    //Chat
    private static FirebaseDatabase firebaseDatabase;
    private static DatabaseReference userRef, chatRef;

    public static Context getStaticContext() {
        return BLiveApplication.getInstance().getApplicationContext();
    }

    public static BLiveApplication getInstance() {
        return mInstance;
    }

    public static void setCurrentActivity(Activity mCurrentActivity) {
        activity = mCurrentActivity;
    }

    public static Activity getCurrentActivity() {
        return activity;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        FirebaseApp.initializeApp(this);
        Firebase.setAndroidContext(this);
        EmojiManager.install(new GoogleEmojiProvider());
        firebaseDatabase = FirebaseDatabase.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        //setupAgoraEngine();
        mChatManager = new ChatManager(this);
        mChatManager.init();
        mInstance = this;
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/calibri_regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        AndroidAudioConverter.load(this, new ILoadCallback() {
            @Override
            public void onSuccess() {

            }
            @Override
            public void onFailure(Exception error) {
                error.printStackTrace();
            }
        });
    }

    public static DatabaseReference getUserRef() {
        if (userRef == null) {
            userRef = firebaseDatabase.getReference(ChatUtils.REF_DATA).child(REF_USER);
            userRef.keepSynced(true);
        }
        return userRef;
    }

    public static DatabaseReference getChatRef() {
        if (chatRef == null) {
            chatRef = firebaseDatabase.getReference(ChatUtils.REF_DATA).child(REF_CHAT);
            chatRef.keepSynced(true);
        }
        return chatRef;
    }

    public synchronized void initWorkerThread() {
        if (mWorkerThread == null) {
            mWorkerThread = new WorkerThread(getApplicationContext());
            mWorkerThread.start();
            mWorkerThread.waitForReady();
        }
    }

    public synchronized WorkerThread getWorkerThread() {
        return mWorkerThread;
    }

    public GoogleSignInClient getGoogleSignInClient() {
        return mGoogleSignInClient;
    }

    public synchronized void deInitWorkerThread() {
        if (mWorkerThread != null) {
            mWorkerThread.exit();
            try {
                mWorkerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mWorkerThread = null;
        }
    }

    public ChatManager getChatManager() {
        return mChatManager;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID,
                    "BLive Application",
                    NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(serviceChannel);
        }
    }
}