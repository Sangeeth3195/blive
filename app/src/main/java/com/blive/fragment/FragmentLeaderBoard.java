package com.blive.fragment;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.blive.constant.Constants_api;
import com.blive.utils.AdvanceWebViewArun.AdvancedWebViewArun;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.blive.activity.ActivityLeaderBoard;
import com.blive.R;
import com.blive.session.SessionUser;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;
public class FragmentLeaderBoard extends Fragment implements AdvancedWebViewArun.Listener {
    public Activity mActivity;
    private AdvancedWebViewArun mWebView;
    private ProgressDialog progressDialog;
    String from = "", urlNew = "", errorUrl = "", walleturl = "";
    public FragmentLeaderBoard() {
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
    private void initUI() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.show();
        progressDialog.setMessage("Loading");
        mWebView = getActivity().findViewById(R.id.webview);
        mWebView.setListener(getActivity(), this);
        mWebView.setGeolocationEnabled(false);
        mWebView.setMixedContentAllowed(true);
        mWebView.setCookiesEnabled(false);
        mWebView.getSettings().setAppCacheEnabled(false);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.setVerticalScrollBarEnabled(true);
        mWebView.setHorizontalScrollBarEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.setThirdPartyCookiesEnabled(true);

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
        mWebView.loadUrl(Constants_api.getTopperList + SessionUser.getUser().getUser_id() + "&country=" + SessionUser.getUser().getCountry());
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_leaderboard, container, false);
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
    }
    @Override
    public void onExternalPageRequest(String url) {
    }
}