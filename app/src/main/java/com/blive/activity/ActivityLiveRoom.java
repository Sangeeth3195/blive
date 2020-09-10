package com.blive.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.PointF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blive.model.GetTarget;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.core.content.res.ResourcesCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.se.omapi.SEService;
import android.text.InputFilter;
import android.text.TextPaint;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.blive.adapter.AdapterActiveViewers;
import com.blive.adapter.AdapterBroadcastToppers;
import com.blive.adapter.AdapterFriendsShare;
import com.blive.adapter.AdapterGifts;
import com.blive.adapter.AdapterGroupTopper;
import com.blive.adapter.AdapterImages;
import com.blive.adapter.AdapterMessage;
import com.blive.adapter.AdapterRequests;
import com.blive.adapter.Adaptertopperlist;
import com.blive.adapter.InviteListAdapter;
import com.blive.agora.AGEventHandler;
import com.blive.agora.AGLinearLayout;
import com.blive.agora.FaceBeautificationPopupWindow;
import com.blive.agora.GridVideoViewContainer;
import com.blive.agora.VideoStatusData;
import com.blive.BLiveApplication;
import com.blive.agora.rtmChat.ChatHandler;
import com.blive.agora.rtmChat.ChatManager;
import com.blive.constant.Constants_api;
import com.blive.constant.Constants_app;
import com.blive.custom.LikeAnimationView;
import com.blive.model.Audience;
import com.blive.model.AudienceResponse;
import com.blive.model.EntranceEffect;
import com.blive.model.FollowResponse;
import com.blive.model.GenericResponse;
import com.blive.model.Gift;
import com.blive.model.GiftMessage;
import com.blive.model.GiftResponse;
import com.blive.model.Giftrewards;
import com.blive.model.MessageBean;
import com.blive.model.PkGiftDetailsModel;
import com.blive.model.Pksession;
import com.blive.model.ProfileResponse;
import com.blive.model.TopFansResponse;
import com.blive.model.URL;
import com.blive.model.User;
import com.blive.R;
import com.blive.model.UsersResponse;
import com.blive.service.ApiClientBase;
import com.blive.service.ApiInterface;
import com.blive.service.ServiceGenerator;
import com.blive.service.linkshorten;
import com.blive.session.SessionUser;
import com.blive.utils.DividerLine;
import com.blive.utils.TransformImgCircle;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Optional;

import io.agora.rtc.Constants;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.models.UserInfo;
import io.agora.rtc.video.ChannelMediaInfo;
import io.agora.rtc.video.ChannelMediaRelayConfiguration;
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
import io.agora.rtm.SendMessageOptions;
import jp.wasabeef.picasso.transformations.BlurTransformation;
import retrofit2.Call;
import retrofit2.Response;

public class ActivityLiveRoom extends BaseActivity implements AGEventHandler, AdapterGifts.ListenerGift,
        AdapterImages.ListenerImage, AdapterRequests.Listener, AdapterBroadcastToppers.Listener, AdapterFriendsShare.Listener,
        AdapterGroupTopper.Listener, AdapterActiveViewers.ListenerActiveViers, AdapterMessage.ListenerMessage, InviteListAdapter.Listener {

    private FrameLayout root;
    private ImageView starRatings, starRatings1, ivGuestClose, ivGift, ivUser, ivFollow, ivGiftItem, ivImng, ivEntrance, ivDiamond, ivSwitchCam,
            ivVideoMute, ivOffline, ivJoin, ivPause, ivWebViewClose, bulletImg, ivBulletUserImg, ivGuestOne, ivGuestTwo,
            ivFreeGiftIcon, ivFreeDiamond, ivFreeGiftAchieve, ivUserProfileffect, ivFrame, ivFrameGuestTwo, button2, guest_image_gold1,
            guest_image_gold2, guest_image_gold3, image_gold3, image_gold2, image_gold1, disconnect, back_pk, msgSenderPicIv,
            pkTimeAccept, pkTimeReject, broadcasterWinLose, guestWinLose, broadcasterWinLose1, guestWinLose1, ivPkTopper, ivPkTopper_audience;
    protected Context context;
    ProgressBar disconnect_progress;
    private GridVideoViewContainer mGridVideoViewContainer;
    private TextView msgSenderUserNameTv, pkTimeMessageTv, tvTimerCount, guestcurrentgold, broadcurrentgold;
    private RelativeLayout pkTimeRequestRl, pkTimeOptionsRl, pk_layout, firsstlay, secondtlay;
    private final HashMap<Integer, SurfaceView> mUidsList = new HashMap<>();
    private RecyclerView rvMessages, rvRequests, rvImages, rvGift, rvFreeGift, rvAssets, rv_guestTopperList, rvFriendsList, rv_images_pk;
    private List<MessageBean> messageBeanList;
    private AdapterMessage adapter;
    private LinearLayout llChat, llGift, llNormalGift, llCombo, cvGiftMessage, llKickOut, ll_freeGiftlayer, ll_current_diamond, llUnFollow, llFollow, broad_topgift, guest_topgift;
    private String selectedPKTime = "5", channelName = "", selfName = "", image = "", broadcasterId = "", isFollowing = "", time = "", idelTime = "", broadTime = "", guestId = "", decodeImage = "", decodeImageGuest = "",
            moonValue = "", level = "", dailyAndWeeklyGold = "", freeGiftAnim = "", urlNew = "", errorUrl = "", removeGuestId = "", rematch = "0", channelrtm = "";
    private int channelUserCount, position = -1, size = 0, viewers = 11, likes = 0, gold = 0, oldGold = 0, temp = 0, temp1 = 0, temp2 = 0, guestUid = 0,
            broadcasterUid = 0, mStartIndex, dvalue = 0, page = 1, comboMultiplier = 0, lastPage = 0, oldMoonImage = 0, lastBroasTime = 0, freeGiftCount = 0,
            mPosition = -1, showWinLoseTime = 10000, guestresultgold = 0, broadcastresultgold = 0, totalgold = 2, guestgold = 1, broadcastgold = 1;
    private boolean isFirst = false, isLiked = false, isClose = false, isArrived = false, isAudioMuted = false,
            isBroadcastEnded = false, isRequested = false, isGoldAdding = false, isGiftShowing = false, isTextMuted = false, isAudienceFollowing = false,
            isBroadcaster = false, isAudience = false, isGuest = false, isEntranceEffects = false, isUserListEnd = false, isRefreshing = false,
            isAPICalled = false, isBroadcasterOffline = true, isVideoMute = false, isBroadcastMuted = false, isCallEnd = false, isCallRequested = false,
            flag = false, videoFlag = false, isClicked = false, isClickedProfile = false, isCounterEnable = false, isBulletenabled = false,
            isBroadcasterPaused = false, isRedirectScreen = false, isFollowingFrMessage = false, isThisGuest1 = false,
            isThisGuest2 = false, isAcceptRequest = true, isGuestRequest = false, isGuestVideoMuted = false, isCreated = true, pkRequestSent = false, ispknow = false,
            pkGuest = false, intermediateJoin = false, broadcasterAudience = false, timerReqSent = false, matchboole = false, timerStarted = false, global_pk = false,
            guestAudience = false, isGiftclick = false;
    public static int cRole = 0;
    public static boolean isGuest1 = false, isGuest2 = false;
    private ArrayList<PkGiftDetailsModel.Application> topperdetailsarray = new ArrayList<>();
    private ArrayList<PkGiftDetailsModel.Application> topperdetailsarray_guest = new ArrayList<>();
    private Gift mGift;
    private Integer pkTimer = 0;
    private LikeAnimationView mLikeAnimationView;
    private ArrayList<Bitmap> mAnimationItemList;
    private Animation slideUp, slideDown;
    private User broadcaster;
    private ArrayList<Gift> giftsList, giftTools, freeGifts;
    private ArrayList<Audience> audiences, guestAddedList;
    private ArrayList<RelativeLayout> rlVideoMutes;
    private ArrayList<ImageView> ivBlurList;
    private long startTime, endTime, onPauseStartTime, onResumeStopTime, totalIdelTime = 0;
    private ArrayList<User> users, usersFriendsList;
    private ArrayList<Audience> mAudiences, mRequests, mGuests;
    private ArrayList<String> messagesList;
    private AGLinearLayout bottomBroadcaster, bottomAudience;
    private AdapterRequests adapterRequests;
    private FloatingActionMenu fabMenu;
    private FloatingActionButton fabRequest /*,fabMic*/, fabBeauty;
    private TextView tvCurrentDiamondValue, tvCurrentFreeGiftValue, tvGiftName, tvCount, tvReceived, tvGift, tvAssets, tvFreeGifts, tvNoRequests, tvCountReq, tvEntranceName,
            tvMoonLevelCount, tvNoContributors, tvNoFriendsList, tvGiftMessage, tvGiftsList, tvAssetsList, tvFreeGiftsList, tvConnecting,
            tvConnectingTwo, tvOffline, tvPause, tvStarLevel, tvFreeGiftCount, tvBulletmsg, tvBulletName, count_tv, guesttext, broadtext;
    private ArrayList<GiftMessage> giftMessages;
    private ArrayList<EntranceEffect> entranceEffects;
    final Handler giftHandler = new Handler();
    final Handler entranceHandler = new Handler();
    final Handler callEndHandler = new Handler();
    final Handler hideIcon = new Handler();
    private Audience guest, guest2;
    private RelativeLayout rlGuestClose, rlLive, rl_notify, rlCoins, rlOffline, guestOne, guestTwo, rlPause, rlGuestTwoClose,
            rlVideoMuteGuestOne, rlVideoMuteGuestTwo, rl_star;
    private AdapterGifts adapterGifts;
    private LinearLayout rlBulletlay;
    private Audience tempGuest;
    private CardView cvRequests, cvWebView;
    private AdapterFriendsShare adapterFriends;
    public BottomSheetDialog bottomSheetDialog;
    private CheckBox checkBoxSelectAllFriends;
    private Spinner spCombo;
    private Button bSendGift, btnSendFriends, btnChangeAssests, pkTimeStartOptionBtn, pkStartBtn;
    private Button pkOption5, PkOption10, PkOption15, PkOption20;
    private View.OnClickListener ontimeSelect;
    private AlertDialog alertDialog;
    private ProgressBar progressBar;
    private FaceBeautificationPopupWindow mFaceBeautificationPopupWindow;
    private RtmClient mRtmClient;
    private ChatManager mChatManager;
    private RtmChannel mRtmChannel;
    private ChatHandler mChatHandler;
    private WebView webViewFreeGift;
    private EditText userTypedMessage;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler = new Handler();
    ArrayList<String> blockedlist = new ArrayList<>();
    private ArrayList<Audience> guestarraylist = new ArrayList<>();
    ArrayList<String> textmutelist = new ArrayList<>();
    private String guestclick = "";
    private SeekBar progressBar_seek1;

    int[] imageArray = {R.drawable.free_icon};
    int i = 0;
    int iamge = 0;

    // notification
    NotificationCompat.Builder mBuilder;
    NotificationManagerCompat notificationManager;
    int notificationId = 35667;
    String CHANNEL_ID = "my_channel_id";
    private String price = "", multiplier = "";

    private VideoEncoderConfiguration.VideoDimensions localVideoDimensions = null;
    private String gusetOneVideoMute = "", gusetTwoVideoMute = "";
    RelativeLayout rl_videoMuteGuestOne1;
    String url_url = "";
    boolean guestonemute = false;
    boolean guesttwomute = false;
    private List<ChannelMediaInfo> channelMediaInfos = new ArrayList<>();
    private ListView channelListView;
    private BottomSheetDialog dialog_friends;
    private ImageView ivpk;
    Dialog requestdialog, rewarddialog, dialog_winner;
    private String clicksdialog = "0";
    private String pkusername = "", pkname = "";
    private String pkuserid = "", pkid = "";
    private String pkimage = "";
    private String SessionId = "";
    private ArrayList<User> invitesUserList = new ArrayList<>();
    private InviteListAdapter inviteListAdapter;
    private String broadcast_type = "solo", pkdisplayname = "";
    private Integer pkTimeInt_int = 0;
    CountDownTimer countDownTimer = null;
    private TextView scroll;
    private String originallevel = "0";
    private boolean fromprofile = false;
    private CallbackManager callbackManager;

    @BindView(R.id.ivActiveViewers)
    ImageView ivActiveViewers;
    private String privatestatus = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_room);

        BLiveApplication.setCurrentActivity(this);
        changeStatusBarColor();
        callbackManager = CallbackManager.Factory.create();

        LocalBroadcastManager.getInstance(this).registerReceiver(pkGuestAccept, new IntentFilter("pkGuestAccept"));
        LocalBroadcastManager.getInstance(this).registerReceiver(pkGuestrequest, new IntentFilter("pkGuestrequest"));
        LocalBroadcastManager.getInstance(this).registerReceiver(pkReject, new IntentFilter("pkReject"));
        LocalBroadcastManager.getInstance(this).registerReceiver(PK_MESSAGE, new IntentFilter("PK_MESSAGE"));
        LocalBroadcastManager.getInstance(this).registerReceiver(pkSpecialGiftSent, new IntentFilter("pkSpecialGiftSent"));

    }

    private BroadcastReceiver pkSpecialGiftSent = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            new Handler().postDelayed(() -> {
                String requestdata = intent.getExtras().getString("data", "");
                Log.d(TAG, "onReceive: " + requestdata);
                BLiveApplication.getCurrentActivity().runOnUiThread(() -> {
                    scroll.setText(requestdata);
                    scroll.setVisibility(View.VISIBLE);
                    Dialog alertDialog = new Dialog(BLiveApplication.getCurrentActivity());
                    alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    Window window = alertDialog.getWindow();
                    WindowManager.LayoutParams wlp = window.getAttributes();

                    wlp.gravity = Gravity.CENTER;
                    wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                    window.setAttributes(wlp);
                    Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable
                            (Color.TRANSPARENT));
                    alertDialog.setContentView(R.layout.alert_pkspecialgift);
                    TextView tvWinner = alertDialog.findViewById(R.id.tv_winnerName);
                    TextView tvDiamondBack = alertDialog.findViewById(R.id.tv_diamondBack);
                    ImageView ivPkImg = alertDialog.findViewById(R.id.iv_diamond);
                    try {
                        Glide.with(ActivityLiveRoom.this).load(R.drawable.diamond_blast).apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true))
                                .into(ivPkImg);

                    } catch (Exception e) {
                        Log.i("autolog", "e: " + e.toString());
                    }
                    Button okBtn = alertDialog.findViewById(R.id.btn_ok);
                    String message = requestdata;

                    MessageBean message1 = new MessageBean("", SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + message, true, false, false);
                    messageBeanList.add(message1);
                    adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                    rvMessages.scrollToPosition(messageBeanList.size() - 1);
                    sendChannelMessage("PKGIFTFROM2020&&" + message);
                    String[] separated = message.split(" -- ");
                    tvWinner.setText(separated[0]);
                    tvDiamondBack.setText(separated[1]);
                    okBtn.setOnClickListener(v -> alertDialog.dismiss());
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    /*alertDialog.show();*/
                    new Handler().postDelayed(alertDialog::dismiss, 5000);

                    final Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        /*scroll.setVisibility(View.GONE);*/
                    }, 5000);
                });
            }, 4000);
        }
    };

    private BroadcastReceiver pkReject = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            pkRequestSent = false;
            ivpk.setVisibility(View.VISIBLE);
            disconnect_progress.setVisibility(View.GONE);
            showColorToast("Pk Request Got Rejected");
            if (dialog_friends.isShowing()) {
                dialog_friends.dismiss();
            }
        }
    };
    private BroadcastReceiver PK_MESSAGE = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String requestdata = intent.getExtras().getString("data", "");
            String from = intent.getExtras().getString("from", "");
        }
    };
    private BroadcastReceiver pkGuestrequest = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            Log.d(TAG, "onReceive: " + "pkGuestrequest");

            if (isBroadcaster && !ispknow) {
                findViewById(R.id.iv_pk).setVisibility(View.VISIBLE);
                findViewById(R.id.menu).setZ(2.0f);
                String requestdata = intent.getExtras().getString("data", "");
                try {
                    JSONObject obj = new JSONObject(requestdata);
                    String name = obj.getString("user_name");
                    String image = obj.getString("sender_pic");
                    String pkuserid_ = obj.getString("user_id");
                    String pkdisplayname1 = obj.getString("user_fname");

                    requestdialog.setTitle("Pk");
                    requestdialog.setCancelable(false);
                    requestdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    requestdialog.setContentView(R.layout.requestlayout);
                    Window window = requestdialog.getWindow();
                    WindowManager.LayoutParams wlp = window.getAttributes();
                    wlp.gravity = Gravity.CENTER;
                    wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                    window.setAttributes(wlp);
                    if (!pkRequestSent) {
                        requestdialog.show();
                    }
                    TextView text_name = requestdialog.findViewById(R.id.text_name);
                    Button accept_request = requestdialog.findViewById(R.id.accept_request);
                    Button reject_request = requestdialog.findViewById(R.id.reject_request);
                    ImageView image_profile = requestdialog.findViewById(R.id.image_profile);
                    try {
                        Glide.with(ActivityLiveRoom.this)
                                .load(image)
                                .into(image_profile);
                    } catch (Exception e) {
                        Crashlytics.log(e.toString());
                    }
                    text_name.setText(pkdisplayname1 + " Has Invited You For PK Challenge");
                    new Handler().postDelayed(() -> {
                        if (!clicksdialog.equalsIgnoreCase("1")) {
                            JSONObject acceptjson = new JSONObject();
                            try {
                                acceptjson.put("request_type", "PK_REJECTED");
                                acceptjson.put("sender_name", SessionUser.getUser().getUsername());
                                sendPeerMessage(name, acceptjson.toString());
                                if (requestdialog.isShowing()) {
                                    requestdialog.dismiss();
                                }
                            } catch (Exception e) {
                                Crashlytics.log(e.toString());
                            }
                        } else {
                        }
                    }, 5000);
                    accept_request.setOnClickListener(v -> {
                        JSONObject messageParams = new JSONObject();
                        try {
                            messageParams.put("pkGuestAccept", "accepted");
                            messageParams.put("user_id", SessionUser.getUser().getUser_id());
                            messageParams.put("user_id", SessionUser.getUser().getUser_id());
                            messageParams.put("user_name", SessionUser.getUser().getUsername());
                            messageParams.put("name", SessionUser.getUser().getName());
                            messageParams.put("user_image", SessionUser.getUser().getProfile_pic());
                            clicksdialog = "1";
                            disconnect.setVisibility(View.VISIBLE);
                            pkRequestSent = false;
                            ispknow = true;
                            pkGuest = true;
                            isBroadcaster = false;
                            pkusername = name;
                            pkdisplayname = pkdisplayname1;
                            getAllListViewInfos(pkusername);
                            pkvisible();
                            pkStartBtn.setVisibility(View.VISIBLE);
                            JSONObject messageParams1 = new JSONObject();
                            messageParams1.put("user_id", obj.getString("user_id"));
                            messageParams1.put("PKuser_id", SessionUser.getUser().getUser_id());
                            messageParams1.put("user_name", obj.getString("user_name"));
                            messageParams1.put("user_image", obj.getString("user_image"));
                            messageParams1.put("msg", " broadcaster joined pk request");
                            sendChannelMessage(SessionUser.getUser().getUser_id() + "broadcaster joined pk request" + messageParams1.toString());
                            sendPeerMessage(name, messageParams.toString());
                            fabMenu.setZ(2.0f);
                            joinrtmchannel(name, "0");
                            pkusername = name;
                            pkuserid = pkuserid_;
                            pkid = pkuserid_;
                            Log.i("autolog", "messageParams1: " + messageParams1);
                            Log.d(TAG, "onReceive: " + pkusername + "pkuserid" + pkuserid + "pkid" + pkid);
                            pkStartBtn.setVisibility(View.VISIBLE);
                            requestdialog.dismiss();
                        } catch (JSONException e) {
                            Log.e(TAG, "onReceive: " + e.toString());
                            e.printStackTrace();
                        }
                    });
                    reject_request.setOnClickListener(v -> {
                        JSONObject acceptjson = new JSONObject();
                        clicksdialog = "1";
                        try {
                            acceptjson.put("request_type", "PK_REJECTED");
                            acceptjson.put("sender_name", SessionUser.getUser().getUsername());
                            sendPeerMessage(name, acceptjson.toString());
                            requestdialog.dismiss();
                        } catch (Exception e) {
                            Crashlytics.log(e.toString());
                        }
                    });
                } catch (JSONException e) {
                    Log.i("autolog", "e: " + e);
                    e.printStackTrace();
                }
            }
        }
    };

    private BroadcastReceiver pkGuestAccept = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            Log.d(TAG, "onReceive: " + "pkGuestAccept");
            pkRequestSent = false;
            findViewById(R.id.menu).setZ(2.0f);
            disconnect.setVisibility(View.VISIBLE);
            ispknow = true;
            pkGuest = false;
            isBroadcaster = true;
            String data = intent.getExtras().getString("data", "");
            try {
                JSONObject obj1 = new JSONObject(data);
                pkuserid = obj1.getString("user_id");
                pkusername = obj1.getString("user_name");
                pkdisplayname = obj1.getString("name");
                Log.i("autolog", "pkusername: " + pkusername + "pkuserid: " + pkuserid);
                pkimage = obj1.getString("user_image");
                getAllListViewInfos(pkusername);
                sendChannelMessage(" Moved to pk challenge" + pkuserid);
                ispknow = true;
                pkStartBtn.setVisibility(View.VISIBLE);
                if (dialog_friends.isShowing()) {
                    dialog_friends.dismiss();
                }
                fabMenu.setZ(2.0f);
                /*callMatchPkChallenge(broadcasterId, pkuserid, SessionUser.getUser().getUsername());*/


                if (utils.isNetworkAvailable()) {
                    final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
                    Call<Pksession> call = apiClient.startPkSession(broadcasterId, pkuserid, SessionUser.getUser().getUsername());
                    call.enqueue(new retrofit2.Callback<Pksession>() {
                        @Override
                        public void onResponse(@NonNull Call<Pksession> call, @NonNull Response<Pksession> response) {
                            Pksession genericResponse = response.body();
                            if (response.code() == 200) {
                                if (genericResponse != null) {
                                    Log.d(TAG, "onResponse: " + "generic" + genericResponse.getStatus() + response.code());
                                    if (genericResponse.getStatus().equalsIgnoreCase("success")) {
                                        SessionId = genericResponse.getSessionId();
                                        sendChannelMessage("sessionid=" + SessionId);
                                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Session_Id", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("session_Id", genericResponse.getSessionId());
                                        editor.commit();
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
                        public void onFailure(@NonNull Call<Pksession> call, @NonNull Throwable t) {
                            showToast(t.getMessage());
                        }
                    });
                }
            } catch (JSONException e) {
                Log.d(TAG, "onReceive: " + e.toString());
                e.printStackTrace();
            }
        }
    };

    private void initViews() {
        try {
            rlVideoMutes = new ArrayList<>();
            ivBlurList = new ArrayList<>();
            blockedlist.clear();
            guestarraylist.clear();
            SessionUser.isScreenSharing(true);
            ivPkTopper = findViewById(R.id.iv_pkTopper);
            ivPkTopper_audience = findViewById(R.id.iv_pkTopper_audience);
            llChat = findViewById(R.id.ll_chat);
            button2 = findViewById(R.id.btn_2);
            image_gold1 = findViewById(R.id.image_gold1);
            image_gold2 = findViewById(R.id.image_gold2);
            image_gold3 = findViewById(R.id.image_gold3);
            guest_image_gold1 = findViewById(R.id.guest_image_gold1);
            guest_image_gold3 = findViewById(R.id.guest_image_gold3);
            guest_image_gold2 = findViewById(R.id.guest_image_gold2);
            broadtext = findViewById(R.id.broadtext);
            guesttext = findViewById(R.id.guesttext);
            disconnect = findViewById(R.id.disconnect);
            disconnect_progress = findViewById(R.id.disconnect_progress);
            back_pk = findViewById(R.id.back_pk);
            firsstlay = findViewById(R.id.firsstlay);
            secondtlay = findViewById(R.id.secondtlay);
            broadcurrentgold = findViewById(R.id.broadcurrentgold);
            guestcurrentgold = findViewById(R.id.guestcurrentgold);
            pkStartBtn = findViewById(R.id.pk_start_btn1);
            pk_layout = findViewById(R.id.pk_layout);
            guest_topgift = findViewById(R.id.guest_topgift);
            broad_topgift = findViewById(R.id.broad_topgift);
            rv_images_pk = findViewById(R.id.rv_images_pk);
            pkTimeStartOptionBtn = findViewById(R.id.start_pk_option);
            pkTimeOptionsRl = findViewById(R.id.pk_time_layout1);
            pkTimeRequestRl = findViewById(R.id.pk_time_request_rl1);
            msgSenderPicIv = findViewById(R.id.senderPicIv);
            msgSenderUserNameTv = findViewById(R.id.userNameTv);
            pkTimeMessageTv = findViewById(R.id.messageTextV);
            pkTimeAccept = findViewById(R.id.btnAccept1);
            pkTimeReject = findViewById(R.id.btnReject1);
            broadcasterWinLose = findViewById(R.id.iv_broadcaster_winlose);
            guestWinLose = findViewById(R.id.iv_guest_winlose);
            broadcasterWinLose1 = findViewById(R.id.iv_broadcaster_winlose1);
            guestWinLose1 = findViewById(R.id.iv_guest_winlose1);
            firsstlay.setVisibility(View.GONE);
            secondtlay.setVisibility(View.GONE);
            progressBar_seek1 = findViewById(R.id.progressBar);
            pkOption5 = findViewById(R.id.pk_time_op_5);
            PkOption10 = findViewById(R.id.pk_time_op_10);
            PkOption15 = findViewById(R.id.pk_time_op_15);
            PkOption20 = findViewById(R.id.pk_time_op_20);
            tvTimerCount = findViewById(R.id.tv_timer_count);

            ivGuestOne = findViewById(R.id.iv_guestOne);
            ivGuestTwo = findViewById(R.id.iv_guestTwo);
            rlVideoMuteGuestOne = findViewById(R.id.rl_videoMuteGuestOne);
            rlVideoMuteGuestTwo = findViewById(R.id.rl_videoMuteGuestTwo);
            rl_star = findViewById(R.id.rl_star);
            ivFrameGuestTwo = findViewById(R.id.iv_frame_Guest_Two);
            rlGuestTwoClose = findViewById(R.id.rl_guest_two);
            guestTwo = findViewById(R.id.second_guest);
            ivFrame = findViewById(R.id.iv_frame_Guest_One);
            btnChangeAssests = findViewById(R.id.btn_changeAssests);
            ll_current_diamond = findViewById(R.id.ll_current_diamond);
            ll_freeGiftlayer = findViewById(R.id.ll_freeGiftlayer);
            guestOne = findViewById(R.id.first_guest);
            ivJoin = findViewById(R.id.iv_join);
            rlPause = findViewById(R.id.rl_pause);
            ivPause = findViewById(R.id.iv_pause);
            tvOffline = findViewById(R.id.tv_offline);
            tvPause = findViewById(R.id.tv_pause);
            tvConnecting = findViewById(R.id.tv_connecting);
            tvConnectingTwo = findViewById(R.id.tv_connecting_Two);
            tvGiftsList = findViewById(R.id.tv_GiftList);
            tvAssetsList = findViewById(R.id.tv_Assets);
            tvFreeGiftsList = findViewById(R.id.tv_FreeGifts);
            bSendGift = findViewById(R.id.bSendGift);
            tvCurrentDiamondValue = findViewById(R.id.tvCurrentDiamondValue);
            tvCurrentFreeGiftValue = findViewById(R.id.tvCurrentFreeGiftValue);
            llGift = findViewById(R.id.ll_gift);
            bottomBroadcaster = findViewById(R.id.bottom_broadcaster);
            bottomAudience = findViewById(R.id.bottom_audience);
            rvGift = findViewById(R.id.rv_gift);
            fabMenu = findViewById(R.id.menu);
            fabRequest = findViewById(R.id.fb_request);
            fabBeauty = findViewById(R.id.fb_beauty);
            starRatings = findViewById(R.id.moonLevelStar);
            starRatings1 = findViewById(R.id.moonLevelStar1);
            rvRequests = findViewById(R.id.rvRequests);
            rvImages = findViewById(R.id.rv_images);
            rvFreeGift = findViewById(R.id.rv_freeGift);
            rvAssets = findViewById(R.id.rv_assets);
            root = findViewById(R.id.root);
            ivGift = findViewById(R.id.iv_gift);
            rlGuestClose = findViewById(R.id.rl_guestClose);
            ivGuestClose = findViewById(R.id.iv_GuestClose);
            cvRequests = findViewById(R.id.cvRequests);
            rlLive = findViewById(R.id.rl_live);
            rl_notify = findViewById(R.id.rl_notify);
            rlOffline = findViewById(R.id.rl_offline);
            rlCoins = findViewById(R.id.rl_coins);
            tvGiftName = findViewById(R.id.tv_gift_name);
            tvCount = findViewById(R.id.tv_count);
            tvReceived = findViewById(R.id.tv_received);
            tvGift = findViewById(R.id.tv_gift);
            tvAssets = findViewById(R.id.tv_assets);
            tvFreeGifts = findViewById(R.id.tv_freeGifts);
            tvNoRequests = findViewById(R.id.tv_no_requests);
            tvCountReq = findViewById(R.id.tv_count_req);
            tvEntranceName = findViewById(R.id.tv_entranceName);
            ivUser = findViewById(R.id.iv_user);
            ivFollow = findViewById(R.id.iv_follow);
            ivGiftItem = findViewById(R.id.iv_gift_item);
            ivImng = findViewById(R.id.iv);
            ivEntrance = findViewById(R.id.iv_entrance);
            ivDiamond = findViewById(R.id.iv_diamond);
            ivFreeDiamond = findViewById(R.id.iv_free_diamond);
            llNormalGift = findViewById(R.id.ll_normal_gift);
            tvMoonLevelCount = findViewById(R.id.tvMoonLevelCount);
            ivSwitchCam = findViewById(R.id.btn_2);
            tvGiftMessage = findViewById(R.id.tv_gift_message);
            cvGiftMessage = findViewById(R.id.cv_gift_message);
            llCombo = findViewById(R.id.ll_combo);
            ivVideoMute = findViewById(R.id.iv_videoMute);
            ivOffline = findViewById(R.id.iv_offline);
            spCombo = findViewById(R.id.spCombo);
            tvStarLevel = findViewById(R.id.tv_star_level);
            ivFreeGiftIcon = findViewById(R.id.iv_free_gift_img);
            tvFreeGiftCount = findViewById(R.id.tv_freeGift_count);
            cvWebView = findViewById(R.id.cv_webView);
            webViewFreeGift = findViewById(R.id.webView_freeGift);
            ivWebViewClose = findViewById(R.id.iv_webView_Close);
            bulletImg = findViewById(R.id.bt_bullet);
            rlBulletlay = findViewById(R.id.ll_bullet);
            userTypedMessage = findViewById(R.id.userMessageBox);
            tvBulletmsg = findViewById(R.id.tv_bullet_msg);
            tvBulletName = findViewById(R.id.tv_bullet_msg_name);
            ivBulletUserImg = findViewById(R.id.iv_bullet_user);
            ivFreeGiftAchieve = findViewById(R.id.iv_free_gift_achieve);
            rvMessages = findViewById(R.id.message_list);
            rl_videoMuteGuestOne1 = findViewById(R.id.rl_videoMuteGuestOne1);
            ivpk = findViewById(R.id.iv_pk);
            cvWebView.setVisibility(View.GONE);
            rlBulletlay.setVisibility(View.GONE);
            ivUserProfileffect = findViewById(R.id.iv_profile_dp_affect);
            int level = Integer.parseInt(broadcaster.getLevel());
            rlVideoMutes.add(rlVideoMuteGuestOne);
            rlVideoMutes.add(rlVideoMuteGuestTwo);
            ivBlurList.add(ivGuestOne);
            ivBlurList.add(ivGuestTwo);
            scroll = findViewById(R.id.textScrolling);
            scroll.setSelected(true);

            /*TextPaint paint = scroll.getPaint();
            float width = paint.measureText("Tianjin, China");

            Shader textShader = new LinearGradient(0, 0, width, scroll.getTextSize(),
                    new int[]{
                            Color.parseColor("#F97C3C"),
                            Color.parseColor("#FDB54E"),
                            Color.parseColor("#64B678"),
                            Color.parseColor("#478AEA"),
                            Color.parseColor("#8446CC"),
                    }, null, Shader.TileMode.CLAMP);
            scroll.getPaint().setShader(textShader);*/

            dialog_friends = new BottomSheetDialog(this);
            requestdialog = new Dialog(this);
            rewarddialog = new Dialog(this);
            dialog_winner = new Dialog(this);

            originallevel = SessionUser.getUser().getLevel();

            ivActiveViewers.setOnClickListener(v -> loadInviteListsFriendsPrivate());

            rlVideoMuteGuestTwo.setVisibility(View.GONE);

            ivPkTopper.setVisibility(View.GONE);
            if (cRole == 1) {
                ivpk.setVisibility(View.VISIBLE);
            }


            swipeRefreshLayout = findViewById(R.id.swipeRefresh_FreeGift);
            swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

            swipeRefreshLayout.setOnRefreshListener(() -> {
                webViewFreeGift.loadUrl(urlNew);
            });

            ivPkTopper.setOnClickListener(v -> {
                cvWebView.setVisibility(View.VISIBLE);
                webViewFreeGift.loadUrl(Constants_api.pk_GiftTopperList);
                webViewFreeGift.getSettings().setSupportMultipleWindows(true);
                webViewFreeGift.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                webViewFreeGift.getSettings().setJavaScriptEnabled(true);
                webViewFreeGift.getSettings().setDomStorageEnabled(true);
                webViewFreeGift.getSettings().setLoadWithOverviewMode(true);
                webViewFreeGift.getSettings().setUseWideViewPort(true);
                webViewFreeGift.getSettings().setDomStorageEnabled(true);
                /*webViewFreeGift.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);*/
                webViewFreeGift.getSettings().setDefaultTextEncodingName("utf-8");
                webViewFreeGift.getSettings().setPluginState(WebSettings.PluginState.ON);
                webViewFreeGift.setWebViewClient(new WebViewClient() {

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {

                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        if (!url.equalsIgnoreCase(errorUrl)) {
                            swipeRefreshLayout.setVisibility(View.VISIBLE);
                            if (swipeRefreshLayout.isRefreshing())
                                swipeRefreshLayout.setRefreshing(false);
                            swipeRefreshLayout.setVisibility(View.VISIBLE);
                            urlNew = view.getUrl();
                        }
                    }

                    @Override
                    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                        errorUrl = request.getUrl().toString();
                    }

                    @Override
                    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(ActivityLiveRoom.this);
                        builder.setMessage(R.string.notification_error_ssl_cert_invalid);
                        builder.setPositiveButton("continue", (dialog, which) -> handler.proceed());
                        builder.setNegativeButton("cancel", (dialog, which) -> handler.cancel());
                        final AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
            });
            ivPkTopper_audience.setOnClickListener(v -> {
                cvWebView.setVisibility(View.VISIBLE);
                webViewFreeGift.loadUrl(Constants_api.pk_GiftTopperList);
                webViewFreeGift.getSettings().setSupportMultipleWindows(true);
                webViewFreeGift.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                webViewFreeGift.getSettings().setJavaScriptEnabled(true);
                webViewFreeGift.getSettings().setDomStorageEnabled(true);
                webViewFreeGift.getSettings().setLoadWithOverviewMode(true);
                webViewFreeGift.getSettings().setUseWideViewPort(true);
                webViewFreeGift.getSettings().setDomStorageEnabled(true);
                /*webViewFreeGift.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);*/
                webViewFreeGift.getSettings().setDefaultTextEncodingName("utf-8");
                webViewFreeGift.getSettings().setPluginState(WebSettings.PluginState.ON);
                webViewFreeGift.setWebViewClient(new WebViewClient() {

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {

                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        if (!url.equalsIgnoreCase(errorUrl)) {
                            swipeRefreshLayout.setVisibility(View.VISIBLE);
                            if (swipeRefreshLayout.isRefreshing())
                                swipeRefreshLayout.setRefreshing(false);
                            swipeRefreshLayout.setVisibility(View.VISIBLE);
                            urlNew = view.getUrl();
                        }
                    }

                    @Override
                    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                        errorUrl = request.getUrl().toString();
                    }

                    @Override
                    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(ActivityLiveRoom.this);
                        builder.setMessage(R.string.notification_error_ssl_cert_invalid);
                        builder.setPositiveButton("continue", (dialog, which) -> handler.proceed());
                        builder.setNegativeButton("cancel", (dialog, which) -> handler.cancel());
                        final AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
            });

            progressBar_seek1 = findViewById(R.id.progressBar);
            if (pkTimer <= 0) {
                tvTimerCount.setVisibility(View.GONE);
            }

            if (intermediateJoin) {
                if (pkTimer > 2) {
                    new Handler().postDelayed(() -> {
                        Log.d("PK", "pk time left  starting a timer ");
                        startTimer(pkTimer - 2);
                        GiftUpdateAPI();
                        ispknow = true;
                    }, 2000);
                }
            } else {

            }

            swipeRefreshLayout.setOnRefreshListener(() -> {
                webViewFreeGift.loadUrl(urlNew);
            });

            progressBar_seek1.setOnTouchListener((v, event) -> true);

            ivpk.setOnClickListener(v ->
                    loadInviteLists());

            disconnect.setOnClickListener(v -> showdisconnect());

            back_pk.setOnClickListener(v -> {
                if (pkTimeOptionsRl.getVisibility() == View.VISIBLE) {
                    pkTimeOptionsRl.setVisibility(View.GONE);
                    pkStartBtn.setVisibility(View.VISIBLE);
                }
            });
            llGift.setOnClickListener(v -> {

            });

            pkStartBtn.setOnClickListener(view -> {
                pkTimeOptionsRl.setVisibility(View.VISIBLE);
                pkStartBtn.setVisibility(View.GONE);
                pkTimeOptionsRl.setZ(186.0f);
                rvImages.setZ(-2.0f);
                rvMessages.setZ(-2.0f);
                fabMenu.setZ(2.0f);
                matchboole = true;
            });

            pkTimeStartOptionBtn.setOnClickListener(view -> {
                pkTimeOptionsRl.setVisibility(View.GONE);
                fabMenu.setZ(40.0f);
                rvMessages.setZ(-2.0f);
                sendPkMessage(selectedPKTime + " minutes Pk requested");
                pkStartBtn.setVisibility(View.GONE);
                timerReqSent = true;
            });
            pkTimeOptionsRl.setOnClickListener(view -> {

            });

            broad_topgift.setOnClickListener(v -> {
                if (isBroadcaster || broadcasterAudience) {
                    if (topperdetailsarray.size() != 0) {
                        showtoppers(topperdetailsarray);
                    }
                } else {
                    if (topperdetailsarray_guest.size() != 0) {
                        showtoppers(topperdetailsarray_guest);
                    }
                }
            });

            guest_topgift.setOnClickListener(v -> {
                if (isBroadcaster || broadcasterAudience) {
                    if (topperdetailsarray_guest.size() != 0) {
                        showtoppers(topperdetailsarray_guest);
                    }
                } else {
                    if (topperdetailsarray.size() != 0) {
                        showtoppers(topperdetailsarray);
                    }
                }
            });

            secondtlay.setOnClickListener((View view) -> {
                Log.i("autolog", "pkuserid: " + pkuserid);
                if (llGift.getVisibility() == View.VISIBLE) {
                    llGift.startAnimation(slideDown);
                    llGift.setVisibility(View.GONE);
                } else {
                    if (broadcasterAudience || isBroadcaster) {
                        getClickedProfileData(pkuserid);
                    } else if (pkGuest) {
                        getClickedProfileData(pkuserid);
                    } else {
                        getClickedProfileData(broadcasterId);
                    }
                }
            });

            firsstlay.setOnClickListener((View view) -> {
                if (llGift.getVisibility() == View.VISIBLE) {
                    llGift.startAnimation(slideDown);
                    llGift.setVisibility(View.GONE);
                } else {
                    if (broadcasterAudience || isBroadcaster) {
                        getClickedProfileData(broadcasterId);
                    } else if (pkGuest) {
                        getClickedProfileData(pkuserid);
                    } else {
                        getClickedProfileData(pkuserid);
                    }
                }
            });

            String[] pktimeOptions = getResources().getStringArray(R.array.pk_timeframes);
            if (Integer.parseInt(selectedPKTime) == Integer.parseInt(pktimeOptions[0])) {
                pkOption5.setTextColor(Color.parseColor("#FFFFFF"));
                PkOption10.setTextColor(Color.parseColor("#000000"));
                PkOption15.setTextColor(Color.parseColor("#000000"));
                PkOption20.setTextColor(Color.parseColor("#000000"));
                pkOption5.setBackground(getDrawable(R.drawable.pk_option_checked));
                PkOption10.setBackground(getDrawable(R.drawable.pk_option_normal));
                PkOption15.setBackground(getDrawable(R.drawable.pk_option_normal));
                PkOption20.setBackground(getDrawable(R.drawable.pk_option_normal));
            } else if (Integer.parseInt(selectedPKTime) == Integer.parseInt(pktimeOptions[1])) {
                pkOption5.setTextColor(Color.parseColor("#000000"));
                PkOption10.setTextColor(Color.parseColor("#FFFFFF"));
                PkOption15.setTextColor(Color.parseColor("#000000"));
                PkOption20.setTextColor(Color.parseColor("#000000"));
                pkOption5.setBackground(getDrawable(R.drawable.pk_option_normal));
                PkOption15.setBackground(getDrawable(R.drawable.pk_option_normal));
                PkOption10.setBackground(getDrawable(R.drawable.pk_option_checked));
                PkOption20.setBackground(getDrawable(R.drawable.pk_option_normal));
            } else if (Integer.parseInt(selectedPKTime) == Integer.parseInt(pktimeOptions[2])) {
                pkOption5.setTextColor(Color.parseColor("#000000"));
                PkOption10.setTextColor(Color.parseColor("#000000"));
                PkOption15.setTextColor(Color.parseColor("#FFFFFF"));
                PkOption20.setTextColor(Color.parseColor("#000000"));
                pkOption5.setBackground(getDrawable(R.drawable.pk_option_normal));
                PkOption10.setBackground(getDrawable(R.drawable.pk_option_normal));
                PkOption15.setBackground(getDrawable(R.drawable.pk_option_checked));
                PkOption20.setBackground(getDrawable(R.drawable.pk_option_normal));
            } else if (Integer.parseInt(selectedPKTime) == Integer.parseInt(pktimeOptions[3])) {
                pkOption5.setTextColor(Color.parseColor("#000000"));
                PkOption10.setTextColor(Color.parseColor("#000000"));
                PkOption15.setTextColor(Color.parseColor("#000000"));
                PkOption20.setTextColor(Color.parseColor("#FFFFFF"));
                pkOption5.setBackground(getDrawable(R.drawable.pk_option_normal));
                PkOption10.setBackground(getDrawable(R.drawable.pk_option_normal));
                PkOption15.setBackground(getDrawable(R.drawable.pk_option_normal));
                PkOption20.setBackground(getDrawable(R.drawable.pk_option_checked));
            }

            ontimeSelect = view -> {
                if (view == pkOption5) {
                    pkOption5.setTextColor(Color.parseColor("#FFFFFF"));
                    PkOption10.setTextColor(Color.parseColor("#000000"));
                    PkOption15.setTextColor(Color.parseColor("#000000"));
                    PkOption20.setTextColor(Color.parseColor("#000000"));
                    view.setBackground(getDrawable(R.drawable.pk_option_checked));
                    PkOption10.setBackground(getDrawable(R.drawable.pk_option_normal));
                    PkOption15.setBackground(getDrawable(R.drawable.pk_option_normal));
                    PkOption20.setBackground(getDrawable(R.drawable.pk_option_normal));
                    selectedPKTime = getResources().getStringArray(R.array.pk_timeframes)[0];
                } else if (view == PkOption10) {
                    pkOption5.setTextColor(Color.parseColor("#000000"));
                    PkOption10.setTextColor(Color.parseColor("#FFFFFF"));
                    PkOption15.setTextColor(Color.parseColor("#000000"));
                    PkOption20.setTextColor(Color.parseColor("#000000"));
                    view.setBackground(getDrawable(R.drawable.pk_option_checked));
                    pkOption5.setBackground(getDrawable(R.drawable.pk_option_normal));
                    PkOption15.setBackground(getDrawable(R.drawable.pk_option_normal));
                    PkOption20.setBackground(getDrawable(R.drawable.pk_option_normal));
                    selectedPKTime = getResources().getStringArray(R.array.pk_timeframes)[1];
                } else if (view == PkOption15) {
                    pkOption5.setTextColor(Color.parseColor("#000000"));
                    PkOption10.setTextColor(Color.parseColor("#000000"));
                    PkOption15.setTextColor(Color.parseColor("#FFFFFF"));
                    PkOption20.setTextColor(Color.parseColor("#000000"));
                    view.setBackground(getDrawable(R.drawable.pk_option_checked));
                    pkOption5.setBackground(getDrawable(R.drawable.pk_option_normal));
                    PkOption10.setBackground(getDrawable(R.drawable.pk_option_normal));
                    PkOption20.setBackground(getDrawable(R.drawable.pk_option_normal));
                    selectedPKTime = getResources().getStringArray(R.array.pk_timeframes)[2];
                } else if (view == PkOption20) {
                    pkOption5.setTextColor(Color.parseColor("#000000"));
                    PkOption10.setTextColor(Color.parseColor("#000000"));
                    PkOption15.setTextColor(Color.parseColor("#000000"));
                    PkOption20.setTextColor(Color.parseColor("#FFFFFF"));
                    view.setBackground(getDrawable(R.drawable.pk_option_checked));
                    pkOption5.setBackground(getDrawable(R.drawable.pk_option_normal));
                    PkOption10.setBackground(getDrawable(R.drawable.pk_option_normal));
                    PkOption15.setBackground(getDrawable(R.drawable.pk_option_normal));
                    selectedPKTime = getResources().getStringArray(R.array.pk_timeframes)[3];
                }
            };

            pkOption5.setOnClickListener(ontimeSelect);
            PkOption10.setOnClickListener(ontimeSelect);
            PkOption15.setOnClickListener(ontimeSelect);
            PkOption20.setOnClickListener(ontimeSelect);

            ivFollow.setVisibility(View.GONE);

            Glide.with(getApplicationContext())
                    .load(SessionUser.getUser().getTools_applied())
                    .into(ivUserProfileffect);

            Glide.with(getApplicationContext())
                    .load(broadcaster.getTools_applied())
                    .into(ivUserProfileffect);

            ivpk.setOnClickListener(v -> loadInviteLists());

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.combo_array, R.layout.item_spinner);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spCombo.setAdapter(adapter);
            spCombo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.i("autolog", "parent: " + parent.getAdapter().getItem(position).toString());
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.white));
                    ((TextView) parent.getChildAt(0)).setTextSize(15);
                    comboMultiplier = Integer.parseInt(parent.getAdapter().getItem(position).toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        } catch (Exception e) {
            Log.i("autolog", "e: " + e.toString());
        }

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
        try {
            event().addEventHandler(this);
            Intent i = getIntent();
            cRole = i.getIntExtra(Constants_app.ACTION_KEY_CROLE, 0);
            Log.i("autolog", "cRole: " + cRole);
            channelName = i.getStringExtra("name");
            selfName = i.getStringExtra("selfname");
            channelUserCount = i.getIntExtra("usercount", 0);
            image = i.getStringExtra("image");
            broadcasterId = i.getStringExtra("broadcasterId");
            /*isFollowing = i.getStringExtra("isFollowing");*/
            isFollowing = "no";
            broadcaster = i.getParcelableExtra("broadcaster");
            position = i.getIntExtra("position", -1);
            /*users = i.getParcelableArrayListExtra("users");*/
            intermediateJoin = i.getBooleanExtra("intermediateJoin", false);
            broadcasterAudience = i.getBooleanExtra("broadcasterAudience", false);
            guestAudience = i.getBooleanExtra("guestAudience", false);
            pkTimer = i.getIntExtra("pkTimer", 0);
            pkuserid = i.getStringExtra("PKuserId");
            Log.i("autolog", "pkuserid: " + pkuserid);
            channelrtm = i.getStringExtra("rtmname");
            Log.i("autolog", "channelrtm: " + channelrtm);
            try {
                privatestatus = ActivityStreamSet.status;
            } catch (Exception e) {
                privatestatus = "";
            }
            if (cRole == 0) {
                Log.e(TAG, "initViews: " + privatestatus);
                if (privatestatus.equalsIgnoreCase("PRIVATE")) {
                    ivActiveViewers.setVisibility(View.VISIBLE);
                    ivpk.setVisibility(View.GONE);
                } else {
                    ivActiveViewers.setVisibility(View.GONE);
                }

            }
            initViews();
            try {
                broadcast_type = i.getStringExtra("broad_type");
                if (broadcast_type.equalsIgnoreCase("solo")) {
                    guestAudience = false;
                    broadcasterAudience = false;
                    ispknow = false;
                    pkgone();
                }
                if (broadcast_type.equalsIgnoreCase("solo_viewprofile")) {
                    fromprofile = true;
                    guestAudience = false;
                    broadcasterAudience = false;
                    ispknow = false;
                    pkgone();
                }
                if (broadcast_type.equalsIgnoreCase("pk_viewprofile")) {
                    fromprofile = true;
                }
            } catch (Exception e) {
                Log.d(TAG, "initUI: " + e.toString());
            }
            Log.d(TAG, "initUI: " + broadcast_type);


            if (broadcasterAudience || guestAudience) {
                ispknow = true;
            } else {
                if (broadcast_type.equalsIgnoreCase("solo")) {
                    pkgone();
                }
            }

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
                    Log.e(TAG, "onLoginFailed: " + errorInfo.getErrorDescription());
                    mChatManager.doLogin(channelrtm);
                    /*joinrtmchannel(channelrtm,"o");*/
                }

                @Override
                public void onChannelJoinSuccess() {
                    Log.e(TAG, "onRTMChannelJoinSuccess: ");
                }

                @Override
                public void onChannelJoinFailed(ErrorInfo errorCode) {
                    Log.e(TAG, "onRTMChannelJoinFailed: ");
                    /*mChatManager.doLogin(channelrtm);*/
                    Log.i("autolog-fromlive_bro", "mChannelName: " + channelrtm);
                    joinrtmchannel(channelrtm, "o");
                }

                @Override
                public void onMessageReceived(RtmMessage message, RtmChannelMember fromMember) {
                    Log.e(TAG, "onRTMMessageReceived: account = " + fromMember.getUserId() + " msg = " + message.getText());
                    runOnUiThread(() -> onMessageReceive(message, fromMember));
                }

                @Override
                public void onMemberJoined(RtmChannelMember rtmChannelMember) {
                    runOnUiThread(() -> {
                        Log.i("autolog", "rtmChannelMember: " + rtmChannelMember.getChannelId() + rtmChannelMember.getUserId());
                        viewers = viewers + 1;
                        if (isBroadcaster) {
                            if (isBroadcastMuted) {
                                new Handler().postDelayed(() -> sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + "Broadcast has been Paused!"), 2500);
                            }
                        } else if (isGuest1) {
                            if (!ispknow) {
                                if (guestonemute)
                                    new Handler().postDelayed(() -> sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " Guest One Muted"), 2000);
                            }
                        } else if (isGuest2) {
                            if (!ispknow) {
                                if (guesttwomute)
                                    new Handler().postDelayed(() -> sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " Guest Two MuTed"), 2000);
                            }
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


            if (guestAudience || isAudience) {
                if (SessionUser.getRtmLoginSession()) {
                    try {
                        mChatManager.leaveChannel();
                        if (mRtmChannel != null) {
                            mRtmChannel.leave(null);
                            mRtmChannel.release();
                            mRtmChannel = null;
                        }
                        mRtmChannel = mRtmClient.createChannel(channelrtm, new RtmChannelListener() {
                            @Override
                            public void onMemberCountUpdated(int i) {

                            }

                            @Override
                            public void onAttributesUpdated(List<RtmChannelAttribute> list) {

                            }

                            @Override
                            public void onMessageReceived(RtmMessage message, RtmChannelMember fromMember) {
                                String text = message.getText();
                                Log.i("autolog", "text: " + text);
                                String fromUser = fromMember.getUserId();
                                runOnUiThread(() -> onMessageReceive(message, fromMember));
                            }

                            @Override
                            public void onMemberJoined(RtmChannelMember rtmChannelMember) {
                                runOnUiThread(() -> {
                                    Log.i("autolog", "rtmChannelMember: " + rtmChannelMember.getChannelId() + rtmChannelMember.getUserId());
                                    viewers = viewers + 1;
                                    if (isBroadcaster) {
                                        if (isBroadcastMuted) {
                                            new Handler().postDelayed(() -> sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + "Broadcast has been Paused!"), 2500);
                                        }
                                    } else if (isGuest1) {
                                        if (!ispknow) {
                                            if (guestonemute)
                                                new Handler().postDelayed(() -> sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " Guest One Muted"), 2000);
                                        }
                                    } else if (isGuest2) {
                                        if (!ispknow) {
                                            if (guesttwomute)
                                                new Handler().postDelayed(() -> sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " Guest Two MuTed"), 2000);
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onMemberLeft(RtmChannelMember member) {
                                Log.i("autolog", "member: " + member);
                                runOnUiThread(() -> {
                                    if (!isBroadcastEnded) {
                                        callAudiencesAPI(broadcaster.getUser_id());
                                    }
                                });
                            }
                        });

                    } catch (RuntimeException e) {
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
                            Log.d(TAG, "join channel failure! errorCode = "
                                    + errorInfo.getErrorCode());
                            Log.i("autolog-fromlive_guest", "mChannelName: " + channelrtm);
                            /*mChatManager.doLogin(channelrtm);*/
                            joinrtmchannel(channelrtm, "0");
                        }
                    });
                } else {
                    Log.d("autolog", "login: ");
                    /*mChatManager.doLogin(channelrtm);*/
                    Log.i("autolog", "mChatManager: " + mChatManager);
                    try {
                        joinrtmchannel(channelrtm, "0");
                    } catch (Exception e) {
                        Log.i("autolog", "e: " + e.getLocalizedMessage());

                    }
                }
            } else {
                if (cRole != 0) {
                    mChatManager = BLiveApplication.getInstance().getChatManager();
                    mRtmClient = mChatManager.getRtmClient();
                    mRtmChannel = mChatManager.getRtmChannel();
                    joinrtmchannel(channelrtm, "0");
                } else if (cRole == 0) {
                    mChatManager = BLiveApplication.getInstance().getChatManager();
                    mRtmClient = mChatManager.getRtmClient();
                    mRtmChannel = mChatManager.getRtmChannel();
                    joinrtmchannel(SessionUser.getUser().getUsername(), "0");
                }


/*
                mChatHandler = new ChatHandler() {
                    @Override
                    public void onLoginSuccess() {
                        Log.e(TAG, "onLoginSuccess: ");
                    }

                    @Override
                    public void onLoginFailed(ErrorInfo errorInfo) {
                        Log.e(TAG, "onLoginFailed: " + errorInfo.getErrorDescription());
                        mChatManager.doLogin(channelrtm);
                        */
                /*joinrtmchannel(channelrtm,"o");*//*

                    }

                    @Override
                    public void onChannelJoinSuccess() {
                        Log.e(TAG, "onRTMChannelJoinSuccess: ");
                    }

                    @Override
                    public void onChannelJoinFailed(ErrorInfo errorCode) {
                        Log.e(TAG, "onRTMChannelJoinFailed: ");
                        */
                /*mChatManager.doLogin(channelrtm);*//*

                        Log.i("autolog-fromlive_bro", "mChannelName: " + channelrtm);
                        joinrtmchannel(channelrtm, "o");
                    }

                    @Override
                    public void onMessageReceived(RtmMessage message, RtmChannelMember fromMember) {
                        Log.e(TAG, "onRTMMessageReceived: account = " + fromMember.getUserId() + " msg = " + message.getText());
                        runOnUiThread(() -> onMessageReceive(message, fromMember));
                    }

                    @Override
                    public void onMemberJoined(RtmChannelMember rtmChannelMember) {
                        runOnUiThread(() -> {
                            Log.i("autolog", "rtmChannelMember: " + rtmChannelMember.getChannelId() + rtmChannelMember.getUserId());
                            viewers = viewers + 1;
                            if (isBroadcaster) {
                                if (isBroadcastMuted) {
                                    new Handler().postDelayed(() -> sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + "Broadcast has been Paused!"), 2500);
                                }
                            } else if (isGuest1) {
                                if (!ispknow) {
                                    if (guestonemute)
                                        new Handler().postDelayed(() -> sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " Guest One Muted"), 2000);
                                }
                            } else if (isGuest2) {
                                if (!ispknow) {
                                    if (guesttwomute)
                                        new Handler().postDelayed(() -> sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " Guest Two MuTed"), 2000);
                                }
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
*/
            }
            mChatManager.addChantHandler(mChatHandler);

            initViews();
            dvalue = Integer.valueOf(SessionUser.getUser().getDiamond());
            getLevel();
            startTime = System.currentTimeMillis();
            Log.e(TAG, "initUI: Diamond" + SessionUser.getUser().getDiamond());
            tvCurrentDiamondValue.setText(SessionUser.getUser().getDiamond());
            try {
                Log.e(TAG, "Image URL: " + image);
                decodeImage = URLDecoder.decode(image, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if (decodeImage != null && !decodeImage.isEmpty()) {
                Log.e(TAG, "Decode Image URL: " + decodeImage);
                Picasso.get().load(decodeImage).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(ivUser);
                Picasso.get().load(decodeImage).fit().transform(new BlurTransformation(mActivity)).centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).
                        placeholder(R.drawable.user).into(ivOffline);
                Picasso.get().load(decodeImage).fit().transform(new BlurTransformation(mActivity)).centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).
                        placeholder(R.drawable.user).into(ivPause);
            } else {
                Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(ivUser);
                Picasso.get().load(R.drawable.user).fit().transform(new BlurTransformation(mActivity)).centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).
                        placeholder(R.drawable.user).into(ivOffline);
                Picasso.get().load(R.drawable.user).fit().transform(new BlurTransformation(mActivity)).centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).
                        placeholder(R.drawable.user).into(ivPause);
            }

            Glide.with(getApplicationContext())
                    .load(R.drawable.diamond_svg)
                    .into(ivDiamond);

            Glide.with(getApplicationContext())
                    .load(R.drawable.free_icon)
                    .into(ivFreeDiamond);

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
            llCombo.setVisibility(View.GONE);

            adapterRequests = new AdapterRequests(getApplicationContext(), mRequests);
            adapterRequests.setOnClickListener(this);

            GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2, GridLayoutManager.HORIZONTAL, false);
            rvGift.setLayoutManager(layoutManager);
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

            broadcasterUid = broadcaster.getId();
            moonValue = broadcaster.getOver_all_gold();

            int times = Integer.valueOf(moonValue) / 8100;
            tvMoonLevelCount.setText("5x" + times);

            loadStarImage(moonValue);

            if (cRole == 0) {
                throw new RuntimeException("Should not reach here");
            }

            String roomName = i.getStringExtra(Constants_app.ACTION_KEY_ROOM_NAME);
            doConfigEngine(cRole);

            mGridVideoViewContainer = findViewById(R.id.grid_video_view_container);

            slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
            slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);

            ImageButton imageButton = findViewById(R.id.sendButton);
            imageButton.setOnClickListener(sendButtonListener);

            LinearLayoutManager layoutManagers = new LinearLayoutManager(this);
            layoutManagers.setOrientation(RecyclerView.VERTICAL);
            rvMessages.setLayoutManager(layoutManagers);
            LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
            linearLayoutManager1.setOrientation(RecyclerView.VERTICAL);
            rvRequests.setLayoutManager(linearLayoutManager1);

            ImageView bMessage = findViewById(R.id.btn_msg);
            ImageView bSwitchCam = findViewById(R.id.btn_2);
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
                Log.e(TAG, "initUI: " + broadcaster.getOver_all_gold());
                tvReceived.setText(String.valueOf(gold));

                isBroadcasterOffline = true;
            }

            if (isBroadcaster(cRole)) {

                broadcaster = SessionUser.getUser();
                SurfaceView surfaceV = RtcEngine.CreateRendererView(getApplicationContext());
                rtcEngine().setupLocalVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, SessionUser.getUser().getId()));
                surfaceV.setZOrderOnTop(false);
                surfaceV.setZOrderMediaOverlay(false);

                mUidsList.put(SessionUser.getUser().getId(), surfaceV); // get first surface view
                isBroadcaster = true;
                isAudience = false;
                mGridVideoViewContainer.initViewContainer(getApplicationContext(), SessionUser.getUser().getId(), mUidsList, true, isBroadcaster, isGuest1, ispknow);// first is now full view
                worker().preview(true, surfaceV, SessionUser.getUser().getId());

                ivGift.setVisibility(View.GONE);
                rvMessages.setVisibility(View.VISIBLE);
                bottomBroadcaster.setVisibility(View.VISIBLE);
                bottomAudience.setVisibility(View.GONE);
                cvRequests.setVisibility(View.GONE);
                ivFollow.setVisibility(View.GONE);
                ivVideoMute.setVisibility(View.GONE);
                ivGuestClose.setVisibility(View.GONE);
                broadcasterUI(bSwitchCam/*, fabMic*/);
            } else {
                isAudience = true;
                isBroadcasterOffline = true;
                /*fabMic.setVisibility(View.GONE);*/
                fabBeauty.setVisibility(View.GONE);
                rlOffline.setVisibility(View.VISIBLE);
                rvMessages.setVisibility(View.GONE);
                ivGift.setVisibility(View.GONE);
                fabMenu.setVisibility(View.GONE);
                fabRequest.setVisibility(View.GONE);
                rlCoins.setVisibility(View.GONE);
                ivGuestClose.setVisibility(View.GONE);
                bottomBroadcaster.setVisibility(View.GONE);
                bottomAudience.setVisibility(View.GONE);
                cvRequests.setVisibility(View.GONE);
                rvImages.setVisibility(View.GONE);
                ivActiveViewers.setVisibility(View.GONE);

                isTextMuted = broadcaster.getText_muted().equalsIgnoreCase("yes");


                if (isFollowing.equalsIgnoreCase("yes"))
                    ivFollow.setVisibility(View.GONE);
                else
                    ivFollow.setVisibility(View.VISIBLE);

//                callVisitorLogApi("join");

                audienceUI(bSwitchCam);
            }

            worker().joinChannel(roomName, SessionUser.getUser().getId());
            worker().getRtcEngine().setParameters("Hi This Is 3 Call");
            UserInfo userInfo = new UserInfo();
            userInfo.userAccount = "";
            worker().getRtcEngine().getUserInfoByUid(0, userInfo);
            worker().getRtcEngine().getParameter("Hi This Is 3 Call", "Hi This Is 3 Call");
            TextView textRoomName = findViewById(R.id.room_name);
            textRoomName.setText(broadcaster.getName());

            Glide.with(getApplicationContext())
                    .load(R.drawable.free_icon)
                    .into(ivFreeGiftIcon);

//        guestOne.setOnClickListener(view -> {
//            Log.e(TAG, "initViews: " + "Guest One CLicked");
//            if (isGuest2 || isGuest1) {
//                guestclick = "guest2";
//                callGetGuestsAPI1();
//            }
//        });
//
//        guestTwo.setOnClickListener(view -> {
//            Log.e(TAG, "initViews: " + "Guest TWo CLicked");
//            if (isGuest1 || isGuest2) {
//                guestclick = "guest1";
//                callGetGuestsAPI1();
//            }
//        });


            guestTwo.setOnClickListener(view -> {
                Log.e(TAG, "initViews: " + "Guest TWo CLicked");
                if (isGuest1 || isGuest2) {
                    Log.i("autolog", "guestarraylist.size(): " + guestarraylist.size());
                    guestclick = "guest1";
                    callGetGuestsAPI1();
                } else {
                    if (mUidsList.size() >= 2) {
                        Log.i("autolog", "guestarraylist.size(): " + guestarraylist.size());
                        guestclick = "guest1";
                        callGetGuestsAPI1();
                    }
                }
            });

            guestOne.setOnClickListener(view -> {
                Log.e(TAG, "initViews: " + "GuestOne CLicked");
                if (isGuest2 || isGuest1) {
                    Log.i("autolog", "guestarraylist.size(): " + guestarraylist.size());
                    guestclick = "guest2";
                    callGetGuestsAPI1();
                } else {
                    if (mUidsList.size() >= 2) {
                        Log.i("autolog", "guestarraylist.size(): " + guestarraylist.size());
                        guestclick = "guest2";
                        callGetGuestsAPI1();
                    }
                }
            });

            ivWebViewClose.setOnClickListener(v -> {
                webViewFreeGift.destroy();
                cvWebView.setVisibility(View.GONE);
            });

            ivFreeGiftIcon.setOnClickListener(v -> {
                cvWebView.setVisibility(View.VISIBLE);
                onClickFreeFift(iamge);
            });

            bulletImg.setOnClickListener(v -> {
                if (!isBulletenabled) {
                    isBulletenabled = true;
                    bulletImg.setImageDrawable(getDrawable(R.drawable.bullet_one));
                    userTypedMessage.setHint("1 Diamond per Bullet Screen");
                    int maxLengthofEditText = 50;
                    userTypedMessage.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLengthofEditText)});
                } else {
                    isBulletenabled = false;
                    bulletImg.setImageDrawable(getDrawable(R.drawable.bullet_2));
                    int maxLengthofEditText = 500;
                    userTypedMessage.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLengthofEditText)});
                    userTypedMessage.setHint(getString(R.string.message_hint));
                }
            });

            bMessage.setOnClickListener(v -> {
                if (fabMenu.isOpened()) {
                    fabMenu.close(true);
                }
                llChat.setVisibility(View.VISIBLE);
                userTypedMessage = findViewById(R.id.userMessageBox);
                userTypedMessage.requestFocus();
                // Show soft keyboard for the user to enter the value.
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.showSoftInput(userTypedMessage, InputMethodManager.SHOW_IMPLICIT);
            });

            ivWebViewClose.setOnClickListener(v -> {
                webViewFreeGift.loadUrl(urlNew);
                cvWebView.setVisibility(View.GONE);
            });

            root.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (fabMenu.isOpened()) {
                        fabMenu.close(true);
                    }
                    gestureDetector.onTouchEvent(event);
                    return true;
                }

                private GestureDetector gestureDetector = new GestureDetector(ActivityLiveRoom.this, new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        Log.e(TAG, "onSingleTapUp: root");
                        if (fabMenu.isOpened()) {
                            fabMenu.close(true);
                        }
                        return false;
                    }

                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        if (fabMenu.isOpened()) {
                            fabMenu.close(true);
                        }
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
                    rlLive.setVisibility(View.VISIBLE);
                    fabMenu.setVisibility(View.VISIBLE);

                    /*ivImng.setVisibility(View.VISIBLE);*/
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
                private GestureDetector gestureDetector = new GestureDetector(ActivityLiveRoom.this, new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        if (fabMenu.isOpened()) {
                            fabMenu.close(true);
                        }
                        return super.onSingleTapConfirmed(e);
                    }

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
                        callRemoveImageAPI();
                    }*/
                }

                void onSwipeDown() {
                   /* if (!isBroadcaster()) {
                        position = position - 1;
                        isSwiped = true;
                        isSwipedDown = true;
                        callRemoveImageAPI();
                    }*/
                }

                void onSwipeLeft() {
                    if (!isBroadcasterOffline) {
                        rlLive.setVisibility(View.GONE);
                        fabMenu.setVisibility(View.GONE);
                        ivImng.setVisibility(View.GONE);
                    }
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
                        else if (isGuest) {
                            bottomAudience.setVisibility(View.GONE);
                            bottomBroadcaster.setVisibility(View.VISIBLE);
                        } else
                            bottomAudience.setVisibility(View.VISIBLE);

                        llGift.startAnimation(slideDown);
                        llGift.setVisibility(View.GONE);
                    } else if (cvRequests.getVisibility() == View.VISIBLE) {
                        cvRequests.startAnimation(slideDown);
                        cvRequests.setVisibility(View.GONE);
                    }
                    gestureDetector.onTouchEvent(event);
                    return true;
                }
            });

            if (!isFirst) {
                isFirst = true;
                String broad = "solo";
                if (ispknow) {
                    broad = "pk";
                } else {
                    broad = "solo";

                }



//                callAudiencesAPI(broadcaster.getUser_id());



                    if (utils.isNetworkAvailable()) {
                        utils.showProgress();
                        final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
                        Call<GiftResponse> call = apiClient.getGifts(broad, SessionUser.getUser().getUser_id());
                        Log.i("autolog", "broad: " + broad);
                        call.enqueue(new retrofit2.Callback<GiftResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<GiftResponse> call, @NonNull Response<GiftResponse> response) {
                                GiftResponse giftResponse = response.body();
                                utils.hideProgress();
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



            tvOffline.postDelayed(() -> tvOffline.setVisibility(View.VISIBLE), 5000);

            if (!ispknow) {
                ivJoin.postDelayed(() -> ivJoin.setVisibility(View.VISIBLE), 5000);
            }
        } catch (Exception e) {
            Log.i("autolog-gift", "e: " + e.toString());

        }
    }

    private void getLevel() {
        try {
            level = SessionUser.getUser().getLevel();
            if (level.length() == 1) {
                level = "0" + level;
            }
            if (ispknow) {
                levelchange();
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    private void callVisitorLogApi(String type) {
        final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
        Call<UsersResponse> call = apiClient.visitorLog(SessionUser.getUser().getUser_id(), broadcaster.getUser_id(), type);
        call.enqueue(new retrofit2.Callback<UsersResponse>() {
            @Override
            public void onResponse(@NonNull Call<UsersResponse> call, @NonNull Response<UsersResponse> response) {
                UsersResponse usersResponse = response.body();
                if (response.code() == 200) {
                    if (usersResponse != null) {
                        if (usersResponse.getStatus().equalsIgnoreCase("success")) {
                            freeGiftCount = usersResponse.getData().getGiftAvailable();
                            tvCurrentFreeGiftValue.setText(String.valueOf(freeGiftCount));
                            Log.e(TAG, "  tvCurrentFreeGiftValue " + freeGiftCount);
                            if (type.equals("join")) {
                                lastBroasTime = usersResponse.getData().getLastBroadTime();
                                checkFreeGiftStatus(lastBroasTime);
                            } else {

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

    public void checkFreeGiftStatus(int lastBroasTime) {
        long result = TimeUnit.SECONDS.toMillis(lastBroasTime);
        new Handler().postDelayed(() -> callGiftCountTimer(), 30000);
    }

    public void callGiftCountTimer() {
        CountDownTimer countDownTimer1 = new CountDownTimer(3600000, 300000) {
            @Override
            public void onTick(long millisUntilFinished) {
                try {
                    callVisitorLogApi("left");
                } catch (Exception e) {
                    Crashlytics.logException(e);
                }
            }

            @Override
            public void onFinish() {
                if (!isCounterEnable) {
                    isCounterEnable = true;
                }
            }
        }.start();
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
                                    Log.d(TAG, "onResponse: "+ usersResponse.getData().getAudiencefreeGiftCount());
                                    setAudiences(usersResponse.getData().getAudiences(), usersResponse.getData().getEntranceEffect(), usersResponse.getData().getViewers_count(),
                                            usersResponse.getData().getAudiencefreeGiftCount(), usersResponse.getData().getOverAllGold(), usersResponse.getData().getIsTheUserFollowing());
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
Log.i("autolog", "t: " + t.getMessage());
                    showToast(t.getMessage());
                }
            });
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

    private void setAudiences(ArrayList<Audience> audiences, String entranceUrl, int viewers_count, int freeGiftCount, int overAllGold, String istheuserFollowing) {
        mAudiences = audiences;
        AdapterImages adapterImages = new AdapterImages(this, audiences);
        adapterImages.setOnClickListener(this);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        rvImages.setLayoutManager(layoutManager1);
        rvImages.setAdapter(adapterImages);
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        rv_images_pk.setLayoutManager(llm);
        rv_images_pk.setAdapter(adapterImages);
        rvImages.scrollToPosition(0);
        viewers = viewers_count;
        tvCount.setText(String.valueOf(viewers));
        tvFreeGiftCount.setText(String.valueOf(freeGiftCount));
        tvCurrentFreeGiftValue.setText(String.valueOf(freeGiftCount));
        tvReceived.setText(String.valueOf(overAllGold));

        if (broadcaster.getUser_id().equals(SessionUser.getUser().getUser_id())) {
            ivFollow.setVisibility(View.GONE);
        } else {
            if (istheuserFollowing.equalsIgnoreCase("yes"))
                ivFollow.setVisibility(View.GONE);
            else
                ivFollow.setVisibility(View.VISIBLE);
        }

        try {
            sendChannelMessage("FreeGiftCount" + freeGiftCount);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }

        if (!isBroadcaster()) {
            if (!isArrived) {
                isArrived = true;
                MessageBean message = new MessageBean(selfName, SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " has arrived", true, false, false);
                messageBeanList.add(message);
                adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                rvMessages.scrollToPosition(messageBeanList.size() - 1);
                try {
                    sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " has arrived");
                } catch (Exception e) {
                    Crashlytics.logException(e);
                }
                new Handler().postDelayed(() -> {
                    if (!entranceUrl.isEmpty()) {
                        sendChannelMessage(SessionUser.getUser().getUser_id() + level + " enTraNceEffEct " + SessionUser.getUser().getName());
                    }
                }, 3000);

                EntranceEffect entranceEffect = new EntranceEffect();
                entranceEffect.setAccount(SessionUser.getUser().getName());
                entranceEffect.setUrl(entranceUrl);
                Log.e(TAG, "setAudiences: " + entranceUrl);
                entranceEffect.setId(SessionUser.getUser().getUser_id());
                entranceEffects.add(entranceEffect);

                if (entranceEffects.size() > 0) {
                    if (!isEntranceEffects) {
                        callEntranceEffect(temp1);
                    }
                }
            }
        }
    }

    public void onGoldLayoutClicked(View view) {
        try {
            showContributors(broadcaster.getName(), tvReceived.getText().toString());
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    @Override
    protected void deInitUI() {
        doLeaveChannel();
        removeCallBack();
        event().removeEventHandler(this);
        utils.deleteCache(mActivity);
        mUidsList.clear();
    }

    public void onClickFreeFift(int imageid) {
        cvWebView.setVisibility(View.VISIBLE);
       /* if (imageid == 1) {
            webViewFreeGift.loadUrl(Constants_api.treasureBox);

        } else {
            webViewFreeGift.loadUrl(Constants_api.freeGiftWeb);
        }*/
        webViewFreeGift.loadUrl(Constants_api.freeGiftWeb);
        webViewFreeGift.getSettings().setSupportMultipleWindows(true);
        webViewFreeGift.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webViewFreeGift.getSettings().setJavaScriptEnabled(true);
        webViewFreeGift.getSettings().setDomStorageEnabled(true);
        webViewFreeGift.getSettings().setLoadWithOverviewMode(true);
        webViewFreeGift.getSettings().setUseWideViewPort(true);
        webViewFreeGift.getSettings().setDomStorageEnabled(true);
        /*webViewFreeGift.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);*/
        webViewFreeGift.getSettings().setDefaultTextEncodingName("utf-8");
        webViewFreeGift.getSettings().setPluginState(WebSettings.PluginState.ON);
        webViewFreeGift.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!url.equalsIgnoreCase(errorUrl)) {
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                    if (swipeRefreshLayout.isRefreshing())
                        swipeRefreshLayout.setRefreshing(false);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                    urlNew = view.getUrl();
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                errorUrl = request.getUrl().toString();
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ActivityLiveRoom.this);
                builder.setMessage(R.string.notification_error_ssl_cert_invalid);
                builder.setPositiveButton("continue", (dialog, which) -> handler.proceed());
                builder.setNegativeButton("cancel", (dialog, which) -> handler.cancel());
                final AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @OnClick(R.id.iv_follow)
    public void onClickFollow() {
        callFollowAPI("follow", broadcaster.getUser_id(), broadcaster.getName());
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

    @OnClick(R.id.bSendGift)
    public void sendGift() {
        try {
            if (mGift != null) {
                if (dvalue >= Integer.valueOf(mGift.getPrice())) {
                    if (Integer.valueOf(mGift.getPrice()) < 101) {
                        int price = Integer.valueOf(mGift.getPrice());//* comboMultiplier
                        Log.e(TAG, "sendGift: comboMultiplier" + comboMultiplier + "  " + price);
                        if (dvalue >= price) {
                            sendGift(String.valueOf(price), String.valueOf(comboMultiplier), "");
                        } else {
                            isRedirectScreen = true;
                            if (isGuest)
                                bottomBroadcaster.setVisibility(View.VISIBLE);
                            else
                                bottomAudience.setVisibility(View.VISIBLE);
                            Intent intent = new Intent(mActivity, ActivityAdvancedWV.class);
                            intent.putExtra("title", "Wallet");
                            intent.putExtra("from", "liveRoom");
                            intent.putExtra("url", Constants_api.wallet + SessionUser.getUser().getUser_id());
                            startActivity(intent);
                        }
                    } else if (Integer.valueOf(mGift.getPrice()) > 101) {
                        if (Integer.valueOf(mGift.getPrice()) > 0) {
                            sendGift(String.valueOf(mGift.getPrice()), "", "");
                            if (Integer.valueOf(mGift.getPrice()) == 0) {
                                Log.e(TAG, "sendGift: ");
                                llCombo.setVisibility(View.VISIBLE);
                                int price = Integer.valueOf(mGift.getPrice());//* comboMultiplier
                                sendGift(String.valueOf(price), String.valueOf(comboMultiplier), "");
                            }
                        }
                    }
                } else {
                    isRedirectScreen = true;
                    if (isGuest)
                        bottomBroadcaster.setVisibility(View.VISIBLE);
                    else
                        bottomAudience.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(mActivity, ActivityAdvancedWV.class);
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

    public void sendGift(String mPrice, String mMultiplier, String messageType) {
        price = mPrice;
        multiplier = mMultiplier;
        Log.e(TAG, "sendGift: multiplier " + multiplier);
        String giftName = "";
        if (messageType.contains("bulletMessage")) {
            giftName = "bulletMessage";
        } else {
            giftName = mGift.getName();
            if (mPrice.equals("0")) {
                if (freeGiftCount > 0) {
//                    freeGiftCount = freeGiftCount - Integer.parseInt(multiplier);
//                    tvCurrentFreeGiftValue.setText(String.valueOf(freeGiftCount));
                }
            }
        }

        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
           /* Call<GiftResponse> call = apiClient.sendGift(SessionUser.getUser().getUser_id(), SessionUser.getUser().getUser_id(), broadcasterId, giftName, mPrice, multiplier);
            call.enqueue(new retrofit2.Callback<GiftResponse>() {
                @Override
                public void onResponse(@NonNull Call<GiftResponse> call, @NonNull Response<GiftResponse> response) {
                    GiftResponse giftResponse = response.body();
                    utils.hideProgress();
                    if (response.code() == 200) {
                        if (giftResponse != null) {
                            if (giftResponse.getStatus().equalsIgnoreCase("success")) {
                                comboMultiplier = 0;
                                    if (ispknow) {
                                        Log.d("onMessageReceive", "onMessageReceive: ");
                                        GiftUpdateAPI();
                                    }
                                onGiftSuccess(giftResponse.getData().getUser_id(), giftResponse.getData().getOver_all_gold(), giftResponse.getData().getCurrent_gold_value(),
                                        giftResponse.getData().getMoon_level(), giftResponse.getData().getMoon_value(), giftResponse.getData().getFreegiftReceived(),
                                        giftResponse.getData().getDiamond(), giftResponse.getData().getFreeGiftsAvailable(), giftResponse.getData().getFreeGiftAnim(), messageType);
                                Log.e(TAG, "onResponse: onGiftSuccess   " + giftResponse.getData().getOver_all_gold());
                            } else {
                                ivGift.setEnabled(true);
                                showToast(giftResponse.getMessage());
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
            });*/
            if (guestAudience) {
                Call<GiftResponse> call = apiClient.sendGift(SessionUser.getUser().getUser_id(), SessionUser.getUser().getUser_id(), broadcaster.getUser_id(), giftName, mPrice, multiplier);
                call.enqueue(new retrofit2.Callback<GiftResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GiftResponse> call, @NonNull Response<GiftResponse> response) {
                        GiftResponse giftResponse = response.body();
                        Log.d(TAG, "onResponse: " + response.raw().request().url());
                        utils.hideProgress();
                        if (response.code() == 200) {
                            if (giftResponse != null) {
                                if (giftResponse.getStatus().equalsIgnoreCase("success")) {
                                    comboMultiplier = 0;
                                    onGiftSuccess(giftResponse.getData().getUser_id(), giftResponse.getData().getOver_all_gold(), giftResponse.getData().getCurrent_gold_value(),
                                            giftResponse.getData().getMoon_level(), giftResponse.getData().getMoon_value(), giftResponse.getData().getFreegiftReceived(),
                                            giftResponse.getData().getDiamond(), giftResponse.getData().getFreeGiftsAvailable(), giftResponse.getData().getFreeGiftAnim(), messageType, giftResponse.getData().getGift_name());

                                    if (ispknow) {
                                        GiftUpdateAPI();
                                    }
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
            } else {
                Call<GiftResponse> call = apiClient.sendGift(SessionUser.getUser().getUser_id(), SessionUser.getUser().getUser_id(), broadcaster.getUser_id(), giftName, mPrice, multiplier);
                call.enqueue(new retrofit2.Callback<GiftResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GiftResponse> call, @NonNull Response<GiftResponse> response) {
                        GiftResponse giftResponse = response.body();
                        Log.i("autolog", "response: " + response.raw().request().url());

                        utils.hideProgress();
                        if (response.code() == 200) {
                            if (giftResponse != null) {
                                if (giftResponse.getStatus().equalsIgnoreCase("success")) {
                                    onGiftSuccess(giftResponse.getData().getUser_id(), giftResponse.getData().getOver_all_gold(), giftResponse.getData().getCurrent_gold_value(),
                                            giftResponse.getData().getMoon_level(), giftResponse.getData().getMoon_value(), giftResponse.getData().getFreegiftReceived(),
                                            giftResponse.getData().getDiamond(), giftResponse.getData().getFreeGiftsAvailable(), giftResponse.getData().getFreeGiftAnim(), messageType, giftResponse.getData().getGift_name());
                                    if (ispknow) {
                                        GiftUpdateAPI();
                                    }
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
        /*llGift.startAnimation(slideDown);
        llGift.setVisibility(View.GONE);*/
        ivGift.setEnabled(false);
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
            isRedirectScreen = true;
            Intent intent = new Intent(this, ActivityViewProfile.class);
            intent.putExtra("image", decodeImage);
            intent.putExtra("userId", broadcaster.getUser_id());
            intent.putExtra("from", "liveRoom");
            startActivityForResult(intent, 1);
        } else {
            onclickBroad();
            /*isClicked = false;
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityLiveRoom.this);
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
                Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivGuestProfile);*/
        }
    }

    @OnClick(R.id.fb_request)
    public void onClickRequests() {
        fabMenu.close(true);
        cvRequests.startAnimation(slideUp);
        cvRequests.setVisibility(View.VISIBLE);
//        fabRequest.setVisibility(View.GONE);
    }

    public void onClickShare(View view) {
        fabMenu.close(true);
        isRedirectScreen = true;
        showSharingDialog(ActivityLiveRoom.this);
    }

    @Optional
    @OnClick(R.id.iv_gift)
    public void onClickGift() {
        String broad = "solo";
        if (ispknow) {
            broad = "pk";
        } else {
            broad = "solo";
        }
        if (utils.isNetworkAvailable()) {
            utils.showProgress();
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<GiftResponse> call = apiClient.getGifts(broad, SessionUser.getUser().getUser_id());
            Log.i("autolog", "broad: " + broad);
            call.enqueue(new retrofit2.Callback<GiftResponse>() {
                @Override
                public void onResponse(@NonNull Call<GiftResponse> call, @NonNull Response<GiftResponse> response) {
                    GiftResponse giftResponse = response.body();
                    utils.hideProgress();
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

        if (llGift.getVisibility() == View.VISIBLE) {
            llGift.startAnimation(slideDown);
            llGift.setVisibility(View.GONE);
            /*if(isAudience){
                bottomAudience.setVisibility(View.GONE);
            }
            if(isGuest){
                bottomBroadcaster.setVisibility(View.GONE);
            }*/
        } else {
            llGift.startAnimation(slideUp);
            llGift.setVisibility(View.VISIBLE);
            tvCurrentDiamondValue.setText(SessionUser.getUser().getDiamond());
            /*if(isAudience){
                bottomAudience.setVisibility(View.GONE);
            }
            if(isGuest){
                bottomBroadcaster.setVisibility(View.GONE);
            }*/
        }
    }

    @OnClick(R.id.btn_changeAssests)
    public void onClickchangeAssests() {
        Intent intent = new Intent(mActivity, ActivityWebView.class);
        intent.putExtra("title", "My Assests");
        intent.putExtra("from", "liveRoom");
        intent.putExtra("url", Constants_api.assets + SessionUser.getUser().getUser_id());
        startActivity(intent);
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
            adapterGifts = new AdapterGifts(this, giftTools);
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
            adapterGifts = new AdapterGifts(this, freeGifts);
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

    public void onClickClose(View view) {
        if (cRole == 1) {
            callBroadcasterClose();
        } else {
            callAudienceClose();
        }
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
            Call<UsersResponse> call = apiClient.removeAudience(SessionUser.getUser().getUser_id(), broadcaster.getUser_id(), String.valueOf(seconds));
            call.enqueue(new retrofit2.Callback<UsersResponse>() {
                @Override
                public void onResponse(@NonNull Call<UsersResponse> call, @NonNull Response<UsersResponse> response) {
                    UsersResponse usersResponse = response.body();
                    Log.i("autolog", "(response.code(): " + (response.code()));
                    Log.i("autolog", "(response.code(): " + (response.raw().request().url()));

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
            Intent intent = new Intent(ActivityLiveRoom.this, ActivityBroadcastStop.class);
            intent.putExtra("name", broadcaster.getName());
            intent.putExtra("image", broadcaster.getProfile_pic());
            intent.putExtra("user_id", broadcaster.getUser_id());
            startActivity(intent);
        }
    }

    private View.OnClickListener sendButtonListener = v -> {
        try {
            getLevel();
            userTypedMessage = findViewById(R.id.userMessageBox);
            String msg = userTypedMessage.getText().toString();
            msg = msg.trim();
            if (msg.length() > 0) {
                if (isBulletenabled) {
                    int diamondVal = Integer.parseInt(tvCurrentDiamondValue.getText().toString());
                    if (diamondVal > 0) {
                        rlBulletlay.setVisibility(View.VISIBLE);
                        tvBulletmsg.setText(msg);//msg
                        tvBulletName.setText(SessionUser.getUser().getName());
                        if (!SessionUser.getUser().getProfile_pic().isEmpty()) {
                            Picasso.get().load(SessionUser.getUser().getProfile_pic()).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivBulletUserImg);
                        } else
                            Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivBulletUserImg);

                        TranslateAnimation anim = new TranslateAnimation(900f, -500f, 0.0f, 0.0f);  // might need to review the docs
                        anim.setDuration(7000); // set how long you want the animation

                       /* TranslateAnimation anim = new TranslateAnimation(900f, 0f, 0.0f, 0.0f);  // might need to review the docs
                        anim.setDuration(7000); // set how long you want the animation*/

                        rlBulletlay.setAnimation(anim);
                        rlBulletlay.setVisibility(View.GONE);

                        String bulletMsg = "bulletMessage" + msg;
                        sendGift("1", "", bulletMsg);


                        MessageBean messageBean = new MessageBean(selfName, SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " : " + msg, true, false, false);
                        messageBeanList.add(messageBean);
                        adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                        rvMessages.scrollToPosition(messageBeanList.size() - 1);
                        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " : " + msg);
                    } else {
                        isRedirectScreen = true;
                        Intent intent = new Intent(mActivity, ActivityAdvancedWV.class);
                        intent.putExtra("title", "Wallet");
                        intent.putExtra("from", "liveRoom");
                        intent.putExtra("url", Constants_api.wallet + SessionUser.getUser().getUser_id());
                        startActivity(intent);
                    }
                } else {
                    MessageBean messageBean = new MessageBean(selfName, SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " : " + msg, true, false, false);
                    messageBeanList.add(messageBean);
                    adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                    rvMessages.scrollToPosition(messageBeanList.size() - 1);
                    if (msg.contains("Has sent gIfTsEnTtOyOU")) {

                    } else {
                        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " : " + msg);
                    }
                }
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

    private void broadcasterUI(ImageView button2/*, FloatingActionButton fabMic*/) {

        button2.setOnClickListener(v -> {
            worker().getRtcEngine().switchCamera();
        });

/*
        fabMic.setOnClickListener(v -> {
            Object tag = v.getTag();
            flag = tag == null || !((boolean) tag);
            worker().getRtcEngine().muteLocalAudioStream(flag);
            FloatingActionButton button = (FloatingActionButton) v;
            button.setTag(flag);
            if (flag) {
                button.setBackground(getResources().getDrawable(R.mipmap.micmute));
            } else {
                button.setBackground(getResources().getDrawable(R.mipmap.mic));
            }
            fabMenu.close(true);
        });
*/
    }

    private void audienceUI(ImageView button2) {
        button2.setVisibility(View.GONE);
        fabRequest.setVisibility(View.GONE);
    }

    /*private void doConfigEngine(int cRole) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int prefIndex = pref.getInt(Constants_app.PrefManager.PREF_PROPERTY_PROFILE_IDX, Constants_app.DEFAULT_PROFILE_IDX);
        if (prefIndex > Constants_app.VIDEO_PROFILES.length - 1) {
            prefIndex = Constants_app.DEFAULT_PROFILE_IDX;
        }
        int vProfile = Constants_app.VIDEO_PROFILES[4];

        worker().configEngine(cRole, vProfile);
        rtcEngine().setParameters("{\"che.audio.live_for_comm\":true}");
        rtcEngine().setParameters("{\"che.video.moreFecSchemeEnable\":true}");

        Log.e(TAG, "doConfigEngine: vProfile : " + vProfile);
        if (vProfile == Constants.VIDEO_PROFILE_480P) {
            rtcEngine().setParameters("{\"che.video.lowBitRateStreamParameter\":{\"width\":240,\"height\":320,\"frameRate\":15,\"bitRate\":140}}");
        } else {
            rtcEngine().setParameters("{\"che.video.lowBitRateStreamParameter\":{\"width\":180,\"height\":320,\"frameRate\":15,\"bitRate\":140}}");
        }
    }*/

   /* private void doConfigEngine(int cRole) {
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
        if (isBroadcaster()) {
            worker().preview(false, null, SessionUser.getUser().getId());
        }
    }

    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
        Log.e(TAG, "onFirstRemoteVideoDecoded: ");
    }

   /* private void doRenderRemoteUi(final int uid) {
        Log.e(TAG, "doRenderRemoteUi: " + uid);
        runOnUiThread(() -> {
            if (isFinishing()) {
                return;
            }
            if (!isBroadcaster && !isGuest) {
                isBroadcasterOffline = false;
                ivGift.setVisibility(View.VISIBLE);
                bottomAudience.setVisibility(View.VISIBLE);
                *//*rlOffline.setVisibility(View.GONE);*//*
                rvMessages.setVisibility(View.VISIBLE);
                fabMenu.setVisibility(View.VISIBLE);
                rlCoins.setVisibility(View.VISIBLE);
                rvImages.setVisibility(View.VISIBLE);
                ivGuestClose.setVisibility(View.GONE);
                ivVideoMute.setVisibility(View.GONE);
            }
            if (isGuest) {
                bottomAudience.setVisibility(View.GONE);
                bottomBroadcaster.setVisibility(View.VISIBLE);
                ivGuestClose.setVisibility(View.VISIBLE);
                ImageView button2 = findViewById(R.id.btn_2);
                button2.setVisibility(View.VISIBLE);
                ivVideoMute.setVisibility(View.VISIBLE);
            } else if (isAudience) {
                bottomAudience.setVisibility(View.VISIBLE);
                bottomBroadcaster.setVisibility(View.GONE);
                ivGuestClose.setVisibility(View.GONE);
                ImageView button2 = findViewById(R.id.btn_2);
                button2.setVisibility(View.GONE);
            } else if (isGuest1) {
                Log.d(TAG, "doRenderRemoteUi: " + isGuest1);
                rlGuestTwoClose.setVisibility(View.GONE);
                rlGuestClose.setVisibility(View.VISIBLE);
//                rlGuestTwoClose.removeAllViews();
                guestTwo.removeAllViews();
            } else if (isGuest2) {
                rlGuestTwoClose.setVisibility(View.VISIBLE);
                rlGuestClose.setVisibility(View.VISIBLE);
            }
            if (mUidsList.size() < 3) {
                SurfaceView surfaceV = RtcEngine.CreateRendererView(getApplicationContext());
                surfaceV.setZOrderOnTop(false);
                surfaceV.setZOrderMediaOverlay(false);
                mUidsList.put(uid, surfaceV);
                if (config().mUid == uid) {
                    rtcEngine().setupLocalVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, uid));
                } else {
                    rtcEngine().setupRemoteVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, uid));
                }

                if (isBroadcaster) {
                    if (mUidsList.size() == 2) {
                        guestUid = uid;
                        rlGuestClose.setVisibility(View.VISIBLE);
                        rlGuestTwoClose.setVisibility(View.GONE);
                    } else if (mUidsList.size() == 3) {
                        rlGuestTwoClose.setVisibility(View.VISIBLE);
                    } else if (mUidsList.size() == 1) {
                        guest = null;
                        rlGuestClose.setVisibility(View.GONE);

                        guestUid = 0;
                        guestOne.setVisibility(View.GONE);
                        guestTwo.setVisibility(View.GONE);
                        guestOne.removeAllViews();
                        guestTwo.removeAllViews();
                    }
                } else if (isAudience) {
                    if (mUidsList.size() == 1) {
                        isVideoMute = false;
                    }
                }

                if (mUidsList.size() > 1)
                    mViewType = VIEW_TYPE_SMALL;
                else if (mUidsList.size() == 1)
                    mViewType = VIEW_TYPE_DEFAULT;

                if (mViewType == VIEW_TYPE_DEFAULT) {
                    Log.e(TAG, "doRenderRemoteUi VIEW_TYPE_DEFAULT" + " " + (uid & 0xFFFFFFFFL));
                    switchToDefaultVideoView();
                } else {
                    Log.e(TAG, "doRenderRemoteUi VIEW_TYPE_SMALL" + " " + (uid & 0xFFFFFFFFL));
                    switchToSmallVideoView(broadcaster.getId());
                }
            }
        });
    }*/

    private void doRenderRemoteUi(final int uid) {
        Log.e(TAG, "doRenderRemoteUi: " + uid);
        Log.e(TAG, "doRenderRemoteUi: " + mUidsList.size());

        if (ispknow) {
            if (mUidsList.size() > 2) {
                showToast("Can't add more than two users");
            }
        }

        runOnUiThread(() -> {
            if (isFinishing()) {
                return;
            }
            if (!isBroadcaster && !isGuest) {
                isBroadcasterOffline = false;
                ivGift.setVisibility(View.VISIBLE);
                bottomAudience.setVisibility(View.VISIBLE);
                rlOffline.setVisibility(View.GONE);
                rvMessages.setVisibility(View.VISIBLE);
                fabMenu.setVisibility(View.VISIBLE);
                rlCoins.setVisibility(View.VISIBLE);
                rvImages.setVisibility(View.VISIBLE);
                ivGuestClose.setVisibility(View.GONE);
                ivVideoMute.setVisibility(View.GONE);
            }
            if (isGuest) {
                bottomAudience.setVisibility(View.GONE);
                bottomBroadcaster.setVisibility(View.VISIBLE);
                ivGuestClose.setVisibility(View.VISIBLE);
                button2.setVisibility(View.VISIBLE);
                ivVideoMute.setVisibility(View.VISIBLE);
                ivpk.setVisibility(View.GONE);
            } else if (isAudience) {
//                bottomAudience.setVisibility(View.VISIBLE);
//                bottomBroadcaster.setVisibility(View.GONE);
                ivGuestClose.setVisibility(View.GONE);
                button2.setVisibility(View.GONE);
            } else if (isGuest1) {
                Log.d(TAG, "doRenderRemoteUi: " + isGuest1);
                rlGuestTwoClose.setVisibility(View.VISIBLE);
                rlGuestClose.setVisibility(View.VISIBLE);
                rlGuestTwoClose.removeAllViews();
                ivpk.setVisibility(View.GONE);
                guestTwo.removeAllViews();
            } else if (isGuest2) {
                rlGuestTwoClose.setVisibility(View.VISIBLE);
                rlGuestClose.setVisibility(View.VISIBLE);
                ivpk.setVisibility(View.GONE);
            }
            if (mUidsList.size() < 3) {
                SurfaceView surfaceV = RtcEngine.CreateRendererView(getApplicationContext());
                surfaceV.setZOrderOnTop(false);
                surfaceV.setZOrderMediaOverlay(false);
                mUidsList.put(uid, surfaceV);
                if (config().mUid == uid) {
                    rtcEngine().setupLocalVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, uid));
                } else {
                    rtcEngine().setupRemoteVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, uid));
                }

                if (isBroadcaster) {
                    if (mUidsList.size() == 2) {
                        guestUid = uid;
                        rlGuestClose.setVisibility(View.VISIBLE);
                        rlGuestTwoClose.setVisibility(View.VISIBLE);
                        ivpk.setVisibility(View.GONE);
                    } else if (mUidsList.size() == 3) {
                        rlGuestTwoClose.setVisibility(View.VISIBLE);
                        ivFrameGuestTwo.setVisibility(View.GONE);
                        ivpk.setVisibility(View.GONE);
                    } else if (mUidsList.size() == 1) {
                        guest = null;
                        rlGuestClose.setVisibility(View.GONE);
                        ivFrame.setVisibility(View.GONE);
                        ivFrameGuestTwo.setVisibility(View.GONE);
                        guestUid = 0;
                        guestOne.setVisibility(View.GONE);
                        guestTwo.setVisibility(View.GONE);
                        guestOne.removeAllViews();
                        guestTwo.removeAllViews();
                    }
                } else if (isAudience) {
                    if (mUidsList.size() == 1) {
                        isVideoMute = false;
                    }
                }

                Log.i("autolog", "userjoined: " + uid + ispknow);
                Log.i("autolog-uid", "mUidsList.size(): " + mUidsList.size());
                Log.i("autolog", "ispknow: " + ispknow);
                Log.i("autolog", "pkGuest: " + pkGuest);


                if (ispknow) {
                    if (mUidsList.size() > 1) {
                        mViewType = VIEW_TYPE_SMALL;
                        Log.i("autolog", "VIEW_TYPE_SMALL: " + VIEW_TYPE_SMALL);
                    } else if (mUidsList.size() == 1) {
                        mViewType = VIEW_TYPE_DEFAULT;
                        Log.i("autolog", "VIEW_TYPE_DEFAULT: " + VIEW_TYPE_DEFAULT);
                    }
                    if (mUidsList.size() == 2 && ispknow) {
                        rv_images_pk.setVisibility(View.VISIBLE);
                        rvImages.setVisibility(View.GONE);
                        pkvisible();
                        if (ispknow) {
                            ivJoin.setVisibility(View.GONE);
                            ivpk.setVisibility(View.GONE);
                            button2.setVisibility(View.GONE);
                            if (pkGuest || isBroadcaster) {
                                ivGift.setVisibility(View.GONE);
                                button2.setVisibility(View.VISIBLE);
                            }
                        } else {
                            pkgone();
                        }
                    } else {
                        pkgone();
                    }

                    if (ispknow) {
                        Log.d(TAG, "doRenderRemoteUi: pk");
                        if (mUidsList.size() > 2) {
                            showToast("Can't add more than two users");
                        } else {
                            if (mViewType == VIEW_TYPE_DEFAULT) {
                                Log.e(TAG, "doRenderRemoteUi VIEW_TYPE_DEFAULT" + " " + (uid & 0xFFFFFFFFL));
                                switchToDefaultVideoView();
                            } else {
                                Log.e(TAG, "doRenderRemoteUi VIEW_TYPE_SMALL" + " " + (uid & 0xFFFFFFFFL));
                                switchToSmallVideoView(broadcaster.getId());
                            }
                        }
                    } else {
                        Log.d(TAG, "doRenderRemoteUi: solo");
                        if (mViewType == VIEW_TYPE_DEFAULT) {
                            Log.e(TAG, "doRenderRemoteUi VIEW_TYPE_DEFAULT" + " " + (uid & 0xFFFFFFFFL));
                            switchToDefaultVideoView();
                        } else {
                            Log.e(TAG, "doRenderRemoteUi VIEW_TYPE_SMALL" + " " + (uid & 0xFFFFFFFFL));
                            switchToSmallVideoView(broadcaster.getId());
                        }
                    }
                } else {
                    Log.i("autolog", "solo: " + ispknow);
                    if (!ispknow) {
                        firsstlay.setVisibility(View.GONE);
                        secondtlay.setVisibility(View.GONE);


                    }
                    if (mUidsList.size() > 1)
                        mViewType = VIEW_TYPE_SMALL;
                    else if (mUidsList.size() == 1)
                        mViewType = VIEW_TYPE_DEFAULT;

                    if (mViewType == VIEW_TYPE_DEFAULT) {
                        Log.e(TAG, "doRenderRemoteUi VIEW_TYPE_DEFAULT" + " " + (uid & 0xFFFFFFFFL));
                        switchToDefaultVideoView();
                    } else {
                        Log.e(TAG, "doRenderRemoteUi VIEW_TYPE_SMALL" + " " + (uid & 0xFFFFFFFFL));
                        switchToSmallVideoView(broadcaster.getId());
                    }
                }
            }


            if (isBroadcaster || pkGuest) {
                fabRequest.setVisibility(View.VISIBLE);
                if (fabMenu.isOpened()) {
                    fabMenu.close(true);
                }
            } else {
                if (fabMenu.isOpened()) {
                    fabMenu.close(true);
                }

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

            worker().getEngineConfig().mUid = uid;

            SurfaceView surfaceV = mUidsList.remove(broadcasterUid);
            if (surfaceV != null) {
                mUidsList.put(uid, surfaceV);
            }
        });
    }

   /* @Override
    public void onUserOffline(int uid, int reason) {
        Log.e(TAG, "onUserOffline: " + (uid & 0xFFFFFFFFL) + " " + guestId + " " + reason);
        if (uid == broadcaster.getId()) {
            if (!isBroadcastEnded)
                runOnUiThread(this::callAPIOffline);
        } else if (isBroadcaster) {
            if (guest != null) {
                if (uid == guest.getId()) {
                    Log.e(TAG, "onUserOffline: Four");
                    sendChannelMessage(guestId + level + SessionUser.getUser().getName() + " Guest Removed");
                }
            }
        }
        doRemoveRemoteUi(uid);
    }*/

    @Override
    public void onUserOffline(int uid, int reason) {
        Log.i("autolog", "uid: " + uid);

        if (uid == broadcaster.getId()) {
            if (!isBroadcastEnded){
                //                runOnUiThread(this::callAPIOffline);
            }
        } else {
            if (isBroadcaster) {
                for (int i = 0; i < mGuests.size(); i++) {
                    if (uid == mGuests.get(i).getId()) {
                        int finalI = i;
                        runOnUiThread(() -> callRemoveGuestAPI(mGuests.get(finalI).getUser_id()));
                        runOnUiThread(() -> {
                            if (finalI == 0) {
                                sendChannelMessage(removeGuestId + level + SessionUser.getUser().getName() + " Guest One Removed");
                                rlVideoMuteGuestOne.setVisibility(View.GONE);
                                rl_videoMuteGuestOne1.setVisibility(View.GONE);
                                rlVideoMuteGuestTwo.setVisibility(View.GONE);

//                                rl_videoMuteGuestOne1.removeAllViews();
                            } else {
                                sendChannelMessage(removeGuestId + level + SessionUser.getUser().getName() + " Guest Two Removed");
                                rlVideoMuteGuestTwo.setVisibility(View.GONE);

                            }
                        });
                    }
                }
            }
            doRemoveRemoteUi(uid);
        }

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
        Log.i("autolog", "uidresult: " + uid);
        doRenderRemoteUi(uid);
    }

    private void requestRemoteStreamType(final int currentHostCount) {
        Log.d(TAG, "requestRemoteStreamType " + currentHostCount);
        new Handler().postDelayed(() -> {
            HashMap.Entry<Integer, SurfaceView> highest = null;
            for (HashMap.Entry<Integer, SurfaceView> pair : mUidsList.entrySet()) {
                Log.d(TAG, "requestRemoteStreamType " + currentHostCount + " local " + (config().mUid & 0xFFFFFFFFL) + " " + (pair.getKey() & 0xFFFFFFFFL) + " " + pair.getValue().getHeight() + " " + pair.getValue().getWidth());
                if (pair.getKey() != config().mUid && (highest == null || highest.getValue().getHeight() < pair.getValue().getHeight())) {
                    if (highest != null) {
                        rtcEngine().setRemoteVideoStreamType(highest.getKey(), Constants.VIDEO_STREAM_LOW);
                        Log.d(TAG, "setRemoteVideoStreamType switch highest VIDEO_STREAM_LOW " + currentHostCount + " " + (highest.getKey() & 0xFFFFFFFFL) + " " + highest.getValue().getWidth() + " " + highest.getValue().getHeight());
                    }
                    highest = pair;
                } else if (pair.getKey() != config().mUid && (highest != null && highest.getValue().getHeight() >= pair.getValue().getHeight())) {
                    rtcEngine().setRemoteVideoStreamType(pair.getKey(), Constants.VIDEO_STREAM_LOW);
                    Log.d(TAG, "setRemoteVideoStreamType VIDEO_STREAM_LOW " + currentHostCount + " " + (pair.getKey() & 0xFFFFFFFFL) + " " + pair.getValue().getWidth() + " " + pair.getValue().getHeight());
                }
            }
            if (highest != null && highest.getKey() != 0) {
                rtcEngine().setRemoteVideoStreamType(highest.getKey(), Constants.VIDEO_STREAM_HIGH);
                Log.d(TAG, "setRemoteVideoStreamType VIDEO_STREAM_HIGH " + currentHostCount + " " + (highest.getKey() & 0xFFFFFFFFL) + " " + highest.getValue().getWidth() + " " + highest.getValue().getHeight());
            }
        }, 500);
    }

    /*private void doRemoveRemoteUi(final int uid) {
        runOnUiThread(() -> {
            if (isFinishing()) {
                return;
            }

            mUidsList.remove(uid);

            if (isBroadcaster) {
                if (mUidsList.size() == 1) {
                    guestUid = 0;
                    guest = null;
                    rlGuestClose.setVisibility(View.GONE);
                    rlGuestTwoClose.setVisibility(View.GONE);

                    guestOne.setVisibility(View.GONE);
                    guestTwo.setVisibility(View.GONE);
                    guestOne.removeAllViews();
                    guestTwo.removeAllViews();
                    isVideoMute = false;
                }
            } else if (isAudience) {
                if (mUidsList.size() == 1) {
                    rlGuestClose.setVisibility(View.GONE);
                    rlGuestTwoClose.setVisibility(View.GONE);

                    guestOne.setVisibility(View.GONE);
                    guestTwo.setVisibility(View.GONE);
                    guestOne.removeAllViews();
                    guestTwo.removeAllViews();
                    isVideoMute = false;
                }
            }

            Log.e(TAG, "doRemoveRemoteUi " + (uid & 0xFFFFFFFFL));

            if (mUidsList.size() == 1)
                mViewType = VIEW_TYPE_DEFAULT;
            else if (mUidsList.size() == 2)
                mViewType = VIEW_TYPE_SMALL;
            else if (mUidsList.size() == 3)
                mViewType = VIEW_TYPE_SMALL;

            if (mViewType == VIEW_TYPE_DEFAULT) {
                switchToDefaultVideoView();
            } else if (mViewType == VIEW_TYPE_SMALL) {
                switchToSmallVideoView(broadcaster.getId());
            }
        });
    }*/

    private void doRemoveRemoteUi(final int uid) {
        runOnUiThread(() -> {
            if (isFinishing()) {
                return;
            }
            Log.i("autolog", "mUidsList: " + mUidsList.size());
            mUidsList.remove(uid);
            Log.i("autolog", "mUidsList: " + mUidsList.size());
            Log.i("autolog", "uid: " + uid);

            if (isBroadcaster) {
                if (mUidsList.size() == 1) {
                    guestUid = 0;
                    guest = null;
                    rlGuestClose.setVisibility(View.GONE);
                    rlGuestTwoClose.setVisibility(View.VISIBLE);
                    ivpk.setVisibility(View.VISIBLE);
                    ivFrame.setVisibility(View.GONE);
                    guestOne.setVisibility(View.GONE);
                    guestTwo.setVisibility(View.GONE);
                    guestOne.removeAllViews();
                    guestTwo.removeAllViews();
                    isVideoMute = false;
                }
            } else if (isAudience) {
                if (mUidsList.size() == 1) {
                    rlGuestClose.setVisibility(View.GONE);
                    rlGuestTwoClose.setVisibility(View.VISIBLE);
                    ivFrame.setVisibility(View.GONE);
                    guestOne.setVisibility(View.GONE);
                    guestTwo.setVisibility(View.GONE);
                    guestOne.removeAllViews();
                    guestTwo.removeAllViews();
                    isVideoMute = false;
                }
            }

            if (mUidsList.size() == 1) {
                if (ispknow) {
                    if (pkGuest || isBroadcaster) {
                        pkdisconnet();
//                        channelredirection(SessionUser.getUser().getUsername(), "0");
                    }
                } else {
                }
            } else {
                pkvisible();
            }

            if (mUidsList.size() == 1)
                mViewType = VIEW_TYPE_DEFAULT;
            else if (mUidsList.size() == 2)
                if (ispknow) {
                    mViewType = VIEW_TYPE_DEFAULT;
                } else {
                    mViewType = VIEW_TYPE_SMALL;
                }
            else if (mUidsList.size() == 3)
                mViewType = VIEW_TYPE_SMALL;

            if (mViewType == VIEW_TYPE_DEFAULT) {
                switchToDefaultVideoView();
            } else if (mViewType == VIEW_TYPE_SMALL) {
                switchToSmallVideoView(broadcaster.getId());
            }
            if (isAudience) {
                ivJoin.setVisibility(View.VISIBLE);
            } else {
                ivJoin.setVisibility(View.GONE);
            }
        });

        Log.d(TAG, "doRemoveRemoteUi: " + mUidsList.size());


    }

   /* private void switchToDefaultVideoView() {

        Log.e(TAG, "switchToDefaultVideoView: " + mUidsList.size());

        rlVideoMuteGuestTwo.setVisibility(View.GONE);
        rl_videoMuteGuestOne1.setVisibility(View.GONE);

        ivFrame.setVisibility(View.GONE);
        ivFrameGuestTwo.setVisibility(View.GONE);

        if (mUidsList.size() == 0) {
            Log.e(TAG, "Broadcast has ended:: switchToDefaultVideoView");
            callAPIOffline();
        } else {
            mGridVideoViewContainer.initViewContainer(getApplicationContext(), SessionUser.getUser().getId(), mUidsList, true, isBroadcaster, isGuest1, ispknow);// first is now full view

            mViewType = VIEW_TYPE_DEFAULT;

            int sizeLimit = mUidsList.size();
            if (sizeLimit > Constants_app.MAX_PEER_COUNT + 1) {
                sizeLimit = Constants_app.MAX_PEER_COUNT + 1;
            }
        }
    }*/

    private void switchToDefaultVideoView() {
        try {
            Log.i("autolog-default", "mUidsList.size(): " + mUidsList.size());
            rlVideoMuteGuestTwo.setVisibility(View.GONE);
            rl_videoMuteGuestOne1.setVisibility(View.GONE);
            rlVideoMuteGuestTwo.setVisibility(View.GONE);
            ivFrame.setVisibility(View.GONE);
            ivFrameGuestTwo.setVisibility(View.GONE);
            if (mUidsList.size() == 0) {

                Log.e(TAG, "Broadcast has ended:: switchToDefaultVideoView");
                Log.i("autolog", "reasontwo: " + "reasontwo");

//            callAPIOffline();
            } else {
                if (!ispknow) {
                    tvFreeGiftCount.setVisibility(View.VISIBLE);
                }
                mGridVideoViewContainer.initViewContainer(getApplicationContext(), broadcaster.getId(), mUidsList, false, isBroadcaster, isGuest1, ispknow);
                if (mUidsList.size() != 2) {
                    pkStartBtn.setVisibility(View.GONE);
                } else if (mUidsList.size() == 2) {
                    if (isBroadcaster || pkGuest) {
                        pkStartBtn.setVisibility(View.VISIBLE);
                    }


                    if (!ispknow) {
                        firsstlay.setVisibility(View.GONE);
                        secondtlay.setVisibility(View.GONE);
                    }
                }
                mViewType = VIEW_TYPE_DEFAULT;

                int sizeLimit = mUidsList.size();
                if (sizeLimit > Constants_app.MAX_PEER_COUNT + 1) {
                    sizeLimit = Constants_app.MAX_PEER_COUNT + 1;
                }
                privateicon();
            }
        } catch (Exception e) {
            Log.i("autolog", "exception:default" + e.toString());

        }
    }

    private void switchToSmallVideoView(int uid) {
        try {
            if (!ispknow) {
                firsstlay.setVisibility(View.GONE);
                secondtlay.setVisibility(View.GONE);
                tvFreeGiftCount.setVisibility(View.VISIBLE);
            }
            Log.e(TAG, "switchToSmallVideoView: 0 " + mUidsList.size());
            ArrayList<VideoStatusData> mUsers = new ArrayList<>();
            mGridVideoViewContainer.initViewContainer(getApplicationContext(), uid, mUidsList, false, isBroadcaster, false, ispknow);
            for (HashMap.Entry<Integer, SurfaceView> entry : mUidsList.entrySet()) {
                if (entry.getKey() != uid) {
                    mUsers.add(0, new VideoStatusData(entry.getKey(), entry.getValue(), VideoStatusData.DEFAULT_STATUS, VideoStatusData.DEFAULT_VOLUME));
                }
            }
            Log.e(TAG, "switchToSmallVideoView: 1 " + mUsers.size());
            if (mUsers.size() == 1) {
                Log.e(TAG, "switchToSmallVideoView: " + "sangeeth");
                rlVideoMuteGuestOne.setVisibility(View.GONE);
                final VideoStatusData user = mUsers.get(0);
                SurfaceView surfaceView = user.mView;
                surfaceView.setZOrderOnTop(true);
                surfaceView.setZOrderMediaOverlay(true);
                guestTwo.setVisibility(View.VISIBLE);
                guestOne.removeAllViews();
                guestOne.setVisibility(View.GONE);
                guestTwo.removeAllViews();
                guestTwo.addView(surfaceView, 0);
                ivFrame.setVisibility(View.GONE);
                ivFrameGuestTwo.setVisibility(View.VISIBLE);
//                rlGuestTwoClose.removeAllViews();
                rlGuestTwoClose.setVisibility(View.GONE);
            } else if (mUsers.size() == 2) {
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
                                            tvConnecting.setVisibility(View.GONE);
                                            tvConnectingTwo.setVisibility(View.GONE);
                                            Log.e(TAG, "switchToSmallVideoView: " + " ARun");
                                            guestTwo.setVisibility(View.VISIBLE);
                                            guestOne.setVisibility(View.VISIBLE);
                                            Log.e(TAG, "switchToSmallVideoView   1: " + mUsers.get(0).mUid);
                                            Log.e(TAG, "switchToSmallVideoView   2: " + mUsers.get(1).mUid);
                                            if (usersResponse.getData().getGuests().size() == 2) {
                                                ivFrame.setVisibility(View.VISIBLE);
                                                ivFrameGuestTwo.setVisibility(View.VISIBLE);
                                                if (usersResponse.getData().getGuests().get(0).getId() == mUsers.get(0).mUid) {
                                                    if (guestOne.getChildCount() > 0) {
                                                        guestOne.removeAllViews();
                                                    }
                                                    if (guestTwo.getChildCount() > 0) {
                                                        guestTwo.removeAllViews();
                                                    }
                                                    final VideoStatusData user = mUsers.get(0);
                                                    SurfaceView surfaceView = user.mView;
                                                    surfaceView.setZOrderOnTop(true);
                                                    surfaceView.setZOrderMediaOverlay(true);
                                                    stripView(surfaceView);
                                                    guestTwo.addView(surfaceView, 0);
                                                    final VideoStatusData user2 = mUsers.get(1);
                                                    SurfaceView surfaceView1 = user2.mView;
                                                    surfaceView1.setZOrderOnTop(true);
                                                    surfaceView1.setZOrderMediaOverlay(true);
                                                    stripView(surfaceView1);
                                                    guestOne.addView(surfaceView1, 0);


                                                } else if (usersResponse.getData().getGuests().get(0).getId() == mUsers.get(1).mUid) {
                                                    if (guestOne.getChildCount() > 0) {
                                                        guestOne.removeAllViews();
                                                    }
                                                    if (guestTwo.getChildCount() > 0) {
                                                        guestTwo.removeAllViews();
                                                    }
                                                    final VideoStatusData user = mUsers.get(1);
                                                    SurfaceView surfaceView = user.mView;
                                                    surfaceView.setZOrderOnTop(true);
                                                    surfaceView.setZOrderMediaOverlay(true);
                                                    stripView(surfaceView);
                                                    guestTwo.addView(surfaceView, 0);
                                                    final VideoStatusData user2 = mUsers.get(0);
                                                    SurfaceView surfaceView1 = user2.mView;
                                                    surfaceView1.setZOrderOnTop(true);
                                                    surfaceView1.setZOrderMediaOverlay(true);
                                                    stripView(surfaceView1);
                                                    guestOne.addView(surfaceView1, 0);
                                                }
                                            } else {
                                                showToast("Something goes wrong");
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
            }
            requestRemoteStreamType(mUidsList.size());
            videomute();

        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    private void videomute() {

        rlVideoMuteGuestTwo.setVisibility(View.GONE);
        rl_videoMuteGuestOne1.setVisibility(View.GONE);
        rlVideoMuteGuestOne.setVisibility(View.GONE);


        if (guestonemute) {
            rl_videoMuteGuestOne1.setVisibility(View.VISIBLE);
        }
        Log.d(TAG, "guestonemute: " + guestonemute + guesttwomute);
    }

    public static void stripView(SurfaceView view) {
        ViewParent parent = view.getParent();
        if (parent != null) {
            ((FrameLayout) parent).removeView(view);
        }
    }

    public int mViewType = VIEW_TYPE_DEFAULT;

    public static final int VIEW_TYPE_DEFAULT = 0;

    public static final int VIEW_TYPE_SMALL = 1;

    private void callAlertKickOut() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityLiveRoom.this);
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

    private void removeCallBack() {
        try {
            mChatManager.removeChatHandler(mChatHandler);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    private void CallAlertDialog(String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityLiveRoom.this);
        alertDialogBuilder.setMessage(broadcaster.getName() + " has requested you to Join the Broadcast !");
        alertDialogBuilder.setPositiveButton("Accept",
                (arg0, arg1) -> {
                    String id1 = msg.substring(0, 6);
                    guestId = id1;
                    isGuest = true;
                    isGuestRequest = true;
                    tvConnecting.setVisibility(View.GONE);
                    tvConnectingTwo.setVisibility(View.GONE);
                    /*fabMic.setBackground(getResources().getDrawable(R.mipmap.mic));*/
                    ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.videomute));
                    /*fabMic.setTag(false);*/
                    callAddGuestAPI(SessionUser.getUser().getUser_id(), broadcasterId);
                });

        alertDialogBuilder.setNegativeButton("Reject",
                (DialogInterface arg0, int arg1) -> {
                    sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " has rejected your video call request");
                    arg0.dismiss();
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        callEndHandler.postDelayed(() -> {
            if (alertDialog.isShowing()) {
                sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " has rejected your video call request");
                alertDialog.dismiss();
            }
        }, 10000);
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
                                rlBulletlay.setVisibility(View.VISIBLE);
                                String profile = profileResponse.getData().getUser().getProfile_pic();
                                String profileName = profileResponse.getData().getUser().getName();
                                tvBulletmsg.setText(bulletMessage);
                                tvBulletName.setText(profileName);
                                if (!profile.isEmpty()) {
                                    Picasso.get().load(profile).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivBulletUserImg);
                                } else
                                    Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivBulletUserImg);

                                rlBulletlay.setVisibility(View.VISIBLE);

                                TranslateAnimation anim = new TranslateAnimation(900f, -500f, 0.0f, 0.0f);  // might need to review the docs
                                anim.setDuration(7000); // set how long you want the animation

                                rlBulletlay.setAnimation(anim);
                                rlBulletlay.setVisibility(View.GONE);

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


    private void callAdapterRequests(ArrayList<Audience> mRequests) {
        if (mRequests.size() > 0) {
            rvRequests.setAdapter(adapterRequests);
            tvNoRequests.setVisibility(View.GONE);
            rvRequests.setVisibility(View.VISIBLE);
        } else {
            tvNoRequests.setVisibility(View.VISIBLE);
            rvRequests.setVisibility(View.GONE);
        }
    }

    private void callSwitchCase(int j) {

        try {
            Log.i("autolog", "callSwitchCase: " + j);
            isGiftShowing = true;
            for (int i = 0; i < giftsList.size(); i++) {
                if (giftMessages.get(j).getMessage().contains(giftsList.get(i).getName())) {
                    if (giftsList.get(i).getType().equalsIgnoreCase("normal")) {
                        Picasso.get().load(giftsList.get(i).getThumbnail()).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivGiftItem);
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
                        Log.e(TAG, "callSwitchCase: gif");
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
            Log.e(TAG, "callSwitchCase: " + e.toString());
        }
    }

    private void callAddGiftValue(int k) {
        Log.i("autolog", "callAddGiftValue  k: " + k);
        try {
            isGoldAdding = true;
            int price = 0;
            for (int i = 0; i < giftsList.size(); i++) {
                if (messagesList.get(k).contains(giftsList.get(i).getName())) {
                    String currentString = messagesList.get(k);
                    String[] separated = currentString.split(" X");
                    String split = separated[1];
                    Log.i("autolog", "split: " + split);

                    if (messagesList.get(k).contains("X25")) {
                        price = Integer.valueOf(giftsList.get(i).getPrice()) * 25;
                    } else if (messagesList.get(k).contains("X50")) {
                        price = Integer.valueOf(giftsList.get(i).getPrice()) * 50;
                    } else if (messagesList.get(k).contains("X100")) {
                        price = Integer.valueOf(giftsList.get(i).getPrice()) * 100;
                    } else if (messagesList.get(k).contains("X200")) {
                        price = Integer.valueOf(giftsList.get(i).getPrice()) * 200;
                    } else {
                        price = Integer.valueOf(giftsList.get(i).getPrice());
                    }
                }
            }

            gold = gold + price;
            tvReceived.setText(String.valueOf(gold));
            Log.e(TAG, "callAddGiftValue: " + gold);
            int tempMoonValue = Integer.valueOf(moonValue) + price;
            moonValue = String.valueOf(tempMoonValue);
            int times = Integer.valueOf(moonValue) / 8100;
            tvMoonLevelCount.setText("5x" + times);
            /*loadStarImage(moonValue);*/

            temp2 = temp2 + 1;
            if (messagesList.size() > temp2) {
                callAddGiftValue(temp2);
            } else {
                temp2 = 0;
                messagesList.clear();
                isGoldAdding = false;
            }
        } catch (Exception e) {

        }
    }

    private void setGifGift(Gift gift) {
        Log.i("autolog", "setGifGift: " + gift.getName());
        String completePath = getApplicationContext().getFilesDir() + "/.webPStorage/" + gift.getName() + ".webp";
        ivImng.setVisibility(View.VISIBLE);

        File fileNew = new File(completePath);
        if (fileNew.exists()) {
            //Log.i("autolog", "exists" + fileNew.exists());
            Glide.get(this).clearMemory();
            Glide.with(this)
                    .load(fileNew)
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
                    .into(ivImng);
        } else {
            Log.i("autolog", "exists" + fileNew.exists());
            try {
                Glide.get(this).clearMemory();
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
                        .into(ivImng);
            } catch (Exception e1) {
                Crashlytics.logException(e1);
            }
        }

        giftHandler.postDelayed(() -> {
            ivImng.setVisibility(View.GONE);
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
        super.onDestroy();
        utils.deleteCache(this);
        if (worker().getRtcEngine() != null) {
            worker().getRtcEngine().stopChannelMediaRelay();
        }
        if (mChatManager != null) {
            mChatManager.leaveChannel();
            mChatManager.removeChatHandler(mChatHandler);
        }
        doLeaveChannel();
        if (mRtmChannel != null) {
            Log.d(TAG, "onDestroyrtm: ");
            mRtmChannel.release();
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(pkGuestAccept);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(pkGuestrequest);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(pkReject);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(PK_MESSAGE);
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "onStop: ");
        /*if (!isRedirectScreen) {
            Log.e(TAG, "onStop: finishAndRemoveTask");
            finishAndRemoveTask();
        }*/
        super.onStop();
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

    private void doSwitchToBroadcaster(boolean broadcaster) {
        final int currentHostCount = mUidsList.size();
        final int uid = SessionUser.getUser().getId();
        Log.e(TAG, "doSwitchToBroadcaster " + currentHostCount + " " + (uid & 0XFFFFFFFFL) + " " + broadcaster);
        if (broadcaster) {
            doConfigEngine(Constants.CLIENT_ROLE_BROADCASTER);
            bottomBroadcaster.setVisibility(View.VISIBLE);
            bottomAudience.setVisibility(View.GONE);
            if (!isBroadcaster) {
                ivGift.setVisibility(View.VISIBLE);

            } else {
                ivGift.setVisibility(View.GONE);

            }
            /*fabMic.setVisibility(View.VISIBLE);*/
            new Handler().postDelayed(() -> {
                doRenderRemoteUi(uid);
                ImageView button2 = findViewById(R.id.btn_2);
                /*FloatingActionButton button3 = findViewById(R.id.fb_mic);*/
                broadcasterUI(button2/*, button3*/);
            }, 1000); // wait for reconfig engine
        } else {
            stopInteraction(currentHostCount, uid);
            /*fabMic.setVisibility(View.GONE);*/
            bottomBroadcaster.setVisibility(View.GONE);
            bottomAudience.setVisibility(View.VISIBLE);
            ivVideoMute.setVisibility(View.VISIBLE);
            rlGuestTwoClose.setVisibility(View.GONE);
            ivGift.setVisibility(View.VISIBLE);
        }
    }

    private void stopInteraction(final int currentHostCount, final int uid) {
        doConfigEngine(Constants.CLIENT_ROLE_AUDIENCE);

        new Handler().postDelayed(() -> {
            doRemoveRemoteUi(uid);
            ImageView button2 = findViewById(R.id.btn_2);
            audienceUI(button2);
        }, 1000); // wait for reconfig engine
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
        intent.putExtra("gold", String.valueOf(Integer.parseInt(tvReceived.getText().toString()) - oldGold));
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

    @Override
    public void OnClicked(Gift gift) {
        mGift = null;
        mGift = gift;
        List<String> ExclamatoryList = Arrays.asList(gift.getCombo_pack().split("!"));
        List<String> Spinnerlist = Arrays.asList(ExclamatoryList.get(Integer.parseInt(gift.getCombo())).split(","));
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, Spinnerlist);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCombo.setAdapter(spinnerArrayAdapter);
        if (gift.getCombo().equalsIgnoreCase("0")) {
            llCombo.setVisibility(View.GONE);
            comboMultiplier = 0;
        } else {
            llCombo.setVisibility(View.VISIBLE);
        }
    }

    private void callEntranceEffect(int j) {
        try {
            String name = entranceEffects.get(j).getAccount();
            String id = entranceEffects.get(j).getId();
            String url = entranceEffects.get(j).getUrl();
            Log.e(TAG, "callEntranceEffect: " + entranceEffects.get(j).getUrl());
            Log.e(TAG, "callEntranceEffect: " + url);
            if (name.length() > 14) {
                name = name.substring(0, 14);
            }

            isEntranceEffects = true;
            Glide.with(this)
                    .load(url)
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

    public void onRemoveSuccess(ArrayList<Audience> audiences, int viewers_count) {

        viewers = viewers_count;
        if (audiences.size() > 0)
            tvCount.setText(String.valueOf(viewers));

        if (!isBroadcastEnded) {
            try {
                if (position != -1 && position < users.size()) {
                    doLeaveChannel();
                    mUidsList.clear();
                    Constants_app.cleanMessageListBeanList();
                } else {
                    showToast("Not any more Live User");
                }
            } catch (Exception e) {
                Log.i("autolog", "e: " + e);

            }

        }
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
            LayoutInflater layoutInflater = LayoutInflater.from(ActivityLiveRoom.this);
            final View dialogView = layoutInflater.inflate(R.layout.pop_up_manage, null);
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityLiveRoom.this);

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
                textmutelist.add(audience.getUser_id());
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

            LayoutInflater layoutInflater = LayoutInflater.from(ActivityLiveRoom.this);
            final View dialogView = layoutInflater.inflate(R.layout.pop_up_report, null);
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityLiveRoom.this);

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

        ivPic.setOnClickListener(v -> {
            ivPic.setClickable(false);
           /* if (!isClicked) {
                isClicked = true;*/
            Log.e(TAG, "showAlertViewProfile: ivPic ");
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
            Log.i("autolog", "mGuests: " + mGuests.size() + audience.getUser_id());


            Log.i("autolog", "isCallEnd: " + isCallEnd);
            Log.e(TAG, "showAlertViewProfile: ");
            if (!ispknow) {

                Log.e(TAG, "showAlertViewProfile: 1" + mUidsList.size() + isGuest1 + isGuest2);
                if (!isCallEnd) {
                    Log.e(TAG, "showAlertViewProfile: 2");
                    if (mUidsList.size() != 3) {
                        Log.e(TAG, "showAlertViewProfile: 3");
                        isCallRequested = true;
                        boolean isExits = false;
                        for (int i = 0; i < mGuests.size(); i++) {
                            Log.i("autolog", "mGuests: " + mGuests.get(i).getUser_id() + audience.getUser_id());
                            if (mGuests.get(i).getUser_id().equalsIgnoreCase(audience.getUser_id())) {
                                isExits = true;
                            }
                        }
                        if (!isExits) {
                            int audiencelevel = Integer.parseInt(audience.getLevel());
                            Log.i("autolog", "audiencelevel: " + audiencelevel);

                            if (audiencelevel > 10) {
                                isCallEnd = true;
                                sendChannelMessage(audience.getUser_id() + level + SessionUser.getUser().getName() + " has requested you to join the Video Call");
                            } else {
                                showToast("User is not yet reached level 10 to make call");
                            }

                            if (!isGuest1) {
                                tvConnecting.setVisibility(View.VISIBLE);
                            } else if (!isGuest2) {
                                tvConnectingTwo.setVisibility(View.VISIBLE);
                            }
                        } else
                            showToast("User is already added to Broadcast !");
                    } else {
                        showToast("Can't add more than two visitor to the Broadcast!");
                    }
                }
            } else {
                showToast("You are in Pk");
            }
            callEndHandler.postDelayed(() -> {
                tvConnecting.setVisibility(View.GONE);
                tvConnectingTwo.setVisibility(View.GONE);
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

                if (ispknow) {
                    if (audience.getUser_id().equalsIgnoreCase(pkuserid)) {
                        showToast("This Is Your Pk Guest You Can't Kickout In Pk");
                    } else {
                        sendChannelMessage(audience.getUser_id() + level + audience.getName() + " has bEEn kicKed OuT");
                        callBlockAPI(audience.getUser_id());
                        MessageBean message = new MessageBean(selfName, SessionUser.getUser().getUser_id() + level + audience.getName() + " has been kicked out", true, false, false);
                        messageBeanList.add(message);
                        adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                        rvMessages.scrollToPosition(messageBeanList.size() - 1);
                        blockedlist.add(audience.getUser_id());
                    }
                } else {
                    ArrayList<Audience> guestarr = new ArrayList<>();
                    guestarr.clear();
                    if (utils.isNetworkAvailable()) {
                        if (utils.isNetworkAvailable()) {
                            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
                            Call<UsersResponse> call = apiClient.getGuests(SessionUser.getUser().getUser_id(), broadcasterId);
                            call.enqueue(new retrofit2.Callback<UsersResponse>() {
                                @Override
                                public void onResponse(@NonNull Call<UsersResponse> call, @NonNull Response<UsersResponse> response) {
                                    UsersResponse usersResponse = response.body();
                                    Log.i("autolog", "response: " + response.raw().request().url());
                                    if (response.code() == 200) {
                                        if (usersResponse != null) {
                                            if (usersResponse.getStatus().equalsIgnoreCase("success")) {
                                                for (int i = 0; i <usersResponse.getData() .getGuests().size(); i++) {
                                                    guestarr.add(usersResponse.getData().getGuests().get(i));
                                                }

                                                isClickedProfile = false;
                                                Boolean guestclick = false;
                                                try {
                                                    guestclick=false;
                                                    for (int i = 0; i < guestarr.size(); i++) {
                                                        Log.i("autolog", "guestarr: " + guestarr.get(i).getUser_id());
                                                        if (audience.getUser_id().equalsIgnoreCase(guestarr.get(i).getUser_id())) {
                                                            Log.i("autolog", "guestarr: " + guestarr.get(i).getUser_id());
                                                            guestclick = true;
                                                        }
                                                    }

                                                }catch (Exception e){
                                                    guestclick=false;

                                                }

                                                if (!guestclick) {
                                                    sendChannelMessage(audience.getUser_id() + level + audience.getName() + " has bEEn kicKed OuT");
                                                    callBlockAPI(audience.getUser_id());
                                                    MessageBean message = new MessageBean(selfName, SessionUser.getUser().getUser_id() + level + audience.getName() + " has been kicked out", true, false, false);
                                                    messageBeanList.add(message);
                                                    adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                                                    rvMessages.scrollToPosition(messageBeanList.size() - 1);
                                                    blockedlist.add(audience.getUser_id());
                                                } else {
                                                    showToast("You can't KickOut Your Guest");
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
                }
            }
        });

        llTopFans.setOnClickListener(v -> {
            isRedirectScreen = true;
            Intent intent = new Intent(ActivityLiveRoom.this, ActivityTopFans.class);
            intent.putExtra("activationToken", audience.getActivation_code());
            intent.putExtra("user_id", audience.getUser_id());
            startActivity(intent);
        });

        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
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

    public void onClickGuestCall(View view) {
        if (SessionUser.getUser().getUser_id().equals(guest.getUser_id())) {
            showGuestProfile(guest, 1);
        } else if (isBroadcaster) {
            showBroadcasterControl(guest, 1);
        } else {
            showAlertViewProfile(guest, false);
        }
    }

    public void onClickGuestCallTwo(View view) {
        if (SessionUser.getUser().getUser_id().equals(guest2.getUser_id())) {
            Log.e(TAG, "onClickGuestCallTwo:  ");
            showGuestProfile(guest2, 1);
            showToast("HIIIIII");
        } else if (isBroadcaster) {
            showBroadcasterControl(guest2, 2);
            showToast("JJJJJJJ");
        } else if (isAudience) {
            showAlertViewProfile(guest2, false);
            showToast("BYEEEEEE");
        }
    }

    private void showBroadcasterControl(Audience guestUser, int guestCount) {
        Dialog alertDialog = new Dialog(this);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.alert_end_call);

        RelativeLayout root = alertDialog.findViewById(R.id.root);
        TextView tvName = alertDialog.findViewById(R.id.tv_name);
        LinearLayout llEndCall = alertDialog.findViewById(R.id.ll_endcall);
        LinearLayout llProfile = alertDialog.findViewById(R.id.ll_kick_out);
        TextView tvMute = alertDialog.findViewById(R.id.tv_mute);
        ImageView ivPic = alertDialog.findViewById(R.id.iv_profile);
        LinearLayout llAudioMute = alertDialog.findViewById(R.id.ll_audio_mute);
        LinearLayout llTopFans = alertDialog.findViewById(R.id.ll_topFans);
        RelativeLayout rlReport = alertDialog.findViewById(R.id.rl_report);
        LinearLayout llView = alertDialog.findViewById(R.id.ll_view);
        LinearLayout llView1 = alertDialog.findViewById(R.id.ll_view_1);
        TextView tvBGold = alertDialog.findViewById(R.id.tv_bGold);
        TextView tvLevel = alertDialog.findViewById(R.id.tv_level);
        tvName.setText(guestUser.getName());
        tvBGold.setText(guestUser.getOver_all_gold());
        tvLevel.setText(" Lv : " + guestUser.getLevel() + " ");

        root.setOnClickListener(v -> alertDialog.dismiss());

        ivPic.setOnClickListener(v -> {
            if (!isClicked) {
                isClicked = true;
                gotoProfile(Constants_app.decodeImage(guestUser.getProfile_pic()), guestUser.getUser_id());
            }
        });

        llProfile.setOnClickListener(v -> {
            if (!isClicked) {
                isClicked = true;
                gotoProfile(Constants_app.decodeImage(guestUser.getProfile_pic()), guestUser.getUser_id());
            }
        });

        if (!guestUser.getProfile_pic().isEmpty()) {
            Picasso.get().load(guestUser.getProfile_pic()).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivPic);
        } else
            Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivPic);

        llEndCall.setOnClickListener(v -> {
            removeGuestApi(guestUser.getUser_id(), false, true, guestCount);
            alertDialog.dismiss();
        });

        rlReport.setOnClickListener(v -> {

            LayoutInflater layoutInflater = LayoutInflater.from(ActivityLiveRoom.this);
            final View dialogView = layoutInflater.inflate(R.layout.pop_up_report, null);
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityLiveRoom.this);

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
                callReportAPI(guestUser.getUser_id(), tvpr1);
                alertDialog12.dismiss();
            });

            tvRpl2.setOnClickListener(v12 -> {
                String tvpr2 = "Illegal or Violence";
                callReportAPI(guestUser.getUser_id(), tvpr2);
                alertDialog12.dismiss();
            });

            tvRpl3.setOnClickListener(v13 -> {
                String tvpr3 = "Endanger Personal Safety";
                callReportAPI(guestUser.getUser_id(), tvpr3);
                alertDialog12.dismiss();
            });

            tvRpl4.setOnClickListener(v14 -> {
                String tvpr4 = "Illegal Avatar";
                callReportAPI(guestUser.getUser_id(), tvpr4);
                alertDialog12.dismiss();
            });

            tvRpl5.setOnClickListener(v15 -> {
                String tvpr5 = "others";
                callReportAPI(guestUser.getUser_id(), tvpr5);
                alertDialog12.dismiss();
            });
        });

        llAudioMute.setOnClickListener(v -> {
            if (guestUid != 0) {
                if (!isAudioMuted) {
                    worker().getRtcEngine().muteRemoteAudioStream(guestUid, true);
                    tvMute.setText(R.string.unMute);
                    isAudioMuted = true;
                } else {
                    worker().getRtcEngine().muteRemoteAudioStream(guestUid, false);
                    tvMute.setText(R.string.mute);
                    isAudioMuted = false;
                }
            }
        });

        llTopFans.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityLiveRoom.this, ActivityTopFans.class);
            intent.putExtra("activationToken", guestUser.getActivation_code());
            intent.putExtra("user_id", guestUser.getUser_id());
            startActivity(intent);
        });

        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);
    }

    public void showGuestProfile(Audience guest, int guestCount) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityLiveRoom.this);
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

        tvGuestGoldSent.setText(guest.getTotal_gift_send());
        tvMaxTarget.setText(guest.getBroadcasting_hours());
        tvMinTarget.setText(guest.getBroadcasting_min_target());
        tvFriends.setText(guest.getFriendsCount());
        tvFollowers.setText(guest.getFollowersCount());
        tvFollowings.setText(guest.getFansCount());
        String totalShareProgress = guest.getShare() + "/" + guest.getShare_target();
        String totalGoldProgress = guest.getGold() + "/" + guest.getGold_target();
        String totalViewersProgress = viewers + "/" + guest.getViewers_target();
        tvShareProgress.setText(totalShareProgress);
        tvGoldProgress.setText(totalGoldProgress);
        tvViewersProgress.setText(totalViewersProgress);

        Glide.with(getApplicationContext())
                .load(guest.getDpEffects())
                .into(ivEffect);

        try {
            int shareProgress = Integer.parseInt(guest.getShare_target());
            int goldProgress = Integer.parseInt(guest.getGold_target());
            int viewersProgress = Integer.parseInt(guest.getViewers_target());
            int share = Integer.parseInt(guest.getShare());
            int gold = Integer.parseInt(guest.getGold());

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

        tvGuestLevel.setText(guest.getLevel());
        tvGuestGold.setText(guest.getTotal_gift_receiver());
        tvGuestName.setText(guest.getName());
        tvBliveId.setText(guest.getReference_user_id());

        if (!guest.getProfile_pic().isEmpty()) {
            Picasso.get().load(guest.getProfile_pic()).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivGuestProfile);
        } else
            Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivGuestProfile);

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

   /* @Override
    public void onAcceptRequest(Audience user, int position) {
        Log.e(TAG, "onAcceptRequest: " + mUidsList.size());
        if (guest == null && mUidsList.size() == 1) {
            Log.e(TAG, "onAcceptRequest: accepted" + mUidsList.size());
            isVideoMute = false;
            guest = user;
            rl_notify.setVisibility(View.GONE);
            mRequests.remove(position);
            adapterRequests.notifyDataSetChanged();
            cvRequests.startAnimation(slideDown);
            cvRequests.setVisibility(View.GONE);
            guestId = user.getUser_id();
            ivBlur.setVisibility(View.GONE);
            tvVideoMute.setVisibility(View.GONE);
            rlGuestClose.setVisibility(View.VISIBLE);
            ivFrame.setVisibility(View.GONE);
            guestOne.setVisibility(View.VISIBLE);
            ivBlur.setVisibility(View.GONE);
        *//*    rlGuestPause.setVisibility(View.GONE);
            ivGuestPause.setVisibility(View.GONE);*//*
            ImageView bSwitchCam = findViewById(R.id.btn_2);
            bSwitchCam.setVisibility(View.VISIBLE);
            sendChannelMessage(user.getUser_id() + level + SessionUser.getUser().getName() + " Broadcaster has accepted your broadcast request");
        } else if (mUidsList.size() == 2) {
            guest2 = user;
            mRequests.remove(position);
            adapterRequests.notifyDataSetChanged();
            cvRequests.startAnimation(slideDown);
            cvRequests.setVisibility(View.GONE);
            guestTwo.setVisibility(View.VISIBLE);
            sendChannelMessage(user.getUser_id() + level + SessionUser.getUser().getName() + " Broadcaster has accepted your broadcast request");
        } else {
            showToast("Can't add more than two visitor to the Broadcast!");
        }
    }*/

    @Override
    public void onAcceptRequest(Audience user, int position) {
        if (!isGuest1 || !isGuest2) {
            Log.e(TAG, "onAcceptRequest: " + isGuest1 + isGuest2);
            if (isAcceptRequest) {
                isAcceptRequest = false;
                cvRequests.startAnimation(slideDown);
                cvRequests.setVisibility(View.GONE);
                callAddGuestAPI(user.getUser_id(), broadcasterId);
                mPosition = position;
                tempGuest = user;
            }
        } else {
            showToast("Can't add more than two visitor to the Broadcast!");
        }
    }

    @Override
    public void onRejectRequest(Audience user, int position) {
        mRequests.remove(position);
        adapterRequests.notifyDataSetChanged();
        if (mRequests.size() == 0) {
            rvRequests.setVisibility(View.GONE);
            tvNoRequests.setVisibility(View.VISIBLE);
            tvCountReq.setText("" + mRequests.size());
        } else {
            tvNoRequests.setVisibility(View.GONE);
            rvRequests.setVisibility(View.VISIBLE);
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
        sendChannelMessage(user.getUser_id() + level + SessionUser.getUser().getName() + " has rejected your video call request");

    }

    public void setGifts(ArrayList<Gift> gifts, ArrayList<Gift> tools, ArrayList<Gift> freeGift) {

        giftsList.clear();
        giftsList.addAll(gifts);
        giftTools.addAll(tools);
        freeGifts.addAll(freeGift);
        adapter.setGifts(gifts);

        adapterGifts = new AdapterGifts(this, giftsList);
        adapterGifts.setOnClickListener(this);
        rvGift.setAdapter(adapterGifts);

        callAudiencesAPI(broadcaster.getUser_id());
    }

    public void onClickSwitchCam(View view) {
        if (isGuest) {
            ivSwitchCam.setOnClickListener(v -> {
                worker().getRtcEngine().switchCamera();
            });

            ivSwitchCam.setVisibility(View.GONE);
            ivVideoMute.setVisibility(View.GONE);
            ivGuestClose.setVisibility(View.GONE);
        }
    }

    /*public void onClickVideoMute(View view) {
        if (isGuest) {
            if (!isVideoMute) {
                videoFlag = true;
                isVideoMute = true;
                worker().getRtcEngine().muteLocalVideoStream(true);
                sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " Guest Video Muted");
                ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.video_unmute));
                ivFrame.setVisibility(View.GONE);
            } else {
                videoFlag = false;
                isVideoMute = false;
                worker().getRtcEngine().muteLocalVideoStream(false);
                ivFrame.setVisibility(View.GONE);
                sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " Guest Video UnMuted");
                ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.videomute));
            }
        }
    }*/

    public void onClickVideoMute(View view) {
        try {
            callGuestVideoMute();
        } catch (Exception e) {
            Log.i("autolog", "e: " + e);
            Crashlytics.logException(e);
        }
    }

    public void OnClickCallEnd(View view) {
        if (isBroadcaster) {
            sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " BroadCaster Call End");
            isCallEnd = false;
            rlGuestClose.setVisibility(View.GONE);
            rlGuestTwoClose.setVisibility(View.GONE);
            ivFrame.setVisibility(View.GONE);
            ivFrameGuestTwo.setVisibility(View.GONE);
            tvConnecting.setVisibility(View.GONE);
            tvConnectingTwo.setVisibility(View.GONE);
        }
    }

    public void OnClickGuestClose(View view) {
        isRequested = false;
        isGuest = false;
        /*fabMic.setBackground(getResources().getDrawable(R.mipmap.mic));*/
        ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.videomute));
        /*fabMic.setTag(false);*/
        doSwitchToBroadcaster(false);
        removeGuest();
    }

    public void onClickMoonLevel(View view) {
        if (utils.isNetworkAvailable()) {
            dailyAndWeeklyGold = "daily";
            try {
                showContributors(broadcaster.getName(), tvReceived.getText().toString());
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
        }
    }

    @Override
    public void OnClicked(Audience audience) {
        if (!isClicked) {
            isClicked = true;
            gotoProfile(Constants_app.decodeImage(audience.getProfile_pic()), audience.getUser_id());
        }
    }

    public void gotoProfile(String image, String userId) {
        isRedirectScreen = true;
        Intent intent = new Intent(ActivityLiveRoom.this, ActivityViewProfile.class);
        intent.putExtra("image", image);
        intent.putExtra("userId", userId);
        intent.putExtra("from", "liveRoom");
        startActivity(intent);
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

    @Override
    public void OnClicked(User user) {

    }

    public void onTopListSuccess(ArrayList<User> dailyUsers, ArrayList<User> weeklyUsers) {
        try {
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
            Log.e("topperError", e.getMessage());
        }
    }

    @Override
    public void onClickedActiveAudience(Audience audience) {
        if (isBroadcaster) {
            showAlertViewProfile(audience, true);
        } else if (!audience.getUser_id().equals(SessionUser.getUser().getUser_id())) {
            showAlertViewProfile(audience, false);
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

    /**
     * API CALLBACK: rtm channel event listener
     */
    private void onMessageReceive(final RtmMessage text, final RtmChannelMember fromMember) {

        runOnUiThread(() -> {
            String account = fromMember.getUserId();
            String msg = text.getText();
            Log.e(TAG, "onMessageReceived account = " + account + " msg = " + msg);
            int colorchange = 0;
            if (SessionUser.getUser().getUsername().equalsIgnoreCase(account)) {
                if (ispknow) {
                    if (broadcasterAudience || isBroadcaster) {
                        colorchange = 98;
                    } else if (guestAudience || pkGuest) {
                        colorchange = 99;
                    }
                }
            }

            MessageBean messageBean;
            if (!account.equals(selfName)) {
                if (msg.contains("Broadcaster has accepted your broadcast request")) {
                    /*if (!isBroadcaster) {
                        String id = msg.substring(0, 6);
                        if (id.equalsIgnoreCase(SessionUser.getUser().getUser_id())) {
                            doSwitchToBroadcaster(true);
                            sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + "gUeSt i$ aDdEd");
                            ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.videomute));
                            bottomBroadcaster.setVisibility(View.VISIBLE);
                            bottomAudience.setVisibility(View.GONE);
                            isGuest = true;
                            rlGuestClose.setVisibility(View.VISIBLE);
                            ivFrame.setVisibility(View.GONE);
                            guestOne.setVisibility(View.VISIBLE);
                            if (mUidsList.size() > 2)
                                guestTwo.setVisibility(View.VISIBLE);
                            else
                                guestOne.setVisibility(View.VISIBLE);
                        }
                    }*/
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
                        guestonemute = false;
                        guesttwomute = false;
                        isGuest1 = false;
                        doSwitchToBroadcaster(false);
                    } else if (isGuest2) {
                        isGuest2 = false;
                        isGuest1 = true;
                        if (isGuestVideoMuted) {
                            rlVideoMuteGuestTwo.setVisibility(View.GONE);
                            rlVideoMuteGuestOne.setVisibility(View.VISIBLE);

                        } else {
                            rlVideoMuteGuestOne.setVisibility(View.GONE);
                        }
                        callGetGuestsAPI();
                        rl_videoMuteGuestOne1.setVisibility(View.GONE);
                        rlVideoMuteGuestTwo.setVisibility(View.GONE);

                        if (guesttwomute) {
                            rl_videoMuteGuestOne1.setVisibility(View.VISIBLE);
                            rlVideoMuteGuestOne.setVisibility(View.GONE);
                            rlVideoMuteGuestTwo.setVisibility(View.GONE);
                        } else {
                            rl_videoMuteGuestOne1.setVisibility(View.GONE);
                            rlVideoMuteGuestOne.setVisibility(View.GONE);
                            rlVideoMuteGuestTwo.setVisibility(View.GONE);
                        }

                    }
                } else if (msg.contains("Guest Two Removed")) {
                    String id = msg.substring(0, 6);
                    isGuest2 = false;
                    guesttwomute = false;
                    if (SessionUser.getUser().getUser_id().equals(id)) {
                        isGuest = false;
                        doSwitchToBroadcaster(false);
                    }
                    rlVideoMuteGuestTwo.setVisibility(View.GONE);
                    rlVideoMuteGuestOne.setVisibility(View.GONE);
                    rlGuestClose.setVisibility(View.GONE);

                    if (guestonemute) {
                        rl_videoMuteGuestOne1.setVisibility(View.VISIBLE);
                        rlVideoMuteGuestOne.setVisibility(View.GONE);
                    } else {
                        rl_videoMuteGuestOne1.setVisibility(View.GONE);
                        rlVideoMuteGuestOne.setVisibility(View.GONE);
                    }
                } else if (msg.contains("Guest One Left the Broadcast")) {
                    String id = msg.substring(0, 6);
                    if (SessionUser.getUser().getUser_id().equals(id)) {
                        isGuest = false;
                        isGuest1 = false;
                        guestonemute = false;
                        guesttwomute = false;
                        doSwitchToBroadcaster(false);
                    } else if (isGuest2) {
                        isGuest2 = false;
                        isGuest1 = true;
                        if (isGuestVideoMuted) {
                            rlVideoMuteGuestTwo.setVisibility(View.GONE);
                            rlVideoMuteGuestOne.setVisibility(View.VISIBLE);
                        } else {
                            rlVideoMuteGuestOne.setVisibility(View.GONE);
                        }
                        callGetGuestsAPI();
                        rl_videoMuteGuestOne1.setVisibility(View.GONE);
                        rlVideoMuteGuestTwo.setVisibility(View.GONE);

                        if (guesttwomute) {
                            rl_videoMuteGuestOne1.setVisibility(View.VISIBLE);
                            rlVideoMuteGuestOne.setVisibility(View.GONE);
                            rlVideoMuteGuestTwo.setVisibility(View.GONE);
                        } else {
                            rl_videoMuteGuestOne1.setVisibility(View.GONE);
                            rlVideoMuteGuestOne.setVisibility(View.GONE);
                            rlVideoMuteGuestTwo.setVisibility(View.GONE);
                        }
                    }
                } else if (msg.contains("Guest Two Left the Broadcast")) {
                    String id = msg.substring(0, 6);
                    isGuest2 = false;
                    guesttwomute = false;
                    if (SessionUser.getUser().getUser_id().equals(id)) {
                        isGuest = false;
                        doSwitchToBroadcaster(false);
                    }
                    rlVideoMuteGuestTwo.setVisibility(View.GONE);
                    rlVideoMuteGuestOne.setVisibility(View.GONE);
                    if (guestonemute) {
                        rl_videoMuteGuestOne1.setVisibility(View.VISIBLE);
                        rlVideoMuteGuestOne.setVisibility(View.GONE);

                    } else {
                        rl_videoMuteGuestOne1.setVisibility(View.GONE);
                        rlVideoMuteGuestOne.setVisibility(View.GONE);

                    }
                } else if (msg.contains("Broadcast has been Paused!")) {
                    if (!isBroadcaster) {
                        if (ispknow) {
                            if (broadcasterAudience) {
                                firsstlay.setBackground(null);
                            } else {
                                secondtlay.setBackground(null);
                            }
                        } else {
                            tvPause.setVisibility(View.VISIBLE);
                            rlPause.setVisibility(View.VISIBLE);
                        }
                    }
                } else if (msg.contains("Broadcast has been Resumed!")) {
                    if (!isBroadcaster) {
                        if (ispknow) {
                            if (broadcasterAudience) {
                                firsstlay.setBackground(null);
                            } else {
                                secondtlay.setBackground(null);
                            }
                        } else {
                            tvPause.setVisibility(View.GONE);
                            rlPause.setVisibility(View.GONE);
                        }
                    }
                } else if (msg.contains("Broadcast has ended")) {
                    if (!isBroadcaster) {
                        isBroadcastEnded = true;
                        callRemoveImageAPI();
                    }
                } else if (msg.contains("Broadcastpk has been Paused!")) {
                    if (isBroadcaster) {

                        if (ispknow) {
                            if (broadcasterAudience || isBroadcaster) {
                                secondtlay.setBackground(null);
                            } else {
                                firsstlay.setBackground(null);
                            }
                        } else {
                            tvPause.setVisibility(View.VISIBLE);
                            rlPause.setVisibility(View.VISIBLE);
                        }

                    }
                } else if (msg.contains("Broadcastpk has been Resumed!")) {
                    if (isBroadcaster) {
                        if (ispknow) {
                            if (broadcasterAudience || isBroadcaster) {
                                secondtlay.setBackground(null);
                            } else {
                                firsstlay.setBackground(null);
                            }
                        } else {
                            tvPause.setVisibility(View.GONE);
                            rlPause.setVisibility(View.GONE);
                        }
                    }
                } else if (msg.contains("Broadcastpk has ended")) {
                    if (!isBroadcaster) {
                        if (pkGuest) {
                            isBroadcastEnded = true;
                            ispknow = false;
                            pkGuest = false;
                            isBroadcaster = true;
                            if (rtcEngine() != null) {
                                rtcEngine().leaveChannel();
                            }
                            mUidsList.clear();
                            worker().joinChannel(SessionUser.getUser().getUsername(), SessionUser.getUser().getId());
                            if (mRtmChannel != null) {
                                mRtmChannel.release();
                            }

                            joinrtmchannel(SessionUser.getUser().getUsername(), "0");
                            /*soloActiveApi();*/
                            pkgone();
                            isBroadcaster = true;
                            pkGuest = false;
                            ispknow = false;
                            doRenderRemoteUi(SessionUser.getUser().getId());
                        } else if (guestAudience) {
                            new Handler().postDelayed(() -> {

                                Log.i("autolog", "guestAudience: " + guestAudience);
                                showToast("this is a  guestAudience");

                                if (rtcEngine() != null) {
                                    rtcEngine().leaveChannel();
                                }
//                                rtcEngine().joinChannel(null,SessionUser.getUser().getUsername(),"Blive",SessionUser.getUser().getId());

                                mUidsList.clear();
                                worker().joinChannel(broadcaster.getUsername(), SessionUser.getUser().getId());
                                if (mRtmChannel != null) {
                                    mRtmChannel.release();
                                }
                                joinrtmchannel(broadcaster.getUsername(), "0");
                                pkgone();
//                                doRenderRemoteUi(broadcaster.getId());
                                guestAudience = false;
                                isAudience = true;
                                ispknow = false;
                                isGuest = false;

                            }, 3000);

                                /*isBroadcastEnded = true;
                                ispknow = false;
                                Intent intent = new Intent(ActivityLiveRoom.this, ActivityBroadcastStop.class);
                                intent.putExtra("name", broadcaster.getName());
                                intent.putExtra("image", broadcaster.getProfile_pic());
                                intent.putExtra("user_id", broadcaster.getUser_id());
                                startActivity(intent);*/

                        } else if (broadcasterAudience) {
                            showToast("this is a broad audience");
                            isBroadcastEnded = true;
                            broadcasterAudience = false;
                            callRemoveImageAPI();
                        } else {
                            showToast("something goes wrong");
                        }
                    }
                    if (!isBroadcaster) {
                        Log.i("autolog", "pkbrpoadloop end: " + pkGuest + isBroadcaster);
                        if (broadcasterAudience) {
                            isBroadcastEnded = true;
                            new Handler().postDelayed(() -> {
                                showToast("this is a broad audience");
                                if (rtcEngine() != null) {
                                    rtcEngine().leaveChannel();
                                }
                                mUidsList.clear();
                                worker().joinChannel(broadcaster.getUsername(), SessionUser.getUser().getId());
                                if (mRtmChannel != null) {
                                    mRtmChannel.release();
                                }

                                joinrtmchannel(broadcaster.getUsername(), "0");
                                pkgone();
                                broadcasterAudience = false;
                                isAudience = true;
                                ispknow = false;
                                isGuest = false;
                            }, 3000);
                        } else if (guestAudience) {
                            ispknow = false;
                            /* isBroadcastEnded = true;*/
                                /*Intent intent = new Intent(ActivityLiveRoom.this, ActivityBroadcastStop.class);
                                intent.putExtra("name", broadcaster.getName());
                                intent.putExtra("image", broadcaster.getProfile_pic());
                                intent.putExtra("user_id", broadcaster.getUser_id());
                                startActivity(intent);*/

                            Log.i("autolog", "Sangeeth: " + guestAudience);

                            showToast("this is a  guestAudience");

                            isBroadcastEnded = true;
                            guestAudience = false;
                            callRemoveImageAPI();
                        } else {

                        }
                    }
                } else if (msg.contains("Broadcastpkguest has ended")) {
                    if (!isBroadcaster) {
                        Log.i("autolog", "pkbrpoadloop end: " + pkGuest + isBroadcaster);
                        if (broadcasterAudience) {
                            isBroadcastEnded = true;
                            new Handler().postDelayed(() -> {
                                showToast("this is a broad audience");
                                if (rtcEngine() != null) {
                                    rtcEngine().leaveChannel();
                                }
                                mUidsList.clear();
                                worker().joinChannel(broadcaster.getUsername(), SessionUser.getUser().getId());
                                if (mRtmChannel != null) {
                                    mRtmChannel.release();
                                }

                                joinrtmchannel(broadcaster.getUsername(), "0");
                                pkgone();
                                broadcasterAudience = false;
                                isAudience = true;
                                ispknow = false;
                                isGuest = false;
                            }, 3000);
                        } else if (guestAudience) {
                            ispknow = false;
                            isBroadcastEnded = true;
                            Log.i("autolog", "Sangeeth: " + guestAudience);

                            showToast("this is a  guestAudience");

                            isBroadcastEnded = true;
                            guestAudience = false;
                            callRemoveImageAPI();
                        } else {

                        }
                    } else {
                        showToast("this is a broad guest");
                    }
                } else if (msg.contains("Broadcast Pk Guest Has Quit")) {
                    if (!isBroadcaster) {
                        if (pkGuest) {
                            joinrtmchannel(SessionUser.getUser().getUsername(), "0");
                            Log.i("autolog", "pkGuest: " + pkGuest);
                            isBroadcastEnded = true;
                            ispknow = false;
                            pkGuest = false;
                            isBroadcaster = true;
                            ivpk.setVisibility(View.VISIBLE);
                            pkgone();
                            worker().getRtcEngine().stopChannelMediaRelay();
                        } else if (guestAudience) {
                            joinrtmchannel(broadcaster.getUsername(), "0");
                            Log.e(TAG, "guestAudience" + "guestAudeience");
                            isBroadcastEnded = true;
                            guestAudience = false;
                            broadcasterAudience = false;
                            pkgone();
                            isAudience = true;
                            ispknow = false;
                            isGuest = false;

                        } else if (broadcasterAudience) {
                            isBroadcastEnded = true;
                            Log.e(TAG, "onMessageReceive: " + "broadcasterAudience");
                            ispknow = false;
                            pkgone();
                            guestAudience = false;
                            broadcasterAudience = false;
                            isAudience = true;
                            isGuest = false;
                        }
                    } else {
                        Log.i("autolog", "broad: " + isBroadcaster);
                        ispknow = false;
                        pkGuest = false;
                        isBroadcaster = true;
                        ispknow = false;
                        ivpk.setVisibility(View.VISIBLE);
                        pkgone();
                        joinrtmchannel(SessionUser.getUser().getUsername(), "0");
                        worker().getRtcEngine().stopChannelMediaRelay();
                    }
                } else if (msg.contains("Broadcaster Pk Has end")) {
                    Log.i("autolog", "Broadcaster: " + broadcasterAudience + guestAudience + pkGuest);

                    if (!isBroadcaster) {
                        if (pkGuest) {
                            joinrtmchannel(SessionUser.getUser().getUsername(), "0");
                            Log.i("autolog", "pkGuest: " + pkGuest);
                            isBroadcastEnded = true;
                            ispknow = false;
                            pkGuest = false;
                            isBroadcaster = true;
                            ivpk.setVisibility(View.VISIBLE);
                            pkgone();
                            worker().getRtcEngine().stopChannelMediaRelay();
                        } else if (guestAudience) {
                            joinrtmchannel(broadcaster.getUsername(), "0");
                            Log.e(TAG, "guestAudience" + "guestAudeience");
                            isBroadcastEnded = true;
                            guestAudience = false;
                            broadcasterAudience = false;
                            pkgone();
                            isAudience = true;
                            ispknow = false;
                            isGuest = false;

                        } else if (broadcasterAudience) {
                            isBroadcastEnded = true;
                            Log.e(TAG, "onMessageReceive: " + "broadcasterAudience");
                            ispknow = false;
                            pkgone();
                            guestAudience = false;
                            broadcasterAudience = false;
                            isAudience = true;
                            isGuest = false;
                            callRemoveImageAPI();
                        }
                    } else {
                        Log.i("autolog", "broad: " + isBroadcaster);
                        ispknow = false;
                        pkGuest = false;
                        isBroadcaster = true;
                        ispknow = false;
                        ivpk.setVisibility(View.VISIBLE);
                        pkgone();
                        joinrtmchannel(SessionUser.getUser().getUsername(), "0");
                        worker().getRtcEngine().stopChannelMediaRelay();
                    }
                } else if (msg.contains("guest Pk Guest Has end")) {
                    Log.i("autolog", "Broadcaster: " + broadcasterAudience + guestAudience + pkGuest);
                    if (!isBroadcaster) {
                        if (pkGuest) {
                            joinrtmchannel(SessionUser.getUser().getUsername(), "0");
                            Log.i("autolog", "pkGuest: " + pkGuest);
                            isBroadcastEnded = true;
                            ispknow = false;
                            pkGuest = false;
                            isBroadcaster = true;
                            ivpk.setVisibility(View.VISIBLE);
                            pkgone();
                            worker().getRtcEngine().stopChannelMediaRelay();
                        } else if (guestAudience) {
                            joinrtmchannel(broadcaster.getUsername(), "0");
                            Log.e(TAG, "guestAudience" + "guestAudeience");
                            isBroadcastEnded = true;
                            guestAudience = false;
                            broadcasterAudience = false;
                            pkgone();
                            isAudience = true;
                            ispknow = false;
                            isGuest = false;
                            callRemoveImageAPI();

                        } else if (broadcasterAudience) {
                            isBroadcastEnded = true;
                            Log.e(TAG, "onMessageReceive: " + "broadcasterAudience");
                            ispknow = false;
                            pkgone();
                            guestAudience = false;
                            broadcasterAudience = false;
                            isAudience = true;
                            isGuest = false;

                        }
                    } else {
                        Log.i("autolog", "broad: " + isBroadcaster);
                        ispknow = false;
                        pkGuest = false;
                        isBroadcaster = true;
                        ispknow = false;
                        ivpk.setVisibility(View.VISIBLE);
                        pkgone();
                        joinrtmchannel(SessionUser.getUser().getUsername(), "0");
                        worker().getRtcEngine().stopChannelMediaRelay();
                    }
                } else if (msg.contains("has requested you to join the Video Call")) {

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
                } else if (msg.contains("has accepted your video call request")) {
                    isCallEnd = false;
                    String id = msg.substring(0, 6);
                    removeUserFromList(id);
                    callGetGuestsAPI();
                } else if (msg.contains("has rejected your video call request")) {
                    String id = msg.substring(0, 6);
                    Log.i("autolog", "id: " + id);
                    if (isBroadcaster) {
                        isCallEnd = false;
                        messageBean = new MessageBean(account, msg, false, false, false);
                        messageBeanList.add(messageBean);
                        adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                        rvMessages.scrollToPosition(messageBeanList.size() - 1);
                        tvConnecting.setVisibility(View.GONE);
                        rlGuestClose.setVisibility(View.GONE);
                        rlGuestTwoClose.setVisibility(View.GONE);
                        guestOne.setVisibility(View.GONE);
                        guestTwo.setVisibility(View.GONE);
                        Log.i("autolog", "id: " + SessionUser.getUser().getUser_id());
                    } else if (SessionUser.getUser().getUser_id().equalsIgnoreCase(id)) {
                        Log.i("autolog", "id: " + SessionUser.getUser().getUser_id());
                        isRequested = false;
                    }
                } else if (msg.contains("Guest One Muted")) {
                    if (!ispknow) {
                        guestonemute = true;
                        rlVideoMuteGuestTwo.setVisibility(View.VISIBLE);
                        rl_videoMuteGuestOne1.setVisibility(View.VISIBLE);
                    } else {
                        //                        showToast("this is");
                    }
                } else if (msg.contains("Guest One UnMuted")) {
                    if (!ispknow) {
                        guestonemute = false;
                        rlVideoMuteGuestTwo.setVisibility(View.GONE);
                        rl_videoMuteGuestOne1.setVisibility(View.GONE);
                    }
                } else if (msg.contains("Guest Two MuTed")) {
                    if (!ispknow) {
                        guesttwomute = true;
                        rlGuestClose.setVisibility(View.VISIBLE);
                        rlVideoMuteGuestOne.setVisibility(View.VISIBLE);
                    }
                } else if (msg.contains("Guest Two UnMuTed")) {
                    if (!ispknow) {
                        guesttwomute = false;
                        rlGuestClose.setVisibility(View.GONE);
                        rlVideoMuteGuestOne.setVisibility(View.GONE);
                    }
                } else if (msg.contains("has bEEn kicKed OuT")) {
                    if (!isBroadcaster) {
                        String id = msg.substring(0, 6);
                        if (id.equalsIgnoreCase(SessionUser.getUser().getUser_id())) {
                            guestonemute = false;
                            guesttwomute = false;
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

                    messageBean = new MessageBean(account, message, false, false, false);
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

                    messageBean = new MessageBean(account, message, false, false, false);
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

                    String userId = msg.substring(0, 6);
                   /* if(isGiftclick || mAudiences.get(i).getUser_id().equalsIgnoreCase(userId)){
                        isGiftclick=false;*/
                    String message = msg;
                    if (msg.contains(": Has sent gIfTsEnTtOyOU0")) {
                        message = message.replace(" gIfTsEnTtOyOU0", "");
                    } else if (msg.contains(": Has sent gIfTsEnTtOyOU1")) {
                        message = message.replace(" gIfTsEnTtOyOU1", "");
                    } else if (msg.contains(": Has sent gIfTsEnTtOyOU")) {
                        message = message.replace(" gIfTsEnTtOyOU", "");
                    }
                    /*message = message.replace(" gIfTsEnTtOyOU", "");*/
                    messageBean = new MessageBean(account, message, false, false, true);
                    messageBeanList.add(messageBean);
                    adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                    rvMessages.scrollToPosition(messageBeanList.size() - 1);
                    if (ispknow) {
                        Log.d("onMessageReceive", "onMessageReceive: ");
                        GiftUpdateAPI();
                    }
                    if (ispknow) {
                        if (msg.contains(": Has sent gIfTsEnTtOyOU0")) {
                            if (isBroadcaster || broadcasterAudience) {
                                Log.i("isBroadcaster", "msg: " + msg);
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
                            }
                        } else if (msg.contains(": Has sent gIfTsEnTtOyOU1")) {
                            if (pkGuest || guestAudience) {
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
                            }
                        }
                    } else {
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
                    }
                    /* }*/
                } else if (msg.contains("FreeGiftCount")) {
                    String message = msg;
                    Log.e(TAG, "onMessageReceive: FreeGiftCount  " + message);
                    message = message.replace("FreeGiftCount", "");
                    tvFreeGiftCount.setText(message);
                } else if (msg.contains("OverAllGOldValue")) {
                    String message = msg;
                    Log.e(TAG, "onMessageReceive: OverAllGOldValue  " + message);

                    if (!ispknow) {
                        try {
                            message = message.replace("OverAllGOldValue", "");
                            Log.i("autolog", "message: " + message);
                            String[] values = message.split("&&");
                            String moonvalue = values[1];
                            loadStarImage(moonvalue);
                            Log.i("autolog", "moonvalue: " + moonvalue);
                            String moonlevelfrom = values[2];
                            tvMoonLevelCount.setText("5x" + moonlevelfrom);
                            String overallgold = values[0];
                            Log.i("autolog", "moonlevel: " + moonlevelfrom);
                            message = message.replace("&&", "");
                            overallgold = overallgold.replace("&&", "");

                            tvReceived.setText(overallgold);
                        } catch (Exception e) {
                            Log.i("autolog", "e: " + e);
                            message = message.replace("OverAllGOldValue", "");
                            tvReceived.setText(message);
                        }
                    }
                } else if (msg.contains("currentDiamondValue")) {
                    String message = msg;
                    message = message.replace("currentDiamondValue", "");
                    tvCurrentDiamondValue.setText(message);
                } else if (msg.contains("freeGiftAchieved")) {
                    Log.e(TAG, "freeGiftAchieved: freeGiftReceived" + "freeGiftAchieved");
                    showFreeGiftAchieved();
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
                    } else if (msg.contains("Broadcast has been Paused!")) {
                        if (!isBroadcaster) {
                            tvPause.setVisibility(View.VISIBLE);
                            rlPause.setVisibility(View.VISIBLE);
                        }
                    } else if (msg.contains("Broadcast has been Resumed!")) {
                        if (!isBroadcaster) {
                            tvPause.setVisibility(View.GONE);
                            rlPause.setVisibility(View.GONE);
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
                    } else if (msg.contains("minutes Pk requested")) {
                        String pktime = "";
                        try {
                            if (pkGuest || (!timerReqSent && isBroadcaster)) {
                                if (pkTimeOptionsRl.getVisibility() == View.VISIBLE) {
                                    pkTimeOptionsRl.setVisibility(View.GONE);
                                    fabMenu.setZ(40.0f);
                                    rvMessages.setZ(-2.0f);
                                    sendPkMessage(selectedPKTime + " minutes Pk requested");
                                    pkStartBtn.setVisibility(View.GONE);
                                }
                                String pk_time_str = msg.substring(msg.indexOf(" ") + 1, msg.indexOf(" minutes"));
                                pktime = pk_time_str;
                                Integer pk_time = Integer.parseInt(pk_time_str);
                                pkStartBtn.setVisibility(View.GONE);
                                showPkTimeMessagePopup(msg.substring(msg.indexOf(" ") + 1));
                                pkTimeAccept.setOnClickListener(view -> {
                                    pkTimeRequestRl.setVisibility(View.GONE);
                                    fabMenu.setZ(40.0f);
                                    sendPkMessage(pk_time_str + " Minutes Pk Challenge Has Been Accepted");
                                    if (pkGuest) {
                                        startTimer(pk_time * 60);
                                    } else if (isBroadcaster) {
                                        startBroadcasterTimer(pk_time * 60);
                                    }
                                });
                                pkTimeReject.setOnClickListener(view -> {
                                    pkTimeRequestRl.setVisibility(View.GONE);
                                    fabMenu.setZ(40.0f);

                                    sendPkMessage(pk_time_str + " minutes Pk request rejected");
                                    pkStartBtn.setVisibility(View.VISIBLE);
                                });
                            }
                        } catch (Exception e) {
                            Log.i("autolog", "e: " + e.toString());
                            Crashlytics.log(e.toString());
                            e.printStackTrace();
                            sendPkMessage(pktime + " minutes Pk request rejected");
                        }
                    } else if (msg.contains(" Minutes Pk Challenge Has Been Accepted")) {
                        try {
                            String pk_time_str = msg.substring(msg.indexOf(" ") + 1, msg.indexOf(" Minutes"));
                            Integer pk_time = Integer.parseInt(pk_time_str);
                            timerReqSent = false;
                            if (isBroadcaster) {
                                startBroadcasterTimer(pk_time * 60);
                            } else if (!isGuest || pkGuest) {
                                startTimer(pk_time * 60);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            sendPkMessage("5" + " minutes Pk request rejected");
                            pkStartBtn.setVisibility(View.VISIBLE);
                        }
                    } else if (msg.contains("minutes Pk request rejected")) {
                        try {
                            timerReqSent = false;
                            if (isBroadcaster || pkGuest) {
                                pkStartBtn.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            Log.i("autolog", "e: " + e);
                            e.printStackTrace();
                            if (isBroadcaster || pkGuest) {
                                pkStartBtn.setVisibility(View.VISIBLE);
                            }
                        }
                    } else if (msg.contains("has arrived")) {
                        callAudiencesAPI(broadcaster.getUser_id());
                    } else if (msg.contains("Pk challenge result is draw")) {
                        pkDrawAction();
                    } else if (msg.contains("Pk challenge won by guest ")) {
                        pkGuestWon();
                    } else if (msg.contains("Pk challenge won by broadcaster ")) {
                        pkBroadcasterWon();
                    } else if (msg.contains("Moved to pk challenge")) {
                        ispknow = true;
                        isGuest = false;
                        isGuest1 = false;
                        isGuest2 = false;
                        if (broadcasterId.equalsIgnoreCase(broadcaster.getUser_id())) {
                            Log.i("autolog", "broadcaster.getUser_id()1: " + broadcaster.getUser_id());
                            Log.i("autolog", "broadcasterId1: " + broadcasterId);
                            String currentString = msg;
                            String[] separated = currentString.split("Moved to pk challenge");
                            pkuserid = separated[1];
                            if (!pkGuest) {
                                broadcasterAudience = true;
                            }
                        } else {
                            Log.i("autolog", "broadcaster.getUser_id()2: " + broadcaster.getUser_id());
                            Log.i("autolog", "broadcasterId2: " + broadcasterId);
                        }
                    } else if (msg.contains("broadcaster joined pk request")) {
                        isGuest = false;
                        isGuest1 = false;
                        isGuest2 = false;
                        ispknow = true;
                        guestAudience = true;
                        timerStarted = false;
                        pkvisible();
                        String id = msg.substring(0, 6);
                        String userId = msg.substring(0, 6);
                        pkuserid = userId;
                        String message = msg;
                        message = message.replace(id, "");
                        message = message.replace("broadcaster joined pk request", "");
                        message = message.trim();
                        try {
                            JSONObject msgJson = new JSONObject(message);
                            try {
                                showToast("Moving to Pk ");
                                broadcasterId = msgJson.getString("user_id");
//                                worker().leaveChannel(config().mChannel);
//                                worker().joinChannel(msgJson.getString("user_name"), SessionUser.getUser().getId());
                                joinrtmchannel(msgJson.getString("user_name"), "0");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (msg.contains(" splitto")) {
                        String currentString = msg;
                        String[] separated = currentString.split("splitto");
                        pkuserid = separated[1];
                    } else if (msg.contains("Won Pk Challenge and he got Reward")) {
                        try {
                            String finalMsg = msg.replace("Won Pk Challenge and he got Reward", " ");
                            MessageBean message1 = new MessageBean("", SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + finalMsg, true, false, false);
                            messageBeanList.add(message1);
                            adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                            rvMessages.scrollToPosition(messageBeanList.size() - 1);
                            /*showwinneralert();*/
                            new Handler().postDelayed(() -> {
                                if (dialog_winner.isShowing()) {
                                    dialog_winner.dismiss();
                                }
                                rewarddialog.setTitle("Pk");
                                rewarddialog.setCancelable(true);
//                            rewarddialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationexit;
                                rewarddialog.setContentView(R.layout.rewardlayout);
                                Window window = rewarddialog.getWindow();
                                WindowManager.LayoutParams wlp = window.getAttributes();
                                wlp.gravity = Gravity.CENTER;
                                wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                                window.setAttributes(wlp);
                                /*rewarddialog.show();*/
                                TextView text_rewardpoint = rewarddialog.findViewById(R.id.text_rewardpoint);
                                Button ok = rewarddialog.findViewById(R.id.ok_button);
                                text_rewardpoint.setText(finalMsg.toString().trim());
                                scroll.setText(finalMsg.toString().trim());
                                ok.setOnClickListener(v -> {
                                    if (rewarddialog.isShowing()) {
                                        rewarddialog.dismiss();
                                    }
                                });
                            }, 1000);
                        } catch (Exception e) {
                            Log.i("autolog", "e: " + e.toString());
                            Crashlytics.log(e.getMessage());

                        }
                    } else if (msg.contains("sessionid=")) {
                        String currentString = msg;
                        String[] separated = currentString.split("=");
                        SessionId = separated[1];
                        Log.d("SessionId", "onMessageReceive: " + SessionId);
                    } else if (msg.contains("PKGIFTFROM2020&&")) {
                        String currentString = msg.replace("PKGIFTFROM2020&&", " ");

                        MessageBean message1 = new MessageBean("", SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + currentString, true, false, false);
                        messageBeanList.add(message1);
                        adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                        rvMessages.scrollToPosition(messageBeanList.size() - 1);
                        try {
                            BLiveApplication.getCurrentActivity().runOnUiThread(() -> {

                                scroll.setText(currentString);
                                scroll.setVisibility(View.VISIBLE);
                                Dialog alertDialog = new Dialog(BLiveApplication.getCurrentActivity());
                                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                Window window = alertDialog.getWindow();
                                WindowManager.LayoutParams wlp = window.getAttributes();
                                wlp.gravity = Gravity.CENTER;
                                wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                                window.setAttributes(wlp);
                                Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable
                                        (Color.TRANSPARENT));
                                alertDialog.setContentView(R.layout.alert_pkspecialgift);
                                TextView tvWinner = alertDialog.findViewById(R.id.tv_winnerName);
                                TextView tvDiamondBack = alertDialog.findViewById(R.id.tv_diamondBack);
                                Button okBtn = alertDialog.findViewById(R.id.btn_ok);
                                ImageView ivPkImg = alertDialog.findViewById(R.id.iv_diamond);

                                try {
                                    Glide.with(ActivityLiveRoom.this).load(R.drawable.diamond_blast).apply(new RequestOptions()
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .skipMemoryCache(true))
                                            .into(ivPkImg);
                                } catch (Exception e) {
                                    Log.i("autolog", "e: " + e.toString());

                                }
                                String[] separated = currentString.split(" -- ");
                                tvWinner.setText(separated[0]);
                                tvDiamondBack.setText(separated[1]);
                                okBtn.setOnClickListener(v -> alertDialog.dismiss());
                                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                /*alertDialog.show();*/
                                new Handler().postDelayed(alertDialog::dismiss, 5000);

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        /*scroll.setVisibility(View.GONE);*/
                                    }
                                }, 5000);
                            });
                        } catch (Exception e) {
                            Log.d(TAG, "onMessageReceived: " + e.getMessage());

                        }

                    } else if (msg.contains("PK Challenge Has Started")) {
                        GiftUpdateAPI();
                        responsegift(0, 0);
                    }
                }
            } else {
                if (msg.contains("Broadcast Pk Guest Has Quit")) {
                    if (isBroadcaster) {
                        isBroadcastEnded = true;
                        ispknow = false;
                        pkGuest = false;
                        isBroadcaster = true;
                        ivpk.setVisibility(View.VISIBLE);
                        pkgone();
                        worker().getRtcEngine().stopChannelMediaRelay();
                    }
                }
            }
        });
    }

    @Override
    public void onMessageClicked(String name, String id) {
        if (!id.equals(SessionUser.getUser().getUser_id())) {
            if (!isClickedProfile) {
                isClickedProfile = true;
                getClickedProfileData(id);
            }
        } else if (!id.equals(SessionUser.getUser().getUser_id())) {
            getClickedProfileData(id);
        } else {

        }
    }

    @Override
    public void onRequestSent(String userName, String jsonObject) {
        Log.i("autolog", "userName: " + userName);

        if (!pkRequestSent) {
            pkRequestSent = true;
            sendPeerMessage(userName, jsonObject);
            Log.e(TAG, "onRequestSent: " + userName);
            findViewById(R.id.menu).setZ(2.0f);
            disconnect_progress.setVisibility(View.VISIBLE);
    /*disconnect.setVisibility(View.VISIBLE);
    disconnect_progress.setVisibility(View.VISIBLE);*/
            runOnUiThread(() -> {
                sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " sent Pk Challenge request to " + userName);
                ((ActivityLiveRoom) this).findViewById(R.id.iv_pk).setVisibility(View.VISIBLE);
            });
        } else {
            showColorToast("Please Wait 10 Sec To Recreate Pk Session");
            ((ActivityLiveRoom) this).findViewById(R.id.iv_pk).setVisibility(View.VISIBLE);

            new Handler().postDelayed(() -> {
                if (!ispknow) {
                    pkRequestSent = false;
                }
            }, 8000);


        }
    /*handler.postDelayed(() -> {
        if (!ispknow) {
            pkRequestSent = false;
            disconnect.setVisibility(View.GONE);
            disconnect_progress.setVisibility(View.GONE);
        }
    }, 11000);*/
    }

    @Override
    public void onRequestSendFailure(String userName) {
        runOnUiThread(() -> {
            sendMessageToChannel("  Failed to send pk request to" + userName);
            findViewById(R.id.iv_pk).setVisibility(View.VISIBLE);
            findViewById(R.id.menu).setZ(2.0f);
        });
    }

    class LikeAnimationViewProvider implements LikeAnimationView.Provider {
        LikeAnimationViewProvider() {
        }

        public Bitmap getBitmap(Object obj) {
            return ActivityLiveRoom.this.mAnimationItemList == null ? null : ActivityLiveRoom.this.mAnimationItemList.get((Integer) obj);
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
        audience.setIsTheUserFollowing(istheuserFolowing);

       /* if (SessionUser.getUser().getUser_id().equals(guest.getUser_id())) {

        } else if (SessionUser.getUser().getUser_id().equals(guest2.getUser_id())) {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityLiveRoom.this);
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
                Picasso.get().load(Utils.getDecodedImage(user.getProfile_pic())).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivGuestProfile);
            } else
                Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(ivGuestProfile);

        }*/

        /////////////////////////////////////////////////////////

        if (ispknow) {
            try {
                if (SessionUser.getUser().getUser_id().equals(user.getUser_id())) {
//                    showAlertViewProfile(audience, true);
                    showBroadcastProfile(SessionUser.getUser());
                } else if (isBroadcaster) {
                    showAlertViewProfile(audience, true);
                } else if (pkGuest) {
                    showAlertViewProfile(audience, true);
                } else if (broadcasterAudience || guestAudience) {
                    showAlertViewProfile(audience, false);
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.i("autolog", "e: " + e.getMessage());
            }

        } else {
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


    }

    @Override
    public void onBackPressed() {
        try {
            if (llChat.getVisibility() == View.VISIBLE) {
                llChat.setVisibility(View.GONE);
            } else if (fabMenu.isOpened()) {
                fabMenu.close(true);
            } else if (cvWebView.getVisibility() == View.VISIBLE) {
                cvWebView.setVisibility(View.GONE);
            } else if (cvRequests.getVisibility() == View.VISIBLE) {
                cvRequests.startAnimation(slideDown);
                cvRequests.setVisibility(View.GONE);
            } else if (bottomSheetDialog != null) {
                bottomSheetDialog.dismiss();
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
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        if (isInPictureInPictureMode) {
            root.setVisibility(View.GONE);
            rlLive.setVisibility(View.GONE);
            fabMenu.setVisibility(View.GONE);
            ivGift.setVisibility(View.GONE);
            rvMessages.setVisibility(View.GONE);
            guestOne.setVisibility(View.GONE);
            if (isBroadcasterPaused || isGuest) {
                tvPause.setText("Video Paused");
                tvPause.setVisibility(View.VISIBLE);
                rlPause.setVisibility(View.VISIBLE);
            }
        } else {
            root.setVisibility(View.VISIBLE);
            rlLive.setVisibility(View.VISIBLE);
            fabMenu.setVisibility(View.VISIBLE);
            ivGift.setVisibility(View.VISIBLE);
            rvMessages.setVisibility(View.VISIBLE);
            guestOne.setVisibility(View.VISIBLE);

            if (isBroadcasterPaused || isGuest) {
                tvPause.setVisibility(View.GONE);
                rlPause.setVisibility(View.GONE);
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

            Log.e(TAG, "callStatusAPI: INACTIVE " + idelSeconds + totalBroadcastSeconds + broadcastSeconds);

            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<GenericResponse> call = apiClient.goLive(SessionUser.getUser().getUser_id(), "INACTIVE", "solo",
                    String.valueOf(broadcastSeconds), String.valueOf(idelSeconds), String.valueOf(totalBroadcastSeconds), "", "");

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
                                if (ispknow) {
                                    /* ispknow = false;*/
                                    if (pkGuest) {
                                        pkdisconnetbrd();
                                        /*Log.e(TAG, "onResponse: " + pkGuest + " " + " Broadcastpkguest has ended ");
                                        sendChannelMessage(broadcaster.getUser_id() + level + SessionUser.getUser().getName() + " Broadcastpkguest has ended");*/
                                    } else {
                                        pkdisconnetbrd();
                                       /* Log.e(TAG, "onResponse: " + " Broadcastpk has ended ");
                                        sendChannelMessage(broadcaster.getUser_id() + level + SessionUser.getUser().getName() + " Broadcastpk has ended");*/
                                    }
                                } else {
                                    Log.e(TAG, "onResponse: " + "Broadcast has ended");
                                    sendChannelMessage(broadcaster.getUser_id() + level + SessionUser.getUser().getName() + " Broadcast has ended");
                                }
                                Constants_app.cleanMessageListBeanList();
                                onStatusSuccess();
                            } else {
//                                showToast(genericResponse.getMessage());
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

    public void onGiftSuccess(String userId, String overAllGoldValue, String currentGoldValue, String moonLevel, String moonValue, int freeGiftReceived, int diamond,
                              String freeGiftAvailable, String freeGiftAnim, String messagetype, String giftname) {
        try {
            Log.e(TAG, "onGiftSuccess: freeGiftReceived New " + freeGiftReceived);
            Log.i("autolog", "moonLevel: " + moonLevel);

            if (messagetype.contains("bulletMessage")) {
                String message = messagetype;
                message = message.replace("bulletMessage", "");
                sendChannelMessage(SessionUser.getUser().getUser_id() + "buLLetMeSsAGe" + message);

                if (isBroadcaster) {
                    dvalue = dvalue - Integer.valueOf("1");
                    SessionUser sessionUser = new SessionUser();
                    User user = sessionUser.getUserData();
                    user.setDiamond(String.valueOf(diamond));
                    sessionUser.saveUserNew(user);
                    sendChannelMessage("currentDiamondValue" + diamond);
                    tvCurrentDiamondValue.setText(String.valueOf(diamond));
                    tvReceived.setText(overAllGoldValue);
                    tvFreeGiftCount.setText(String.valueOf(freeGiftReceived));
                } else {
                    dvalue = dvalue - Integer.valueOf("1");
                    SessionUser sessionUser = new SessionUser();
                    User user = sessionUser.getUserData();
                    user.setDiamond(String.valueOf(diamond));
                    sessionUser.saveUserNew(user);
                    sendChannelMessage("currentDiamondValue" + diamond);
                    tvCurrentDiamondValue.setText(String.valueOf(diamond));
                    tvReceived.setText(overAllGoldValue);
                    tvFreeGiftCount.setText(String.valueOf(freeGiftReceived));
                    tvCurrentFreeGiftValue.setText(freeGiftAvailable);
                    if (Integer.parseInt(tvCurrentFreeGiftValue.getText().toString()) < 0) {
                        tvCurrentFreeGiftValue.setText("0");
                    }
                }

                ivGift.setEnabled(true);
                llCombo.setVisibility(View.GONE);
                return;
            }
            Log.i("autolog", "multiplier: " + multiplier);
            if (!multiplier.isEmpty()) {
                multiplier = " X" + multiplier;
                if (multiplier.equalsIgnoreCase("0")) {
                    multiplier = "";
                }
            } else {
                multiplier = "";
            }

            Log.i("autolog", "multiplier: " + multiplier);

            try {
                getLevel();
            } catch (Exception e) {
                Crashlytics.logException(e);
            }

            if (cRole != 1) {
                MessageBean messageBean = new MessageBean(selfName, SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " : Has sent " + mGift.getName() + multiplier, true, false, true);
                messageBeanList.add(messageBean);
                adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                rvMessages.scrollToPosition(messageBeanList.size() - 1);

                if (ispknow) {
                    if (guestAudience) {
                        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " : Has sent gIfTsEnTtOyOU1 " + giftname);
                        Log.e(TAG, "onGiftSuccess: " + SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " : Has sent gIfTsEnTtOyOU " + giftname);
                    } else if (broadcasterAudience) {
                        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " : Has sent gIfTsEnTtOyOU0 " + giftname);
                        Log.e(TAG, "onGiftSuccess: " + SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " : Has sent gIfTsEnTtOyOU " + giftname);
                    }
                } else {
                    sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " : Has sent gIfTsEnTtOyOU " + giftname);
                    Log.e(TAG, "onGiftSuccess: " + SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " : Has sent gIfTsEnTtOyOU " + giftname);
                    Log.i("autolog", "multiplier: " + multiplier);
                    Log.i("autolog", "giftname: " + giftname);
                }

            }
            tvFreeGiftCount.setText(String.valueOf(freeGiftReceived));
            sendChannelMessage("FreeGiftCount" + freeGiftReceived);

            if (price.equals("0")) {
                freeGiftAchieved(freeGiftReceived, freeGiftAnim);
            }

            GiftMessage giftMessage = new GiftMessage();
            giftMessage.setAccount(SessionUser.getUser().getUsername());
            giftMessage.setMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " : Has sent " + giftname);
            giftMessages.add(giftMessage);
            if (giftMessages.size() > 0) {
                if (!isGiftShowing) {
                    callSwitchCase(temp);
                }
            }

            messagesList.add(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " : Has sent " + giftname);
            if (messagesList.size() > 0) {
                if (!isGoldAdding)
                    callAddGiftValue(temp2);
            }

            dvalue = dvalue - Integer.valueOf(mGift.getPrice());
            SessionUser sessionUser = new SessionUser();
            User user = sessionUser.getUserData();
            user.setDiamond(String.valueOf(diamond));
            sessionUser.saveUserNew(user);
            tvReceived.setText(overAllGoldValue);
            Log.e(TAG, "onGiftSuccess: overAllGoldValue " + overAllGoldValue);
            tvCurrentDiamondValue.setText(String.valueOf(diamond));
            tvFreeGiftCount.setText(String.valueOf(freeGiftReceived));

            sendChannelMessage("OverAllGOldValue" + overAllGoldValue + "&&" + moonValue + "&&" + moonLevel);
            tvCurrentFreeGiftValue.setText(freeGiftAvailable);
            if (Integer.parseInt(tvCurrentFreeGiftValue.getText().toString()) < 0) {
                tvCurrentFreeGiftValue.setText("0");
            }
//            adapterGifts.onGiftReset();
            mGift = null;
            loadStarImage(moonValue);
            tvMoonLevelCount.setText("5x" + moonLevel);
            ivGift.setEnabled(true);
            llCombo.setVisibility(View.GONE);

        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    public void freeGiftAchieved(int freeGiftReceived, String freeGiftAnim) {
        int divVal = freeGiftReceived % 10;
        if (freeGiftReceived % 10 == 0) {
            sendChannelMessage("freeGiftAchieved");
            showFreeGiftAchieved();
        }
    }

    public void showFreeGiftAchieved() {
        ivFreeGiftAchieve.setVisibility(View.VISIBLE);
        Glide.with(getApplicationContext())
                .load(R.drawable.free_gift_achieve)
                .into(ivFreeGiftAchieve);
        Log.e(TAG, "freeGiftAchieved: freeGiftReceived" + "showFreeGiftAchieved");
        Handler handler = new Handler();

        handler.postDelayed(() -> {
            ivFreeGiftAchieve.setVisibility(View.GONE);
        }, 9000);
    }

    public void showFreeGiftAchieved_100() {
        ivFreeGiftAchieve.setVisibility(View.VISIBLE);
        Glide.with(getApplicationContext())
                .load(Constants_api.free_Gift_Level_100th)
                .into(ivFreeGiftAchieve);
        Handler handler = new Handler();

        handler.postDelayed(() -> {
            ivFreeGiftAchieve.setVisibility(View.GONE);
        }, 9000);
    }

    public void loadStarImage(String moonValue) {

        String level = Constants_app.loadStarLevel(moonValue);
        tvStarLevel.setText(level);

        if (oldMoonImage != 0) {
            Drawable res = getResources().getDrawable(oldMoonImage);
            starRatings1.setImageDrawable(res);
        }

        String uri = Constants_app.loadBroadCasterStar(moonValue);
        int starImage = getResources().getIdentifier(uri, null, getPackageName());
        Drawable res = getResources().getDrawable(starImage);
        starRatings.setImageDrawable(res);

        oldMoonImage = starImage;
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
                                Log.i("autolog", "result: " + result);
                                callShareAPI("facebook");
                                sendMessageToChannel(" has shared with Facebook");
                            }

                            @Override
                            public void onCancel() {
                                Log.i("autolog", "onCancel: ");
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
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityLiveRoom.this);
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

    public void showContributors(String username, String overAllGold) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityLiveRoom.this);
        View parentView = getLayoutInflater().inflate(R.layout.item_gold_top_list, null);
        bottomSheetDialog.setContentView(parentView);
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

        tvNoContributors = parentView.findViewById(R.id.tv_no_contributors);
        rv_guestTopperList = parentView.findViewById(R.id.rv_toppers);
        rv_guestTopperList.setVisibility(View.GONE);
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
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        rv_guestTopperList.setNestedScrollingEnabled(false);
        if (utils.isNetworkAvailable()) {
            progressBar.setVisibility(View.VISIBLE);
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<TopFansResponse> call = apiClient.getTopFans(broadcaster.getUser_id());
            call.enqueue(new retrofit2.Callback<TopFansResponse>() {
                @Override
                public void onResponse(@NonNull Call<TopFansResponse> call, @NonNull Response<TopFansResponse> response) {
                    Log.i("autolog", "response: " + response.raw().request().url());
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
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        isClicked = true;
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

    public void showActiveViewers() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityLiveRoom.this);
        View parentView = getLayoutInflater().inflate(R.layout.active_viewrs_list, null);
        bottomSheetDialog.setContentView(parentView);
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) parentView.getParent());
        //    bottomSheetBehavior.setPeekHeight(800);
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
                //   bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        bottomSheetDialog.show();

        TextView tvNoAudience = parentView.findViewById(R.id.tv_no_audience);
        RecyclerView rvActiveViewers = parentView.findViewById(R.id.rv_active_viewers);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, true);
        rvActiveViewers.setLayoutManager(layoutManager);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
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

    public void showNotification() {
        notificationManager = NotificationManagerCompat.from(ActivityLiveRoom.this);
        createNotificationChannel();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            mBuilder = new NotificationCompat.Builder(ActivityLiveRoom.this, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Broadcast Still Streaming ")
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("Your broadcast has paused!"))
                    .setChannelId(CHANNEL_ID)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            notificationManager.notify(notificationId, mBuilder.build());
        } else {
            mBuilder = new NotificationCompat.Builder(ActivityLiveRoom.this)
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
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);

            Log.e("Showing Notification", "channel");
        }
    }

    public void clearNotification() {
        notificationManager = NotificationManagerCompat.from(ActivityLiveRoom.this);
        notificationManager.cancelAll();
    }

    /**
     * API CALL: send message to a channel
     */
    private void sendChannelMessage(String msg) {
        Log.i("autolog", "msg: " + msg);
        try {
            // step 1: create a message

            RtmMessage message = mRtmClient.createMessage();
            message.setText(msg);
            Log.i("autolog", "mRtmClient: " + mRtmChannel.getId());

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
                    Log.i("autolog", "errorCode: " + errorCode);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (errorCode) {
                                case RtmStatusCode.ChannelMessageError.CHANNEL_MESSAGE_ERR_TIMEOUT:
                                case RtmStatusCode.ChannelMessageError.CHANNEL_MESSAGE_ERR_FAILURE:
                                    /*showToast(getString(R.string.send_msg_failed));*/
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
        super.onNewIntent(i);
        startActivity(i);
        finish();
    }

    /*public void onVideoEndClicked(View view) {
        isRequested = false;
        isGuest = false;
        fabMic.setBackground(getResources().getDrawable(R.mipmap.mic));
        ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.videomute));
        fabMic.setTag(false);
        doSwitchToBroadcaster(false);
        removeGuest();
    }*/

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
                        rlVideoMuteGuestOne.setVisibility(View.GONE);
                        rlVideoMuteGuestTwo.setVisibility(View.GONE);
                        break;
                    case 2:
                        sendChannelMessage(removeGuestId + level + SessionUser.getUser().getName() + " Guest Two Removed");
                        rlVideoMuteGuestTwo.setVisibility(View.GONE);
                        break;
                }
            } else if (isRemoveSelf) {
                switch (i) {
                    case 1:
                        sendChannelMessage(removeGuestId + level + SessionUser.getUser().getName() + " Guest One Left the Broadcast");
                        rlVideoMuteGuestOne.setVisibility(View.GONE);
                        rl_videoMuteGuestOne1.setVisibility(View.GONE);
                        rlVideoMuteGuestTwo.setVisibility(View.GONE);
                        break;
                    case 2:
                        sendChannelMessage(removeGuestId + level + SessionUser.getUser().getName() + " Guest Two Left the Broadcast");
                        rlVideoMuteGuestTwo.setVisibility(View.GONE);
                        break;
                }
            }

            this.removeGuestId = removeGuestId;
            callRemoveGuestAPI(removeGuestId);
        }
    }

    private void callGuestVideoMute() {
        Log.i("autolog", "callGuestVideoMute: " + guest.getUser_id());

        if (SessionUser.getUser().getUser_id().equals(guest.getUser_id())) {
            gusetOneVideoMute = "1";
            if (isGuestVideoMuted) {
                callVideoMuteAPI(guest.getUser_id(), broadcaster.getUser_id(), "no");
                isGuestVideoMuted = false;
                ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.videomute));
                worker().getRtcEngine().muteLocalVideoStream(false);
                if (!flag)
                    worker().getRtcEngine().muteLocalAudioStream(false);
                sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " Guest One UnMuted");
                guestonemute = false;
                rlGuestTwoClose.setVisibility(View.GONE);
                rlVideoMuteGuestTwo.setVisibility(View.GONE);
                rl_videoMuteGuestOne1.setVisibility(View.GONE);
            } else {
                callVideoMuteAPI(guest.getUser_id(), broadcaster.getUser_id(), "yes");
                isGuestVideoMuted = true;
                ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.video_unmute));
                worker().getRtcEngine().muteLocalVideoStream(true);
                if (!flag)
                    worker().getRtcEngine().muteLocalAudioStream(false);
                sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " Guest One Muted");
                guestonemute = true;
                rlGuestTwoClose.setVisibility(View.VISIBLE);
                rlVideoMuteGuestTwo.setVisibility(View.VISIBLE);
                rl_videoMuteGuestOne1.setVisibility(View.VISIBLE);
            }
        } else if (SessionUser.getUser().getUser_id().equals(guest2.getUser_id())) {
            gusetTwoVideoMute = "1";
            if (isGuestVideoMuted) {
                callVideoMuteAPI(guest2.getUser_id(), broadcaster.getUser_id(), "no");
                isGuestVideoMuted = false;
                ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.videomute));
                worker().getRtcEngine().muteLocalVideoStream(false);
                sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " Guest Two UnMuTed");
                guesttwomute = false;
                rlGuestClose.setVisibility(View.GONE);
                rlVideoMuteGuestOne.setVisibility(View.GONE);
            } else {
                callVideoMuteAPI(guest2.getUser_id(), broadcaster.getUser_id(), "yes");
                isGuestVideoMuted = true;
                ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.video_unmute));
                worker().getRtcEngine().muteLocalVideoStream(false);
                sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " Guest Two MuTed");
                guesttwomute = true;
                rlGuestClose.setVisibility(View.VISIBLE);
                rlVideoMuteGuestOne.setVisibility(View.VISIBLE);
            }
        }
    }

    private void callGuestVideoMutePause() {
        if (isGuest1 || isGuest2) {
            if (isGuest1) {
                if (SessionUser.getUser().getUser_id().equals(guest.getUser_id())) {
                    callVideoMuteAPI(guest.getUser_id(), broadcaster.getUser_id(), "yes");
                    if (!isGuestVideoMuted) {
                        ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.video_unmute));
                        worker().getRtcEngine().muteLocalVideoStream(true);
                        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " Guest One Muted");
                        guestonemute = true;
                        rlVideoMuteGuestOne.setVisibility(View.VISIBLE);
                    }
                    if (!flag)
                        worker().getRtcEngine().muteLocalAudioStream(true);
                    worker().getRtcEngine().muteAllRemoteAudioStreams(true);
                }
            }
            if (isGuest2) {
                if (SessionUser.getUser().getUser_id().equals(guest2.getUser_id())) {
                    callVideoMuteAPI(guest2.getUser_id(), broadcaster.getUser_id(), "yes");
                    if (!isGuestVideoMuted) {
                        ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.video_unmute));
                        worker().getRtcEngine().muteLocalVideoStream(true);
                        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " Guest Two MuTed");
                        guesttwomute = true;
                        rlVideoMuteGuestTwo.setVisibility(View.VISIBLE);
                    }
                    if (!flag)
                        worker().getRtcEngine().muteLocalAudioStream(true);
                    worker().getRtcEngine().muteAllRemoteAudioStreams(true);
                }
            }
        }
    }

    private void callGuestVideoMuteResume() {
        if (isGuest1 || isGuest2) {
            if (isGuest1) {
                if (SessionUser.getUser().getUser_id().equals(guest.getUser_id())) {
                    callVideoMuteAPI(guest.getUser_id(), broadcaster.getUser_id(), "no");
                    if (!isGuestVideoMuted) {
                        ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.videomute));
                        worker().getRtcEngine().muteLocalVideoStream(false);
                        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " Guest One UnMuted");
                        guestonemute = false;
                        rlVideoMuteGuestOne.setVisibility(View.GONE);
                    }
                    if (!flag)
                        worker().getRtcEngine().muteLocalAudioStream(false);
                    worker().getRtcEngine().muteAllRemoteAudioStreams(false);
                }
            }
            if (isGuest2) {
                if (SessionUser.getUser().getUser_id().equals(guest2.getUser_id())) {
                    callVideoMuteAPI(guest2.getUser_id(), broadcaster.getUser_id(), "no");
                    if (!isGuestVideoMuted) {
                        ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.videomute));
                        worker().getRtcEngine().muteLocalVideoStream(false);
                        sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " Guest Two UnMuTed");
                        guesttwomute = false;
                        rlVideoMuteGuestTwo.setVisibility(View.GONE);
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
                                //  showToast(genericResponse.getData().getMessage());
                            } else {
                                //    showToast(genericResponse.getMessage());
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

    private void callGetGuestsAPI() {
        if (utils.isNetworkAvailable()) {
            if (utils.isNetworkAvailable()) {
                final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
                Call<UsersResponse> call = apiClient.getGuests(SessionUser.getUser().getUser_id(), broadcasterId);
                call.enqueue(new retrofit2.Callback<UsersResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<UsersResponse> call, @NonNull Response<UsersResponse> response) {
                        UsersResponse usersResponse = response.body();
                        Log.i("autolog", "response: " + response.raw().request().url());
                        if (response.code() == 200) {
                            if (usersResponse != null) {
                                if (usersResponse.getStatus().equalsIgnoreCase("success")) {
                                    tvConnecting.setVisibility(View.GONE);
                                    tvConnectingTwo.setVisibility(View.GONE);
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


        switchToDefaultVideoView();

        if (guests.size() == 0) {
            isGuest1 = false;
            isGuest2 = false;

            guest = null;
            guest2 = null;
        }

        if (guests.size() > 0)
            loadGuestDetails(guests);
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
                                    sendChannelMessage(SessionUser.getUser().getUser_id() + level + " has accepted your video call request ");
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
        if (isClose) {
            callRemoveImageAPI();
            Constants_app.cleanMessageListBeanList();
            finish();
        } else {
            loadGuestDetails(users);
        }
        loadGuestDetails(users);
    }

    private void switchAudienceToGuest() {
        isGuest = true;
        doSwitchToBroadcaster(true);
    }

    public void loadGuestDetails(ArrayList<Audience> users) {


        if (isBroadcaster) {
            mGuests.clear();
            mGuests.addAll(users);
        }
        if (isGuest)
            resetGuestValues();

        for (int i = 0; i < 2; i++) {
            rlVideoMutes.get(i).setVisibility(View.GONE);
        }


/*
        if (users.size()==2){
            if (users.get(0).getVideo_muted().equalsIgnoreCase("yes")){
                rlVideoMuteGuestTwo.setVisibility(View.VISIBLE);
            }
        }
*/
        for (int i = 0; i < users.size(); i++) {
            try {
                Picasso.get().load(users.get(i).getProfile_pic()).fit().transform(new BlurTransformation(mActivity)).centerCrop()
                        .memoryPolicy(MemoryPolicy.NO_STORE).into(ivBlurList.get(i));
            } catch (Exception e) {
                Crashlytics.logException(e);
            }

           /* if (users.get(i).getVideo_muted().equalsIgnoreCase("yes")) {
                rlVideoMutes.get(i).setVisibility(View.VISIBLE);
                if (users.size() == 2) {
                    if (users.get(1).getVideo_muted().equalsIgnoreCase("yes")) {
                        rlVideoMuteGuestOne.setVisibility(View.VISIBLE);
                    } else {
                        rl_videoMuteGuestOne1.setVisibility(View.GONE);
                    }
                }

            } else {
                rlVideoMutes.get(i).setVisibility(View.GONE);
                rlVideoMuteGuestTwo.setVisibility(View.GONE);

            }*/
        }

        if (users.size() > 0) {
            if (isBroadcaster) {
                ivGuestClose.setVisibility(View.GONE);
                ivGift.setVisibility(View.GONE);
                ivVideoMute.setVisibility(View.GONE);
            } else {

            }

            if (users.size() == 1) {

                isGuest1 = true;
                isGuest2 = false;

                guest = users.get(0);
                guest2 = null;

                if (rlGuestTwoClose.getVisibility() == View.VISIBLE) {
                    Log.d("rlGuestTwoClose", "visible: ");
                } else {
                    Log.d("rlGuestTwoClose", "gone: ");
                }
                if (rlGuestClose.getVisibility() == View.VISIBLE) {
                    Log.d("rlGuestClose", "visible: ");
                } else {
                    Log.d("rlGuestClose", "gone: ");
                }


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
            }
        } else {

            if (!isBroadcaster) {
                ivGift.setVisibility(View.VISIBLE);
            }

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
        callAudiencesAPI1(broadcaster.getUser_id());
    }

    private void resetGuestValues() {
        isThisGuest1 = false;
        isThisGuest2 = false;
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
        String text = "hi how are you..!";
        byte[] bytes = new byte[0];
        try {
            bytes = text.getBytes("UTF-8");
            String text1 = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        sendChannelMessage(SessionUser.getUser().getUser_id() + level + "Broadcast has been Resumed!");
    }

    private void callBroadcasterClose() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    isClose = true;
                    callStatusAPI();
                    callUnBlockAPI();
                    callTextmuteAPI();
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
                if (fromprofile) {
                    changeActivity(ActivityHome.class);
                    finish();
                } else {
                    finish();
                }
            }
        }
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
                isBroadcastMuted = false;
                rlPause.setVisibility(View.GONE);
                callBroadcasterUnMute();
            } else if (isAudience) {
                worker().getRtcEngine().muteAllRemoteAudioStreams(false);
            }
        } else {
            isCreated = false;
        }
        isClicked = false;

        BLiveApplication.setCurrentActivity(this);
        if (isRedirectScreen) {
            isRedirectScreen = false;
            dvalue = Integer.valueOf(SessionUser.getUser().getDiamond());
            tvCurrentDiamondValue.setText(SessionUser.getUser().getDiamond());
        }
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause:" + isGuest);
        super.onPause();
        if (isGuest) {
            Log.e(TAG, "onPause:" + isGuest);
            callGuestVideoMutePause();
        } else if (isBroadcaster) {
            onPauseStartTime = System.currentTimeMillis();
            Log.e(TAG, "onResume: " + onPauseStartTime);
            isBroadcastMuted = true;
            rlPause.setVisibility(View.VISIBLE);
            callBroadcasterMute();
        } else if (isAudience) {
            worker().getRtcEngine().muteAllRemoteAudioStreams(true);
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


  /*  @Override
    protected void onResume() {
        Log.e(TAG, "onResume: ");
        getLevel();
        super.onResume();
        isClicked = false;
        if (isBroadcaster) {
            onResumeStopTime = System.currentTimeMillis();
            long idelTime = onResumeStopTime - onPauseStartTime;
            totalIdelTime = totalIdelTime + idelTime;
            ivGift.setVisibility(View.GONE);
            clearNotification();
            isBroadcastMuted = false;
            worker().getRtcEngine().enableVideo();
            if (!flag)
                worker().getRtcEngine().muteLocalAudioStream(false);
            worker().getRtcEngine().muteAllRemoteAudioStreams(false);
            isBroadcasterPaused = false;
            sendChannelMessage(SessionUser.getUser().getUser_id() + level + "Broadcast has been Resumed!");
        } else if (isGuest) {
            clearNotification();
            if (!videoFlag) {
                isVideoMute = false;
                worker().getRtcEngine().muteLocalVideoStream(false);
                if (!flag)
                    worker().getRtcEngine().muteLocalAudioStream(false);
                worker().getRtcEngine().muteAllRemoteAudioStreams(false);
                sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " Guest Video UnMuted");
                ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.videomute));
            }
        } else if (isAudience) {
            worker().getRtcEngine().muteAllRemoteAudioStreams(false);
        }

        BLiveApplication.setCurrentActivity(this);
        if (isRedirectScreen) {
            isRedirectScreen = false;
            dvalue = Integer.valueOf(SessionUser.getUser().getDiamond());
            tvCurrentDiamondValue.setText(SessionUser.getUser().getDiamond());
        }

        if (isRedirectScreen) {
            isRedirectScreen = false;
        }
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause: ");
        super.onPause();
        getLevel();
        if (isClose) {
            return;
        }
        if (isBroadcaster) {
            onPauseStartTime = System.currentTimeMillis();
            Log.e(TAG, "onResume: " + onPauseStartTime);
            showNotification();
            isBroadcastMuted = true;
            worker().getRtcEngine().disableVideo();
            if (!flag)
                worker().getRtcEngine().muteLocalAudioStream(true);
            worker().getRtcEngine().muteAllRemoteAudioStreams(true);
            isBroadcasterPaused = true;
            sendChannelMessage(SessionUser.getUser().getUser_id() + level + "Broadcast has been Paused!");
        } else if (isGuest) {
            showNotification();
            if (!videoFlag) {
                isVideoMute = true;
                worker().getRtcEngine().muteLocalVideoStream(true);
                if (!flag)
                    worker().getRtcEngine().muteLocalAudioStream(true);
                worker().getRtcEngine().muteAllRemoteAudioStreams(true);
                sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " Guest Video Muted");
                ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.video_unmute));
            }
        } else if (isAudience) {
            worker().getRtcEngine().muteAllRemoteAudioStreams(true);
        }
    }*/


    private void callUnBlockAPI() {
        if (utils.isNetworkAvailable()) {
            /* utils.showProgress();*/
            if (blockedlist.size() == 0) {

            } else {
                for (int j = 0; j < blockedlist.size(); j++) {
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

    private void callGetGuestsAPI1() {
        if (utils.isNetworkAvailable()) {
            if (utils.isNetworkAvailable()) {
                utils.showProgress();
                final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
                Call<UsersResponse> call = apiClient.getGuests(SessionUser.getUser().getUser_id(), broadcasterId);
                call.enqueue(new retrofit2.Callback<UsersResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<UsersResponse> call, @NonNull Response<UsersResponse> response) {
                        UsersResponse usersResponse = response.body();
                        if (response.code() == 200) {
                            if (usersResponse != null) {
                                if (usersResponse.getStatus().equalsIgnoreCase("success")) {
                                    utils.hideProgress();
                                    Log.i("autolog", "usersResponse " + usersResponse.getData().getGuests().size());
                                    guestarraylist.clear();
                                    for (int i = 0; i < usersResponse.getData().getGuests().size(); i++) {
                                        guestarraylist.add(usersResponse.getData().getGuests().get(i));
                                    }
                                    Log.i("autolog", "guestarraylist " + guestarraylist.size());
                                    if (guestarraylist.size() == 2) {


                                        if (guestclick.equalsIgnoreCase("guest1")) {
                                            try {
                                                if (isGuest1) {
                                                    if (SessionUser.getUser().getUser_id().equals(guestarraylist.get(0).getUser_id())) {
                                                        showGuestProfile(guestarraylist.get(0), 1);
                                                    } else if (isBroadcaster) {
                                                        showBroadcasterControl(guestarraylist.get(0), 1);
                                                    } else if (isAudience) {
                                                        showAlertViewProfile(guestarraylist.get(0), false);
                                                    }
                                                } else if (isAudience) {
                                                    Log.e(TAG, "iS Audience: " + "Clicked");
                                                    showAlertViewProfile(guestarraylist.get(0), false);
                                                }

                                            } catch (Exception e) {
                                                Crashlytics.logException(e);
                                            }
                                        } else if (guestclick.equalsIgnoreCase("guest2")) {
                                            try {
                                                Log.i("autolog", "guestarraylist: " + guestarraylist.get(0).getUsername());
                                                if (isGuest2) {
                                                    if (SessionUser.getUser().getUser_id().equals(guestarraylist.get(1).getUser_id())) {
                                                        showGuestProfile(guestarraylist.get(1), 2);
                                                    } else if (isBroadcaster) {
                                                        showBroadcasterControl(guestarraylist.get(1), 2);
                                                    } else if (isAudience) {
                                                        showAlertViewProfile(guestarraylist.get(1), false);
                                                    }
                                                } else if (isAudience) {
                                                    showAlertViewProfile(guestarraylist.get(1), false);
                                                }
                                            } catch (Exception e) {
                                                Crashlytics.logException(e);
                                                Log.i("autolog", "e: " + e.getMessage());
                                            }
                                        } else {
                                            Log.i("autolog", "guestarraylist:already loded " + guestarraylist.get(0).getUsername());
                                        }
                                    }
                                    if (guestarraylist.size() == 1) {
                                        try {
                                            if (SessionUser.getUser().getUser_id().equals(guestarraylist.get(0).getUser_id())) {
                                                showGuestProfile(guestarraylist.get(0), 1);
                                            } else if (isBroadcaster) {
                                                showBroadcasterControl(guestarraylist.get(0), 1);
                                            } else if (isAudience) {
                                                showAlertViewProfile(guestarraylist.get(0), false);
                                            }
                                        } catch (Exception e) {
                                            Crashlytics.logException(e);
                                            Log.i("autolog", "e: " + e.getMessage());
                                        }
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

    public void sendPeerMessage(String dst, String content) {
        Log.i("autolog", "content: " + content);

        final RtmMessage message = mRtmClient.createMessage();
        message.setText(content);
        SendMessageOptions option = new SendMessageOptions();
        mRtmClient.sendMessageToPeer(dst, message, option, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("autolog", "aVoid: " + aVoid);
                /*pkRequestSent = true;*/
                if (dialog_friends.isShowing()) {
                    dialog_friends.dismiss();
                }
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                Log.i("autolog", "errorInfo: " + errorInfo.getErrorCode() + "errormsg" + errorInfo.getErrorCode());
                pkRequestSent = false;
            }
        });
    }

    public ChannelMediaRelayConfiguration getAllListViewInfos(String channelName) {
        Log.i("autolog", "channelName: " + channelName);
        ChannelMediaRelayConfiguration configuration = new ChannelMediaRelayConfiguration();
        ChannelMediaInfo tempSrcInfo = new ChannelMediaInfo(null, null, 0);
        configuration.setSrcChannelInfo(tempSrcInfo);
        channelMediaInfos.clear();
        for (int i = 0; i < 1; i++) {
            String tempDestToken = null;
            ChannelMediaInfo tempDestInfo = new ChannelMediaInfo(channelName, tempDestToken, 12345);
            channelMediaInfos.add(tempDestInfo);
            configuration.setDestChannelInfo(tempDestInfo.channelName, tempDestInfo);
            if (configuration == null) {
                showToast("dest channel should not be null");
            }
            worker().getRtcEngine().startChannelMediaRelay(configuration);
        }
        return configuration;
    }

    private void loadInviteLists() {
        if (!pkRequestSent) {
            if (utils.isNetworkAvailable()) {
                utils.showProgress();
                final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
                Call<AudienceResponse> call = apiClient.getInvitesList("friend", "1", SessionUser.getUser().getUser_id());
                call.enqueue(new retrofit2.Callback<AudienceResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<AudienceResponse> call, @NonNull Response<AudienceResponse> response) {
                        utils.hideProgress();
                        AudienceResponse invitesResponse = response.body();
                        if (response.code() == 200) {
                            if (invitesResponse != null) {
                                if (invitesResponse.getStatus().equalsIgnoreCase("success")) {
                                    onInviteListSuccess(invitesResponse.getData().getUserList());
                                } else {
                                    showToast(invitesResponse.getMessage());
                                    showToast("No Friends");
                                }
                            } else {
                                showToast("No Friends");
                                showToast(getString(R.string.server_error));
                            }
                        } else {
                            showToast("No Friends");
                            checkResponseCode(response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<AudienceResponse> call, @NonNull Throwable t) {
                        showToast(t.getMessage());
                    }
                });
            }

        } else {
            showToast("Pk Request Already In progress");
        }
    }

    private void loadInviteListsFriendsPrivate() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityLiveRoom.this);
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

    private void onInviteListSuccess(ArrayList<User> inviteUsers) {
        Log.i("autolog", "inviteUsers: " + inviteUsers.size());
        try {
            /*dialog_friends.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));*/
            dialog_friends.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog_friends.getWindow().setGravity(Gravity.BOTTOM);
            dialog_friends.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //style id
            dialog_friends.setContentView(R.layout.alertdialogfriends);
            dialog_friends.setCanceledOnTouchOutside(true);
            TextView nofriends = dialog_friends.findViewById(R.id.nofriends);
            RecyclerView rvTest = dialog_friends.findViewById(R.id.recylerview_friends);
            dialog_friends.setCancelable(true);
            if (inviteUsers.size() == 0) {
                nofriends.setVisibility(View.VISIBLE);
                rvTest.setVisibility(View.GONE);
            } else {
                invitesUserList.clear();
                for (User user : inviteUsers) {
                    if (/*user.getStatus().equals(getString(R.string.user_active_status)) &&*/ user.getBroadcast_type().equalsIgnoreCase("solo") &&
                            user.getFriend_blocked() == 0 && user.getGuest().equalsIgnoreCase("0")) {
                        invitesUserList.add(user);
                    }
                }
                Log.i("autolog", "invitesUserList: " + invitesUserList.size());

                if (invitesUserList.size() == 0) {
                    nofriends.setVisibility(View.VISIBLE);
                    rvTest.setVisibility(View.GONE);
                    dialog_friends.show();
                } else {
                    inviteListAdapter = new InviteListAdapter(this, invitesUserList, this);
                    inviteListAdapter.setOnClickListener(ActivityLiveRoom.this);
                    nofriends.setVisibility(View.GONE);
                    rvTest.setVisibility(View.VISIBLE);
                    dialog_friends.show();
                    rvTest.setHasFixedSize(true);
                    rvTest.setLayoutManager(new LinearLayoutManager(context));
                    rvTest.setAdapter(inviteListAdapter);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast(e.getMessage());
        }
    }

    public void callMatchPkChallenge(String broadcasterId, String id1, String pkusername) {
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<Pksession> call = apiClient.startPkSession(broadcasterId, id1, pkusername);
            call.enqueue(new retrofit2.Callback<Pksession>() {
                @Override
                public void onResponse(@NonNull Call<Pksession> call, @NonNull Response<Pksession> response) {
                    Pksession genericResponse = response.body();
                    if (response.code() == 200) {
                        if (genericResponse != null) {
                            Log.d(TAG, "onResponse: " + "generic" + genericResponse.getStatus() + response.code());
                            if (genericResponse.getStatus().equalsIgnoreCase("success")) {
                                SessionId = genericResponse.getSessionId();
                                sendChannelMessage("sessionid=" + SessionId);
                                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Session_Id", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("session_Id", genericResponse.getSessionId());
                                editor.commit();
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
                public void onFailure(@NonNull Call<Pksession> call, @NonNull Throwable t) {
                    showToast(t.getMessage());
                }
            });
        }
    }

    public void pkgone() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        ivPkTopper.setVisibility(View.GONE);
        scroll.setText("");
        scroll.setVisibility(View.GONE);
        ivPkTopper_audience.setVisibility(View.GONE);
        pk_layout.setVisibility(View.GONE);
        rv_images_pk.setVisibility(View.GONE);
        disconnect_progress.setVisibility(View.GONE);
        disconnect.setVisibility(View.GONE);
        rvImages.setVisibility(View.VISIBLE);
        progressBar_seek1.setVisibility(View.VISIBLE);
        tvTimerCount.setVisibility(View.VISIBLE);
        progressBar_seek1.setVisibility(View.VISIBLE);
        broadcurrentgold.setVisibility(View.VISIBLE);
        guestcurrentgold.setVisibility(View.VISIBLE);
        ivFreeGiftIcon.setVisibility(View.VISIBLE);
        tvFreeGiftCount.setVisibility(View.VISIBLE);
        firsstlay.setVisibility(View.GONE);
        secondtlay.setVisibility(View.GONE);
        firsstlay.setBackground(null);
        secondtlay.setBackground(null);
        secondtlay.setVisibility(View.GONE);
        firsstlay.setVisibility(View.GONE);

        if (ispknow) {
            if (broadcasterAudience || isBroadcaster) {
                level = "98";
            } else if (guestAudience || pkGuest) {
                level = "99";
            }
        } else {
            level = SessionUser.getUser().getLevel();
            Log.i("autolog", "level: " + level);
            level = originallevel;
            Log.i("autolog", "level: " + level);
            rl_star.setVisibility(View.VISIBLE);
        }

        if (isBroadcaster) {
            ivJoin.setVisibility(View.GONE);
            bottomBroadcaster.setVisibility(View.VISIBLE);
            bottomAudience.setVisibility(View.GONE);
            if (mUidsList.size() == 1) {
                ivpk.setVisibility(View.VISIBLE);
            } else {
                ivpk.setVisibility(View.GONE);
            }
        } else if (pkGuest) {
            ivJoin.setVisibility(View.GONE);
            ivpk.setVisibility(View.VISIBLE);
        } else if (isAudience) {
            ivJoin.setVisibility(View.VISIBLE);
        } else if (isGuest || isGuest2 || isGuest1) {
            ivJoin.setVisibility(View.GONE);
            ivpk.setVisibility(View.GONE);
            ivVideoMute.setVisibility(View.VISIBLE);
           /* bottomBroadcaster.setVisibility(View.VISIBLE);
            bottomAudience.setVisibility(View.GONE);*/
        } else if (isAudience) {
            ivJoin.setVisibility(View.VISIBLE);
        } else {
            ivJoin.setVisibility(View.VISIBLE);
        }
        if (cRole == 0) {
            fabRequest.setVisibility(View.VISIBLE);
            if (fabMenu.isOpened()) {
                fabMenu.close(true);
            }
        }

        levelchange();
        privateicon();
        Log.i("autolog", "privatestatus: " + privatestatus);


    }

    public void pkvisible() {
        if (ispknow) {
            ivPkTopper.setVisibility(View.VISIBLE);
            ivPkTopper_audience.setVisibility(View.VISIBLE);
            rl_star.setVisibility(View.INVISIBLE);
        }

        pk_layout.setVisibility(View.VISIBLE);
        firsstlay.setVisibility(View.VISIBLE);
        secondtlay.setVisibility(View.VISIBLE);
        scroll.setVisibility(View.VISIBLE);
        progressBar_seek1.setVisibility(View.GONE);
        if (ispknow) {
            ivFreeGiftIcon.setVisibility(View.GONE);
        }
        if (!ispknow) {
            scroll.setVisibility(View.GONE);
            ivPkTopper.setVisibility(View.GONE);
        }
        tvFreeGiftCount.setVisibility(View.GONE);
        tvTimerCount.setVisibility(View.GONE);
        progressBar_seek1.setVisibility(View.GONE);
        broadcurrentgold.setVisibility(View.GONE);
        guestcurrentgold.setVisibility(View.GONE);
        broad_topgift.setVisibility(View.GONE);
        guest_topgift.setVisibility(View.GONE);
        secondtlay.setVisibility(View.VISIBLE);
        firsstlay.setVisibility(View.VISIBLE);
        timerStarted = false;
        ivpk.setVisibility(View.GONE);
        ivJoin.setVisibility(View.GONE);

        if (pkGuest | guestAudience) {
            progressBar_seek1.setProgressDrawable(getDrawable(R.drawable.seekbar_oppo));
            try {
                broadcurrentgold.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_guest, 0, 0, 0);
                guestcurrentgold.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star, 0, 0, 0);
            } catch (Exception e) {
                Log.i("autolog", "e: " + e);
            }
        }

        if (pkGuest) {
            ivGift.setVisibility(View.GONE);
            button2.setVisibility(View.GONE);
            bottomBroadcaster.setVisibility(View.VISIBLE);
            bottomAudience.setVisibility(View.GONE);
        }
        if (fabMenu.isOpened()) {
            fabMenu.close(true);
        }

        levelchange();

     /*   if (isBroadcaster) {
            bottomBroadcaster.setVisibility(View.VISIBLE);
            bottomAudience.setVisibility(View.GONE);
        } else if (pkGuest) {
            bottomBroadcaster.setVisibility(View.VISIBLE);
            bottomAudience.setVisibility(View.GONE);
        } else {
            bottomBroadcaster.setVisibility(View.GONE);
            bottomAudience.setVisibility(View.VISIBLE);
        }*/
        privateicon();
    }

    private void privateicon() {
        if (privatestatus.equalsIgnoreCase("PRIVATE")) {
            ivpk.setVisibility(View.GONE);
        } else {
            if (isBroadcaster) {
                if (!ispknow) {
                    ivpk.setVisibility(View.VISIBLE);
                } else {
                    ivpk.setVisibility(View.GONE);
                }
                ivActiveViewers.setVisibility(View.GONE);
            } else {
                ivActiveViewers.setVisibility(View.GONE);
            }

        }
    }

    private void levelchange() {
        originallevel = level;
        if (ispknow) {
            if (broadcasterAudience || isBroadcaster) {
                level = "98";

            } else if (guestAudience || pkGuest) {
                level = "99";
            }
        } else {
            level = SessionUser.getUser().getLevel();
        }
        Log.i("autolog", "level: " + level);

    }

    private void showdisconnect() {
        try {
            final BottomSheetDialog parentView = new BottomSheetDialog(ActivityLiveRoom.this);
            parentView.requestWindowFeature(Window.FEATURE_NO_TITLE);
            parentView.setCancelable(true);
            parentView.setContentView(R.layout.disconnect);

            Window window = parentView.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);
            parentView.show();
            ImageView disconnect = parentView.findViewById(R.id.disconnect_btm);
            Button disconnect_btn = parentView.findViewById(R.id.disconnect_btn_btm);
            TextView name_pk = parentView.findViewById(R.id.name_pk);
            try {
                Glide.with(ActivityLiveRoom.this).load(R.drawable.matching).apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true))
                        .into(disconnect);
            } catch (Exception e) {
                Crashlytics.log(e.toString());
            }

            disconnect_btn.setOnClickListener(v -> {

                pkdisconnet();

                parentView.dismiss();

                /*if (ispknow) {
                    if (pkGuest) {
                        runOnUiThread(() -> {
                            isBroadcaster = true;
                            mUidsList.clear();
                            worker().leaveChannel(config().mChannel);
                            worker().joinChannel(SessionUser.getUser().getUsername(), SessionUser.getUser().getId());
                            doRenderRemoteUi(SessionUser.getUser().getId());
                            sendChannelMessage(broadcaster.getUser_id() + level + SessionUser.getUser().getName() + " Broadcast Pk Guest Has Quit");
                            joinrtmchannel(SessionUser.getUser().getUsername(), "1");
                            ispknow = false;
                            pkGuest = false;
                            pkgone();
                            if (countDownTimer != null) {
                                Log.i("autolog", "countDownTimer: " + countDownTimer.toString());
                                countDownTimer.cancel();
                                if (utils.isNetworkAvailable()) {
                                    final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
                                    Log.d("PK", "data being sent " + broadcasterId);
                                    Call<GenericResponse> call = apiClient.endPkSession(broadcasterId, SessionUser.getUser().getUser_id(), SessionId);
                                    call.enqueue(new retrofit2.Callback<GenericResponse>() {
                                        @Override
                                        public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                                            utils.hideProgress();
                                            GenericResponse genericResponse = response.body();
                                            Log.d("PK", "response of end Pk status " + genericResponse.getStatus());
                                            if (response.code() == 200) {
                                                if (genericResponse != null) {
                                                    if (genericResponse.getStatus().equalsIgnoreCase("success")) {
                                                        Log.d("PK", "pk ended");
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
                            }
                            *//*soloActiveApi();*//*
                            parentView.dismiss();
                        });

                    } else {
                        sendChannelMessage(broadcaster.getUser_id() + level + SessionUser.getUser().getName() + " Broadcast Pk Guest Has Quit");
                        ispknow = false;
                        pkgone();

                        parentView.dismiss();

                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                            if (utils.isNetworkAvailable()) {
                                final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
                                Log.d("PK", "data being sent " + broadcasterId);
                                Call<GenericResponse> call = apiClient.endPkSession(broadcasterId, pkuserid, SessionId);
                                call.enqueue(new retrofit2.Callback<GenericResponse>() {
                                    @Override
                                    public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                                        utils.hideProgress();
                                        GenericResponse genericResponse = response.body();
                                        Log.d("PK", "response of end Pk status " + genericResponse.getStatus());
                                        if (response.code() == 200) {
                                            if (genericResponse != null) {
                                                if (genericResponse.getStatus().equalsIgnoreCase("success")) {
                                                    Log.d("PK", "pk ended");
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

                        }
                    }
                }*/

            });
            /*soloActiveApi();*/
        } catch (Exception e) {
            Log.i("autolog", "e: " + e.toString());
        }
    }

    private void pkdisconnet() {
        Log.e(TAG, "pkdisconnet: " + ispknow);
        if (ispknow) {
            if (pkGuest) {
                Log.e(TAG, "pkdisconnet: PK GUEST" + pkGuest);
                runOnUiThread(() -> {
                    if (utils.isNetworkAvailable()) {
                        final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
                        Log.d("PK", "data being sent " + broadcasterId);
                        Call<GenericResponse> call = apiClient.endPkSession(pkid, SessionUser.getUser().getUser_id(), SessionId);
                        call.enqueue(new retrofit2.Callback<GenericResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                                utils.hideProgress();
                                GenericResponse genericResponse = response.body();
                                Log.d("PK", "response-request " + response.raw().request().url());
                                if (response.code() == 200) {
                                    if (genericResponse != null) {
                                        Log.d("PK", "response of end Pk status " + genericResponse.getStatus());
                                        if (genericResponse.getStatus().equalsIgnoreCase("success")) {
                                            Log.d("PK", "pk ended");
                                            isBroadcaster = true;
                                            // cross channel STOP
                                            worker().getRtcEngine().stopChannelMediaRelay();
                                            sendChannelMessage(broadcaster.getUser_id() + level + SessionUser.getUser().getName() + " Broadcast Pk Guest Has Quit");
                                            ispknow = false;
                                            pkGuest = false;
                                            pkgone();
                                            joinrtmchannel(SessionUser.getUser().getUsername(), "0");
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
                });

            } else {
                // cross channel STOP
                if (utils.isNetworkAvailable()) {
                    final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
                    Log.d("PK", "data being sent " + broadcasterId);
                    Call<GenericResponse> call = apiClient.endPkSession(broadcasterId, pkuserid, SessionId);
                    call.enqueue(new retrofit2.Callback<GenericResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                            utils.hideProgress();
                            GenericResponse genericResponse = response.body();
                            Log.d("PK", "response-request " + response.raw().request().url());
                            if (response.code() == 200) {
                                if (genericResponse != null) {
                                    Log.d("PK", "response of end Pk status " + genericResponse.getStatus());
                                    if (genericResponse.getStatus().equalsIgnoreCase("success")) {
                                        Log.d("PK", "pk ended");
                                        worker().getRtcEngine().stopChannelMediaRelay();
                                        joinrtmchannel(SessionUser.getUser().getUsername(), "0");
                                        sendChannelMessage(broadcaster.getUser_id() + level + SessionUser.getUser().getName() + " Broadcast Pk Guest Has Quit");
                                        ispknow = false;
                                        pkgone();
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
            }
        }
    }

    private void pkdisconnetbrd() {
        Log.e(TAG, "pkdisconnet: " + ispknow);
        if (ispknow) {
            if (pkGuest) {
                Log.e(TAG, "pkdisconnet: PK GUEST" + pkGuest);
                runOnUiThread(() -> {
                    if (utils.isNetworkAvailable()) {
                        final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
                        Log.d("PK", "data being sent " + broadcasterId);
                        Call<GenericResponse> call = apiClient.endPkSession(pkid, SessionUser.getUser().getUser_id(), SessionId);
                        call.enqueue(new retrofit2.Callback<GenericResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                                utils.hideProgress();
                                GenericResponse genericResponse = response.body();
                                Log.d("PK", "response-request " + response.raw().request().url());
                                if (response.code() == 200) {
                                    if (genericResponse != null) {
                                        Log.d("PK", "response of end Pk status " + genericResponse.getStatus());
                                        if (genericResponse.getStatus().equalsIgnoreCase("success")) {
                                            Log.d("PK", "pk ended");
                                            isBroadcaster = true;
                                            // cross channel STOP
                                            worker().getRtcEngine().stopChannelMediaRelay();
                                            sendChannelMessage(" guest Pk Guest Has end");

                                            ispknow = false;
                                            pkGuest = false;
                                            pkgone();
                                            joinrtmchannel(SessionUser.getUser().getUsername(), "0");
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
                });

            } else {
                // cross channel STOP
                if (utils.isNetworkAvailable()) {
                    final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
                    Log.d("PK", "data being sent " + broadcasterId);
                    Call<GenericResponse> call = apiClient.endPkSession(broadcasterId, pkuserid, SessionId);
                    call.enqueue(new retrofit2.Callback<GenericResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                            utils.hideProgress();
                            GenericResponse genericResponse = response.body();
                            Log.d("PK", "response-request " + response.raw().request().url());
                            if (response.code() == 200) {
                                if (genericResponse != null) {
                                    Log.d("PK", "response of end Pk status " + genericResponse.getStatus());
                                    if (genericResponse.getStatus().equalsIgnoreCase("success")) {
                                        Log.d("PK", "pk ended");
                                        worker().getRtcEngine().stopChannelMediaRelay();
                                        joinrtmchannel(SessionUser.getUser().getUsername(), "0");
                                        sendChannelMessage(" Broadcaster Pk Has end");
                                        ispknow = false;
                                        pkgone();
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
            }
        }
    }

    private void showtoppers(ArrayList<PkGiftDetailsModel.Application> list) {
        try {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityLiveRoom.this);
            View parentView = getLayoutInflater().inflate(R.layout.bottom_toppers, null);
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
            RecyclerView bottomtoppers = parentView.findViewById(R.id.toppers_recycle);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            bottomtoppers.setLayoutManager(linearLayoutManager);
            Adaptertopperlist customAdapter = new Adaptertopperlist(list, ActivityLiveRoom.this);
            bottomtoppers.setAdapter(customAdapter);

        } catch (Exception e) {

        }
    }

    public void startTimer(Integer pkTimeSelectedInSecs) {
        Log.i("autolog", "pkTimeSelectedInSecs: " + pkTimeSelectedInSecs);
        if (!timerStarted) {
            Log.i("autolog", "pkTimeSelectedInSecs: " + pkTimeSelectedInSecs);
            pk_layout.setVisibility(View.VISIBLE);
            global_pk = false;
            timerStarted = true;
            tvTimerCount.setVisibility(View.VISIBLE);
            progressBar_seek1.setVisibility(View.VISIBLE);
            broadcurrentgold.setVisibility(View.VISIBLE);
            guestcurrentgold.setVisibility(View.VISIBLE);
            broad_topgift.setVisibility(View.VISIBLE);
            guest_topgift.setVisibility(View.VISIBLE);
            broadcasterWinLose.setVisibility(View.VISIBLE);
            guestWinLose.setVisibility(View.VISIBLE);
           /* broadcasterWinLose.setVisibility(View.GONE);
            guestWinLose.setVisibility(View.GONE);*/

            try {
                Glide.with(ActivityLiveRoom.this).load(R.drawable.treasure_blow_icon).apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true))
                        .into(broadcasterWinLose);
                Glide.with(ActivityLiveRoom.this).load(R.drawable.treasure_blow_icon).apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true))
                        .into(guestWinLose);
            } catch (Exception e) {

            }

            int broadcasterProgress = 50;
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer.onFinish();
            }

            Integer pkTimeInt = pkTimeSelectedInSecs * 1000;
            pkTimeInt_int = pkTimeInt;
            Log.i("autolog", "pkTimeInt_int: " + pkTimeInt_int);

            countDownTimer = new CountDownTimer(pkTimeInt, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                    long millis = millisUntilFinished;
                    String hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                    tvTimerCount.setText(hms);
                    long coun = millisUntilFinished / 1000;

                    if (coun <= 10) {
                        RunAnimation();
                    }
                }

                @Override
                public void onFinish() {
                    Log.e(TAG, "onFinish: " + " CountDown Timer Finished");
                    if (isBroadcaster) {

                    }
                }
            }.start();


            handler.postDelayed(this::pkTimerDone, pkTimeInt);
        }
    }

    private void GiftUpdateAPI() {
        Log.d(TAG, " : " + "no id exxist" + broadcasterId + "    " + pkuserid);
        if (utils.isNetworkAvailable()) {
            if (broadcaster.getUser_id().equalsIgnoreCase("") && pkuserid.equalsIgnoreCase("")) {
                showToast("id doesn't exist");
            } else {
                if (pkGuest) {
                    Log.e(TAG, "GiftUpdateAPI2:");
                    final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
                    Call<PkGiftDetailsModel> call = apiClient.getDetailsGold(pkid, SessionUser.getUser().getUser_id());
                    call.enqueue(new retrofit2.Callback<PkGiftDetailsModel>() {
                        @Override
                        public void onResponse(@NonNull Call<PkGiftDetailsModel> call, @NonNull Response<PkGiftDetailsModel> response) {
                            PkGiftDetailsModel usersResponse = response.body();
                            Log.e(TAG, "GiftUpdateAPI2:" + response.raw().request().url());

                            if (response.code() == 200) {
                                if (usersResponse != null) {
                                    if (usersResponse.getStatus().equalsIgnoreCase("success")) {
                                        responsegift(usersResponse.getData().getRequester().getPts(), usersResponse.getData().getRequestee().getPts());
                                        imageloadingtopper(usersResponse.getData().getRequester().getToppers(), usersResponse.getData().getRequestee().getToppers());
                                        giftset(usersResponse.getData().getRequester().getOver_all_gold(), usersResponse.getData().getRequestee().getOver_all_gold());
                                    } else {
//                                        showToast(usersResponse.getStatus());
                                    }
                                } else {
                                    showToast(getString(R.string.server_error));
                                }
                            } else {
                                checkResponseCode(response.code());
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<PkGiftDetailsModel> call, @NonNull Throwable t) {
                            showToast(t.getMessage());
                            Log.d(TAG, "onFailure: " + t.getMessage());
                        }
                    });
                } else {
                    Log.e(TAG, "GiftUpdateAPI2:");
                    final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
                    Call<PkGiftDetailsModel> call = apiClient.getDetailsGold(broadcasterId, pkuserid);
                    call.enqueue(new retrofit2.Callback<PkGiftDetailsModel>() {
                        @Override
                        public void onResponse(@NonNull Call<PkGiftDetailsModel> call, @NonNull Response<PkGiftDetailsModel> response) {
                            PkGiftDetailsModel usersResponse = response.body();
                            Log.e(TAG, "GiftUpdateAPI2:" + response.raw().request().url());

                            if (response.code() == 200) {
                                if (usersResponse != null) {
                                    if (usersResponse.getStatus().equalsIgnoreCase("success")) {
                                        responsegift(usersResponse.getData().getRequester().getPts(), usersResponse.getData().getRequestee().getPts());
                                        imageloadingtopper(usersResponse.getData().getRequester().getToppers(), usersResponse.getData().getRequestee().getToppers());
                                        giftset(usersResponse.getData().getRequester().getOver_all_gold(), usersResponse.getData().getRequestee().getOver_all_gold());
                                    } else {
                                        showToast(usersResponse.getStatus());
                                    }
                                } else {
                                    showToast(getString(R.string.server_error));
                                }
                            } else {
                                checkResponseCode(response.code());
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<PkGiftDetailsModel> call, @NonNull Throwable t) {
                            showToast(t.getMessage());
                            Log.d(TAG, "onFailure: " + t.getMessage());
                        }
                    });
                }


            }
        }
    }

    private void imageloadingtopper(ArrayList<PkGiftDetailsModel.Application> toppers, ArrayList<PkGiftDetailsModel.Application> toppers1) {
        int toppercount = toppers.size();
        int toppercountguest = toppers1.size();
        try {
            if (isBroadcaster || broadcasterAudience) {
                if (toppercount == 1) {
                    Glide.with(this)
                            .load(toppers.get(0).getProfile_pic())
                            .into(image_gold1);
                } else if (toppercount == 2) {
                    Glide.with(this)
                            .load(toppers.get(0).getProfile_pic())
                            .into(image_gold1);
                    Glide.with(this)
                            .load(toppers.get(1).getProfile_pic())
                            .into(image_gold2);
                } else if (toppercount == 3) {
                    Glide.with(this)
                            .load(toppers.get(0).getProfile_pic())
                            .into(image_gold1);
                    Glide.with(this)
                            .load(toppers.get(1).getProfile_pic())
                            .into(image_gold2);
                    Glide.with(this)
                            .load(toppers.get(2).getProfile_pic())
                            .into(image_gold3);
                }

                if (toppercountguest == 1) {
                    Glide.with(this)
                            .load(toppers1.get(0).getProfile_pic())
                            .into(guest_image_gold3);
                } else if (toppercountguest == 2) {
                    Glide.with(this)
                            .load(toppers1.get(0).getProfile_pic())
                            .into(guest_image_gold3);
                    Glide.with(this)
                            .load(toppers1.get(1).getProfile_pic())
                            .into(guest_image_gold2);
                } else if (toppercountguest == 3) {
                    Glide.with(this)
                            .load(toppers1.get(0).getProfile_pic())
                            .into(guest_image_gold3);
                    Glide.with(this)
                            .load(toppers1.get(1).getProfile_pic())
                            .into(guest_image_gold2);
                    Glide.with(this)
                            .load(toppers1.get(2).getProfile_pic())
                            .into(guest_image_gold1);
                }
            } else {
                if (toppercountguest == 1) {
                    Glide.with(this)
                            .load(toppers1.get(0).getProfile_pic())
                            .into(image_gold1);
                } else if (toppercountguest == 2) {
                    Glide.with(this)
                            .load(toppers1.get(0).getProfile_pic())
                            .into(image_gold1);
                    Glide.with(this)
                            .load(toppers1.get(1).getProfile_pic())
                            .into(image_gold2);
                } else if (toppercountguest == 3) {
                    Glide.with(this)
                            .load(toppers1.get(0).getProfile_pic())
                            .into(image_gold1);
                    Glide.with(this)
                            .load(toppers1.get(1).getProfile_pic())
                            .into(image_gold2);
                    Glide.with(this)
                            .load(toppers1.get(2).getProfile_pic())
                            .into(image_gold3);
                }

                if (toppercount == 1) {
                    Glide.with(this)
                            .load(toppers.get(0).getProfile_pic())
                            .into(guest_image_gold3);
                } else if (toppercount == 2) {
                    Glide.with(this)
                            .load(toppers.get(0).getProfile_pic())
                            .into(guest_image_gold3);
                    Glide.with(this)
                            .load(toppers.get(1).getProfile_pic())
                            .into(guest_image_gold2);
                } else if (toppercount == 3) {
                    Glide.with(this)
                            .load(toppers.get(0).getProfile_pic())
                            .into(guest_image_gold3);
                    Glide.with(this)
                            .load(toppers.get(1).getProfile_pic())
                            .into(guest_image_gold2);
                    Glide.with(this)
                            .load(toppers.get(2).getProfile_pic())
                            .into(guest_image_gold1);
                }
            }

            topperdetailsarray.clear();
            topperdetailsarray_guest.clear();

            if (broadcasterAudience || isBroadcaster) {
                topperdetailsarray = toppers;
            } else {
                topperdetailsarray_guest = toppers1;
            }
            topperdetailsarray = toppers;
            topperdetailsarray_guest = toppers1;
        } catch (Exception e) {
            Log.i("autolog", "e: " + e);
        }
    }

    public void responsegift(int requester_gold, int requestee_gold) {
        Log.i("autolog", "requestee_gold: " + requestee_gold);
        Log.i("autolog", "requester_gold: " + requester_gold);
        totalgold = 0;
        totalgold = requester_gold + requestee_gold;
        broadcastresultgold = requester_gold;
        guestresultgold = requestee_gold;

        if (broadcasterAudience || isBroadcaster) {
            guestcurrentgold.setText("" + requestee_gold);
            broadcurrentgold.setText("" + requester_gold);

        } else {
            broadcurrentgold.setText("" + requestee_gold);
            guestcurrentgold.setText("" + requester_gold);
        }

        if (totalgold == 0) {
            progressBar_seek1.setMax(2);
            progressBar_seek1.setProgress(1);
        } else {
            if (broadcasterAudience || isBroadcaster) {
                progressBar_seek1.setMax(totalgold);
                progressBar_seek1.setProgress(requester_gold);

            } else {
                progressBar_seek1.setMax(totalgold);
                progressBar_seek1.setProgress(requestee_gold);
            }
        }
    }

    /*void soloActiveApi() {
        if (isBroadcaster || pkGuest) {
            if (utils.isNetworkAvailable()) {
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

                Log.e(TAG, "callStatusAPI: ACTIVE " + idelSeconds + totalBroadcastSeconds + broadcastSeconds);

                final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
                Call<GenericResponse> call = apiClient.goLive(SessionUser.getUser().getUser_id(), "ACTIVE", "solo",
                        String.valueOf(broadcastSeconds), String.valueOf(idelSeconds), String.valueOf(totalBroadcastSeconds));
                call.enqueue(new retrofit2.Callback<GenericResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                        Log.i("autolog", "response: " + response.raw().request().url());
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
        } else {
            Log.d(TAG, "soloinactiveapi: this is not a broadcaster");
        }
    }*/


    void joinrtmchannel(String channelName, String s) {
        runOnUiThread(() -> {
            mChatManager = BLiveApplication.getInstance().getChatManager();
            Log.i("autolog", "mChatManager: " + mChatManager);
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
                            String text = message.getText();
                            Log.i("autolog", "text: " + text);
                            String fromUser = fromMember.getUserId();
                            runOnUiThread(() -> onMessageReceive(message, fromMember));
                        }

                        @Override
                        public void onMemberJoined(RtmChannelMember rtmChannelMember) {
                            runOnUiThread(() -> {
                                Log.i("autolog", "rtmChannelMember: " + rtmChannelMember.getChannelId() + rtmChannelMember.getUserId());
                                viewers = viewers + 1;
                                if (isBroadcaster) {
                                    if (isBroadcastMuted) {
                                        new Handler().postDelayed(() -> sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + "Broadcast has been Paused!"), 2500);
                                    }
                                } else if (isGuest1) {
                                    if (!ispknow) {
                                        if (guestonemute)
                                            new Handler().postDelayed(() -> sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " Guest One Muted"), 2000);
                                    }
                                } else if (isGuest2) {
                                    if (!ispknow) {
                                        if (guesttwomute)
                                            new Handler().postDelayed(() -> sendChannelMessage(SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " Guest Two MuTed"), 2000);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onMemberLeft(RtmChannelMember member) {
                            Log.i("autolog", "member: " + member);
                            runOnUiThread(() -> {
                                if (!isBroadcastEnded) {
                                    callAudiencesAPI(broadcaster.getUser_id());
                                }
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
                        Log.i("autolog-fromlive_join", "mChannelName: " + channelrtm);
                        if (errorInfo.getErrorCode() == 8) {
                            mChatManager.logout();
                        }
                        mChatManager.doLogin(channelName);

                    }
                });
            } else {
                mChatManager.doLogin(channelName);
            }
        });
    }

    public void pkTimerDone() {
        Log.i("autolog", "pkTimerDone: ");
        tvTimerCount.setVisibility(View.GONE);
        progressBar_seek1.setVisibility(View.GONE);
        broadcurrentgold.setVisibility(View.GONE);
        guestcurrentgold.setVisibility(View.GONE);
        broad_topgift.setVisibility(View.GONE);
        guest_topgift.setVisibility(View.GONE);
        rematch = "1";
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Session_Id", MODE_PRIVATE);
        String sessionId = sharedPreferences.getString("session_Id", "");

        timerStarted = false;
        if (isBroadcaster || pkGuest) {
            pkStartBtn.setVisibility(View.VISIBLE);
            if (isBroadcaster) {
                if (guestresultgold == broadcastresultgold) {
                    sendPkMessage(" Pk challenge result is draw");
                    new Handler().postDelayed(this::pkDrawAction, 100);

                } else if (guestresultgold > broadcastresultgold) {
                    sendPkMessage(" Pk challenge won by guest " + pkname);
                    Log.i("autolog", "pkid: " + pkuserid + "pkname" + pkname);
                    rewardapi(pkuserid, guestresultgold, pkdisplayname);
                    new Handler().postDelayed(this::pkGuestWon, 100);
                } else {
                    sendPkMessage(" Pk challenge won by broadcaster " + broadcaster.getName());
                    Log.i("autolog", "SessionUser.getUser().getUser_id(): " + SessionUser.getUser().getUser_id());
                    rewardapi(SessionUser.getUser().getUser_id(), broadcastresultgold, SessionUser.getUser().getName());
                    new Handler().postDelayed(this::pkBroadcasterWon, 100);
                }

                if (utils.isNetworkAvailable()) {
                    final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
                    Call<GenericResponse> call = apiClient.endPKChallenge(sessionId, SessionUser.getUser().getUser_id(), pkuserid);
                    call.enqueue(new retrofit2.Callback<GenericResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                            utils.hideProgress();
                            GenericResponse genericResponse = response.body();
                            Log.i("autolog", "genericResponse: " + response.raw().request().url());

                            if (response.code() == 200) {
                                if (genericResponse != null) {
                                    Log.i("autolog", "genericResponse: " + genericResponse);
                                    if (genericResponse.getStatus().equalsIgnoreCase("success")) {
                                        Log.d("PK", "pk ended");
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
            }
        }
        broadcasterWinLose.setVisibility(View.GONE);
        guestWinLose.setVisibility(View.GONE);
    }

    public void pkDrawAction() {
        broadcasterWinLose.setVisibility(View.VISIBLE);
        guestWinLose.setVisibility(View.VISIBLE);
        broadcasterWinLose1.setVisibility(View.VISIBLE);
        guestWinLose1.setVisibility(View.VISIBLE);

        try {
            Glide.with(ActivityLiveRoom.this).load(R.drawable.winner).apply(new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true))
                    .into(broadcasterWinLose1);
            Glide.with(ActivityLiveRoom.this).load(R.drawable.winner).apply(new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true))
                    .into(guestWinLose1);
        } catch (Exception e) {

        }

        showToast("The Pk challenge was a draw");
        guesttext.setVisibility(View.VISIBLE);
        broadtext.setVisibility(View.VISIBLE);
        guesttext.setText("Draw");
        broadtext.setText("Draw");
        responsegift(0, 0);

        new Handler().postDelayed(() -> {
            hideAnimations();
        }, showWinLoseTime);
    }

    public void pkGuestWon() {
        broadcasterWinLose.setVisibility(View.VISIBLE);
        guestWinLose.setVisibility(View.VISIBLE);
        broadcasterWinLose1.setVisibility(View.VISIBLE);
        guestWinLose1.setVisibility(View.VISIBLE);

        try {
            if (broadcasterAudience || isBroadcaster) {
                Glide.with(this).load(R.drawable.runner).apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true))
                        .into(broadcasterWinLose1);
                Glide.with(this).load(R.drawable.winner).apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true))
                        .into(guestWinLose1);
            } else {
                Glide.with(this).load(R.drawable.winner).apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true))
                        .into(broadcasterWinLose1);
                Glide.with(this).load(R.drawable.runner).apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true))
                        .into(guestWinLose1);
            }

        } catch (Exception e) {

        }

        guesttext.setVisibility(View.VISIBLE);
        broadtext.setVisibility(View.VISIBLE);
        showwinneralert();


        responsegift(0, 0);

        if (broadcasterAudience || isBroadcaster) {
            guesttext.setText("Winner");
            broadtext.setText("Loser");
        } else {
            broadtext.setText("Winner");
            guesttext.setText("Loser");
        }

        new Handler().postDelayed(() -> {
            hideAnimations();
        }, showWinLoseTime);
    }

    public void pkBroadcasterWon() {
        showwinneralert();
        responsegift(0, 0);
        broadcasterWinLose.setVisibility(View.VISIBLE);
        guestWinLose.setVisibility(View.VISIBLE);
        broadcasterWinLose1.setVisibility(View.VISIBLE);
        guestWinLose1.setVisibility(View.VISIBLE);
        try {
            if (broadcasterAudience || isBroadcaster) {
                Glide.with(this).load(R.drawable.winner).apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true))
                        .into(broadcasterWinLose1);
                Glide.with(this).load(R.drawable.runner).apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true))
                        .into(guestWinLose1);
            } else {
                Glide.with(this).load(R.drawable.runner).apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true))
                        .into(broadcasterWinLose1);
                Glide.with(this).load(R.drawable.winner).apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true))
                        .into(guestWinLose1);
            }
        } catch (Exception e) {

        }

        guesttext.setVisibility(View.VISIBLE);
        broadtext.setVisibility(View.VISIBLE);

        if (broadcasterAudience || isBroadcaster) {
            guesttext.setText("Loser");
            broadtext.setText("Winner");
        } else {
            guesttext.setText("Winner");
            broadtext.setText("Loser");
        }
        new Handler().postDelayed(() -> {
            hideAnimations();
        }, showWinLoseTime);
    }

    private void RunAnimation() {
        Animation a = AnimationUtils.loadAnimation(this, R.anim.popup_txt);
        a.reset();
        tvTimerCount.clearAnimation();
        tvTimerCount.startAnimation(a);
    }

    public void hideAnimations() {
        broadcasterWinLose.setVisibility(View.GONE);
        guestWinLose.setVisibility(View.GONE);
        broadcasterWinLose1.setVisibility(View.GONE);
        guestWinLose1.setVisibility(View.GONE);
        guesttext.setVisibility(View.GONE);
        broadtext.setVisibility(View.GONE);
    }

    private void rewardapi(String pkuserid, int guestresultgold, String name) {
        if (utils.isNetworkAvailable()) {
            utils.showProgress();
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<Giftrewards> call = apiClient.pk_reward(pkuserid, String.valueOf(guestresultgold));
            call.enqueue(new retrofit2.Callback<Giftrewards>() {
                @Override
                public void onResponse(@NonNull Call<Giftrewards> call, @NonNull Response<Giftrewards> response) {
                    Log.d("url" + "responseGet", "got response: " + response.raw().request().url());
                    utils.hideProgress();
                    Giftrewards invitesResponse = response.body();
                    if (response.code() == 200) {
                        if (invitesResponse != null) {
                            Log.d(TAG + "responseGet", "got response: " + response.body().getStatus());
                            if (invitesResponse.getStatus().equalsIgnoreCase("success")) {
                                try {
                                    new Handler().postDelayed(() -> {
                                        if (dialog_winner.isShowing()) {
                                            dialog_winner.dismiss();
                                        }

                                        MessageBean message1 = new MessageBean("", SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + invitesResponse.getData().getMessage(), true, false, false);
                                        messageBeanList.add(message1);
                                        adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                                        rvMessages.scrollToPosition(messageBeanList.size() - 1);
                                        /*sendChannelMessage(name + "Won Pk Challenge and he got Reward" + invitesResponse.getData().getMessage() + " Gold");*/
                                        sendChannelMessage("Won Pk Challenge and he got Reward" + invitesResponse.getData().getMessage());
                                        rewarddialog.setTitle("Pk");
                                        rewarddialog.setCancelable(true);
                                        rewarddialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationexit;
                                        rewarddialog.setContentView(R.layout.rewardlayout);
                                        Window window = rewarddialog.getWindow();
                                        WindowManager.LayoutParams wlp = window.getAttributes();
                                        wlp.gravity = Gravity.CENTER;
                                        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                                        window.setAttributes(wlp);
                                        /*rewarddialog.show();*/
                                        TextView text_rewardpoint = rewarddialog.findViewById(R.id.text_rewardpoint);
                                        Button ok = rewarddialog.findViewById(R.id.ok_button);
                                        text_rewardpoint.setText(name + " Won " + invitesResponse.getData().getMessage() + " Gold");
                                        scroll.setText(invitesResponse.getData().getMessage());
                                        /*scroll.setText("\uD83C\uDF81"+name + " Won " + invitesResponse.getData().getMessage() + " Gold");*/
                                        ok.setOnClickListener(v -> {
                                            if (rewarddialog.isShowing()) {
                                                rewarddialog.dismiss();
                                            }
                                        });
                                    }, 1000);
                                } catch (Exception e) {
                                    Log.i("autolog", "e: " + e);
                                    Crashlytics.log(e.getMessage());

                                }
                            } else {
                                showToast(invitesResponse.getMessage());
                            }
                        } else {
                            showToast(getString(R.string.server_error));
                        }
                    } else {
                        checkResponseCode(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Giftrewards> call, @NonNull Throwable t) {
                    showToast(t.getMessage());
                }
            });
        }
    }

    void showwinneralert() {
        dialog_winner.setContentView(R.layout.winningdialog);
        dialog_winner.setTitle("pk");
        dialog_winner.setCancelable(true);
        dialog_winner.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        ImageView imageView_winner = dialog_winner.findViewById(R.id.imageView_winner);
        try {
            Glide.with(ActivityLiveRoom.this).load(R.drawable.treasure_chest).apply(new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true))
                    .into(imageView_winner);
        } catch (Exception e) {
            Crashlytics.log(e.toString());
        }
        try {
            dialog_winner.show();
        } catch (Exception e) {
            Crashlytics.log(e.toString());
        }
        new Handler().postDelayed(() -> dialog_winner.dismiss(), 4000);
    }

    public void startBroadcasterTimer(Integer pkTimeSelected) {
        startTimer(pkTimeSelected);
        Log.d(TAG, "startBroadcasterTimer: " + SessionId + "time" + pkTimeSelected);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Session_Id", MODE_PRIVATE);
        String sessionId = sharedPreferences.getString("session_Id", "");
        Log.d(TAG, "startBroadcasterTimer: " + sessionId + "time" + pkTimeSelected);


        if (pkGuest && isBroadcaster) {

        }

        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<GenericResponse> call = apiClient.startPKChallenge(SessionId, broadcasterId, pkuserid, pkTimeSelected.toString());
            call.enqueue(new retrofit2.Callback<GenericResponse>() {
                @Override
                public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                    Log.d(TAG, "onResponse: " + response.raw().request().url());
                    utils.hideProgress();
                    GenericResponse genericResponse = response.body();
                    if (response.code() == 200) {
                        if (genericResponse != null) {
                            if (genericResponse.getStatus().equalsIgnoreCase("success")) {
                                sendPkMessage(" PK Challenge Has Started");
                                GiftUpdateAPI();
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
    }

    private void giftset(int over_all_gold, int over_all_gold1) {
        if (broadcasterAudience || isBroadcaster) {
            tvReceived.setText("" + over_all_gold);
        } else {
            tvReceived.setText("" + over_all_gold1);
        }
    }

    private void showPkTimeMessagePopup(String message) {
        String base64;
        String username;
        if (pkGuest) {
            base64 = broadcaster.getProfile_pic();
            username = broadcaster.getName();
        } else if (isBroadcaster) {
            base64 = broadcaster.getProfile_pic();
            username = broadcaster.getName();
        } else {
            base64 = broadcaster.getProfile_pic();
            username = broadcaster.getName();
        }
        try {
            String sender_image = URLDecoder.decode(base64, "UTF-8");
            Picasso.get().load(sender_image).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).transform(new TransformImgCircle()).into(msgSenderPicIv);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        msgSenderUserNameTv.setText(pkdisplayname);
        pkTimeMessageTv.setText(message);

        pkTimeRequestRl.setVisibility(View.VISIBLE);
        pkTimeRequestRl.setZ(200.0f);
        fabMenu.setZ(2.0f);
    }

    public void showBroadcastProfile(User broadcaster) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityLiveRoom.this);
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
        String totalViewersProgress = channelUserCount + "/" + broadcaster.getViewers_target();
        tvShareProgress.setText(totalShareProgress);
        tvGoldProgress.setText(totalGoldProgress);
        tvViewersProgress.setText(totalViewersProgress);

        Glide.with(getApplicationContext())
                .load(broadcaster.getTools_applied())
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
            Crashlytics.logException(e);
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

    public void sendPkMessage(String message) {
        MessageBean messageBean = new MessageBean(selfName, SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + message, true, false, false);
        messageBeanList.add(messageBean);
        adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
        rvMessages.scrollToPosition(messageBeanList.size() - 1);
        String shrink = SessionUser.getUser().getName().replaceAll(" ", "");
        sendChannelMessage(SessionUser.getUser().getUser_id() + level + shrink + " " + message);
    }


    private void onclickBroad(){
        Log.e(TAG, "onclickBroad: " );
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<GetTarget> call = apiClient.getTarget(broadcaster.getUser_id());
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
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityLiveRoom.this);
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