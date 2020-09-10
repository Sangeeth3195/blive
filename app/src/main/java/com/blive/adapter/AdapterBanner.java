package com.blive.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.blive.constant.Constants_api;
import com.blive.R;

public class AdapterBanner extends PagerAdapter {

    private Context context;
    private Listener listener;

    public AdapterBanner(Context context) {
        this.context = context;
    }

    public int getCount() {
        return 8;
    }

    @SuppressLint({"InflateParams", "SetJavaScriptEnabled"})
    @NonNull
    public Object instantiateItem(@NonNull View collection, int position) {

        LayoutInflater inflater = (LayoutInflater) collection.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;
        WebView wv;
        RelativeLayout rl;

        assert inflater != null;
        view = inflater.inflate(R.layout.item_banner, null);
        wv = view.findViewById(R.id.wv);
        rl = view.findViewById(R.id.rl);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setAppCacheEnabled(false);
        wv.setOnClickListener(null);

        rl.setOnClickListener(v -> listener.OnClickedBanner(position));

        ((ViewPager) collection).addView(view, 0);
        switch (position) {
            case 0:
                wv.loadUrl(Constants_api.index1);
                break;
            /*case 1:
                wv.loadUrl(Constants_api.index2);
                break;
            case 2:
                wv.loadUrl(Constants_api.index3);
                break;
            case 3:
                wv.loadUrl(Constants_api.index4);
                break;
            case 4:
                wv.loadUrl(Constants_api.index5);
                break;
            case 5:
                wv.loadUrl(Constants_api.index6);
                break;
            case 6:
                wv.loadUrl(Constants_api.index7);
                break;
            case 7:
                wv.loadUrl(Constants_api.index8);
                break;*/
        }
        return view;
    }

    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView((View) arg2);

    }

    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == ((View) arg1);

    }

    public void setOnClickListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void OnClickedBanner(int position);
    }
}