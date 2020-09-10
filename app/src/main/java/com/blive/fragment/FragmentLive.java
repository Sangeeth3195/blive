package com.blive.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.http.SslError;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blive.BLiveApplication;
import com.blive.activity.ActivityHome;
import com.blive.activity.ActivityWebView;
import com.blive.adapter.AdapterBanner;
import com.blive.adapter.AdapterUsers;
import com.blive.agora.rtmChat.ChatHandler;
import com.blive.agora.rtmChat.ChatManager;
import com.blive.constant.Constants_api;
import com.blive.model.ActiveUserResponse;
import com.blive.model.GenericResponse;
import com.blive.model.ProfileResponse;
import com.blive.model.User;
import com.blive.R;
import com.blive.model.UsersResponse;
import com.blive.service.ApiClientBase;
import com.blive.service.ApiInterface;
import com.blive.session.SessionManager;
import com.blive.session.SessionUser;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmClient;
import retrofit2.Call;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class FragmentLive extends BaseFragment implements AdapterUsers.ListenerChannel, AdapterBanner.Listener {

    @BindView(R.id.rv_users)
    RecyclerView rvUsers;
    @BindView(R.id.tv_no_users)
    TextView tvNoUsers;
    @BindView(R.id.rl_loading)
    RelativeLayout rlLoad;
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.iv_bad_internet)
    ImageView ivBadInternet;

    AlertDialog alertDialog;
    private SwipeRefreshLayout swipeRefreshLive;
    String regId, versionName, isDomain, isValue;
    private TextView tvDiamondCredit;
    private User user;
    private ArrayList<User> users, tempUser;
    private int position = -1, page = 1, lastPage = 1;
    private boolean isSolo = false, isGroup = false, isScreenShare = false, isKaraoke = false, isAudio = false, isRefreshing = false;
    String countryName = "";
    private AdapterUsers adapterUsers;
    private boolean isUserListEnd = false, isAPICalled = false, isWebError = false, isUserClicked = false;
    GridLayoutManager layoutManager;
    LinearLayoutManager linearLayoutManager;
    private RtmClient mRtmClient;
    private ChatManager mChatManager;
    private ChatHandler mChatHandler;
    private RelativeLayout rlDiamond;
    ProgressDialog progressBar;
    FusedLocationProviderClient mFusedLocationClient;
    private static final int PERMISSION_ID = 44;
    SessionManager sessionManager;
    String lat = "", lon = "";


    public static String clicks_resume = "";
    private String latlngresume="0";

    public FragmentLive() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            latlngresume="1";
            getLastLocation();
        }

           /* getActiveUsers(1, "all");
            adapterUsers.notifyDataSetChanged();*/

    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initUI() {
        getLastLocation();
        displayFireBaseRegId();

        progressBar = new ProgressDialog(getActivity());
        progressBar.setCancelable(false);
        progressBar.setMessage("Loading Live");
        mChatManager = BLiveApplication.getInstance().getChatManager();
        mRtmClient = mChatManager.getRtmClient();

        AdapterBanner adapterBanner = new AdapterBanner(mActivity);
        adapterBanner.setOnClickListener(this);
        webView.loadUrl(Constants_api.index1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        webView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // do your handling codes here, which url is the requested url
                // probably you need to open that url rather than redirect:
                if (!url.equalsIgnoreCase(Constants_api.index1)) {
                    Intent intent = new Intent(mActivity, ActivityWebView.class);
                    intent.putExtra("title", "BLive");
                    intent.putExtra("from", "home");
                    intent.putExtra("url", url);
                    startActivity(intent);
                    /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);*/
                } else {

                }
                return true; // then it is not handled by default action
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.e(TAG, "onPageStarted: " + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                isWebError = false;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                webView.setVisibility(View.GONE);
                isWebError = true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.notification_error_ssl_cert_invalid);
                builder.setPositiveButton("continue", (dialog, which) -> handler.proceed());
                builder.setNegativeButton("cancel", (dialog, which) -> handler.cancel());
                final AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        countryName = SessionUser.getUser().getCountry();

        getVersionInfo();

        users = new ArrayList<>();
        tempUser = new ArrayList<>();
        swipeRefreshLive = getActivity().findViewById(R.id.swipeRefreshLive);
        rlDiamond = getActivity().findViewById(R.id.rl_diamond);
        tvDiamondCredit = getActivity().findViewById(R.id.tv_diamondCredit);
        swipeRefreshLive.setColorSchemeResources(R.color.colorAccent);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Mobile_Domain", MODE_PRIVATE);
        isDomain = sharedPreferences.getString("isDomain", "");
        isValue = sharedPreferences.getString("isValue", "");

        Log.e(TAG, "initUI: " + isDomain);
        Log.e(TAG, "initUI: " + isValue);

        if (isDomain.equals("mobile") && isValue.equals("0")) {
            rlDiamond.setVisibility(View.VISIBLE);
            tvDiamondCredit.setText("You have got 10 Diamonds!");
            sharedPreferences = getApplicationContext().getSharedPreferences("Mobile_Domain", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("isValue", "1");
            editor.commit();
        } else {
            rlDiamond.setVisibility(View.GONE);
        }

        layoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        linearLayoutManager = new LinearLayoutManager(mActivity);
        rvUsers.setLayoutManager(layoutManager);
        rvUsers.setNestedScrollingEnabled(false);
        tempUser.add(null);

        adapterUsers = new AdapterUsers(mActivity, users);
        adapterUsers.setOnClickListener(this);
        rvUsers.setAdapter(adapterUsers);
        rvUsers.setVisibility(View.VISIBLE);

        rvUsers.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (!isRefreshing) {
                if (!isUserListEnd) {
                    if (!isAPICalled) {
                        if (isLastItemDisplaying()) {
                            if (page < lastPage) {
                                page = page + 1;
                                if (isSolo)
                                    getActiveUsers(page, "solo");
                                else if (isGroup)
                                    getActiveUsers(page, "group");
                                else if (isScreenShare)
                                    getActiveUsers(page, "screenShare");
                                else if (isKaraoke)
                                    getActiveUsers(page, "karaoke");
                                else if (isAudio)
                                    getActiveUsers(page, "audio");
                                else
                                    getActiveUsers(page, "all");
                            }
                        }
                    }
                }
            }
        });

        swipeRefreshLive.setOnRefreshListener(() -> {
            page = 1;
            isRefreshing = true;
            isUserListEnd = false;

            getProfileData();

            Log.e(TAG, "initUI: " + " Swipe Refresh New ");

            if (isSolo)
                getActiveUsers(page, "solo");
            else if (isGroup)
                getActiveUsers(page, "group");
            else if (isScreenShare)
                getActiveUsers(page, "screenShare");
            else if (isKaraoke)
                getActiveUsers(page, "karaoke");
            else if (isAudio)
                getActiveUsers(page, "audio");
            else
                getActiveUsers(page, "all");

            swipeRefreshLive.setRefreshing(false);
        });

        getProfileData();

        getActiveUsers(page, "all");

        callDeviceUpdateAPI();
    }

    private void getProfileData() {
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<ProfileResponse> call = apiClient.getProfile(SessionUser.getUser().getUser_id(), SessionUser.getUser().getUser_id());
            call.enqueue(new retrofit2.Callback<ProfileResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                    ProfileResponse profileResponse = response.body();
                    if (response.code() == 200) {
                        if (profileResponse != null) {
                            if (profileResponse.getStatus().equalsIgnoreCase("success")) {
                                Log.e(TAG, "onResponse: " + profileResponse.getData().getUser().toString());
                                SessionUser.saveUser(profileResponse.getData().getUser());
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

    private void callDeviceUpdateAPI() {
        if (utils.isNetworkAvailable()) {
            String deviceId = utils.getDeviceId(mActivity);
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<GenericResponse> call = apiClient.sendDeviceDetails(SessionUser.getUser().getUser_id(), deviceId, "android", versionName);
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

    private void getVersionInfo() {
        try {
            PackageInfo packageInfo = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void getActiveUsers(int page, String broadcastType) {
        if (isWebError) {
            webView.loadUrl(Constants_api.index1);
        }
        if (utils.isNetworkAvailable()) {
            if (!isAPICalled) {
                isAPICalled = true;
                if (page > 1) {
                    adapterUsers.update(tempUser);
                } else
                    swipeRefreshLive.setRefreshing(true);

                String deviceId = utils.getDeviceId(getApplicationContext());
                final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
                Call<ActiveUserResponse> call = apiClient.getActiveUsers(regId, countryName, String.valueOf(page), broadcastType, String.valueOf(SessionUser.getUser().getId()), SessionUser.getUser().getUser_id(), deviceId, sessionManager.getSessionStringValue("location","lat"), sessionManager.getSessionStringValue("location","lon"));
                call.enqueue(new retrofit2.Callback<ActiveUserResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ActiveUserResponse> call, @NonNull Response<ActiveUserResponse> response) {
                        Log.i("autolog", "response: " + response.raw().request().url());
                        ActiveUserResponse activeUserResponse = response.body();
                        if (progressBar.isShowing()) {
                            progressBar.dismiss();
                        }
                        if (swipeRefreshLive.isRefreshing()) {
                            swipeRefreshLive.setRefreshing(false);
                        }
                        if (response.code() == 200) {
                            if (activeUserResponse != null) {
                                if (activeUserResponse.getStatus().equalsIgnoreCase("success")) {
                                    lastPage = activeUserResponse.getData().getLast_page();
                                    isAPICalled = false;
                                    isRefreshing = false;
                                    if (page == 1) {
                                        if (activeUserResponse.getData().getActiveUsers().size() > 0) {
                                            rvUsers.setVisibility(View.VISIBLE);
                                            tvNoUsers.setVisibility(View.GONE);
                                            adapterUsers.refresh(activeUserResponse.getData().getActiveUsers());
                                        } else {
                                            rvUsers.setVisibility(View.GONE);
                                            tvNoUsers.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        adapterUsers.removeLastItem();
                                        if (page == lastPage)
                                            isUserListEnd = true;
                                        adapterUsers.update(activeUserResponse.getData().getActiveUsers());
                                    }
                                } else {
                                    if (page == 1)
                                        isAPICalled = false;
                                    isRefreshing = false;
                                    rvUsers.setVisibility(View.GONE);
                                    tvNoUsers.setVisibility(View.VISIBLE);
                                }
                            } else {
                                if (page == 1)
                                    isAPICalled = false;
                                isRefreshing = false;
                                rvUsers.setVisibility(View.GONE);
                                tvNoUsers.setVisibility(View.VISIBLE);
                                utils.showToast(getString(R.string.server_error));
                            }
                            String checkedIn = SessionUser.getUser().getIs_this_user_checked_in();
                            Log.e(TAG, "CHECK IN: " + SessionUser.getUser().getIs_this_user_checked_in());
                            if (checkedIn != null && !checkedIn.isEmpty() && !checkedIn.equals("null")) {
                                if (checkedIn.equals("no")) {
                                    try {
                                        displayPopUp();

                                    } catch (Exception e) {
                                        Log.i("autolog", "e: " + e);
                                        Crashlytics.log(e.toString());
                                    }
                                }
                            }
                        } else {
                            checkResponseCode(response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ActiveUserResponse> call, @NonNull Throwable t) {
                        isRefreshing = false;
                        isAPICalled = false;
                        swipeRefreshLive.setRefreshing(false);
                        rvUsers.setVisibility(View.GONE);
                        tvNoUsers.setVisibility(View.VISIBLE);
                        showToast(t.getMessage());
                        if (progressBar.isShowing()) {
                            progressBar.dismiss();
                        }
                    }
                });
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        sessionManager=new SessionManager(getActivity());
        getLastLocation();
        return inflater.inflate(R.layout.fragment_live, container, false);

    }

    private boolean isLastItemDisplaying() {
        if (adapterUsers != null) {
            if (Objects.requireNonNull(rvUsers.getAdapter()).getItemCount() != 0) {
                int lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                return lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == rvUsers.getAdapter().getItemCount() - 11;
            }
            return false;
        }
        return false;
    }

    @Override
    public void OnClicked(int mPosition, User mUser) {
        /*progressBar.show();*/
        Log.e(TAG, "OnClicked: times");
        if (!isUserClicked) {
            isUserClicked = true;
            Log.e(TAG, "OnClicked: times one");
//              utils.showProgress();
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<UsersResponse> call = apiClient.getBroadcastType(mUser.getUser_id());
            call.enqueue(new retrofit2.Callback<UsersResponse>() {
                @Override
                public void onResponse(@NonNull Call<UsersResponse> call, @NonNull Response<UsersResponse> response) {
//                     utils.hideProgress();
                    /*progressBar.dismiss();*/
                    isUserClicked = false;
                    UsersResponse usersResponse = response.body();
                    if (response.code() == 200) {
                        if (usersResponse != null) {
                            if (usersResponse.getStatus().equalsIgnoreCase("success")) {
                                if (!usersResponse.getData().getBroadcastType().equalsIgnoreCase(mUser.getBroadcast_type()))
                                    mUser.setBroadcast_type(usersResponse.getData().getBroadcastType());

                                if (mUser.getBroadcast_type().equalsIgnoreCase("pk")) {
                                    if (usersResponse.getData().getGuest_details().getChallenge_time_left().length() != 0) {
                                        mUser.setPkTimeLeft(Integer.parseInt(usersResponse.getData().getGuest_details().getChallenge_time_left()));
                                        mUser.setPk_broadcaster_id(usersResponse.getData().getGuest_details().getPk_broadcaster_id());
                                        mUser.setPk_guest_id(usersResponse.getData().getGuest_details().getPk_guest_id());
                                        Log.i("autolog", "usersResponse: " + usersResponse.getData().getGuest_details().getPk_guest_id() + usersResponse.getData().getGuest_details().getPk_broadcaster_id());
                                    }
                                }

                                if (!mUser.getBroadcast_type().isEmpty()) {
                                    user = mUser;
                                    position = mPosition;
                                    ((ActivityHome) Objects.requireNonNull(getActivity())).getUser(user, users, position);
                                } else
                                    showToast("User is Offline Now !");
                            } else {
                                showToast("User is Offline Now !");
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
//                      utils.hideProgress();
                    showToast(t.getMessage());
                    isUserClicked = false;
                }
            });
        }
    }

    public void moveNotificationUserToLive(User userData) {

//        ArrayList<User> newAUserList = new ArrayList<>();
//        newAUserList.add(userData);
//        ((ActivityHome) Objects.requireNonNull(getActivity())).getUser(userData, newAUserList, 0);

        Log.e(TAG, "OnClicked: times one");
//              utils.showProgress();
        final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
        Call<UsersResponse> call = apiClient.getBroadcastType(userData.getUser_id());
        call.enqueue(new retrofit2.Callback<UsersResponse>() {
            @Override
            public void onResponse(@NonNull Call<UsersResponse> call, @NonNull Response<UsersResponse> response) {
//                     utils.hideProgress();
                /*progressBar.dismiss();*/

                UsersResponse usersResponse = response.body();
                if (response.code() == 200) {
                    if (usersResponse != null) {
                        if (usersResponse.getStatus().equalsIgnoreCase("success")) {
                            if (!usersResponse.getData().getBroadcastType().equalsIgnoreCase(userData.getBroadcast_type()))
                                userData.setBroadcast_type(usersResponse.getData().getBroadcastType());

                            if (userData.getBroadcast_type().equalsIgnoreCase("pk")) {
                                if (usersResponse.getData().getGuest_details().getChallenge_time_left().length() != 0) {
                                    userData.setPkTimeLeft(Integer.parseInt(usersResponse.getData().getGuest_details().getChallenge_time_left()));
                                    userData.setPk_broadcaster_id(usersResponse.getData().getGuest_details().getPk_broadcaster_id());
                                    userData.setPk_guest_id(usersResponse.getData().getGuest_details().getPk_guest_id());
                                    userData.setPk_channelname(usersResponse.getData().getGuest_details().getPk_channelname());

                                    Log.i("autolog", "usersResponse: " + usersResponse.getData().getGuest_details().getPk_guest_id() + usersResponse.getData().getGuest_details().getPk_broadcaster_id());
                                }
                            }

                            if (!userData.getBroadcast_type().isEmpty()) {
                                user = userData;
                                position = 0;
                                ((ActivityHome) Objects.requireNonNull(getActivity())).getUser(user, users, position);
                            } else
                                showToast("User is Offline Now !");
                        } else {
                            showToast("User is Offline Now !");
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
//                      utils.hideProgress();
                showToast(t.getMessage());
            }
        });
    }

    private void displayFireBaseRegId() {
        regId = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "displayFireBaseRegId: " + regId);
    }

    @Override
    public void OnClickedBanner(int position) {

    }

    public void displayPopUp() {

        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        final View dialogView = layoutInflater.inflate(R.layout.pop_up_check_in, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        final ImageView imageView = dialogView.findViewById(R.id.giftbox);

        alertDialogBuilder.setView(dialogView);
        alertDialog = alertDialogBuilder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        final Button bCheckIn = dialogView.findViewById(R.id.gift_check_in);
        bCheckIn.setOnClickListener(v -> checkInAPI());
    }

    public void displayPopUp1() {

        String urlNew = "", url = "", from = "", errorUrl = "", htmlData = "";


        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        final View dialogView = layoutInflater.inflate(R.layout.pop_up_game, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        final WebView wv = dialogView.findViewById(R.id.game_webview);
        final ImageView close_web = dialogView.findViewById(R.id.close_web);
        wv.setBackgroundColor(Color.TRANSPARENT);


        alertDialogBuilder.setView(dialogView);
        alertDialog = alertDialogBuilder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        close_web.setOnClickListener(v -> alertDialog.dismiss());

        String url1 = Constants_api.Daily_spin + SessionUser.getUser().getUser_id();
        Log.i("autolog", "url1: " + url1);

        if (utils.isNetworkAvailable()) {
            wv.loadUrl(url1);
            Log.i("autolog", "url1: " + url1);
            wv.getSettings().setSupportMultipleWindows(true);
            wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            wv.getSettings().setJavaScriptEnabled(true);
            wv.getSettings().setDomStorageEnabled(true);
            wv.getSettings().setDefaultTextEncodingName("utf-8");
            wv.getSettings().setLoadWithOverviewMode(true);
            wv.getSettings().setUseWideViewPort(true);
            wv.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            wv.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

            wv.getSettings().setPluginState(WebSettings.PluginState.ON);

            WebChromeClient client = new WebChromeClient();
            wv.setWebChromeClient(client);


            wv.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageStarted(WebView view, String url1, Bitmap favicon) {
                }

                @Override
                public void onPageFinished(WebView view, String url1) {
                    super.onPageFinished(view, url1);
//                    if (!url1.equalsIgnoreCase(errorUrl)) {
//                        rlError.setVisibility(View.GONE);
//                        rlLoading.setVisibility(View.GONE);
//                        swipeRefreshLayout.setVisibility(View.VISIBLE);
//                        rlWv.setVisibility(View.VISIBLE);
//                        wv.setVisibility(View.VISIBLE);
//                        if (swipeRefreshLayout.isRefreshing())
//                            swipeRefreshLayout.setRefreshing(false);
//                        swipeRefreshLayout.setVisibility(View.VISIBLE);
//                        rlWv.setVisibility(View.VISIBLE);
//                        url1New = view.getUrl();
//                    }
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    Log.i("WebViewerror", "error: " + error);
//                    errorUrl = request.getUrl().toString();
//                    rlError.setVisibility(View.VISIBLE);
//                    rlLoading.setVisibility(View.GONE);
                    wv.setVisibility(View.GONE);
//                    swipeRefreshLayout.setVisibility(View.GONE);
//                    rlWv.setVisibility(View.GONE);
                }

                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    Log.i("autolog", "error: " + error);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(R.string.notification_error_ssl_cert_invalid);
                    builder.setPositiveButton("continue", (dialog, which) -> handler.proceed());
                    builder.setNegativeButton("cancel", (dialog, which) -> handler.cancel());
                    final AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        } else {
            showToast("There is No InterNet");
        }


    }

    private void checkInAPI() {
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<GenericResponse> call = apiClient.checkIn(SessionUser.getUser().getUser_id());
            call.enqueue(new retrofit2.Callback<GenericResponse>() {
                @Override
                public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                    GenericResponse genericResponse = response.body();
                    if (response.code() == 200) {
                        if (genericResponse != null) {
                            if (genericResponse.getStatus().equalsIgnoreCase("success")) {
                                showToast(genericResponse.getData().getMessage());
                                alertDialog.dismiss();
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
                                    sessionManager.storeSessionStringvalue("location","lat",lat);
                                    sessionManager.storeSessionStringvalue("location","lon",lon);
                                    Log.i("autolog", "lat: " + lat);
                                    lon = String.valueOf(task.getResult().getLongitude());
                                    Log.i("autolog", "lon: " + lon);
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(getActivity(), "Turn on location", Toast.LENGTH_LONG).show();
/*
                if (sessionManager.getSessionStringValue("location","lat").equalsIgnoreCase("0")){
                    latlngresume="1";
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
*/
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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
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
            Log.i("autolog", "lat: " + lat);
            lon = String.valueOf(mLastLocation.getLongitude());
            Log.i("autolog", "lon: " + lon);
            sessionManager.storeSessionStringvalue("location","lat",lat);
            sessionManager.storeSessionStringvalue("location","lon",lon);

        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                getActivity(),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }
}