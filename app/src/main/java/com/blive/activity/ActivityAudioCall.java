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
import android.graphics.Bitmap;;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.PorterDuff;
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
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.blive.BLiveApplication;
import com.blive.R;
import com.blive.adapter.AdapterActiveViewers;
import com.blive.adapter.AdapterBroadcastToppers;
import com.blive.adapter.AdapterFriendsShare;
import com.blive.adapter.AdapterGiftGRP;
import com.blive.adapter.AdapterGroupTopper;
import com.blive.adapter.AdapterImages;
import com.blive.adapter.AdapterMessage;
import com.blive.adapter.AdapterRequests;
import com.blive.agora.AGEventHandler;
import com.blive.agora.AGLinearLayout;
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.agora.rtc.Constants;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmChannel;
import io.agora.rtm.RtmChannelMember;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmMessage;
import io.agora.rtm.RtmStatusCode;
import jp.wasabeef.picasso.transformations.BlurTransformation;
import retrofit2.Call;
import retrofit2.Response;

public class ActivityAudioCall extends BaseActivity implements AGEventHandler,
        AdapterImages.ListenerImage, AdapterRequests.Listener, AdapterBroadcastToppers.Listener, AdapterFriendsShare.Listener,
        AdapterGroupTopper.Listener, AdapterActiveViewers.ListenerActiveViers, AdapterMessage.ListenerMessage, AdapterGiftGRP.ListenerGift {

    @BindView(R.id.iv_user)
    ImageView ivUser;
    @BindView(R.id.iv_follow)
    ImageView ivFollow;
    @BindView(R.id.bottom_audience)
    AGLinearLayout bottomAudience;
    @BindView(R.id.bottom_broadcaster)
    AGLinearLayout bottomBroadcaster;
    @BindView(R.id.ll_chat)
    LinearLayout llChat;
    @BindView(R.id.rv_images)
    RecyclerView rvImages;
    @BindView(R.id.ll_gift)
    LinearLayout llGift;
    @BindView(R.id.ll_normal_gift)
    LinearLayout llNormalGift;
    @BindView(R.id.iv_gift_item)
    ImageView ivGiftItem;
    @BindView(R.id.tv_gift_name)
    TextView tvGiftName;
    @BindView(R.id.tv_count)
    TextView tvCount;
   /* @BindView(R.id.tv_received)
    TextView tvReceived;*/
    @BindView(R.id.rv_gift)
    RecyclerView rvGift;
    @BindView(R.id.iv_gift)
    ImageView ivGift;
    @BindView(R.id.cvRequests)
    CardView cvRequests;

    @BindView(R.id.cv_menu)
    CardView cvMenu;

    @BindView(R.id.cvCloseCall)
    CardView cvCloseCall;
    @BindView(R.id.tv_count_req)
    TextView tv_count_req;
    @BindView(R.id.rl_notify)
    RelativeLayout rl_notify;
    @BindView(R.id.tv_no_requests)
    TextView tvNoRequests;
    @BindView(R.id.tv_gift)
    TextView tvGift;
    @BindView(R.id.tv_assets)
    TextView tvAssets;
    @BindView(R.id.rv_assets)
    RecyclerView rvAssets;
    @BindView(R.id.rvSendGifts)
    RecyclerView rvSendGifts;
    @BindView(R.id.cvSendGift)
    CardView cvSendGift;
    @BindView(R.id.star_layout)
    ImageView starImage;
    @BindView(R.id.moonLevelStar)
    ImageView starRatings;
    @BindView(R.id.iv_entrance)
    ImageView ivEntrance;
    @BindView(R.id.tv_entranceName)
    TextView tvEntranceName;
    @BindView(R.id.iv_end_broadcast)
    ImageView ivEndBroadcast;
    @BindView(R.id.ivVideoRequest)
    ImageView ivVideoRequest;

    private String channelName = "", level = "", dailyAndWeeklyGold = "",selfName = "", time = "", broadcasterId = "", image = "",guestId = "",
            isFollowing = "", image_1 = "", decodeImage = "",removeGuestId = "", idelTime = "", broadTime = "",giftSendTo = "";
    private int viewers = 0, likes = 0, gold = 0, oldGold = 0, guests = 0, temp = 0,temp1 = 0, temp2 = 0,
            comboMultiplier = 0;
    private User broadcaster;
    private long startTime, endTime, onPauseStartTime, onResumeStopTime, totalIdelTime = 0;
    private int cRole = 0, mPosition = -1, page = 1, lastPage = 0,oldMoonImage = 0;
    private RecyclerView rvMessages, rvFriendsList,rvFreeGift;
    private LinearLayout llKickOut, llUnFollow, llFollow,llBulletlay;
    private List<MessageBean> messageBeanList;
    private AdapterMessage adapter;
    private Animation slideUp, slideDown;
    private ProgressBar progressBar;
    private volatile boolean mAudioMuted = false,isAudienceFollowing = false;
    private volatile int mAudioRouting = -1;
    private Gift mGift;
    private boolean isFirst = false, isLiked = false, isClose = false, isSwiped = false, isSwipedDown = false,isEntranceEffects = false,
            isArrived = false, isTextMuted = false,isBroadcastEnded = false, isRequested = false, isRefreshing = false,
            isUserListEnd, isAPICalled = false,isRedirectScreen = false, isGuest1 = false, isGuest2 = false,isGuest3 = false, isGuest4 = false,
            isGuest5 = false, isGuest6 = false, isGuest7 = false, isThisGuest1 = false,
            isThisGuest2 = false,isThisGuest3 = false, isThisGuest4 = false, isThisGuest5 = false, isThisGuest6 = false, isThisGuest7 = false,
            isBroadcastMuted = false,isCallEnd = false,isClickedProfile = false,isFollowingFrMessage = false;
    private ImageView  ivUser1, ivUser2, ivUser3,
            ivUser4, ivUser5, ivUser6, ivUser7, ivUser8, ivUser9, ivUser10, ivUser11, ivUser12,ivOffline,ivPause,buttonMsg;
    private final HashMap<Integer, SurfaceView> mUidsList = new HashMap<>();
    private boolean isBroadcaster = false, isAudience = false, isGuest = false, isGiftShowing = false,isClicked = false,isAcceptRequest = true,
            isGuestRequest = false, isGuestVideoMuted = false,isCallRequested = false,flag = false,isProfileShowing = false,
            isBroadcasterMuted = false, isCreated = true,isDiamondPurchase = false,isGuestAvailable = false;
    private LinearLayout llCombo, cvGiftMessage, ll_freeGiftlayer, ll_current_diamond;
    private int channelUserCount, mStartIndex;
    private AlertDialog alertDialog;
    final Handler callEndHandler = new Handler();
    private LikeAnimationView mLikeAnimationView;
    private ArrayList<Audience> audiences, guestAddedList;
    private TextView tvNoFriendsList, count_tv,tvFreeGifts,tvGiftsList, tvAssetsList, tvFreeGiftsList,tvCountReq,tvBulletmsg,
            tvBulletName,tvCurrentDiamondValue,tvStarLevel,tvNoContributors;
    private ArrayList<Bitmap> mAnimationItemList;
    private Spinner spCombo;
    private int position = -1, size = 0, diamondValue = 0;
    private ArrayList<Audience> mAudiences, mRequests, mGuests;
    private AdapterRequests adapterRequests;
    private AdapterGiftGRP adapterGifts;
    private RelativeLayout rlLiveAudio;
    private RecyclerView rvRequests,rv_guestTopperList;
    private RecyclerView rvEndCall;
    private Audience guestOne, guestTwo, guestThree, guestFour, guestFive, guestSix, guestSeven;
    private Audience tempGuest;
    private Audience guest, guest2;
    int dvalue = 0;
    private ImageView iv, ivRoot,ivBulletUserImg;
    private FloatingActionMenu fabMenu;
    private FloatingActionButton fbRequest;//fbMic
    private ArrayList<Gift> giftsList, giftTools, freeGifts;
    private ArrayList<User> users, usersFriendsList;
    private ArrayList<GiftMessage> giftMessages;
    private ArrayList<String> messagesList;
    private AdapterFriendsShare adapterFriends;
    private ArrayList<EntranceEffect> entranceEffects;
    final Handler handler = new Handler();
    private ArrayList<ImageView> userImageViews;
    private RtmClient mRtmClient;
    private ChatManager mChatManager;
    private RtmChannel mRtmChannel;
    private ChatHandler mChatHandler;
    private CheckBox checkBoxSelectAllFriends;
    private Button bSendGift, btnSendFriends, btnChangeAssests;
    ArrayList<String> blockedlist = new ArrayList<>();
    private String price = "", multiplier = "";

    NotificationCompat.Builder mBuilder;
    NotificationManagerCompat notificationManager;
    int notificationId = 35667;
    String CHANNEL_ID = "my_channel_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isCreated) {
            if (isGuest) {
                callGuestVideoMuteResume();
            } else if (isBroadcaster) {
                onResumeStopTime = System.currentTimeMillis();
                long  idelTime = onResumeStopTime - onPauseStartTime;
                totalIdelTime = totalIdelTime + idelTime;
                isBroadcasterMuted = false;
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
            tvCurrentDiamondValue.setText(SessionUser.getUser().getDiamond());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isGuest) {
            callGuestVideoMutePause();
        } else if (isBroadcaster) {
            onPauseStartTime = System.currentTimeMillis();
            Log.e(TAG, "onResume: "+ onPauseStartTime);
            isBroadcasterMuted = true;
            callBroadcasterMute();
        } else if (isAudience) {
            worker().getRtcEngine().muteAllRemoteAudioStreams(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
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

        mRequests = new ArrayList<>();
        mAudiences = new ArrayList<>();
        mGuests = new ArrayList<>();
        giftMessages = new ArrayList<>();
        userImageViews = new ArrayList<>();
        giftMessages = new ArrayList<>();
        entranceEffects = new ArrayList<>();
        mGuests = new ArrayList<>();
        mRequests = new ArrayList<>();
        mAudiences = new ArrayList<>();
        giftsList = new ArrayList<>();
        giftTools = new ArrayList<>();
        freeGifts = new ArrayList<>();
        usersFriendsList = new ArrayList<>();
        messagesList = new ArrayList<>();
        messageBeanList = new ArrayList<>();
        giftsList = new ArrayList<>();

        btnChangeAssests = findViewById(R.id.btn_changeAssests);
        fabMenu = findViewById(R.id.menu);
        llCombo = findViewById(R.id.ll_combo);
        tvCountReq = findViewById(R.id.tv_count_req);
        /*fbMic = findViewById(R.id.fb_mic);*/
        fbRequest = findViewById(R.id.fb_request);
        rvMessages = findViewById(R.id.message_list);
        rlLiveAudio = findViewById(R.id.rl_live_audio);
        llBulletlay = findViewById(R.id.ll_bullet);
        tvBulletmsg = findViewById(R.id.tv_bullet_msg);
        tvBulletName = findViewById(R.id.tv_bullet_msg_name);
        tvCurrentDiamondValue = findViewById(R.id.tvCurrentDiamondValue);
        tvStarLevel = findViewById(R.id.tv_star_level);
        ivBulletUserImg = findViewById(R.id.iv_bullet_user);
        rvRequests = findViewById(R.id.rvRequestsAudiocall);
        rvEndCall = findViewById(R.id.rvEndcall);
        iv = findViewById(R.id.iv);
        spCombo = findViewById(R.id.spCombo);
        ivOffline = findViewById(R.id.iv_offline);
        ivPause = findViewById(R.id.iv_pause);
        tvFreeGifts = findViewById(R.id.tv_freeGifts);
        rvFreeGift = findViewById(R.id.rv_freeGift);
        tvGiftsList = findViewById(R.id.tv_GiftList);
        tvAssetsList = findViewById(R.id.tv_Assets);
        tvFreeGiftsList = findViewById(R.id.tv_FreeGifts);
        buttonMsg = findViewById(R.id.btn_msg);
        llChat = findViewById(R.id.ll_chat);

        ivEndBroadcast.setVisibility(View.GONE);

        ImageButton imageButton = findViewById(R.id.sendButton);
        imageButton.setOnClickListener(sendButtonListener);
        getLevel();
        loadStarImage(broadcaster.getOver_all_gold());

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
                Log.e(TAG, "onRTMChannelJoinSuccess: ");
            }

            @Override
            public void onChannelJoinFailed(ErrorInfo errorCode) {
                Log.e(TAG, "onRTMChannelJoinFailed: ");
            }

            @Override
            public void onMessageReceived(RtmMessage message, RtmChannelMember fromMember) {
                Log.e(TAG, "onRTMMessageReceived: account = " + fromMember.getUserId() + " msg = " + message.getText());
                runOnUiThread(() -> onMessageReceive(message, fromMember));
            }

            @Override
            public void onMemberJoined(RtmChannelMember rtmChannelMember) {
                runOnUiThread(() -> {
                    viewers = viewers + 1;
                    if (isBroadcaster) {
                        if (isBroadcastMuted) {
                            new Handler().postDelayed(() -> sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + "Broadcast has been Paused!"), 2500);
                        }
                    } else if (isGuest1) {
                        if (isGuestVideoMuted)
                            new Handler().postDelayed(() -> sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " Guest One Muted"), 2000);
                    } else if (isGuest2) {
                        if (isGuestVideoMuted)
                            new Handler().postDelayed(() -> sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " gUesT$ T*o MuTed"), 2000);
                    }
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
        intiImageViews();

        if (image != null && !image.isEmpty()) {
            Picasso.get().load(image).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivUser);
            Picasso.get().load(image).fit().transform(new BlurTransformation(mActivity)).centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).
                    placeholder(R.drawable.user).into(ivOffline);
            Picasso.get().load(image).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivUser1);
        } else {
            Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivUser);
            Picasso.get().load(R.drawable.user).fit().transform(new BlurTransformation(mActivity)).centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).
                    placeholder(R.drawable.user).into(ivOffline);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvMessages.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        linearLayoutManager1.setOrientation(OrientationHelper.VERTICAL);
        rvRequests.setLayoutManager(linearLayoutManager1);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        linearLayoutManager2.setOrientation(OrientationHelper.VERTICAL);
        rvEndCall.setLayoutManager(linearLayoutManager2);

        GridLayoutManager layoutManager11 = new GridLayoutManager(getApplicationContext(), 2, GridLayoutManager.HORIZONTAL, false);
        rvAssets.setLayoutManager(layoutManager11);
        rvAssets.setVisibility(View.VISIBLE);
        rvAssets.setHasFixedSize(true);
        GridLayoutManager layoutManager2 = new GridLayoutManager(getApplicationContext(), 2, GridLayoutManager.HORIZONTAL, false);
        rvFreeGift.setLayoutManager(layoutManager2);
        rvFreeGift.setVisibility(View.VISIBLE);
        rvFreeGift.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvGift.setLayoutManager(linearLayoutManager);

        doConfigEngine(cRole);

        AdapterGiftGRP adapterGifts = new AdapterGiftGRP(this, giftsList);
        adapterGifts.setOnClickListener(this);
        rvGift.setAdapter(adapterGifts);

        adapter = new AdapterMessage(this, messageBeanList);
        adapter.setOnClickListener(this);
        rvMessages.setAdapter(adapter);

        MessageBean messageBean = new MessageBean(selfName, getResources().getString(R.string.warning), true, true, false);
        messageBeanList.add(messageBean);
        adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
        rvMessages.scrollToPosition(messageBeanList.size() - 1);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        rvImages.setLayoutManager(layoutManager1);

        buttonMsg.setOnClickListener(v -> {
            llChat.setVisibility(View.VISIBLE);
            EditText userTypedMessage = findViewById(R.id.userMessageBox);
            userTypedMessage.requestFocus();
            // Show soft keyboard for the user to enter the value.
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.showSoftInput(userTypedMessage, InputMethodManager.SHOW_IMPLICIT);
        });

        if (broadcaster != null) {
            oldGold = Integer.valueOf(broadcaster.getOver_all_gold());
            gold = Integer.valueOf(broadcaster.getOver_all_gold());
           /* tvReceived.setText(String.valueOf(gold));*/
        }

        worker().joinChannel(channelName, config().mUid);

        TextView textRoomName = findViewById(R.id.room_name);
        textRoomName.setText(channelName);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.combo_array, R.layout.item_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCombo.setAdapter(adapter);

        spCombo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        comboMultiplier = 1;
                        break;
                    case 1:
                        comboMultiplier = 25;
                        break;
                    case 2:
                        comboMultiplier = 50;
                        break;
                    case 3:
                        comboMultiplier = 100;
                        break;
                    case 4:
                        comboMultiplier = 200;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (isBroadcaster(cRole)) {
            mUidsList.put(0, null);
            isBroadcaster = true;
            worker().preview(false, null, 0);
            cvCloseCall.setVisibility(View.GONE);
            bottomBroadcaster.setVisibility(View.VISIBLE);
            bottomAudience.setVisibility(View.GONE);
            cvRequests.setVisibility(View.GONE);
            ivFollow.setVisibility(View.GONE);
            fbRequest.setVisibility(View.VISIBLE);
            broadcasterUI();
        } else {
            ivGift.setVisibility(View.VISIBLE);
            cvCloseCall.setVisibility(View.GONE);
            bottomBroadcaster.setVisibility(View.GONE);
            bottomAudience.setVisibility(View.VISIBLE);
            cvRequests.setVisibility(View.GONE);
            fbRequest.setVisibility(View.GONE);
            if (isFollowing.equalsIgnoreCase("Yes"))
                ivFollow.setVisibility(View.GONE);
            else
                ivFollow.setVisibility(View.VISIBLE);

            audienceUI();
        }

        slideUp = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);

        rlLiveAudio.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(ActivityAudioCall.this, new GestureDetector.SimpleOnGestureListener() {
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
                            return true;
                        case 2:
                            onSwipeDown();
                            return true;
                    }
                    return false;
                }
            });

            void onSwipeUp() {
               /* if (!isBroadcaster()) {
                    position = position + 1;
                    isSwiped = true;
                    isClose = false;
                    callRemoveImageAPI();
                }*/
            }

            void onSwipeDown() {
                /*if (!isBroadcaster()) {
                    position = position - 1;
                    isSwiped = true;
                    isSwipedDown = true;
                    isClose = false;
                    callRemoveImageAPI();
                }*/
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
                Call<GiftResponse> call = apiClient.getGifts("audio",SessionUser.getUser().getUser_id());
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

    @OnClick(R.id.fb_request)
    public void onClickRequests() {
        fabMenu.close(true);
        cvRequests.startAnimation(slideUp);
        cvRequests.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.iv_menu)
    public void onClickMenu() {
        cvMenu.startAnimation(slideUp);
        cvMenu.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btn_changeAssests)
    public void onClickchangeAssests() {
        Intent intent = new Intent(mActivity, ActivityWebView.class);
        intent.putExtra("title", "My Assests");
        intent.putExtra("from", "groupcall3");
        intent.putExtra("url", Constants_api.assets + SessionUser.getUser().getUser_id());
        startActivity(intent);
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

    private void intiImageViews() {

        ivUser1 = findViewById(R.id.iv_broadcaster);
        ivUser2 = findViewById(R.id.iv_guestOne);
        ivUser3 = findViewById(R.id.iv_guestTwo);
        ivUser4 = findViewById(R.id.iv_guestThree);
        ivUser5 = findViewById(R.id.iv_guestFour);
        ivUser6 = findViewById(R.id.iv_guestFive);
        ivUser7 = findViewById(R.id.iv_guestSix);
        ivUser8 = findViewById(R.id.iv_guestSeven);
        /*ivUser9 = findViewById(R.id.iv_guestEight);*/

        userImageViews.add(ivUser2);
        userImageViews.add(ivUser3);
        userImageViews.add(ivUser4);
        userImageViews.add(ivUser5);
        userImageViews.add(ivUser6);
        userImageViews.add(ivUser7);
        userImageViews.add(ivUser8);
        userImageViews.add(ivUser9);
        userImageViews.add(ivUser10);
        userImageViews.add(ivUser11);
        userImageViews.add(ivUser12);
    }

    @Override
    protected void deInitUI() {
        doLeaveChannel();
        event().removeEventHandler(this);
        mUidsList.clear();
    }

    private boolean isBroadcaster(int cRole) {
        return cRole == Constants.CLIENT_ROLE_BROADCASTER;
    }

    private void doLeaveChannel() {
        worker().leaveChannel(config().mChannel);
    }

    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {

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
            }
        });
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        if (uid == broadcaster.getId()) {
            if (!isBroadcastEnded)
                runOnUiThread(this::callAPIOffline);
        } else {
            if (isBroadcaster) {
                for (int i = 0; i < mGuests.size(); i++) {
                    if (uid == mGuests.get(i).getId()) {
                        int finalI = i;
                        runOnUiThread(() -> callRemoveGuestAPI(mGuests.get(finalI).getUser_id()));
                        runOnUiThread(() -> {
                            if (finalI == 0) {
                                sendChannelMessage(removeGuestId + level + SessionUser.getUser().getName() + " Guest One Removed");

                            } else {
                                sendChannelMessage(removeGuestId + level + SessionUser.getUser().getName() + " Guest Two Removed");

                            }
                        });
                    }
                }
            }
        }
        doRemoveRemoteUi(uid);
    }

    private void callAPIOffline() {
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
                                    Log.e(TAG, "Broadcast has ended: User Offline");
                                    sendChannelMessage(broadcaster.getUser_id() + level + SessionUser.getUser().getName() + " Broadcast has ended");
                                });
                            } else {
                                runOnUiThread(() -> {
                                    try {
                                        Log.e(TAG, "Broadcast has ended: User Offline One");
                                        sendChannelMessage(broadcaster.getUser_id() + level + SessionUser.getUser().getName() + " Broadcast has ended");
                                    } catch (Exception e) {
                                        Crashlytics.logException(e);
                                    }
                                });
                            }
                        } else {
                            runOnUiThread(() -> {
                                try {
                                    Log.e(TAG, "Broadcast has ended: User Offline Two");
                                    sendChannelMessage(broadcaster.getUser_id() + level + SessionUser.getUser().getName() + " Broadcast has ended");
                                } catch (Exception e) {
                                    Crashlytics.logException(e);
                                }
                            });
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

    private void doRenderRemoteUi(final int uid) {
        runOnUiThread(() -> {
            if (isFinishing()) {
                return;
            }
            if (mUidsList.size() < 11) {
                SurfaceView surfaceV = RtcEngine.CreateRendererView(getApplicationContext());
                surfaceV.setZOrderOnTop(true);
                surfaceV.setZOrderMediaOverlay(true);
                mUidsList.put(uid, surfaceV);
                if (config().mUid == uid) {
                    rtcEngine().setupLocalVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, uid));
                } else {
                    rtcEngine().setupRemoteVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, uid));
                }
            }
        });
    }

    private void doRemoveRemoteUi(final int uid) {
        runOnUiThread(() -> {
            if (isFinishing()) {
                return;
            }

            mUidsList.remove(uid);
            int bigBgUid = -1;
            Log.d(TAG, "doRemoveRemoteUi " + (uid & 0xFFFFFFFFL) + " " + (bigBgUid & 0xFFFFFFFFL));

        });
    }

    public void onSwitchSpeakerClicked(View view) {
        Log.i(TAG,"onVoiceMuteClicked " + view + " audio_status: " + mAudioMuted);

        RtcEngine rtcEngine = rtcEngine();
        rtcEngine.muteLocalAudioStream(mAudioMuted = !mAudioMuted);

        ImageView iv = (ImageView) view;

        if (mAudioMuted) {
            iv.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
        } else {
            iv.clearColorFilter();
        }
    }

    /*public void onSwitchSpeakerClicked(View view) {
        Log.i(TAG, "onSwitchSpeakerClicked " + view + " " + mAudioMuted + " " + mAudioRouting);
        RtcEngine rtcEngine = rtcEngine();
        rtcEngine.setEnableSpeakerphone(mAudioRouting != 3);
    }*/


    @OnClick(R.id.iv_follow)
    public void onClickFollow() {
        if (utils.isNetworkAvailable()) {
            callFollowAPI("follow", broadcaster.getUser_id(), broadcaster.getName());
        }
    }

    public void onClickShare(View view) {
        fabMenu.close(true);
        showSharingDialog(ActivityAudioCall.this);
    }

    @OnClick(R.id.iv_gift)
    public void onClickGift() {
        if (llGift.getVisibility() == View.VISIBLE) {
            if (isGuest) {
                bottomBroadcaster.setVisibility(View.VISIBLE);
            } else if (cRole == 1) {
                bottomBroadcaster.setVisibility(View.VISIBLE);
            } else {
                bottomAudience.setVisibility(View.VISIBLE);
            }
            llGift.startAnimation(slideDown);
            llGift.setVisibility(View.GONE);
        } else {
            if (isGuest) {
                bottomBroadcaster.setVisibility(View.GONE);
            } else if (cRole == 1) {
                bottomBroadcaster.setVisibility(View.GONE);
            } else {
                bottomAudience.setVisibility(View.GONE);
            }
            llGift.startAnimation(slideUp);
            llGift.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.tv_gift)
    public void onClickGiftMenu() {
        tvGift.setTextColor(getResources().getColor(R.color.colorAccent));
        tvAssets.setTextColor(getResources().getColor(R.color.white));
        tvFreeGifts.setTextColor(getResources().getColor(R.color.white));
        ll_freeGiftlayer.setVisibility(View.VISIBLE);
        ll_current_diamond.setVisibility(View.VISIBLE);
        btnChangeAssests.setVisibility(View.GONE);
        if (giftsList.size() > 0) {
            rvAssets.setVisibility(View.GONE);
            rvFreeGift.setVisibility(View.GONE);
            rvGift.setVisibility(View.VISIBLE);
            tvAssetsList.setVisibility(View.GONE);
            tvGiftsList.setVisibility(View.GONE);
            tvFreeGiftsList.setVisibility(View.GONE);
            bSendGift.setVisibility(View.VISIBLE);
        } else {
            rvAssets.setVisibility(View.GONE);
            rvFreeGift.setVisibility(View.GONE);
            rvGift.setVisibility(View.GONE);
            tvAssetsList.setVisibility(View.GONE);
            tvGiftsList.setVisibility(View.VISIBLE);
            tvFreeGiftsList.setVisibility(View.GONE);
            bSendGift.setVisibility(View.GONE);
        }
        tvCurrentDiamondValue.setText(SessionUser.getUser().getDiamond());
    }

    @OnClick(R.id.tv_assets)
    public void onClickAssetsMenu() {
        tvAssets.setTextColor(getResources().getColor(R.color.colorAccent));
        tvFreeGifts.setTextColor(getResources().getColor(R.color.white));
        tvGift.setTextColor(getResources().getColor(R.color.white));
        llCombo.setVisibility(View.GONE);
        ll_freeGiftlayer.setVisibility(View.GONE);
        ll_current_diamond.setVisibility(View.GONE);
        btnChangeAssests.setVisibility(View.VISIBLE);
        if (giftTools.size() > 0) {
            adapterGifts = new AdapterGiftGRP(this, giftTools);
            adapterGifts.setOnClickListener(this);
            rvAssets.setAdapter(adapterGifts);
            rvGift.setVisibility(View.GONE);
            rvFreeGift.setVisibility(View.GONE);
            rvAssets.setVisibility(View.GONE);
            tvAssetsList.setVisibility(View.GONE);
            tvGiftsList.setVisibility(View.GONE);
            tvFreeGiftsList.setVisibility(View.GONE);
            bSendGift.setVisibility(View.VISIBLE);
        } else {
            rvGift.setVisibility(View.GONE);
            rvFreeGift.setVisibility(View.GONE);
            rvAssets.setVisibility(View.GONE);
            tvAssetsList.setVisibility(View.VISIBLE);
            tvGiftsList.setVisibility(View.GONE);
            tvFreeGiftsList.setVisibility(View.GONE);
            bSendGift.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.tv_freeGifts)
    public void onClickFreeGift() {
        tvFreeGifts.setTextColor(getResources().getColor(R.color.colorAccent));
        tvGift.setTextColor(getResources().getColor(R.color.white));
        tvAssets.setTextColor(getResources().getColor(R.color.white));
        llCombo.setVisibility(View.GONE);
        ll_current_diamond.setVisibility(View.GONE);
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
            bSendGift.setVisibility(View.VISIBLE);
        } else {
            rvGift.setVisibility(View.GONE);
            rvAssets.setVisibility(View.GONE);
            rvFreeGift.setVisibility(View.GONE);
            tvAssetsList.setVisibility(View.GONE);
            tvGiftsList.setVisibility(View.GONE);
            tvFreeGiftsList.setVisibility(View.VISIBLE);
            bSendGift.setVisibility(View.GONE);
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

                        } else {

                        }
                        callGetGuestsAPI();
                    }
                } else if (msg.contains("Guest Two Removed")) {
                    String id = msg.substring(0, 6);
                    if (SessionUser.getUser().getUser_id().equals(id)) {
                        isGuest = false;
                        doSwitchToBroadcaster(false);
                    }
                } else if (msg.contains("Guest One Left the Broadcast")) {

                } else if (msg.contains("Guest Two Left the Broadcast")) {

                } else if (msg.contains("StarValue")) {
                    String id = msg.substring(0, 6);
                    Log.e("StarId", id + " " + SessionUser.getUser().getUser_id());
                    String starid = msg.replace(id, "");
                    String starValue = starid.replace("StarValue", "");
                    if (SessionUser.getUser().getUser_id().equals(id)) {
                        loadStarImage(starValue);
                    } else if (broadcaster.getUser_id().equals(id)) {
                        if (!isGuest)
                            loadStarImage(starValue);
                    }
                }else if (msg.contains("has requested you to join the Video Call")) {
                    if (!isBroadcaster) {
                        String id = msg.substring(0, 6);
                        if (id.equalsIgnoreCase(SessionUser.getUser().getUser_id())) {
                            int level = Integer.parseInt(SessionUser.getUser().getLevel());
                            if (level >= 10) {
                                CallAlertDialog(msg);
                            } else {
                                showToast("User is not yet reached level 10 to make call");
                            }
                        }
                    }
                } else if (msg.contains("BroadCaster Call End")) {
                    if (!isBroadcaster) {
                        if (alertDialog != null) {
                            alertDialog.dismiss();
                        }
                    }
                } else if (msg.contains("has accepted your audio call request")) {
                    isCallEnd = false;
                    String id = msg.substring(0, 6);
                    removeUserFromList(id);
                    callGetGuestsAPI();
                } else if (msg.contains("has rejected your audio call request")) {
                    if (isBroadcaster) {
                        isCallEnd = false;
                        messageBean = new MessageBean(account, msg, false, false, false);
                        messageBeanList.add(messageBean);
                        adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                        rvMessages.scrollToPosition(messageBeanList.size() - 1);
                    }
                } else if (msg.contains("Guest One Muted")) {

                } else if (msg.contains("Guest One UnMuted")) {

                } else if (msg.contains("gUesT$ T*o MuTed")) {

                } else if (msg.contains("gUesT$ T*o UnMuTed")) {

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
                } else if (msg.contains("buLLetMeSsAGe")) {
                    String id = msg.substring(0, 6);
                    String userId = msg.substring(0, 6);
                    String message = msg;
                    message = message.replace(id, "");
                    message = message.replace("buLLetMeSsAGe", "");
                    message = message.trim();
                    Log.e(TAG, "onMessageReceive: bullet" + message);
                    getUserProfile(userId, message);
                } else if (msg.contains("enTraNceEffEct")) {
                    String id = msg.substring(0, 8);
                    String userId = msg.substring(0, 6);
                    String message = msg;
                    message = message.replace(id, "");
                    message = message.replace("enTraNceEffEct", "");
                    message = message.trim();
                    String url = "";

                    for (int i = 0; i < mAudiences.size(); i++) {
                        if (mAudiences.get(i).getUser_id().equalsIgnoreCase(userId)) {
                            url = mAudiences.get(i).getEntranceEffect();
                        }
                    }
                    EntranceEffect entranceEffect = new EntranceEffect();
                    entranceEffect.setAccount(message);
                    entranceEffect.setUrl(url);
                    entranceEffect.setId(userId);
                    entranceEffects.add(entranceEffect);
                    if (entranceEffects.size() > 0) {
                        if (!isEntranceEffects) {
                            callEntranceEffect(temp1);
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
                    if (giftMessages.size() > 0) {
                        if (!isGiftShowing) {
                            callSwitchCase(temp);
                        }
                    }
                }  else if (msg.contains("currentDiamondValue")) {
                    String message = msg;
                    message = message.replace("currentDiamondValue", "");
                    tvCurrentDiamondValue.setText(message);
                }  else {
                    messageBean = new MessageBean(account, msg, false, false, false);
                    messageBeanList.add(messageBean);
                    adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                    rvMessages.scrollToPosition(messageBeanList.size() - 1);

                    if (msg.contains("has liked")) {
                        doLikeAnimationAudience();
                        likes = likes + 1;
                    } else if (msg.contains("has arrived")) {
                        callAudiencesAPI(broadcaster.getUser_id());
                    } else if (msg.contains("Broadcast has been Paused!")) {
                        if (!isBroadcaster) {

                        }
                    } else if (msg.contains("Broadcast has been Resumed!")) {
                        if (!isBroadcaster) {

                        }
                    } else if (msg.contains("Broadcast has ended")) {
                        if (!isBroadcaster) {
                            isBroadcastEnded = true;
                            callRemoveImageAPI();
                        }
                    } else if (msg.contains("has requested to join the broadcast")) {
                        if (isBroadcaster) {
                            boolean isExist = false;
                            String id = msg.substring(0, 6);
                            if (mRequests.size() > 0) {
                                for (int i = 0; i < mRequests.size(); i++) {
                                    if (id.equals(mRequests.get(i).getUser_id()))
                                        isExist = true;
                                }
                            }
                            if (!isExist) {
                                for (int i = 0; i < mAudiences.size(); i++) {
                                    if (id.equalsIgnoreCase(mAudiences.get(i).getUser_id())) {
                                        mRequests.add(mAudiences.get(i));
                                    }
                                }
                            }
                            rl_notify.setVisibility(View.VISIBLE);
                            tvCountReq.setText(String.valueOf(mRequests.size()));
                            callAdapterRequests(mRequests);
                        }
                    } else if (msg.contains("has left the room")) {
                        if (isBroadcaster) {
                            String id = msg.substring(0, 6);
                            for (int i = 0; i < mAudiences.size(); i++) {
                                if (id.equalsIgnoreCase(mAudiences.get(i).getUser_id())) {
                                    mRequests.remove(mAudiences.get(i));
                                    adapterRequests.notifyDataSetChanged();
                                }
                            }
                            int size = mRequests.size();
                            if (size > 0) {
                                tvCountReq.setText(String.valueOf(size));
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

    private void callAlertKickOut() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityAudioCall.this);
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
                doLeaveChannel();
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
        String broadcastingTime = hours + ":" + mins + ":" + seconds;

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

    public void loadStarImage(String moonValue) {
        Log.e(TAG, "loadStarImage: " + moonValue);
        String level = Constants_app.loadStarLevel(moonValue);
        tvStarLevel.setText(level);

        if (oldMoonImage != 0) {
            Drawable res = getResources().getDrawable(oldMoonImage);
            starImage.setImageDrawable(res);
        }

        String uri = Constants_app.loadBroadCasterStar(moonValue);
        Log.e(TAG, "loadStarImage: " + uri);
        int starImage = getResources().getIdentifier(uri, null, getPackageName());
        Drawable res = getResources().getDrawable(starImage);
        starRatings.setImageDrawable(res);
        oldMoonImage = starImage;
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

    private void doSwitchToBroadcaster(boolean broadcaster) {
        final int currentHostCount = mUidsList.size();
        final int uid = config().mUid;
        Log.e(TAG, "doSwitchToBroadcaster " + currentHostCount + " " + (uid & 0XFFFFFFFFL) + " " + broadcaster);

        if (broadcaster) {
            doConfigEngine(Constants.CLIENT_ROLE_BROADCASTER);
            bottomBroadcaster.setVisibility(View.VISIBLE);
            bottomAudience.setVisibility(View.GONE);
            ivGift.setVisibility(View.VISIBLE);
            /*fbMic.setVisibility(View.VISIBLE);*/
            new Handler().postDelayed(() -> {
                doRenderRemoteUi(uid);

                ImageView button2 = findViewById(R.id.btn_2);
                broadcasterUI();

            }, 1000);
        } else {
            stopInteraction(currentHostCount, uid);
            bottomBroadcaster.setVisibility(View.GONE);
            bottomAudience.setVisibility(View.VISIBLE);
        }
    }

    private void stopInteraction(final int currentHostCount, final int uid) {
        doConfigEngine(Constants.CLIENT_ROLE_AUDIENCE);
        new Handler().postDelayed(() -> {
            doRemoveRemoteUi(uid);
            ImageView button2 = findViewById(R.id.btn_2);
            audienceUI();
        }, 1000); // wait for reconfig engine
    }

  /*  private void callSwitchCase(int j) {
        isGiftShowing = true;
        for (int i = 0; i < giftsList.size(); i++) {
            if (giftMessages.get(j).getMessage().contains(giftsList.get(i).getName())) {
                if (giftsList.get(i).getType().equalsIgnoreCase("normal")) {
                    normalGift(giftMessages.get(j).getAccount(), giftsList.get(i).getThumbnail(), giftsList.get(i).getName(), Integer.valueOf(giftsList.get(i).getDuration()));
                } else {
                    Glide.with(this)
                            .load(giftsList.get(i).getGif())
                            .into(iv);
                    iv.setVisibility(View.VISIBLE);
                    //isQueued = true;
                    handler.postDelayed(() -> {
                        iv.setVisibility(View.GONE);
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
      *//*  tvReceived.setText(String.valueOf(gold));*//*
    }*/

    private void callSwitchCase(int j) {
        isGiftShowing = true;
        for (int i = 0; i < giftsList.size(); i++) {
            if (giftMessages.get(j).getMessage().contains(giftsList.get(i).getName())) {
                if (giftsList.get(i).getType().equalsIgnoreCase("normal")) {
                    /*   normalGift(giftMessages.get(j).getAccount(), giftsList.get(i).getThumbnail(), giftsList.get(i).getName(), Integer.valueOf(giftsList.get(i).getDuration()));*/
                    setGifGift(giftsList.get(i), giftMessages.get(j).getMessage());
                    handler.postDelayed(() -> {
                        iv.setVisibility(View.GONE);
                        /*ivBroadcastSmallGift.setVisibility(View.GONE);
                        ivGuestOneSmallGift.setVisibility(View.GONE);
                        ivGuestTwoSmallGift.setVisibility(View.GONE);*/
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
                       /* ivBroadcastSmallGift.setVisibility(View.GONE);
                        ivGuestOneSmallGift.setVisibility(View.GONE);
                        ivGuestTwoSmallGift.setVisibility(View.GONE);*/
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
                       /* ivGuestOneSmallGift.setVisibility(View.VISIBLE);*/
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
                                }).into(iv);
                    }
                }
                if (isGuest2) {
                    isGuestAvailable = true;
                    if (sendId.equals(guestTwo.getUser_id())) {
                       /* ivGuestTwoSmallGift.setVisibility(View.VISIBLE);*/
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
                }
                Log.e("SendGiftId", sendId + " " + broadcaster.getUser_id());
                if (sendId.equals(broadcaster.getUser_id()) || !isGuestAvailable) {
                  /*  ivBroadcastSmallGift.setVisibility(View.VISIBLE);*/
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
                            }).into(iv);
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

    private void normalGift(String sender, String image, String name, int duration) {
        Picasso.get().load(image).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivGiftItem);
        tvGiftName.setText(sender + " has sent you a " + name);
        llNormalGift.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                llNormalGift.setVisibility(View.GONE);
            }
        }, duration);
    }

    @Override
    public void onBackPressed() {
        try {
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
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        utils.deleteCache(this);
        mChatManager.leaveChannel();
        mChatManager.removeChatHandler(mChatHandler);
    }

    public void onClickClose(View view) {
        if (cRole == 1) {
            callBroadcasterClose();
        } else {
            callAudienceClose();
        }
    }

    private void callBroadcasterClose() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    isClose = true;
                    Log.e(TAG, "Broadcast has ended: callStatusAPI");
                    callStatusAPI();
                    callUnBlockAPI();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    isClose = false;
                    dialog.dismiss();
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Quit Broadcast ?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void callAudienceClose() {
        isClose = true;
        if (isGuest) {
            isGuest = false;
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
            utils.showProgress();
            endTime = System.currentTimeMillis();
            long mills = endTime - startTime;
            int hours = (int) (mills / (1000 * 60 * 60));
            int mins = (int) (mills / (1000 * 60)) % 60;
            int seconds = (int) (mills / 1000) % 60;
            String broadcastingTime = hours + ":" + mins + ":" + seconds;

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

            long broadcastSeconds = TimeUnit.MILLISECONDS.toSeconds(mills);
            long idelSeconds = TimeUnit.MILLISECONDS.toSeconds(totalIdelTime);
            long totalBroadcastSeconds = TimeUnit.MILLISECONDS.toSeconds(totalBroadTime);

            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<GenericResponse> call = apiClient.goLive(SessionUser.getUser().getUser_id(), "INACTIVE", "audio",
                    String.valueOf(broadcastSeconds), String.valueOf(idelSeconds), String.valueOf(totalBroadcastSeconds),"","");

            call.enqueue(new retrofit2.Callback<GenericResponse>() {
                @Override
                public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                    utils.hideProgress();
                    GenericResponse genericResponse = response.body();
                    if (response.code() == 200) {
                        if (genericResponse != null) {
                            if (genericResponse.getStatus().equalsIgnoreCase("success")) {
                                getLevel();
                                isBroadcastEnded = true;
                                sendChannelMessage(broadcaster.getUser_id() + level + SessionUser.getUser().getName() + " Broadcast has ended");
                                Constants_app.cleanMessageListBeanList();
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
                    utils.hideProgress();
                    showToast(t.getMessage());
                }
            });
        }
    }

    private void callUnBlockAPI() {
        if (utils.isNetworkAvailable()) {
            /* utils.showProgress();*/
            if (blockedlist.size()==0){

            }else {
                for (int j = 0; j <blockedlist.size() ; j++) {
                    final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
                    Call<GenericResponse> call = apiClient.block("unblock", blockedlist.get(j), SessionUser.getUser().getUser_id());
                    call.enqueue(new retrofit2.Callback<GenericResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                            GenericResponse genericResponse = response.body();
                            /*  utils.hideProgress();*/
                            if (response.code() == 200) {
                                if (genericResponse != null) {
                                    if (genericResponse.getStatus().equalsIgnoreCase("success")) {
//                                showToast(genericResponse.getData().getMessage());
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

    public void onStatusSuccess() {
        endTime = System.currentTimeMillis();
        long mills = endTime - startTime;
        int hours = (int) (mills / (1000 * 60 * 60));
        int mins = (int) (mills / (1000 * 60)) % 60;

        if (hours > 1 && mins > 1)
            time = hours + " hr : " + mins + " min ";
        else if (hours == 1 && mins > 1)
            time = hours + " hr : " + mins + " min ";
        else
            time = hours + " hr : " + mins + " min ";

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

        Intent intent = new Intent(this, ActivityStreamDetails.class);
        intent.putExtra("gold", String.valueOf(gold - oldGold));
        intent.putExtra("viewers", String.valueOf(viewers));
        intent.putExtra("likes", String.valueOf(likes));
        intent.putExtra("time", String.valueOf(time));
        intent.putExtra("idelTime", String.valueOf(idelTime));
        intent.putExtra("broadTime", String.valueOf(broadTime));
        intent.putExtra("from", "solo");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void callRemoveImageAPI() {
        endTime = System.currentTimeMillis();
        long mills = endTime - startTime;
        int hours = (int) (mills / (1000 * 60 * 60));
        int mins = (int) (mills / (1000 * 60)) % 60;
        int seconds = (int) (mills / 1000) % 60;
        String broadcastingTime = hours + ":" + mins + ":" + seconds;

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
                                onRemoveSuccess(usersResponse.getData().getAudiences(), usersResponse.getData().getViewers_count());
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
            removeCallBack();
            Constants_app.cleanMessageListBeanList();
            utils.hideProgress();
            finish();
            Intent intent = new Intent(ActivityAudioCall.this, ActivityBroadcastStop.class);
            intent.putExtra("name", broadcaster.getName());
            intent.putExtra("image", broadcaster.getProfile_pic());
            intent.putExtra("user_id", broadcaster.getUser_id());
            startActivity(intent);
        }
    }

    private void removeCallBack() {
        try {
            mChatManager.removeChatHandler(mChatHandler);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    private void broadcasterUI() {
        /*fbMic.setOnClickListener(view -> {
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
        });*/
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

    @Override
    public void onMessageClicked(String name, String id) {
        if (!id.equals(SessionUser.getUser().getUser_id())) {
            Intent intent = new Intent(this, ActivityViewProfile.class);
            intent.putExtra("image", "");
            intent.putExtra("userId", id);
            startActivity(intent);
        }
    }

    public void onRemoveSuccess(ArrayList<Audience> audiences, int viewers_count) {
        viewers = viewers_count;
        if (audiences.size() > 0)
            tvCount.setText(String.valueOf(viewers));

        if (!isBroadcastEnded) {
            if (position != -1 && position < users.size()) {
                doLeaveChannel();
                mUidsList.clear();
                Constants_app.cleanMessageListBeanList();
            } else {
                showToast("Not any more Live User");
            }
        }
    }

    @Override
    public void OnClicked(Gift gift) {
        mGift = gift;
        if ((Integer.valueOf(gift.getPrice()) == 0)) {
            Log.e(TAG, "OnClicked: " + "Free Gift Logic");
            spCombo.setSelection(0);
            comboMultiplier = 1;
            llCombo.setVisibility(View.VISIBLE);
        } else if (gift.getType().equals("normal")) {
            llCombo.setVisibility(View.VISIBLE);
            spCombo.setSelection(0);
            comboMultiplier = 1;
        } else if (gift.getType().equals("Gif")) {
            llCombo.setVisibility(View.GONE);
            comboMultiplier = 0;
        } else if (Integer.valueOf(gift.getPrice()) > 101) {
            comboMultiplier = 0;
            llCombo.setVisibility(View.GONE);
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
                showToast("You are blocked to send message to this communicate");
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

    @Override
    public void onClickedActiveAudience(Audience audience) {
        if (isBroadcaster) {
            showAlertViewProfile(audience, true);
        } else if (!audience.getUser_id().equals(SessionUser.getUser().getUser_id())) {
            showAlertViewProfile(audience, false);
        }
    }

    @Override
    public void OnClicked(Audience audience) {
        if (!isClicked) {
            isClicked = true;
            gotoProfile(Constants_app.decodeImage(audience.getProfile_pic()), audience.getUser_id());
        }
    }

    @OnClick(R.id.iv_user)
    public void onClickUser() {
        if (!isClicked) {
            isClicked = true;
            if (cRole != 1) {
                isRedirectScreen = true;
                Intent intent = new Intent(this, ActivityViewProfile.class);
                intent.putExtra("image", decodeImage);
                intent.putExtra("userId", broadcasterId);
                intent.putExtra("from", "liveRoom");
                startActivityForResult(intent, 1);
            } else {
                isClicked = false;
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityAudioCall.this);
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

                Log.e(TAG, "onClickUser: click " + SessionUser.getUser().getTools_applied());

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
                    Log.e("ProgressError", e.getMessage());
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
    }

    public void gotoProfile(String image, String userId) {
        isRedirectScreen = true;
        Intent intent = new Intent(ActivityAudioCall.this, ActivityViewProfile.class);
        intent.putExtra("image", image);
        intent.putExtra("userId", userId);
        intent.putExtra("from", "liveRoom");
        startActivity(intent);
    }

    @Override
    public void OnClicked(User user) {

    }

    class LikeAnimationViewProvider implements LikeAnimationView.Provider {
        LikeAnimationViewProvider() {
        }

        public Bitmap getBitmap(Object obj) {
            return ActivityAudioCall.this.mAnimationItemList == null ? null : (Bitmap) ActivityAudioCall.this.mAnimationItemList.get(((Integer) obj).intValue());
        }
    }

    public void onGiftSuccess(String userId, String guestGoldValue, String currentGoldValue, String moonLevel, String moonValue) {
        int sentGift = Integer.parseInt(mGift.getPrice());
        dvalue = dvalue - sentGift;
        SessionUser.getUser().setDiamond(String.valueOf(dvalue));
        tvCurrentDiamondValue.setText(String.valueOf(dvalue));

        switch (giftSendTo) {
            case "guestOne":
                break;
            case "guestTwo":
                break;
            case "broadcaster":
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
                messageText = userId + level + SessionUser.getUser().getName() + " : Has sent gIfTsEnTtOyOU " + mGift.getName() + " to " + guestOne.getName() + multiplier;
                break;
            case "guestTwo":
                messageText = userId + level + SessionUser.getUser().getName() + " : Has sent gIfTsEnTtOyOU " + mGift.getName() + " to " + guestTwo.getName() + multiplier;
                break;
            case "broadcaster":
                messageText = userId + level + SessionUser.getUser().getName() + " : Has sent gIfTsEnTtOyOU " + mGift.getName() + " to " + broadcaster.getName() + multiplier;
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

    public void onClickJoin(View view) {
        if (!isRequested) {
            int level = Integer.parseInt(SessionUser.getUser().getLevel());
            if (level >= 10) {
                isRequested = true;
                getLevel();
                MessageBean message = new MessageBean(selfName, SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " has requested to join the broadcast", true, false, false);
                messageBeanList.add(message);
                adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                rvMessages.scrollToPosition(messageBeanList.size() - 1);
                sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " has requested to join the broadcast");
            } else {
                showToast("Achieve Level 10 to Call");
            }
        } else
            showToast("Already requested !");
    }

    @Override
    public void onAcceptRequest(Audience user, int position) {
        if (!isGuest1 || !isGuest2 || !isGuest3 || !isGuest4 || !isGuest5 || !isGuest6 || !isGuest7 ) {
            if (isAcceptRequest) {
                isAcceptRequest = false;
                cvRequests.startAnimation(slideDown);
                cvRequests.setVisibility(View.GONE);
                callAddGuestAPI(user.getUser_id(), broadcasterId);
                mPosition = position;
                tempGuest = user;
            }
        } else {
            showToast("Can't add more than Eight visitor to the Broadcast!");
        }
    }

    @Override
    public void onRejectRequest(Audience user,int position) {
        mRequests.remove(position);
        adapterRequests.notifyDataSetChanged();
    }

    private void audienceUI() {
       /* fbMic.setVisibility(View.GONE);*/
        ivGift.setVisibility(View.VISIBLE);
    }

    private void doConfigEngine(int cRole) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int prefIndex = pref.getInt(Constants_app.PrefManager.PREF_PROPERTY_PROFILE_IDX, Constants_app.DEFAULT_PROFILE_IDX);
        if (prefIndex > Constants_app.VIDEO_PROFILES.length - 1) {
            prefIndex = Constants_app.DEFAULT_PROFILE_IDX;
        }
        int vProfile = Constants_app.VIDEO_PROFILES[prefIndex];

        worker().configEngine1(cRole, vProfile);
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

    @Override
    public void onNewIntent(Intent i) {
        startActivity(i);
        finish();
    }

    private void showAlertViewProfile(Audience audience, boolean isBroadcaster) {
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

        isClickedProfile = false;
        isCallRequested = false;

        Glide.with(this)
                .load(audience.getDpEffects())
                .into(ivEffect);

        if (isBroadcaster) {
            llView.setVisibility(View.VISIBLE);
            llView1.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
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

            try {
                if (audience.getText_muted().equalsIgnoreCase("no")) {
                    tvMuted.setVisibility(View.VISIBLE);
                    tvUnMuted.setVisibility(View.GONE);
                } else if (audience.getText_muted().equalsIgnoreCase("yes")) {
                    tvMuted.setVisibility(View.GONE);
                    tvUnMuted.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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

            llView.setVisibility(View.GONE);
            llView1.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
            rlReport.setVisibility(View.VISIBLE);
            tvProfile.setText("Profile");
        }

        tvName.setText(audience.getName());
        tvBGold.setText(audience.getOver_all_gold());
        tvLevel.setText(" Lv : " + audience.getLevel() + " ");

        root.setOnClickListener(v -> {
            isClickedProfile = false;
            alertDialog.dismiss();
        });

        llManage.setOnClickListener(v -> {
            LayoutInflater layoutInflater = LayoutInflater.from(ActivityAudioCall.this);
            final View dialogView = layoutInflater.inflate(R.layout.pop_up_manage, null);
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityAudioCall.this);

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
            LayoutInflater layoutInflater = LayoutInflater.from(ActivityAudioCall.this);
            final View dialogView = layoutInflater.inflate(R.layout.pop_up_report, null);
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityAudioCall.this);

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

        ivPic.setOnClickListener(v -> {
            ivPic.setClickable(false);
           /* if (!isClicked) {
                isClicked = true;*/
            alertDialog.cancel();
            gotoProfile(Constants_app.decodeImage(audience.getProfile_pic()), audience.getUser_id());
            ivPic.postDelayed(() -> ivPic.setClickable(true), 1000);
            /*  }*/
        });

        if (!audience.getProfile_pic().isEmpty()) {
            Picasso.get().load(audience.getProfile_pic()).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivPic);
        } else
            Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivPic);

        llCallRequest.setOnClickListener(v -> {
            if (!isCallEnd) {
                if (!isGuest1 || !isGuest2 || !isGuest3 || !isGuest4 || !isGuest5 || !isGuest6 || !isGuest7 ) {
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

                        } else if (!isGuest2) {

                        }
                    } else
                        showToast("User is already added to Broadcast !");
                } else {
                    showToast("Can't add more than Eight visitor to the Broadcast!");
                }
            }
            callEndHandler.postDelayed(() -> {
            }, 10000);
        });

        llKickOut.setOnClickListener(v -> {
            if (tvProfile.getText().toString().equals("Profile")) {
                if (!isClicked) {
                    isClicked = true;
                    gotoProfile(Constants_app.decodeImage(audience.getProfile_pic()), audience.getUser_id());
                }
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

        llTopFans.setOnClickListener(v -> {
            isRedirectScreen = true;
            Intent intent = new Intent(ActivityAudioCall.this, ActivityTopFans.class);
            intent.putExtra("activationToken", audience.getActivation_code());
            intent.putExtra("user_id", audience.getUser_id());
            startActivity(intent);
        });

        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
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
                                llKickOut.setEnabled(false);
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
            tvCountReq.setText(String.valueOf(size));
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

    public void onGuestListSuccess(ArrayList<Audience> guests) {
        mGuests.clear();
        mGuests = guests;

       /* switchToDefaultVideoView(guests);*/

        if (guests.size() == 0) {
            isGuest1 = false;
            isGuest2 = false;

            Log.e(TAG, "loadGuestDetails : Size 0 "  );

            guestOne = null;
            guestTwo = null;

        }

        if (guests.size() > 0)
            loadGuestDetails(guests);
    }

    private void CallAlertDialog(String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityAudioCall.this);
        alertDialogBuilder.setMessage(broadcaster.getName() + " has requested you to Join the Broadcast !");
        alertDialogBuilder.setPositiveButton("Accept",
                (arg0, arg1) -> {
                    String id1 = msg.substring(0, 6);
                    guestId = id1;
                    isGuest = true;
                    isGuestRequest = true;
                   /* fbMic.setBackground(getResources().getDrawable(R.mipmap.mic));
                    fbMic.setTag(false);*/
                    callAddGuestAPI(SessionUser.getUser().getUser_id(), broadcasterId);
                });

        alertDialogBuilder.setNegativeButton("Reject",
                (DialogInterface arg0, int arg1) -> {
                    sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " has rejected your audio call request");
                    arg0.dismiss();
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        callEndHandler.postDelayed(() -> {
            if (alertDialog.isShowing()) {
                sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " has rejected your audio call request");
                alertDialog.dismiss();

            }
        }, 10000);
    }

    public void showSharingDialog(Activity activity) {
        try {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setContentView(R.layout.sharing_layout);

            ImageView facebookShareBtn = dialog.findViewById(R.id.facebook_share);
            ImageView twitterShareBtn = dialog.findViewById(R.id.twitter_share);
            ImageView whatsAppShareBtn = dialog.findViewById(R.id.whatsapp_share);
            ImageView instagramShareBtn = dialog.findViewById(R.id.whatsapp_share);
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

           /* instagramShareBtn.setOnClickListener(view->{
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.putExtra(Intent.EXTRA_STREAM,uri);
                shareIntent.putExtra(Intent.EXTRA_TEXT,"YOUR TEXT TO SHARE IN INSTAGRAM");
                shareIntent.setPackage("com.instagram.android");
                return shareIntent;
            });*/

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
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityAudioCall.this);
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
                    callShareAPI("blive");
                    shareNotifications(friendsJsonArr);
                    bottomSheetDialog.dismiss();
                } else {
                    showToast("Select Friends");
                }
            }
        });

        getFriends(1);
    }

    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (adapterFriends != null) {
            if (Objects.requireNonNull(recyclerView.getAdapter()).getItemCount() != 0) {
                if (!isUserListEnd) {
                    int lastVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findLastCompletelyVisibleItemPosition();
                    return lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1;
                }
            }
            return false;
        }
        return false;
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
                                    setFriends(usersResponse.getData().getUsers(), usersResponse.getData().getLast_page());
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

    private void setFriends(ArrayList<User> mUsers, int mPage) {
        lastPage = mPage;
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

    private void shareNotifications(JsonArray mFriendsJsonArr) {
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<GenericResponse> call = apiClient.shareNotification(SessionUser.getUser().getUser_id(), String.valueOf(mFriendsJsonArr));
            call.enqueue(new retrofit2.Callback<GenericResponse>() {
                @Override
                public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                    utils.hideProgress();
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

    private void callAddGuestAPI(String guest_id, String broadcasterId) {
        Log.e(TAG, "callAddGuestAPI: " + guest_id + " = " + broadcasterId);
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
                                    sendChannelMessage(SessionUser.getUser().getUser_id() + level + " has accepted your audio call request ");
                                    loadGuestDetails(usersResponse.getData().getGuests());
                                } else {
                                    mRequests.remove(mPosition);
                                    tvCountReq.setText(String.valueOf(mRequests.size()));
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

    private void callRemoveGuestAPI(String guestId) {
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<UsersResponse> call = apiClient.removeGuest(guestId, broadcasterId);
            call.enqueue(new retrofit2.Callback<UsersResponse>() {
                @Override
                public void onResponse(@NonNull Call<UsersResponse> call, @NonNull Response<UsersResponse> response) {
                    UsersResponse usersResponse = response.body();
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
                }
            });
        }
    }

    public void onRemoveGuestSuccess(ArrayList<Audience> users) {
        mGuests.clear();
        mGuests = users;
        Log.e(TAG, "onRemoveGuestSuccess: " + users.size());
        if (isClose) {
            callRemoveImageAPI();
            Constants_app.cleanMessageListBeanList();
            finish();
        } else {
            loadGuestDetails(users);
        }
        loadGuestDetails(users);
    }

    public void loadGuestDetails(ArrayList<Audience> users) {
        Log.d(TAG, "loadGuestDetails: " + users.size());
        if (isGuest)
            resetGuestValues();

        for (int i = 0; i < 7; i++) {

        }

        for (int i = 0; i < users.size(); i++) {
            try {

            } catch (Exception e) {
                Crashlytics.logException(e);
            }

            if (users.get(i).getVideo_muted().equalsIgnoreCase("yes")) {

            } else {

            }
        }

        if (users.size() > 0) {
            if (isBroadcaster) {
                ivGift.setVisibility(View.GONE);
            } else {

            }

            if (users.size() == 1) {

                isGuest1 = true;
                isGuest2 = false;

                guest = users.get(0);
                guest2 = null;

                if (SessionUser.getUser().getUser_id().equals(guest.getUser_id())) {
                    isThisGuest1 = true;
                }
            } else if (users.size() == 2) {

                isGuest1 = true;
                isGuest2 = true;

                guest = users.get(0);
                guest2 = users.get(1);

                if (SessionUser.getUser().getUser_id().equals(guest.getUser_id())) {
                    isThisGuest1 = true;
                } else if (SessionUser.getUser().getUser_id().equals(guest2.getUser_id())) {
                    isThisGuest2 = true;
                }
            }else if (users.size() == 3) {

                isGuest1 = true;
                isGuest2 = true;
                isGuest3 = true;
                isGuest4 = false;
                isGuest5 = false;
                isGuest6 = false;
                isGuest7 = false;

                guestOne = users.get(0);
                guestTwo = users.get(1);
                guestThree = users.get(2);
                guestFour = null;
                guestFive = null;
                guestSix = null;
                guestSeven = null;

                /*tvGuestOneGold.setText(guestOne.getOver_all_gold());
                tvGuestTwoGold.setText(guestTwo.getOver_all_gold());
                tvGuestThreeGold.setText(guestThree.getOver_all_gold());

                rlGuestOneGoldLayout.setVisibility(View.VISIBLE);
                rlGuestTwoGoldLayout.setVisibility(View.VISIBLE);
                rlGuestThreeGoldLayout.setVisibility(View.VISIBLE);
                rlGuestFourGoldLayout.setVisibility(View.GONE);
                rlGuestFiveGoldLayout.setVisibility(View.GONE);
                rlGuestSixGoldLayout.setVisibility(View.GONE);
                rlGuestSevenGoldLayout.setVisibility(View.GONE);
                rlGuestEightGoldLayout.setVisibility(View.GONE);*/

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
                isGuest6 = false;
                isGuest7 = false;

                guestOne = users.get(0);
                guestTwo = users.get(1);
                guestThree = users.get(2);
                guestFour = users.get(3);
                guestFive = null;
                guestSix = null;
                guestSeven = null;

                /*tvGuestOneGold.setText(guestOne.getOver_all_gold());
                tvGuestTwoGold.setText(guestTwo.getOver_all_gold());
                tvGuestThreeGold.setText(guestThree.getOver_all_gold());
                tvGuestFourGold.setText(guestFour.getOver_all_gold());

                rlGuestOneGoldLayout.setVisibility(View.VISIBLE);
                rlGuestTwoGoldLayout.setVisibility(View.VISIBLE);
                rlGuestThreeGoldLayout.setVisibility(View.VISIBLE);
                rlGuestFourGoldLayout.setVisibility(View.VISIBLE);
                rlGuestFiveGoldLayout.setVisibility(View.GONE);
                rlGuestSixGoldLayout.setVisibility(View.GONE);
                rlGuestSevenGoldLayout.setVisibility(View.GONE);*/

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
                isGuest6 = false;
                isGuest7 = false;

                guestOne = users.get(0);
                guestTwo = users.get(1);
                guestThree = users.get(2);
                guestFour = users.get(3);
                guestFive = users.get(4);
                guestSix = null;
                guestSeven = null;

                /*tvGuestOneGold.setText(guestOne.getOver_all_gold());
                tvGuestTwoGold.setText(guestTwo.getOver_all_gold());
                tvGuestThreeGold.setText(guestThree.getOver_all_gold());
                tvGuestFourGold.setText(guestFour.getOver_all_gold());
                tvGuestFiveGold.setText(guestFive.getOver_all_gold());

                rlGuestOneGoldLayout.setVisibility(View.VISIBLE);
                rlGuestTwoGoldLayout.setVisibility(View.VISIBLE);
                rlGuestThreeGoldLayout.setVisibility(View.VISIBLE);
                rlGuestFourGoldLayout.setVisibility(View.VISIBLE);
                rlGuestFiveGoldLayout.setVisibility(View.VISIBLE);
                rlGuestSixGoldLayout.setVisibility(View.GONE);
                rlGuestSevenGoldLayout.setVisibility(View.GONE);*/

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
            } else if (users.size() == 6) {
                isGuest1 = true;
                isGuest2 = true;
                isGuest3 = true;
                isGuest4 = true;
                isGuest5 = true;
                isGuest6 = true;
                isGuest7 = false;

                guestOne = users.get(0);
                guestTwo = users.get(1);
                guestThree = users.get(2);
                guestFour = users.get(3);
                guestFive = users.get(4);
                guestSix = users.get(5);
                guestSeven = null;

                /*tvGuestOneGold.setText(guestOne.getOver_all_gold());
                tvGuestTwoGold.setText(guestTwo.getOver_all_gold());
                tvGuestThreeGold.setText(guestThree.getOver_all_gold());
                tvGuestFourGold.setText(guestFour.getOver_all_gold());
                tvGuestFiveGold.setText(guestFive.getOver_all_gold());
                tvGuestSixGold.setText(guestSix.getOver_all_gold());

                rlGuestOneGoldLayout.setVisibility(View.VISIBLE);
                rlGuestTwoGoldLayout.setVisibility(View.VISIBLE);
                rlGuestThreeGoldLayout.setVisibility(View.VISIBLE);
                rlGuestFourGoldLayout.setVisibility(View.VISIBLE);
                rlGuestFiveGoldLayout.setVisibility(View.VISIBLE);
                rlGuestSixGoldLayout.setVisibility(View.VISIBLE);
                rlGuestSevenGoldLayout.setVisibility(View.GONE);*/

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
                } else if (SessionUser.getUser().getUser_id().equals(guestSix.getUser_id())) {
                    isThisGuest6 = true;
                    loadStarImage(guestSix.getOver_all_gold());
                }
            } else if (users.size() == 7) {
                isGuest1 = true;
                isGuest2 = true;
                isGuest3 = true;
                isGuest4 = true;
                isGuest5 = true;
                isGuest6 = true;
                isGuest7 = true;

                guestOne = users.get(0);
                guestTwo = users.get(1);
                guestThree = users.get(2);
                guestFour = users.get(3);
                guestFive = users.get(4);
                guestSix = users.get(5);
                guestSeven = users.get(6);

                /*tvGuestOneGold.setText(guestOne.getOver_all_gold());
                tvGuestTwoGold.setText(guestTwo.getOver_all_gold());
                tvGuestThreeGold.setText(guestThree.getOver_all_gold());
                tvGuestFourGold.setText(guestFour.getOver_all_gold());
                tvGuestFiveGold.setText(guestFive.getOver_all_gold());
                tvGuestSixGold.setText(guestSix.getOver_all_gold());
                tvGuestSevenGold.setText(guestSeven.getOver_all_gold());

                rlGuestOneGoldLayout.setVisibility(View.VISIBLE);
                rlGuestTwoGoldLayout.setVisibility(View.VISIBLE);
                rlGuestThreeGoldLayout.setVisibility(View.VISIBLE);
                rlGuestFourGoldLayout.setVisibility(View.VISIBLE);
                rlGuestFiveGoldLayout.setVisibility(View.VISIBLE);
                rlGuestSixGoldLayout.setVisibility(View.VISIBLE);
                rlGuestSevenGoldLayout.setVisibility(View.VISIBLE);*/

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
                } else if (SessionUser.getUser().getUser_id().equals(guestSix.getUser_id())) {
                    isThisGuest6 = true;
                    loadStarImage(guestSix.getOver_all_gold());
                } else if (SessionUser.getUser().getUser_id().equals(guestSeven.getUser_id())) {
                    isThisGuest7 = true;
                    loadStarImage(guestSeven.getOver_all_gold());
                }
            }
        } else {
            ivGift.setVisibility(View.VISIBLE);
            isGuest1 = false;
            isGuest2 = false;
            guest = null;
            guest2 = null;
        }

        if (mPosition != -1) {
            if (mRequests.size() > 0) {
                adapterRequests.notifyDataSetChanged();
                rl_notify.setVisibility(View.VISIBLE);
                tvCountReq.setText(String.valueOf(mRequests.size()));
            } else {
                rl_notify.setVisibility(View.GONE);
                tvCountReq.setText("0");
                tvCountReq.setText(String.valueOf(0));
            }
        }
    }

    private void resetGuestValues() {
        isThisGuest1 = false;
        isThisGuest2 = false;
        isThisGuest3 = false;
        isThisGuest4 = false;
        isThisGuest5 = false;
        isThisGuest6 = false;
        isThisGuest7 = false;
    }

    private void getUserProfile(String id, String bulletMessage) {
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<ProfileResponse> call = apiClient.getProfile(SessionUser.getUser().getUser_id(), id);
            call.enqueue(new retrofit2.Callback<ProfileResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                    ProfileResponse profileResponse = response.body();
                    if (response.code() == 200) {
                        if (profileResponse != null) {
                            if (profileResponse.getStatus().equalsIgnoreCase("success")) {
                                llBulletlay.setVisibility(View.VISIBLE);
                                String profile = profileResponse.getData().getUser().getProfile_pic();
                                String profileName = profileResponse.getData().getUser().getName();
                                tvBulletmsg.setText(bulletMessage);
                                tvBulletName.setText(profileName);
                                if (!profile.isEmpty()) {
                                    Picasso.get().load(profile).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivBulletUserImg);
                                } else
                                    Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivBulletUserImg);

                                llBulletlay.setVisibility(View.VISIBLE);

                                TranslateAnimation anim = new TranslateAnimation(900f, -500f, 0.0f, 0.0f);  // might need to review the docs
                                anim.setDuration(7000);

                                llBulletlay.setAnimation(anim);
                                llBulletlay.setVisibility(View.GONE);

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

    private void removeGuest() {
        if (isThisGuest1)
            removeGuestApi(SessionUser.getUser().getUser_id(), true, false, 1);
        else if (isThisGuest2)
            removeGuestApi(SessionUser.getUser().getUser_id(), true, false, 2);
    }

    public void removeGuestApi(String removeGuestId, boolean isRemoveSelf, boolean isBroadcasterRemove, int i) {
        if (utils.isNetworkAvailable()) {
            if (isBroadcasterRemove) {
                switch (i) {
                    case 1:
                        sendChannelMessage(removeGuestId + level + SessionUser.getUser().getName() + " Guest One Removed");
                        break;
                    case 2:
                        sendChannelMessage(removeGuestId + level + SessionUser.getUser().getName() + " Guest Two Removed");
                        break;
                }
            } else if (isRemoveSelf) {
                switch (i) {
                    case 1:
                        sendChannelMessage(removeGuestId + level + SessionUser.getUser().getName() + " Guest One Left the Broadcast");
                        break;
                    case 2:
                        sendChannelMessage(removeGuestId + level + SessionUser.getUser().getName() + " Guest Two Left the Broadcast");
                        break;
                }
            }

            this.removeGuestId = removeGuestId;
            callRemoveGuestAPI(removeGuestId);
        }
    }

    public void onStarClicked(View view) {
        dailyAndWeeklyGold = "daily";
        if (SessionUser.getUser().getUser_id().equals(broadcaster.getUser_id())) {
            if (isBroadcaster)
                guestGoldTopperDetails("",
                        broadcaster.getProfile_pic(), broadcaster.getUser_id(), broadcaster.getName());
            return;
        }

        if (isGuest) {
            if (isGuest1 || isGuest2 || isGuest3 || isGuest4 || isGuest5 || isGuest6 || isGuest7 ) {
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
                }else if (SessionUser.getUser().getUser_id().equals(guestThree.getUser_id())) {
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
                } else if (SessionUser.getUser().getUser_id().equals(guestSix.getUser_id())) {
                    if (isGuest6) {
                        guestGoldTopperDetails(guestSix.getOver_all_gold(),
                                guestSix.getProfile_pic(), guestSix.getUser_id(), guestSix.getName());
                    }
                } else if (SessionUser.getUser().getUser_id().equals(guestSeven.getUser_id())) {
                    if (isGuest7) {
                        guestGoldTopperDetails(guestSeven.getOver_all_gold(),
                                guestSeven.getProfile_pic(), guestSeven.getUser_id(), guestSeven.getName());
                    }
                }
            } else {
                guestGoldTopperDetails(/*tvBroadcastGold.toString()*/"",
                        broadcaster.getProfile_pic(), broadcaster.getUser_id(), broadcaster.getName());
            }
        } else {
            guestGoldTopperDetails("",
                    broadcaster.getProfile_pic(), broadcaster.getUser_id(), broadcaster.getName());
        }
    }

    public void guestGoldTopperDetails(String overAllGold, String profilePic, String userid, String name) {
        isProfileShowing = false;
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityAudioCall.this);
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
        ImageView ivEffect = parentView.findViewById(R.id.iv_effect);
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
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
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

    private void callGuestVideoMuteResume() {
        if (isGuest1 || isGuest2) {
            if (isGuest1) {
                if (SessionUser.getUser().getUser_id().equals(guestOne.getUser_id())) {
                    callVideoMuteAPI(guestOne.getUser_id(), broadcaster.getUser_id(), "no");
                    if (!isGuestVideoMuted) {
                        worker().getRtcEngine().muteLocalVideoStream(false);
                        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " GuestOne UnMuted");
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
                        worker().getRtcEngine().muteLocalVideoStream(false);
                        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " GuestTwo UnMuted");
                    }
                    if (!flag)
                        worker().getRtcEngine().muteLocalAudioStream(false);
                    worker().getRtcEngine().muteAllRemoteAudioStreams(false);
                }
            }
        }
    }

    private void callGuestVideoMutePause() {
        if (isGuest1 || isGuest2) {
            if (isGuest1) {
                if (SessionUser.getUser().getUser_id().equals(guestOne.getUser_id())) {
                    callVideoMuteAPI(guestOne.getUser_id(), broadcaster.getUser_id(), "yes");
                    if (!isGuestVideoMuted) {
                        worker().getRtcEngine().muteLocalVideoStream(true);
                        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " GuestOne Muted");
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
                        worker().getRtcEngine().muteLocalVideoStream(true);
                        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " GuestTwo Muted");
                    }
                    if (!flag)
                        worker().getRtcEngine().muteLocalAudioStream(true);
                    worker().getRtcEngine().muteAllRemoteAudioStreams(true);
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

    private void callBroadcasterUnMute() {
        clearNotification();
        worker().getRtcEngine().enableVideo();
        Log.e(TAG, "onResume: " + flag);
        if (!flag)
            worker().getRtcEngine().muteLocalAudioStream(false);
        worker().getRtcEngine().muteAllRemoteAudioStreams(false);
        sendChannelMessage(SessionUser.getUser().getUser_id() + level + "Broadcast has been Resumed!");
    }

    private void callBroadcasterMute() {
        worker().getRtcEngine().disableVideo();
        if (!flag)
            worker().getRtcEngine().muteLocalAudioStream(true);
        worker().getRtcEngine().muteAllRemoteAudioStreams(true);
        sendChannelMessage(SessionUser.getUser().getUser_id() + level + "Broadcast has been Paused!");
    }

    public void clearNotification() {
        notificationManager = NotificationManagerCompat.from(ActivityAudioCall.this);
        notificationManager.cancelAll();
    }

    public void showNotification() {
        notificationManager = NotificationManagerCompat.from(ActivityAudioCall.this);
        createNotificationChannel();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            mBuilder = new NotificationCompat.Builder(ActivityAudioCall.this, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Broadcast Still Streaming ")
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("Your broadcast has paused!"))
                    .setChannelId(CHANNEL_ID)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            notificationManager.notify(notificationId, mBuilder.build());
        } else {
            mBuilder = new NotificationCompat.Builder(ActivityAudioCall.this)
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
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void switchAudienceToGuest() {
        isGuest = true;
        doSwitchToBroadcaster(true);
    }


    public void sendMessageToChannel(String msg) {
        MessageBean message = new MessageBean(selfName, SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + msg, true, false, false);
        messageBeanList.add(message);
        adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
        rvMessages.scrollToPosition(messageBeanList.size() - 1);
        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + msg);
    }
}