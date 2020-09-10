package com.blive.utils.AdvanceWebViewArun;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.blive.R;
import com.blive.session.SessionLogin;

import butterknife.OnClick;

public class webview extends Activity implements AdvancedWebViewArun.Listener {
    private AdvancedWebViewArun mWebView;
    private ProgressDialog progressDialog;
    String url = "", from = "", urlNew = "", errorUrl = "", walleturl = "";
    RelativeLayout rl_back;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_advance);
        rl_back=findViewById(R.id.rl_back);
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setMessage("Loading");
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        url = intent.getStringExtra("url");
        from = intent.getStringExtra("from");
        setTitle(title);
        if (title.equalsIgnoreCase("Wallet")) {
            walleturl = url;
        }
        rl_back.setOnClickListener(v -> onBackClicked());

        mWebView = findViewById(R.id.webview);
        mWebView.setListener(this, this);
        mWebView.setGeolocationEnabled(false);
        mWebView.setMixedContentAllowed(true);
        mWebView.setCookiesEnabled(false);
        mWebView.getSettings().setAppCacheEnabled(false);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.setVerticalScrollBarEnabled(true);
        mWebView.setHorizontalScrollBarEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.setThirdPartyCookiesEnabled(true);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.setWebContentsDebuggingEnabled(true);
        }*/
         mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {

            }

        });
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);

            }

        });
        mWebView.addHttpHeader("X-Requested-With", "");
        mWebView.loadUrl(url);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
        // ...
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        mWebView.onPause();
        // ...
        super.onPause();
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

    @Override
    public void onBackPressed() {
        if (!mWebView.onBackPressed()) { return; }
        // ...
        super.onBackPressed();
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        mWebView.setVisibility(View.INVISIBLE);
        progressDialog.show();
    }

    @Override
    public void onPageFinished(String url) {
        mWebView.setVisibility(View.VISIBLE);
        progressDialog.dismiss();
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        progressDialog.dismiss();
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

		/*if (AdvancedWebView.handleDownload(this, url, suggestedFilename)) {
			// download successfully handled
		}
		else {
			// download couldn't be handled because user has disabled download manager app on the device
		}*/
    }

    @Override
    public void onExternalPageRequest(String url) {
    }

    @OnClick(R.id.rl_back)
    public void onBackClicked() {
        if (SessionLogin.getLoginSession()) {
            if (from != null) {
                if (from.equalsIgnoreCase("liveRoom")) {

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


}