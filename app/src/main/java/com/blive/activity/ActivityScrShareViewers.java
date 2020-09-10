package com.blive.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blive.model.URL;
import com.blive.service.ServiceGenerator;
import com.blive.service.linkshorten;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.ViewPager;
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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.blive.adapter.AdapterFriendsShare;
import com.blive.adapter.AdapterGiftGRP;
import com.blive.adapter.AdapterGroupTopper;
import com.blive.adapter.AdapterImages;
import com.blive.adapter.AdapterMessage;
import com.blive.agora.AGEventHandler;
import com.blive.agora.AGLinearLayout;
import com.blive.BLiveApplication;
import com.blive.BuildConfig;
import com.blive.agora.rtmChat.ChatHandler;
import com.blive.agora.rtmChat.ChatManager;
import com.blive.constant.Constants_api;
import com.blive.constant.Constants_app;
import com.blive.custom.LikeAnimationView;
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
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
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


public class ActivityScrShareViewers extends BaseActivity implements AGEventHandler, AdapterGiftGRP.ListenerGift, AdapterImages.ListenerImage,
        AdapterFriendsShare.Listener, AdapterGroupTopper.Listener, AdapterMessage.ListenerMessage {

    @BindView(R.id.iv_gift)
    ImageView ivGift;
    @BindView(R.id.rl_live)
    RelativeLayout rlLive;
    @BindView(R.id.bottom_audience)
    AGLinearLayout bottomAudience;
    @BindView(R.id.iv_user)
    ImageView ivUser;
    @BindView(R.id.iv_follow)
    ImageView ivFollow;
    @BindView(R.id.rl_coins)
    RelativeLayout rlCoins;
    @BindView(R.id.rv_gift)
    RecyclerView rvGift;
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
    @BindView(R.id.tv_gift)
    TextView tvGift;
    @BindView(R.id.tv_assets)
    TextView tvAssets;
    @BindView(R.id.iv)
    ImageView iv;
    @BindView(R.id.tvCurrentDiamondValue)
    TextView tvCurrentDiamondValue;
    @BindView(R.id.rl_offline)
    RelativeLayout rlOffline;
    @BindView(R.id.iv_offline)
    ImageView ivOffline;

    private FrameLayout mFlRoot;
    private RtcEngine mRtcEngine;
    private List<MessageBean> messageBeanList;
    private AdapterMessage adapter;
    private LinearLayout llChat, llCombo, llUnFollow, llFollow, llKickOut;
    private String channelName = "", selfName = "", image = "", broadcasterId = "", isFollowing = "", time = "", guestId = "", level = "", moonValue = "",
            dailyAndWeeklyGold = "", decodeImage = "",url_url;
    private int cRole = 0, position = -1, size = 0, viewers = 0, likes = 0, gold = 0, oldGold = 0, diamondvalue = 0, temp = 0, comboMultiplier = 0, page = 1,
            channelUserCount = 0, mStartIndex = 0, dvalue = 0, temp1 = 0, temp2 = 0, lastPage = 0, oldMoonImageUrl = 0;
    private boolean isBroadcaster = false, isAudience = false, isTextMuted = false, isUserListEnd = false, isRefreshing = false,
            isAPICalled = false, isEntranceEffects = false, isGoldAdding = false, isFirst = false, isLiked = false, isClose = false, isSwiped = false,
            isSwipedDown = false, isArrived = false, isBroadcastEnded = false, isGiftShowing = false, isDiamondPurchase = false, isNextUser = false,
            isVideoMute = false, isClicked = false, isClickedProfile = false, isAudienceFollowing = false;
    private Gift mGift;
    private LikeAnimationView mLikeAnimationView;
    private ArrayList<Bitmap> mAnimationItemList;
    private Animation slideUp, slideDown;
    private User broadcaster;
    private ArrayList<Gift> giftsList, giftTools, freeGifts;
    private ArrayList<Audience> mAudiences;
    private long startTime, endTime;
    private ArrayList<User> users;
    private TextView tvReceived, tvFreeGifts, tvNoFriendsList, tvMoonLevelCount, tvGiftMessage, tvEntranceName, tvNoContributors,
            tvGiftsList, tvAssestsList, tvFreeGiftsList, tvOffline, tvPause, tvStarLevel,count_tv;
    private FrameLayout root;
    private ArrayList<GiftMessage> giftMessages;
    private ArrayList<EntranceEffect> entranceEffects;
    private ArrayList<String> messagesList;
    private ArrayList<User> usersFriendsList;
    private RecyclerView rvFreeGift, rvAssets, rvFriendsList, rv_guestTopperList, rvMessages, rvImages;
    private AdapterGiftGRP adapterGifts;
    private Spinner spCombo;
    private Button bSendGift, btnSendFriends, btnChangeAssests;
    private AdapterFriendsShare adapterFriends;
    public ViewPager viewPager;
    private CheckBox checkBoxSelectAllFriends;
    private ImageView ivDiamond, starRatings, starRatings1, ivEntrance, ivBlur, ivBlurbroad, ivUserProfileffect;
    final Handler giftHandler = new Handler();
    private LinearLayout cvGiftMessage, ll_current_diamond;
    final Handler entranceHandler = new Handler();
    private ProgressBar progressBar;
    private AlertDialog alertDialog;
    private String price = "", multiplier = "";
    private RtmClient mRtmClient;
    private ChatManager mChatManager;
    private RtmChannel mRtmChannel;
    private ChatHandler mChatHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_audience);
        BLiveApplication.setCurrentActivity(this);
        ButterKnife.bind(this);
        changeStatusBarColor();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isClicked = false;
        BLiveApplication.setCurrentActivity(this);
        if (isDiamondPurchase) {
            isDiamondPurchase = false;
            dvalue = Integer.valueOf(SessionUser.getUser().getDiamond());
            tvCurrentDiamondValue.setText(SessionUser.getUser().getDiamond());
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
        isNextUser = i.getBooleanExtra("isSwiped", false);

        mChatManager = BLiveApplication.getInstance().getChatManager();
        mRtmClient = mChatManager.getRtmClient();
        mRtmChannel = mChatManager.getRtmChannel();
        if (cRole!=0){
            joinrtmchannel(broadcaster.getUsername(),"0");
        }else if (cRole==0){
            joinrtmchannel(SessionUser.getUser().getUsername(),"0");
        }
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

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

        SessionUser.isScreenSharing(true);

        btnChangeAssests = findViewById(R.id.btn_changeAssests);
        ll_current_diamond = findViewById(R.id.ll_current_diamond);
        tvStarLevel = findViewById(R.id.tv_star_level);
        tvPause = findViewById(R.id.tv_pause);
        ivBlur = findViewById(R.id.iv_blur);
        ivBlurbroad = findViewById(R.id.iv_blur_broad);
        mFlRoot = findViewById(R.id.fl_root);
        bSendGift = findViewById(R.id.bSendGift);
        tvOffline = findViewById(R.id.tv_offline);
        tvGiftsList = findViewById(R.id.tv_GiftList);
        tvAssestsList = findViewById(R.id.tv_AssetsList);
        tvFreeGiftsList = findViewById(R.id.tv_FreeGiftList);
        ivOffline = findViewById(R.id.iv_offline);
        rlOffline = findViewById(R.id.rl_offline);
        tvEntranceName = findViewById(R.id.tv_entranceName);
        ivEntrance = findViewById(R.id.iv_entrance);
        cvGiftMessage = findViewById(R.id.cv_gift_message);
        tvGiftMessage = findViewById(R.id.tv_gift_message);
        tvMoonLevelCount = findViewById(R.id.tvMoonLevelCount);
        llCombo = findViewById(R.id.ll_combo);
        starRatings = findViewById(R.id.moonLevelStar);
        starRatings1 = findViewById(R.id.moonLevelStar1);
        ivDiamond = findViewById(R.id.iv_diamond);
        tvCurrentDiamondValue = findViewById(R.id.tvCurrentDiamondValue);
        tvGift = findViewById(R.id.tv_gift);
        tvAssets = findViewById(R.id.tv_assets);
        tvFreeGifts = findViewById(R.id.tv_freeGifts);
        rvFreeGift = findViewById(R.id.rv_freeGift);
        rvAssets = findViewById(R.id.rv_assets);
        llChat = findViewById(R.id.ll_chat);
        rvMessages = findViewById(R.id.rv_message);
        root = findViewById(R.id.root);
        tvReceived = findViewById(R.id.tv_received);
        rvImages = findViewById(R.id.rv_images);
        startTime = System.currentTimeMillis();
        spCombo = findViewById(R.id.spCombo);

        ivUserProfileffect = findViewById(R.id.iv_profile_dp_affect);
        int level = Integer.parseInt(broadcaster.getLevel());

        Glide.with(getApplicationContext())
                .load(broadcaster.getTools_applied())
                .into(ivUserProfileffect);

        Glide.with(getApplicationContext())
                .load(R.drawable.diamond_svg)
                .into(ivDiamond);

        dvalue = Integer.valueOf(String.valueOf(SessionUser.getUser().getDiamond()));
        getLevel();
        tvCurrentDiamondValue.setText(SessionUser.getUser().getDiamond());

        try {
            decodeImage = URLDecoder.decode(image, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (decodeImage != null && !decodeImage.isEmpty()) {
            Picasso.get().load(decodeImage).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(ivUser);
            Picasso.get().load(decodeImage).fit().transform(new BlurTransformation(mActivity)).centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).
                    placeholder(R.drawable.user).into(ivOffline);
        } else {
            Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(ivUser);
            Picasso.get().load(R.drawable.user).fit().transform(new BlurTransformation(mActivity)).centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).
                    placeholder(R.drawable.user).into(ivOffline);
        }

        initEngineAndJoin();

        giftMessages = new ArrayList<>();
        entranceEffects = new ArrayList<>();
        mAudiences = new ArrayList<>();
        giftsList = new ArrayList<>();
        giftTools = new ArrayList<>();
        freeGifts = new ArrayList<>();
        messageBeanList = new ArrayList<>();
        messagesList = new ArrayList<>();
        usersFriendsList = new ArrayList<>();
        llCombo.setVisibility(View.GONE);

        ImageButton imageButton = findViewById(R.id.sendButton);
        imageButton.setOnClickListener(sendButtonListener);

        moonValue = broadcaster.getOver_all_gold();
        int times = Integer.valueOf(moonValue) / 8100;
        tvMoonLevelCount.setText("5x" + String.valueOf(times));
        loadStarImage(moonValue);

        String roomName = i.getStringExtra(Constants_app.ACTION_KEY_ROOM_NAME);

        slideUp = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        rvMessages.setLayoutManager(layoutManager);
        LinearLayoutManager layoutManagerImages = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        rvImages.setLayoutManager(layoutManagerImages);
        GridLayoutManager layoutManager1 = new GridLayoutManager(getApplicationContext(), 2, GridLayoutManager.HORIZONTAL, false);
        rvGift.setLayoutManager(layoutManager1);
        rvGift.setVisibility(View.VISIBLE);
        rvGift.setHasFixedSize(true);
        GridLayoutManager layoutManager11 = new GridLayoutManager(getApplicationContext(), 2, GridLayoutManager.HORIZONTAL, false);
        rvAssets.setLayoutManager(layoutManager11);
        rvAssets.setVisibility(View.VISIBLE);
        rvAssets.setHasFixedSize(true);
        GridLayoutManager layoutManager2 = new GridLayoutManager(getApplicationContext(), 2, GridLayoutManager.HORIZONTAL, false);
        rvFreeGift.setLayoutManager(layoutManager2);
        rvFreeGift.setVisibility(View.VISIBLE);
        rvFreeGift.setHasFixedSize(true);

        adapter = new AdapterMessage(this, messageBeanList);
        rvMessages.setAdapter(adapter);
        adapter.setOnClickListener(this);

        String moonValueLevel = Constants_app.loadStarLevel(moonValue);
        tvStarLevel.setText(moonValueLevel);

        loadStarImage(moonValue);

        MessageBean messageBean = new MessageBean(selfName, getResources().getString(R.string.warning), true, true, false);
        messageBeanList.add(messageBean);
        adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
        rvMessages.scrollToPosition(messageBeanList.size() - 1);

        if (broadcaster != null) {
            oldGold = Integer.valueOf(broadcaster.getOver_all_gold());
            gold = Integer.valueOf(broadcaster.getOver_all_gold());
            tvReceived.setText(String.valueOf(gold));
        }

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
                        comboMultiplier = 99;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        rlOffline.setVisibility(View.VISIBLE);
        rvImages.setVisibility(View.GONE);
        rvMessages.setVisibility(View.GONE);
        bottomAudience.setVisibility(View.GONE);
        ivGift.setVisibility(View.GONE);

        tvOffline.postDelayed(() -> {
            tvOffline.setVisibility(View.VISIBLE);
        }, 5000);

        if (isFollowing.equalsIgnoreCase("yes"))
            ivFollow.setVisibility(View.GONE);
        else
            ivFollow.setVisibility(View.VISIBLE);

        TextView textRoomName = findViewById(R.id.room_name);
        textRoomName.setText(broadcaster.getName());

        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }

            private GestureDetector gestureDetector = new GestureDetector(ActivityScrShareViewers.this, new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
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
                            return true;
                        case 4:
                            onSwipeRight();
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
                rlLive.setVisibility(View.VISIBLE);
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
            private GestureDetector gestureDetector = new GestureDetector(ActivityScrShareViewers.this, new GestureDetector.SimpleOnGestureListener() {
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
            }

            void onSwipeDown() {
            }

            void onSwipeLeft() {
                rlLive.setVisibility(View.GONE);
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

                    bottomAudience.setVisibility(View.VISIBLE);
                    llGift.startAnimation(slideDown);
                    llGift.setVisibility(View.GONE);
                }

                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        if (!isFirst) {
            isFirst = true;
            if (utils.isNetworkAvailable()) {
                utils.showProgress();
                final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
                Call<GiftResponse> call = apiClient.getGifts("screenSharing",SessionUser.getUser().getUser_id());
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

        callAudiencesAPI(broadcaster.getUser_id());

    }

    private void initEngineAndJoin() {
        try {
            mRtcEngine = RtcEngine.create(getApplicationContext(), BuildConfig.private_app_id, new IRtcEngineEventHandler() {

                @Override
                public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
                    //Log.d(TAG, "onJoinChannelSuccess: " + (uid & 0xFFFFFFL));
                }

                @Override
                public void onUserJoined(final int uid, int elapsed) {
                    //Log.e(TAG, "onUserJoined: " + (uid & 0xFFFFFFL));
                    try {
                        runOnUiThread(() -> setupRemoteView(uid));
                    } catch (Exception e) {
                        Log.e("ErrorOccurred", e.getMessage());
                        Crashlytics.logException(e);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
        mRtcEngine.enableVideo();
        mRtcEngine.setClientRole(Constants.CLIENT_ROLE_AUDIENCE);

        mRtcEngine.joinChannel(null, channelName, "BLive", SessionUser.getUser().getId());
    }

    @Override
    protected void deInitUI() {
        doLeaveChannel();
        event().removeEventHandler(this);
    }

    @OnClick(R.id.btn_changeAssests)
    public void onClickchangeAssests() {
        Intent intent = new Intent(mActivity, ActivityWebView.class);
        intent.putExtra("title", "My Assests");
        intent.putExtra("from", "screenshare");
        intent.putExtra("url", Constants_api.assets + SessionUser.getUser().getUser_id());
        startActivity(intent);
    }

    @OnClick(R.id.iv_follow)
    public void onClickFollow() {
        callFollowAPI("follow", broadcaster.getUser_id());
    }

    private void callFollowAPI(String type, String userId) {
        if (utils.isNetworkAvailable()) {
            utils.showProgress();

            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<FollowResponse> call = apiClient.follow(type, userId, SessionUser.getUser().getUser_id());
            call.enqueue(new retrofit2.Callback<FollowResponse>() {
                @Override
                public void onResponse(@NonNull Call<FollowResponse> call, @NonNull Response<FollowResponse> response) {
                    FollowResponse followResponse = response.body();
                    utils.hideProgress();
                    if (response.code() == 200) {
                        if (followResponse != null) {
                            if (followResponse.getStatus().equalsIgnoreCase("success")) {
                                showToast(followResponse.getData().getMessage());
                                ivFollow.setVisibility(View.GONE);
                                getLevel();
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

    @OnClick(R.id.bSendGift)
    public void sendGift() {
        try {
            if (mGift != null) {
                if (dvalue >= Integer.valueOf(mGift.getPrice())) {
                    if (Integer.valueOf(mGift.getPrice()) < 101) {
                        int price = Integer.valueOf(mGift.getPrice()) * comboMultiplier;
                        if (dvalue >= price) {
                            sendGift(String.valueOf(price), String.valueOf(comboMultiplier));
                        } else {
                            isDiamondPurchase = true;
                            bottomAudience.setVisibility(View.VISIBLE);
                            Intent intent = new Intent(mActivity, ActivityWebView.class);
                            intent.putExtra("title", "Wallet");
                            intent.putExtra("from", "liveRoom");
                            intent.putExtra("url", Constants_api.wallet + SessionUser.getUser().getUser_id());
                            startActivity(intent);
                        }
                    } else {
                        sendGift(mGift.getPrice(), "");
                    }
                } else {
                    isDiamondPurchase = true;
                    bottomAudience.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(mActivity, ActivityWebView.class);
                    intent.putExtra("title", "Wallet");
                    intent.putExtra("from", "liveRoom");
                    intent.putExtra("url", Constants_api.wallet + SessionUser.getUser().getUser_id());
                    startActivity(intent);
                }
            } else {
                showToast("Pick a Gift to send");
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    public void sendGift(String mPrice, String mMultiplier) {
        price = mPrice;
        multiplier = mMultiplier;
        llGift.startAnimation(slideDown);
        llGift.setVisibility(View.GONE);
        ivGift.setEnabled(false);
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<GiftResponse> call = apiClient.sendGift(SessionUser.getUser().getUser_id(), SessionUser.getUser().getUser_id(), broadcasterId, mGift.getName(), mPrice, multiplier);
            call.enqueue(new retrofit2.Callback<GiftResponse>() {
                @Override
                public void onResponse(@NonNull Call<GiftResponse> call, @NonNull Response<GiftResponse> response) {
                    GiftResponse giftResponse = response.body();
                    utils.hideProgress();
                    if (response.code() == 200) {
                        if (giftResponse != null) {
                            if (giftResponse.getStatus().equalsIgnoreCase("success")) {
                                onGiftSuccess(giftResponse.getData().getOver_all_gold(), giftResponse.getData().getCurrent_gold_value(), giftResponse.getData().getMoon_level(), giftResponse.getData().getMoon_value(),giftResponse.getData().getGift_name());
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

    public void onGoldLayoutClicked(View view) {
        try {
            showTopContributors(broadcaster.getName(), tvReceived.getText().toString());
        } catch (Exception e) {
            Log.e(TAG, "onClick: " + e);
            Crashlytics.logException(e);
        }

    }

    public void onClickMessage(View view) {
        if (!isBroadcaster) {
            if (!isTextMuted)
                llChat.setVisibility(View.VISIBLE);
            else
                showToast("You Are Currently Text Muted \"For This Session.\"");
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
                MessageBean messageBean = new MessageBean(selfName, SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " has liked you ♥", true, false, false);
                messageBeanList.add(messageBean);
                adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                rvMessages.scrollToPosition(messageBeanList.size() - 1);
                sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " has liked you ♥");
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

    private void getLevel() {
        level = SessionUser.getUser().getLevel();
        if (level.length() == 1) {
            level = "0" + level;
        }
    }

    public void onGiftsSuccess(ArrayList<Gift> gifts, ArrayList<Gift> tools, ArrayList<Gift> freeGift) {
        giftsList.addAll(gifts);
        giftTools.addAll(tools);
        freeGifts.addAll(freeGift);

        try {
            adapter.setGifts(gifts);
        } catch (Exception e) {
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

        adapterGifts = new AdapterGiftGRP(this, giftsList);
        adapterGifts.setOnClickListener(this);
        rvGift.setAdapter(adapterGifts);

        callAudiencesAPI(broadcaster.getUser_id());
    }

    @Override
    public void OnClicked(User user) {

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
                                onImagesSuccess(usersResponse.getData().getAudiences(), usersResponse.getData().getEntranceEffect());
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
            Log.e("topperError", e.getMessage());
        }
    }

    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
        Log.e(TAG, "onFirstRemoteVideoDecoded: ");
        runOnUiThread(() -> {
            rlOffline.setVisibility(View.GONE);
            rvImages.setVisibility(View.VISIBLE);
            rvMessages.setVisibility(View.VISIBLE);
            bottomAudience.setVisibility(View.VISIBLE);
            ivGift.setVisibility(View.VISIBLE);
        });

    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        //Log.e(TAG, "onJoinChannelSuccess: " + uid);
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        //Log.e(TAG, "onUserOffline: " + uid);
        if (uid == broadcaster.getId()) {
            if (!isBroadcastEnded)
                runOnUiThread(this::callAPIOffline);
        }
    }

    @Override
    public void onUserJoined(int uid, int elapsed) {
        //Log.e(TAG, "onUserJoined: " + uid);
        //rlOffline.setVisibility(View.GONE);
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
                            }
                        } else {
                            runOnUiThread(() -> {
                                try {
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

    class LikeAnimationViewProvider implements LikeAnimationView.Provider {
        LikeAnimationViewProvider() {
        }

        public Bitmap getBitmap(Object obj) {
            return ActivityScrShareViewers.this.mAnimationItemList == null ? null : (Bitmap) ActivityScrShareViewers.this.mAnimationItemList.get(((Integer) obj).intValue());
        }
    }

    @OnClick(R.id.iv_user)
    public void onClickUser() {
        if (!isClicked) {
            isClicked = true;
            if (cRole != 1) {
                isClicked = false;
                Intent intent = new Intent(this, ActivityViewProfile.class);
                intent.putExtra("image", image);
                intent.putExtra("userId", broadcasterId);
                intent.putExtra("from", "liveRoom");
                startActivity(intent);
            }
        }
    }

    public void onClickShare(View view) {
        try {
            showSharingDialog(ActivityScrShareViewers.this);
        } catch (Exception e) {
            Log.e(TAG, "onClick: " + e);
            Crashlytics.logException(e);
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
        ll_current_diamond.setVisibility(View.VISIBLE);
        btnChangeAssests.setVisibility(View.GONE);
        llCombo.setVisibility(View.GONE);
        if (giftsList.size() > 0) {
            rvAssets.setVisibility(View.GONE);
            rvFreeGift.setVisibility(View.GONE);
            rvGift.setVisibility(View.VISIBLE);
            tvAssestsList.setVisibility(View.GONE);
            tvGiftsList.setVisibility(View.GONE);
            tvFreeGiftsList.setVisibility(View.GONE);
            bSendGift.setVisibility(View.VISIBLE);
        } else {
            rvAssets.setVisibility(View.GONE);
            rvFreeGift.setVisibility(View.GONE);
            rvGift.setVisibility(View.GONE);
            tvAssestsList.setVisibility(View.GONE);
            tvGiftsList.setVisibility(View.VISIBLE);
            tvFreeGiftsList.setVisibility(View.GONE);
            bSendGift.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.tv_assets)
    public void onClickAssetsMenu() {
        tvAssets.setTextColor(getResources().getColor(R.color.colorAccent));
        tvFreeGifts.setTextColor(getResources().getColor(R.color.white));
        tvGift.setTextColor(getResources().getColor(R.color.white));
        llCombo.setVisibility(View.GONE);
        ll_current_diamond.setVisibility(View.GONE);
        btnChangeAssests.setVisibility(View.VISIBLE);
        if (giftTools.size() > 0) {
            adapterGifts = new AdapterGiftGRP(this, giftTools);
            adapterGifts.setOnClickListener(this);
            rvAssets.setAdapter(adapterGifts);
            rvGift.setVisibility(View.GONE);
            rvFreeGift.setVisibility(View.GONE);
            rvAssets.setVisibility(View.VISIBLE);
            tvAssestsList.setVisibility(View.GONE);
            tvGiftsList.setVisibility(View.GONE);
            tvFreeGiftsList.setVisibility(View.GONE);
            bSendGift.setVisibility(View.VISIBLE);
        } else {
            rvGift.setVisibility(View.GONE);
            rvFreeGift.setVisibility(View.GONE);
            rvAssets.setVisibility(View.GONE);
            tvAssestsList.setVisibility(View.VISIBLE);
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
            tvAssestsList.setVisibility(View.GONE);
            tvGiftsList.setVisibility(View.GONE);
            tvFreeGiftsList.setVisibility(View.GONE);
            bSendGift.setVisibility(View.VISIBLE);
        } else {
            rvGift.setVisibility(View.GONE);
            rvAssets.setVisibility(View.GONE);
            rvFreeGift.setVisibility(View.GONE);
            tvAssestsList.setVisibility(View.GONE);
            tvGiftsList.setVisibility(View.GONE);
            tvFreeGiftsList.setVisibility(View.VISIBLE);
            bSendGift.setVisibility(View.GONE);
        }
    }

    public void onClickClose(View view) {
        try {
            isClose = true;
            callRemoveImageAPI();
            Constants_app.cleanMessageListBeanList();
            deInitModules();
            finish();
        } catch (Exception e) {
            Log.e(TAG, "onClickClose: " + e);
            Crashlytics.logException(e);
        }
    }

    private void deInitModules() {
        RtcEngine.destroy();
        mRtcEngine = null;

        mChatManager.leaveChannel();
        mChatManager.removeChatHandler(mChatHandler);
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
            Intent intent = new Intent(ActivityScrShareViewers.this, ActivityBroadcastStop.class);
            intent.putExtra("name", broadcaster.getName());
            intent.putExtra("image", broadcaster.getProfile_pic());
            intent.putExtra("user_id", broadcaster.getUser_id());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            finish();
            startActivity(intent);
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
            Crashlytics.logException(e);
        }
    };

    private void doLeaveChannel() {
        worker().leaveChannel(config().mChannel);
        if (isBroadcaster()) {
            worker().preview(false, null, 0);
        }
    }

    private void setupRemoteView(int uid) {
        SurfaceView surfaceV = RtcEngine.CreateRendererView(getApplicationContext());
        surfaceV.setZOrderOnTop(true);
        surfaceV.setZOrderMediaOverlay(true);
        mFlRoot.addView(surfaceV, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        try {
            mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, uid));
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        rlOffline.setVisibility(View.GONE);
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
                    entranceEffect.setAccount(account);
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
                            try {
                                callAlertKickOut();
                            } catch (Exception e) {
                                Log.e(TAG, "onClick: " + e);
                                Crashlytics.logException(e);
                            }

                        }
                    } else {
                        msg = msg.replace("has bEEn kicKed OuT", "has been kicked out");
                        messageBean = new MessageBean(account, msg, false, false, false);
                        messageBeanList.add(messageBean);
                        adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                        rvMessages.scrollToPosition(messageBeanList.size() - 1);
                    }
                } else if (msg.contains(": Has sent gIfTsEnTtOyOU")) {

                    String message = msg;
                    message = message.replace("gIfTsEnTtOyOU", "");

                    messageBean = new MessageBean(account, message, false, false, true);
                    //messageBean.setBackground(getMessageColor(account));
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
                    //messageBean.setBackground(getMessageColor(account));
                    messageBeanList.add(messageBean);
                    adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                    rvMessages.scrollToPosition(messageBeanList.size() - 1);

                    if (msg.contains("liked")) {
                        doLikeAnimationAudience();
                        likes = likes + 1;
                    } else if (msg.contains("Video Muted")) {
                        if (!isVideoMute) {
                            isVideoMute = true;
                            ivBlur.setVisibility(View.VISIBLE);
                            tvPause.setVisibility(View.VISIBLE);
                            ivBlurbroad.setVisibility(View.VISIBLE);
                            Picasso.get().load(broadcaster.getProfile_pic()).fit().transform(new BlurTransformation(mActivity)).centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).
                                    placeholder(R.drawable.user).into(ivBlurbroad);
                        }
                    } else if (msg.contains("Video UnMuted")) {
                        if (isVideoMute) {
                            isVideoMute = false;
                            ivBlur.setVisibility(View.GONE);
                            tvPause.setVisibility(View.GONE);
                            ivBlurbroad.setVisibility(View.GONE);
                        }
                    } else if (msg.contains("has arrived")) {
                        viewers = viewers + 1;
                        callAudiencesAPI(broadcaster.getUser_id());
                    } else if (msg.contains("Broadcast has ended")) {
                        isBroadcastEnded = true;
                        callRemoveImageAPI();
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityScrShareViewers.this);
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
                mChatManager.leaveChannel();
                mChatManager.removeChatHandler(mChatHandler);
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

    private void callSwitchCase(int j) {
        try {
            isGiftShowing = true;

            for (int i = 0; i < giftsList.size(); i++) {
                if (giftMessages.get(j).getMessage().contains(giftsList.get(i).getName())) {
                    if (giftsList.get(i).getType().equalsIgnoreCase("normal")) {
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
            Log.e(TAG, "callSwitchCase: " + e);
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
            //moonLevel = Constants_app.getMoon_level(moonValue);
            int times = Integer.valueOf(moonValue) / 8100;
            //moonLevelCount = String.valueOf(times);
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

    @Override
    protected void onDestroy() {
        Constants_app.cleanMessageListBeanList();
        super.onDestroy();
    }

    @Override
    public void OnClicked(Gift gift) {
        mGift = gift;
        if (Integer.valueOf(mGift.getPrice()) < 101) {
            llCombo.setVisibility(View.GONE);
            spCombo.setSelection(0);
            comboMultiplier = 1;
        } else {
            comboMultiplier = 0;
            llCombo.setVisibility(View.GONE);
        }
    }

    public void onImagesSuccess(ArrayList<Audience> audiences, String entranceUrl) {
        mAudiences = audiences;
        AdapterImages adapterImages = new AdapterImages(this, audiences);
        adapterImages.setOnClickListener(this);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        rvImages.setLayoutManager(layoutManager1);
        rvImages.setAdapter(adapterImages);
        tvCount.setText(String.valueOf(audiences.size()));

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
                entranceEffects.add(entranceEffect);

                if (entranceEffects.size() > 0) {
                    if (!isEntranceEffects) {
                        callEntranceEffect(temp1);
                    }
                }
            }
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
            Log.e(TAG, "callEntranceEffect: " + e);
        }
    }

    public void onRemoveSuccess(ArrayList<Audience> images) {

    }

    @Override
    public void OnClickedAudience(Audience audience) {
        if (!SessionUser.getUser().getUser_id().equals(audience.getUser_id())) {
            Intent intent = new Intent(this, ActivityViewProfile.class);
            intent.putExtra("image", Constants_app.decodeImage(audience.getProfile_pic()));
            intent.putExtra("userId", audience.getUser_id());
            intent.putExtra("from", "liveRoom");
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (llChat.getVisibility() == View.VISIBLE) {
            llChat.setVisibility(View.GONE);
        } else if (llGift.getVisibility() == View.VISIBLE) {
            bottomAudience.setVisibility(View.VISIBLE);
            llGift.startAnimation(slideDown);
            llGift.setVisibility(View.GONE);
        } else {
            isClose = true;
            callRemoveImageAPI();
            Constants_app.cleanMessageListBeanList();
            deInitModules();
            finish();
        }
    }

    public void onGiftSuccess(String overAllGold, String currentGoldValue, String moonLevel, String moonValue, String giftname) {

        if (!multiplier.isEmpty()) {
            multiplier = " X" + multiplier;
        } else
            multiplier = "";

        getLevel();

        if (cRole != 1) {
            MessageBean messageBean = new MessageBean(selfName, SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " : Has sent " + mGift.getName() + multiplier, true, false, true);
            messageBeanList.add(messageBean);
            adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
            rvMessages.scrollToPosition(messageBeanList.size() - 1);
            sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " : Has sent gIfTsEnTtOyOU " + giftname + multiplier);
        }

        GiftMessage giftMessage = new GiftMessage();
        giftMessage.setAccount(SessionUser.getUser().getUsername());
        giftMessage.setMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " : Has sent " + mGift.getName() + multiplier);
        giftMessages.add(giftMessage);
        if (giftMessages.size() > 0) {
            if (!isGiftShowing) {
                callSwitchCase(temp);
            }
        }

        messagesList.add(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " : Has sent " + mGift.getName() + multiplier);
        if (messagesList.size() > 0) {
            if (!isGoldAdding)
                callAddGiftValue(temp2);
        }

        dvalue = dvalue - Integer.valueOf(price);
        SessionUser.getUser().setDiamond(String.valueOf(dvalue));
        tvCurrentDiamondValue.setText(String.valueOf(dvalue));

        adapterGifts.onGiftReset();
        mGift = null;
        ivGift.setEnabled(true);
        llCombo.setVisibility(View.GONE);
    }

    /*public void showSharingDialog(Activity activity) {
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
            showBliveFriends();
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

    public void showBLiveFriends() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityScrShareViewers.this);
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

    public void sendMessageToChannel(String msg) {
        MessageBean message = new MessageBean(selfName, SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + msg, true, false, false);
        messageBeanList.add(message);
        adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
        rvMessages.scrollToPosition(messageBeanList.size() - 1);
        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + msg);
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

    public void showBliveFriends() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityScrShareViewers.this);
        View dialog = getLayoutInflater().inflate(R.layout.blive_friends_layout, null);
        bottomSheetDialog.setContentView(dialog);
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
        rvFriendsList = dialog.findViewById(R.id.rv_friends);
        btnSendFriends = dialog.findViewById(R.id.sendSelectedfriends);
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
            try {
                ArrayList<User> selectedFriends = adapterFriends.selectedFriends();

                if (selectedFriends.size() > 0) {
                    JsonArray friendsJsonArr = new JsonArray();
                    for (int i = 0; i < selectedFriends.size(); i++) {
                        friendsJsonArr.add(selectedFriends.get(i).getUser_id());
                    }
                    sendChannelMessage(" has shared with Friends");
                    callShareAPI("blive");
                    shareNotifications(friendsJsonArr);
                    bottomSheetDialog.dismiss();
                    //   dialog.dismiss();
                } else {
                    showToast("Select  Friends");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
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
                        utils.hideProgress();
                        rvFriendsList.setVisibility(View.GONE);
                        tvNoFriendsList.setVisibility(View.VISIBLE);
                        checkBoxSelectAllFriends.setVisibility(View.GONE);
                        showToast(t.getMessage());
                    }
                });
            }
        }
    }

    public void onFriendsSuccess(ArrayList<User> mUsers, int lastPage) {
        this.lastPage = lastPage;
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
                tvNoFriendsList.setVisibility(View.GONE);
                btnSendFriends.setVisibility(View.VISIBLE);
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

    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (Objects.requireNonNull(recyclerView.getAdapter()).getItemCount() != 0) {
            if (!isUserListEnd) {
                int lastVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findLastCompletelyVisibleItemPosition();
                return lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1;
            }
        }
        return false;
    }

    public void loadStarImage(String moonValue) {

        String level = Constants_app.loadStarLevel(moonValue);
        tvStarLevel.setText(level);

        if (oldMoonImageUrl != 0) {
            /*Glide.with(getApplicationContext())
                    .load(oldMoonImage)
                    .into(starRatings1);*/
            Drawable res = getResources().getDrawable(oldMoonImageUrl);
            starRatings1.setImageDrawable(res);
        }

        String uri = Constants_app.loadBroadCasterStar(moonValue);
        int starImage = getResources().getIdentifier(uri, null, getPackageName());
        Drawable res = getResources().getDrawable(starImage);
        starRatings.setImageDrawable(res);
       /* Glide.with(getApplicationContext())
                .load(starImage)
                .into(starRatings);*/

        oldMoonImageUrl = starImage;
    }

    public void onClickMoonLevel(View view) {
        if (utils.isNetworkAvailable()) {
            dailyAndWeeklyGold = "daily";
            try {
                showTopContributors(broadcaster.getName(), tvReceived.getText().toString());
            } catch (Exception e) {
                Log.e(TAG, "onClick: " + e);
                Crashlytics.logException(e);
            }
        }
    }

    public void showTopContributors(String username, String overAllGold) {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityScrShareViewers.this);
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
        ImageView ivBroadcasterProfile = parentView.findViewById(R.id.iv_broadcaster_profile);
        TextView ivBroadcasterName = parentView.findViewById(R.id.broadcasterName);
        TextView ivBroadcasterGoldCount = parentView.findViewById(R.id.tv_broadcaster_gold_count);
        ImageView ivEffect = parentView.findViewById(R.id.iv_effect);
        progressBar = parentView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        ivBroadcasterName.setText(username);
        ivBroadcasterGoldCount.setText(overAllGold);

        if (!image.isEmpty()) {
            Picasso.get().load(image).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(ivBroadcasterProfile);
        } else {
            Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(ivBroadcasterProfile);
        }

        Glide.with(getApplicationContext())
                .load(broadcaster.getTools_applied())
                .into(ivEffect);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult: ");
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
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityScrShareViewers.this);
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
            LayoutInflater layoutInflater = LayoutInflater.from(ActivityScrShareViewers.this);
            final View dialogView = layoutInflater.inflate(R.layout.pop_up_manage, null);
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityScrShareViewers.this);

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

            LayoutInflater layoutInflater = LayoutInflater.from(ActivityScrShareViewers.this);
            final View dialogView = layoutInflater.inflate(R.layout.pop_up_report, null);
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityScrShareViewers.this);

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
            Intent intent = new Intent(ActivityScrShareViewers.this, ActivityTopFans.class);
            intent.putExtra("activationToken", audience.getActivation_code());
            intent.putExtra("user_id", audience.getUser_id());
            startActivity(intent);
        });

        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);
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
        Intent intent = new Intent(ActivityScrShareViewers.this, ActivityViewProfile.class);
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

}