package com.blive.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blive.R;
import com.blive.model.ProfileResponse;
import com.blive.service.ApiClientBase;
import com.blive.service.ApiInterface;
import com.blive.session.SessionLogin;
import com.blive.session.SessionUser;

import butterknife.BindView;
import butterknife.OnClick;
import im.delight.android.webview.AdvancedWebView;
import retrofit2.Call;
import retrofit2.Response;


public class ActivityAdvancedWV extends BaseBackActivity implements AdvancedWebView.Listener {

    private AdvancedWebView mWebView;
    @Nullable
    @BindView(R.id.rl_back)
    RelativeLayout rlBack;
    @Nullable
    @BindView(R.id.rl_skip)
    RelativeLayout rlSkip;
    @Nullable
    @BindView(R.id.tv_title)
    TextView tvTitle;
    String url = "", from = "", urlNew = "", errorUrl = "", walleturl = "";
    ProgressDialog progressDialog;
    ImageView ivgWallet;
    CardView cvHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webviewactivity);
        ivgWallet = findViewById(R.id.iv_gWallet);
        cvHeader = findViewById(R.id.cv_header);
        progressDialog = new ProgressDialog(this);
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        url = intent.getStringExtra("url");
        from = intent.getStringExtra("from");
        setTitle(title);
        if (title.equalsIgnoreCase("Wallet")) {
            ivgWallet.setVisibility(View.VISIBLE);
            walleturl = url;
        } else {
            ivgWallet.setVisibility(View.GONE);
        }
        if (title.equalsIgnoreCase("Toppers")) {
            cvHeader.setVisibility(View.GONE);
        } else {
            cvHeader.setVisibility(View.VISIBLE );
        }

        ivgWallet.setOnClickListener(v -> {
            changeActivity(ActivityInAppPurchase.class);
        });

        mWebView = (AdvancedWebView) findViewById(R.id.webview);
        mWebView.setListener(this, this);
        mWebView.loadUrl(url);

/*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.setWebContentsDebuggingEnabled(true);
        }
*/

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                progressDialog.dismiss();
                Log.i("autolog", "url: " + url);
                if (!url.equalsIgnoreCase(errorUrl)) {
                    urlNew = view.getUrl();
                }

                if (walleturl.equalsIgnoreCase(url) && title.equalsIgnoreCase("WALLET")) {
                    ivgWallet.setVisibility(View.VISIBLE);

                } else {
                    ivgWallet.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Log.i("WebViewerror", "error: " + error);
                errorUrl = request.getUrl().toString();
//                progressDialog.dismiss();

            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                Log.i("autolog", "error: " + error);
//                progressDialog.dismiss();
                final AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAdvancedWV.this);
                builder.setMessage(R.string.notification_error_ssl_cert_invalid);
                builder.setPositiveButton("continue", (dialog, which) -> handler.proceed());
                builder.setNegativeButton("cancel", (dialog, which) -> handler.cancel());
                final AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


//        progressDialog.show();
        if (title.equalsIgnoreCase(""))
//            progressDialog.setMessage("Please Wait...");
//        progressDialog.setCanceledOnTouchOutside(false);


            mWebView.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
//                progressDialog.dismiss();
                    Log.i("autolog", "url: " + url);
                    if (!url.equalsIgnoreCase(errorUrl)) {
                        urlNew = view.getUrl();
                    }
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    Log.i("WebViewerror", "error: " + error);
                    errorUrl = request.getUrl().toString();
//                progressDialog.dismiss();

                }

                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    Log.i("autolog", "error: " + error);
//                progressDialog.dismiss();

                    final AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAdvancedWV.this);
                    builder.setMessage(R.string.notification_error_ssl_cert_invalid);
                    builder.setPositiveButton("continue", (dialog, which) -> handler.proceed());
                    builder.setNegativeButton("cancel", (dialog, which) -> handler.cancel());
                    final AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });


    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
//        if (tvTitle.getText().toString().equalsIgnoreCase("WALLET")) {
//            mWebView.loadUrl(url);
//            /*callProfileAPI();*/
//            Log.i("autolog", "url: " + url);
//        }
//        // ...
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        mWebView.onPause();
        // ...
        super.onPause();
    }

    public void setTitle(String title) {
        assert tvTitle != null;
        tvTitle.setText(title);
    }

    @Override
    protected void onDestroy() {
        mWebView.onDestroy();
        // ...
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        mWebView.onActivityResult(requestCode, resultCode, intent);
        // ...
    }

//    @Override
//    public void onBackPressed() {
//        if (!mWebView.onBackPressed()) { return; }
//        // ...
//        super.onBackPressed();
//    }


    @Override
    public void onPageStarted(String url, Bitmap favicon) {
    }

    @Override
    public void onPageFinished(String url) {
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
    }

    @Override
    public void onExternalPageRequest(String url) {
    }

    @OnClick(R.id.rl_back)
    public void onBackClicked() {
        if (SessionLogin.getLoginSession()) {
            if (from != null) {
                if (from.equalsIgnoreCase("liveRoom")) {
                    if (utils.isNetworkAvailable()) {
                        callProfileAPI();
                    }
                } else {
                    if (urlNew.equalsIgnoreCase(url) || urlNew.isEmpty()) {
                        finish();
                    } else {
                        mWebView.loadUrl(url);
                    }
                }
            } else {
                if (urlNew.equalsIgnoreCase(url) || urlNew.isEmpty()) {
                    finish();
                } else {
                    mWebView.loadUrl(url);
                }
            }
        } else {
            if (urlNew.equalsIgnoreCase(url) || urlNew.isEmpty())
                finish();
            else {
                mWebView.loadUrl(url);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (SessionLogin.getLoginSession()) {
            if (from != null) {
                if (from.equalsIgnoreCase("liveRoom")) {
                    if (utils.isNetworkAvailable()) {
                        callProfileAPI();
                    }
                }
            }
            if (urlNew.contains("wallet")) {
                if (utils.isNetworkAvailable()) {
                    callProfileAPI();
                }
            } else {
                if (urlNew.equalsIgnoreCase(url) || urlNew.isEmpty()) {
                    finish();
                } else {
                    mWebView.loadUrl(url);
                }
            }
        } else {
            if (urlNew.equalsIgnoreCase(url))
                finish();
            else {
                mWebView.loadUrl(url);
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

}