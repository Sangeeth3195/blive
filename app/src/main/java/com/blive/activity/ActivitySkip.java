package com.blive.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blive.BLiveApplication;
import com.blive.R;
import com.blive.utils.Utils;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.UpdateAvailability;

import java.util.concurrent.TimeUnit;
import butterknife.ButterKnife;

import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;

/**
 * Created by sans on 18-08-2018.
 **/

public class ActivitySkip extends BaseActivity {

    private WebView wv;
    private RelativeLayout rlWv;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout rlLoading;
    private RelativeLayout rlError;
    private TextView tvCounter;
    private CountDownTimer countDownTimer;
    private String urlNew = "", url = "", from = "", errorUrl = "", TAG = "ActivitySkip";
    private Utils utils;
    private boolean isSkipEnable = false;
    AppUpdateManager appUpdateManager;
    private int MY_REQUEST_CODE= 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skip);
        ButterKnife.bind(this);
        BLiveApplication.setCurrentActivity(this);
        utils = new Utils(this);
        rlLoading =  findViewById(R.id.rl_loadingSkip);
        swipeRefreshLayout = findViewById(R.id.swipeRefresSkip);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        rlWv = findViewById(R.id.rl_wv);
        wv = findViewById(R.id.wv);
        rlError = findViewById(R.id.rl_error);
        tvCounter = findViewById(R.id.tv_counter);

        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        from = intent.getStringExtra("from");
        initUI();
        changeStatusBarColor();

        // Creates instance of the manager.
         appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
// Before starting an update, register a listener for updates.
        appUpdateManager.registerListener(listener);

    }

    @SuppressLint("SetJavaScriptEnabled")
    public void initUI() {
        skipTime();
        swipeRefreshLayout.setVisibility(View.GONE);
        rlWv.setVisibility(View.GONE);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setVisibility(View.GONE);
            rlWv.setVisibility(View.GONE);
            rlLoading.setVisibility(View.VISIBLE);
            wv.loadUrl(urlNew);
        });

        if (utils.isNetworkAvailable()) {
            wv.loadUrl(url);
            wv.getSettings().setSupportMultipleWindows(true);
            wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            wv.getSettings().setJavaScriptEnabled(true);
            wv.getSettings().setDomStorageEnabled(true);
            wv.getSettings().setLoadWithOverviewMode(true);
            wv.getSettings().setUseWideViewPort(true);
            wv.getSettings().setDomStorageEnabled(true);
            wv.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            wv.getSettings().setDefaultTextEncodingName("utf-8");
            wv.getSettings().setPluginState(WebSettings.PluginState.ON);
            wv.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    Log.e(TAG, "onPageFinished: " + url);
                    if (!url.equalsIgnoreCase(errorUrl)) {
                        rlError.setVisibility(View.GONE);
                        rlLoading.setVisibility(View.GONE);
                        swipeRefreshLayout.setVisibility(View.VISIBLE);
                        rlWv.setVisibility(View.VISIBLE);
                        wv.setVisibility(View.VISIBLE);
                        if (swipeRefreshLayout.isRefreshing())
                            swipeRefreshLayout.setRefreshing(false);
                        swipeRefreshLayout.setVisibility(View.VISIBLE);
                        rlWv.setVisibility(View.VISIBLE);
                        urlNew = view.getUrl();
                    }

                    skipTime();
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    }
                    errorUrl = request.getUrl().toString();
                    rlError.setVisibility(View.VISIBLE);
                    rlLoading.setVisibility(View.GONE);
                    wv.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.GONE);
                    rlWv.setVisibility(View.GONE);
                }

                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(ActivitySkip.this);
                    builder.setMessage(R.string.notification_error_ssl_cert_invalid);
                    builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            handler.proceed();
                        }
                    });
                    builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            handler.cancel();
                        }
                    });
                    final AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }
    }


    // Create a listener to track request state updates.
    InstallStateUpdatedListener listener = state -> {
        // Show module progress, log state, or install the update.
    };


    @Override
    protected void deInitUI() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    public void onClickRefresh(View view) {
        rlLoading.setVisibility(View.VISIBLE);
        rlError.setVisibility(View.GONE);
        wv.loadUrl(url);
    }

    private void skipTime() {
        countDownTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long millis = millisUntilFinished;
                String hms = String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                hms = hms.substring(1);
                tvCounter.setText("Skip " +" "+hms);
            }

            @Override
            public void onFinish() {
                if(!isSkipEnable){
                    isSkipEnable = true;
                    tvCounter.setText("Skip");
                    changeActivity(ActivityHome.class);
                }
            }
        }.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Log.e(TAG, "Update flow failed! Result code: "+ resultCode );
                // If the update is cancelled or fails,
                // you can request to start the update again.
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(
                        appUpdateInfo -> {
                          /*  if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                                    // For a flexible update, use AppUpdateType.FLEXIBLE
                                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                                // Request the update.*/

                                if (appUpdateInfo.updateAvailability()
                                        == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                    // If an in-app update is already running, resume the update.
                                    try {
                                        appUpdateManager.startUpdateFlowForResult(
                                                appUpdateInfo,
                                                IMMEDIATE,
                                                this,
                                                MY_REQUEST_CODE);
                                    } catch (IntentSender.SendIntentException e) {
                                        e.printStackTrace();
                                    }
                                }
                         //   }


                        });
    }


}