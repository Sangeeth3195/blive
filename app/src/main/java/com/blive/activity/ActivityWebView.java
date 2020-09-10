package com.blive.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.blive.BLiveApplication;
import com.blive.constant.Constants_api;
import com.blive.model.ProfileResponse;
import com.blive.R;
import com.blive.service.ApiClientBase;
import com.blive.service.ApiInterface;
import com.blive.session.SessionLogin;
import com.blive.session.SessionUser;

import butterknife.BindView;
import butterknife.OnClick;
import im.delight.android.webview.AdvancedWebView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sans on 18-08-2018.
 **/

public class ActivityWebView extends BaseBackActivity {

    @BindView(R.id.wv)
    WebView wv;
    @BindView(R.id.rl_wv)
    RelativeLayout rlWv;
    @BindView(R.id.swipeRefreshWeb)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rl_loading)
    RelativeLayout rlLoading;
    @BindView(R.id.rl_error)
    RelativeLayout rlError;

    private String urlNew = "", url = "", from = "", errorUrl = "",htmlData="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        BLiveApplication.setCurrentActivity(this);
        initUI();
        changeStatusBarColor();
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void initUI() {


        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        url = intent.getStringExtra("url");
        from = intent.getStringExtra("from");

        setTitle(title);
        if (title.equalsIgnoreCase("Terms and Conditions")) {
            hideBack();
        }else if (title.equalsIgnoreCase("BLive")) {
            hideBack();
            showSkip();
        }else if (title.equalsIgnoreCase("Games")){
            swipeRefreshLayout.setEnabled(false);
        }
        rlLoading.setVisibility(View.VISIBLE);
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
            Log.i("autolog", "url: " + url);
            wv.getSettings().setSupportMultipleWindows(true);
            wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            wv.getSettings().setJavaScriptEnabled(true);
            wv.setWebViewClient(new WebViewClient());
            wv.getSettings().setDomStorageEnabled(true);
            wv.getSettings().setDefaultTextEncodingName("utf-8");
            wv.getSettings().setLoadWithOverviewMode(true);
            wv.getSettings().setUseWideViewPort(true);
            wv.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            wv.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            /*wv.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);*/

            if (title.equalsIgnoreCase("Level"))
                /*wv.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);*/

            wv.getSettings().setPluginState(WebSettings.PluginState.ON);

            WebChromeClient client = new WebChromeClient();
            wv.setWebChromeClient(client);


            wv.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    Log.i("autolog", "url: " + url);
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
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    Log.i("WebViewerror", "error: " + error);
                    errorUrl = request.getUrl().toString();
                    rlError.setVisibility(View.VISIBLE);
                    rlLoading.setVisibility(View.GONE);
                    wv.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.GONE);
                    rlWv.setVisibility(View.GONE);
                }

                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    Log.i("autolog", "error: " + error);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(ActivityWebView.this);
                    builder.setMessage(R.string.notification_error_ssl_cert_invalid);
                    builder.setPositiveButton("continue", (dialog, which) -> handler.proceed());
                    builder.setNegativeButton("cancel", (dialog, which) -> handler.cancel());
                    final AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }
    }

    @OnClick(R.id.rl_back)
    public void onBackClicked() {
        if (SessionLogin.getLoginSession()) {
            String walletUrl = Constants_api.wallet + SessionUser.getUser().getUser_id();
            if (from != null) {
                if (from.equalsIgnoreCase("liveRoom")) {
                    if (utils.isNetworkAvailable()) {
                        callProfileAPI();
                    }
                } else {
                    if (urlNew.equalsIgnoreCase(url) || urlNew.isEmpty())
                        finish();
                    else {
                        rlLoading.setVisibility(View.VISIBLE);
                        wv.setVisibility(View.GONE);
                        swipeRefreshLayout.setVisibility(View.GONE);
                        rlWv.setVisibility(View.GONE);
                        wv.loadUrl(url);
                    }
                }
            } else {
                if (urlNew.equalsIgnoreCase(url) || urlNew.isEmpty())
                    finish();
                else {
                    rlLoading.setVisibility(View.VISIBLE);
                    wv.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.GONE);
                    rlWv.setVisibility(View.GONE);
                    wv.loadUrl(url);
                }
            }
        } else {
            if (urlNew.equalsIgnoreCase(url) || urlNew.isEmpty())
                finish();
            else {
                wv.loadUrl(url);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (SessionLogin.getLoginSession()) {
            String walletUrl = Constants_api.wallet + SessionUser.getUser().getUser_id();
            Log.e(TAG, "onBackPressed: " + urlNew);
            if (urlNew.contains("wallet")) {
                if (utils.isNetworkAvailable()) {
                    callProfileAPI();
                }
            } else {
                if (urlNew.equalsIgnoreCase(url) || urlNew.isEmpty())
                    finish();
                else {
                    wv.loadUrl(url);
                }
            }
        } else {
            if (urlNew.equalsIgnoreCase(url))
                finish();
            else {
                wv.loadUrl(url);
            }
        }
    }

    private void callProfileAPI() {
        Log.e(TAG, "callProfileAPI: ");
        final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
        Call<ProfileResponse> call = apiClient.getProfile(SessionUser.getUser().getUser_id(), SessionUser.getUser().getUser_id());
        call.enqueue(new retrofit2.Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                ProfileResponse profileResponse = response.body();
                if (response.code() == 200) {
                    if (profileResponse != null) {
                        if (profileResponse.getStatus().equalsIgnoreCase("success")) {
                            SessionUser.saveUser(profileResponse.getData().getUser());
                            Log.e(TAG, "onResponse: CallProfile");
                            Intent returnIntent = getIntent();
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        } else {
                            Log.e(TAG, "onResponse: changeActivity");
                            changeActivity(ActivityHome.class);
                            finishAffinity();
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
                showToast(t.getMessage());
            }
        });
    }

    public void onClickRefresh(View view) {
        rlLoading.setVisibility(View.VISIBLE);
        rlError.setVisibility(View.GONE);
        wv.loadUrl(url);
    }
}