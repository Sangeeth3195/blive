package com.blive.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blive.model.GetTarget;
import com.blive.model.URL;
import com.blive.service.ServiceGenerator;
import com.blive.service.linkshorten;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blive.BLiveApplication;
import com.blive.R;
import com.blive.adapter.AdapterActiveViewers;
import com.blive.adapter.AdapterFriendsShare;
import com.blive.adapter.AdapterGiftGRP;
import com.blive.adapter.AdapterGroupTopper;
import com.blive.adapter.AdapterGuest;
import com.blive.adapter.AdapterImages;
import com.blive.adapter.AdapterMessage;
import com.blive.adapter.AdapterRequests;
import com.blive.agora.AGEventHandler;
import com.blive.agora.AGLinearLayout;
import com.blive.agora.FaceBeautificationPopupWindow;
import com.blive.agora.GridVideoViewContainer6;
import com.blive.agora.rtmChat.ChatHandler;
import com.blive.agora.rtmChat.ChatManager;
import com.blive.constant.Constants_api;
import com.blive.constant.Constants_app;
import com.blive.custom.LikeAnimationView;
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
import com.blive.model.UsersResponse;
import com.blive.service.ApiClientBase;
import com.blive.service.ApiInterface;
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
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.JsonArray;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.agora.rtc.Constants;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmChannel;
import io.agora.rtm.RtmChannelAttribute;
import io.agora.rtm.RtmChannelListener;
import io.agora.rtm.RtmChannelMember;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmMessage;
import io.agora.rtm.RtmStatusCode;
import jp.wasabeef.picasso.transformations.BlurTransformation;
import retrofit2.Call;
import retrofit2.Response;

public class ActivityGroupCalls6 extends BaseActivity implements AGEventHandler, AdapterGiftGRP.ListenerGift,
        AdapterImages.ListenerImage, AdapterRequests.Listener, AdapterGuest.Listener, AdapterMessage.ListenerMessage,
        AdapterGroupTopper.Listener, AdapterFriendsShare.Listener, AdapterActiveViewers.ListenerActiveViers {

    @BindView(R.id.cvRequests)
    CardView cvRequests;
    @BindView(R.id.iv_gift)
    ImageView ivGift;
    @BindView(R.id.rl_live)
    RelativeLayout rlLive;
    @BindView(R.id.bottom_broadcaster)
    AGLinearLayout bottomBroadcaster;
    @BindView(R.id.bottom_audience)
    AGLinearLayout bottomAudience;
    @BindView(R.id.iv_user)
    ImageView ivUser;
    @BindView(R.id.iv_follow)
    ImageView ivFollow;
    @BindView(R.id.ivVideoRequest)
    ImageView ivVideoRequest;
    @BindView(R.id.rv_freeGift)
    RecyclerView rvFreeGift;
    @BindView(R.id.tv_freeGifts)
    TextView tvFreeGifts;
    @BindView(R.id.iv_diamond)
    ImageView ivDiamond;
    @BindView(R.id.fl_Broadcaster)
    FrameLayout flBroadcastFrame;
    @BindView(R.id.fl_GuestOne)
    FrameLayout flGuestOneFrame;
    @BindView(R.id.fl_GuestTwo)
    FrameLayout flGuestTwoFrame;
    @BindView(R.id.fl_GuestThree)
    FrameLayout flGuestThreeFrame;
    @BindView(R.id.fl_GuestFour)
    FrameLayout flGuestFourFrame;
    @BindView(R.id.fl_GuestFive)
    FrameLayout flGuestFiveFrame;
    @BindView(R.id.tv_broadcast_gold)
    TextView tvBroadcastGold;
    @BindView(R.id.tv_guest_one_gold)
    TextView tvGuestOneGold;
    @BindView(R.id.tv_guest_two_gold)
    TextView tvGuestTwoGold;
    @BindView(R.id.tv_guest_three_gold)
    TextView tvGuestThreeGold;
    @BindView(R.id.tv_guest_four_gold)
    TextView tvGuestFourGold;
    @BindView(R.id.tv_guest_five_gold)
    TextView tvGuestFiveGold;
    @BindView(R.id.star_layout)
    ImageView starImage;
    @BindView(R.id.tv_entranceName)
    TextView tvEntranceName;
    @BindView(R.id.iv_entrance)
    ImageView ivEntrance;
    @BindView(R.id.rl_guest_one_gold_status)
    RelativeLayout rlGuestOneGoldLayout;
    @BindView(R.id.rl_guest_two_gold_status)
    RelativeLayout rlGuestTwoGoldLayout;
    @BindView(R.id.rl_guest_three_gold_status)
    RelativeLayout rlGuestThreeGoldLayout;
    @BindView(R.id.rl_guest_four_gold_status)
    RelativeLayout rlGuestFourGoldLayout;
    @BindView(R.id.rl_guest_five_gold_status)
    RelativeLayout rlGuestFiveGoldLayout;
    @BindView(R.id.iv_end_broadcast)
    ImageView ivEndBroadcast;
    @BindView(R.id.rl_videoMute2)
    RelativeLayout rlGuestTwoMute;
    @BindView(R.id.rl_videoMute1)
    RelativeLayout rlGuestOneMute;
    @BindView(R.id.rl_videoMute3)
    RelativeLayout rlGuestThreeMute;
    @BindView(R.id.rl_videoMute4)
    RelativeLayout rlGuestFourMute;
    @BindView(R.id.rl_videoMute5)
    RelativeLayout rlGuestFiveMute;
    @BindView(R.id.rl_videoMuteBroadcater)
    RelativeLayout rlBroadcasterMute;
    @BindView(R.id.iv_guest1)
    ImageView ivGuest1;
    @BindView(R.id.iv_guest2)
    ImageView ivGuest2;
    @BindView(R.id.iv_broadcast)
    ImageView ivBroadcast;
    @BindView(R.id.iv_camera_swap)
    ImageView ivCameraSwap;
    @BindView(R.id.iv_video_mute)
    ImageView ivVideoMute;
    @BindView(R.id.iv_guest_two_small_gift)
    ImageView ivGuestTwoSmallGift;
    @BindView(R.id.iv_guest_one_small_gift)
    ImageView ivGuestOneSmallGift;
    @BindView(R.id.iv_guest_three_small_gift)
    ImageView ivGuestThreeSmallGift;
    @BindView(R.id.iv_guest_four_small_gift)
    ImageView ivGuestFourSmallGift;
    @BindView(R.id.iv_guest_five_small_gift)
    ImageView ivGuestFiveSmallGift;
    @BindView(R.id.iv_broadcast_small_gift)
    ImageView ivBroadcastSmallGift;
    @BindView(R.id.tv_connecting_guest_one)
    TextView tvConnectingguestOne;
    @BindView(R.id.tv_connecting_guest_two)
    TextView tvConnectingguestTwo;
    @BindView(R.id.tv_connecting_guest_three)
    TextView tvConnectingguestThree;
    @BindView(R.id.tv_connecting_guest_four)
    TextView tvConnectingguestFour;
    @BindView(R.id.tv_connecting_guest_five)
    TextView tvConnectingguestFive;
    @BindView(R.id.moonLevelStar)
    ImageView starRatings;
    @BindView(R.id.rl_videoPauseBroadcater)
    RelativeLayout rlVideoPauseBroadcater;

    private String privatestatus = "";
    protected Context context;
    private GridVideoViewContainer6 mGridVideoViewContainer6;
    private final HashMap<Integer, SurfaceView> mUidsList = new HashMap<>(); // uid = 0 || uid == EngineConfig.mUid
    private RecyclerView rvMessages, rvEndCall, rv_guestTopperList, rvFriendsList;
    private List<MessageBean> messageBeanList;
    private ArrayList<ImageView> ivBlurList;
    private AdapterMessage adapter;
    private LinearLayout llChat, llNormalGift, llGift, llUnFollow, llFollow, llKickOut;
    private String channelName = "", selfName = "", image = "", broadcasterId = "", isFollowing = "", time = "", guestId = "", giftSendTo = "",
            level = "", multiplier = "", removeGuestId = "", dailyAndWeeklyGold = "", idelTime = "", broadTime = "", url_url = "";
    private int cRole = 0, position = -1, size = 0, viewers = 0, likes = 0, gold = 0, oldGold = 0, diamondValue = 0, mPosition = -1, temp = 0, guestUid = 0,
            dvalue = 0, temp1 = 0, lastPage = 0, oldMoonImage = 0;
    private boolean isFirst = false, isLiked = false, isClose = false, isSwiped = false, isSwipedDown = false, isArrived = false, isBroadcastEnded = false,
            isRequested = false, isGiftShowing = false, isTextMuted = false, isBroadcaster = false, isAudience = false, isGuest = false,
            isGiftSelected = false, isGuest1 = false, isGuest2 = false, isGuest3 = false, isGuest4 = false, isGuest5 = false, isEntranceEffects = false, flag = false, isGuestVideoMuted = false, isGuestMutedThread = false,
            isCallRequested = false, isRefreshing = false, isUserListEnd = false, isAPICalled = false, isGuestRequest = false, isBroadcasterMuted = false, isDiamondPurchase = false,
            isAcceptRequest = true, isThisGuest1 = false, isThisGuest2 = false, isThisGuest3 = false, isThisGuest4 = false, isThisGuest5 = false, isProfileShowing = false, isGuestAvailable = false, isBroadcasterOffline = true,
            isCallEnd = false, isClickedProfile = false, isFollowingFrMessage = false, isAudienceFollowing = false;
    private int channelUserCount, mStartIndex, page = 1;
    private Gift mGift;
    ArrayList<String> textmutelist = new ArrayList<>();
    private LikeAnimationView mLikeAnimationView;
    private ArrayList<Bitmap> mAnimationItemList;
    private Animation slideUp, slideDown;
    private User broadcaster;
    CallbackManager callbackManager;
    private ArrayList<Gift> giftsList, giftTools, freeGifts;
    private ArrayList<Audience> audiences, guestAddedList;
    private ArrayList<RelativeLayout> rlVideoMutes;
    private long startTime, endTime, onPauseStartTime, onResumeStopTime, totalIdelTime = 0;
    private ArrayList<User> users, usersFriendsList;
    private ArrayList<Audience> mAudiences, mRequests, mGuests;
    private AdapterRequests adapterRequests;
    private AdapterGiftGRP adapterGifts;
    private Audience tempGuest;
    private TextView tv_count_req, tvNoRequests, tvAssets, tvGift, tvCount, tvGiftName, tvDiamondValue, tvNoContributors, tvGuestName,count_tv,
            tvGuestLevel, tvGuestGold, tvNoFriendsList, tvBliveId, tvMoonLevelCount, tvGiftsList, tvAssetsList, tvFreeGiftsList, tvOffline, tvStarLevel;
    private RecyclerView rvRequests, rvImages;
    private Button btnSendFriends, btnChangeAssests;
    private CheckBox checkBoxSelectAllFriends;
    private ImageView ivGiftItem, iv, ivGuestProfile, ivOffline, ivFrame;
    private RelativeLayout rl_notify, rlOffline;
    private CardView cvCloseCall;
    private RecyclerView rvAssets, rvGift;
    private FloatingActionMenu fabMenu;
    private FloatingActionButton ivRequest, fabMic, fabBeauty;
    private ArrayList<GiftMessage> giftMessages;
    final Handler handler = new Handler();
    private Audience guestOne, guestTwo, guestThree, guestFour, guestFive;
    private ArrayList<Audience> guestUsers;
    private ArrayList<EntranceEffect> entranceEffects;
    private ArrayList<String> messagesList;
    private ProgressBar progressBar;
    private AdapterFriendsShare adapterFriends;
    private AlertDialog alertDialog;
    final Handler callEndHandler = new Handler();
    private FaceBeautificationPopupWindow mFaceBeautificationPopupWindow;
    private RtmClient mRtmClient;
    private ChatManager mChatManager;
    private RtmChannel mRtmChannel;
    private ChatHandler mChatHandler;

    ArrayList<String> blockedlist = new ArrayList<>();
    private VideoEncoderConfiguration.VideoDimensions localVideoDimensions = null;

    // notification
    NotificationCompat.Builder mBuilder;
    NotificationManagerCompat notificationManager;
    int notificationId = 35667;
    String CHANNEL_ID = "my_channel_id";
    private boolean isCreated = true;
    private boolean clicked = false;

    @BindView(R.id.ivActiveViewers)
    ImageView ivActiveViewers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_calls6);
        BLiveApplication.setCurrentActivity(this);
        callbackManager = CallbackManager.Factory.create();
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isCreated) {
            if (isGuest) {
                callGuestVideoMuteResume();
            } else if (isBroadcaster) {
                onResumeStopTime = System.currentTimeMillis();
                long idelTime = onResumeStopTime - onPauseStartTime;
                totalIdelTime = totalIdelTime + idelTime;
                isBroadcasterMuted = false;
                rlVideoPauseBroadcater.setVisibility(View.GONE);
                callBroadcasterUnMute();
            } else if (isAudience) {
                worker().getRtcEngine().muteAllRemoteAudioStreams(false);
            }
        } else {
            isCreated = false;
        }

        BLiveApplication.setCurrentActivity(this);
        if (isDiamondPurchase) {
            isDiamondPurchase = false;
            dvalue = Integer.valueOf(SessionUser.getUser().getDiamond());
            tvDiamondValue.setText(SessionUser.getUser().getDiamond());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isGuest) {
            callGuestVideoMutePause();
        } else if (isBroadcaster) {
            onPauseStartTime = System.currentTimeMillis();
            Log.e(TAG, "onResume: " + onPauseStartTime);
            isBroadcasterMuted = true;
            rlVideoPauseBroadcater.setVisibility(View.VISIBLE);
            callBroadcasterMute();
        } else if (isAudience) {
            worker().getRtcEngine().muteAllRemoteAudioStreams(true);
        }
    }

    private void callBroadcasterMute() {
        worker().getRtcEngine().disableVideo();
        if (!flag)
            worker().getRtcEngine().muteLocalAudioStream(true);
        worker().getRtcEngine().muteAllRemoteAudioStreams(true);
        sendChannelMessage(SessionUser.getUser().getUser_id() + level + "Broadcast has been Paused!");
    }

    private void callBroadcasterUnMute() {
        clearNotification();
        worker().getRtcEngine().enableVideo();
        Log.e(TAG, "onResume: " + flag);
        if (!flag)
            worker().getRtcEngine().muteLocalAudioStream(false);
        worker().getRtcEngine().muteAllRemoteAudioStreams(false);
        sendChannelMessage(SessionUser.getUser().getUser_id() + level + "Broadcast has been Resumed!");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    private boolean isBroadcaster(int cRole) {
        return cRole == Constants.CLIENT_ROLE_BROADCASTER;
    }

    private boolean isBroadcaster() {
        return isBroadcaster(config().mClientRole);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initUI() {

        event().addEventHandler(this);

        Intent i = getIntent();
        cRole = i.getIntExtra(Constants_app.ACTION_KEY_CROLE, 0);
        channelName = i.getStringExtra("name");
        selfName = i.getStringExtra("selfname");
        channelUserCount = i.getIntExtra("usercount", 0);
        image = i.getStringExtra("image");
        broadcasterId = i.getStringExtra("broadcasterId");
        isFollowing = i.getStringExtra("isFollowing");
        broadcaster = i.getParcelableExtra("broadcaster");
        position = i.getIntExtra("position", -1);
        users = i.getParcelableArrayListExtra("users");
        SessionUser.isScreenSharing(true);
        startTime = System.currentTimeMillis();

        tvBroadcastGold.setText(broadcaster.getOver_all_gold());

        try {
            privatestatus = ActivityStreamSet.status;
        } catch (Exception e) {
            privatestatus = "";
        }
        if (cRole == 1) {
            Log.e(TAG, "initViews: " + privatestatus);
            if (privatestatus.equalsIgnoreCase("PRIVATE")) {
                ivActiveViewers.setVisibility(View.VISIBLE);

            } else {
                ivActiveViewers.setVisibility(View.GONE);
            }

        }

        blockedlist.clear();

        mChatManager = BLiveApplication.getInstance().getChatManager();
        mRtmClient = mChatManager.getRtmClient();
        mRtmChannel = mChatManager.getRtmChannel();

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
                    if (isBroadcaster) {
                        if (isBroadcasterMuted) {
                            new Handler().postDelayed(() -> sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " Broadcast has been Paused!"), 2500);
                        }
                    } else if (isGuest1) {
                        if (isGuestVideoMuted)
                            new Handler().postDelayed(() -> sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " GuestOne Muted"), 2000);
                    } else if (isGuest2) {
                        if (isGuestVideoMuted)
                            new Handler().postDelayed(() -> sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " GuestTwo Muted"), 2000);
                    } else if (isGuest3) {
                        if (isGuestVideoMuted)
                            new Handler().postDelayed(() -> sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " GuestThree Muted"), 2000);
                    } else if (isGuest4) {
                        if (isGuestVideoMuted)
                            new Handler().postDelayed(() -> sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " GuestFour Muted"), 2000);
                    } else if (isGuest5) {
                        if (isGuestVideoMuted)
                            new Handler().postDelayed(() -> sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " GuestFive Muted"), 2000);
                    }
                });
            }

            @Override
            public void onMemberLeft(RtmChannelMember rtmChannelMember) {
                runOnUiThread(() -> {
                    if (!isBroadcastEnded)
                        callAudiencesAPI(broadcaster.getUser_id());
                });
            }
        };


        mChatManager.addChantHandler(mChatHandler);
        if (cRole!=0){
            joinrtmchannel(broadcaster.getUsername(),"0");
        }else if (cRole==0){
            joinrtmchannel(SessionUser.getUser().getUsername(),"0");
        }
        mRequests = new ArrayList<>();
        mAudiences = new ArrayList<>();
        mGuests = new ArrayList<>();
        giftMessages = new ArrayList<>();
        giftsList = new ArrayList<>();
        giftTools = new ArrayList<>();
        freeGifts = new ArrayList<>();
        guestUsers = new ArrayList<>();
        entranceEffects = new ArrayList<>();
        messagesList = new ArrayList<>();
        usersFriendsList = new ArrayList<>();
        rlVideoMutes = new ArrayList<>();
        ivBlurList = new ArrayList<>();

        btnChangeAssests = findViewById(R.id.btn_changeAssests);
        ivFrame = findViewById(R.id.iv_frame);
        fabMenu = findViewById(R.id.menu);
        ivRequest = findViewById(R.id.fb_request);
        tvDiamondValue = findViewById(R.id.currentdiamondvalue);
        rvRequests = findViewById(R.id.rvRequests9);
        rlOffline = findViewById(R.id.rl_offline);
        tvOffline = findViewById(R.id.tv_offline);
        ivOffline = findViewById(R.id.iv_offline);
        rvImages = findViewById(R.id.rv_images);
        rvRequests = findViewById(R.id.rvRequests9);
        rvEndCall = findViewById(R.id.rvEndcall9);
        iv = findViewById(R.id.iv);
        rl_notify = findViewById(R.id.rl_notify);
        tv_count_req = findViewById(R.id.tv_count_req);
        cvCloseCall = findViewById(R.id.cvCloseCall);
        tvNoRequests = findViewById(R.id.tv_no_requests);
        FrameLayout root = findViewById(R.id.root);
        rvAssets = findViewById(R.id.rv_assets);
        tvAssets = findViewById(R.id.tv_assets);
        tvGift = findViewById(R.id.tv_gift);
        tvGiftsList = findViewById(R.id.tv_GiftList);
        tvAssetsList = findViewById(R.id.tv_AssetsList);
        tvFreeGiftsList = findViewById(R.id.tv_FreeGiftList);
        tvCount = findViewById(R.id.tv_count);
        tvGiftName = findViewById(R.id.tv_gift_name);
        ivGiftItem = findViewById(R.id.iv_gift_item);
        llNormalGift = findViewById(R.id.ll_normal_gift);
        llGift = findViewById(R.id.ll_gift);
        rvGift = findViewById(R.id.rv_gift);
        fabMic = findViewById(R.id.fb_mike);
        tvMoonLevelCount = findViewById(R.id.tvMoonLevelCount);
        ivVideoMute.setVisibility(View.GONE);
        ivEndBroadcast.setVisibility(View.GONE);
        fabBeauty = findViewById(R.id.fb_beauty);
        tvStarLevel = findViewById(R.id.tv_star_level);
        rlVideoMutes.add(rlGuestOneMute);
        rlVideoMutes.add(rlGuestTwoMute);
        rlVideoMutes.add(rlGuestThreeMute);
        rlVideoMutes.add(rlGuestFourMute);
        rlVideoMutes.add(rlGuestFiveMute);
        ivBlurList.add(ivGuest1);
        ivBlurList.add(ivGuest2);

        Glide.with(getApplicationContext())
                .load(R.drawable.diamond_svg)
                .into(ivDiamond);

        Glide.with(getApplicationContext())
                .load(Constants_api.guest_Frame)
                .into(ivFrame);

        tvDiamondValue.setText(SessionUser.getUser().getDiamond());
        dvalue = Integer.valueOf(SessionUser.getUser().getDiamond());

        loadStarImage(broadcaster.getOver_all_gold());

        ivActiveViewers.setOnClickListener(v -> loadInviteListsFriendsPrivate());

        getLevel();

        if (image != null && !image.isEmpty()) {
            Picasso.get().load(image).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivUser);
            Picasso.get().load(image).fit().transform(new BlurTransformation(mActivity)).centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).
                    placeholder(R.drawable.user).into(ivOffline);
            Picasso.get().load(image).fit().transform(new BlurTransformation(mActivity)).centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivBroadcast);
        } else {
            Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivUser);
            Picasso.get().load(R.drawable.user).fit().transform(new BlurTransformation(mActivity)).centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).
                    placeholder(R.drawable.user).into(ivOffline);
            Picasso.get().load(R.drawable.blur).fit().transform(new BlurTransformation(mActivity)).centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivBroadcast);
        }

        if (cRole == 0) {
            throw new RuntimeException("Should not reach here");
        }

        String roomName = i.getStringExtra(Constants_app.ACTION_KEY_ROOM_NAME);

        doConfigEngine(cRole);

        mGridVideoViewContainer6 = findViewById(R.id.grid_video_view_container6);

        slideUp = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);

        // Setting up onClickListeners for send button
        ImageButton imageButton = findViewById(R.id.sendButton);
        imageButton.setOnClickListener(sendButtonListener);

        rvMessages = findViewById(R.id.message_list);
        rvMessages.setNestedScrollingEnabled(false);
        rvEndCall = findViewById(R.id.rvEndcall9);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvMessages.setLayoutManager(layoutManager);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        linearLayoutManager1.setOrientation(RecyclerView.VERTICAL);
        rvRequests.setLayoutManager(linearLayoutManager1);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        linearLayoutManager2.setOrientation(RecyclerView.VERTICAL);
        rvEndCall.setLayoutManager(linearLayoutManager2);
        LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(this);
        linearLayoutManager3.setOrientation(RecyclerView.VERTICAL);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2, GridLayoutManager.HORIZONTAL, false);
        rvGift.setLayoutManager(gridLayoutManager);
        rvGift.setVisibility(View.VISIBLE);
        rvGift.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(getApplicationContext(), 2, GridLayoutManager.HORIZONTAL, false);
        rvAssets.setLayoutManager(gridLayoutManager1);
        rvAssets.setVisibility(View.VISIBLE);
        rvAssets.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager2 = new GridLayoutManager(getApplicationContext(), 2, GridLayoutManager.HORIZONTAL, false);
        rvFreeGift.setLayoutManager(gridLayoutManager2);
        rvFreeGift.setVisibility(View.VISIBLE);
        rvFreeGift.setHasFixedSize(true);

        giftsList = new ArrayList<>();

        ImageView buttonMsg = findViewById(R.id.btn_msg);
        llChat = findViewById(R.id.ll_chat);

        messageBeanList = new ArrayList<>();
        adapter = new AdapterMessage(this, messageBeanList);
        rvMessages.setAdapter(adapter);
        adapter.setOnClickListener(this);

        MessageBean messageBean = new MessageBean(selfName, getResources().getString(R.string.warning), true, true, false);
        messageBeanList.add(messageBean);
        adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
        rvMessages.scrollToPosition(messageBeanList.size() - 1);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        rvImages.setLayoutManager(layoutManager1);

        if (broadcaster != null) {
            oldGold = Integer.valueOf(broadcaster.getOver_all_gold());
            gold = Integer.valueOf(broadcaster.getOver_all_gold());
        }

        if (isBroadcaster(cRole)) {
            SurfaceView surfaceV = RtcEngine.CreateRendererView(getApplicationContext());
            rtcEngine().setupLocalVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, SessionUser.getUser().getId()));
            surfaceV.setZOrderOnTop(true);
            surfaceV.setZOrderMediaOverlay(true);
            surfaceV.setId(mUidsList.size() + 1);
            mUidsList.put(SessionUser.getUser().getId(), surfaceV); // get first surface view
            isBroadcaster = true;
            ArrayList<Audience> guests = new ArrayList<>();
            mGridVideoViewContainer6.initViewContainer(getApplicationContext(), SessionUser.getUser().getId(), mUidsList, true, isBroadcaster, guests);// first is now full view
            worker().preview(true, surfaceV, SessionUser.getUser().getId());

            bottomBroadcaster.setVisibility(View.VISIBLE);
            bottomAudience.setVisibility(View.GONE);
            cvRequests.setVisibility(View.GONE);
            ivFollow.setVisibility(View.GONE);
            ivRequest.setVisibility(View.VISIBLE);
            broadcasterUI(ivCameraSwap);
            fabMic.setVisibility(View.VISIBLE);
            ivVideoMute.setVisibility(View.GONE);

            isAudience = false;
            rlGuestOneGoldLayout.setVisibility(View.GONE);
            rlGuestTwoGoldLayout.setVisibility(View.GONE);
            rlGuestThreeGoldLayout.setVisibility(View.GONE);
            rlGuestFourGoldLayout.setVisibility(View.GONE);
            rlGuestFiveGoldLayout.setVisibility(View.GONE);

            fabMic.setOnClickListener(view -> {
                Object tag = view.getTag();
                flag = tag == null || !((boolean) tag);
                worker().getRtcEngine().muteLocalAudioStream(flag);
                FloatingActionButton button = (FloatingActionButton) view;
                button.setTag(flag);
                if (flag) {
                    button.setBackground(getResources().getDrawable(R.mipmap.micmute));
                } else {
                    button.setBackground(getResources().getDrawable(R.mipmap.mic));
                }
                fabMenu.close(true);
            });

        } else {
            isAudience = true;
            isBroadcasterOffline = true;
            fabBeauty.setVisibility(View.GONE);
            rlOffline.setVisibility(View.VISIBLE);
            fabMic.setVisibility(View.GONE);
            rvImages.setVisibility(View.GONE);
            rvMessages.setVisibility(View.GONE);
            fabMenu.setVisibility(View.GONE);
            ivGift.setVisibility(View.GONE);
            ivRequest.setVisibility(View.GONE);
            bottomBroadcaster.setVisibility(View.GONE);
            bottomAudience.setVisibility(View.GONE);
            cvRequests.setVisibility(View.GONE);
            ivActiveViewers.setVisibility(View.GONE);
            isTextMuted = broadcaster.getText_muted().equalsIgnoreCase("yes");

            if (isFollowing.equalsIgnoreCase("yes"))
                ivFollow.setVisibility(View.GONE);
            else
                ivFollow.setVisibility(View.VISIBLE);

            rlGuestOneGoldLayout.setVisibility(View.GONE);
            rlGuestTwoGoldLayout.setVisibility(View.GONE);
            rlGuestThreeGoldLayout.setVisibility(View.GONE);
            rlGuestFourGoldLayout.setVisibility(View.GONE);
            rlGuestFiveGoldLayout.setVisibility(View.GONE);

            audienceUI(ivCameraSwap);
        }

        worker().joinChannel(roomName, SessionUser.getUser().getId());

        TextView textRoomName = findViewById(R.id.room_name);
        textRoomName.setText(broadcaster.getName());

        buttonMsg.setOnClickListener(v -> {
            llChat.setVisibility(View.VISIBLE);
            EditText userTypedMessage = findViewById(R.id.userMessageBox);
            userTypedMessage.requestFocus();
            // Show soft keyboard for the user to enter the value.
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.showSoftInput(userTypedMessage, InputMethodManager.SHOW_IMPLICIT);
        });

        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }

            private GestureDetector gestureDetector = new GestureDetector(ActivityGroupCalls6.this, new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    if (fabMenu.isOpened()) {
                        fabMenu.close(true);
                    } else if (llGift.getVisibility() == View.VISIBLE) {
                        llGift.startAnimation(slideDown);
                        llGift.setVisibility(View.GONE);
                    }
                    return false;
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    doLikeAnimation();
                    return super.onDoubleTap(e);
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    switch (getSlope(e1.getX(), e1.getY(), e2.getX(), e2.getY())) {
                        case 1:
                            onSwipeUp();
                            Log.e(TAG, "onFling: top");
                            return true;
                        case 2:
                            onSwipeLeft();
                            Log.e(TAG, "onFling: left");
                            return true;
                        case 3:
                            onSwipeDown();
                            Log.e(TAG, "onFling: down");
                            return true;
                        case 4:
                            onSwipeRight();
                            Log.e(TAG, "onFling: right");
                            return true;
                    }
                    return false;
                }
            });

            void onSwipeUp() {
            }

            void onSwipeDown() {
            }

            void onSwipeLeft() {
            }

            void onSwipeRight() {
            }

            private int getSlope(float x1, float y1, float x2, float y2) {
                Double angle = Math.toDegrees(Math.atan2(y1 - y2, x2 - x1));
                if (angle > 45 && angle <= 135)
                    // top
                    return 1;
                if (angle >= 135 && angle < 180 || angle < -135 && angle > -180)
                    // left
                    return 2;
                if (angle < -45 && angle >= -135)
                    // down
                    return 3;
                if (angle > -45 && angle <= 45)
                    // right
                    return 4;
                return 0;
            }
        });

        rlLive.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(ActivityGroupCalls6.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    doLikeAnimation();
                    if (fabMenu.isOpened()) {
                        fabMenu.close(true);
                    }
                    return super.onDoubleTap(e);
                }


                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    switch (getSlope(e1.getX(), e1.getY(), e2.getX(), e2.getY())) {
                        case 1:
                            onSwipeUp();
                            return true;
                        case 2:
                            onSwipeLeft();
                            return true;
                        case 3:
                            onSwipeDown();
                            return true;
                        case 4:
                            onSwipeRight();
                            return true;
                    }
                    return false;
                }
            });

            void onSwipeUp() {
                /*if (!isBroadcaster()) {
                    position = position + 1;
                    isSwiped = true;
                    isClose = false;
                    callRemoveImageAPI();
                }*/
            }

            void onSwipeDown() {
               /* if (!isBroadcaster()) {
                    position = position - 1;
                    isSwiped = true;
                    isSwipedDown = true;
                    isClose = false;
                    callRemoveImageAPI();
                }*/
            }

            void onSwipeLeft() {
            }

            void onSwipeRight() {
                Log.e(TAG, "onSwipeRight: ");
            }

            private int getSlope(float x1, float y1, float x2, float y2) {
                Double angle = Math.toDegrees(Math.atan2(y1 - y2, x2 - x1));
                if (angle > 45 && angle <= 135)
                    // top
                    return 1;
                if (angle >= 135 && angle < 180 || angle < -135 && angle > -180)
                    // left
                    return 2;
                if (angle < -45 && angle >= -135)
                    // down
                    return 3;
                if (angle > -45 && angle <= 45)
                    // right
                    return 4;
                return 0;
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (llChat.getVisibility() == View.VISIBLE) {
                    EditText userTypedMessage = findViewById(R.id.userMessageBox);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(userTypedMessage.getWindowToken(), 0);
                    new Handler().postDelayed(() -> llChat.setVisibility(View.GONE), 500);
                } else if (llGift.getVisibility() == View.VISIBLE) {
                    if (cRole == 1)
                        bottomBroadcaster.setVisibility(View.VISIBLE);
                    else
                        bottomAudience.setVisibility(View.VISIBLE);

                    llGift.startAnimation(slideDown);
                    llGift.setVisibility(View.GONE);
                } else if (cvRequests.getVisibility() == View.VISIBLE) {
                    cvRequests.startAnimation(slideDown);
                    cvRequests.setVisibility(View.GONE);
                } else if (cvCloseCall.getVisibility() == View.VISIBLE) {
                    cvCloseCall.startAnimation(slideDown);
                    cvCloseCall.setVisibility(View.GONE);
                }

                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        if (!isFirst) {
            isFirst = true;
            if (utils.isNetworkAvailable()) {
                final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
                Call<GiftResponse> call = apiClient.getGifts("groupof6", SessionUser.getUser().getUser_id());
                call.enqueue(new retrofit2.Callback<GiftResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GiftResponse> call, @NonNull Response<GiftResponse> response) {
                        GiftResponse giftResponse = response.body();
                        if (response.code() == 200) {
                            if (giftResponse != null) {
                                if (giftResponse.getStatus().equalsIgnoreCase("success")) {
                                    setGifts(giftResponse.getData().getGifts(), giftResponse.getData().getTools(), giftResponse.getData().getFreeGits());
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

        ivVideoRequest.postDelayed(() -> ivVideoRequest.setVisibility(View.VISIBLE), 5000);

    }

    @Override
    protected void deInitUI() {
        doLeaveChannel();
        event().removeEventHandler(this);
        mUidsList.clear();
    }

    @OnClick(R.id.fb_request)
    public void onClickRequests() {
        fabMenu.close(true);
        cvRequests.startAnimation(slideUp);
        cvRequests.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.iv_follow)
    public void onClickFollow() {
        if (utils.isNetworkAvailable()) {
            callFollowAPI("follow", broadcaster.getUser_id(), broadcaster.getName());
        }
    }

    private void callFollowAPI(String type, String userId, String name) {
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
                                if (isFollowingFrMessage == true) {
                                    Log.e(TAG, "onResponse: " + isFollowingFrMessage);
                                    llFollow.setVisibility(View.GONE);
                                    llUnFollow.setVisibility(View.VISIBLE);
                                }
                                getLevel();
                                if (isBroadcaster) {
                                    isAudienceFollowing = true;
                                }
                                MessageBean message = new MessageBean(selfName, SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " started following you", true, false, false);
                                messageBeanList.add(message);
                                adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                                rvMessages.scrollToPosition(messageBeanList.size() - 1);
                                sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " started following" + " " + name);
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

    public void onClickMessage(View view) {
        if (fabMenu.isOpened()) {
            fabMenu.close(true);
        }
        if (!isBroadcaster) {
            if (!isTextMuted)
                llChat.setVisibility(View.VISIBLE);
            else
                showToast("You Are Currently Text Muted \"For This Session.\"");
        }
    }

    public void onClickEffects(View view) {
        if (isBroadcaster()) {
            if (mFaceBeautificationPopupWindow == null) {
                mFaceBeautificationPopupWindow = new FaceBeautificationPopupWindow(this.getBaseContext());
            }
        } else {
            return;
        }

        if (!mFaceBeautificationPopupWindow.isShowing()) {
            mFaceBeautificationPopupWindow.show(view, new FaceBeautificationPopupWindow.UserEventHandler() {
                @Override
                public void onFBSwitch(boolean on) {
                    if (on) {
                        Constants_app.BEAUTY_EFFECT_ENABLED = true;
                        worker().enablePreProcessor();
                    } else {
                        worker().disablePreProcessor();
                        Constants_app.BEAUTY_EFFECT_ENABLED = false;
                    }
                }

                @Override
                public void onLightnessSet(float lightness) {
                    worker().setBeautyEffectParameters(lightness, Constants_app.BEAUTY_OPTIONS.smoothnessLevel, Constants_app.BEAUTY_OPTIONS.rednessLevel);
                }

                @Override
                public void onSmoothnessSet(float smoothness) {
                    worker().setBeautyEffectParameters(Constants_app.BEAUTY_OPTIONS.lighteningLevel, smoothness, Constants_app.BEAUTY_OPTIONS.rednessLevel);
                }

                @Override
                public void onRednessSet(float redness) {
                    worker().setBeautyEffectParameters(Constants_app.BEAUTY_OPTIONS.lighteningLevel, Constants_app.BEAUTY_OPTIONS.smoothnessLevel, redness);
                }
            });
        }
    }

    private void doLikeAnimation() {
        this.mLikeAnimationView = findViewById(R.id.like_animation_view);
        this.mLikeAnimationView.post(() -> {
            mLikeAnimationView.setEndPoint(new PointF((float) (mLikeAnimationView.getMeasuredWidth() / 2), 0.0f));
            mLikeAnimationView.setLikeAnimationViewProvider(new LikeAnimationViewProvider());
        });
        if (this.mAnimationItemList == null) {
            this.mAnimationItemList = new ArrayList();
            this.mAnimationItemList.add(((BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.dd, null)).getBitmap());
            this.mAnimationItemList.add(((BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.b, null)).getBitmap());
            this.mAnimationItemList.add(((BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.aa, null)).getBitmap());
            this.mAnimationItemList.add(((BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.c, null)).getBitmap());
            this.mAnimationItemList.add(((BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.cc, null)).getBitmap());
            this.mAnimationItemList.add(((BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.dd, null)).getBitmap());
        }
        if (this.mStartIndex == this.mAnimationItemList.size() - 1) {
            this.mStartIndex = 0;
        }
        this.mLikeAnimationView.startAnimation(this.mStartIndex);
        this.mStartIndex++;

        if (cRole != 1) {
            if (!isLiked) {
                MessageBean messageBean = new MessageBean(selfName, SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " has liked ♥", true, false, false);
                messageBeanList.add(messageBean);
                adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                rvMessages.scrollToPosition(messageBeanList.size() - 1);
                sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " has liked ♥");
            }
            isLiked = true;
        }
    }

    private void doLikeAnimationAudience() {
        this.mLikeAnimationView = findViewById(R.id.like_animation_view);
        this.mLikeAnimationView.post(() -> {
            mLikeAnimationView.setEndPoint(new PointF((float) (mLikeAnimationView.getMeasuredWidth() / 2), 0.0f));
            mLikeAnimationView.setLikeAnimationViewProvider(new LikeAnimationViewProvider());
        });
        if (this.mAnimationItemList == null) {
            this.mAnimationItemList = new ArrayList();
            this.mAnimationItemList.add(((BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.dd, null)).getBitmap());
            this.mAnimationItemList.add(((BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.b, null)).getBitmap());
            this.mAnimationItemList.add(((BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.aa, null)).getBitmap());
            this.mAnimationItemList.add(((BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.c, null)).getBitmap());
            this.mAnimationItemList.add(((BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.cc, null)).getBitmap());
            this.mAnimationItemList.add(((BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.dd, null)).getBitmap());
        }
        if (this.mStartIndex == this.mAnimationItemList.size() - 1) {
            this.mStartIndex = 0;
        }
        this.mLikeAnimationView.startAnimation(this.mStartIndex);
        this.mStartIndex++;
    }

    @OnClick(R.id.iv_user)
    public void onClickUser() {
        if (cRole != 1) {
            Intent intent = new Intent(this, ActivityViewProfile.class);
            intent.putExtra("image", image);
            intent.putExtra("userId", broadcasterId);
            intent.putExtra("from", "liveRoom");
            startActivityForResult(intent, 1);
        } else {
            onclickBroad(SessionUser.getUser().getUser_id());
            /*showBroadcastProfile(broadcaster);*/
        }
    }

    @OnClick(R.id.iv_gift)
    public void onClickGift() {
        if (llGift.getVisibility() == View.VISIBLE) {
            llGift.startAnimation(slideDown);
            llGift.setVisibility(View.GONE);
        } else {
            llGift.startAnimation(slideUp);
            llGift.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.tv_gift)
    public void onClickGiftMenu() {
        tvGift.setTextColor(getResources().getColor(R.color.colorAccent));
        tvAssets.setTextColor(getResources().getColor(R.color.white));
        tvFreeGifts.setTextColor(getResources().getColor(R.color.white));
        btnChangeAssests.setVisibility(View.GONE);
        if (giftsList.size() > 0) {
            rvAssets.setVisibility(View.GONE);
            rvFreeGift.setVisibility(View.GONE);
            rvGift.setVisibility(View.VISIBLE);
            tvAssetsList.setVisibility(View.GONE);
            tvGiftsList.setVisibility(View.GONE);
            tvFreeGiftsList.setVisibility(View.GONE);
        } else {
            rvAssets.setVisibility(View.GONE);
            rvFreeGift.setVisibility(View.GONE);
            rvGift.setVisibility(View.GONE);
            tvAssetsList.setVisibility(View.GONE);
            tvGiftsList.setVisibility(View.VISIBLE);
            tvFreeGiftsList.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.tv_assets)
    public void onClickAssetsMenu() {
        tvAssets.setTextColor(getResources().getColor(R.color.colorAccent));
        tvFreeGifts.setTextColor(getResources().getColor(R.color.white));
        tvGift.setTextColor(getResources().getColor(R.color.white));
        btnChangeAssests.setVisibility(View.VISIBLE);
        if (giftTools.size() > 0) {
            adapterGifts = new AdapterGiftGRP(this, giftTools);
            adapterGifts.setOnClickListener(this);
            rvAssets.setAdapter(adapterGifts);
            rvGift.setVisibility(View.GONE);
            rvFreeGift.setVisibility(View.GONE);
            rvAssets.setVisibility(View.VISIBLE);
            tvAssetsList.setVisibility(View.GONE);
            tvGiftsList.setVisibility(View.GONE);
            tvFreeGiftsList.setVisibility(View.GONE);
        } else {
            rvGift.setVisibility(View.GONE);
            rvFreeGift.setVisibility(View.GONE);
            rvAssets.setVisibility(View.GONE);
            tvAssetsList.setVisibility(View.VISIBLE);
            tvGiftsList.setVisibility(View.GONE);
            tvFreeGiftsList.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.tv_freeGifts)
    public void onClickFreeGift() {
        tvFreeGifts.setTextColor(getResources().getColor(R.color.colorAccent));
        tvGift.setTextColor(getResources().getColor(R.color.white));
        tvAssets.setTextColor(getResources().getColor(R.color.white));
        btnChangeAssests.setVisibility(View.GONE);
        if (freeGifts.size() > 0) {
            adapterGifts = new AdapterGiftGRP(this, freeGifts);
            adapterGifts.setOnClickListener(this);
            rvFreeGift.setAdapter(adapterGifts);
            rvGift.setVisibility(View.GONE);
            rvAssets.setVisibility(View.GONE);
            rvFreeGift.setVisibility(View.VISIBLE);
            tvAssetsList.setVisibility(View.GONE);
            tvGiftsList.setVisibility(View.GONE);
            tvFreeGiftsList.setVisibility(View.GONE);
        } else {
            rvGift.setVisibility(View.GONE);
            rvAssets.setVisibility(View.GONE);
            rvFreeGift.setVisibility(View.GONE);
            tvAssetsList.setVisibility(View.GONE);
            tvGiftsList.setVisibility(View.GONE);
            tvFreeGiftsList.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.btn_changeAssests)
    public void onClickchangeAssests() {
        Intent intent = new Intent(mActivity, ActivityWebView.class);
        intent.putExtra("title", "My Assests");
        intent.putExtra("from", "groupcall3");
        intent.putExtra("url", Constants_api.assets + SessionUser.getUser().getUser_id());
        startActivity(intent);
    }

    public void onClickClose(View view) {
        if (cRole == 1) {
            callBroadcasterClose();
        } else {
            callAudienceClose();
        }
    }

    public void removeGuestApi(String removeGuestId, boolean isRemoveSelf, boolean isBroadcasterRemove, int i) {
        Log.e(TAG, "removeGuestApi: " + removeGuestId + " " + isRemoveSelf + " " + isBroadcasterRemove + " " + i);
        if (utils.isNetworkAvailable()) {
            if (isBroadcasterRemove) {
                switch (i) {
                    case 1:
                        rlGuestOneGoldLayout.setVisibility(View.GONE);
                        sendChannelMessage(removeGuestId + level + SessionUser.getUser().getName() + " Guest One Removed");
                        rlGuestOneMute.setVisibility(View.GONE);
                        break;
                    case 2:
                        rlGuestTwoGoldLayout.setVisibility(View.GONE);
                        sendChannelMessage(removeGuestId + level + SessionUser.getUser().getName() + " Guest Two Removed");
                        rlGuestTwoMute.setVisibility(View.GONE);

                        break;
                    case 3:
                        rlGuestThreeGoldLayout.setVisibility(View.GONE);
                        sendChannelMessage(removeGuestId + level + SessionUser.getUser().getName() + " Guest Three Removed");
                        rlGuestThreeMute.setVisibility(View.GONE);

                        break;
                    case 4:
                        rlGuestFourGoldLayout.setVisibility(View.GONE);
                        sendChannelMessage(removeGuestId + level + SessionUser.getUser().getName() + " Guest Four Removed");
                        rlGuestFourMute.setVisibility(View.GONE);

                        break;
                    case 5:
                        rlGuestFiveGoldLayout.setVisibility(View.GONE);
                        sendChannelMessage(removeGuestId + level + SessionUser.getUser().getName() + " Guest Five Removed");
                        rlGuestFiveMute.setVisibility(View.GONE);

                        break;
                }
            } else if (isRemoveSelf) {
                switch (i) {
                    case 1:
                        rlGuestOneGoldLayout.setVisibility(View.GONE);
                        sendChannelMessage(removeGuestId + level + SessionUser.getUser().getName() + " Guest One Left the Broadcast");
                        rlGuestOneMute.setVisibility(View.GONE);
                        clicked = false;

                        break;
                    case 2:
                        rlGuestTwoGoldLayout.setVisibility(View.GONE);
                        sendChannelMessage(removeGuestId + level + SessionUser.getUser().getName() + " Guest Two Left the Broadcast");
                        rlGuestTwoMute.setVisibility(View.GONE);
                        clicked = false;

                        break;
                    case 3:
                        rlGuestThreeGoldLayout.setVisibility(View.GONE);
                        sendChannelMessage(removeGuestId + level + SessionUser.getUser().getName() + " Guest Three Left the Broadcast");
                        rlGuestThreeMute.setVisibility(View.GONE);
                        clicked = false;

                        break;
                    case 4:
                        rlGuestFourGoldLayout.setVisibility(View.GONE);
                        sendChannelMessage(removeGuestId + level + SessionUser.getUser().getName() + " Guest Four Left the Broadcast");
                        rlGuestFourMute.setVisibility(View.GONE);
                        clicked = false;

                        break;
                    case 5:
                        rlGuestFiveGoldLayout.setVisibility(View.GONE);
                        sendChannelMessage(removeGuestId + level + SessionUser.getUser().getName() + " Guest Five Left the Broadcast");
                        rlGuestFiveMute.setVisibility(View.GONE);
                        clicked = false;

                        break;
                }
            }
            this.removeGuestId = removeGuestId;
            callRemoveGuestAPI(removeGuestId);
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
            utils.hideProgress();
            Intent intent = new Intent(ActivityGroupCalls6.this, ActivityBroadcastStop.class);
            intent.putExtra("name", broadcaster.getName());
            intent.putExtra("image", broadcaster.getProfile_pic());
            finish();
            startActivity(intent);
        }
    }

    private View.OnClickListener sendButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            getLevel();
            EditText userTypedMessage = findViewById(R.id.userMessageBox);
            String msg = userTypedMessage.getText().toString();
            if (!msg.equals("")) {
                MessageBean messageBean = new MessageBean(selfName, SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " : " + msg, true, false, false);
                messageBeanList.add(messageBean);
                adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                rvMessages.scrollToPosition(messageBeanList.size() - 1);
                sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " : " + msg);
            }

            userTypedMessage.setText("");

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(userTypedMessage.getWindowToken(), 0);

            new Handler().postDelayed(() -> llChat.setVisibility(View.GONE), 500);
        }
    };

    private void broadcasterUI(ImageView button2) {
        button2.setOnClickListener(v ->
                worker().getRtcEngine().switchCamera());

        fabMic.setOnClickListener(view -> {
            Object tag = view.getTag();
            flag = tag == null || !((boolean) tag);
            worker().getRtcEngine().muteLocalAudioStream(flag);
            FloatingActionButton button = (FloatingActionButton) view;
            button.setTag(flag);
            if (flag) {
                button.setBackground(getResources().getDrawable(R.mipmap.micmute));
            } else {
                button.setBackground(getResources().getDrawable(R.mipmap.mic));
            }
            fabMenu.close(true);
        });
    }

    private void audienceUI(ImageView button2) {
        button2.setVisibility(View.GONE);
        fabMic.setVisibility(View.GONE);
        ivGift.setVisibility(View.VISIBLE);
    }

    /*private void doConfigEngine(int cRole) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int prefIndex = pref.getInt(Constants_app.PrefManager.PREF_PROPERTY_PROFILE_IDX, Constants_app.DEFAULT_PROFILE_IDX);
        if (prefIndex > Constants_app.VIDEO_DIMENSIONS.length - 1) {
            prefIndex = Constants_app.DEFAULT_PROFILE_IDX;
        }
        //int vProfile = Constants_app.VIDEO_DIMENSIONS[4];
        VideoEncoderConfiguration.VideoDimensions dimension = Constants_app.VIDEO_DIMENSIONS[prefIndex];
        localVideoDimensions = dimension;
        worker().configEngine(cRole, dimension);
        //  worker().configEngine(cRole, vProfile);
        //  rtcEngine().setParameters("{\"che.audio.live_for_comm\":true}");
        //  rtcEngine().setParameters("{\"che.video.moreFecSchemeEnable\":true}");

        //   Log.e(TAG, "doConfigEngine: vProfile : " + vProfile);
        //   if (vProfile == Constants.VIDEO_PROFILE_480P) {
        //            rtcEngine().setParameters("{\"che.video.lowBitRateStreamParameter\":{\"width\":240,\"height\":320,\"frameRate\":15,\"bitRate\":140}}");
        //        } else {
        //            rtcEngine().setParameters("{\"che.video.lowBitRateStreamParameter\":{\"width\":180,\"height\":320,\"frameRate\":15,\"bitRate\":140}}");
        //        }
    }*/

    private void doConfigEngine(int cRole) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int prefIndex = pref.getInt(Constants_app.PrefManager.PREF_PROPERTY_PROFILE_IDX, Constants_app.DEFAULT_PROFILE_IDX);
        if (prefIndex > Constants_app.VIDEO_PROFILES.length - 1) {
            prefIndex = Constants_app.DEFAULT_PROFILE_IDX;
        }
        int vProfile = Constants_app.VIDEO_PROFILES[prefIndex];

        worker().configEngine1(cRole, vProfile);
    }

    private void doLeaveChannel() {
        worker().leaveChannel(config().mChannel);
        if (isBroadcaster) {
            worker().preview(false, null, 0);
        }
    }

    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {

    }

    private void doRenderRemoteUi(final int uid) {
        runOnUiThread(() -> {
            if (isFinishing()) {
                return;
            }

            if (!isBroadcaster && !isGuest) {
                isBroadcasterOffline = false;
                bottomAudience.setVisibility(View.VISIBLE);
                rlOffline.setVisibility(View.GONE);
                rvImages.setVisibility(View.VISIBLE);
                rvMessages.setVisibility(View.VISIBLE);
                fabMenu.setVisibility(View.VISIBLE);
                ivGift.setVisibility(View.VISIBLE);
            }

            if (mUidsList.size() < 6) {
                SurfaceView surfaceV = RtcEngine.CreateRendererView(getApplicationContext());
                surfaceV.setZOrderOnTop(true);
                surfaceV.setZOrderMediaOverlay(true);
                mUidsList.put(uid, surfaceV);

                if (config().mUid == uid) {
                    rtcEngine().setupLocalVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, uid));
                } else {
                    rtcEngine().setupRemoteVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, uid));
                }
                Log.e(TAG, "doRenderRemoteUi: ");
                callGetGuestsAPI();
            }
        });
    }

    @Override
    public void onJoinChannelSuccess(final String channel, final int uid, final int elapsed) {
        runOnUiThread(() -> {
            if (isFinishing()) {
                return;
            }

            if (mUidsList.containsKey(uid)) {
                Log.e(TAG, "already added to UI, ignore it " + (uid & 0xFFFFFFFFL) + " " + mUidsList.get(uid));
                return;
            }

            final boolean isBroadcaster = isBroadcaster();
            Log.e(TAG, "onJoinChannelSuccess " + channel + " " + uid + " " + elapsed + " " + isBroadcaster);

            worker().getEngineConfig().mUid = uid;

            SurfaceView surfaceV = mUidsList.remove(0);
            if (surfaceV != null) {
                mUidsList.put(uid, surfaceV);

                if (guestUsers.size() < 1) {
                    Audience user = new Audience();
                    user.setUser_id(broadcaster.getUser_id());
                    user.setCurrent_gold_value(broadcaster.getCurrent_gold_value());
                    user.setOver_all_gold(broadcaster.getOver_all_gold());
                    user.setActivation_code(broadcaster.getActivation_code());

                    String uidValue = String.valueOf(uid);
                    user.setUid(uidValue);
                    guestUsers.add(user);

                }
            }
        });
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        if (uid == broadcaster.getId()) {
//            if (!isBroadcastEnded)
//                runOnUiThread(this::callAPIOffline);
        } else {
            if (isBroadcaster) {
                for (int i = 0; i < mGuests.size(); i++) {
                    if (uid == mGuests.get(i).getId()) {
                        int finalI = i;
                        runOnUiThread(() -> callRemoveGuestAPI(mGuests.get(finalI).getUser_id()));
                        runOnUiThread(() -> {
                            if (finalI == 0) {
                                sendChannelMessage(removeGuestId + level + SessionUser.getUser().getName() + " Guest One Removed");
                                rlGuestOneMute.setVisibility(View.GONE);
                            } else if (finalI == 1) {
                                sendChannelMessage(removeGuestId + level + SessionUser.getUser().getName() + " Guest Two Removed");
                                rlGuestTwoMute.setVisibility(View.GONE);
                            } else if (finalI == 2) {
                                sendChannelMessage(removeGuestId + level + SessionUser.getUser().getName() + " Guest Three Removed");
                                rlGuestThreeMute.setVisibility(View.GONE);
                            } else if (finalI == 3) {
                                sendChannelMessage(removeGuestId + level + SessionUser.getUser().getName() + " Guest Four Removed");
                                rlGuestFourMute.setVisibility(View.GONE);
                            } else if (finalI == 4) {
                                sendChannelMessage(removeGuestId + level + SessionUser.getUser().getName() + " Guest Five Removed");
                                rlGuestFiveMute.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            }
        }
        doRemoveRemoteUi(uid);
    }

    private void callRemoveGuestAPI(String guestId) {
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<UsersResponse> call = apiClient.removeGuest(guestId, broadcasterId);
            call.enqueue(new retrofit2.Callback<UsersResponse>() {
                @Override
                public void onResponse(@NonNull Call<UsersResponse> call, @NonNull Response<UsersResponse> response) {
                    UsersResponse usersResponse = response.body();
                    clicked = false;

                    if (response.code() == 200) {
                        if (usersResponse != null) {
                            if (usersResponse.getStatus().equalsIgnoreCase("success")) {
                                onRemoveGuestSuccess(usersResponse.getData().getGuests());
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
                    clicked = false;

                }
            });
        }
    }

    private void callAPIOffline() {
        sendChannelMessage(broadcaster.getUser_id() + level + SessionUser.getUser().getName() + " Broadcast has ended");
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<GenericResponse> call = apiClient.offline(broadcaster.getUser_id());
            call.enqueue(new retrofit2.Callback<GenericResponse>() {
                @Override
                public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                    GenericResponse genericResponse = response.body();
                    if (response.code() == 200) {
                        if (genericResponse != null) {
                            if (genericResponse.getStatus().equalsIgnoreCase("success")) {
                                runOnUiThread(() -> {
                                    isBroadcastEnded = true;
                                    callRemoveImageAPI();
                                });
                            } else {
                                runOnUiThread(() -> finish());
                            }
                        } else {
                            runOnUiThread(() -> finish());
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

    @Override
    public void onUserJoined(int uid, int elapsed) {
        doRenderRemoteUi(uid);
    }

    private void doRemoveRemoteUi(final int uid) {
        runOnUiThread(() -> {
            if (isFinishing()) {
                return;
            }

            mUidsList.remove(uid);

            int bigBgUid = -1;

            Log.e(TAG, "doRemoveRemoteUi " + (uid & 0xFFFFFFFFL) + " " + (bigBgUid & 0xFFFFFFFFL));
            callGetGuestsAPI();
        });
    }

    private void switchToDefaultVideoView(ArrayList<Audience> guests) {
        Log.e(TAG, "switchToDefaultVideoView: " + mUidsList.size());
        if (mUidsList.size() == 1) {
            for (HashMap.Entry<Integer, SurfaceView> entry : mUidsList.entrySet()) {
                if (entry.getKey() != broadcaster.getId()) {
                    isBroadcasterMuted = true;
                    rlBroadcasterMute.setVisibility(View.VISIBLE);
                }
            }
        }

        mGridVideoViewContainer6.initViewContainer(getApplicationContext(), broadcaster.getId(), mUidsList, false, isBroadcaster, guests);

        mViewType = VIEW_TYPE_DEFAULT;

        int sizeLimit = mUidsList.size();
        if (sizeLimit > Constants_app.MAX_PEER_COUNT + 1) {
            sizeLimit = Constants_app.MAX_PEER_COUNT + 1;
        }
    }

    public int mViewType = VIEW_TYPE_DEFAULT;

    public static final int VIEW_TYPE_DEFAULT = 0;

    /**
     * API CALL: send message to a channel
     */
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (errorCode) {
                            case RtmStatusCode.ChannelMessageError.CHANNEL_MESSAGE_ERR_TIMEOUT:
                            case RtmStatusCode.ChannelMessageError.CHANNEL_MESSAGE_ERR_FAILURE:
//                                showToast(getString(R.string.send_msg_failed));
                                break;
                        }
                    }
                });
            }
        });
    }

    private void removeUserFromList(String id) {
        if (mRequests.size() > 0) {
            for (int i = 0; i < mAudiences.size(); i++) {
                if (id.equalsIgnoreCase(mAudiences.get(i).getUser_id())) {
                    mRequests.remove(mAudiences.get(i));
                    adapterRequests.notifyDataSetChanged();
                }
            }
        }
        int size = mRequests.size();
        if (size > 0) {
            tv_count_req.setText(String.valueOf(size));
            rl_notify.setVisibility(View.VISIBLE);
            tvNoRequests.setVisibility(View.GONE);
        } else {
            rl_notify.setVisibility(View.GONE);
            tvNoRequests.setVisibility(View.VISIBLE);
        }
    }

    private void callGetGuestsAPI() {
        if (utils.isNetworkAvailable()) {
            if (utils.isNetworkAvailable()) {
                final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
                Call<UsersResponse> call = apiClient.getGuests(SessionUser.getUser().getUser_id(), broadcasterId);
                call.enqueue(new retrofit2.Callback<UsersResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<UsersResponse> call, @NonNull Response<UsersResponse> response) {
                        UsersResponse usersResponse = response.body();
                        if (response.code() == 200) {
                            if (usersResponse != null) {
                                if (usersResponse.getStatus().equalsIgnoreCase("success")) {
                                    tvConnectingguestOne.setVisibility(View.GONE);
                                    tvConnectingguestTwo.setVisibility(View.GONE);
                                    tvConnectingguestThree.setVisibility(View.GONE);
                                    tvConnectingguestFour.setVisibility(View.GONE);
                                    tvConnectingguestFive.setVisibility(View.GONE);
                                    onGuestListSuccess(usersResponse.getData().getGuests());
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
    }

    private void switchAudienceToGuest() {
        isGuest = true;
        doSwitchToBroadcaster(true);
    }

    private void callAdapterRequests(ArrayList<Audience> mRequests) {
        if (mRequests.size() > 0) {
            adapterRequests = new AdapterRequests(getApplicationContext(), mRequests);
            adapterRequests.setOnClickListener(this);
            rvRequests.setAdapter(adapterRequests);
            tvNoRequests.setVisibility(View.GONE);
            rvRequests.setVisibility(View.VISIBLE);
        } else {
            tvNoRequests.setVisibility(View.VISIBLE);
            rvRequests.setVisibility(View.GONE);
        }
    }

    private void callSwitchCase(int j) {
        isGiftShowing = true;
        for (int i = 0; i < giftsList.size(); i++) {
            if (giftMessages.get(j).getMessage().contains(giftsList.get(i).getName())) {
                if (giftsList.get(i).getType().equalsIgnoreCase("normal")) {
                    /*normalGift(giftMessages.get(j).getAccount(), giftsList.get(i).getThumbnail(), giftsList.get(i).getName(), Integer.valueOf(giftsList.get(i).getDuration()));*/
                    setGifGift(giftsList.get(i), giftMessages.get(j).getMessage());
                    handler.postDelayed(() -> {
                        iv.setVisibility(View.GONE);
                        ivBroadcastSmallGift.setVisibility(View.GONE);
                        ivGuestOneSmallGift.setVisibility(View.GONE);
                        ivGuestTwoSmallGift.setVisibility(View.GONE);
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
                    setGifGift(giftsList.get(i), giftMessages.get(j).getMessage());
                    handler.postDelayed(() -> {
                        iv.setVisibility(View.GONE);
                        ivBroadcastSmallGift.setVisibility(View.GONE);
                        ivGuestOneSmallGift.setVisibility(View.GONE);
                        ivGuestTwoSmallGift.setVisibility(View.GONE);
                        temp = temp + 1;
                        if (giftMessages.size() > temp) {
                            callSwitchCase(temp);
                        } else {
                            temp = 0;
                            giftMessages.clear();
                            isGiftShowing = false;
                        }
                    }, Integer.valueOf(giftsList.get(i).getDuration()));
                }
                gold = gold + Integer.valueOf(giftsList.get(i).getPrice());
            }
        }
    }

    private void normalGift(String sender, String image, String name, int duration) {
        Picasso.get().load(image).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivGiftItem);
        tvGiftName.setText(sender + " has sent you a " + name);
        llNormalGift.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> llNormalGift.setVisibility(View.GONE), duration);
    }

    private void setGifGift(Gift gift, String giftMessage) {
        String sendId = giftMessage.substring(0, 6);
        String completePath = getApplicationContext().getFilesDir() + "/.webPStorage/" + gift.getName() + ".webp";

        File fileNew = new File(completePath);
        if (fileNew.exists()) {
            int giftPrice = Integer.parseInt(gift.getPrice());
            if (gift.getType().equalsIgnoreCase("normal")) {
                if (isGuest1) {
                    isGuestAvailable = true;
                    if (sendId.equals(guestOne.getUser_id())) {
                        ivGuestOneSmallGift.setVisibility(View.VISIBLE);
                        Log.e("giftFile", fileNew.getAbsolutePath() + " " + gift.getGif());
                        Glide.with(getApplicationContext())
                                .load(fileNew)
                                .addListener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        Crashlytics.logException(e);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        return false;
                                    }
                                }).into(ivGuestOneSmallGift);
                    }
                }
                if (isGuest2) {
                    isGuestAvailable = true;
                    if (sendId.equals(guestTwo.getUser_id())) {
                        ivGuestTwoSmallGift.setVisibility(View.VISIBLE);
                        Glide.with(getApplicationContext())
                                .load(fileNew)
                                .addListener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        Crashlytics.logException(e);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        return false;
                                    }
                                }).into(ivGuestTwoSmallGift);
                    }
                }
                if (isGuest3) {
                    isGuestAvailable = true;
                    if (sendId.equals(guestThree.getUser_id())) {
                        ivGuestThreeSmallGift.setVisibility(View.VISIBLE);
                        Glide.with(getApplicationContext())
                                .load(fileNew)
                                .addListener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        Crashlytics.logException(e);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        return false;
                                    }
                                }).into(ivGuestThreeSmallGift);
                    }
                }
                if (isGuest4) {
                    isGuestAvailable = true;
                    if (sendId.equals(guestFour.getUser_id())) {
                        ivGuestFourSmallGift.setVisibility(View.VISIBLE);
                        Glide.with(getApplicationContext())
                                .load(fileNew)
                                .addListener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        Crashlytics.logException(e);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        return false;
                                    }
                                }).into(ivGuestFourSmallGift);
                    }
                }
                if (isGuest5) {
                    isGuestAvailable = true;
                    if (sendId.equals(guestFive.getUser_id())) {
                        ivGuestFiveSmallGift.setVisibility(View.VISIBLE);
                        Glide.with(getApplicationContext())
                                .load(fileNew)
                                .addListener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        Crashlytics.logException(e);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        return false;
                                    }
                                }).into(ivGuestFiveSmallGift);
                    }
                }
                Log.e("SendGiftId", sendId + " " + broadcaster.getUser_id());
                if (sendId.equals(broadcaster.getUser_id()) || !isGuestAvailable) {
                    ivBroadcastSmallGift.setVisibility(View.VISIBLE);
                    isGuestAvailable = false;
                    Glide.with(getApplicationContext())
                            .load(fileNew)
                            .addListener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    Crashlytics.logException(e);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    return false;
                                }
                            }).into(ivBroadcastSmallGift);
                }
            } else {
                iv.setVisibility(View.VISIBLE);
                Glide.with(getApplicationContext())
                        .load(fileNew)
                        .addListener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Crashlytics.logException(e);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        }).into(iv);
            }
        } else {
            try {
                Glide.with(getApplicationContext())
                        .load(gift.getGif())
                        .addListener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Crashlytics.logException(e);
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mChatManager.leaveChannel();
        mChatManager.removeChatHandler(mChatHandler);
    }


    public void onClickJoin(View view) {
        if (!isRequested) {
            isRequested = true;
            MessageBean message = new MessageBean(selfName, SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " has requested to join the broadcast", true, false, false);
            messageBeanList.add(message);
            adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
            rvMessages.scrollToPosition(messageBeanList.size() - 1);
            sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " has requested to join the broadcast");
        } else
            showToast("Already requested !");
    }

    private void doSwitchToBroadcaster(boolean switchToBroadcast) {
        final int currentHostCount = mUidsList.size();
        final int uid = SessionUser.getUser().getId();

        if (switchToBroadcast) {
            startTime = System.currentTimeMillis();
            doConfigEngine(Constants.CLIENT_ROLE_BROADCASTER);
            bottomBroadcaster.setVisibility(View.VISIBLE);
            bottomAudience.setVisibility(View.GONE);
            fabMic.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> {
                doRenderRemoteUi(uid);
                broadcasterUI(ivCameraSwap);

            }, 1000); // wait for reconfig engine
        } else {
            fabMic.setVisibility(View.GONE);
            stopInteraction(currentHostCount, uid);
            bottomBroadcaster.setVisibility(View.GONE);
            bottomAudience.setVisibility(View.VISIBLE);
        }
    }

    private void stopInteraction(final int currentHostCount, final int uid) {
        doConfigEngine(Constants.CLIENT_ROLE_AUDIENCE);

        new Handler().postDelayed(() -> {
            doRemoveRemoteUi(uid);
            audienceUI(ivCameraSwap);
        }, 1000); // wait for reconfig engine
    }

    public void onStatusSuccess() {
        utils.hideProgress();

        MessageBean message = new MessageBean(selfName, SessionUser.getUser().getUser_id() + level + " Broadcast has ended", true, false, false);
        messageBeanList.add(message);
        adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
        rvMessages.scrollToPosition(messageBeanList.size() - 1);
        sendChannelMessage(SessionUser.getUser().getUser_id() + level + " Broadcast has ended");

        endTime = System.currentTimeMillis();
        Constants_app.cleanMessageListBeanList();

        long mills = endTime - startTime;
        int hours = (int) (mills / (1000 * 60 * 60));
        int mins = (int) (mills / (1000 * 60)) % 60;

        if (hours > 1 && mins > 1)
            time = hours + " : " + mins;
        else if (hours == 1 && mins > 1)
            time = hours + " : " + mins;
        else
            time = hours + " : " + mins;


        int idelHours = (int) (totalIdelTime / (1000 * 60 * 60));
        int idelMins = (int) (totalIdelTime / (1000 * 60)) % 60;

        if (idelHours > 1 && idelMins > 1)
            idelTime = idelHours + " : " + idelMins;
        else if (idelHours == 1 && idelMins > 1)
            idelTime = idelHours + " : " + idelMins;
        else
            idelTime = idelHours + " : " + idelMins;

        long totalBroadTime = mills - totalIdelTime;

        int broadHours = (int) (totalBroadTime / (1000 * 60 * 60));
        int broadMins = (int) (totalBroadTime / (1000 * 60)) % 60;

        if (broadHours > 1 && broadMins > 1)
            broadTime = broadHours + " : " + broadMins;
        else if (broadHours == 1 && broadMins > 1)
            broadTime = broadHours + " : " + broadMins;
        else
            broadTime = broadHours + " : " + broadMins;


        Intent intent = new Intent(this, ActivityStreamDetails.class);
        intent.putExtra("gold", String.valueOf(gold - oldGold));
        intent.putExtra("viewers", String.valueOf(viewers));
        intent.putExtra("likes", String.valueOf(likes));
        intent.putExtra("time", String.valueOf(time));
        intent.putExtra("idelTime", String.valueOf(idelTime));
        intent.putExtra("broadTime", String.valueOf(broadTime));
        intent.putExtra("from", "videoCall");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(intent);
    }

    @Override
    public void OnClicked(Gift gift) {
        mGift = gift;
        isGiftSelected = true;
        if (mGift != null) {
            llGift.startAnimation(slideDown);
            llGift.setVisibility(View.GONE);
        } else {
            showToast("Choose a Gift to send");
        }
    }

    private void setAudiences(ArrayList<Audience> audiences, String entranceUrl, int viewers_count, int freeGiftCount, int overAllGold, String istheuserFollowing) {
        mAudiences = audiences;
        AdapterImages adapterImages = new AdapterImages(this, audiences);
        adapterImages.setOnClickListener(this);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        rvImages.setLayoutManager(layoutManager1);
        rvImages.setAdapter(adapterImages);
        viewers = viewers_count;
        tvCount.setText(String.valueOf(viewers));

        if (broadcaster.getUser_id().equals(SessionUser.getUser().getUser_id())) {
            ivFollow.setVisibility(View.GONE);
        } else {
            if (istheuserFollowing.equalsIgnoreCase("yes"))
                ivFollow.setVisibility(View.GONE);
            else
                ivFollow.setVisibility(View.VISIBLE);
        }


        if (!isBroadcaster()) {
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
                entranceEffect.setId(SessionUser.getUser().getUser_id());
                entranceEffects.add(entranceEffect);
                if (entranceEffects.size() > 0) {
                    if (!isEntranceEffects) {
                        callEntranceEffect(temp1);
                    }
                }
                callGetGuestsAPI();
            }
        }
    }

    private void callEntranceEffect(int j) {
        isEntranceEffects = true;
        for (int i = 0; i < giftTools.size(); i++) {
            if (entranceEffects.get(j).getUrl().contains(giftTools.get(i).getGif())) {

                Glide.with(this)
                        .load(entranceEffects.get(j).getUrl())
                        .into(ivEntrance);
                tvEntranceName.setVisibility(View.VISIBLE);
                tvEntranceName.setText(entranceEffects.get(j).getAccount() + "\n is Arriving");
                ivEntrance.setVisibility(View.VISIBLE);

                handler.postDelayed(() -> {
                    tvEntranceName.setText("");
                    tvEntranceName.setVisibility(View.GONE);
                    ivEntrance.setVisibility(View.GONE);
                    temp1 = temp1 + 1;
                    if (entranceEffects.size() > temp1) {
                        callSwitchCase(temp1);
                    } else {
                        temp1 = 0;
                        entranceEffects.clear();
                        isEntranceEffects = false;
                    }
                }, 5000);
            }
        }
    }

    private void getLevel() {
        try {
            level = SessionUser.getUser().getLevel();
            if (level.length() == 1) {
                level = "0" + level;
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    public void onRemoveSuccess(ArrayList<Audience> images) {

    }

    @Override
    public void onAcceptRequest(Audience user, int position) {
        if (!isGuest1 || !isGuest2 || !isGuest3 || !isGuest4 || !isGuest5) {
            if (isAcceptRequest) {
                isAcceptRequest = false;
                cvRequests.startAnimation(slideDown);
                cvRequests.setVisibility(View.GONE);
                callAddGuestAPI(user.getUser_id(), broadcasterId);
                mPosition = position;
                tempGuest = user;
            }
        } else {
            showToast("Can't add more than Five visitor to the Broadcast!");
        }
    }

    private void callAddGuestAPI(String guest_id, String broadcasterId) {
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<UsersResponse> call = apiClient.addGuest(guest_id, broadcasterId);
            call.enqueue(new retrofit2.Callback<UsersResponse>() {
                @Override
                public void onResponse(@NonNull Call<UsersResponse> call, @NonNull Response<UsersResponse> response) {
                    UsersResponse usersResponse = response.body();
                    if (response.code() == 200) {
                        if (usersResponse != null) {
                            if (usersResponse.getStatus().equalsIgnoreCase("success")) {
                                isAcceptRequest = true;
                                guestAddedList = usersResponse.getData().getGuests();
                                utils.hideProgress();
                                bottomBroadcaster.setVisibility(View.VISIBLE);
                                bottomAudience.setVisibility(View.GONE);
                                getLevel();
                                if (isGuestRequest) {
                                    isGuestRequest = false;
                                    switchAudienceToGuest();
                                    sendChannelMessage(SessionUser.getUser().getUser_id() + level + " has accepted your video call request ");
                                    loadGuestDetails(usersResponse.getData().getGuests());
                                } else {
                                    mRequests.remove(mPosition);
                                    tv_count_req.setText(String.valueOf(mRequests.size()));
                                    adapterRequests.notifyDataSetChanged();
                                    sendChannelMessage(tempGuest.getUser_id() + level + " Broadcaster has accepted your broadcast request");
                                    loadGuestDetails(usersResponse.getData().getGuests());
                                }
                            } else {
                                isAcceptRequest = true;
                                showToast(usersResponse.getMessage());
                                cvRequests.startAnimation(slideUp);
                                cvRequests.setVisibility(View.VISIBLE);
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

    private void callAlertKickOut() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityGroupCalls6.this);
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setMessage("You have been kicked out of the room !");
        alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();
        callRemoveAPI();
        new Handler().postDelayed(() -> {
            if (alertDialog.isShowing()) {
                alertDialog.dismiss();
                finishAffinity();
                changeActivity(ActivityHome.class);
            }
        }, 3000);
    }

    private void callRemoveAPI() {

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

        final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
        Call<UsersResponse> call = apiClient.removeAudience(SessionUser.getUser().getUser_id(), broadcasterId, broadcastingTime);
        call.enqueue(new retrofit2.Callback<UsersResponse>() {
            @Override
            public void onResponse(@NonNull Call<UsersResponse> call, @NonNull Response<UsersResponse> response) {
                UsersResponse usersResponse = response.body();
                if (response.code() == 200) {
                    if (usersResponse != null) {
                        if (usersResponse.getStatus().equalsIgnoreCase("success")) {
                            isClose = true;

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

    @Override
    public void onRejectRequest(Audience user,int position) {
        mRequests.remove(position);
        tv_count_req.setText(String.valueOf(mRequests.size()));
        adapterRequests.notifyDataSetChanged();
    }

    public void setGifts(ArrayList<Gift> gifts, ArrayList<Gift> tools, ArrayList<Gift> freeGift) {
        giftsList.addAll(gifts);
        giftTools.addAll(tools);
        freeGifts.addAll(freeGift);

        try {
            adapter.setGifts(gifts);
        } catch (Exception e) {
            Log.e(TAG, "onGiftsSuccess: " + e);
            Crashlytics.logException(e);
        }

        adapterGifts = new AdapterGiftGRP(this, giftsList);
        adapterGifts.setOnClickListener(this);
        rvGift.setAdapter(adapterGifts);

        callAudiencesAPI(broadcaster.getUser_id());
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
                                setAudiences(usersResponse.getData().getAudiences(), usersResponse.getData().getEntranceEffect(), usersResponse.getData().getViewers_count(),
                                        usersResponse.getData().getFreeGiftCount(), usersResponse.getData().getOverAllGold(), usersResponse.getData().getIsTheUserFollowing());
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

    public void onGuestListSuccess(ArrayList<Audience> guests) {
        mGuests.clear();
        mGuests = guests;
        Log.d(TAG, "onGuestListSuccess: " + mGuests.size());
        for (int i = 0; i < guests.size(); i++) {
            Log.d(TAG, "onGuestListSuccess: " + guests.get(i).getId());
        }
        switchToDefaultVideoView(guests);
        if (guests.size() > 0)
            loadGuestDetails(guests);
    }

    public void onRemoveGuestSuccess(ArrayList<Audience> users) {
        mGuests.clear();
        mGuests = users;
        if (isClose) {
            callRemoveImageAPI();
            Constants_app.cleanMessageListBeanList();
            finish();
        } else
            loadGuestDetails(users);
    }

    @Override
    public void onGiftSend(Audience user, int position) {
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<GiftResponse> call = apiClient.sendGift(SessionUser.getUser().getUser_id(), SessionUser.getUser().getUser_id(), user.getUser_id(), mGift.getName(), mGift.getPrice(), multiplier);
            call.enqueue(new retrofit2.Callback<GiftResponse>() {
                @Override
                public void onResponse(@NonNull Call<GiftResponse> call, @NonNull Response<GiftResponse> response) {
                    GiftResponse giftResponse = response.body();
                    if (response.code() == 200) {
                        if (giftResponse != null) {
                            if (giftResponse.getStatus().equalsIgnoreCase("success")) {
                                onGiftSuccess(giftResponse.getData().getUser_id(), giftResponse.getData().getOver_all_gold(), giftResponse.getData().getCurrent_gold_value(), giftResponse.getData().getMoon_level(), giftResponse.getData().getMoon_value(), giftResponse.getData().getGift_name());
                            } else {
                                ivGift.setEnabled(true);
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

    public void onClickSend(View view) {
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<GiftResponse> call = apiClient.sendGift(SessionUser.getUser().getUser_id(), SessionUser.getUser().getUser_id(), broadcasterId, mGift.getName(), mGift.getPrice(), multiplier);
            call.enqueue(new retrofit2.Callback<GiftResponse>() {
                @Override
                public void onResponse(@NonNull Call<GiftResponse> call, @NonNull Response<GiftResponse> response) {
                    GiftResponse giftResponse = response.body();
                    if (response.code() == 200) {
                        if (giftResponse != null) {
                            if (giftResponse.getStatus().equalsIgnoreCase("success")) {
                                onGiftSuccess(giftResponse.getData().getUser_id(), giftResponse.getData().getOver_all_gold(), giftResponse.getData().getCurrent_gold_value(), giftResponse.getData().getMoon_level(), giftResponse.getData().getMoon_value(), giftResponse.getData().getGift_name());
                            } else {
                                ivGift.setEnabled(true);
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

    public void onTopListSuccess(ArrayList<User> dailyUsers, ArrayList<User> weeklyUsers) {
        try {
            tvNoContributors.setVisibility(View.GONE);
            if (dailyAndWeeklyGold.equals("daily")) {
                dailyAndWeeklyGold = "";
                if (dailyUsers.size() > 0) {
                    AdapterGroupTopper adapterTopFans = new AdapterGroupTopper(this, dailyUsers);
                    adapterTopFans.setOnClickListener(this);
                    rv_guestTopperList.setVisibility(View.VISIBLE);
                    rv_guestTopperList.setAdapter(adapterTopFans);
                } else {
                    tvNoContributors.setVisibility(View.VISIBLE);
                }
            } else {
                if (weeklyUsers.size() > 0) {
                    AdapterGroupTopper adapterTopFans = new AdapterGroupTopper(this, weeklyUsers);
                    adapterTopFans.setOnClickListener(this);
                    rv_guestTopperList.setVisibility(View.VISIBLE);
                    rv_guestTopperList.setAdapter(adapterTopFans);
                } else {
                    tvNoContributors.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("topError", e.getMessage());
        }
    }

    @Override
    public void OnClicked(User user) {

    }

    public void loadStarImage(String moonValue) {
        Log.e(TAG, "loadStarImage: " + moonValue);
        String level = Constants_app.loadStarLevel(moonValue);
        tvStarLevel.setText(level);

        if (oldMoonImage != 0) {
            /*Glide.with(getApplicationContext())
                    .load(oldMoonImage)
                    .into(starRatings1);*/
            Drawable res = getResources().getDrawable(oldMoonImage);
            starImage.setImageDrawable(res);
        }

        String uri = Constants_app.loadBroadCasterStar(moonValue);
        Log.e(TAG, "loadStarImage: " + uri);
        int starImage = getResources().getIdentifier(uri, null, getPackageName());
        Drawable res = getResources().getDrawable(starImage);
        starRatings.setImageDrawable(res);
       /* Glide.with(getApplicationContext())
                .load(starImage)
                .into(starRatings);*/

        oldMoonImage = starImage;
    }

    public void onFriendsSuccess(ArrayList<User> users, int mLastPage) {
        lastPage = mLastPage;
        isAPICalled = false;
        isRefreshing = false;
        if (page == 1) {
            if (users.size() > 0) {
                if (users.size() < Constants_app.pageLimit)
                    isUserListEnd = true;

                usersFriendsList.clear();
                usersFriendsList.addAll(users);
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
            if (users.size() > 0) {
                if (users.size() < Constants_app.pageLimit)
                    isUserListEnd = true;

                usersFriendsList.addAll(users);
                adapterFriends.notifyDataSetChanged();
            } else
                isUserListEnd = true;
        }
    }

    @Override
    public void onClickedActiveAudience(Audience audience) {
        showAlertViewProfile(audience, true);
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
                if (msg.contains("Broadcaster has accepted your broadcast request")) {
                    if (!isBroadcaster) {
                        String id = msg.substring(0, 6);
                        if (id.equalsIgnoreCase(SessionUser.getUser().getUser_id())) {
                            switchAudienceToGuest();
                        }
                        callGetGuestsAPI();
                    }
                } else if (msg.contains("Guest One Removed")) {
                    String id = msg.substring(0, 6);
                    if (SessionUser.getUser().getUser_id().equals(id)) {
                        isGuest = false;
                        doSwitchToBroadcaster(false);
                    } else if (isGuest2) {
                        isGuest2 = false;
                        isGuest1 = true;
                        if (isGuestVideoMuted) {
                            rlGuestTwoMute.setVisibility(View.GONE);
                            rlGuestOneMute.setVisibility(View.VISIBLE);
                        } else {
                            rlGuestOneMute.setVisibility(View.GONE);
                        }
                        /*callGetGuestsAPI();*/
                    }
                } else if (msg.contains("Guest Two Removed")) {
                    String id = msg.substring(0, 6);
                    if (SessionUser.getUser().getUser_id().equals(id)) {
                        isGuest = false;
                        doSwitchToBroadcaster(false);
                    }
                } else if (msg.contains("Guest Three Removed")) {
                    String id = msg.substring(0, 6);
                    if (SessionUser.getUser().getUser_id().equals(id)) {
                        isGuest = false;
                        doSwitchToBroadcaster(false);
                    }
                } else if (msg.contains("Guest Four Removed")) {
                    String id = msg.substring(0, 6);
                    if (SessionUser.getUser().getUser_id().equals(id)) {
                        isGuest = false;
                        doSwitchToBroadcaster(false);
                    }
                } else if (msg.contains("Guest Five Removed")) {
                    String id = msg.substring(0, 6);
                    if (SessionUser.getUser().getUser_id().equals(id)) {
                        isGuest = false;
                        doSwitchToBroadcaster(false);
                    }
                } else if (msg.contains("has rejected your video call request")) {
                    if (isBroadcaster) {
                        isCallEnd = false;
                        messageBean = new MessageBean(account, msg, false, false, false);
                        messageBeanList.add(messageBean);
                        adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                        rvMessages.scrollToPosition(messageBeanList.size() - 1);
                        tvConnectingguestOne.setVisibility(View.GONE);
                        tvConnectingguestTwo.setVisibility(View.GONE);
                        tvConnectingguestThree.setVisibility(View.GONE);
                        tvConnectingguestFour.setVisibility(View.GONE);
                        tvConnectingguestFive.setVisibility(View.GONE);
                    }
                } else if (msg.contains("has requested you to join the Video Call")) {
                    if (!isBroadcaster) {
                        String id = msg.substring(0, 6);
                        if (id.equalsIgnoreCase(SessionUser.getUser().getUser_id())) {
                            CallAlertDialog(msg);
                        }
                    }
                } else if (msg.contains("has accepted your video call request")) {
                    isCallEnd = false;
                    String id = msg.substring(0, 6);
                    removeUserFromList(id);
                    callGetGuestsAPI();
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
                } else if (msg.contains("has been muted for this broadcast")) {
                    if (!isBroadcaster) {
                        String id = msg.substring(0, 6);
                        if (id.equalsIgnoreCase(SessionUser.getUser().getUser_id())) {
                            isTextMuted = true;
                        }
                    }
                } else if (msg.contains("has been unMuted for this broadcast")) {
                    String id = msg.substring(0, 6);
                    if (id.equalsIgnoreCase(SessionUser.getUser().getUser_id())) {
                        isTextMuted = false;
                    }
                } else if (msg.contains("enTraNceEffEct")) {
                    String id = msg.substring(0, 6);
                    String message = msg;
                    message = message.replace(id, "");
                    message = message.replace("enTraNceEffEct", "");
                    message = message.trim();
                    EntranceEffect entranceEffect = new EntranceEffect();
                    entranceEffect.setAccount(account);
                    entranceEffect.setUrl(message);
                    entranceEffects.add(entranceEffect);
                    if (entranceEffects.size() > 0) {
                        if (!isEntranceEffects) {
                            callEntranceEffect(temp1);
                        }
                    }
                } else if (msg.contains("broadcaster")) {
                    String message = msg.replace("broadcaster", "");
                    flBroadcastFrame.setVisibility(View.VISIBLE);
                    tvBroadcastGold.setVisibility(View.VISIBLE);
                    tvBroadcastGold.setText(message);
                } else if (msg.contains("guestOne")) {
                    String message = msg.replace("guestOne", "");
                    flGuestOneFrame.setVisibility(View.VISIBLE);
                    tvGuestOneGold.setText(message);
                } else if (msg.contains("guestTwo")) {
                    String message = msg.replace("guestTwo", "");
                    flGuestTwoFrame.setVisibility(View.VISIBLE);
                    tvGuestTwoGold.setText(message);
                } else if (msg.contains("guestThree")) {
                    String message = msg.replace("guestThree", "");
                    flGuestThreeFrame.setVisibility(View.VISIBLE);
                    tvGuestThreeGold.setText(message);
                } else if (msg.contains("guestFour")) {
                    String message = msg.replace("guestFour", "");
                    flGuestFourFrame.setVisibility(View.VISIBLE);
                    tvGuestFourGold.setText(message);
                } else if (msg.contains("guestFive")) {
                    String message = msg.replace("guestFive", "");
                    flGuestFiveFrame.setVisibility(View.VISIBLE);
                    tvGuestFiveGold.setText(message);
                } else if (msg.contains(" GuestOne Muted")) {
                    rlGuestOneMute.setVisibility(View.VISIBLE);
                } else if (msg.contains(" GuestOne UnMuted")) {
                    rlGuestOneMute.setVisibility(View.GONE);
                } else if (msg.contains(" GuestTwo Muted")) {
                    rlGuestTwoMute.setVisibility(View.VISIBLE);
                } else if (msg.contains(" GuestTwo UnMuted")) {
                    rlGuestTwoMute.setVisibility(View.GONE);
                } else if (msg.contains(" GuestThree Muted")) {
                    rlGuestThreeMute.setVisibility(View.VISIBLE);
                } else if (msg.contains(" GuestThree UnMuted")) {
                    rlGuestThreeMute.setVisibility(View.GONE);
                } else if (msg.contains(" GuestFour Muted")) {
                    rlGuestFourMute.setVisibility(View.VISIBLE);
                } else if (msg.contains(" GuestFour UnMuted")) {
                    rlGuestFourMute.setVisibility(View.GONE);
                } else if (msg.contains(" GuestFive Muted")) {
                    rlGuestFiveMute.setVisibility(View.VISIBLE);
                } else if (msg.contains(" GuestFive UnMuted")) {
                    rlGuestFiveMute.setVisibility(View.GONE);
                } else if (msg.contains("StarValue")) {
                    String id = msg.substring(0, 6);
                    String starid = msg.replace(id, "");
                    String starValue = starid.replace("StarValue", "");
                    if (SessionUser.getUser().getUser_id().equals(id)) {
                        loadStarImage(starValue);
                    } else if (broadcaster.getUser_id().equals(id)) {
                        if (!isGuest)
                            loadStarImage(starValue);
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
                    if (giftMessages.size() > 0) {
                        if (!isGiftShowing) {
                            callSwitchCase(temp);
                        }
                    }
                } else {
                    messageBean = new MessageBean(account, msg, false, false, false);
                    messageBeanList.add(messageBean);
                    adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                    rvMessages.scrollToPosition(messageBeanList.size() - 1);

                    if (msg.contains("has liked")) {
                        doLikeAnimationAudience();
                        likes = likes + 1;
                    } else if (msg.contains("has arrived")) {
                        //viewers = viewers + 1;
                        callAudiencesAPI(broadcaster.getUser_id());
                    } else if (msg.contains("Guest One Left the Broadcast")) {
                        rlGuestOneMute.setVisibility(View.GONE);
                    } else if (msg.contains("Broadcast has been Paused!")) {
                        if (!isBroadcaster) {
                            isBroadcasterMuted = true;
                            rlVideoPauseBroadcater.setVisibility(View.VISIBLE);
                        }
                    } else if (msg.contains("Broadcast has been Resumed!")) {
                        if (!isBroadcaster) {
                            isBroadcasterMuted = false;
                            rlVideoPauseBroadcater.setVisibility(View.GONE);
                        }
                    } else if (msg.contains("Broadcast has ended")) {
                        if (!isBroadcaster) {
                            isBroadcastEnded = true;
                            callRemoveImageAPI();
                        }
                    } else if (msg.contains("has requested to join the broadcast")) {
                        if (isBroadcaster) {
                            String id = msg.substring(0, 6);
                            for (int i = 0; i < mAudiences.size(); i++) {
                                if (id.equalsIgnoreCase(mAudiences.get(i).getUser_id())) {
                                    mRequests.add(mAudiences.get(i));
                                }
                            }
                            if (mAudiences.size() > 0) {
                                rl_notify.setVisibility(View.VISIBLE);
                                tv_count_req.setText(String.valueOf(mRequests.size()));
                            } else {
                                rl_notify.setVisibility(View.GONE);
                                tv_count_req.setText("0");
                            }
                            callAdapterRequests(mRequests);
                        }
                    } else if (msg.contains("has left the room")) {
                        if (isBroadcaster) {
                            String id = msg.substring(0, 6);

                            if (mRequests.size() > 0) {
                                for (int i = 0; i < mAudiences.size(); i++) {
                                    if (id.equalsIgnoreCase(mAudiences.get(i).getUser_id())) {
                                        mRequests.remove(mAudiences.get(i));
                                        adapterRequests.notifyDataSetChanged();
                                    }
                                }
                            }
                            int size = mRequests.size();
                            if (size > 0) {
                                tv_count_req.setText(String.valueOf(size));
                                rl_notify.setVisibility(View.VISIBLE);
                                tvNoRequests.setVisibility(View.GONE);
                            } else {
                                rl_notify.setVisibility(View.GONE);
                                tvNoRequests.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onMessageClicked(String name, String id) {
        Log.e(TAG, "onMessageClicked: " + id);
        if (!id.equals(SessionUser.getUser().getUser_id())) {
            if (!isClickedProfile) {
                isClickedProfile = true;
                getClickedProfileData(id);
            }
        } else if (!id.equals(SessionUser.getUser().getUser_id())) {
            getClickedProfileData(id);
        } else {
            Log.e(TAG, "onMessageClicked: " + "user not click user");
        }
    }

    private void getClickedProfileData(String id) {
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<ProfileResponse> call = apiClient.getProfile(SessionUser.getUser().getUser_id(), id);
            Log.e(TAG, "getClickedProfileData: " + SessionUser.getUser().getUser_id() + id);
            call.enqueue(new retrofit2.Callback<ProfileResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                    ProfileResponse profileResponse = response.body();
                    if (response.code() == 200) {
                        if (profileResponse != null) {
                            if (profileResponse.getStatus().equalsIgnoreCase("success")) {
                                setAudienceData(profileResponse.getData().getUser());
                                Log.e(TAG, "onResponse: " + profileResponse.getData().getUser());
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

    private void setAudienceData(User user) {
        Audience audience = new Audience();
        audience.setName(user.getName());
        audience.setId(user.getId());
        audience.setUser_id(user.getUser_id());
        audience.setActivation_code(user.getActivation_code());
        audience.setProfile_pic(user.getProfile_pic());
        audience.setLevel(user.getLevel());
        audience.setOver_all_gold(user.getOver_all_gold());
        audience.setText_muted(user.getText_muted());
        audience.setIsTheUserFollowing(user.getIsTheUserFollowing());

        if (!user.getUser_id().equals(SessionUser.getUser().getUser_id()) && !user.getUser_id().isEmpty()) {
            if (isBroadcaster) {
                showAlertViewProfile(audience, true);
            }
        } else if (user.getUser_id().isEmpty()) {
            showToast("User Id empty....");
        } else {
            showAlertViewProfile(audience, false);
            showToast("Audience");
        }
    }

    class LikeAnimationViewProvider implements LikeAnimationView.Provider {
        LikeAnimationViewProvider() {
        }

        public Bitmap getBitmap(Object obj) {
            return ActivityGroupCalls6.this.mAnimationItemList == null ? null : (Bitmap) ActivityGroupCalls6.this.mAnimationItemList.get((Integer) obj);
        }
    }

    @Override
    public void onBackPressed() {
        if (llChat.getVisibility() == View.VISIBLE) {
            llChat.setVisibility(View.GONE);
        } else if (llGift.getVisibility() == View.VISIBLE) {
            llGift.startAnimation(slideDown);
            llGift.setVisibility(View.GONE);
        } else if (cvRequests.getVisibility() == View.VISIBLE) {
            cvRequests.startAnimation(slideDown);
            cvRequests.setVisibility(View.GONE);
        } else {
            if (cRole == 1) {
                callBroadcasterClose();
            } else {
                callAudienceClose();
            }
        }
    }

    private void callBroadcasterClose() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    callStatusAPI();
                    callUnBlockAPI();
                    callTextmuteAPI();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Quit Broadcast ?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void callTextmuteAPI() {
        if (utils.isNetworkAvailable()) {
            if (textmutelist.size() == 0) {
            } else {
                for (int j = 0; j < textmutelist.size(); j++) {
                    if (utils.isNetworkAvailable()) {
                        final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
                        Call<GenericResponse> call = apiClient.textMute(textmutelist.get(j), SessionUser.getUser().getUser_id(), "no");
                        call.enqueue(new retrofit2.Callback<GenericResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                                GenericResponse genericResponse = response.body();
                                if (response.code() == 200) {
                                    if (genericResponse != null) {
                                        if (genericResponse.getStatus().equalsIgnoreCase("success")) {

                                        } else {
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
            }
        }
    }

    private void callAudienceClose() {
        isClose = true;
        if (isGuest) {
            isGuest = false;
            clicked = true;
            doSwitchToBroadcaster(false);
            removeGuest();
        } else {
            if (!isBroadcaster) {
                if (isRequested)
                    sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " has left the room");
            }
            callRemoveImageAPI();
            if (isClose) {
                Constants_app.cleanMessageListBeanList();
                finish();
            }
        }
    }

    private void callStatusAPI() {

        if (utils.isNetworkAvailable()) {
            endTime = System.currentTimeMillis();
            long mills = endTime - startTime;
            int hours = (int) (mills / (1000 * 60 * 60));
            int mins = (int) (mills / (1000 * 60)) % 60;
            int seconds = (int) (mills / 1000) % 60;

            if (hours > 1 && mins > 1)
                time = hours + " hr : " + mins + " min ";
            else if (hours == 1 && mins > 1)
                time = hours + " hr : " + mins + " min ";
            else
                time = hours + " hr : " + mins + " min ";

            String broadcastingTime = hours + " hr :" + mins + " min " + seconds;

            int idelHours = (int) (totalIdelTime / (1000 * 60 * 60));
            int idelMins = (int) (totalIdelTime / (1000 * 60)) % 60;

            if (idelHours > 1 && idelMins > 1)
                idelTime = idelHours + " hr : " + idelMins + " min ";
            else if (idelHours == 1 && idelMins > 1)
                idelTime = idelHours + " hr : " + idelMins + " min ";
            else
                idelTime = idelHours + " hr : " + idelMins + " min ";

            long totalBroadTime = mills - totalIdelTime;

            int broadHours = (int) (totalBroadTime / (1000 * 60 * 60));
            int broadMins = (int) (totalBroadTime / (1000 * 60)) % 60;

            if (broadHours > 1 && broadMins > 1)
                broadTime = broadHours + " hr : " + broadMins + " min ";
            else if (broadHours == 1 && broadMins > 1)
                broadTime = broadHours + " hr : " + broadMins + " min ";
            else
                broadTime = broadHours + " hr : " + broadMins + " min ";

            int diff = gold - oldGold;
            /*int currentGold = Integer.valueOf(SessionUser.getUser().getCurrent_gold_value()) + diff;*/

            long broadcastSeconds = TimeUnit.MILLISECONDS.toSeconds(mills);
            long totalBroadcastSeconds = TimeUnit.MILLISECONDS.toSeconds(totalBroadTime);
            long idelSeconds = TimeUnit.MILLISECONDS.toSeconds(totalIdelTime);

            utils.showProgress();

            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<GenericResponse> call = apiClient.goLive(SessionUser.getUser().getUser_id(), "INACTIVE", "groupof6",
                    String.valueOf(broadcastSeconds), String.valueOf(idelSeconds), String.valueOf(totalBroadcastSeconds), "", "");
            call.enqueue(new retrofit2.Callback<GenericResponse>() {
                @Override
                public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                    utils.hideProgress();
                    GenericResponse genericResponse = response.body();
                    if (response.code() == 200) {
                        if (genericResponse != null) {
                            if (genericResponse.getStatus().equalsIgnoreCase("success")) {
                                //onStatusSuccess();
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

            getLevel();
            isBroadcastEnded = true;

            MessageBean message = new MessageBean(selfName, SessionUser.getUser().getUser_id() + level + " Broadcast has ended", true, false, false);
            messageBeanList.add(message);
            adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
            rvMessages.scrollToPosition(messageBeanList.size() - 1);
            sendChannelMessage(SessionUser.getUser().getUser_id() + level + " Broadcast has ended");

            Constants_app.cleanMessageListBeanList();

            Intent intent = new Intent(this, ActivityStreamDetails.class);
            intent.putExtra("gold", String.valueOf(gold - oldGold));
            intent.putExtra("viewers", String.valueOf(viewers));
            intent.putExtra("likes", String.valueOf(likes));
            intent.putExtra("time", String.valueOf(time));
            intent.putExtra("idelTime", String.valueOf(idelTime));
            intent.putExtra("broadTime", String.valueOf(broadTime));
            intent.putExtra("from", "videoCall");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            finish();
            startActivity(intent);
        }
    }

    private void callUnBlockAPI() {
        if (utils.isNetworkAvailable()) {
            if (blockedlist.size() == 0) {

            } else {
                for (int j = 0; j < blockedlist.size(); j++) {
                    final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
                    Call<GenericResponse> call = apiClient.block("unblock", blockedlist.get(j), SessionUser.getUser().getUser_id());
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
        }
    }

    public void onGiftSuccess(String userId, String guestGoldValue, String currentGoldValue, String moonLevel, String moonValue, String giftname) {

        int sentGift = Integer.parseInt(mGift.getPrice());
        dvalue = dvalue - sentGift;
        SessionUser.getUser().setDiamond(String.valueOf(dvalue));
        tvDiamondValue.setText(String.valueOf(dvalue));

        switch (giftSendTo) {
            case "guestOne":
                tvGuestOneGold.setText(guestGoldValue);
                break;
            case "guestTwo":
                tvGuestTwoGold.setText(guestGoldValue);
                break;
            case "guestThree":
                tvGuestThreeGold.setText(guestGoldValue);
                break;
            case "guestFour":
                tvGuestFourGold.setText(guestGoldValue);
                break;
            case "guestFive":
                tvGuestFiveGold.setText(guestGoldValue);
                break;
            case "broadcaster":
                tvBroadcastGold.setText(guestGoldValue);
                break;
        }

        if (!multiplier.isEmpty()) {
            multiplier = " X" + multiplier;
        } else
            multiplier = "";

        getLevel();

        String messageText = "";
        switch (giftSendTo) {
            case "guestOne":
                messageText = userId + level + SessionUser.getUser().getName() + " : Has sent gIfTsEnTtOyOU " + giftname + " to " + guestOne.getName() + multiplier;
                break;
            case "guestTwo":
                messageText = userId + level + SessionUser.getUser().getName() + " : Has sent gIfTsEnTtOyOU " + giftname + " to " + guestTwo.getName() + multiplier;
                break;
            case "guestThree":
                messageText = userId + level + SessionUser.getUser().getName() + " : Has sent gIfTsEnTtOyOU " + giftname + " to " + guestThree.getName() + multiplier;
                break;
            case "guestFour":
                messageText = userId + level + SessionUser.getUser().getName() + " : Has sent gIfTsEnTtOyOU " + giftname + " to " + guestFour.getName() + multiplier;
                break;
            case "guestFive":
                messageText = userId + level + SessionUser.getUser().getName() + " : Has sent gIfTsEnTtOyOU " + giftname + " to " + guestFive.getName() + multiplier;
                break;
            case "broadcaster":
                messageText = userId + level + SessionUser.getUser().getName() + " : Has sent gIfTsEnTtOyOU " + giftname + " to " + broadcaster.getName() + multiplier;
                break;
        }

        sendChannelMessage(giftSendTo + guestGoldValue);
        sendChannelMessage(messageText);
        sendChannelMessage(userId + moonValue + "StarValue");
        messageText = messageText.replace("gIfTsEnTtOyOU", "");
        MessageBean messageBean = new MessageBean(selfName, messageText, true, false, true);
        messageBeanList.add(messageBean);
        adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
        rvMessages.scrollToPosition(messageBeanList.size() - 1);

        if (!isGuest) {
            if (userId.equalsIgnoreCase(broadcaster.getUser_id()))
                loadStarImage(moonValue);
        }

        GiftMessage giftMessage = new GiftMessage();
        giftMessage.setAccount(SessionUser.getUser().getName());
        giftMessage.setMessage(messageText);
        giftMessages.add(giftMessage);

        if (giftMessages.size() > 0) {
            if (!isGiftShowing) {
                callSwitchCase(temp);
            }
        }

        messagesList.add(messageText);
        ivGift.setEnabled(true);
        mGift = null;
    }

    @Override
    public void OnClickedAudience(Audience audience) {
        if (!audience.getUser_id().equals(SessionUser.getUser().getUser_id()) && !audience.getUser_id().isEmpty()) {
            if (isBroadcaster) {
                showAlertViewProfile(audience, true);
            } else if (!audience.getUser_id().equals(SessionUser.getUser().getUser_id())) {
                showAlertViewProfile(audience, false);
            }
        }
    }

    public void showAlertViewProfile(Audience audience, boolean isBroadcaster) {
        if (!SessionUser.getUser().getUser_id().equals(audience.getUser_id())) {
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

            root.setOnClickListener(v -> {
                isClickedProfile = false;
                alertDialog.dismiss();
            });
            isCallRequested = false;

//            Glide.with(this)
//                    .load(audience.getDpEffects())
//                    .into(ivEffect);

            isClickedProfile = false;

            if (!isBroadcaster) {
                llCallRequest.setVisibility(View.GONE);
                llTextMute.setVisibility(View.GONE);
                tvProfile.setText("Profile");
            } else {
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
            }

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

            tvName.setText(audience.getName());
            tvBGold.setText(audience.getOver_all_gold());
            String levelValue = " Lv : " + audience.getLevel();
            tvLevel.setText(levelValue);
            ivPic.setOnClickListener(v -> {
                gotoProfile(Constants_app.decodeImage(audience.getProfile_pic()), audience.getUser_id());
            });

            if (!audience.getProfile_pic().isEmpty()) {
                Picasso.get().load(audience.getProfile_pic()).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivPic);
            } else
                Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivPic);

            llCallRequest.setOnClickListener(v -> {
                if (!isCallEnd) {
                    if (!isGuest1 || !isGuest2 || !isGuest3 || !isGuest4 || !isGuest5) {
                        isCallRequested = true;
                        boolean isExits = false;
                        for (int i = 0; i < mGuests.size(); i++) {
                            if (mGuests.get(i).getUser_id().contains(audience.getUser_id())) {
                                isExits = true;
                            }
                        }
                        if (!isExits) {
                            isCallEnd = true;
                            sendChannelMessage(audience.getUser_id() + level + SessionUser.getUser().getName() + " has requested you to join the Video Call");
                            if (!isGuest1) {
                                tvConnectingguestOne.setVisibility(View.VISIBLE);
                            } else if (!isGuest2) {
                                tvConnectingguestTwo.setVisibility(View.VISIBLE);
                            } else if (!isGuest3) {
                                tvConnectingguestThree.setVisibility(View.VISIBLE);
                            } else if (!isGuest4) {
                                tvConnectingguestFour.setVisibility(View.VISIBLE);
                            } else if (!isGuest5) {
                                tvConnectingguestFive.setVisibility(View.VISIBLE);
                            }
                        } else
                            showToast("User is already added to Broadcast !");
                    } else {
                        showToast("Can't add more than two visitor to the Broadcast!");
                    }
                }
                callEndHandler.postDelayed(() -> {
                    tvConnectingguestOne.setVisibility(View.GONE);
                    tvConnectingguestTwo.setVisibility(View.GONE);
                    tvConnectingguestThree.setVisibility(View.GONE);
                    tvConnectingguestFour.setVisibility(View.GONE);
                    tvConnectingguestFive.setVisibility(View.GONE);
                }, 10000);
            });

            llFollow.setOnClickListener(v -> {
                Log.e(TAG, "ll_Follow Clicked: " + audience.getIsTheUserFollowing());
                if (audience.getIsTheUserFollowing().equalsIgnoreCase("no")) {
                    audience.setIsTheUserFollowing("yes");
                    llFollow.setVisibility(View.VISIBLE);
                    llUnFollow.setVisibility(View.GONE);
                    alertDialog.dismiss();
                    isFollowingFrMessage = true;
                    callFollowAPI("follow", audience.getUser_id(), audience.getName());
                } else if (audience.getIsTheUserFollowing().equalsIgnoreCase("yes")) {
                    audience.setIsTheUserFollowing("no");
                    llFollow.setVisibility(View.GONE);
                    llUnFollow.setVisibility(View.VISIBLE);
                    alertDialog.dismiss();
                    callFollowAPI("unfollow", audience.getUser_id(), audience.getName());
                }
            });

            llUnFollow.setOnClickListener(v -> {
                audience.setIsTheUserFollowing("no");
                llFollow.setVisibility(View.GONE);
                llUnFollow.setVisibility(View.VISIBLE);
                alertDialog.dismiss();
                callUnFollowAPI("unfollow", audience.getUser_id());
            });

            llKickOut.setOnClickListener(v -> {
                if (tvProfile.getText().toString().equals("Profile")) {
                    gotoProfile(Constants_app.decodeImage(audience.getProfile_pic()), audience.getUser_id());
                } else {
                    alertDialog.dismiss();
                    isClickedProfile = false;
                    callBlockAPI(audience.getUser_id());
                    MessageBean message = new MessageBean(selfName, SessionUser.getUser().getUser_id() + level + audience.getName() + " has been kicked out", true, false, false);
                    messageBeanList.add(message);
                    adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                    rvMessages.scrollToPosition(messageBeanList.size() - 1);
                    sendChannelMessage(audience.getUser_id() + level + audience.getName() + " has bEEn kicKed OuT");
                    blockedlist.add(audience.getUser_id());
                }
            });

            rlReport.setOnClickListener(v -> {

                LayoutInflater layoutInflater = LayoutInflater.from(ActivityGroupCalls6.this);
                final View dialogView = layoutInflater.inflate(R.layout.pop_up_report, null);
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityGroupCalls6.this);

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

            llTopFans.setOnClickListener(v -> {
                Intent intent = new Intent(ActivityGroupCalls6.this, ActivityTopFans.class);
                intent.putExtra("activationToken", audience.getActivation_code());
                intent.putExtra("user_id", audience.getUser_id());
                startActivity(intent);
            });

            llTextMute.setOnClickListener(v -> {
                if (audience.getText_muted().equalsIgnoreCase("no")) {
                    audience.setText_muted("yes");
                    tvMuted.setVisibility(View.GONE);
                    tvUnMuted.setVisibility(View.VISIBLE);
                    sendChannelMessage(audience.getUser_id() + level + audience.getName() + " has been muted for this broadcast");
                    alertDialog.dismiss();
                    callTextMute(SessionUser.getUser().getUser_id(), audience.getUser_id(), "yes");
                } else if (audience.getText_muted().equalsIgnoreCase("yes")) {
                    audience.setText_muted("no");
                    tvUnMuted.setVisibility(View.GONE);
                    tvMuted.setVisibility(View.VISIBLE);
                    sendChannelMessage(audience.getUser_id() + level + audience.getName() + " has been unMuted for this broadcast");
                    alertDialog.dismiss();
                    callTextMute(SessionUser.getUser().getUser_id(), audience.getUser_id(), "no");
                }
            });

            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();
            alertDialog.setCancelable(true);
            alertDialog.setCanceledOnTouchOutside(true);
        }
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

    public void gotoProfile(String image, String userId) {
        Intent intent = new Intent(ActivityGroupCalls6.this, ActivityViewProfile.class);
        intent.putExtra("image", image);
        intent.putExtra("userId", userId);
        intent.putExtra("from", "liveRoom");
        startActivity(intent);
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

    public void onBroadcasterClicked(View view) {

        /*if(isBroadcaster){
            showBroadcastProfile(broadcaster);
        }
*/
        if (isGiftSelected) {
            if (!isBroadcaster) {
                giftSendTo = "broadcaster";
                if (SessionUser.getUser().getUser_id().equals(broadcaster.getUser_id())) {
                    showToast("Can't send gift to yourself");
                } else {
                    apiGiftSendToGuest(broadcaster.getUser_id());
                }
            } else {
                showToast("Can't send gift to yourself");
            }
        } else {

            if (isAudience) {
                getClickedProfileData(broadcaster.getUser_id());
                Log.e(TAG, "onBroadcasterClicked: " + broadcaster.getUser_id());
            }

            if (!isAudience)
                /*showBroadcastProfile(broadcaster);*/
                onclickBroad(broadcaster.getUser_id());
        }
    }

    public void onGuestOneClicked(View view) {
        if (isGiftSelected) {
            if (isGuest1) {
                giftSendTo = "guestOne";
                if (SessionUser.getUser().getUser_id().equals(guestOne.getUser_id())) {
                    showToast("Can't send gift to yourself");
                } else {
                    if (isGuest1) {
                        apiGiftSendToGuest(guestOne.getUser_id());
                    }
                }
            }
        } else {
            if (isGuest1) {
                showGuestProfile(guestOne, 1);
            }
        }
    }

    public void onGuestTwoClicked(View view) {
        if (isGiftSelected) {
            if (isGuest2) {
                giftSendTo = "guestTwo";
                if (SessionUser.getUser().getUser_id().equals(guestTwo.getUser_id())) {
                    showToast("Can't send gift to yourself");
                } else {
                    if (isGuest2) {
                        apiGiftSendToGuest(guestTwo.getUser_id());
                    }
                }
            }
        } else {
            if (isGuest2) {
                showGuestProfile(guestTwo, 2);
            }
        }
    }

    public void onGuestThreeClicked(View view) {
        if (isGiftSelected) {
            if (isGuest3) {
                giftSendTo = "guestThree";
                if (SessionUser.getUser().getUser_id().equals(guestThree.getUser_id())) {
                    showToast("Can't send gift to yourself");
                } else {
                    if (isGuest3) {
                        apiGiftSendToGuest(guestThree.getUser_id());
                    }
                }
            }
        } else {
            if (isGuest3) {
                showGuestProfile(guestThree, 3);
            }
        }
    }

    public void onGuestFourClicked(View view) {
        if (isGiftSelected) {
            if (isGuest4) {
                giftSendTo = "guestFour";
                if (SessionUser.getUser().getUser_id().equals(guestFour.getUser_id())) {
                    showToast("Can't send gift to yourself");
                } else {
                    if (isGuest3) {
                        apiGiftSendToGuest(guestFour.getUser_id());
                    }
                }
            }
        } else {
            if (isGuest4) {
                showGuestProfile(guestFour, 4);
            }
        }
    }

    public void onGuestFiveClicked(View view) {
        if (isGiftSelected) {
            if (isGuest5) {
                giftSendTo = "guestFive";
                if (SessionUser.getUser().getUser_id().equals(guestFive.getUser_id())) {
                    showToast("Can't send gift to yourself");
                } else {
                    if (isGuest5) {
                        apiGiftSendToGuest(guestFive.getUser_id());
                    }
                }
            }
        } else {
            if (isGuest5) {
                showGuestProfile(guestFive, 5);
            }
        }
    }

    public void onBroadcastGoldClicked(View view) {
        if (!isProfileShowing)
            isProfileShowing = true;
        dailyAndWeeklyGold = "weekly";
        guestGoldTopperDetails(broadcaster.getOver_all_gold(),
                broadcaster.getProfile_pic(), broadcaster.getUser_id(), broadcaster.getName());
    }

    public void onGuestOneGoldClicked(View view) {
        if (isGuest1) {
            if (!isProfileShowing)
                isProfileShowing = true;

            dailyAndWeeklyGold = "weekly";
            guestGoldTopperDetails(guestOne.getOver_all_gold(),
                    guestOne.getProfile_pic(), guestOne.getUser_id(), guestOne.getName());
        }
    }

    public void onGuestTwoGoldClicked(View view) {
        if (isGuest2) {
            if (!isProfileShowing)
                isProfileShowing = true;

            dailyAndWeeklyGold = "weekly";
            guestGoldTopperDetails(guestTwo.getOver_all_gold(),
                    guestTwo.getProfile_pic(), guestTwo.getUser_id(), guestTwo.getName());
        }
    }

    public void onGuestThreeGoldClicked(View view) {
        if (isGuest2) {
            if (!isProfileShowing)
                isProfileShowing = true;

            dailyAndWeeklyGold = "weekly";
            guestGoldTopperDetails(guestTwo.getOver_all_gold(),
                    guestTwo.getProfile_pic(), guestTwo.getUser_id(), guestTwo.getName());
        }
    }

    public void onGuestFourGoldClicked(View view) {
        if (isGuest2) {
            if (!isProfileShowing)
                isProfileShowing = true;

            dailyAndWeeklyGold = "weekly";
            guestGoldTopperDetails(guestTwo.getOver_all_gold(),
                    guestTwo.getProfile_pic(), guestTwo.getUser_id(), guestTwo.getName());
        }
    }

    public void onGuestFiveGoldClicked(View view) {
        if (isGuest2) {
            if (!isProfileShowing)
                isProfileShowing = true;

            dailyAndWeeklyGold = "weekly";
            guestGoldTopperDetails(guestTwo.getOver_all_gold(), guestTwo.getProfile_pic(),
                    guestTwo.getUser_id(), guestTwo.getName());
        }
    }

    public void apiGiftSendToGuest(String guestid) {
        if (utils.isNetworkAvailable()) {
            isGiftSelected = false;
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<GiftResponse> call = apiClient.sendGift(SessionUser.getUser().getUser_id(), SessionUser.getUser().getUser_id(), guestid, mGift.getName(), mGift.getPrice(), multiplier);
            call.enqueue(new retrofit2.Callback<GiftResponse>() {
                @Override
                public void onResponse(@NonNull Call<GiftResponse> call, @NonNull Response<GiftResponse> response) {
                    GiftResponse giftResponse = response.body();
                    if (response.code() == 200) {
                        if (giftResponse != null) {
                            if (giftResponse.getStatus().equalsIgnoreCase("success")) {
                                onGiftSuccess(giftResponse.getData().getUser_id(), giftResponse.getData().getOver_all_gold(), giftResponse.getData().getCurrent_gold_value(), giftResponse.getData().getMoon_level(), giftResponse.getData().getMoon_value(), giftResponse.getData().getGift_name());
                            } else {
                                ivGift.setEnabled(true);
                                showToast(giftResponse.getMessage());
                                isDiamondPurchase = true;
                                Intent intent = new Intent(mActivity, ActivityAdvancedWV.class);
                                intent.putExtra("title", "Wallet");
                                intent.putExtra("from", "liveRoom");
                                intent.putExtra("url", Constants_api.wallet + SessionUser.getUser().getUser_id());
                                startActivity(intent);
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

    public void guestGoldTopperDetails(String overAllGold, String profilePic, String userid, String name) {
        isProfileShowing = false;
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityGroupCalls6.this);
        View parentView = getLayoutInflater().inflate(R.layout.item_gold_top_list, null);
        bottomSheetDialog.setContentView(parentView);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) parentView.getParent());
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

        rv_guestTopperList = parentView.findViewById(R.id.rv_toppers);
        ImageView ivBroadcasterProfile = parentView.findViewById(R.id.iv_broadcaster_profile);
        TextView ivBroadcasterName = parentView.findViewById(R.id.broadcasterName);
        TextView ivBroadcasterGoldCount = parentView.findViewById(R.id.tv_broadcaster_gold_count);
        tvNoContributors = parentView.findViewById(R.id.tv_no_contributors);
        tvNoContributors.setVisibility(View.GONE);

        ivBroadcasterName.setText(name);
        ivBroadcasterGoldCount.setText(overAllGold);
        if (!profilePic.isEmpty()) {
            Picasso.get().load(Constants_app.decodeImage(profilePic)).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivBroadcasterProfile);
        } else {
            Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivBroadcasterProfile);
        }

        progressBar = parentView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rv_guestTopperList.setLayoutManager(layoutManager);
        rv_guestTopperList.setNestedScrollingEnabled(false);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        if (utils.isNetworkAvailable()) {
            progressBar.setVisibility(View.VISIBLE);
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<TopFansResponse> call = apiClient.getTopFans(userid);
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

    public void loadGuestDetails(ArrayList<Audience> users) {
        callAudiencesAPI1(broadcaster.getUser_id());
        if (isGuest)
            resetGuestValues();

        for (int i = 0; i < 5; i++) {
            rlVideoMutes.get(i).setVisibility(View.GONE);
        }

        for (int i = 0; i < users.size(); i++) {
            try {
                Picasso.get().load(users.get(i).getProfile_pic()).fit().transform(new BlurTransformation(mActivity)).centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivBlurList.get(i));
            } catch (Exception e) {
                Crashlytics.logException(e);
            }

            if (users.get(i).getVideo_muted().equalsIgnoreCase("yes")) {
                rlVideoMutes.get(i).setVisibility(View.VISIBLE);
            } else {
                rlVideoMutes.get(i).setVisibility(View.GONE);
            }
        }

        if (users.size() > 0) {
            if (isBroadcaster) {
                ivEndBroadcast.setVisibility(View.GONE);
                ivEndBroadcast.setVisibility(View.GONE);
                ivCameraSwap.setVisibility(View.VISIBLE);
                ivGift.setVisibility(View.VISIBLE);
                ivVideoMute.setVisibility(View.GONE);
            } else {
                ivEndBroadcast.setVisibility(View.VISIBLE);
                ivCameraSwap.setVisibility(View.VISIBLE);
                ivVideoMute.setVisibility(View.VISIBLE);
            }

            if (users.size() == 1) {

                isGuest1 = true;
                isGuest2 = false;
                isGuest3 = false;
                isGuest4 = false;
                isGuest5 = false;

                guestOne = users.get(0);
                guestTwo = null;
                guestThree = null;
                guestFour = null;
                guestFive = null;

                tvGuestOneGold.setText(guestOne.getOver_all_gold());
                tvGuestTwoGold.setText("");

                rlGuestOneGoldLayout.setVisibility(View.VISIBLE);
                rlGuestTwoGoldLayout.setVisibility(View.GONE);
                rlGuestThreeGoldLayout.setVisibility(View.GONE);
                rlGuestFourGoldLayout.setVisibility(View.GONE);
                rlGuestFiveGoldLayout.setVisibility(View.GONE);

                if (SessionUser.getUser().getUser_id().equals(guestOne.getUser_id())) {
                    isThisGuest1 = true;
                    loadStarImage(guestOne.getOver_all_gold());
                }
            } else if (users.size() == 2) {

                isGuest1 = true;
                isGuest2 = true;
                isGuest3 = false;
                isGuest4 = false;
                isGuest5 = false;

                guestOne = users.get(0);
                guestTwo = users.get(1);
                guestThree = null;
                guestFour = null;
                guestFive = null;

                tvGuestOneGold.setText(guestOne.getOver_all_gold());
                tvGuestTwoGold.setText(guestTwo.getOver_all_gold());
                tvGuestThreeGold.setText("");
                tvGuestFourGold.setText("");
                tvGuestFiveGold.setText("");

                rlGuestOneGoldLayout.setVisibility(View.VISIBLE);
                rlGuestTwoGoldLayout.setVisibility(View.VISIBLE);
                rlGuestThreeGoldLayout.setVisibility(View.GONE);
                rlGuestFourGoldLayout.setVisibility(View.GONE);
                rlGuestFiveGoldLayout.setVisibility(View.GONE);

                if (SessionUser.getUser().getUser_id().equals(guestOne.getUser_id())) {
                    isThisGuest1 = true;
                    loadStarImage(guestOne.getOver_all_gold());
                } else if (SessionUser.getUser().getUser_id().equals(guestTwo.getUser_id())) {
                    isThisGuest2 = true;
                    loadStarImage(guestTwo.getOver_all_gold());
                }
            } else if (users.size() == 3) {

                isGuest1 = true;
                isGuest2 = true;
                isGuest3 = true;
                isGuest4 = false;
                isGuest5 = false;

                guestOne = users.get(0);
                guestTwo = users.get(1);
                guestThree = users.get(2);
                guestFour = null;
                guestFive = null;

                tvGuestOneGold.setText(guestOne.getOver_all_gold());
                tvGuestTwoGold.setText(guestTwo.getOver_all_gold());
                tvGuestThreeGold.setText(guestThree.getOver_all_gold());
                tvGuestFourGold.setText("");
                tvGuestFiveGold.setText("");

                rlGuestOneGoldLayout.setVisibility(View.VISIBLE);
                rlGuestTwoGoldLayout.setVisibility(View.VISIBLE);
                rlGuestThreeGoldLayout.setVisibility(View.VISIBLE);
                rlGuestFourGoldLayout.setVisibility(View.GONE);
                rlGuestFiveGoldLayout.setVisibility(View.GONE);

                if (SessionUser.getUser().getUser_id().equals(guestOne.getUser_id())) {
                    isThisGuest1 = true;
                    loadStarImage(guestOne.getOver_all_gold());
                } else if (SessionUser.getUser().getUser_id().equals(guestTwo.getUser_id())) {
                    isThisGuest2 = true;
                    loadStarImage(guestTwo.getOver_all_gold());
                } else if (SessionUser.getUser().getUser_id().equals(guestThree.getUser_id())) {
                    isThisGuest3 = true;
                    loadStarImage(guestThree.getOver_all_gold());
                }
            } else if (users.size() == 4) {

                isGuest1 = true;
                isGuest2 = true;
                isGuest3 = true;
                isGuest4 = true;
                isGuest5 = false;

                guestOne = users.get(0);
                guestTwo = users.get(1);
                guestThree = users.get(2);
                guestFour = users.get(3);
                guestFive = null;

                tvGuestOneGold.setText(guestOne.getOver_all_gold());
                tvGuestTwoGold.setText(guestTwo.getOver_all_gold());
                tvGuestThreeGold.setText(guestThree.getOver_all_gold());
                tvGuestFourGold.setText(guestFour.getOver_all_gold());
                tvGuestFiveGold.setText("");

                rlGuestOneGoldLayout.setVisibility(View.VISIBLE);
                rlGuestTwoGoldLayout.setVisibility(View.VISIBLE);
                rlGuestThreeGoldLayout.setVisibility(View.VISIBLE);
                rlGuestFourGoldLayout.setVisibility(View.VISIBLE);
                rlGuestFiveGoldLayout.setVisibility(View.GONE);

                if (SessionUser.getUser().getUser_id().equals(guestOne.getUser_id())) {
                    isThisGuest1 = true;
                    loadStarImage(guestOne.getOver_all_gold());
                } else if (SessionUser.getUser().getUser_id().equals(guestTwo.getUser_id())) {
                    isThisGuest2 = true;
                    loadStarImage(guestTwo.getOver_all_gold());
                } else if (SessionUser.getUser().getUser_id().equals(guestThree.getUser_id())) {
                    isThisGuest3 = true;
                    loadStarImage(guestThree.getOver_all_gold());
                } else if (SessionUser.getUser().getUser_id().equals(guestFour.getUser_id())) {
                    isThisGuest4 = true;
                    loadStarImage(guestFour.getOver_all_gold());
                }
            } else if (users.size() == 5) {

                isGuest1 = true;
                isGuest2 = true;
                isGuest3 = true;
                isGuest4 = true;
                isGuest5 = true;

                guestOne = users.get(0);
                guestTwo = users.get(1);
                guestThree = users.get(2);
                guestFour = users.get(3);
                guestFive = users.get(4);

                tvGuestOneGold.setText(guestOne.getOver_all_gold());
                tvGuestTwoGold.setText(guestTwo.getOver_all_gold());
                tvGuestThreeGold.setText(guestThree.getOver_all_gold());
                tvGuestFourGold.setText(guestFour.getOver_all_gold());
                tvGuestFiveGold.setText(guestFive.getOver_all_gold());

                rlGuestOneGoldLayout.setVisibility(View.VISIBLE);
                rlGuestTwoGoldLayout.setVisibility(View.VISIBLE);
                rlGuestThreeGoldLayout.setVisibility(View.VISIBLE);
                rlGuestFourGoldLayout.setVisibility(View.VISIBLE);
                rlGuestFiveGoldLayout.setVisibility(View.VISIBLE);

                if (SessionUser.getUser().getUser_id().equals(guestOne.getUser_id())) {
                    isThisGuest1 = true;
                    loadStarImage(guestOne.getOver_all_gold());

                } else if (SessionUser.getUser().getUser_id().equals(guestTwo.getUser_id())) {
                    isThisGuest2 = true;
                    loadStarImage(guestTwo.getOver_all_gold());

                } else if (SessionUser.getUser().getUser_id().equals(guestThree.getUser_id())) {
                    isThisGuest3 = true;
                    loadStarImage(guestThree.getOver_all_gold());

                } else if (SessionUser.getUser().getUser_id().equals(guestFour.getUser_id())) {
                    isThisGuest4 = true;
                    loadStarImage(guestFour.getOver_all_gold());

                } else if (SessionUser.getUser().getUser_id().equals(guestFive.getUser_id())) {
                    isThisGuest5 = true;
                    loadStarImage(guestFive.getOver_all_gold());
                }
            }
        } else {
            ivGift.setVisibility(View.VISIBLE);
            tvGuestOneGold.setText("");
            tvGuestTwoGold.setText("");
            tvGuestThreeGold.setText("");
            tvGuestFourGold.setText("");
            tvGuestFiveGold.setText("");

            rlGuestOneGoldLayout.setVisibility(View.GONE);
            rlGuestTwoGoldLayout.setVisibility(View.GONE);
            rlGuestThreeGoldLayout.setVisibility(View.GONE);
            rlGuestFourGoldLayout.setVisibility(View.GONE);
            rlGuestFiveGoldLayout.setVisibility(View.GONE);

            rlGuestOneMute.setVisibility(View.GONE);
            rlGuestOneMute.setVisibility(View.GONE);
            rlGuestOneMute.setVisibility(View.GONE);
            rlGuestOneMute.setVisibility(View.GONE);
            rlGuestOneMute.setVisibility(View.GONE);

            isGuest1 = false;
            isGuest2 = false;
            isGuest3 = false;
            isGuest4 = false;
            isGuest5 = false;

            guestOne = null;
            guestTwo = null;
            guestThree = null;
            guestFour = null;
            guestFive = null;

            loadStarImage(broadcaster.getOver_all_gold());
        }

        if (mPosition != -1) {
            if (mRequests.size() > 0) {
                adapterRequests.notifyDataSetChanged();
                rl_notify.setVisibility(View.VISIBLE);
                tv_count_req.setText(String.valueOf(mRequests.size()));
            } else {
                rl_notify.setVisibility(View.GONE);
                tv_count_req.setText("0");
                tv_count_req.setText(String.valueOf(0));
            }
        }
    }

    private void callAudiencesAPI1(String broadcasterId) {
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<UsersResponse> call = apiClient.getAudiences("100001", broadcasterId);
            call.enqueue(new retrofit2.Callback<UsersResponse>() {
                @Override
                public void onResponse(@NonNull Call<UsersResponse> call, @NonNull Response<UsersResponse> response) {
                    UsersResponse usersResponse = response.body();
                    if (response.code() == 200) {
                        if (usersResponse != null) {
                            if (usersResponse.getStatus().equalsIgnoreCase("success")) {
                                try {
                                    setAudiences(usersResponse.getData().getAudiences(), usersResponse.getData().getEntranceEffect(), usersResponse.getData().getViewers_count(),
                                            usersResponse.getData().getFreeGiftCount(), usersResponse.getData().getOverAllGold(), usersResponse.getData().getIsTheUserFollowing());
                                } catch (Exception e) {
                                    Crashlytics.logException(e);
                                }
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

    private void resetGuestValues() {
        isThisGuest1 = false;
        isThisGuest2 = false;
        isThisGuest3 = false;
        isThisGuest4 = false;
        isThisGuest5 = false;
    }

    public void onStarClicked(View view) {
        dailyAndWeeklyGold = "daily";
        if (SessionUser.getUser().getUser_id().equals(broadcaster.getUser_id())) {
            if (isBroadcaster)
                guestGoldTopperDetails(tvBroadcastGold.getText().toString(),
                        broadcaster.getProfile_pic(), broadcaster.getUser_id(), broadcaster.getName());
            return;
        }

        if (isGuest) {
            if (isGuest1 || isGuest2 || isGuest3 || isGuest4 || isGuest5) {
                if (SessionUser.getUser().getUser_id().equals(guestOne.getUser_id())) {
                    if (isGuest1) {
                        guestGoldTopperDetails(guestOne.getOver_all_gold(),
                                guestOne.getProfile_pic(), guestOne.getUser_id(), guestOne.getName());
                    }
                } else if (SessionUser.getUser().getUser_id().equals(guestTwo.getUser_id())) {
                    if (isGuest2) {
                        guestGoldTopperDetails(guestTwo.getOver_all_gold(),
                                guestTwo.getProfile_pic(), guestTwo.getUser_id(), guestTwo.getName());
                    }
                } else if (SessionUser.getUser().getUser_id().equals(guestThree.getUser_id())) {
                    if (isGuest3) {
                        guestGoldTopperDetails(guestThree.getOver_all_gold(),
                                guestThree.getProfile_pic(), guestThree.getUser_id(), guestThree.getName());
                    }
                } else if (SessionUser.getUser().getUser_id().equals(guestFour.getUser_id())) {
                    if (isGuest4) {
                        guestGoldTopperDetails(guestFour.getOver_all_gold(),
                                guestFour.getProfile_pic(), guestFour.getUser_id(), guestFour.getName());
                    }
                } else if (SessionUser.getUser().getUser_id().equals(guestFive.getUser_id())) {
                    if (isGuest5) {
                        guestGoldTopperDetails(guestFive.getOver_all_gold(),
                                guestFive.getProfile_pic(), guestFive.getUser_id(), guestFive.getName());
                    }
                }
            } else {
                guestGoldTopperDetails(tvBroadcastGold.getText().toString(),
                        broadcaster.getProfile_pic(), broadcaster.getUser_id(), broadcaster.getName());
            }
        } else {
            guestGoldTopperDetails(tvBroadcastGold.getText().toString(),
                    broadcaster.getProfile_pic(), broadcaster.getUser_id(), broadcaster.getName());
        }
    }

    public void onVideoMuteClicked(View view) {
        try {
            callGuestVideoMute();
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    private void callGuestVideoMute() {
        if (isGuest1 || isGuest2 || isGuest3 || isGuest4 || isGuest5) {
            if (isGuest1) {
                if (SessionUser.getUser().getUser_id().equals(guestOne.getUser_id())) {
                    if (isGuestVideoMuted) {
                        callVideoMuteAPI(guestOne.getUser_id(), broadcaster.getUser_id(), "no");
                        isGuestVideoMuted = false;
                        ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.videomute));
                        worker().getRtcEngine().muteLocalVideoStream(false);
                        if (!flag)
                            worker().getRtcEngine().muteLocalAudioStream(false);
                        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " GuestOne UnMuted");
                        rlGuestOneMute.setVisibility(View.GONE);
                    } else {
                        callVideoMuteAPI(guestOne.getUser_id(), broadcaster.getUser_id(), "yes");
                        isGuestVideoMuted = true;
                        ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.video_unmute));
                        worker().getRtcEngine().muteLocalVideoStream(true);
                        if (!flag)
                            worker().getRtcEngine().muteLocalAudioStream(false);
                        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " GuestOne Muted");
                        rlGuestOneMute.setVisibility(View.VISIBLE);
                    }
                }
            }
            if (isGuest2) {
                if (SessionUser.getUser().getUser_id().equals(guestTwo.getUser_id())) {
                    if (isGuestVideoMuted) {
                        callVideoMuteAPI(guestTwo.getUser_id(), broadcaster.getUser_id(), "no");
                        isGuestVideoMuted = false;
                        if (!flag)
                            worker().getRtcEngine().muteLocalAudioStream(false);
                        ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.videomute));
                        worker().getRtcEngine().muteLocalVideoStream(false);
                        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " GuestTwo UnMuted");
                        rlGuestTwoMute.setVisibility(View.GONE);
                    } else {
                        callVideoMuteAPI(guestTwo.getUser_id(), broadcaster.getUser_id(), "yes");
                        isGuestVideoMuted = true;
                        ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.video_unmute));
                        worker().getRtcEngine().muteLocalVideoStream(true);
                        if (!flag)
                            worker().getRtcEngine().muteLocalAudioStream(false);
                        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " GuestTwo Muted");
                        rlGuestTwoMute.setVisibility(View.VISIBLE);
                    }
                }
            }
            if (isGuest3) {
                if (SessionUser.getUser().getUser_id().equals(guestThree.getUser_id())) {
                    if (isGuestVideoMuted) {
                        callVideoMuteAPI(guestThree.getUser_id(), broadcaster.getUser_id(), "no");
                        isGuestVideoMuted = false;
                        ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.videomute));
                        worker().getRtcEngine().muteLocalVideoStream(false);
                        if (!flag)
                            worker().getRtcEngine().muteLocalAudioStream(false);
                        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " GuestThree UnMuted");
                        rlGuestThreeMute.setVisibility(View.GONE);
                    } else {
                        callVideoMuteAPI(guestThree.getUser_id(), broadcaster.getUser_id(), "yes");
                        isGuestVideoMuted = true;
                        ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.video_unmute));
                        worker().getRtcEngine().muteLocalVideoStream(true);
                        if (!flag)
                            worker().getRtcEngine().muteLocalAudioStream(false);
                        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " GuestThree Muted");
                        rlGuestThreeMute.setVisibility(View.VISIBLE);
                    }
                }
            }
            if (isGuest4) {
                if (SessionUser.getUser().getUser_id().equals(guestFour.getUser_id())) {
                    if (isGuestVideoMuted) {
                        callVideoMuteAPI(guestFour.getUser_id(), broadcaster.getUser_id(), "no");
                        isGuestVideoMuted = false;
                        ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.videomute));
                        worker().getRtcEngine().muteLocalVideoStream(false);
                        if (!flag)
                            worker().getRtcEngine().muteLocalAudioStream(false);
                        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " GuestFour UnMuted");
                        rlGuestFourMute.setVisibility(View.GONE);
                    } else {
                        callVideoMuteAPI(guestFour.getUser_id(), broadcaster.getUser_id(), "yes");
                        isGuestVideoMuted = true;
                        ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.video_unmute));
                        worker().getRtcEngine().muteLocalVideoStream(true);
                        if (!flag)
                            worker().getRtcEngine().muteLocalAudioStream(false);
                        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " GuestFour Muted");
                        rlGuestFourMute.setVisibility(View.VISIBLE);
                    }
                }
            }
            if (isGuest5) {
                if (SessionUser.getUser().getUser_id().equals(guestFive.getUser_id())) {
                    if (isGuestVideoMuted) {
                        callVideoMuteAPI(guestFive.getUser_id(), broadcaster.getUser_id(), "no");
                        isGuestVideoMuted = false;
                        ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.videomute));
                        worker().getRtcEngine().muteLocalVideoStream(false);
                        if (!flag)
                            worker().getRtcEngine().muteLocalAudioStream(false);
                        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " GuestFive UnMuted");
                        rlGuestFiveMute.setVisibility(View.GONE);
                    } else {
                        callVideoMuteAPI(guestFive.getUser_id(), broadcaster.getUser_id(), "yes");
                        isGuestVideoMuted = true;
                        ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.video_unmute));
                        worker().getRtcEngine().muteLocalVideoStream(true);
                        if (!flag)
                            worker().getRtcEngine().muteLocalAudioStream(false);
                        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " GuestFive Muted");
                        rlGuestFiveMute.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }
    private void callGuestVideoMutePause() {
        if (isGuest1 || isGuest2 || isGuest3 || isGuest4 || isGuest5) {
            if (isGuest1) {
                if (SessionUser.getUser().getUser_id().equals(guestOne.getUser_id())) {
                    callVideoMuteAPI(guestOne.getUser_id(), broadcaster.getUser_id(), "yes");
                    if (!isGuestVideoMuted) {
                        ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.video_unmute));
                        worker().getRtcEngine().muteLocalVideoStream(true);
                        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " GuestOne Muted");
                        rlGuestOneMute.setVisibility(View.VISIBLE);
                    }
                    if (!flag)
                        worker().getRtcEngine().muteLocalAudioStream(true);
                    worker().getRtcEngine().muteAllRemoteAudioStreams(true);
                }
            }
            if (isGuest2) {
                if (SessionUser.getUser().getUser_id().equals(guestTwo.getUser_id())) {
                    callVideoMuteAPI(guestTwo.getUser_id(), broadcaster.getUser_id(), "yes");
                    if (!isGuestVideoMuted) {
                        ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.video_unmute));
                        worker().getRtcEngine().muteLocalVideoStream(true);
                        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " GuestTwo Muted");
                        rlGuestTwoMute.setVisibility(View.VISIBLE);
                    }
                    if (!flag)
                        worker().getRtcEngine().muteLocalAudioStream(true);
                    worker().getRtcEngine().muteAllRemoteAudioStreams(true);
                }
            }
            if (isGuest3) {
                if (SessionUser.getUser().getUser_id().equals(guestThree.getUser_id())) {
                    callVideoMuteAPI(guestThree.getUser_id(), broadcaster.getUser_id(), "yes");
                    if (!isGuestVideoMuted) {
                        ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.video_unmute));
                        worker().getRtcEngine().muteLocalVideoStream(true);
                        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " GuestThree Muted");
                        rlGuestThreeMute.setVisibility(View.VISIBLE);
                    }
                    if (!flag)
                        worker().getRtcEngine().muteLocalAudioStream(true);
                    worker().getRtcEngine().muteAllRemoteAudioStreams(true);
                }
            }
            if (isGuest4) {
                if (SessionUser.getUser().getUser_id().equals(guestFour.getUser_id())) {
                    callVideoMuteAPI(guestFour.getUser_id(), broadcaster.getUser_id(), "yes");
                    if (!isGuestVideoMuted) {
                        ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.video_unmute));
                        worker().getRtcEngine().muteLocalVideoStream(true);
                        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " GuestFour Muted");
                        rlGuestFourMute.setVisibility(View.VISIBLE);
                    }
                    if (!flag)
                        worker().getRtcEngine().muteLocalAudioStream(true);
                    worker().getRtcEngine().muteAllRemoteAudioStreams(true);
                }
            }
            if (isGuest5) {
                if (SessionUser.getUser().getUser_id().equals(guestFive.getUser_id())) {
                    callVideoMuteAPI(guestFive.getUser_id(), broadcaster.getUser_id(), "yes");
                    if (!isGuestVideoMuted) {
                        ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.video_unmute));
                        worker().getRtcEngine().muteLocalVideoStream(true);
                        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " GuestFive Muted");
                        rlGuestFiveMute.setVisibility(View.VISIBLE);
                    }
                    if (!flag)
                        worker().getRtcEngine().muteLocalAudioStream(true);
                    worker().getRtcEngine().muteAllRemoteAudioStreams(true);
                }
            }
        }
    }

    private void callGuestVideoMuteResume() {
        if (isGuest1 || isGuest2 || isGuest3 || isGuest4 || isGuest5) {
            if (isGuest1) {
                if (SessionUser.getUser().getUser_id().equals(guestOne.getUser_id())) {
                    callVideoMuteAPI(guestOne.getUser_id(), broadcaster.getUser_id(), "no");
                    if (!isGuestVideoMuted) {
                        ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.videomute));
                        worker().getRtcEngine().muteLocalVideoStream(false);
                        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " GuestOne UnMuted");
                        rlGuestOneMute.setVisibility(View.GONE);
                    }
                    if (!flag)
                        worker().getRtcEngine().muteLocalAudioStream(false);
                    worker().getRtcEngine().muteAllRemoteAudioStreams(false);
                }
            }
            if (isGuest2) {
                if (SessionUser.getUser().getUser_id().equals(guestTwo.getUser_id())) {
                    callVideoMuteAPI(guestTwo.getUser_id(), broadcaster.getUser_id(), "no");
                    if (!isGuestVideoMuted) {
                        ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.videomute));
                        worker().getRtcEngine().muteLocalVideoStream(false);
                        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " GuestTwo UnMuted");
                        rlGuestTwoMute.setVisibility(View.GONE);
                    }
                    if (!flag)
                        worker().getRtcEngine().muteLocalAudioStream(false);
                    worker().getRtcEngine().muteAllRemoteAudioStreams(false);
                }
            }
            if (isGuest3) {
                if (SessionUser.getUser().getUser_id().equals(guestThree.getUser_id())) {
                    callVideoMuteAPI(guestThree.getUser_id(), broadcaster.getUser_id(), "no");
                    if (!isGuestVideoMuted) {
                        ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.videomute));
                        worker().getRtcEngine().muteLocalVideoStream(false);
                        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " GuestThree UnMuted");
                        rlGuestThreeMute.setVisibility(View.GONE);
                    }
                    if (!flag)
                        worker().getRtcEngine().muteLocalAudioStream(false);
                    worker().getRtcEngine().muteAllRemoteAudioStreams(false);
                }
            }
            if (isGuest4) {
                if (SessionUser.getUser().getUser_id().equals(guestFour.getUser_id())) {
                    callVideoMuteAPI(guestFour.getUser_id(), broadcaster.getUser_id(), "no");
                    if (!isGuestVideoMuted) {
                        ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.videomute));
                        worker().getRtcEngine().muteLocalVideoStream(false);
                        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " GuestFour UnMuted");
                        rlGuestFourMute.setVisibility(View.GONE);
                    }
                    if (!flag)
                        worker().getRtcEngine().muteLocalAudioStream(false);
                    worker().getRtcEngine().muteAllRemoteAudioStreams(false);
                }
            }
            if (isGuest5) {
                if (SessionUser.getUser().getUser_id().equals(guestFive.getUser_id())) {
                    callVideoMuteAPI(guestFive.getUser_id(), broadcaster.getUser_id(), "no");
                    if (!isGuestVideoMuted) {
                        ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.videomute));
                        worker().getRtcEngine().muteLocalVideoStream(false);
                        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " GuestFive UnMuted");
                        rlGuestFiveMute.setVisibility(View.GONE);
                    }
                    if (!flag)
                        worker().getRtcEngine().muteLocalAudioStream(false);
                    worker().getRtcEngine().muteAllRemoteAudioStreams(false);
                }
            }
        }
    }

    private void callVideoMuteAPI(String guestId, String broadcasterId, String videoMuted) {
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<GenericResponse> call = apiClient.videoMute(guestId, broadcasterId, videoMuted);
            call.enqueue(new retrofit2.Callback<GenericResponse>() {
                @Override
                public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                    GenericResponse genericResponse = response.body();
                    if (response.code() == 200) {
                        if (genericResponse != null) {
                            if (genericResponse.getStatus().equalsIgnoreCase("success")) {

                            } else {

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

    public void onVideoEndClicked(View view) {
        isRequested = false;
        isGuest = false;
        fabMic.setBackground(getResources().getDrawable(R.mipmap.mic));
        ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.videomute));
        fabMic.setTag(false);
        doSwitchToBroadcaster(false);
        removeGuest();
    }

    private void removeGuest() {
        if (isThisGuest1)
            if (!clicked)
                removeGuestApi(SessionUser.getUser().getUser_id(), true, false, 1);
            else if (isThisGuest2)
                if (!clicked) {
                    removeGuestApi(SessionUser.getUser().getUser_id(), true, false, 2);
                } else {
                    showToast("wait a second or click once again");
                }
            else if (isThisGuest3)
                if (!clicked) {
                    removeGuestApi(SessionUser.getUser().getUser_id(), true, false, 3);
                } else {
                    showToast("wait a second or click once again");
                }
            else if (isThisGuest4)
                if (!clicked)
                    removeGuestApi(SessionUser.getUser().getUser_id(), true, false, 4);
                else showToast("wait a second or click once again");


            else if (isThisGuest5)
                if (!clicked)

                    removeGuestApi(SessionUser.getUser().getUser_id(), true, false, 5);
                else showToast("wait a second or click once again");
    }

    public void showGuestProfile(Audience guestUser, int guestCount) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityGroupCalls6.this);
        View parentView = getLayoutInflater().inflate(R.layout.guest_profile_layout, null);
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
        ivGuestProfile = parentView.findViewById(R.id.iv_guest_profile);
        tvGuestName = parentView.findViewById(R.id.tv_guest_name);
        tvGuestLevel = parentView.findViewById(R.id.tv_guest_level);
        tvGuestGold = parentView.findViewById(R.id.tv_guest_gold_received);
        TextView tvGuestGoldSent = parentView.findViewById(R.id.tv_guest_gold_sent);
        TextView tvMinTarget = parentView.findViewById(R.id.tv_minTarget);
        TextView tvMaxTarget = parentView.findViewById(R.id.tv_max_target);
        tvBliveId = parentView.findViewById(R.id.tv_blive_id);
        TextView tvFriends = parentView.findViewById(R.id.tv_friends);
        TextView tvFollowers = parentView.findViewById(R.id.tv_followers);
        TextView tvFollowings = parentView.findViewById(R.id.tv_followings);
        TextView tvShareProgress = parentView.findViewById(R.id.tv_share_progress);
        TextView tvGoldProgress = parentView.findViewById(R.id.tv_gold_progress);
        TextView tvViewersProgress = parentView.findViewById(R.id.tv_viewers_progress);
        ImageView ivEndCall = parentView.findViewById(R.id.iv_end_guest);
        ivEndCall.setVisibility(View.GONE);
        ProgressBar pbShareProgress = parentView.findViewById(R.id.pb_share);
        ProgressBar pbGoldProgress = parentView.findViewById(R.id.pb_gold);
        ProgressBar pbViewersProgress = parentView.findViewById(R.id.pb_viewers);

        if (SessionUser.getUser().getUser_id().equals(broadcaster.getUser_id())) {
            ivEndCall.setVisibility(View.VISIBLE);
        } else {
            ivEndCall.setVisibility(View.GONE);
        }

        ivEndCall.setOnClickListener(view -> {
            removeGuestApi(guestUser.getUser_id(), false, true, guestCount);
            bottomSheetDialog.dismiss();
        });

        tvGuestGoldSent.setText(guestUser.getTotal_gift_send());
        tvMaxTarget.setText(guestUser.getBroadcasting_hours());
        tvMinTarget.setText(guestUser.getBroadcasting_min_target());
        tvFriends.setText(guestUser.getFriendsCount());
        tvFollowers.setText(guestUser.getFollowersCount());
        tvFollowings.setText(guestUser.getFansCount());
        String totalShareProgress = guestUser.getShare() + "/" + guestUser.getShare_target();
        String totalGoldProgress = guestUser.getGold() + "/" + guestUser.getGold_target();
        String totalViewersProgress = channelUserCount + "/" + guestUser.getViewers_target();
        tvShareProgress.setText(totalShareProgress);
        tvGoldProgress.setText(totalGoldProgress);
        tvViewersProgress.setText(totalViewersProgress);

        try {
            int shareProgress = Integer.parseInt(guestUser.getShare_target());
            int goldProgress = Integer.parseInt(guestUser.getGold_target());
            int viewersProgress = Integer.parseInt(guestUser.getViewers_target());
            int share = Integer.parseInt(guestUser.getShare());
            int gold = Integer.parseInt(guestUser.getGold());

            Resources res = getResources();
            Drawable drawableGold = res.getDrawable(R.drawable.progress_bar_gold_back);
            Drawable drawableShare = res.getDrawable(R.drawable.progress_bar_gold_back);
            Drawable drawableViewers = res.getDrawable(R.drawable.progress_bar_viewers_back);

            int sharePercent = (share * 100 / shareProgress);
            int goldPercent = (gold * 100 / goldProgress);
            int viewerPercent = (channelUserCount * 100 / viewersProgress);

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

        tvGuestLevel.setText(guestUser.getLevel());
        tvGuestGold.setText(guestUser.getTotal_gift_receiver());
        tvGuestName.setText(guestUser.getName());
        tvBliveId.setText(guestUser.getReference_user_id());

        if (!guestUser.getProfile_pic().isEmpty())
            Picasso.get().load(guestUser.getProfile_pic()).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivGuestProfile);
        else
            Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivGuestProfile);
    }

    public void showBroadcastProfile(User broadcaster) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityGroupCalls6.this);
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
        ivGuestProfile = parentView.findViewById(R.id.iv_guest_profile);
        tvGuestName = parentView.findViewById(R.id.tv_guest_name);
        tvGuestLevel = parentView.findViewById(R.id.tv_guest_level);
        tvGuestGold = parentView.findViewById(R.id.tv_guest_gold_received);
        TextView tvGuestGoldSent = parentView.findViewById(R.id.tv_guest_gold_sent);
        TextView tvMinTarget = parentView.findViewById(R.id.tv_minTarget);
        TextView tvMaxTarget = parentView.findViewById(R.id.tv_max_target);
        tvBliveId = parentView.findViewById(R.id.tv_blive_id);
        TextView tvFriends = parentView.findViewById(R.id.tv_friends);
        TextView tvFollowers = parentView.findViewById(R.id.tv_followers);
        TextView tvFollowings = parentView.findViewById(R.id.tv_followings);
        TextView tvShareProgress = parentView.findViewById(R.id.tv_share_progress);
        TextView tvGoldProgress = parentView.findViewById(R.id.tv_gold_progress);
        TextView tvViewersProgress = parentView.findViewById(R.id.tv_viewers_progress);

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
        String totalViewersProgress = channelUserCount + "/" + broadcaster.getViewers_target();
        tvShareProgress.setText(totalShareProgress);
        tvGoldProgress.setText(totalGoldProgress);
        tvViewersProgress.setText(totalViewersProgress);

        //   try {
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
        int viewerPercent = (channelUserCount * 100 / viewersProgress);

        pbShareProgress.setProgress(sharePercent);   // Main Progress
        pbShareProgress.setMax(100); // Maximum Progress
        pbShareProgress.setProgressDrawable(drawableShare);

        pbGoldProgress.setProgress(goldPercent);   // Main Progress
        pbGoldProgress.setMax(100); // Maximum Progress
        pbGoldProgress.setProgressDrawable(drawableGold);

        pbViewersProgress.setProgress(viewerPercent);   // Main Progress
        pbViewersProgress.setMax(viewersProgress); // Maximum Progress
        pbViewersProgress.setProgressDrawable(drawableViewers);

//        } catch (Exception e) {
//            Crashlytics.logException(e);
//        }

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

    public void onClickShare(View view) {
        fabMenu.close(true);
        showSharingDialog(ActivityGroupCalls6.this);
    }

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
                    try {
                        ShareDialog shareDialog;
                        shareDialog = new ShareDialog(this);
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentUrl(Uri.parse("https://stg.sjhinfotech.com/BliveWeb/link/index.html?user_id=100001"))
                                .build();
                        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                            @Override
                            public void onSuccess(Sharer.Result result) {
                                callShareAPI("facebook");
                                sendMessageToChannel(" has shared with Facebook");
                            }

                            @Override
                            public void onCancel() {
                                Toast.makeText(activity, "Please Share Properly.!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(FacebookException error) {

                            }
                        });
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

    public void sendMessageToChannel(String msg) {
        MessageBean message = new MessageBean(selfName, SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + msg, true, false, false);
        messageBeanList.add(message);
        adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
        rvMessages.scrollToPosition(messageBeanList.size() - 1);
        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + msg);
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
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityGroupCalls6.this);
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

        rvFriendsList = dialog.findViewById(R.id.rv_friends);
        tvNoFriendsList = dialog.findViewById(R.id.tv_no_friends);
        btnSendFriends = dialog.findViewById(R.id.sendSelectedfriends);
        btnSendFriends.setVisibility(View.GONE);
        checkBoxSelectAllFriends = dialog.findViewById(R.id.cb_selectAllFriends);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        rvFriendsList.setLayoutManager(layoutManager);
        rvFriendsList.setNestedScrollingEnabled(false);
        rvFriendsList.addItemDecoration(new DividerLine(this));
        layoutManager.setOrientation(RecyclerView.VERTICAL);

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
                    callShareAPI("blive");
                    shareNotifications(friendsJsonArr);
                    bottomSheetDialog.dismiss();
                    //   dialog.dismiss();
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
        if (adapterFriends != null) {
            if (recyclerView.getAdapter().getItemCount() != 0) {
                if (!isUserListEnd) {
                    int lastVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findLastCompletelyVisibleItemPosition();
                    return lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1;
                }
            }
            return false;
        }
        return false;
    }

    private void CallAlertDialog(String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityGroupCalls6.this);
        alertDialogBuilder.setMessage(broadcaster.getName() + " has requested you to Join the Broadcast !");
        alertDialogBuilder.setPositiveButton("Accept",
                (arg0, arg1) -> {
                    String id1 = msg.substring(0, 6);
                    guestId = id1;
                    isGuest = true;
                    isGuestRequest = true;
                    tvConnectingguestOne.setVisibility(View.GONE);
                    tvConnectingguestTwo.setVisibility(View.GONE);
                    tvConnectingguestThree.setVisibility(View.GONE);
                    tvConnectingguestFour.setVisibility(View.GONE);
                    tvConnectingguestFive.setVisibility(View.GONE);
                    fabMic.setBackground(getResources().getDrawable(R.mipmap.mic));
                    ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.videomute));
                    fabMic.setTag(false);
                    callAddGuestAPI(SessionUser.getUser().getUser_id(), broadcasterId);
                });

        alertDialogBuilder.setNegativeButton("Reject",
                (DialogInterface arg0, int arg1) -> {
                    tvConnectingguestOne.setVisibility(View.GONE);
                    tvConnectingguestTwo.setVisibility(View.GONE);
                    tvConnectingguestThree.setVisibility(View.GONE);
                    tvConnectingguestFour.setVisibility(View.GONE);
                    tvConnectingguestFive.setVisibility(View.GONE);
                    sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " has rejected your video call request");
                    arg0.dismiss();
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();

        callEndHandler.postDelayed(() -> {
            if (alertDialog.isShowing()) {
                tvConnectingguestOne.setVisibility(View.GONE);
                tvConnectingguestTwo.setVisibility(View.GONE);
                tvConnectingguestThree.setVisibility(View.GONE);
                tvConnectingguestFour.setVisibility(View.GONE);
                tvConnectingguestFive.setVisibility(View.GONE);
                sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " has rejected your video call request");
                alertDialog.dismiss();
            }
        }, 10000);
    }

    public void showNotification() {
        notificationManager = NotificationManagerCompat.from(ActivityGroupCalls6.this);
        createNotificationChannel();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            mBuilder = new NotificationCompat.Builder(ActivityGroupCalls6.this, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Broadcast Still Streaming ")
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("Your broadcast has paused!"))
                    .setChannelId(CHANNEL_ID)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            notificationManager.notify(notificationId, mBuilder.build());
        } else {
            mBuilder = new NotificationCompat.Builder(ActivityGroupCalls6.this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Broadcast Still Streaming ")
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("Your broadcast has paused!"))
                    .setChannelId(CHANNEL_ID)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
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
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                boolean follow = data.getBooleanExtra("follow", false);
                if (follow)
                    ivFollow.setVisibility(View.GONE);
                else
                    ivFollow.setVisibility(View.VISIBLE);
            } else if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }

    public void clearNotification() {
        notificationManager = NotificationManagerCompat.from(ActivityGroupCalls6.this);
        notificationManager.cancelAll();
    }
    void joinrtmchannel(String channelName, String s) {
        runOnUiThread(() -> {
            mChatManager = BLiveApplication.getInstance().getChatManager();
            Log.d(TAG, "  : " + channelName);
            if (SessionUser.getRtmLoginSession()) {
                try {
                    mChatManager.leaveChannel();

                    if (mRtmChannel != null) {
                        mRtmChannel.leave(null);
                        mRtmChannel.release();
                        mRtmChannel = null;
                    }


//                    mChatManager.createChannel(channelName);
                    mRtmChannel = mRtmClient.createChannel(channelName, new RtmChannelListener() {


                        @Override
                        public void onMemberCountUpdated(int i) {

                        }

                        @Override
                        public void onAttributesUpdated(List<RtmChannelAttribute> list) {

                        }

                        @Override
                        public void onMessageReceived(RtmMessage message, RtmChannelMember fromMember) {
                            runOnUiThread(() -> onMessageReceive(message, fromMember));
                        }

                        @Override
                        public void onMemberJoined(RtmChannelMember rtmChannelMember) {
                            runOnUiThread(() -> {
                                if (isBroadcaster) {
                                    if (isBroadcasterMuted) {
                                        new Handler().postDelayed(() -> sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " Broadcast has been Paused!"), 2500);
                                    }
                                } else if (isGuest1) {
                                    if (isGuestVideoMuted)
                                        new Handler().postDelayed(() -> sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " GuestOne Muted"), 2000);
                                } else if (isGuest2) {
                                    if (isGuestVideoMuted)
                                        new Handler().postDelayed(() -> sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " GuestTwo Muted"), 2000);
                                } else if (isGuest3) {
                                    if (isGuestVideoMuted)
                                        new Handler().postDelayed(() -> sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " GuestThree Muted"), 2000);
                                } else if (isGuest4) {
                                    if (isGuestVideoMuted)
                                        new Handler().postDelayed(() -> sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " GuestFour Muted"), 2000);
                                } else if (isGuest5) {
                                    if (isGuestVideoMuted)
                                        new Handler().postDelayed(() -> sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " GuestFive Muted"), 2000);
                                }
                            });
                        }

                        @Override
                        public void onMemberLeft(RtmChannelMember rtmChannelMember) {
                            runOnUiThread(() -> {
                                if (!isBroadcastEnded)
                                    callAudiencesAPI(broadcaster.getUser_id());
                            });
                        }
                    });
                } catch (RuntimeException e) {
                    Log.d(TAG, "joinrtmchannel: " + e.toString());
                    Log.e("exceptions", "Fails to create channel. Maybe the channel ID is invalid," +
                            " or already in use. See the API Reference for more information.");
                }
                mRtmChannel.join(new ResultCallback<Void>() {
                    @Override
                    public void onSuccess(Void responseInfo) {
                        Log.d(TAG, "Successfully joins the channel!");
                    }

                    @Override
                    public void onFailure(ErrorInfo errorInfo) {
                        Log.d(TAG, "join channel failure! errorCode = " + errorInfo.getErrorCode());
                        mChatManager.doLogin(channelName);
                    }
                });
            } else {
                mChatManager.doLogin(channelName);
            }
        });
    }

    private void loadInviteListsFriendsPrivate() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityGroupCalls6.this);
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
        rvFriendsList = dialog.findViewById(R.id.rv_friends);
        tvNoFriendsList = dialog.findViewById(R.id.tv_no_friends);
        count_tv = dialog.findViewById(R.id.count_tv);
        btnSendFriends = dialog.findViewById(R.id.sendSelectedfriends);
        btnSendFriends.setVisibility(View.GONE);
        checkBoxSelectAllFriends = dialog.findViewById(R.id.cb_selectAllFriends);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        rvFriendsList.setLayoutManager(layoutManager);
        rvFriendsList.setNestedScrollingEnabled(false);
        rvFriendsList.addItemDecoration(new DividerLine(this));
        layoutManager.setOrientation(RecyclerView.VERTICAL);
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
                count_tv.setText("" + usersFriendsList.size());
            } else {
                adapterFriends = new AdapterFriendsShare(getApplicationContext(), usersFriendsList, false);
                rvFriendsList.setAdapter(adapterFriends);
                adapterFriends.notifyDataSetChanged();
                count_tv.setText("" + usersFriendsList.size());
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
                    sendMessageToChannel(" has shared with Friends");
                    FriendsPrivateCallNotifications(friendsJsonArr);
                    Log.e(TAG, "loadInviteListsFriendsPrivate: " + friendsJsonArr);
                    /*shareNotifications(friendsJsonArr);*/
                    bottomSheetDialog.dismiss();
                } else {
                    showToast("Select Friends!");
                }
            }
        });
        getFriends(1);
    }

    private void FriendsPrivateCallNotifications(JsonArray mFriendsJsonArr) {
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<GenericResponse> call = apiClient.activeNotification(SessionUser.getUser().getUser_id(), String.valueOf(mFriendsJsonArr));
            call.enqueue(new retrofit2.Callback<GenericResponse>() {
                @Override
                public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                    utils.hideProgress();
                    GenericResponse genericResponse = response.body();
                    if (response.code() == 200) {
                        if (genericResponse != null) {
                            Log.e(TAG, "onResponse: " + response.raw().request().url());
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
    private void onclickBroad(String userId){
        Log.e(TAG, "onclickBroad: " );
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<GetTarget> call = apiClient.getTarget(userId);
            Log.e(TAG, "onClickUser: " + broadcaster.getUser_id());
            call.enqueue(new retrofit2.Callback<GetTarget>() {
                @Override
                public void onResponse(@NonNull Call<GetTarget> call, @NonNull Response<GetTarget> response) {
                    GetTarget getTarget = response.body();
                    if (response.code() == 200) {
                        if (getTarget != null) {
                            if (getTarget.getStatus().equalsIgnoreCase("success")) {
                                Log.e(TAG, "onResponse: " +getTarget.getData().getBroadcastingHours() );
                                onTargetSuccess(getTarget.getData().getBroadcastingHours(),getTarget.getData().getBroadcastingMinTarget(),getTarget.getData().getFansCount(),
                                        getTarget.getData().getFollowersCount(),getTarget.getData().getFriendsCount(),getTarget.getData().getGold(),getTarget.getData().getGoldTarget()
                                        ,getTarget.getData().getShare(),getTarget.getData().getShareTarget(),getTarget.getData().getTotalGiftReceiver(),getTarget.getData().getTotalGiftSend(),
                                        getTarget.getData().getViewersTarget());
                            } else {
                            }
                        } else {
                            showToast(getString(R.string.server_error));
                        }
                    } else {
                        checkResponseCode(response.code());
                    }
                }
                @Override
                public void onFailure(@NonNull Call<GetTarget> call, @NonNull Throwable t) {
                    showToast(t.getMessage());
                }
            });
        }
    }
    public void onTargetSuccess(String broadcastingHours, String broadcastingMinTarget,
                                String fansCount, String followersCount, String friendsCount,
                                String gold, String goldTarget, String share, String shareTarget,
                                String totalGiftReceiver, String totalGiftSend, String viewersTarget) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityGroupCalls6.this);
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
        tvGuestGoldSent.setText(totalGiftSend);
        tvMaxTarget.setText(broadcastingHours);
        tvMinTarget.setText(broadcastingMinTarget);
        tvFriends.setText(friendsCount);
        tvFollowers.setText(followersCount);
        tvFollowings.setText(fansCount);
        String totalShareProgress = share + "/" + shareTarget;
        String totalGoldProgress = gold + "/" + goldTarget;
        String totalViewersProgress = viewers + "/" + viewersTarget;
        tvShareProgress.setText(totalShareProgress);
        tvGoldProgress.setText(totalGoldProgress);
        tvViewersProgress.setText(totalViewersProgress);
        Glide.with(getApplicationContext())
                .load(SessionUser.getUser().getTools_applied())
                .into(ivEffect);
        try {
            int shareProgress = Integer.parseInt(shareTarget);
            int goldProgress = Integer.parseInt(goldTarget);
            int viewersProgress = Integer.parseInt(viewersTarget);
            int share1 = Integer.parseInt(share);
            int gold1 = Integer.parseInt(gold);
            Resources res = getResources();
            Drawable drawableGold = res.getDrawable(R.drawable.progress_bar_gold_back);
            Drawable drawableShare = res.getDrawable(R.drawable.progress_bar_gold_back);
            Drawable drawableViewers = res.getDrawable(R.drawable.progress_bar_viewers_back);
            int sharePercent = (share1 * 100 / shareProgress);
            int goldPercent = (gold1 * 100 / goldProgress);
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
        tvGuestLevel.setText(level);
        tvGuestGold.setText(totalGiftReceiver);
        tvGuestName.setText(broadcaster.getName());
        tvBliveId.setText(broadcaster.getReference_user_id());
        if (!broadcaster.getProfile_pic().isEmpty()) {
            Picasso.get().load(broadcaster.getProfile_pic()).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivGuestProfile);
        } else
            Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivGuestProfile);
    }
}