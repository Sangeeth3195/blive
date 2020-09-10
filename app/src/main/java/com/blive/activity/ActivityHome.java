package com.blive.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.blive.BLiveApplication;
import com.blive.agora.rtmChat.ChatHandler;
import com.blive.agora.rtmChat.ChatManager;
import com.blive.constant.Constants_api;
import com.blive.constant.Constants_app;
import com.blive.fcm.Config;
import com.blive.fcm.NotificationUtils;
import com.blive.fragment.FragmentHome;
import com.blive.fragment.FragmentNearBy;
import com.blive.fragment.FragmentProfile;
import com.blive.fragment.FragmentLeaderBoard;
import com.blive.model.User;

import com.blive.R;
import com.blive.model.VersionResponse;
import com.blive.service.ApiClientBase;
import com.blive.service.ApiInterface;
import com.blive.service.DownloadGifts;
import com.blive.session.SessionUser;
import com.blive.utils.AdvanceWebViewArun.webview;
import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import io.agora.rtc.Constants;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmChannelMember;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmMessage;
import retrofit2.Call;
import retrofit2.Response;

public class ActivityHome extends BaseActivity {

    private static final int REQUEST_APP_UPDATE = 1245;
    @BindView(R.id.frame_layout)
    FrameLayout container;
    @BindView(R.id.iv_home)
    ImageView ivHome;
    @BindView(R.id.iv_near)
    ImageView ivNear;
    @BindView(R.id.iv_leaderboard)
    ImageView ivLeaderboard;
    @BindView(R.id.iv_profile)
    ImageView ivProfile;
    @BindView(R.id.iv_live)
    ImageView ivLive;
    AppUpdateManager appUpdateManager;

    private User user;
    private ArrayList<User> users;
    private int position = -1, versionCode = 0;
    boolean doubleBackToExitPressedOnce = false, isLoggedIn = false, isHome = false, isNearBy = false, isLeaderboard = false, isProfile = false,
            isHomeVisible = false, isNearByVisible = false, isLeaderboardVisible = false, isProfileVisible = false, isNotificationUser = false;
    String image = "";
    protected Context context;
    private String provider = "", versionName = "";
    FragmentHome fragmentHome;
    FragmentNearBy fragmentNearBy;
    FragmentLeaderBoard fragmentLeaderBoard;
    FragmentProfile fragmentProfile;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isForceUpdate = false;
    private RtmClient mRtmClient;
    private ChatManager mChatManager;
    private ChatHandler mChatHandler;
    String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BLiveApplication.setCurrentActivity(this);

        appUpdateManager = AppUpdateManagerFactory.create(this);

        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(
                        appUpdateInfo -> {

                            // Checks that the platform will allow the specified type of update.
                            if ((appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE)
                                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                                // Request the update.
                                try {
                                    appUpdateManager.startUpdateFlowForResult(
                                            appUpdateInfo,
                                            AppUpdateType.IMMEDIATE,
                                            this,
                                            REQUEST_APP_UPDATE);
                                } catch (IntentSender.SendIntentException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
//
//        Bundle bundle = getIntent().getExtras();
//        String message = bundle.getString("message");
//        Log.i("autolog", "user: " + user);
//        getUsername(message);

//        username=DeeplinkActivity.username;
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            public void run() {
//                // time ran out.
//                if (!username.equalsIgnoreCase("")){
//                    getUser(DeeplinkActivity.userDatamodel,DeeplinkActivity.userDatamodelArray,0);
//                }
//
//                timer.cancel();
//            }
//        }, 3000);


        // Downloading Service Started
        Intent intent = new Intent(this, ActivityHome.class);
        startService(intent);

        DownloadGifts downloadGifts = new DownloadGifts(getApplicationContext(), this);
        downloadGifts.downloadGiftFromServer();

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                        FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_ALL);
                        displayFireBaseRegId();
                    } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                        String message = intent.getStringExtra("message");
                        EventBus.getDefault().post(message);
                    }
                } catch (Exception e) {
                    Crashlytics.logException(e);
                }
            }
        };
    }


    protected void initUI() {
        if (SessionUser.getIsScreenSharing()) {
            ((BLiveApplication) getApplication()).deInitWorkerThread();
            ((BLiveApplication) getApplication()).initWorkerThread();
            SessionUser.isScreenSharing(false);
        }

        Glide.with(this).load(R.drawable.go_live).into(ivLive);

        fragmentHome = new FragmentHome();
        fragmentNearBy = new FragmentNearBy();
        fragmentLeaderBoard = new FragmentLeaderBoard();
        fragmentProfile = new FragmentProfile();

        try {
            if (getIntent().getExtras() != null) {
                if (getIntent().getExtras().containsKey("userID")) {
                    isNotificationUser = true;
                    Intent intent = getIntent();
                    String userId = intent.getStringExtra("userID");
                    loadHomeFragment(new FragmentHome(userId), "home");
                } else {
                    loadHomeFragment(fragmentHome, "home");
                }
            } else {
                loadHomeFragment(fragmentHome, "home");
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }

        isHome = true;
        isHomeVisible = true;

        switch (getPackageName()) {
            case "com.blive.debug":
                FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_DEV);
                break;
            case "com.blive.stage":
                FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_STAGE);
                break;
            case "com.blive":
                FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_PROD);
                break;
        }

        getVersionInfo();

        if (getApplicationContext().getPackageName().equalsIgnoreCase("com.blive")) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<VersionResponse> call = apiClient.getAppVersion();
            call.enqueue(new retrofit2.Callback<VersionResponse>() {
                @Override
                public void onResponse(@NonNull Call<VersionResponse> call, @NonNull Response<VersionResponse> response) {
                    VersionResponse versionResponse = response.body();
                    if (versionResponse != null) {
                        if (versionResponse.getStatus().equalsIgnoreCase("success")) {
                            checkAppVersion(versionResponse.getData().getVersionName(), versionResponse.getData().getVersionCode(), versionResponse.getData().getUpdateType());
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.server_error), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<VersionResponse> call, @NonNull Throwable t) {

                }
            });
        }

        mChatManager = BLiveApplication.getInstance().getChatManager();
        mRtmClient = mChatManager.getRtmClient();
       mChatHandler = new ChatHandler() {
            @Override
            public void onLoginSuccess() {
                Log.e(TAG, "onLoginSuccess: ");
            }

            @Override
            public void onLoginFailed(ErrorInfo errorInfo) {
                Log.e(TAG, "onLoginFailed: ");
                runOnUiThread(() -> {
                    if (errorInfo.getErrorCode() == 8)
                        try {
                            if (user.getUsername() != null) {
                                mChatManager.createChannel(SessionUser.getUser().getUsername().trim());
                            } else {
                                mChatManager.doLogin();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                });
            }

            @Override
            public void onChannelJoinSuccess() {
                runOnUiThread(() -> {
                    utils.hideProgress();


                });
            }

            @Override
            public void onChannelJoinFailed(ErrorInfo errorInfo) {
                runOnUiThread(() -> {
                    Log.e(TAG, "channel join failed: ");
                    if (errorInfo.getErrorCode() == 1) {
                        Log.e(TAG, "run: " + errorInfo.getErrorDescription());
                        try {
                            doLogin1(SessionUser.getUser().getUsername().trim());
                        } catch (Exception e) {
                            Log.i("autolog", "e: " + e.toString());
                            Crashlytics.log(e.getMessage());
                        }
                    }
                });
            }

            @Override
            public void onMessageReceived(RtmMessage message, RtmChannelMember fromMember) {

            }

            @Override
            public void onMemberJoined(RtmChannelMember rtmChannelMember) {
                Log.e(TAG, "onRTMMemberJoined: ");
            }

            @Override
            public void onMemberLeft(RtmChannelMember rtmChannelMember) {
            }
        };


     mChatManager.addChantHandler(mChatHandler);

       if (!SessionUser.getRtmLoginSession()) {
            doLogin1(SessionUser.getUser().getUsername());
        } else {
            mChatManager.createChannel(SessionUser.getUser().getUsername());
            //     logoutRtm(SessionUser.getUser().getUsername());
        }
        doLogin1(SessionUser.getUser().getUsername());

        mChatManager.leaveChannel();

        checkCamAndAudioPermission();


        if (!isNotificationUser) {
            Intent intent1 = getIntent();
            if (intent1 != null) {
                String from = intent1.getStringExtra("from");
                if (from == null)
                    return;
                /*if (from.equalsIgnoreCase("splash")) {
                    Log.e(TAG, "initUI: " + "splash");
                    Intent intent = new Intent(mActivity, ActivitySkip.class);
                    intent.putExtra("title", "BLive");
                    intent.putExtra("from", "home");
                    intent.putExtra("url", Constants_api.news);
                    Log.e(TAG, "Splash_NEWS: " + Constants_api.news);
                    startActivity(intent);
                }*/
            }
        }


    }

    @Override
    protected void deInitUI() {

    }

    private void getVersionInfo() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();

    }

    private void displayFireBaseRegId() {
        String regId = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "displayFireBaseRegId: " + regId);
    }

    @Override
    public void onResume() {
        super.onResume();
        utils.hideProgress();
        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(
                        appUpdateInfo -> {

                            if (appUpdateInfo.updateAvailability()
                                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                // If an in-app update is already running, resume the update.
                                try {
                                    appUpdateManager.startUpdateFlowForResult(
                                            appUpdateInfo,
                                            AppUpdateType.IMMEDIATE,
                                            this,
                                            REQUEST_APP_UPDATE);
                                } catch (IntentSender.SendIntentException e) {
                                    e.printStackTrace();
                                }
                            }
                        });


        if (SessionUser.getIsScreenSharing()) {
            ((BLiveApplication) getApplication()).deInitWorkerThread();
            ((BLiveApplication) getApplication()).initWorkerThread();
            SessionUser.isScreenSharing(false);
        }

        //addCallback();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());

        if (isForceUpdate) {
            AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(ActivityHome.this);
            alertDialogBuilder1.setMessage("Please update to new version!");
            alertDialogBuilder1.setPositiveButton("Update",
                    (arg0, arg1) -> {
                        openPlayStore();
                        arg0.dismiss();
                    });

            AlertDialog alertDialog1 = alertDialogBuilder1.create();
            alertDialog1.setCancelable(false);
            alertDialog1.setCanceledOnTouchOutside(false);
            alertDialog1.show();
        }
    }

    @OnClick(R.id.iv_home)
    public void onClickHome() {
        if (utils.isNetworkAvailable()) {
            if (!isHomeVisible) {
                EventBus.getDefault().post("searchShow");
                EventBus.getDefault().post("homeTab");
                if (!isHome) {
                    isHome = true;

                    if (isNearByVisible)
                        loadFragment(fragmentHome, "home", fragmentNearBy);
                    else if (isLeaderboardVisible)
                        loadFragment(fragmentHome, "home", fragmentLeaderBoard);
                    else if (isProfileVisible)
                        loadFragment(fragmentHome, "home", fragmentProfile);

                } else {
                    if (isNearByVisible)
                        showFragment(fragmentHome, fragmentNearBy);
                    else if (isLeaderboardVisible)
                        showFragment(fragmentHome, fragmentLeaderBoard);
                    else if (isProfileVisible)
                        showFragment(fragmentHome, fragmentProfile);
                }
                isHomeVisible = true;
                isNearByVisible = false;
                isLeaderboardVisible = false;
                isProfileVisible = false;
            } else {
                EventBus.getDefault().post("homeTab");
            }
        }
    }

    @OnClick(R.id.iv_near)
    public void onClickNearMe() {
        if (utils.isNetworkAvailable()) {
            if (!isNearByVisible) {
                EventBus.getDefault().post("searchHide");
                if (!isNearBy) {
                    getSupportFragmentManager().getFragments();
                    isNearBy = true;
                    if (isHomeVisible)
                        loadFragment(fragmentNearBy, "nearby", fragmentHome);
                    else if (isLeaderboardVisible)
                        loadFragment(fragmentNearBy, "nearby", fragmentLeaderBoard);
                    else if (isProfileVisible)
                        loadFragment(fragmentNearBy, "nearby", fragmentProfile);

                } else {
                    if (isHomeVisible)
                        showFragment(fragmentNearBy, fragmentHome);
                    else if (isLeaderboardVisible)
                        showFragment(fragmentNearBy, fragmentLeaderBoard);
                    else if (isProfileVisible)
                        showFragment(fragmentNearBy, fragmentProfile);
                }
                isHomeVisible = false;
                isNearByVisible = true;
                isLeaderboardVisible = false;
                isProfileVisible = false;
            }
        }
    }

    /*
        @OnClick(R.id.iv_leaderboard)
        public void onTvClick() {
            if (utils.isNetworkAvailable()) {
                if (!isLeaderboardVisible) {
                    EventBus.getDefault().post("searchHide");
                    if (!isLeaderboard) {
                        isLeaderboard = true;
                        if (isHomeVisible)
                            loadFragment(fragmentLeaderBoard, "leaderboard", fragmentHome);
                        else if (isNearByVisible)
                            loadFragment(fragmentLeaderBoard, "leaderboard", fragmentNearBy);
                        else if (isProfileVisible)
                            loadFragment(fragmentLeaderBoard, "leaderboard", fragmentProfile);
                    } else {
                        if (isHomeVisible)
                            showFragment(fragmentLeaderBoard, fragmentHome);
                        else if (isNearByVisible)
                            showFragment(fragmentLeaderBoard, fragmentNearBy);
                        else if (isProfileVisible)
                            showFragment(fragmentLeaderBoard, fragmentProfile);
                    }
                    isHomeVisible = false;
                    isNearByVisible = false;
                    isLeaderboardVisible = true;
                    isProfileVisible = false;
                }
            }
        }
    */

    @OnClick(R.id.iv_leaderboard)
    public void onTvClick() {
/*
        if (utils.isNetworkAvailable()) {
            if (!isLeaderboardVisible) {
                EventBus.getDefault().post("searchHide");
                if (!isLeaderboard) {
                    isLeaderboard = true;
                    if (isHomeVisible)
                        loadFragment(fragmentLeaderBoard, "leaderboard", fragmentHome);
                    else if (isNearByVisible)
                        loadFragment(fragmentLeaderBoard, "leaderboard", fragmentNearBy);
                    else if (isProfileVisible)
                        loadFragment(fragmentLeaderBoard, "leaderboard", fragmentProfile);
                } else {
                    if (isHomeVisible)
                        showFragment(fragmentLeaderBoard, fragmentHome);
                    else if (isNearByVisible)
                        showFragment(fragmentLeaderBoard, fragmentNearBy);
                    else if (isProfileVisible)
                        showFragment(fragmentLeaderBoard, fragmentProfile);
                }
                isHomeVisible = false;
                isNearByVisible = false;
                isLeaderboardVisible = true;
                isProfileVisible = false;
            }
        }
*/
        if (utils.isNetworkAvailable()) {
            Intent intent = new Intent(mActivity, webview.class);
            intent.putExtra("title", "Toppers");
            intent.putExtra("from", "liveRoom");
            intent.putExtra("url", Constants_api.getTopperList + SessionUser.getUser().getUser_id() + "&country=" + SessionUser.getUser().getCountry());
            startActivity(intent);
        }
    }

    @OnClick(R.id.iv_profile)
    public void onProfileClick() {
        if (utils.isNetworkAvailable()) {
            if (!isProfileVisible) {
                EventBus.getDefault().post("searchHide");
                EventBus.getDefault().post("refresh");
                if (!isProfile) {
                    isProfile = true;
                    if (isHomeVisible)
                        loadFragment(fragmentProfile, "profile", fragmentHome);
                    else if (isNearByVisible)
                        loadFragment(fragmentProfile, "profile", fragmentNearBy);
                    else if (isLeaderboardVisible)
                        loadFragment(fragmentProfile, "profile", fragmentLeaderBoard);
                } else {
                    if (isHomeVisible)
                        showFragment(fragmentProfile, fragmentHome);
                    else if (isNearByVisible)
                        showFragment(fragmentProfile, fragmentNearBy);
                    else if (isLeaderboardVisible)
                        showFragment(fragmentProfile, fragmentLeaderBoard);
                }
                isHomeVisible = false;
                isNearByVisible = false;
                isLeaderboardVisible = false;
                isProfileVisible = true;
            }
        }
    }

    public void loadHomeFragment(Fragment fragment, String name) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.frame_layout, fragment, name)
                .addToBackStack(name)
                .commit();
    }

    public void loadFragment(Fragment fragment, String name, Fragment oldFragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                .hide(oldFragment)
                .add(R.id.frame_layout, fragment, name)
                .addToBackStack(name)
                .commit();
    }

    private void showFragment(Fragment newFragment, Fragment oldFragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                .hide(oldFragment)
                .show(newFragment).addToBackStack(null)
                .commit();
    }

    @OnClick(R.id.iv_live)
    public void onClickLive() {
        if (utils.isNetworkAvailable()) {
            Intent intent = new Intent(mActivity, ActivityStreamSet.class);
            startActivity(intent);
            mChatManager.removeChatHandler(mChatHandler);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mChatManager.removeChatHandler(mChatHandler);
    }

    @Override
    public void onBackPressed() {

        if (isHomeVisible) {
            if (doubleBackToExitPressedOnce) {
                finishAffinity();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(getApplicationContext(), "Press BACK again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        } else if (isNearByVisible) {
            EventBus.getDefault().post("searchShow");
            showFragment(getSupportFragmentManager().findFragmentByTag("home"), fragmentNearBy);
        } else if (isLeaderboardVisible) {
            EventBus.getDefault().post("searchShow");
            showFragment(getSupportFragmentManager().findFragmentByTag("home"), fragmentLeaderBoard);
        } else if (isProfileVisible) {
            EventBus.getDefault().post("searchShow");
            showFragment(getSupportFragmentManager().findFragmentByTag("home"), fragmentProfile);
        }

        isHomeVisible = true;
        isNearByVisible = false;
        isLeaderboardVisible = false;
        isProfileVisible = false;
    }


    public void getUser(User mUser, ArrayList<User> mUsers, int mPosition) {
        Log.i("autolog", "mUser: " + mUser.getUsername());
        try {
            utils.showProgress();
        }catch (Exception e){
            Crashlytics.log(e.toString());
        }
        user = mUser;
        position = mPosition;
        Log.i("autolog", "position: " + position);
        users = mUsers;
/*
        try {
            if (SessionUser.getRtmLoginSession())
                mChatManager.createChannel(mUser.getUsername().trim());
            else {
                mChatManager.doLogin(mUser.getUsername().trim());
            }
        } catch (Exception e) {

        }
*/

        if (user != null) {
            String base64 = user.getProfile_pic();
            try {
                image = URLDecoder.decode(base64, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (user.getBroadcast_type().equalsIgnoreCase("solo")) {
                Log.d(TAG, "onChannelJoinSuccess: " + "solo");

                Intent intent = new Intent(mActivity, ActivityLiveRoom.class);
                intent.putExtra("mode", false);
                intent.putExtra("name", user.getUsername());
                intent.putExtra("selfname", SessionUser.getUser().getName());
                intent.putExtra("image", image);
                intent.putExtra("token", user.getActivation_code());
                intent.putExtra("broadcasterId", user.getUser_id());
                intent.putExtra("received", user.getReceived());
                Log.e(TAG, "onChannelJoinSuccess: " + user.getReceived());
                intent.putExtra("broadcaster", user);
                intent.putParcelableArrayListExtra("users", users);
                intent.putExtra("position", position);
                intent.putExtra("isFollowing", user.getIsTheUserFollowing());
                intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_AUDIENCE);
                intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, user.getUsername().trim());
                intent.putExtra("rtmname", user.getUsername().trim());
                intent.putExtra("isSwiped", false);
                intent.putExtra("PKuserId", "0");
                intent.putExtra("broad_type", "solo");
                intent.putExtra("pkTimer", user.getPkTimeLeft());
                if (Constants_app.BAudiance == 1) {
                    intent.putExtra("broadcasterAudience", false);
                    Log.i("autolog", "broadcasterAudience: " + "broadcasterAudience");
                    intent.putExtra("guestAudience", false);
                } else {
                    intent.putExtra("broadcasterAudience", false);
                    intent.putExtra("guestAudience", false);
                    Log.i("autolog", "guestAudience: " + "guestAudience");
                }
                intent.putExtra("intermediateJoin", false);
                startActivity(intent);
            } else if (user.getBroadcast_type().equalsIgnoreCase("pk")) {
                Log.d(TAG, "onChannelJoinSuccess: " + "pk");
                Intent intent = new Intent(mActivity, ActivityLiveRoom.class);
                intent.putExtra("mode", false);
                intent.putExtra("broad_type", "pk");
                intent.putExtra("name", user.getUsername());
                intent.putExtra("selfname", SessionUser.getUser().getName());
                intent.putExtra("image", image);
                intent.putExtra("token", user.getActivation_code());
                intent.putExtra("broadcasterId", user.getPk_broadcaster_id());
                intent.putExtra("PKuserId", user.getPk_guest_id());
                Log.i("autolog", "pkUser_id(): " + user.getPk_guest_id());
                intent.putExtra("received", user.getReceived());
                intent.putExtra("broadcaster", user);
                intent.putExtra("pkTimer", user.getPkTimeLeft());
                Log.i("autolog", "user.getPk_channelname(): " + user.getPk_channelname());
                Log.i("autolog", "user.getPk_channelname(): " + user.getUsername());
                if (user.getPk_channelname().equalsIgnoreCase(user.getUsername())) {
                    intent.putExtra("broadcasterAudience", true);
                    Log.i("autolog", "broadcasterAudience: " + "broadcasterAudience");
                    intent.putExtra("guestAudience", false);
                } else {
                    intent.putExtra("broadcasterAudience", false);
                    intent.putExtra("guestAudience", true);
                    Log.i("autolog", "guestAudience: " + "guestAudience");
                }
                intent.putParcelableArrayListExtra("users", users);
                intent.putExtra("position", position);
                intent.putExtra("isFollowing", user.getIsTheUserFollowing());
                intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_AUDIENCE);
                intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, user.getUsername().trim());
                intent.putExtra("rtmname", user.getPk_channelname().trim());
                intent.putExtra("isSwiped", false);
                intent.putExtra("intermediateJoin", true);
                Log.d(TAG, "onChannelJoinSuccess: " + intent.getExtras().toString());
                startActivity(intent);
            } else if (user.getBroadcast_type().equalsIgnoreCase("groupOf3")) {
                Intent intent = new Intent(mActivity, ActivityGroupCalls3.class);
                intent.putExtra("mode", false);
                intent.putExtra("name", user.getUsername());
                intent.putExtra("selfname", SessionUser.getUser().getName());
                intent.putExtra("image", image);
                intent.putExtra("token", user.getActivation_code());
                intent.putExtra("broadcasterId", user.getUser_id());
                intent.putExtra("received", user.getReceived());
                intent.putExtra("broadcaster", user);
                intent.putParcelableArrayListExtra("users", users);
                intent.putExtra("position", position);
                intent.putExtra("isFollowing", user.getIsTheUserFollowing());
                intent.putExtra("isSwiped", false);
                intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_AUDIENCE);
                intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, user.getUsername().trim());
                startActivity(intent);
            } else if (user.getBroadcast_type().equalsIgnoreCase("groupOf6")) {
                Intent intent = new Intent(mActivity, ActivityGroupCalls6.class);
                intent.putExtra("mode", false);
                intent.putExtra("name", user.getUsername());
                intent.putExtra("selfname", SessionUser.getUser().getName());
                intent.putExtra("image", image);
                intent.putExtra("token", user.getActivation_code());
                intent.putExtra("broadcasterId", user.getUser_id());
                intent.putExtra("received", user.getReceived());
                intent.putExtra("broadcaster", user);
                intent.putParcelableArrayListExtra("users", users);
                intent.putExtra("position", position);
                intent.putExtra("isFollowing", user.getIsTheUserFollowing());
                intent.putExtra("isSwiped", false);
                intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_AUDIENCE);
                intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, user.getUsername().trim());
                startActivity(intent);
            } else if (user.getBroadcast_type().equalsIgnoreCase("groupOf9")) {
                Intent intent = new Intent(mActivity, ActivityGroupCalls9.class);
                intent.putExtra("mode", false);
                intent.putExtra("name", user.getUsername());
                intent.putExtra("selfname", SessionUser.getUser().getName());
                intent.putExtra("image", image);
                intent.putExtra("token", user.getActivation_code());
                intent.putExtra("broadcasterId", user.getUser_id());
                intent.putExtra("received", user.getReceived());
                intent.putExtra("broadcaster", user);
                intent.putParcelableArrayListExtra("users", users);
                intent.putExtra("position", position);
                intent.putExtra("isFollowing", user.getIsTheUserFollowing());
                intent.putExtra("isSwiped", false);
                intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_AUDIENCE);
                intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, user.getUsername().trim());
                startActivity(intent);
            } else if (user.getBroadcast_type().equalsIgnoreCase("screenSharing")) {
                Intent intent = new Intent(mActivity, ActivityScrShareViewers.class);
                intent.putExtra("mode", false);
                intent.putExtra("name", user.getUsername());
                intent.putExtra("selfname", SessionUser.getUser().getUsername());
                intent.putExtra("image", image);
                intent.putExtra("token", user.getActivation_code());
                intent.putExtra("broadcasterId", user.getUser_id());
                intent.putExtra("received", user.getReceived());
                intent.putExtra("broadcaster", user);
                intent.putParcelableArrayListExtra("users", users);
                intent.putExtra("position", position);
                intent.putExtra("isSwiped", false);
                intent.putExtra("isFollowing", user.getIsTheUserFollowing());
                intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_AUDIENCE);
                intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, user.getUsername().trim());
                startActivity(intent);
            } /*else if (user.getBroadcast_type().equalsIgnoreCase("karaokeSolo")) {
                            Intent intent = new Intent(mActivity, ActivityKaraokeSolo.class);
                            intent.putExtra("mode", false);
                            intent.putExtra("name", user.getUsername());
                            intent.putExtra("selfname", SessionUser.getUser().getUsername());
                            intent.putExtra("image", image);
                            intent.putExtra("token", user.getActivation_code());
                            intent.putExtra("broadcasterId", user.getUser_id());
                            intent.putExtra("received", user.getReceived());
                            intent.putExtra("broadcaster", user);
                            intent.putParcelableArrayListExtra("users", users);
                            intent.putExtra("position", position);
                            intent.putExtra("isSwiped", false);
                            intent.putExtra("isFollowing", user.getIsTheUserFollowing());
                            intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_AUDIENCE);
                            intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, user.getUsername().trim());
                            startActivity(intent);
                        } else if (user.getBroadcast_type().equalsIgnoreCase("karaokeGroup")) {
                            Intent intent = new Intent(mActivity, ActivityKaraokeDual.class);
                            intent.putExtra("mode", false);
                            intent.putExtra("name", user.getUsername());
                            intent.putExtra("selfname", SessionUser.getUser().getUsername());
                            intent.putExtra("image", image);
                            intent.putExtra("token", user.getActivation_code());
                            intent.putExtra("broadcasterId", user.getUser_id());
                            intent.putExtra("received", user.getReceived());
                            intent.putExtra("broadcaster", user);
                            intent.putParcelableArrayListExtra("users", users);
                            intent.putExtra("position", position);
                            intent.putExtra("isSwiped", false);
                            intent.putExtra("isFollowing", user.getIsTheUserFollowing());
                            intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_AUDIENCE);
                            intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, user.getUsername().trim());
                            startActivity(intent);
                        }*/ else if (user.getBroadcast_type().equalsIgnoreCase("audio")) {
                Intent intent = new Intent(mActivity, ActivityAudioCall.class);
                intent.putExtra("mode", false);
                intent.putExtra("name", user.getUsername());
                intent.putExtra("selfname", SessionUser.getUser().getUsername());
                intent.putExtra("image", image);
                intent.putExtra("token", user.getActivation_code());
                intent.putExtra("broadcasterId", user.getUser_id());
                intent.putExtra("received", user.getReceived());
                intent.putExtra("broadcaster", user);
                intent.putParcelableArrayListExtra("users", users);
                intent.putExtra("position", position);
                intent.putExtra("isSwiped", false);
                intent.putExtra("isFollowing", user.getIsTheUserFollowing());
                intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_AUDIENCE);
                intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, user.getUsername().trim());
                startActivity(intent);
            } else {
                showToast("User is Offline!");
            }
        }



    }

    private void getUsername(String user) {
        if (user.length() == 0) {
            showToast("dummy");
        } else {
            username = "";
            if (SessionUser.getRtmLoginSession())
                mChatManager.createChannel(user.trim());
            else {
                mChatManager.doLogin(user.trim());
            }
        }
    }


    public void checkAppVersion(String mVersionName, String mVersionCode, String type) {
        if (!versionName.equalsIgnoreCase(mVersionName) && versionCode != Integer.valueOf(mVersionCode)) {
            switch (type) {
                case "Soft":
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityHome.this);
                    alertDialogBuilder.setMessage("Please update to new version!");
                    alertDialogBuilder.setPositiveButton("Update",
                            (arg0, arg1) -> {
                                openPlayStore();
                                arg0.dismiss();
                            });

                    alertDialogBuilder.setNegativeButton("Later",
                            (arg0, arg1) -> {
                                arg0.dismiss();
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.setCancelable(true);
                    alertDialog.setCanceledOnTouchOutside(true);
                    alertDialog.show();
                    break;
                case "Hard":
                    isForceUpdate = true;
                    AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(ActivityHome.this);
                    alertDialogBuilder1.setMessage("Please update to new version!");
                    alertDialogBuilder1.setPositiveButton("Update",
                            (arg0, arg1) -> {
                                openPlayStore();
                                arg0.dismiss();
                            });

                    AlertDialog alertDialog1 = alertDialogBuilder1.create();
                    alertDialog1.setCancelable(false);
                    alertDialog1.setCanceledOnTouchOutside(false);
                    alertDialog1.show();
                    break;
            }
        }
    }

    private void openPlayStore() {
        final String appPackageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }


    private void doLogin1(String mChannelName) {
        mRtmClient.login(null, SessionUser.getUser().getUsername().trim(), new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                Log.e(TAG, "login success");
                mActivity.runOnUiThread(() -> {
                    SessionUser.isRtmLoggedIn(true);
                    mChatManager.createChannel(mChannelName);
                });
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                Log.e(TAG, "login failed: " + errorInfo.getErrorCode());
                mActivity.runOnUiThread(() -> {
                    if (errorInfo.getErrorCode() == 8) {
                        mChatManager.createChannel(SessionUser.getUser().getUsername().trim());
                        logoutRtm(SessionUser.getUser().getUsername().trim());
                    } else {
                        mChatManager.doLogin();
                    }
                });
            }
        });
    }



    public void logoutRtm(String mChannelname) {
        mRtmClient.logout(new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e(TAG, "logout onSuccess: ");
                doLogin1(mChannelname);
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                Log.e(TAG, "logout Error: " + errorInfo.getErrorCode() + "  " + errorInfo.getErrorDescription());
            }
        });
    }

}