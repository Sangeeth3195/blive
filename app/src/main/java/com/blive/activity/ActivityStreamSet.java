package com.blive.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blive.BLiveApplication;
import com.blive.agora.rtmChat.ChatHandler;
import com.blive.agora.rtmChat.ChatManager;
import com.blive.constant.Constants_api;
import com.blive.constant.Constants_app;
import com.blive.R;
import com.blive.model.GenericResponse;
import com.blive.service.ApiClientBase;
import com.blive.service.ApiInterface;
import com.blive.session.SessionUser;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

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

public class ActivityStreamSet extends BaseBackActivity {

    private static final int PERMISSION_ID = 44;
    @BindView(R.id.iv_solo)
    ImageView ivSolo;
    @BindView(R.id.iv_group)
    ImageView ivGroup;
    @BindView(R.id.iv_share)
    ImageView ivShare;
    @BindView(R.id.iv_karaoke)
    ImageView ivKaraoke;
    @BindView(R.id.iv_audio)
    ImageView ivAudio;
    @BindView(R.id.ll_menu)
    LinearLayout llMenu;
    @BindView(R.id.rl_details)
    RelativeLayout rlDetails;
    @BindView(R.id.cvLive)
    ImageView cvLive;
    @BindView(R.id.iv1)
    ImageView iv1;
    @BindView(R.id.iv2)
    ImageView iv2;
    @BindView(R.id.iv3)
    ImageView iv3;
    @BindView(R.id.iv4)
    ImageView iv4;
    @BindView(R.id.iv5)
    ImageView iv5;
    @BindView(R.id.iv_groupCall3)
    ImageView ivGroupCall3;
    @BindView(R.id.iv_groupCall6)
    ImageView ivGroupCall6;
    @BindView(R.id.iv_groupCall9)
    ImageView ivGroupCall9;
    @BindView(R.id.iv_karaokeSolo)
    ImageView ivKaraokeSolo;
    @BindView(R.id.iv_karaokeGroup)
    ImageView ivKaraokeGroup;
    @BindView(R.id.ll_karaokeGroup)
    LinearLayout llKaraokeGroup;
    @BindView(R.id.ll_groupCall)
    LinearLayout llGroupCall;
    @BindView(R.id.iv_private)
    ImageView ivPrivate;

    private View view1, view2, view3, view4, view5;

    private boolean isSolo = false, isGroup = false, isScreenShare = false, isKaraoke = false, isAudio = false,
            isActivityResumed = false, isResumedActivity = false;
    private String type = "", image = "";
    private TextView tvTitle;
    private ImageView ivgroup, ivsolo, ivshare, ivaudio, ivkaraoke, ivVideoQuality;
    private Animation slideLeftAnim, slideRightAnim;
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    private RtmClient mRtmClient;
    private ChatManager mChatManager;
    private ChatHandler mChatHandler;
    String clicklive = "1";
    FusedLocationProviderClient mFusedLocationClient;
    protected Context context;
    String lat = "", lon = "";
    String latcheckresume = "";
    Boolean isPrivate= false;
    public static String status="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_set);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        BLiveApplication.setCurrentActivity(this);
        initUI();
    }

    protected void initUI() {

        ivVideoQuality = findViewById(R.id.iv_videoQuality);
        tvTitle = findViewById(R.id.tv_name);
        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);
        view4 = findViewById(R.id.view4);
        view5 = findViewById(R.id.view5);
        ivsolo = findViewById(R.id.iv_solo);
        ivgroup = findViewById(R.id.iv_group);
        ivshare = findViewById(R.id.iv_share);
        ivkaraoke = findViewById(R.id.iv_karaoke);
        ivaudio = findViewById(R.id.iv_audio);
        /*getLastLocation();*/

        ivVideoQuality.setOnClickListener(v -> {
            changeActivity(ActivityVideoQuaity.class);
        });

        ivPrivate.setColorFilter(ContextCompat.getColor(this, R.color.black));
        ivPrivate.setOnClickListener(v -> {
            if (!isPrivate) {
                isPrivate = true;
                ivPrivate.setImageDrawable(getResources().getDrawable(R.drawable.ic_locked_padlock));
                ivPrivate.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
            }else{
                isPrivate = false;
                ivPrivate.setImageDrawable(getResources().getDrawable(R.drawable.ic_unlocked_padlock));
                ivPrivate.setColorFilter(ContextCompat.getColor(this, R.color.black));
            }
        });

       /* mChatManager = BLiveApplication.getInstance().getChatManager();
        mRtmClient = mChatManager.getRtmClient();
        mChatHandler = new ChatHandler() {
            @Override
            public void onLoginSuccess() {
                Log.e(TAG, "onLoginSuccess: ");
            }

            @Override
            public void onLoginFailed(ErrorInfo errorInfo) {
                Log.e(TAG, "onLoginFailed: " + errorInfo);
            }

            @Override
            public void onChannelJoinSuccess() {
                Log.e(TAG, "onChannelJoinSuccess: ");
                runOnUiThread(() -> {
                    isResumedActivity = true;
                    String base64 = SessionUser.getUser().getProfile_pic();
                    try {
                        image = URLDecoder.decode(base64, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if (isSolo) {
                        if (clicklive.equalsIgnoreCase("0")) {
                            Intent intent = new Intent(ActivityStreamSet.this, ActivityLiveRoom.class);
                            intent.putExtra("mode", false);
                            intent.putExtra("name", SessionUser.getUser().getUsername().trim());
                            intent.putExtra("selfname", SessionUser.getUser().getUsername());
                            intent.putExtra("image", image);
                            intent.putExtra("received", SessionUser.getUser().getReceived());
                            intent.putExtra("broadcaster", SessionUser.getUser());
                            intent.putExtra("broadcasterId", SessionUser.getUser().getUser_id());
                            intent.putExtra("broad_type", "solo");
                            intent.putExtra("PKuserId", "0");
                            intent.putExtra("rtmname", SessionUser.getUser().getUsername().trim());
                            intent.putStringArrayListExtra("images", null);
                            intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_BROADCASTER);
                            intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, SessionUser.getUser().getUsername().trim());
                            startActivity(intent);
                            mChatManager.removeChatHandler(mChatHandler);
                        }
                    } else if (isScreenShare) {
                        Intent intent = new Intent(ActivityStreamSet.this, ActivityScreenSharing.class);
                        intent.putExtra("name", SessionUser.getUser().getUsername().trim());
                        intent.putExtra("selfname", SessionUser.getUser().getUsername());
                        intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_BROADCASTER);
                        intent.putExtra("image", image);
                        intent.putExtra("received", SessionUser.getUser().getReceived());
                        intent.putExtra("broadcaster", SessionUser.getUser());
                        intent.putExtra("broadcasterId", SessionUser.getUser().getUser_id());
                        intent.putStringArrayListExtra("images", null);
                        intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, SessionUser.getUser().getUsername().trim());
                        startActivity(intent);
                        mChatManager.removeChatHandler(mChatHandler);
                    } else if (isGroup) {
                        if (type.equals("groupOf3")) {
                            Intent intent = new Intent(ActivityStreamSet.this, ActivityGroupCalls3.class);
                            intent.putExtra("mode", false);
                            intent.putExtra("name", SessionUser.getUser().getUsername().trim());
                            intent.putExtra("selfname", SessionUser.getUser().getUsername());
                            intent.putExtra("image", image);
                            intent.putExtra("received", SessionUser.getUser().getReceived());
                            intent.putExtra("broadcaster", SessionUser.getUser());
                            intent.putExtra("broadcasterId", SessionUser.getUser().getUser_id());
                            intent.putStringArrayListExtra("images", null);
                            intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_BROADCASTER);
                            intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, SessionUser.getUser().getUsername().trim());
                            startActivity(intent);
                            mChatManager.removeChatHandler(mChatHandler);
                        } else if (type.equals("groupOf6")) {
                            Intent intent = new Intent(ActivityStreamSet.this, ActivityGroupCalls6.class);
                            intent.putExtra("mode", false);
                            intent.putExtra("name", SessionUser.getUser().getUsername().trim());
                            intent.putExtra("selfname", SessionUser.getUser().getUsername());
                            intent.putExtra("image", image);
                            intent.putExtra("received", SessionUser.getUser().getReceived());
                            intent.putExtra("broadcaster", SessionUser.getUser());
                            intent.putExtra("broadcasterId", SessionUser.getUser().getUser_id());
                            intent.putStringArrayListExtra("images", null);
                            intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_BROADCASTER);
                            intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, SessionUser.getUser().getUsername().trim());
                            startActivity(intent);
                            mChatManager.removeChatHandler(mChatHandler);
                        } else if (type.equals("groupOf9")) {
                            Intent intent = new Intent(ActivityStreamSet.this, ActivityGroupCalls9.class);
                            intent.putExtra("mode", false);
                            intent.putExtra("name", SessionUser.getUser().getUsername().trim());
                            intent.putExtra("selfname", SessionUser.getUser().getUsername());
                            intent.putExtra("image", image);
                            intent.putExtra("received", SessionUser.getUser().getReceived());
                            intent.putExtra("broadcaster", SessionUser.getUser());
                            intent.putExtra("broadcasterId", SessionUser.getUser().getUser_id());
                            intent.putStringArrayListExtra("images", null);
                            intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_BROADCASTER);
                            intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, SessionUser.getUser().getUsername().trim());
                            startActivity(intent);
                            mChatManager.removeChatHandler(mChatHandler);
                        }
                    } else if (isKaraoke) {

                        showToast("Coming Soon...");

                        *//*if (type.equals("karaokeSolo")) {
                            Intent intent = new Intent(ActivityStreamSet.this, ActivityKaraokeSolo.class);
                            intent.putExtra("name", SessionUser.getUser().getUsername().trim());
                            intent.putExtra("selfname", SessionUser.getUser().getUsername());
                            intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_BROADCASTER);
                            intent.putExtra("image", image);
                            intent.putExtra("received", SessionUser.getUser().getReceived());
                            intent.putExtra("broadcaster", SessionUser.getUser());
                            intent.putExtra("broadcasterId", SessionUser.getUser().getUser_id());
                            intent.putStringArrayListExtra("images", null);
                            intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, SessionUser.getUser().getUsername().trim());
                            startActivity(intent);
                            mChatManager.removeChatHandler(mChatHandler);
                        } else if (type.equals("karaokeGroup")) {
                            Intent intent = new Intent(ActivityStreamSet.this, ActivityKaraokeDual.class);
                            intent.putExtra("name", SessionUser.getUser().getUsername().trim());
                            intent.putExtra("selfname", SessionUser.getUser().getUsername());
                            intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_BROADCASTER);
                            intent.putExtra("image", image);
                            intent.putExtra("received", SessionUser.getUser().getReceived());
                            intent.putExtra("broadcaster", SessionUser.getUser());
                            intent.putExtra("broadcasterId", SessionUser.getUser().getUser_id());
                            intent.putStringArrayListExtra("images", null);
                            intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, SessionUser.getUser().getUsername().trim());
                            startActivity(intent);
                            mChatManager.removeChatHandler(mChatHandler);
                        }*//*
                    } else if (isAudio) {
                       *//* Intent intent = new Intent(ActivityStreamSet.this, ActivityNewPK.class);
                        intent.putExtra("mode", false);
                        intent.putExtra("name", SessionUser.getUser().getUsername().trim());
                        intent.putExtra("selfname", SessionUser.getUser().getUsername());
                        intent.putExtra("image", image);
                        intent.putExtra("received", SessionUser.getUser().getReceived());
                        intent.putExtra("broadcaster", SessionUser.getUser());
                        intent.putExtra("broadcasterId", SessionUser.getUser().getUser_id());
                        intent.putStringArrayListExtra("images", null);
                        intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_BROADCASTER);
                        intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, SessionUser.getUser().getUsername().trim());
                        startActivity(intent);
                        mChatManager.removeChatHandler(mChatHandler);*//*

                        showToast("Coming Soon...");
                    }
                });
            }

            @Override
            public void onChannelJoinFailed(ErrorInfo errorInfo) {
                runOnUiThread(() -> {
                    Log.e(TAG, "channel join failed: " + errorInfo.getErrorDescription());
                    if (errorInfo.getErrorCode() == 1) {
                        doLogin(SessionUser.getUser().getUsername());
                    }
                });
            }

            @Override
            public void onMessageReceived(RtmMessage message, RtmChannelMember fromMember) {
                Log.e(TAG, "onMessageReceived: " + message);
            }

            @Override
            public void onMemberJoined(RtmChannelMember rtmChannelMember) {
                Log.e(TAG, "onMemberJoined: " + rtmChannelMember);
            }

            @Override
            public void onMemberLeft(RtmChannelMember rtmChannelMember) {
                Log.e(TAG, "onMemberLeft: " + rtmChannelMember);
            }
        };

        mChatManager.addChantHandler(mChatHandler);*/

        Glide.with(this).load(R.drawable.go_live).into(cvLive);

        slideLeftAnim = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_left);
        slideRightAnim = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_right);

        ivsolo.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));

        //addCallback();

        isSolo = true;
        isGroup = false;
        isScreenShare = false;
        isKaraoke = false;
        isAudio = false;
        tvTitle.setText("Solo Broadcast");
        view1.setVisibility(View.VISIBLE);
        view2.setVisibility(View.GONE);
        view3.setVisibility(View.GONE);
        view4.setVisibility(View.GONE);
        view5.setVisibility(View.GONE);
        type = "solo";
    }

    @Override
    protected void onStart() {
        askForSystemOverlayPermission();
        super.onStart();
    }

    private void askForSystemOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e("Permission", "askForSystemOverlayPermission" + " " + Settings.canDrawOverlays(this));
        }
        if (!isActivityResumed) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        utils.deleteCache(this);
       /* mChatManager.removeChatHandler(mChatHandler);
        mChatManager.leaveChannel();*/
    }

    @OnClick({R.id.rl_solo, R.id.iv_solo})
    public void onClickSolo() {
        if (!isSolo) {
            isSolo = true;
            isScreenShare = false;
            isAudio = false;

            tvTitle.setText("Broadcast Live");

            type = "solo";

            view1.setVisibility(View.VISIBLE);
            view2.setVisibility(View.GONE);
            view3.setVisibility(View.GONE);
            view4.setVisibility(View.GONE);
            view5.setVisibility(View.GONE);

            iv2.setVisibility(View.GONE);
            iv3.setVisibility(View.GONE);
            iv4.setVisibility(View.GONE);
            iv5.setVisibility(View.GONE);
            iv1.setVisibility(View.VISIBLE);

            ivsolo.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
            ivgroup.setColorFilter(ContextCompat.getColor(this, R.color.black));
            ivshare.setColorFilter(ContextCompat.getColor(this, R.color.black));
            ivkaraoke.setColorFilter(ContextCompat.getColor(this, R.color.black));
            ivaudio.setColorFilter(ContextCompat.getColor(this, R.color.black));

            if (isKaraoke) {
                isKaraoke = false;
                llKaraokeGroup.startAnimation(slideRightAnim);
                llKaraokeGroup.setVisibility(View.INVISIBLE);
            }

            if (isGroup) {
                isGroup = false;
                llGroupCall.startAnimation(slideRightAnim);
                llGroupCall.setVisibility(View.INVISIBLE);
            }
        }
    }

    @OnClick({R.id.rl_group, R.id.iv_group})
    public void onClickGroup() {
        if (!isGroup) {
            isGroup = true;
            isSolo = false;
            isScreenShare = false;
            isAudio = false;

            tvTitle.setText("Group Video Call");

            type = "groupOf3";

            view1.setVisibility(View.GONE);
            view2.setVisibility(View.VISIBLE);
            view3.setVisibility(View.GONE);
            view4.setVisibility(View.GONE);
            view5.setVisibility(View.GONE);

            iv1.setVisibility(View.GONE);
            iv3.setVisibility(View.GONE);
            iv4.setVisibility(View.GONE);
            iv5.setVisibility(View.GONE);
            iv2.setVisibility(View.VISIBLE);

            llGroupCall.startAnimation(slideLeftAnim);
            llGroupCall.setVisibility(View.VISIBLE);

            ivsolo.setColorFilter(ContextCompat.getColor(this, R.color.black));
            ivGroup.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
            ivshare.setColorFilter(ContextCompat.getColor(this, R.color.black));
            ivkaraoke.setColorFilter(ContextCompat.getColor(this, R.color.black));
            ivaudio.setColorFilter(ContextCompat.getColor(this, R.color.black));

            if (isGroup) {
                ivGroupCall3.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.MULTIPLY);
                ivGroupCall6.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                ivGroupCall9.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
            }

            if (isKaraoke) {
                isKaraoke = false;
                llKaraokeGroup.startAnimation(slideRightAnim);
                llKaraokeGroup.setVisibility(View.INVISIBLE);
            }
        }
    }

    @OnClick({R.id.rl_share, R.id.iv_share})
    public void onClickShare() {
        if (!isScreenShare) {
            isSolo = false;
            isScreenShare = true;
            isAudio = false;

            tvTitle.setText("Screen Sharing");

            type = "screenSharing";

            view1.setVisibility(View.GONE);
            view2.setVisibility(View.GONE);
            view3.setVisibility(View.VISIBLE);
            view4.setVisibility(View.GONE);
            view5.setVisibility(View.GONE);

            iv1.setVisibility(View.GONE);
            iv2.setVisibility(View.GONE);
            iv4.setVisibility(View.GONE);
            iv5.setVisibility(View.GONE);
            iv3.setVisibility(View.VISIBLE);

            ivsolo.setColorFilter(ContextCompat.getColor(this, R.color.black));
            ivGroup.setColorFilter(ContextCompat.getColor(this, R.color.black));
            ivshare.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
            ivkaraoke.setColorFilter(ContextCompat.getColor(this, R.color.black));
            ivaudio.setColorFilter(ContextCompat.getColor(this, R.color.black));

            if (isKaraoke) {
                isKaraoke = false;
                llKaraokeGroup.startAnimation(slideRightAnim);
                llKaraokeGroup.setVisibility(View.INVISIBLE);
            }

            if (isGroup) {
                isGroup = false;
                llGroupCall.startAnimation(slideRightAnim);
                llGroupCall.setVisibility(View.INVISIBLE);
            }
        }
    }

    @OnClick({R.id.rl_karaoke, R.id.iv_karaoke})
    public void onClickKaraoke() {
        /*if (!isKaraoke) {
            isSolo = false;
            isScreenShare = false;
            isKaraoke = true;
            isAudio = false;

            tvTitle.setText("Karaoke Live");

            type = "karaokeSolo";

            view1.setVisibility(View.GONE);
            view2.setVisibility(View.GONE);
            view3.setVisibility(View.GONE);
            view4.setVisibility(View.VISIBLE);
            view5.setVisibility(View.GONE);

            iv1.setVisibility(View.GONE);
            iv2.setVisibility(View.GONE);
            iv3.setVisibility(View.GONE);
            iv5.setVisibility(View.GONE);
            iv4.setVisibility(View.VISIBLE);

            llKaraokeGroup.startAnimation(slideLeftAnim);
            llKaraokeGroup.setVisibility(View.VISIBLE);

            ivsolo.setColorFilter(ContextCompat.getColor(this, R.color.black));
            ivGroup.setColorFilter(ContextCompat.getColor(this, R.color.black));
            ivshare.setColorFilter(ContextCompat.getColor(this, R.color.black));
            ivkaraoke.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
            ivaudio.setColorFilter(ContextCompat.getColor(this, R.color.black));

            if (isKaraoke) {
                ivKaraokeSolo.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.MULTIPLY);
                ivKaraokeGroup.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
            }

            if (isGroup) {
                isGroup = false;
                llGroupCall.startAnimation(slideRightAnim);
                llGroupCall.setVisibility(View.INVISIBLE);
            }
        }*/
        showToast("Coming Soon...");
    }

    @OnClick({R.id.rl_audio, R.id.iv_audio})
    public void onClickAudio() {
        /*if (!isAudio) {
            isSolo = false;
            isGroup = false;
            isScreenShare = false;
            isAudio = true;
            tvTitle.setText("PK");
            type = "audio";
            view1.setVisibility(View.GONE);
            view2.setVisibility(View.GONE);
            view3.setVisibility(View.GONE);
            view4.setVisibility(View.GONE);
            view5.setVisibility(View.VISIBLE);

            iv1.setVisibility(View.GONE);
            iv2.setVisibility(View.GONE);
            iv3.setVisibility(View.GONE);
            iv4.setVisibility(View.GONE);
            iv5.setVisibility(View.VISIBLE);

            ivsolo.setColorFilter(ContextCompat.getColor(this, R.color.black));
            ivGroup.setColorFilter(ContextCompat.getColor(this, R.color.black));
            ivshare.setColorFilter(ContextCompat.getColor(this, R.color.black));
            ivkaraoke.setColorFilter(ContextCompat.getColor(this, R.color.black));
            ivaudio.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));

            if (isKaraoke) {
                isKaraoke = false;
                llKaraokeGroup.startAnimation(slideRightAnim);
                llKaraokeGroup.setVisibility(View.INVISIBLE);
            }
        }*/

        showToast("Coming Soon...");
    }

    @OnClick(R.id.tv_agreement)
    public void onClickAgreement() {
        Intent intent = new Intent(this, ActivityWebView.class);
        intent.putExtra("title", "Agreement");
        intent.putExtra("url", Constants_api.agreement);
        startActivity(intent);
    }

    @OnClick(R.id.cvLive)
    public void onClickLive() {
        if(isPrivate){
            status = "PRIVATE";
        }else{
            status = "ACTIVE";
        }
        if (type.equalsIgnoreCase("solo") || type.equalsIgnoreCase("groupOf3") || type.equalsIgnoreCase("groupOf6")
                || type.equalsIgnoreCase("groupOf9") || type.equalsIgnoreCase("screenSharing")
                || type.equalsIgnoreCase("audio") || type.equalsIgnoreCase("karaokeSolo")
                || type.equalsIgnoreCase("karaokeGroup")) {
            if (utils.isNetworkAvailable()) {
                utils.showProgress();
                final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
                Call<GenericResponse> call = apiClient.goLive(SessionUser.getUser().getUser_id(), status, type, "", "", "", lat, lon);
                call.enqueue(new retrofit2.Callback<GenericResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                        utils.hideProgress();
                        GenericResponse genericResponse = response.body();

                        if (response.code() == 200) {
                            if (genericResponse != null) {
                                Log.i("autolog", "genericResponse: " + genericResponse.getStatus());
                                if (genericResponse.getStatus().equalsIgnoreCase("success")) {
                                    clicklive = "0";
                                   /* Log.e(TAG, "RTM onResponse: " + SessionUser.getRtmLoginSession());
                                    if (!SessionUser.getRtmLoginSession()) {
                                        doLogin(SessionUser.getUser().getUsername());
                                    } else {
                                        try {
                                            logoutRtm(SessionUser.getUser().getUsername());
                                        } catch (Exception e) {
                                            Log.i("autolog", "e: " + e.toString());
                                        }

                                        mChatManager.createChannel(SessionUser.getUser().getUsername());

                                    }
                                    doLogin(SessionUser.getUser().getUsername());*/


                                    runOnUiThread(() -> {
                                        isResumedActivity = true;
                                        String base64 = SessionUser.getUser().getProfile_pic();
                                        try {
                                            image = URLDecoder.decode(base64, "UTF-8");
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                        if (isSolo) {
                                            if (clicklive.equalsIgnoreCase("0")) {
                                                Intent intent = new Intent(ActivityStreamSet.this, ActivityLiveRoom.class);
                                                intent.putExtra("mode", false);
                                                intent.putExtra("name", SessionUser.getUser().getUsername().trim());
                                                intent.putExtra("selfname", SessionUser.getUser().getUsername());
                                                intent.putExtra("image", image);
                                                intent.putExtra("received", SessionUser.getUser().getReceived());
                                                intent.putExtra("broadcaster", SessionUser.getUser());
                                                intent.putExtra("broadcasterId", SessionUser.getUser().getUser_id());
                                                intent.putExtra("broad_type", "solo");
                                                intent.putExtra("PKuserId", "0");
                                                intent.putExtra("rtmname", SessionUser.getUser().getUsername().trim());
                                                intent.putStringArrayListExtra("images", null);
                                                intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_BROADCASTER);
                                                intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, SessionUser.getUser().getUsername().trim());
                                                startActivity(intent);
                                                /*mChatManager.removeChatHandler(mChatHandler);*/
                                            }
                                        } else if (isScreenShare) {
                                            Intent intent = new Intent(ActivityStreamSet.this, ActivityScreenSharing.class);
                                            intent.putExtra("name", SessionUser.getUser().getUsername().trim());
                                            intent.putExtra("selfname", SessionUser.getUser().getUsername());
                                            intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_BROADCASTER);
                                            intent.putExtra("image", image);
                                            intent.putExtra("received", SessionUser.getUser().getReceived());
                                            intent.putExtra("broadcaster", SessionUser.getUser());
                                            intent.putExtra("broadcasterId", SessionUser.getUser().getUser_id());
                                            intent.putStringArrayListExtra("images", null);
                                            intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, SessionUser.getUser().getUsername().trim());
                                            startActivity(intent);
                                            /*mChatManager.removeChatHandler(mChatHandler);*/
                                        } else if (isGroup) {
                                            if (type.equals("groupOf3")) {
                                                Intent intent = new Intent(ActivityStreamSet.this, ActivityGroupCalls3.class);
                                                intent.putExtra("mode", false);
                                                intent.putExtra("name", SessionUser.getUser().getUsername().trim());
                                                intent.putExtra("selfname", SessionUser.getUser().getUsername());
                                                intent.putExtra("image", image);
                                                intent.putExtra("received", SessionUser.getUser().getReceived());
                                                intent.putExtra("broadcaster", SessionUser.getUser());
                                                intent.putExtra("broadcasterId", SessionUser.getUser().getUser_id());
                                                intent.putStringArrayListExtra("images", null);
                                                intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_BROADCASTER);
                                                intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, SessionUser.getUser().getUsername().trim());
                                                startActivity(intent);
                                                /*mChatManager.removeChatHandler(mChatHandler);*/
                                            } else if (type.equals("groupOf6")) {
                                                Intent intent = new Intent(ActivityStreamSet.this, ActivityGroupCalls6.class);
                                                intent.putExtra("mode", false);
                                                intent.putExtra("name", SessionUser.getUser().getUsername().trim());
                                                intent.putExtra("selfname", SessionUser.getUser().getUsername());
                                                intent.putExtra("image", image);
                                                intent.putExtra("received", SessionUser.getUser().getReceived());
                                                intent.putExtra("broadcaster", SessionUser.getUser());
                                                intent.putExtra("broadcasterId", SessionUser.getUser().getUser_id());
                                                intent.putStringArrayListExtra("images", null);
                                                intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_BROADCASTER);
                                                intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, SessionUser.getUser().getUsername().trim());
                                                startActivity(intent);
                                                /*mChatManager.removeChatHandler(mChatHandler);*/
                                            } else if (type.equals("groupOf9")) {
                                                Intent intent = new Intent(ActivityStreamSet.this, ActivityGroupCalls9.class);
                                                intent.putExtra("mode", false);
                                                intent.putExtra("name", SessionUser.getUser().getUsername().trim());
                                                intent.putExtra("selfname", SessionUser.getUser().getUsername());
                                                intent.putExtra("image", image);
                                                intent.putExtra("received", SessionUser.getUser().getReceived());
                                                intent.putExtra("broadcaster", SessionUser.getUser());
                                                intent.putExtra("broadcasterId", SessionUser.getUser().getUser_id());
                                                intent.putStringArrayListExtra("images", null);
                                                intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_BROADCASTER);
                                                intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, SessionUser.getUser().getUsername().trim());
                                                startActivity(intent);
                                                /*mChatManager.removeChatHandler(mChatHandler);*/
                                            }
                                        } else if (isKaraoke) {

                                            showToast("Coming Soon...");

                        /*if (type.equals("karaokeSolo")) {
                            Intent intent = new Intent(ActivityStreamSet.this, ActivityKaraokeSolo.class);
                            intent.putExtra("name", SessionUser.getUser().getUsername().trim());
                            intent.putExtra("selfname", SessionUser.getUser().getUsername());
                            intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_BROADCASTER);
                            intent.putExtra("image", image);
                            intent.putExtra("received", SessionUser.getUser().getReceived());
                            intent.putExtra("broadcaster", SessionUser.getUser());
                            intent.putExtra("broadcasterId", SessionUser.getUser().getUser_id());
                            intent.putStringArrayListExtra("images", null);
                            intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, SessionUser.getUser().getUsername().trim());
                            startActivity(intent);
                            mChatManager.removeChatHandler(mChatHandler);
                        } else if (type.equals("karaokeGroup")) {
                            Intent intent = new Intent(ActivityStreamSet.this, ActivityKaraokeDual.class);
                            intent.putExtra("name", SessionUser.getUser().getUsername().trim());
                            intent.putExtra("selfname", SessionUser.getUser().getUsername());
                            intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_BROADCASTER);
                            intent.putExtra("image", image);
                            intent.putExtra("received", SessionUser.getUser().getReceived());
                            intent.putExtra("broadcaster", SessionUser.getUser());
                            intent.putExtra("broadcasterId", SessionUser.getUser().getUser_id());
                            intent.putStringArrayListExtra("images", null);
                            intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, SessionUser.getUser().getUsername().trim());
                            startActivity(intent);
                            mChatManager.removeChatHandler(mChatHandler);
                        }*/
                                        } else if (isAudio) {
                       /* Intent intent = new Intent(ActivityStreamSet.this, ActivityNewPK.class);
                        intent.putExtra("mode", false);
                        intent.putExtra("name", SessionUser.getUser().getUsername().trim());
                        intent.putExtra("selfname", SessionUser.getUser().getUsername());
                        intent.putExtra("image", image);
                        intent.putExtra("received", SessionUser.getUser().getReceived());
                        intent.putExtra("broadcaster", SessionUser.getUser());
                        intent.putExtra("broadcasterId", SessionUser.getUser().getUser_id());
                        intent.putStringArrayListExtra("images", null);
                        intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_BROADCASTER);
                        intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, SessionUser.getUser().getUsername().trim());
                        startActivity(intent);
                        mChatManager.removeChatHandler(mChatHandler);*/

                                            showToast("Coming Soon...");
                                        }
                                    });


                                } else {
                                    showToast(genericResponse.getMessage());
                                }
                            } else {
                                utils.showToast(getString(R.string.server_error));
                            }
                        } else {
                            Log.e(TAG, "onResponse: failed" + response.toString());
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
        } else {
            Toast.makeText(mActivity, "Coming Soon...", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.iv_groupCall3)
    public void groupOf3() {
        if (isGroup) {
            ivGroupCall3.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.MULTIPLY);
            ivGroupCall6.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
            ivGroupCall9.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
            type = "groupOf3";
        }
    }

    @OnClick(R.id.iv_groupCall6)
    public void groupOf6() {
        if (isGroup) {
            ivGroupCall3.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
            ivGroupCall6.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.MULTIPLY);
            ivGroupCall9.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
            type = "groupOf6";
        }
    }

    @OnClick(R.id.iv_groupCall9)
    public void groupOf9() {
        if (isGroup) {
            ivGroupCall3.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
            ivGroupCall6.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
            ivGroupCall9.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.MULTIPLY);
            type = "groupOf9";
        }
    }

    @OnClick(R.id.iv_karaokeSolo)
    public void onKaraokeSolo() {
        if (isKaraoke) {
            ivKaraokeSolo.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.MULTIPLY);
            ivKaraokeGroup.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
            type = "karaokeSolo";
        }
    }

    @OnClick(R.id.iv_karaokeGroup)
    public void onKaraokeGroup() {
        if (isKaraoke) {
            ivKaraokeSolo.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
            ivKaraokeGroup.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.MULTIPLY);
            type = "karaokeGroup";
        }
    }

    @OnClick(R.id.iv_close)
    public void onClose() {
        finishAffinity();
        /*mChatManager.removeChatHandler(mChatHandler);*/
        /*mChatManager.leaveChannel();*/
        changeActivity(ActivityHome.class);
    }

/*
    private void doLogin(String mChannelName) {
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
                        // logoutRtm(SessionUser.getUser().getUsername().trim());
                    } else {
                        mChatManager.doLogin();
                    }
                    //
                });
            }
        });
    }
*/

/*
    public void logoutRtm(String mChannelname) {
        mRtmClient.logout(new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e(TAG, "logout onSuccess: ");
                doLogin(mChannelname);
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                Log.e(TAG, "logout Error: " + errorInfo.getErrorCode() + "  " + errorInfo.getErrorDescription());
            }
        });
    }
*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
       /* mChatManager.removeChatHandler(mChatHandler);
        mChatManager.leaveChannel();*/
        changeActivity(ActivityHome.class);
        finish();
    }

    @Override
    protected void onResume() {
        clicklive = "1";
        isActivityResumed = true;
        Log.i("autolog", "isActivityResumed: " + isActivityResumed);
        super.onResume();
        if (checkPermissions()) {
            /*getLastLocation();*/
        }

        if (isResumedActivity) {
            Log.i("autolog", "isResumedActivity: " + isResumedActivity);
            finishAffinity();
          /*  mChatManager.removeChatHandler(mChatHandler);
            mChatManager.leaveChannel();*/

            Intent mStartActivity = new Intent(this, ActivitySplash.class);
            mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mStartActivity);
            finish();
            Runtime.getRuntime().exit(0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            //Check if the permission is granted or not.

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    //showToast("Granted");
                } else {
                   /* Toast.makeText(this,
                            "Draw over other app permission not available. Closing the application",
                            Toast.LENGTH_SHORT).show();*/
                }
            }
        }
    }


    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    Log.i("autolog", "mLastLocation: " + task.getResult().getLatitude() + "lon" + task.getResult().getLongitude());
                                    lat = String.valueOf(task.getResult().getLatitude());
                                    lon = String.valueOf(task.getResult().getLongitude());
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
               /* Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);*/
            }
        } else {
            requestPermissions();
        }
    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            Log.i("autolog", "mLastLocation: " + mLastLocation.getLatitude() + "lon" + mLastLocation.getLongitude());
            /*latTextView.setText(mLastLocation.getLatitude()+"");
            lonTextView.setText(mLastLocation.getLongitude()+"");*/
            lat = String.valueOf(mLastLocation.getLatitude());
            lon = String.valueOf(mLastLocation.getLongitude());

        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                /*getLastLocation();*/
            }
        }
    }

}

