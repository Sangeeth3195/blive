package com.blive.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blive.constant.Constants_api;
import com.blive.model.URL;
import com.blive.service.ServiceGenerator;
import com.blive.service.linkshorten;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blive.adapter.AdapterActiveViewers;
import com.blive.adapter.AdapterFriendsShare;
import com.blive.adapter.AdapterGroupTopper;
import com.blive.adapter.AdapterImages;
import com.blive.adapter.AdapterMessage;
import com.blive.agora.rtmChat.ChatHandler;
import com.blive.agora.rtmChat.ChatManager;
import com.blive.agora.screenCapture.GLRender;
import com.blive.agora.screenCapture.ImgTexFrame;
import com.blive.agora.screenCapture.ScreenCapture;
import com.blive.agora.screenCapture.SinkConnector;
import com.blive.BLiveApplication;
import com.blive.BuildConfig;
import com.blive.constant.Constants_app;
import com.blive.db.SqlDb;
import com.blive.model.Audience;
import com.blive.model.EntranceEffect;
import com.blive.model.FollowResponse;
import com.blive.model.GenericResponse;
import com.blive.model.Gift;
import com.blive.model.GiftMessage;
import com.blive.model.GiftResponse;
import com.blive.model.MessageBean;
import com.blive.model.ProfileResponse;
import com.blive.model.TopFansResponse;
import com.blive.model.User;
import com.blive.R;
import com.blive.model.UsersResponse;
import com.blive.service.ApiClientBase;
import com.blive.service.ApiInterface;
import com.blive.service.FloatingViewService;
import com.blive.session.SessionUser;
import com.blive.utils.DividerLine;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.crashlytics.android.Crashlytics;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.JsonArray;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.OnClick;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.AgoraVideoFrame;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmChannel;
import io.agora.rtm.RtmChannelMember;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmMessage;
import io.agora.rtm.RtmStatusCode;
import retrofit2.Call;
import retrofit2.Response;


public class ActivityScreenSharing extends BaseBackActivity implements AdapterGroupTopper.Listener,
        AdapterFriendsShare.Listener, AdapterImages.ListenerImage, AdapterActiveViewers.ListenerActiveViers, AdapterMessage.ListenerMessage {

    private ScreenCapture mScreenCapture;
    private GLRender mScreenGLRender;
    private RtcEngine mRtcEngine;
    private boolean mIsLandSpace = false, isAPICalled = false, isRefreshing = false, isUserListEnd = false, isEntranceEffects = false, isBroadcaster = true,
            isArrived = false, isGoldAdding = false, isGiftShowing = false, isClose = false, isBroadcastEnded = false, isFirst = false, isGiftAPI = false, isNextUser = false,
            isSwiped = false, isPermissiondenied = false, isAudienceFollowing = false, isTextMuted = false, isClickedProfile = false, isClicked = false;
    private String channelName = "", time = "", level = "", selfName = "", image = "", broadcasterId = "", isFollowing = "", dailyAndWeeklyGold = "",
            moonValue = "", oldMoonImageUrl = "", moonLevelCount = "", moonLevel = "", url_url = "";
    private int viewers = 0, likes = 0, channelUserCount, gold = 0, oldGold = 0, position = -1, page = 1, temp1 = 0, temp2 = 0, temp = 0,
            broadcasterUid = 0, lastPage = 0, muted = 0, oldMoonImage = 0;
    private User broadcaster;
    private long startTime, endTime;
    private LinearLayout llChat, llNormalGift, cvGiftMessage, llUnFollow, llFollow, llKickOut;
    private RecyclerView rvMessages, rv_guestTopperList, rvFriendsList, rvImages;
    private AdapterFriendsShare adapterFriends;
    private ImageView iv, ivMic, ivUser, ivPic, ivEntrance, starRatings, starRatings1, ivGiftItem, ivActiveViewers, ivUserProfileffect, ivFollow;
    private TextView tvNoContributors, tvReceived, tvNoFriendsList, tvEntranceName, tvCount, tvGiftName, tvMoonLevelCount, tvGiftMessage, tvStarLevel;
    private CheckBox checkBoxSelectAllFriends;
    private AdapterMessage adapter;
    private ArrayList<User> usersFriendsList;
    private ArrayList<User> users;
    private List<MessageBean> messageBeanList;
    private ArrayList<EntranceEffect> entranceEffects;
    private ArrayList<Audience> mAudiences;
    private ArrayList<GiftMessage> giftMessages;
    private ArrayList<String> messagesList;
    private ArrayList<Gift> giftsList, giftTools, freeGifts;
    final Handler entranceHandler = new Handler();
    final Handler giftHandler = new Handler();
    private ShineButton bFollowers, bFriends, bFans;
    private RelativeLayout rlCoins;
    private ProgressBar progressBar;
    private Button btnSendFriends;
    private AlertDialog alertDialog;
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    private RtmClient mRtmClient;
    private ChatManager mChatManager;
    private RtmChannel mRtmChannel;
    private ChatHandler mChatHandler;

    private void initModules() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        if (mScreenGLRender == null) {
            mScreenGLRender = new GLRender();
        }
        if (mScreenCapture == null) {
            mScreenCapture = new ScreenCapture(getApplicationContext(), mScreenGLRender, metrics.densityDpi);
        }

        mScreenCapture.mImgTexSrcConnector.connect(new SinkConnector<ImgTexFrame>() {
            @Override
            public void onFormatChanged(Object obj) {
                Log.d(TAG, "onFormatChanged " + obj.toString());
            }

            @Override
            public void onFrameAvailable(ImgTexFrame frame) {
                Log.d(TAG, "onFrameAvailable " + frame.toString());

                if (mRtcEngine == null) {
                    return;
                }

                AgoraVideoFrame vf = new AgoraVideoFrame();
                vf.format = AgoraVideoFrame.FORMAT_TEXTURE_OES;
                vf.timeStamp = frame.pts;
                vf.stride = frame.mFormat.mWidth;
                vf.height = frame.mFormat.mHeight;
                vf.textureID = frame.mTextureId;
                vf.syncMode = true;
                vf.eglContext14 = mScreenGLRender.getEGLContext();
                vf.transform = frame.mTexMatrix;

                mRtcEngine.pushExternalVideoFrame(vf);
            }
        });

        mScreenCapture.setOnScreenCaptureListener(new ScreenCapture.OnScreenCaptureListener() {
            @Override
            public void onStarted() {
                Log.d(TAG, "Screen Record Started");
            }

            @Override
            public void onError(int err) {
                Log.d(TAG, "onError " + err);
                switch (err) {
                    case ScreenCapture.SCREEN_ERROR_SYSTEM_UNSUPPORTED:
                        isPermissiondenied = true;
                        callInActiveAPI();
                        break;
                    case ScreenCapture.SCREEN_ERROR_PERMISSION_DENIED:
                        isPermissiondenied = true;
                        callInActiveAPI();
                        break;
                }
            }
        });

        WindowManager wm = (WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        int screenWidth = wm.getDefaultDisplay().getWidth();
        int screenHeight = wm.getDefaultDisplay().getHeight();
        if ((mIsLandSpace && screenWidth < screenHeight) ||
                (!mIsLandSpace) && screenWidth > screenHeight) {
            screenWidth = wm.getDefaultDisplay().getHeight();
            screenHeight = wm.getDefaultDisplay().getWidth();
        }

        setOffscreenPreview(screenWidth, screenHeight);

        if (mRtcEngine == null) {
            try {
                mRtcEngine = RtcEngine.create(getApplicationContext(), BuildConfig.private_app_id, new IRtcEngineEventHandler() {
                    @Override
                    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
                        //Log.e(TAG, "onJoinChannelSuccess " + channel + " " + elapsed);
                    }

                    @Override
                    public void onWarning(int warn) {
                        Log.d(TAG, "onWarning " + warn);
                    }

                    @Override
                    public void onError(int err) {
                        Log.d(TAG, "onError " + err);
                    }

                    @Override
                    public void onAudioRouteChanged(int routing) {
                        Log.d(TAG, "onAudioRouteChanged " + routing);
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));

                throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
            }

            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            mRtcEngine.enableVideo();

            if (mRtcEngine.isTextureEncodeSupported()) {
                mRtcEngine.setExternalVideoSource(true, true, true);
            } else {
                throw new RuntimeException("Can not work on device do not supporting texture" + mRtcEngine.isTextureEncodeSupported());
            }

            mRtcEngine.setVideoProfile(Constants.VIDEO_PROFILE_480P_4, true);

            mRtcEngine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
        }
    }

    private void deInitModules() {
        RtcEngine.destroy();
        mRtcEngine = null;

        if (mScreenCapture != null) {
            mScreenCapture.release();
            mScreenCapture = null;
        }

        if (mScreenGLRender != null) {
            mScreenGLRender.quit();
            mScreenGLRender = null;
        }

        mChatManager.leaveChannel();
        mChatManager.removeChatHandler(mChatHandler);
    }

    /**
     * Set offscreen preview.
     *
     * @param width  offscreen width
     * @param height offscreen height
     * @throws IllegalArgumentException
     */
    public void setOffscreenPreview(int width, int height) throws IllegalArgumentException {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Invalid offscreen resolution");
        }

        mScreenGLRender.init(width, height);
    }

    private void startCapture() {
        mScreenCapture.start();
    }

    private void stopCapture() {
        mScreenCapture.stop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_sharing);
        BLiveApplication.setCurrentActivity(this);
        changeStatusBarColor();
        initUI();
    }

    protected void initUI() {
        Intent i = getIntent();
        channelName = i.getStringExtra("name");
        selfName = i.getStringExtra("selfname");
        channelUserCount = i.getIntExtra("usercount", 0);
        image = i.getStringExtra("image");
        broadcasterId = i.getStringExtra("broadcasterId");
        isFollowing = i.getStringExtra("isFollowing");
        broadcaster = i.getParcelableExtra("broadcaster");
        position = i.getIntExtra("position", -1);
        users = i.getParcelableArrayListExtra("users");
        isNextUser = i.getBooleanExtra("isSwiped", false);

        mChatManager = BLiveApplication.getInstance().getChatManager();
        mRtmClient = mChatManager.getRtmClient();
        mRtmChannel = mChatManager.getRtmChannel();
        mChatManager.createChannel(SessionUser.getUser().getUsername().trim());
        mChatHandler = new ChatHandler() {
            @Override
            public void onLoginSuccess() {
                Log.e(TAG, "onLoginSuccess: ");
            }

            @Override
            public void onLoginFailed(ErrorInfo errorInfo) {
                Log.e(TAG, "onLoginFailed: ");
            }

            @Override
            public void onChannelJoinSuccess() {
                Log.e(TAG, "onChannelJoinSuccess: ");
            }

            @Override
            public void onChannelJoinFailed(ErrorInfo errorCode) {
                Log.e(TAG, "onChannelJoinFailed: ");
            }

            @Override
            public void onMessageReceived(RtmMessage message, RtmChannelMember fromMember) {
                runOnUiThread(() -> onMessageReceive(message, fromMember));
            }

            @Override
            public void onMemberJoined(RtmChannelMember rtmChannelMember) {
                runOnUiThread(() -> {

                });
            }

            @Override
            public void onMemberLeft(RtmChannelMember rtmChannelMember) {
                runOnUiThread(() -> {
                    if (!isBroadcastEnded) {
                        callAudiencesAPI(broadcaster.getUser_id());
                    }
                });
            }
        };

        mChatManager.addChantHandler(mChatHandler);

        SessionUser.isScreenSharing(true);
        tvStarLevel = findViewById(R.id.tv_star_level);
        ivActiveViewers = findViewById(R.id.ivActiveViewers);
        tvCount = findViewById(R.id.tv_count);
        rlCoins = findViewById(R.id.rl_coins);
        bFans = findViewById(R.id.bFans);
        bFollowers = findViewById(R.id.bFollowers);
        bFriends = findViewById(R.id.bFriends);
        tvGiftMessage = findViewById(R.id.tv_gift_message);
        cvGiftMessage = findViewById(R.id.cv_gift_message);
        tvMoonLevelCount = findViewById(R.id.tvMoonLevelCount);
        tvGiftName = findViewById(R.id.tv_gift_name);
        ivGiftItem = findViewById(R.id.iv_gift_item);
        bFriends = findViewById(R.id.bFriends);
        bFollowers = findViewById(R.id.bFollowers);
        bFans = findViewById(R.id.bFans);
        starRatings = findViewById(R.id.moonLevelStar);
        starRatings1 = findViewById(R.id.moonLevelStar1);
        rvImages = findViewById(R.id.rv_images);
        ivEntrance = findViewById(R.id.iv_entrance);
        tvEntranceName = findViewById(R.id.tv_entranceName);
        tvReceived = findViewById(R.id.tv_received);
        ivUser = findViewById(R.id.iv_user);
        ivPic = findViewById(R.id.iv_picture);
        ivMic = findViewById(R.id.iv_mic);
        iv = findViewById(R.id.iv);
        ivFollow = findViewById(R.id.iv_follow);
        ImageView buttonMsg = findViewById(R.id.btn_msg);
        llChat = findViewById(R.id.ll_chat);
        rvMessages = findViewById(R.id.rv_message_list);
        ImageButton imageButton = findViewById(R.id.sendButton);
        imageButton.setOnClickListener(sendButtonListener);

        ivUserProfileffect = findViewById(R.id.iv_profile_dp_affect);
        int level = Integer.parseInt(broadcaster.getLevel());
        Log.e(TAG, "initViews: Level " + level);

        Glide.with(getApplicationContext())
                .load(broadcaster.getTools_applied())
                .into(ivUserProfileffect);

        bFans.init(mActivity);
        bFollowers.init(mActivity);
        bFriends.init(mActivity);

        bFollowers.setChecked(true);
        bFriends.setChecked(true);
        bFans.setChecked(true);
        bFollowers.setEnabled(false);
        bFriends.setEnabled(false);
        bFans.setEnabled(false);

        TextView textRoomName = findViewById(R.id.room_name);
        textRoomName.setText(broadcaster.getName());
        startTime = System.currentTimeMillis();

        LinearLayoutManager layoutManagers = new LinearLayoutManager(this);
        layoutManagers.setOrientation(OrientationHelper.VERTICAL);
        rvMessages.setLayoutManager(layoutManagers);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        rvImages.setLayoutManager(layoutManager1);

        if (broadcaster != null) {
            oldGold = Integer.valueOf(broadcaster.getOver_all_gold());
            gold = Integer.valueOf(broadcaster.getOver_all_gold());
            tvReceived.setText(String.valueOf(gold));
        }

        if (!isFirst) {
            isFirst = true;
            if (utils.isNetworkAvailable()) {
                utils.showProgress();
                final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
                Call<GiftResponse> call = apiClient.getGifts("screenSharing", SessionUser.getUser().getUser_id());
                call.enqueue(new retrofit2.Callback<GiftResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GiftResponse> call, @NonNull Response<GiftResponse> response) {
                        GiftResponse giftResponse = response.body();
                        utils.hideProgress();
                        if (response.code() == 200) {
                            if (giftResponse != null) {
                                if (giftResponse.getStatus().equalsIgnoreCase("success")) {
                                    onGiftsSuccess(giftResponse.getData().getGifts(), giftResponse.getData().getTools(), giftResponse.getData().getFreeGits());
                                } else {
                                    showToast(giftResponse.getMessage());
                                }
                            } else {
                                showToast(getString(R.string.server_error));
                            }
                        } else {
                            checkResponseCode(response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GiftResponse> call, @NonNull Throwable t) {
                        showToast(t.getMessage());
                    }
                });
            }
        }

        Glide.with(this)
                .load(image)
                .into(ivPic);

        Glide.with(this)
                .load(image)
                .into(ivUser);

        messagesList = new ArrayList<>();
        giftMessages = new ArrayList<>();
        mAudiences = new ArrayList<>();
        entranceEffects = new ArrayList<>();
        messageBeanList = new ArrayList<>();
        giftsList = new ArrayList<>();
        giftTools = new ArrayList<>();
        freeGifts = new ArrayList<>();
        usersFriendsList = new ArrayList<>();

        getLevel();

        broadcaster = SessionUser.getUser();

        adapter = new AdapterMessage(this, messageBeanList);
        rvMessages.setAdapter(adapter);
        adapter.setOnClickListener(this);

        moonValue = broadcaster.getOver_all_gold();
        if (moonValue.isEmpty())
            moonValue = "0";
        int times = Integer.valueOf(moonValue) / 8100;
        tvMoonLevelCount.setText("5x" + String.valueOf(times));
        loadStarImage(moonValue);

        MessageBean messageBean = new MessageBean(selfName, getResources().getString(R.string.warning), true, true, false);
        messageBeanList.add(messageBean);
        adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
        rvMessages.scrollToPosition(messageBeanList.size() - 1);

        initModules();
        startCapture();
        mRtcEngine.muteAllRemoteAudioStreams(true);
        mRtcEngine.muteAllRemoteVideoStreams(true);
        mRtcEngine.joinChannel(null, channelName, "BLive", SessionUser.getUser().getId());

        buttonMsg.setOnClickListener(v -> {
            llChat.setVisibility(View.VISIBLE);
            EditText userTypedMessage = findViewById(R.id.userMessageBox);
            userTypedMessage.requestFocus();
            // Show soft keyboard for the user to enter the value.
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.showSoftInput(userTypedMessage, InputMethodManager.SHOW_IMPLICIT);
        });

        ivMic.setOnClickListener(v -> {
            Object tag = v.getTag();
            boolean flag = true;
            if (tag != null && (boolean) tag) {
                flag = false;
            }
            mRtcEngine.muteLocalAudioStream(flag);
            ImageView button = (ImageView) v;
            button.setTag(flag);
            if (flag) {
                button.setBackground(getResources().getDrawable(R.mipmap.micmute));
                muted = 1;
            } else {
                button.setBackground(getResources().getDrawable(R.mipmap.mic));
                muted = 0;
            }
        });

        ivActiveViewers.setOnClickListener(v -> {
            showActiveViewers();
        });
    }

    @Override
    protected void onStop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isPermissiondenied) {
                if (Settings.canDrawOverlays(this)) {
                    Intent i = new Intent(BLiveApplication.getStaticContext(), FloatingViewService.class);
                    i.putExtra("inputExtra", "Blive");
                    i.putExtra("selfname", selfName);
                    i.putExtra("channelName", channelName);
                    i.putExtra("startTime", String.valueOf(startTime));
                    i.putExtra("startTime", startTime);
                    i.putExtra("mute", muted);
                    startService(i);
                }
            }
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        try {
            if (!isPermissiondenied)
                stopService(new Intent(BLiveApplication.getStaticContext(), FloatingViewService.class));
            getProfileDetails();
        } catch (Exception exception) {
            Crashlytics.logException(exception);
        }
        super.onResume();
    }

    private void getProfileDetails() {
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<ProfileResponse> call = apiClient.getProfile(SessionUser.getUser().getUser_id(), SessionUser.getUser().getUser_id());
            call.enqueue(new retrofit2.Callback<ProfileResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                    utils.hideProgress();
                    ProfileResponse profileResponse = response.body();
                    if (response.code() == 200) {
                        if (profileResponse != null) {
                            if (profileResponse.getStatus().equalsIgnoreCase("success")) {
                                SessionUser.saveUser(profileResponse.getData().getUser());
                                gold = Integer.valueOf(profileResponse.getData().getUser().getOver_all_gold());
                                tvReceived.setText(profileResponse.getData().getUser().getOver_all_gold());
                                moonValue = profileResponse.getData().getUser().getMoon_value();
                                if (moonValue.isEmpty())
                                    moonValue = "0";
                                loadStarImage(moonValue);
                            } else {
                                showToast(profileResponse.getMessage());
                            }
                        } else {
                            showToast(getString(R.string.server_error));
                        }
                    } else {
                        checkResponseCode(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                    utils.hideProgress();
                    showToast(t.getMessage());
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            //Check if the permission is granted or not.
            if (resultCode == RESULT_OK) {
                /* initializeView();*/
            } else { //Permission is not available
                Toast.makeText(this,
                        "Draw over other app permission not available. Closing the application",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private View.OnClickListener sendButtonListener = v -> {
        try {
            getLevel();
            EditText userTypedMessage = findViewById(R.id.userMessageBox);
            String msg = userTypedMessage.getText().toString();
            msg = msg.trim();
            if (msg.length() > 0) {
                MessageBean messageBean = new MessageBean(selfName, SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " : " + msg, true, false, false);
                messageBeanList.add(messageBean);
                adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                rvMessages.scrollToPosition(messageBeanList.size() - 1);
                sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " : " + msg);
                userTypedMessage.setText("");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(userTypedMessage.getWindowToken(), 0);
                new Handler().postDelayed(() -> llChat.setVisibility(View.GONE), 500);
            } else {
                showToast("Can't send Empty Message");
            }
        } catch (Exception e) {
            Log.e(TAG, "onClick: " + e);
            Crashlytics.logException(e);
        }
    };

    private void getLevel() {
        level = SessionUser.getUser().getLevel();
        if (level.length() == 1) {
            level = "0" + level;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isPermissiondenied) {
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("VideoMute", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit().clear();
            editor.apply();
            stopService(new Intent(BLiveApplication.getStaticContext(), FloatingViewService.class));
            deInitModules();
        }
    }

    private void callInActiveAPI() {
        if (utils.isNetworkAvailable()) {
            endTime = System.currentTimeMillis();
            long mills = endTime - startTime;
            int hours = (int) (mills / (1000 * 60 * 60));
            int mins = (int) (mills / (1000 * 60)) % 60;
            int seconds = (int) (mills / 1000) % 60;
            String broadcastingTime = String.valueOf(hours) + ":" + String.valueOf(mins) + ":" + String.valueOf(seconds);
            utils.showProgress();
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<GenericResponse> call = apiClient.goLive(SessionUser.getUser().getUser_id(), "INACTIVE", "screenSharing", broadcastingTime, "", "", "", "");
            call.enqueue(new retrofit2.Callback<GenericResponse>() {
                @Override
                public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                    utils.hideProgress();
                    GenericResponse genericResponse = response.body();
                    if (response.code() == 200) {
                        if (genericResponse != null) {
                            if (genericResponse.getStatus().equalsIgnoreCase("success")) {
                                onStatusSuccess();
                            } else {
                                showToast(genericResponse.getMessage());
                            }
                        } else {
                            utils.showToast(getString(R.string.server_error));
                        }
                    } else {
                        checkResponseCode(response.code());
                    }
                }
                @Override
                public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                    showToast(t.getMessage());
                }
            });
        }
    }
    public void onStatusSuccess() {
        if (isPermissiondenied) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityScreenSharing.this);
            alertDialogBuilder.setTitle("Alert");
            alertDialogBuilder.setMessage("Due to permission being denied Application will be restarted!");
            alertDialogBuilder.setPositiveButton("Ok",
                    (arg0, arg1) -> {
                        callRestartApp();
                    });

            alertDialog = alertDialogBuilder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setCancelable(false);
            alertDialog.show();
        } else {
            deInitModules();

            endTime = System.currentTimeMillis();

            long mills = endTime - startTime;
            int hours = (int) (mills / (1000 * 60 * 60));
            int mins = (int) (mills / (1000 * 60)) % 60;

            if (hours > 1 && mins > 1)
                time = hours + " Hours " + mins + " minutes";
            else if (hours == 1 && mins > 1)
                time = hours + " Hour " + mins + " minutes";
            else
                time = hours + " Hour " + mins + " minute";

            Intent intent = new Intent(this, ActivityStreamDetails.class);
            intent.putExtra("gold", String.valueOf(gold - oldGold));
            intent.putExtra("viewers", String.valueOf(viewers));
            intent.putExtra("likes", String.valueOf(likes));
            intent.putExtra("time", String.valueOf(time));
            intent.putExtra("from", "screenShare");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void callRestartApp() {
        Intent mStartActivity = new Intent(this, ActivitySplash.class);
        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mStartActivity);
        finish();
        Runtime.getRuntime().exit(0);
        // System.exit(0);
    }

    public void onClickShare(View view) {
        showSharingDialog(ActivityScreenSharing.this);
    }

    public void onGoldLayoutClicked(View view) {
        showTopContributors(broadcaster.getName(), tvReceived.getText().toString());
    }

    public void onClickMoonLevel(View view) {
        if (utils.isNetworkAvailable()) {
            dailyAndWeeklyGold = "daily";
            showTopContributors(broadcaster.getName(), tvReceived.getText().toString());
        }
    }

    @Override
    public void onBackPressed() {
        if (llChat.getVisibility() == View.VISIBLE) {
            llChat.setVisibility(View.GONE);
        } else {
            if (isBroadcaster) {
                DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            callStatusAPI();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.dismiss();
                            break;
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Quit Broadcast ?").setPositiveButton("Sure", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        }
    }

    private void callStatusAPI() {
        isClose = true;
        if (utils.isNetworkAvailable()) {
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("VideoMute", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit().clear();
            editor.apply();
            stopCapture();
            getLevel();
            Constants_app.cleanMessageListBeanList();
            callInActiveAPI();
        }
    }

    /**
     * API CALLBACK: rtm channel event listener
     */
    private void onMessageReceive(final RtmMessage text, final RtmChannelMember fromMember) {
        runOnUiThread(() -> {
            String account = fromMember.getUserId();
            String msg = text.getText();
            Log.e(TAG, "onMessageReceived account = " + account + " msg = " + msg);
            MessageBean messageBean;
            if (!account.equals(selfName)) {
                if (msg.contains("enTraNceEffEct")) {
                    String id = msg.substring(0, 8);
                    String message = msg;
                    message = message.replace(id, "");
                    message = message.replace("enTraNceEffEct", "");
                    message = message.trim();
                    EntranceEffect entranceEffect = new EntranceEffect();
                    entranceEffect.setAccount(message);
                    entranceEffect.setUrl(message);
                    entranceEffects.add(entranceEffect);
                    if (entranceEffects.size() > 0) {
                        if (!isEntranceEffects) {
                            callEntranceEffect(temp1);
                        }
                    }
                } else if (msg.contains("has bEEn kicKed OuT")) {
                    if (!isBroadcaster) {
                        String id = msg.substring(0, 6);
                        if (id.equalsIgnoreCase(SessionUser.getUser().getUser_id())) {
                            callAlertKickOut();
                        } else {
                            msg = msg.replace("has bEEn kicKed OuT", "has been kicked out");
                            messageBean = new MessageBean(account, msg, false, false, false);
                            messageBeanList.add(messageBean);
                            adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                            rvMessages.scrollToPosition(messageBeanList.size() - 1);
                        }
                    }
                } else if (msg.contains(": Has sent gIfTsEnTtOyOU")) {
                    String message = msg;
                    message = message.replace("gIfTsEnTtOyOU", "");

                    messageBean = new MessageBean(account, message, false, false, true);
                    messageBeanList.add(messageBean);
                    adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                    rvMessages.scrollToPosition(messageBeanList.size() - 1);

                    GiftMessage giftMessage = new GiftMessage();
                    giftMessage.setAccount(account);
                    giftMessage.setMessage(message);
                    giftMessages.add(giftMessage);
                    messagesList.add(message);
                    if (messagesList.size() > 0) {
                        if (!isGoldAdding)
                            callAddGiftValue(temp2);
                    }
                    if (giftMessages.size() > 0) {
                        if (!isGiftShowing) {
                            callSwitchCase(temp);
                        }
                    }
                } else if (msg.contains("h@s B&En T*xT MuTe$")) {

                    String message = msg;
                    message = message.replace("h@s B&En T*xT MuTe$", "has been text muted");

                    messageBean = new MessageBean(account, message, false, false, true);
                    messageBeanList.add(messageBean);
                    adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                    rvMessages.scrollToPosition(messageBeanList.size() - 1);

                    if (!isBroadcaster) {
                        String id = msg.substring(0, 6);
                        if (id.equalsIgnoreCase(SessionUser.getUser().getUser_id())) {
                            isTextMuted = true;
                        }
                    }
                } else if (msg.contains("h@s B&En T*xT UnMuTe$")) {

                    String message = msg;
                    message = message.replace("h@s B&En T*xT UnMuTe$", "has been text Unmuted");

                    messageBean = new MessageBean(account, message, false, false, true);
                    messageBeanList.add(messageBean);
                    adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                    rvMessages.scrollToPosition(messageBeanList.size() - 1);

                    String id = msg.substring(0, 6);
                    if (id.equalsIgnoreCase(SessionUser.getUser().getUser_id())) {
                        isTextMuted = false;
                    }
                } else {
                    messageBean = new MessageBean(account, msg, false, false, false);
                    messageBeanList.add(messageBean);
                    adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                    rvMessages.scrollToPosition(messageBeanList.size() - 1);

                    if (msg.contains("liked")) {
                        likes = likes + 1;
                    } else if (msg.contains("has arrived")) {
                        viewers = viewers + 1;
                        callAudiencesAPI(broadcaster.getUser_id());
                    } else if (msg.contains("AdMIn hA$ bLoCkEd yOu tEmPoRAriLy")) {
                        Intent intent = new Intent(BLiveApplication.getCurrentActivity(), ActivityBlocked.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        BLiveApplication.getCurrentActivity().finish();
                        BLiveApplication.getCurrentActivity().startActivity(intent);
                    }
                }
            }
        });
    }

    /**
     * API CALL: send message to a channel
     */
    private void sendChannelMessage(String msg) {
        try {
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (errorCode) {
                                case RtmStatusCode.ChannelMessageError.CHANNEL_MESSAGE_ERR_TIMEOUT:
                                case RtmStatusCode.ChannelMessageError.CHANNEL_MESSAGE_ERR_FAILURE:
                                    showToast(getString(R.string.send_msg_failed));
                                    break;
                            }
                        }
                    });
                }
            });
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    private void callAlertKickOut() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityScreenSharing.this);
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setMessage("You have been kicked out of the room !");
        alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();

        new Handler().postDelayed(() -> {
            if (alertDialog.isShowing()) {
                alertDialog.dismiss();
                mChatManager.leaveChannel();
                mChatManager.removeChatHandler(mChatHandler);
                finishAffinity();
                changeActivity(ActivityHome.class);
            }
        }, 3000);
    }

    private void callAudiencesAPI(String broadcasterId) {
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<UsersResponse> call = apiClient.getAudiences(SessionUser.getUser().getUser_id(), broadcasterId);
            call.enqueue(new retrofit2.Callback<UsersResponse>() {
                @Override
                public void onResponse(@NonNull Call<UsersResponse> call, @NonNull Response<UsersResponse> response) {
                    UsersResponse usersResponse = response.body();
                    if (response.code() == 200) {
                        if (usersResponse != null) {
                            if (usersResponse.getStatus().equalsIgnoreCase("success")) {
                                onImagesSuccess(usersResponse.getData().getAudiences(), usersResponse.getData().getEntranceEffect(), usersResponse.getData().getViewers_count());
                            } else {
                                showToast(usersResponse.getMessage());
                            }
                        } else {
                            showToast(getString(R.string.server_error));
                        }
                    } else {
                        checkResponseCode(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UsersResponse> call, @NonNull Throwable t) {
                    showToast(t.getMessage());
                }
            });
        }
    }

    public void showTopContributors(String username, String overAllGold) {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityScreenSharing.this);
        View parentView = getLayoutInflater().inflate(R.layout.item_gold_top_list, null);
        bottomSheetDialog.setContentView(parentView);
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) parentView.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        bottomSheetDialog.show();

        tvNoContributors = parentView.findViewById(R.id.tv_no_contributors);
        rv_guestTopperList = parentView.findViewById(R.id.rv_toppers);
        progressBar = parentView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        ImageView ivBroadcasterProfile = parentView.findViewById(R.id.iv_broadcaster_profile);
        TextView ivBroadcasterName = parentView.findViewById(R.id.broadcasterName);
        TextView ivBroadcasterGoldCount = parentView.findViewById(R.id.tv_broadcaster_gold_count);
        ImageView ivEffect = parentView.findViewById(R.id.iv_effect);

        ivBroadcasterName.setText(username);
        ivBroadcasterGoldCount.setText(overAllGold);

        Glide.with(getApplicationContext())
                .load(broadcaster.getTools_applied())
                .into(ivEffect);

        if (!image.isEmpty()) {
            Picasso.get().load(image).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivBroadcasterProfile);
        } else {
            Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivBroadcasterProfile);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rv_guestTopperList.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        rv_guestTopperList.setNestedScrollingEnabled(false);
        if (utils.isNetworkAvailable()) {
            progressBar.setVisibility(View.VISIBLE);
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<TopFansResponse> call = apiClient.getTopFans(broadcaster.getUser_id());
            call.enqueue(new retrofit2.Callback<TopFansResponse>() {
                @Override
                public void onResponse(@NonNull Call<TopFansResponse> call, @NonNull Response<TopFansResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    TopFansResponse topFansResponse = response.body();
                    if (response.code() == 200) {
                        if (topFansResponse != null) {
                            if (topFansResponse.getStatus().equalsIgnoreCase("success")) {
                                onTopListSuccess(topFansResponse.getData().getHourlyUsers(), topFansResponse.getData().getWeeklyUsers());
                            } else {
                                showToast(topFansResponse.getMessage());
                            }
                        } else {
                            showToast(getString(R.string.server_error));
                        }
                    } else {
                        checkResponseCode(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<TopFansResponse> call, @NonNull Throwable t) {
                    showToast(t.getMessage());
                }
            });
        }
    }

    public void onTopListSuccess(ArrayList<User> usersDaily, ArrayList<User> weeklyUsersGold) {
        try {
            if (usersDaily.size() > 0 || weeklyUsersGold.size() > 0) {

                if (dailyAndWeeklyGold.equals("daily")) {
                    dailyAndWeeklyGold = "";
                    AdapterGroupTopper adapterTopFans = new AdapterGroupTopper(this, usersDaily);
                    adapterTopFans.setOnClickListener(this);
                    rv_guestTopperList.setVisibility(View.VISIBLE);
                    rv_guestTopperList.setAdapter(adapterTopFans);
                } else {
                    AdapterGroupTopper adapterTopFans = new AdapterGroupTopper(this, weeklyUsersGold);
                    adapterTopFans.setOnClickListener(this);
                    rv_guestTopperList.setVisibility(View.VISIBLE);
                    rv_guestTopperList.setAdapter(adapterTopFans);
                }
                tvNoContributors.setVisibility(View.GONE);
            } else {
                rv_guestTopperList.setVisibility(View.GONE);
                tvNoContributors.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClickClose(View view) {
        try {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        callStatusAPI();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Quit Broadcast ?").setPositiveButton("Sure", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();

        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    @Override
    public void OnClicked(User user) {

    }

   /* public void showSharingDialog(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.sharing_layout);

        ImageView facebookShareBtn = dialog.findViewById(R.id.facebook_share);
        ImageView twitterShareBtn = dialog.findViewById(R.id.twitter_share);
        ImageView whatsAppShareBtn = dialog.findViewById(R.id.whatsapp_share);
        ImageView friendsShareBtn = dialog.findViewById(R.id.friends_share);

        Glide.with(getApplicationContext())
                .load(R.drawable.c_facebook_share)
                .into(facebookShareBtn);

        Glide.with(getApplicationContext())
                .load(R.drawable.c_twitter_share)
                .into(twitterShareBtn);

        Glide.with(getApplicationContext())
                .load(R.drawable.c_whatsapp)
                .into(whatsAppShareBtn);

        Glide.with(getApplicationContext())
                .load(R.drawable.c_friends)
                .into(friendsShareBtn);

        facebookShareBtn.setOnClickListener(view -> {
            if (checkAppInstalled("com.facebook.katana")) {
                sendMessageToChannel(" has shared with Facebook");
                try {
                    ShareDialog shareDialog;
                    shareDialog = new ShareDialog(this);

                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.blive"))
                            .build();

                    shareDialog.show(linkContent);
                } catch (Exception e) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.blive&hl=en");
                    shareIntent.setPackage("com.facebook.katana");
                    startActivity(shareIntent);
                }
                callShareAPI("facebook");
            } else {
                showToast("Application not found error when trying to open");
            }
            dialog.dismiss();
        });

        whatsAppShareBtn.setOnClickListener(view -> {
            if (checkAppInstalled("com.whatsapp")) {
                sendMessageToChannel(" has shared with Whatsapp");
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.blive&hl=en");
                shareIntent.setPackage("com.whatsapp");
                startActivity(shareIntent);
                callShareAPI("whatsapp");
            } else {
                showToast("Application not found error when trying to open");
            }
            dialog.dismiss();
        });

        twitterShareBtn.setOnClickListener(view -> {
            if (checkAppInstalled("com.twitter.android")) {
                sendMessageToChannel(" has shared with Twitter");
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.blive&hl=en");
                shareIntent.setPackage("com.twitter.android");
                startActivity(shareIntent);
                callShareAPI("twitter");
            } else {
                showToast("Application not found error when trying to open");
            }
            dialog.dismiss();
        });

        friendsShareBtn.setOnClickListener(view -> {
            showBLiveFriends();
            dialog.dismiss();
        });

        Window window = dialog.getWindow();
        assert window != null;
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        dialog.show();
    }*/

    public void showSharingDialog(Activity activity) {
        try {
            URL urlobjr = new URL();

            JSONObject jsonObject = new JSONObject();
            try {
                String url = Constants_api.shareAPI + broadcaster.getUser_id() + "&picture=" + broadcaster.getProfile_pic().replaceAll("\\/", "/");
                jsonObject.put("long_url", url);
                urlobjr.long_url = url;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i("autolog", "jsonObject: " + jsonObject);
            url_url = url(urlobjr);
            Log.i("autolog", "url: " + url_url);

            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setContentView(R.layout.sharing_layout);

            ImageView facebookShareBtn = dialog.findViewById(R.id.facebook_share);
            ImageView twitterShareBtn = dialog.findViewById(R.id.twitter_share);
            ImageView whatsAppShareBtn = dialog.findViewById(R.id.whatsapp_share);
            ImageView instagramShareBtn = dialog.findViewById(R.id.instagramShareBtn);
            ImageView friendsShareBtn = dialog.findViewById(R.id.friends_share);

            Glide.with(getApplicationContext())
                    .load(R.drawable.c_facebook_share)
                    .into(facebookShareBtn);
            Glide.with(getApplicationContext())
                    .load(R.drawable.c_twitter_share)
                    .into(twitterShareBtn);
            Glide.with(getApplicationContext())
                    .load(R.drawable.c_whatsapp)
                    .into(whatsAppShareBtn);
            Glide.with(getApplicationContext())
                    .load(R.drawable.c_friends)
                    .into(friendsShareBtn);
            facebookShareBtn.setOnClickListener(view -> {
                if (checkAppInstalled("com.facebook.katana")) {
                    sendMessageToChannel(" has shared with Facebook");
                    try {
                        ShareDialog shareDialog;
                        shareDialog = new ShareDialog(this);
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentUrl(Uri.parse("https://stg.sjhinfotech.com/BliveWeb/link/index.html?user_id=100001"))
                                .build();
                        shareDialog.show(linkContent);
                    } catch (Exception e) {
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");


                        shareIntent.putExtra(Intent.EXTRA_TEXT, url_url + " \n Hey!, Look! I Found A Super Cool Broadcaster on BLive! \n" +
                                "\"Android: https://play.google.com/store/apps/details?id=com.blive\n" +
//                                "\"iOS: https://apps.apple.com/us/app/blive24hrs/id1459884150\n" +
                                "\"Come & Cheer Now!" +
                                "\"Support & Invite Your Friends Too....Forward This Link To Them.....");
                        shareIntent.setPackage("com.facebook.katana");
                        startActivity(shareIntent);
                    }
                    callShareAPI("facebook");
                } else {
                    showToast("Application not found error when trying to open");
                }
                dialog.dismiss();
            });

            whatsAppShareBtn.setOnClickListener(view -> {
                if (checkAppInstalled("com.whatsapp")) {
                    sendMessageToChannel(" has shared with Whatsapp");
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");


                    shareIntent.putExtra(Intent.EXTRA_TEXT, url_url + " \n Hey!, Look! I Found A Super Cool Broadcaster on BLive! \n" +
                            "\"Android: https://play.google.com/store/apps/details?id=com.blive\n" +
                            "\"iOS: https://apps.apple.com/us/app/blive24hrs/id1459884150\n" +
                            "\"Come & Cheer Now!" +
                            "\"Support & Invite Your Friends Too....Forward This Link To Them.....");
                    shareIntent.setPackage("com.whatsapp");
                    startActivity(shareIntent);
                    callShareAPI("whatsapp");
                } else {
                    showToast("Application not found error when trying to open");
                }
                dialog.dismiss();
            });

            twitterShareBtn.setOnClickListener(view -> {
                if (checkAppInstalled("com.twitter.android")) {
                    sendMessageToChannel(" has shared with Twitter");
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, url_url + " \n Hey!, Look! I Found A Super Cool Broadcaster on BLive! \n" +
                            "\"Android: https://play.google.com/store/apps/details?id=com.blive\n" +
                            "\"iOS: https://apps.apple.com/us/app/blive24hrs/id1459884150\n" +
                            "\"Come & Cheer Now!" +
                            "\"Support & Invite Your Friends Too....Forward This Link To Them.....");
                    shareIntent.setPackage("com.twitter.android");
                    startActivity(shareIntent);
                    callShareAPI("twitter");
                } else {
                    showToast("Application not found error when trying to open");
                }
                dialog.dismiss();
            });

            Uri file = Uri.parse("android.resource://com.code2care.thebuddhaquotes/" + R.mipmap.ic_launcher);

            instagramShareBtn.setOnClickListener(view -> {
                if (checkAppInstalled("com.instagram.android")) {
                    sendMessageToChannel(" has shared with Instagram");
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    /*  shareIntent.putExtra(Intent.EXTRA_STREAM, file);*/
                    shareIntent.putExtra(Intent.EXTRA_TEXT, url_url + " \n Hey!, Look! I Found A Super Cool Broadcaster on BLive! \n" +
                            "\"Android: https://play.google.com/store/apps/details?id=com.blive\n" +
                            "\"iOS: https://apps.apple.com/us/app/blive24hrs/id1459884150\n" +
                            "\"Come & Cheer Now!" +
                            "\"Support & Invite Your Friends Too....Forward This Link To Them.....");
                    shareIntent.setPackage("com.instagram.android");
                    startActivity(shareIntent);
                    callShareAPI("instagram");
                } else {
                    showToast("Application not found error when trying to open");
                }
            });

            friendsShareBtn.setOnClickListener(view -> {
                showBLiveFriends();
                dialog.dismiss();
            });

            Window window = dialog.getWindow();
            assert window != null;
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);
            dialog.show();
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    public String url(URL url) {
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ServiceGenerator.getClient().create(ApiInterface.class);
            Call<linkshorten> call = apiClient.linkshorten(url);
            call.enqueue(new retrofit2.Callback<linkshorten>() {
                @Override
                public void onResponse(@NonNull Call<linkshorten> call, @NonNull Response<linkshorten> response) {
                    utils.hideProgress();
                    linkshorten genericResponse = response.body();
                    Log.i("autolog", "genericResponse:" + response.raw().request().url());
                    if (response.code() == 200) {
                        if (genericResponse != null) {
                            url_url = genericResponse.getLink().toString();
                        } else {
                            utils.showToast(getString(R.string.server_error));
                        }
                    } else {
                        Log.e(TAG, "onResponse: failed" + response.toString());
                        checkResponseCode(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<linkshorten> call, @NonNull Throwable t) {
                    utils.hideProgress();
                    showToast(t.getMessage());
                }
            });
        }
        return url_url;
    }

    private void callShareAPI(String domain) {
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<GenericResponse> call = apiClient.share(SessionUser.getUser().getUser_id(), "share", domain);
            call.enqueue(new retrofit2.Callback<GenericResponse>() {
                @Override
                public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                    GenericResponse genericResponse = response.body();
                    if (response.code() == 200) {
                        if (genericResponse != null) {
                            if (genericResponse.getStatus().equalsIgnoreCase("success")) {

                            } else {
                                showToast(genericResponse.getMessage());
                            }
                        } else {
                            showToast(getString(R.string.server_error));
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                    showToast(t.getMessage());
                }
            });
        }
    }

    public boolean checkAppInstalled(String appPackage) {
        PackageManager pm = getApplicationContext().getPackageManager();
        boolean isInstalled = isPackageInstalled(appPackage, pm);
        return isInstalled;
    }

    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        boolean found = true;
        try {
            packageManager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {

            found = false;
        }
        return found;
    }

    public void showBLiveFriends() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityScreenSharing.this);
        View dialog = getLayoutInflater().inflate(R.layout.blive_friends_layout, null);
        bottomSheetDialog.setContentView(dialog);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.show();
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) dialog.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        checkBoxSelectAllFriends = dialog.findViewById(R.id.cb_selectAllFriends);
        btnSendFriends = dialog.findViewById(R.id.sendSelectedfriends);
        rvFriendsList = dialog.findViewById(R.id.rv_friends);
        tvNoFriendsList = dialog.findViewById(R.id.tv_no_friends);
        btnSendFriends.setVisibility(View.GONE);
        checkBoxSelectAllFriends.setVisibility(View.GONE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        rvFriendsList.setLayoutManager(layoutManager);
        rvFriendsList.addItemDecoration(new DividerLine(this));
        layoutManager.setOrientation(OrientationHelper.VERTICAL);

        rvFriendsList.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (!isRefreshing) {
                if (!isUserListEnd) {
                    if (isLastItemDisplaying(rvFriendsList)) {
                        if (page < lastPage) {
                            page = page + 1;
                            getFriends(page);
                        }
                    }
                }
            }
        });

        checkBoxSelectAllFriends.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked) {
                adapterFriends = new AdapterFriendsShare(getApplicationContext(), usersFriendsList, true);
                rvFriendsList.setAdapter(adapterFriends);
                adapterFriends.notifyDataSetChanged();
            } else {
                adapterFriends = new AdapterFriendsShare(getApplicationContext(), usersFriendsList, false);
                rvFriendsList.setAdapter(adapterFriends);
                adapterFriends.notifyDataSetChanged();
            }
        });

        btnSendFriends.setOnClickListener(view -> {

            if (adapterFriends != null) {
                ArrayList<User> selectedFriends = adapterFriends.selectedFriends();

                if (selectedFriends.size() > 0) {
                    JsonArray friendsJsonArr = new JsonArray();
                    for (int i = 0; i < selectedFriends.size(); i++) {
                        friendsJsonArr.add(selectedFriends.get(i).getUser_id());
                    }
                    shareNotifications(friendsJsonArr);
                    bottomSheetDialog.dismiss();
                } else {
                    showToast("Select  Friends");
                }
            }
        });

        getFriends(1);
    }

    private void shareNotifications(JsonArray mFriendsJsonArr) {
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<GenericResponse> call = apiClient.shareNotification(SessionUser.getUser().getUser_id(), String.valueOf(mFriendsJsonArr));
            call.enqueue(new retrofit2.Callback<GenericResponse>() {
                @Override
                public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                    GenericResponse genericResponse = response.body();
                    if (response.code() == 200) {
                        if (genericResponse != null) {
                            if (genericResponse.getStatus().equalsIgnoreCase("success")) {
                                showToast(genericResponse.getData().getMessage());
                            } else {
                                showToast(genericResponse.getMessage());
                            }
                        } else {
                            utils.showToast(getString(R.string.server_error));
                        }
                    } else {
                        checkResponseCode(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                    showToast(t.getMessage());
                }
            });
        }
    }

    public void getFriends(int page) {
        if (!isAPICalled) {
            isAPICalled = true;
            if (utils.isNetworkAvailable()) {
                utils.showProgress();
                final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
                Call<UsersResponse> call = apiClient.getUsers("friend", String.valueOf(page), SessionUser.getUser().getUser_id());
                call.enqueue(new retrofit2.Callback<UsersResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<UsersResponse> call, @NonNull Response<UsersResponse> response) {
                        UsersResponse usersResponse = response.body();
                        utils.hideProgress();
                        if (response.code() == 200) {
                            if (usersResponse != null) {
                                if (usersResponse.getStatus().equalsIgnoreCase("success")) {
                                    onFriendsSuccess(usersResponse.getData().getUsers(), usersResponse.getData().getLast_page());
                                } else {
                                    rvFriendsList.setVisibility(View.GONE);
                                    tvNoFriendsList.setVisibility(View.VISIBLE);
                                    checkBoxSelectAllFriends.setVisibility(View.GONE);
                                    showToast(usersResponse.getMessage());
                                }
                            } else {
                                rvFriendsList.setVisibility(View.GONE);
                                tvNoFriendsList.setVisibility(View.VISIBLE);
                                checkBoxSelectAllFriends.setVisibility(View.GONE);
                                utils.showToast(getString(R.string.server_error));
                            }
                        } else {
                            rvFriendsList.setVisibility(View.GONE);
                            tvNoFriendsList.setVisibility(View.VISIBLE);
                            checkBoxSelectAllFriends.setVisibility(View.GONE);
                            checkResponseCode(response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<UsersResponse> call, @NonNull Throwable t) {
                        rvFriendsList.setVisibility(View.GONE);
                        tvNoFriendsList.setVisibility(View.VISIBLE);
                        checkBoxSelectAllFriends.setVisibility(View.GONE);
                        showToast(t.getMessage());
                    }
                });
            }
        }
    }

    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (Objects.requireNonNull(recyclerView.getAdapter()).getItemCount() != 0) {
            if (!isUserListEnd) {
                int lastVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findLastCompletelyVisibleItemPosition();
                return lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1;
            }
        }
        return false;
    }

    public void onFriendsSuccess(ArrayList<User> mUsers, int mPage) {
        lastPage = mPage;
        utils.hideProgress();
        isAPICalled = false;
        isRefreshing = false;
        if (page == 1) {
            if (mUsers.size() > 0) {
                if (mUsers.size() < Constants_app.pageLimit)
                    isUserListEnd = true;

                usersFriendsList.clear();
                usersFriendsList.addAll(mUsers);
                adapterFriends = new AdapterFriendsShare(this, usersFriendsList);
                adapterFriends.setOnClickListener(this);
                rvFriendsList.setAdapter(adapterFriends);
                rvFriendsList.setVisibility(View.VISIBLE);
                btnSendFriends.setVisibility(View.VISIBLE);
                tvNoFriendsList.setVisibility(View.GONE);
                checkBoxSelectAllFriends.setVisibility(View.VISIBLE);
            } else {
                rvFriendsList.setVisibility(View.GONE);
                tvNoFriendsList.setVisibility(View.VISIBLE);
                checkBoxSelectAllFriends.setVisibility(View.GONE);
                btnSendFriends.setVisibility(View.GONE);
            }
        } else {
            if (mUsers.size() > 0) {
                if (mUsers.size() < Constants_app.pageLimit)
                    isUserListEnd = true;

                usersFriendsList.addAll(mUsers);
                adapterFriends.notifyDataSetChanged();
            } else
                isUserListEnd = true;
        }
    }

    private void callEntranceEffect(int j) {
        try {
            String name = entranceEffects.get(j).getAccount();

            if (name.length() > 14) {
                name = name.substring(0, 14);
            }

            isEntranceEffects = true;
            Glide.with(this)
                    .load(entranceEffects.get(j).getUrl())
                    .into(ivEntrance);
            tvEntranceName.setVisibility(View.VISIBLE);
            tvEntranceName.setText(name + "\n is Arriving");
            ivEntrance.setVisibility(View.VISIBLE);

            entranceHandler.postDelayed(() -> {
                ivEntrance.setVisibility(View.GONE);
                tvEntranceName.setText("");
                tvEntranceName.setVisibility(View.GONE);
                temp1 = temp1 + 1;
                if (entranceEffects.size() > temp1) {
                    callEntranceEffect(temp1);
                } else {
                    temp1 = 0;
                    entranceEffects.clear();
                    isEntranceEffects = false;
                }
            }, 5000);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    public void onImagesSuccess(ArrayList<Audience> audiences, String entranceUrl, int viewers_count) {
        mAudiences = audiences;
        AdapterImages adapterImages = new AdapterImages(this, audiences);
        adapterImages.setOnClickListener(this);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        rvImages.setLayoutManager(layoutManager1);
        rvImages.setAdapter(adapterImages);
        viewers = viewers_count;
        tvCount.setText(String.valueOf(viewers));

        if (!isBroadcaster) {
            if (!isArrived) {
                isArrived = true;
                MessageBean message = new MessageBean(selfName, SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " has arrived", true, false, false);
                messageBeanList.add(message);
                adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                rvMessages.scrollToPosition(messageBeanList.size() - 1);
                sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " has arrived");
                if (!entranceUrl.isEmpty()) {
                    sendChannelMessage(SessionUser.getUser().getUser_id() + level + " enTraNceEffEct" + SessionUser.getUser().getName());
                }

                EntranceEffect entranceEffect = new EntranceEffect();
                entranceEffect.setAccount(SessionUser.getUser().getName());
                entranceEffect.setUrl(entranceUrl);
                entranceEffects.add(entranceEffect);

                if (entranceEffects.size() > 0) {
                    if (!isEntranceEffects) {
                        callEntranceEffect(temp1);
                    }
                }
            }
        }
    }

    @Override
    public void OnClickedAudience(Audience audience) {
        showAlertViewProfile(audience);
    }

    private void showAlertViewProfile(Audience audience) {
        Dialog alertDialog = new Dialog(this);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.alert_view_profile);

        RelativeLayout root = alertDialog.findViewById(R.id.root);
        TextView tvName = alertDialog.findViewById(R.id.tv_name);
        RelativeLayout rlReport = alertDialog.findViewById(R.id.rl_report);
        TextView tvBGold = alertDialog.findViewById(R.id.tv_bGold);
        TextView tvLevel = alertDialog.findViewById(R.id.tv_level);
        TextView tvMuted = alertDialog.findViewById(R.id.tv_muted);
        TextView tvUnMuted = alertDialog.findViewById(R.id.tv_unMuted);
        ImageView ivPic = alertDialog.findViewById(R.id.iv_profile);
        llKickOut = alertDialog.findViewById(R.id.ll_kick_out);
        TextView tvProfile = alertDialog.findViewById(R.id.tv_profile);
        llFollow = alertDialog.findViewById(R.id.ll_follow);
        llUnFollow = alertDialog.findViewById(R.id.ll_un_follow);
        LinearLayout llCallRequest = alertDialog.findViewById(R.id.ll_callRequest);
        LinearLayout llTextMute = alertDialog.findViewById(R.id.ll_text_mute);
        LinearLayout llManage = alertDialog.findViewById(R.id.ll_manage);
        LinearLayout llTopFans = alertDialog.findViewById(R.id.ll_topFans);
        LinearLayout llView = alertDialog.findViewById(R.id.ll_view);
        LinearLayout llView1 = alertDialog.findViewById(R.id.ll_view1);
        View view = alertDialog.findViewById(R.id.view);
        ImageView ivEffect = alertDialog.findViewById(R.id.iv_effect);

        llView1.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
        llView.setVisibility(View.VISIBLE);
        llCallRequest.setVisibility(View.INVISIBLE);

        Glide.with(this)
                .load(audience.getDpEffects())
                .into(ivEffect);

        try {
            if (audience.getIsTheUserFollowing().equalsIgnoreCase("no")) {
                llFollow.setVisibility(View.VISIBLE);
                llUnFollow.setVisibility(View.GONE);
            } else if (audience.getIsTheUserFollowing().equalsIgnoreCase("yes")) {
                llFollow.setVisibility(View.GONE);
                llUnFollow.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }

        if (audience.getText_muted().equalsIgnoreCase("no")) {
            tvMuted.setVisibility(View.VISIBLE);
            tvUnMuted.setVisibility(View.GONE);
        } else if (audience.getText_muted().equalsIgnoreCase("yes")) {
            tvMuted.setVisibility(View.GONE);
            tvUnMuted.setVisibility(View.VISIBLE);
        }

        tvName.setText(audience.getName());
        tvBGold.setText(audience.getOver_all_gold());
        tvLevel.setText(" Lv : " + audience.getLevel() + " ");

        root.setOnClickListener(v -> alertDialog.dismiss());

        llManage.setOnClickListener(v -> {
            LayoutInflater layoutInflater = LayoutInflater.from(ActivityScreenSharing.this);
            final View dialogView = layoutInflater.inflate(R.layout.pop_up_manage, null);
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityScreenSharing.this);

            alertDialogBuilder.setView(dialogView);

            AlertDialog alertDialog1 = alertDialogBuilder.create();
            alertDialog1.show();

        });

        llTextMute.setOnClickListener(v -> {
            if (audience.getText_muted().equalsIgnoreCase("no")) {
                audience.setText_muted("yes");
                tvMuted.setVisibility(View.GONE);
                tvUnMuted.setVisibility(View.VISIBLE);
                sendChannelMessage(audience.getUser_id() + level + audience.getName() + " h@s B&En T*xT MuTe$");
                alertDialog.dismiss();
                callTextMute(SessionUser.getUser().getUser_id(), audience.getUser_id(), "yes");
            } else if (audience.getText_muted().equalsIgnoreCase("yes")) {
                audience.setText_muted("no");
                tvUnMuted.setVisibility(View.GONE);
                tvMuted.setVisibility(View.VISIBLE);
                sendChannelMessage(audience.getUser_id() + level + audience.getName() + " h@s B&En T*xT UnMuTe$");
                alertDialog.dismiss();
                callTextMute(SessionUser.getUser().getUser_id(), audience.getUser_id(), "no");
            }
        });

        rlReport.setOnClickListener(v -> {

            LayoutInflater layoutInflater = LayoutInflater.from(ActivityScreenSharing.this);
            final View dialogView = layoutInflater.inflate(R.layout.pop_up_report, null);
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityScreenSharing.this);

            alertDialogBuilder.setView(dialogView);

            AlertDialog alertDialog12 = alertDialogBuilder.create();
            alertDialog12.show();

            final TextView tvRpl1 = dialogView.findViewById(R.id.tv_rp1);
            final TextView tvRpl2 = dialogView.findViewById(R.id.tv_rp2);
            final TextView tvRpl3 = dialogView.findViewById(R.id.tv_rp3);
            final TextView tvRpl4 = dialogView.findViewById(R.id.tv_rp4);
            final TextView tvRpl5 = dialogView.findViewById(R.id.tv_rp5);

            tvRpl1.setOnClickListener(v1 -> {
                String tvpr1 = "Pornographic";
                callReportAPI(audience.getUser_id(), tvpr1);
                alertDialog12.dismiss();
            });

            tvRpl2.setOnClickListener(v12 -> {
                String tvpr2 = "Illegal or Violence";
                callReportAPI(audience.getUser_id(), tvpr2);
                alertDialog12.dismiss();
            });

            tvRpl3.setOnClickListener(v13 -> {
                String tvpr3 = "Endanger Personal Safety";
                callReportAPI(audience.getUser_id(), tvpr3);
                alertDialog12.dismiss();
            });

            tvRpl4.setOnClickListener(v14 -> {
                String tvpr4 = "Illegal Avatar";
                callReportAPI(audience.getUser_id(), tvpr4);
                alertDialog12.dismiss();
            });

            tvRpl5.setOnClickListener(v15 -> {
                String tvpr5 = "others";
                callReportAPI(audience.getUser_id(), tvpr5);
                alertDialog12.dismiss();
            });
        });

        llFollow.setOnClickListener(v -> {
            if (audience.getIsTheUserFollowing().equalsIgnoreCase("no")) {
                audience.setIsTheUserFollowing("yes");
                llFollow.setVisibility(View.VISIBLE);
                llUnFollow.setVisibility(View.GONE);
                alertDialog.dismiss();
                callFollowAPI("follow", audience.getUser_id());
            } else if (audience.getIsTheUserFollowing().equalsIgnoreCase("yes")) {
                audience.setIsTheUserFollowing("no");
                llFollow.setVisibility(View.GONE);
                llUnFollow.setVisibility(View.VISIBLE);
                alertDialog.dismiss();
                callFollowAPI("unfollow", audience.getUser_id());
            }
        });

        llUnFollow.setOnClickListener(v -> {
            audience.setIsTheUserFollowing("no");
            llFollow.setVisibility(View.GONE);
            llUnFollow.setVisibility(View.VISIBLE);
            alertDialog.dismiss();
            callUnFollowAPI("unfollow", audience.getUser_id());
        });

        ivPic.setOnClickListener(v -> {
            gotoProfile(Constants_app.decodeImage(audience.getProfile_pic()), audience.getUser_id());
        });

        if (!audience.getProfile_pic().isEmpty()) {
            Picasso.get().load(audience.getProfile_pic()).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivPic);
        } else
            Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivPic);

        llKickOut.setOnClickListener(v -> {
            if (tvProfile.getText().toString().equals("Profile")) {
                gotoProfile(Constants_app.decodeImage(audience.getProfile_pic()), audience.getUser_id());
            } else {
                alertDialog.dismiss();
                callBlockAPI(audience.getUser_id());
                MessageBean message = new MessageBean(selfName, SessionUser.getUser().getUser_id() + level + audience.getName() + " has been kicked out", true, false, false);
                messageBeanList.add(message);
                adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                rvMessages.scrollToPosition(messageBeanList.size() - 1);
                sendChannelMessage(audience.getUser_id() + level + audience.getName() + " has bEEn kicKed OuT");
            }
        });

        llTopFans.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityScreenSharing.this, ActivityTopFans.class);
            intent.putExtra("activationToken", audience.getActivation_code());
            intent.putExtra("user_id", audience.getUser_id());
            startActivity(intent);
        });

        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);
    }

    private void callFollowAPI(String type, String userId) {
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<FollowResponse> call = apiClient.follow(type, userId, SessionUser.getUser().getUser_id());
            call.enqueue(new retrofit2.Callback<FollowResponse>() {
                @Override
                public void onResponse(@NonNull Call<FollowResponse> call, @NonNull Response<FollowResponse> response) {
                    FollowResponse followResponse = response.body();
                    if (response.code() == 200) {
                        if (followResponse != null) {
                            if (followResponse.getStatus().equalsIgnoreCase("success")) {
                                showToast(followResponse.getData().getMessage());
                                ivFollow.setVisibility(View.GONE);
                                llFollow.setVisibility(View.GONE);
                                llUnFollow.setVisibility(View.VISIBLE);
                                getLevel();
                                if (isBroadcaster) {
                                    isAudienceFollowing = true;
                                }
                                MessageBean message = new MessageBean(selfName, SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " started following", true, false, false);
                                messageBeanList.add(message);
                                adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                                rvMessages.scrollToPosition(messageBeanList.size() - 1);
                                sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " started following");
                            } else {
                                showToast(followResponse.getMessage());
                            }
                        } else {
                            utils.showToast(getString(R.string.server_error));
                        }
                    } else {
                        checkResponseCode(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<FollowResponse> call, @NonNull Throwable t) {
                    showToast(t.getMessage());
                }
            });
        }
    }

    private void callUnFollowAPI(String type, String audienceId) {
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<FollowResponse> call = apiClient.follow(type, audienceId, SessionUser.getUser().getUser_id());
            call.enqueue(new retrofit2.Callback<FollowResponse>() {
                @Override
                public void onResponse(@NonNull Call<FollowResponse> call, @NonNull Response<FollowResponse> response) {
                    FollowResponse followResponse = response.body();
                    if (response.code() == 200) {
                        if (followResponse != null) {
                            if (followResponse.getStatus().equalsIgnoreCase("success")) {
                                showToast(followResponse.getData().getMessage());
                            } else {
                                showToast(followResponse.getMessage());
                            }
                        } else {
                            utils.showToast(getString(R.string.server_error));
                        }
                    } else {
                        checkResponseCode(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<FollowResponse> call, @NonNull Throwable t) {
                    utils.hideProgress();
                    showToast(t.getMessage());
                }
            });
        }
    }

    private void callTextMute(String broadcasterId, String userId, String textMute) {
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<GenericResponse> call = apiClient.textMute(userId, broadcasterId, textMute);
            call.enqueue(new retrofit2.Callback<GenericResponse>() {
                @Override
                public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                    GenericResponse genericResponse = response.body();
                    if (response.code() == 200) {
                        if (genericResponse != null) {
                            if (genericResponse.getStatus().equalsIgnoreCase("success")) {
                                showToast(genericResponse.getData().getMessage());
                            } else {
                                showToast(genericResponse.getMessage());
                            }
                        } else {
                            utils.showToast(getString(R.string.server_error));
                        }
                    } else {
                        checkResponseCode(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                    utils.hideProgress();
                    showToast(t.getMessage());
                }
            });
        }
    }

    public void gotoProfile(String image, String userId) {
        Intent intent = new Intent(ActivityScreenSharing.this, ActivityViewProfile.class);
        intent.putExtra("image", image);
        intent.putExtra("userId", userId);
        intent.putExtra("from", "liveRoom");
        startActivity(intent);
    }

    private void callBlockAPI(String user_id) {
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<GenericResponse> call = apiClient.block("block", user_id, SessionUser.getUser().getUser_id());
            call.enqueue(new retrofit2.Callback<GenericResponse>() {
                @Override
                public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                    GenericResponse genericResponse = response.body();
                    if (response.code() == 200) {
                        if (genericResponse != null) {
                            if (genericResponse.getStatus().equalsIgnoreCase("success")) {
                                showToast("User has been kicked out successfully");
                            } else {
                                showToast(genericResponse.getMessage());
                            }
                        } else {
                            utils.showToast(getString(R.string.server_error));
                        }
                    } else {
                        checkResponseCode(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                    utils.hideProgress();
                    showToast(t.getMessage());
                }
            });
        }
    }

    public void callReportAPI(String reportedId, String description) {
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<GenericResponse> call = apiClient.report(SessionUser.getUser().getUser_id(), reportedId, "report", description);
            call.enqueue(new retrofit2.Callback<GenericResponse>() {
                @Override
                public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                    GenericResponse genericResponse = response.body();
                    if (response.code() == 200) {
                        if (genericResponse != null) {
                            if (genericResponse.getStatus().equalsIgnoreCase("success")) {
                                showToast(genericResponse.getData().getMessage());
                            } else {
                                showToast(genericResponse.getMessage());
                            }
                        } else {
                            showToast(getString(R.string.server_error));
                        }
                    } else {
                        checkResponseCode(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                    showToast(t.getMessage());
                }
            });
        }
    }

    private void callRemoveImageAPI() {

        endTime = System.currentTimeMillis();
        long mills = endTime - startTime;
        int hours = (int) (mills / (1000 * 60 * 60));
        int mins = (int) (mills / (1000 * 60)) % 60;
        int seconds = (int) (mills / 1000) % 60;
        String broadcastingTime = String.valueOf(hours) + ":" + String.valueOf(mins) + ":" + String.valueOf(seconds);

        if (hours > 1 && mins > 1)
            time = hours + ":" + mins + ":00";
        else if (hours == 1 && mins > 1)
            time = hours + ":" + mins + ":00";
        else
            time = hours + ":" + mins + ":00";

        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<UsersResponse> call = apiClient.removeAudience(SessionUser.getUser().getUser_id(), broadcasterId, broadcastingTime);
            call.enqueue(new retrofit2.Callback<UsersResponse>() {
                @Override
                public void onResponse(@NonNull Call<UsersResponse> call, @NonNull Response<UsersResponse> response) {
                    UsersResponse usersResponse = response.body();
                    if (response.code() == 200) {
                        if (usersResponse != null) {
                            if (usersResponse.getStatus().equalsIgnoreCase("success")) {
                                onRemoveSuccess(usersResponse.getData().getAudiences());
                            } else {
                                showToast(usersResponse.getMessage());
                            }
                        } else {
                            showToast(getString(R.string.server_error));
                        }
                    } else {
                        checkResponseCode(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UsersResponse> call, @NonNull Throwable t) {
                    showToast(t.getMessage());
                }
            });
        }

        if (isBroadcastEnded) {
            Constants_app.cleanMessageListBeanList();
            mChatManager.leaveChannel();
            mChatManager.removeChatHandler(mChatHandler);
            utils.hideProgress();
            finishAffinity();
            Intent intent = new Intent(ActivityScreenSharing.this, ActivityBroadcastStop.class);
            intent.putExtra("name", broadcaster.getName());
            intent.putExtra("image", broadcaster.getProfile_pic());
            intent.putExtra("user_id", broadcaster.getUser_id());
            startActivity(intent);
        }
    }

    private void callSwitchCase(int j) {
        try {
            isGiftShowing = true;

            for (int i = 0; i < giftsList.size(); i++) {
                if (giftMessages.get(j).getMessage().contains(giftsList.get(i).getName())) {
                    if (giftsList.get(i).getType().equalsIgnoreCase("normal")) {
                        //normalGift(giftMessages.get(j).getAccount(), giftsList.get(i).getThumbnail(), giftsList.get(i).getName(), Integer.valueOf(giftsList.get(i).getDuration()));
                        Picasso.get().load(image).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivGiftItem);
                        String msg = giftMessages.get(j).getMessage();
                        String id = msg.substring(0, 8);
                        msg = msg.replace(id, "");
                        String message = msg;

                        tvGiftName.setText(message);
                        llNormalGift.setVisibility(View.VISIBLE);
                        tvGiftMessage.setText(message);
                        cvGiftMessage.setVisibility(View.VISIBLE);
                        giftHandler.postDelayed(() -> {
                            cvGiftMessage.setVisibility(View.GONE);
                            tvGiftMessage.setText("");
                            llNormalGift.setVisibility(View.GONE);
                            temp = temp + 1;
                            if (giftMessages.size() > temp) {
                                callSwitchCase(temp);
                            } else {
                                temp = 0;
                                giftMessages.clear();
                                isGiftShowing = false;
                            }
                        }, Integer.valueOf(giftsList.get(i).getDuration()));
                    } else {
                        Log.e(TAG, "callSwitchCase: gifGift");
                        String msg = giftMessages.get(j).getMessage();
                        String id = msg.substring(0, 8);
                        msg = msg.replace(id, "");
                        String message = msg;
                        tvGiftMessage.setText(message);
                        cvGiftMessage.setVisibility(View.VISIBLE);
                        setGifGift(giftsList.get(i));
                    }
                }
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    private void callAddGiftValue(int k) {
        try {
            isGoldAdding = true;
            int price = 0;
            for (int i = 0; i < giftsList.size(); i++) {
                if (messagesList.get(k).contains(giftsList.get(i).getName())) {
                    if (messagesList.get(k).contains("X25")) {
                        price = Integer.valueOf(giftsList.get(i).getPrice()) * 25;
                    } else if (messagesList.get(k).contains("X99")) {
                        price = Integer.valueOf(giftsList.get(i).getPrice()) * 99;
                    } else if (messagesList.get(k).contains("X999")) {
                        price = Integer.valueOf(giftsList.get(i).getPrice()) * 999;
                    } else {
                        price = Integer.valueOf(giftsList.get(i).getPrice());
                    }
                }
            }

            gold = gold + price;
            tvReceived.setText(String.valueOf(gold));
            int tempMoonValue = Integer.valueOf(moonValue) + price;
            moonValue = String.valueOf(tempMoonValue);
            if (moonValue.isEmpty())
                moonValue = "0";
            int times = Integer.valueOf(moonValue) / 8100;
            tvMoonLevelCount.setText("5x" + String.valueOf(times));
            loadStarImage(moonValue);

            temp2 = temp2 + 1;
            if (messagesList.size() > temp2) {
                callAddGiftValue(temp2);
            } else {
                temp2 = 0;
                messagesList.clear();
                isGoldAdding = false;
            }
        } catch (Exception e) {
            Log.e(TAG, "callAddGiftValue: " + e);
        }
    }

    private void setGifGift(Gift gift) {

        String completePath = Environment.getExternalStorageDirectory() + "/webPStorage/" + gift.getName() + ".webp";
        iv.setVisibility(View.VISIBLE);
        String filepath = gift.getGiftpath();
        //  String completePath = Environment.getExternalStorageDirectory() + "/webPStorage/" + gift.getName()+".webp";

        File fileNew = new File(completePath);
        if (fileNew.exists()) {
            Glide.with(this)
                    .load(fileNew)
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(iv);

        } else {
            try {
                Glide.with(getApplicationContext())
                        .load(gift.getGif())
                        .addListener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Log.e(TAG, "onLoadFailed: " + e);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(iv);

            } catch (Exception e1) {
                Crashlytics.logException(e1);
            }
        }

        giftHandler.postDelayed(() -> {
            iv.setVisibility(View.GONE);
            temp = temp + 1;
            if (giftMessages.size() > temp) {
                callSwitchCase(temp);
            } else {
                temp = 0;
                giftMessages.clear();
                isGiftShowing = false;
            }
            tvGiftMessage.setText("");
            cvGiftMessage.setVisibility(View.GONE);
        }, Integer.valueOf(gift.getDuration()));
    }

    public void loadStarImage(String moonValue) {

        String level = Constants_app.loadStarLevel(moonValue);
        tvStarLevel.setText(level);

        if (oldMoonImage != 0) {
            /*Glide.with(getApplicationContext())
                    .load(oldMoonImage)
                    .into(starRatings1);*/
            Drawable res = getResources().getDrawable(oldMoonImage);
            starRatings1.setImageDrawable(res);
        }

        String uri = Constants_app.loadBroadCasterStar(moonValue);
        int starImage = getResources().getIdentifier(uri, null, getPackageName());
        Drawable res = getResources().getDrawable(starImage);
        starRatings.setImageDrawable(res);
       /* Glide.with(getApplicationContext())
                .load(starImage)
                .into(starRatings);*/

        oldMoonImage = starImage;
    }

    public void onRemoveSuccess(ArrayList<Audience> images) {
        if (isBroadcastEnded) {
            Constants_app.cleanMessageListBeanList();
            mChatManager.leaveChannel();
            mChatManager.removeChatHandler(mChatHandler);
            utils.hideProgress();
            Intent intent = new Intent(ActivityScreenSharing.this, ActivityBroadcastStop.class);
            intent.putExtra("name", broadcaster.getName());
            intent.putExtra("image", broadcaster.getProfile_pic());
            finish();
            startActivity(intent);
        } else {
            if (position != -1 && position < users.size()) {
                Constants_app.cleanMessageListBeanList();
            } else {
                showToast("No More Live User");
            }
        }
    }

    public void onGiftsSuccess(ArrayList<Gift> gifts, ArrayList<Gift> tools, ArrayList<Gift> freeGift) {
        giftsList.addAll(gifts);
        giftTools.addAll(tools);
        freeGifts.addAll(freeGift);

        try {
            adapter.setGifts(gifts);
        } catch (Exception e) {
            Log.e(TAG, "onGiftsSuccess: " + e);
            Crashlytics.logException(e);
        }

        SqlDb sqlDb = new SqlDb(getApplicationContext());
        ArrayList<Gift> giftDbDataArrayList = sqlDb.getGiftData();

        for (int i = 0; i < giftDbDataArrayList.size(); i++) {
            File myFile = new File(Environment.getExternalStorageDirectory() + "/webPStorage/" + giftDbDataArrayList.get(i).getName() + ".webp");
            if (myFile.exists()) {

            } else {
                int deleted = sqlDb.deleteSelectedGift(giftDbDataArrayList.get(i).getGiftId());
                if (deleted > 0) {

                } else {

                }
            }
        }

        callAudiencesAPI(broadcaster.getUser_id());
    }


    public void showActiveViewers() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityScreenSharing.this);
        View parentView = getLayoutInflater().inflate(R.layout.active_viewrs_list, null);
        bottomSheetDialog.setContentView(parentView);
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) parentView.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        bottomSheetDialog.show();

        TextView tvNoAudience = parentView.findViewById(R.id.tv_no_audience);
        RecyclerView rvActiveViewers = parentView.findViewById(R.id.rv_active_viewers);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, true);
        rvActiveViewers.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        rvActiveViewers.setNestedScrollingEnabled(false);

        if (mAudiences.size() > 0) {
            tvNoAudience.setVisibility(View.GONE);
            AdapterActiveViewers activeViewers = new AdapterActiveViewers(getApplicationContext(), mAudiences);
            rvActiveViewers.setAdapter(activeViewers);
            activeViewers.setOnClickListener(this);
        } else {
            tvNoAudience.setVisibility(View.VISIBLE);
        }
    }

    public void sendMessageToChannel(String msg) {
        MessageBean message = new MessageBean(selfName, SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + msg, true, false, false);
        messageBeanList.add(message);
        adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
        rvMessages.scrollToPosition(messageBeanList.size() - 1);
        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + msg);
    }

    @Override
    public void onClickedActiveAudience(Audience audience) {
        if (isBroadcaster) {
            showAlertViewProfile(audience);
        }
    }

    @Override
    public void onMessageClicked(String name, String id) {
        Log.e(TAG, "onMessageClicked: " + id);
        if (!id.equals(SessionUser.getUser().getUser_id())) {
            if (!isClickedProfile) {
                isClickedProfile = true;
                getClickedProfileData(id);
            }
        } else if (id.equals(SessionUser.getUser().getUser_id())) {
            getClickedProfileData(id);
        }
    }

    private void getClickedProfileData(String id) {
        Log.e(TAG, "getClickedProfileData: " + id);
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<ProfileResponse> call = apiClient.getProfile(SessionUser.getUser().getUser_id(), id);
            call.enqueue(new retrofit2.Callback<ProfileResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                    Log.e(TAG, "onResponse: " + response);
                    ProfileResponse profileResponse = response.body();
                    Log.e(TAG, "onResponse: " + response.body());
                    if (response.code() == 200) {
                        if (profileResponse != null) {
                            if (profileResponse.getStatus().equalsIgnoreCase("success")) {
                                setAudienceData(profileResponse.getData().getUser(), profileResponse.getData().getIsThisUserFollowing());
                            } else {
                                showToast(profileResponse.getMessage());
                            }
                        } else {
                            showToast(getString(R.string.server_error));
                        }
                    } else {
                        checkResponseCode(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                    utils.hideProgress();
                    showToast(t.getMessage());
                }
            });
        }
    }

    private void setAudienceData(User user, String istheuserFolowing) {
        Audience audience = new Audience();
        audience.setName(user.getName());
        audience.setId(user.getId());
        audience.setUser_id(user.getUser_id());
        audience.setActivation_code(user.getActivation_code());
        audience.setProfile_pic(user.getProfile_pic());
        audience.setLevel(user.getLevel());
        audience.setOver_all_gold(user.getOver_all_gold());
        audience.setText_muted(user.getText_muted());
        Log.e(TAG, "setAudienceData: " + istheuserFolowing);
        audience.setIsTheUserFollowing(istheuserFolowing);

        if (SessionUser.getUser().getUser_id().equals(user.getUser_id())) {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityScreenSharing.this);
            View parentView = getLayoutInflater().inflate(R.layout.broadcaster_profile, null);
            bottomSheetDialog.setContentView(parentView);
            bottomSheetDialog.setCanceledOnTouchOutside(true);
            final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) parentView.getParent());
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    } else {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            });
            bottomSheetDialog.show();
            ImageView ivGuestProfile = parentView.findViewById(R.id.iv_guest_profile);
            TextView tvGuestName = parentView.findViewById(R.id.tv_guest_name);
            TextView tvGuestLevel = parentView.findViewById(R.id.tv_guest_level);
            TextView tvGuestGold = parentView.findViewById(R.id.tv_guest_gold_received);
            TextView tvGuestGoldSent = parentView.findViewById(R.id.tv_guest_gold_sent);
            TextView tvMinTarget = parentView.findViewById(R.id.tv_minTarget);
            TextView tvMaxTarget = parentView.findViewById(R.id.tv_max_target);
            TextView tvBliveId = parentView.findViewById(R.id.tv_blive_id);
            TextView tvFriends = parentView.findViewById(R.id.tv_friends);
            TextView tvFollowers = parentView.findViewById(R.id.tv_followers);
            TextView tvFollowings = parentView.findViewById(R.id.tv_followings);
            TextView tvShareProgress = parentView.findViewById(R.id.tv_share_progress);
            TextView tvGoldProgress = parentView.findViewById(R.id.tv_gold_progress);
            TextView tvViewersProgress = parentView.findViewById(R.id.tv_viewers_progress);
            ImageView ivEffect = parentView.findViewById(R.id.iv_effect);
            ProgressBar pbShareProgress = parentView.findViewById(R.id.pb_share);
            ProgressBar pbGoldProgress = parentView.findViewById(R.id.pb_gold);
            ProgressBar pbViewersProgress = parentView.findViewById(R.id.pb_viewers);

            tvGuestGoldSent.setText(user.getTotal_gift_send());
            tvMaxTarget.setText(user.getBroadcasting_hours());
            tvMinTarget.setText(user.getBroadcasting_min_target());
            tvFriends.setText(user.getFriendsCount());
            tvFollowers.setText(user.getFollowersCount());
            tvFollowings.setText(user.getFansCount());
            String totalShareProgress = user.getShare() + "/" + user.getShare_target();
            String totalGoldProgress = user.getGold() + "/" + user.getGold_target();
            String totalViewersProgress = viewers + "/" + user.getViewers_target();
            tvShareProgress.setText(totalShareProgress);
            tvGoldProgress.setText(totalGoldProgress);
            tvViewersProgress.setText(totalViewersProgress);

            Glide.with(getApplicationContext())
                    .load(user.getTools_applied())
                    .into(ivEffect);

            try {
                int shareProgress = Integer.parseInt(user.getShare_target());
                int goldProgress = Integer.parseInt(user.getGold_target());
                int viewersProgress = Integer.parseInt(user.getViewers_target());
                int share = Integer.parseInt(user.getShare());
                int gold = Integer.parseInt(user.getGold());

                Resources res = getResources();
                Drawable drawableGold = res.getDrawable(R.drawable.progress_bar_gold_back);
                Drawable drawableShare = res.getDrawable(R.drawable.progress_bar_gold_back);
                Drawable drawableViewers = res.getDrawable(R.drawable.progress_bar_viewers_back);

                int sharePercent = (share * 100 / shareProgress);
                int goldPercent = (gold * 100 / goldProgress);
                int viewerPercent = (viewers * 100 / viewersProgress);

                pbShareProgress.setProgress(sharePercent);   // Main Progress
                pbShareProgress.setMax(100); // Maximum Progress
                pbShareProgress.setProgressDrawable(drawableShare);

                pbGoldProgress.setProgress(goldPercent);   // Main Progress
                pbGoldProgress.setMax(100); // Maximum Progress
                pbGoldProgress.setProgressDrawable(drawableGold);

                pbViewersProgress.setProgress(viewerPercent);   // Main Progress
                pbViewersProgress.setMax(viewersProgress); // Maximum Progress
                pbViewersProgress.setProgressDrawable(drawableViewers);

            } catch (Exception e) {
                Log.e("ProgressError", e.getMessage());
            }

            progressBar = parentView.findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);

            tvGuestLevel.setText(user.getLevel());
            tvGuestGold.setText(user.getTotal_gift_receiver());
            tvGuestName.setText(user.getName());
            tvBliveId.setText(user.getReference_user_id());
            if (!user.getProfile_pic().isEmpty()) {
                Picasso.get().load(user.getProfile_pic()).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivGuestProfile);
            } else
                Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivGuestProfile);
        }

        if (!user.getUser_id().equals(SessionUser.getUser().getUser_id()) && !user.getUser_id().isEmpty()) {
            if (isBroadcaster) {
                showAlertViewProfile(audience);
            }
        } else if (user.getUser_id().isEmpty()) {
            showToast("User Id empty....");
        }
    }

    @OnClick(R.id.iv_user)
    public void onClickUser() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityScreenSharing.this);
        View parentView = getLayoutInflater().inflate(R.layout.broadcaster_profile, null);
        bottomSheetDialog.setContentView(parentView);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) parentView.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        bottomSheetDialog.show();
        ImageView ivGuestProfile = parentView.findViewById(R.id.iv_guest_profile);
        TextView tvGuestName = parentView.findViewById(R.id.tv_guest_name);
        TextView tvGuestLevel = parentView.findViewById(R.id.tv_guest_level);
        TextView tvGuestGold = parentView.findViewById(R.id.tv_guest_gold_received);
        TextView tvGuestGoldSent = parentView.findViewById(R.id.tv_guest_gold_sent);
        TextView tvMinTarget = parentView.findViewById(R.id.tv_minTarget);
        TextView tvMaxTarget = parentView.findViewById(R.id.tv_max_target);
        TextView tvBliveId = parentView.findViewById(R.id.tv_blive_id);
        TextView tvFriends = parentView.findViewById(R.id.tv_friends);
        TextView tvFollowers = parentView.findViewById(R.id.tv_followers);
        TextView tvFollowings = parentView.findViewById(R.id.tv_followings);
        TextView tvShareProgress = parentView.findViewById(R.id.tv_share_progress);
        TextView tvGoldProgress = parentView.findViewById(R.id.tv_gold_progress);
        TextView tvViewersProgress = parentView.findViewById(R.id.tv_viewers_progress);
        ImageView ivEffect = parentView.findViewById(R.id.iv_effect);
        ProgressBar pbShareProgress = parentView.findViewById(R.id.pb_share);
        ProgressBar pbGoldProgress = parentView.findViewById(R.id.pb_gold);
        ProgressBar pbViewersProgress = parentView.findViewById(R.id.pb_viewers);

        tvGuestGoldSent.setText(broadcaster.getTotal_gift_send());
        tvMaxTarget.setText(broadcaster.getBroadcasting_hours());
        tvMinTarget.setText(broadcaster.getBroadcasting_min_target());
        tvFriends.setText(broadcaster.getFriendsCount());
        tvFollowers.setText(broadcaster.getFollowersCount());
        tvFollowings.setText(broadcaster.getFansCount());
        String totalShareProgress = broadcaster.getShare() + "/" + broadcaster.getShare_target();
        String totalGoldProgress = broadcaster.getGold() + "/" + broadcaster.getGold_target();
        String totalViewersProgress = viewers + "/" + broadcaster.getViewers_target();
        tvShareProgress.setText(totalShareProgress);
        tvGoldProgress.setText(totalGoldProgress);
        tvViewersProgress.setText(totalViewersProgress);

        Glide.with(getApplicationContext())
                .load(SessionUser.getUser().getTools_applied())
                .into(ivEffect);

        try {
            int shareProgress = Integer.parseInt(broadcaster.getShare_target());
            int goldProgress = Integer.parseInt(broadcaster.getGold_target());
            int viewersProgress = Integer.parseInt(broadcaster.getViewers_target());
            int share = Integer.parseInt(broadcaster.getShare());
            int gold = Integer.parseInt(broadcaster.getGold());

            Resources res = getResources();
            Drawable drawableGold = res.getDrawable(R.drawable.progress_bar_gold_back);
            Drawable drawableShare = res.getDrawable(R.drawable.progress_bar_gold_back);
            Drawable drawableViewers = res.getDrawable(R.drawable.progress_bar_viewers_back);

            int sharePercent = (share * 100 / shareProgress);
            int goldPercent = (gold * 100 / goldProgress);
            int viewerPercent = (viewers * 100 / viewersProgress);

            pbShareProgress.setProgress(sharePercent);   // Main Progress
            pbShareProgress.setMax(100); // Maximum Progress
            pbShareProgress.setProgressDrawable(drawableShare);

            pbGoldProgress.setProgress(goldPercent);   // Main Progress
            pbGoldProgress.setMax(100); // Maximum Progress
            pbGoldProgress.setProgressDrawable(drawableGold);

            pbViewersProgress.setProgress(viewerPercent);   // Main Progress
            pbViewersProgress.setMax(viewersProgress); // Maximum Progress
            pbViewersProgress.setProgressDrawable(drawableViewers);

        } catch (Exception e) {

        }

        progressBar = parentView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        tvGuestLevel.setText(broadcaster.getLevel());
        tvGuestGold.setText(broadcaster.getTotal_gift_receiver());
        tvGuestName.setText(broadcaster.getName());
        tvBliveId.setText(broadcaster.getReference_user_id());
        if (!broadcaster.getProfile_pic().isEmpty()) {
            Picasso.get().load(broadcaster.getProfile_pic()).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivGuestProfile);
        } else
            Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivGuestProfile);
    }
}