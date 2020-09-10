package com.blive.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.blive.BLiveApplication;
import com.blive.R;
import com.blive.chat.chatmodels.ChatUser;
import com.blive.chat.chatutil.ChatUtils;
import com.blive.constant.Constants_api;
import com.blive.model.AccountResponse;
import com.blive.model.User;
import com.blive.model.VersionResponse;
import com.blive.service.ApiClientBase;
import com.blive.service.ApiInterface;
import com.blive.session.SessionLogin;
import com.blive.session.SessionUser;
import com.blive.utils.Utils;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sans on 13-08-2018.
 **/

public class ActivitySignIn extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.bFb)
    ImageView bFb;
    @BindView(R.id.bGoogle)
    ImageView bGoogle;
    @BindView(R.id.bTwitter)
    ImageView bTwitter;
    @BindView(R.id.bInstagram)
    ImageView bInstagram;
    @BindView(R.id.rl)
    RelativeLayout root;

    boolean doubleBackToExitPressedOnce = false;
    private static final String TAG = ActivitySignIn.class.getSimpleName();
    private CallbackManager callbackManager;
    private static final String EMAIL = "email";
    private LoginButton loginButton;
    private AccessToken accessToken;
    private TwitterLoginButton bTwitterLogin;
    private String userName = "", email = "", image = "", domain = "", versionName = "";
    private static final int RC_SIGN_IN = 123;
    private long mLastClickTime = 0;
    int versionCode = 0;
    private GoogleSignInClient mGoogleSignInClient;
    private boolean isForceUpdate = false;
    AccountResponse accountResponse;
    private HashMap<String, String> userInfo = new HashMap<String, String>();
    private InstagramApp mApp;
    private ImageView btnConnect;
    private Button btnViewInfo;
    String ins_username, ins_email, ins_image;
    private InstagramSession mSession;
    private HashMap<String, String> userInfoHashmap = new HashMap<String, String>();
    private Button btnGetAllImages;
    private Button btnFollowers;
    String user_name;
    private Button btnFollwing;
    private LinearLayout llAfterLoginView;
    private ChatUtils helper;

    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == InstagramApp.WHAT_FINALIZE) {
                userInfoHashmap = mApp.getUserInfo();

            } else if (msg.what == InstagramApp.WHAT_FINALIZE) {
                Toast.makeText(ActivitySignIn.this, "Check your network.",
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new ChatUtils(this);
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(getResources().getString(R.string.com_twitter_sdk_android_CONSUMER_KEY), getResources().getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET)))
                .debug(true)//enable debug mode
                .build();
        Twitter.initialize(config);
        setContentView(R.layout.activity_signin);
        BLiveApplication.setCurrentActivity(this);
        mApp = new InstagramApp(this, ApplicationData.CLIENT_ID, ApplicationData.CLIENT_SECRET, ApplicationData.REDIRECT_URL);
        mApp.setListener(new InstagramApp.OAuthAuthenticationListener() {

            @Override
            public void onSuccess() {

                mApp.fetchUserName(handler);
                email = mApp.getTOken();
                ins_email = mApp.getTOken();

                ins_username = mApp.getUserName();
                userInfo = mApp.getUserInfo();
                image = userInfo.get(InstagramApp.TAG_PROFILE_PICTURE);
                userName = mApp.getUserName();
                if (userName != null && email != null) {
                    try {
                        domain = mApp.domain;
                        callAPICheckUserIns(email);
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                    }
                } else
                    showToast("Login Error Please Try Again !");


            }

            @Override
            public void onFail(String error) {
                Toast.makeText(ActivitySignIn.this, error, Toast.LENGTH_SHORT).show();
            }
        });
        setWidgetReference();
        bindEventHandlers();

    }

    private void bindEventHandlers() {
        btnConnect.setOnClickListener(this);
        btnViewInfo.setOnClickListener(this);
        btnGetAllImages.setOnClickListener(this);
        btnFollwing.setOnClickListener(this);
        btnFollowers.setOnClickListener(this);
    }

    private void setWidgetReference() {
        llAfterLoginView = (LinearLayout) findViewById(R.id.llAfterLoginView);
        btnConnect = (ImageView) findViewById(R.id.btnConnect);
        btnViewInfo = (Button) findViewById(R.id.btnViewInfo);
        btnGetAllImages = (Button) findViewById(R.id.btnGetAllImages);
        btnFollowers = (Button) findViewById(R.id.btnFollows);
        btnFollwing = (Button) findViewById(R.id.btnFollowing);
    }

    @Override
    protected void initUI() {
        try {
            Intent intent = getIntent();
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE;
            decorView.setSystemUiVisibility(uiOptions);

            mGoogleSignInClient = BLiveApplication.getInstance().getGoogleSignInClient();

            FacebookSdk.sdkInitialize(getApplicationContext());
            AppEventsLogger.activateApp(this);
            callbackManager = CallbackManager.Factory.create();

            loginButton = findViewById(R.id.login_button);
            loginButton.setReadPermissions(Arrays.asList(EMAIL));
            bTwitterLogin = findViewById(R.id.bTwitterLogin);

            if (isGoogleSignedIn()) {
                mGoogleSignInClient.signOut()
                        .addOnCompleteListener(this, task -> {

                        });
            }

            loginButton.setOnClickListener(v -> {
                if (!utils.isNetworkAvailable()) {
                    //Toast.makeText(getApplicationContext(),"No Internet connection!",Toast.LENGTH_SHORT).show();
                }
            });

            // Callback registration
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    accessToken = loginResult.getAccessToken();
                    GraphRequest data_request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(), (json_object, response) -> {
                                Log.e(TAG, "facebook: " + json_object);
                                String jsondata = json_object.toString();
                                String user_name = null, user_img = null, mail = null;
                                try {
                                    json_object = new JSONObject(jsondata);
                                    user_name = json_object.getString("name");
                                    userName = user_name;
                                    JSONObject picture = json_object.getJSONObject("picture");
                                    JSONObject data = picture.getJSONObject("data");
                                    user_img = data.getString("url");
                                    image = user_img;
                                    mail = json_object.getString("email");
                                    if (mail != null)
                                        email = mail;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                    Bundle permission_param = new Bundle();
                    permission_param.putString("fields", "id,name,email,picture.width(120).height(120)");
                    data_request.setParameters(permission_param);
                    data_request.executeAsync();

                    final Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        if (email.isEmpty())
                            email = accessToken.getUserId();

                        domain = "facebook";

                        if (userName != null && email != null) {
                            try {
                                callAPICheckUser(email);
                            } catch (Exception e) {
                                Crashlytics.logException(e);
                            }
                        } else
                            showToast("Login Error Please Try Again !");
                    }, 2000);
                }

                @Override
                public void onCancel() {
                    Log.e(TAG, "onCancel: ");
                }

                @Override
                public void onError(FacebookException exception) {
                    Log.e(TAG, "onError: " + exception);
                    showToast("Login failed Please try again later !");
                }
            });

            bTwitterLogin.setCallback(new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    handleTwitterSession(result.data);
                }

                @Override
                public void failure(TwitterException exception) {
                    showToast("Twitter Error Please try again later !");
                }
            });

            if (utils.isNetworkAvailable()) {
                bFb.setOnClickListener(v -> {
                    LoginManager.getInstance().logOut();
                    loginButton.performClick();
                });
            }

            bGoogle.setOnClickListener(v -> signInGoogle());

            bTwitter.setOnClickListener(v -> {
                if (utils.isNetworkAvailable()) {
                    bTwitterLogin.performClick();
                }
            });

            bInstagram.setOnClickListener(v -> {

            });


            if (intent != null) {
                String logout = intent.getStringExtra("logout");
                if (logout != null) {
                    if (logout.equalsIgnoreCase("yes")) {
                        Dialog alertDialog = new Dialog(BLiveApplication.getCurrentActivity());
                        alertDialog.setContentView(R.layout.alert_logout);
                        alertDialog.show();
                    }
                }
            }

            getVersionInfo();

            if (getApplicationContext().getPackageName().equalsIgnoreCase("com.blive")) {
                final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
                Call<VersionResponse> call = apiClient.getAppVersion();
                call.enqueue(new retrofit2.Callback<VersionResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<VersionResponse> call, @NonNull Response<VersionResponse> response) {
                        Log.e(TAG, "onResponse: " + response.toString());
                        if (response.code() == 200) {
                            VersionResponse versionResponse = response.body();
                            if (versionResponse != null) {
                                if (versionResponse.getStatus().equalsIgnoreCase("success")) {
                                    checkAppVersion(versionResponse.getData().getVersionName(), versionResponse.getData().getVersionCode(), versionResponse.getData().getUpdateType());
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<VersionResponse> call, @NonNull Throwable t) {

                    }
                });
            }

        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    private boolean isGoogleSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(this) != null;
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

    @OnClick(R.id.tv_agreement)
    public void onClickAgreement() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        if (utils.isNetworkAvailable()) {
            Intent intent = new Intent(mActivity, ActivityWebView.class);
            intent.putExtra("title", "Terms and Conditions");
            intent.putExtra("url", Constants_api.termsAndConditions);
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);
        if (isForceUpdate) {
            AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(ActivitySignIn.this);
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

    @OnClick(R.id.rlPhone)
    public void onClickPhone() {
        Intent intent = new Intent(mActivity, ActivityMobile.class);
        startActivity(intent);
        Objects.requireNonNull(this).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    @Override
    protected void deInitUI() {

    }

    private void signInGoogle() {
        if (utils.isNetworkAvailable()) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        bTwitterLogin.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with FireBase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                handleSignInResult(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                //Log.e(TAG, "Google sign in failed", e);
            }
        }
    }

    private void handleSignInResult(GoogleSignInAccount acct) {
        try {
            Uri profilePicUrl = acct.getPhotoUrl();
            if (profilePicUrl != null)
                image = profilePicUrl.toString();
        } catch (Exception e) {
            Log.e(TAG, "handleSignInResult: " + e);
        }
        userName = acct.getDisplayName();
        email = acct.getEmail();
        domain = "google";
        try {
            callAPICheckUser(email);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    private void handleTwitterSession(TwitterSession session) {
        //Log.e(TAG, "handleTwitterSession: " + session);
        TwitterAuthClient twitterAuthClient = new TwitterAuthClient();
        twitterAuthClient.requestEmail(session, new com.twitter.sdk.android.core.Callback<String>() {
            @Override
            public void success(Result<String> emailResult) {
                String mail = emailResult.data;
                email = mail;
                userName = session.getUserName();
                if (email.isEmpty())
                    email = userName;
                domain = "twitter";
                image = "https://twitter.com/" + session.getUserName() + "/profile_image?size=bigger";
                try {
                    callAPICheckUser(email);
                } catch (Exception e) {
                    Crashlytics.logException(e);
                }
            }

            @Override
            public void failure(TwitterException e) {
                showToast("Twitter Error Please try again later !");
            }
        });
    }

    private void callAPICheckUser(String email) {
        Utils utils = new Utils(this);
        String deviceId = utils.getDeviceId(this);
        final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
        Call<AccountResponse> call = apiClient.checkAccount(email, "", domain, deviceId);
        call.enqueue(new retrofit2.Callback<AccountResponse>() {
            @Override
            public void onResponse(@NonNull Call<AccountResponse> call, @NonNull Response<AccountResponse> response) {
                Log.i("autolog", "response: " + response.raw().request().url());
                Log.i("autolog", "response: " + response.body());
                if (response.code() == 200) {
                    accountResponse = response.body();
                    if (accountResponse != null) {
                        if (accountResponse.getStatus().equalsIgnoreCase("success")) {
                            if (accountResponse.getData().getMessage().equalsIgnoreCase("New user")) {
                                callNewUser();
                            } else if (accountResponse.getData().getMessage().equalsIgnoreCase("Already exsits")) {
                                // callOldUser(accountResponse.getData().getUser());
                                createUser(accountResponse.getData().getUser());
                            } else if (accountResponse.getData().getMessage().equalsIgnoreCase("Admin_BlocKEd")) {
                                Intent intent = new Intent(BLiveApplication.getCurrentActivity(), ActivityBlocked.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                BLiveApplication.getCurrentActivity().finish();
                                BLiveApplication.getCurrentActivity().startActivity(intent);
                            }
                        } else {
                            Log.e(TAG, "Block User " + response.message());
                            showToast(response.message());
                        }
                    } else {
                        utils.showToast(getString(R.string.server_error));
                    }
                } else {
                    checkResponseCode(response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccountResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Block Message : " + t.getMessage());
                showToast(t.getMessage());
            }
        });
    }

    private void callAPICheckUserIns(String email) {
        Utils utils = new Utils(this);
        String deviceId = utils.getDeviceId(this);
        domain = "instagram";
        final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
        Call<AccountResponse> call = apiClient.checkAccount(email, "", domain, deviceId);
        call.enqueue(new retrofit2.Callback<AccountResponse>() {
            @Override
            public void onResponse(@NonNull Call<AccountResponse> call, @NonNull Response<AccountResponse> response) {
                if (response.code() == 200) {
                    accountResponse = response.body();
                    if (accountResponse != null) {
                        if (accountResponse.getStatus().equalsIgnoreCase("success")) {
                            if (accountResponse.getData().getMessage().equalsIgnoreCase("New user")) {
                                callNewUserIns();
                            } else {
                                // callOldUser(accountResponse.getData().getUser());
                                createUser(accountResponse.getData().getUser());
                            }
                        } else {
                            Log.e(TAG, "Block User " + response.message());
                            showToast(response.message());
                        }
                    } else {
                        utils.showToast(getString(R.string.server_error));
                    }
                } else {
                    checkResponseCode(response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccountResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Block Message : " + t.getMessage());
                showToast(t.getMessage());
            }
        });
    }

    private void callNewUser() {
        Intent intent = new Intent(ActivitySignIn.this, ActivityNewUser.class);
        intent.putExtra("userName", userName);
        intent.putExtra("email", email);
        intent.putExtra("domain", domain);
        intent.putExtra("image", image);
        utils.hideProgress();
        startActivity(intent);
    }

    private void callNewUserIns() {
        Intent intent = new Intent(ActivitySignIn.this, ActivityNewUser.class);
        intent.putExtra("userName", ins_username);
        intent.putExtra("email", ins_email);
        intent.putExtra("domain", domain);
        intent.putExtra("image", image);
        utils.hideProgress();
        startActivity(intent);
    }

    private void callOldUser(User user) {
        /*if (user.getIs_this_user_blocked().equalsIgnoreCase("YES")) {
            finishAffinity();
            changeActivity(ActivityBlocked.class);
        } else {*/
        Answers.getInstance().logLogin(new LoginEvent()
                .putMethod(user.getLogin_domain())
                .putSuccess(true));
        SessionUser.saveUser(user);
        SessionLogin.saveLoginSession();
        utils.hideProgress();
        finishAffinity();
        Intent intent = new Intent(ActivitySignIn.this, ActivityHome.class);
        intent.putExtra("from", "splash");
        startActivity(intent);
    }


    private void createUser(final User newUser) {
        ChatUser chatUser = new ChatUser(newUser.getUser_id(), newUser.getUser_id(), (newUser.getProfile_pic() != null ? newUser.getProfile_pic() : ""),
                newUser.getName(), System.currentTimeMillis());
        BLiveApplication.getUserRef().child(newUser.getUser_id()).setValue(chatUser).addOnSuccessListener(aVoid -> {
            helper.setLoggedInUser(chatUser);
            utils.hideProgress();
            callOldUser(newUser);

        }).addOnFailureListener(e ->
                Toast.makeText(ActivitySignIn.this, "Something went wrong, unable to create user.", Toast.LENGTH_LONG).show());
    }
    /* }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        utils.deleteCache(this);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    public void checkAppVersion(String mVersionName, String mVersionCode, String type) {
        Log.e(TAG, "checkAppVersion: " + mVersionName + " " + mVersionCode);
        if (!versionName.equalsIgnoreCase(mVersionName) && versionCode != Integer.valueOf(mVersionCode)) {
            switch (type) {
                case "Soft":
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivitySignIn.this);
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
                    AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(ActivitySignIn.this);
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
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnConnect) {
            if (utils.isNetworkAvailable()) {

                connectOrDisconnectUser();
            }
        }
    }

    private void connectOrDisconnectUser() {
        if (mApp.hasAccessToken()) {
            // callNewUserIns();

            // changeActivity(ActivityHome.class);

//
           /*Intent intent = new Intent(ActivitySignIn.this, ActivitySkip.class);
           intent.putExtra("from", "splash");
           startActivity(intent);*/
            final AlertDialog.Builder builder = new AlertDialog.Builder(ActivitySignIn.this);
            builder.setMessage("connect from Instagram?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    mApp.resetAccessToken();
                                    mApp.authorize();
                                    // btnConnect.setVisibility(View.VISIBLE);
                                    // llAfterLoginView.setVisibility(View.GONE);
                                    // btnConnect.setText("Connect");
                                    // tvSummary.setText("Not connected");
                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });
            final AlertDialog alert = builder.create();
            alert.show();

        } else {
            mApp.authorize();
        }
    }

    private void displayInfoDialogView() {

//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
//                ActivitySignIn.this);
//        alertDialog.setTitle("Profile Info");
//
//        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.profile_view, null);
//        alertDialog.setView(view);
//        ImageView ivProfile = (ImageView) view
//                .findViewById(R.id.ivProfileImage);
//        TextView tvName = (TextView) view.findViewById(R.id.tvUserName);
//        TextView tvNoOfFollwers = (TextView) view
//                .findViewById(R.id.tvNoOfFollowers);
//        TextView tvNoOfFollowing = (TextView) view.findViewById(R.id.tvNoOfFollowing);
//        new ImageLoader(ActivitySignIn.this).DisplayImage(
//                userInfoHashmap.get(InstagramApp.TAG_PROFILE_PICTURE),
//                ivProfile);
//        tvName.setText(userInfoHashmap.get(InstagramApp.TAG_USERNAME));
//        tvNoOfFollowing.setText(userInfoHashmap.get(InstagramApp.TAG_FOLLOWS));
//        tvNoOfFollwers.setText(userInfoHashmap
//                .get(InstagramApp.TAG_FOLLOWED_BY));
//        alertDialog.create().show();
    }
}