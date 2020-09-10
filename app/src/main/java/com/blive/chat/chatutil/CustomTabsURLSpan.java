package com.blive.chat.chatutil;

import android.text.style.URLSpan;
import android.view.View;

public class CustomTabsURLSpan extends URLSpan {
    CustomTabsURLSpan(String url) {
        super(url);
    }

    @Override
    public void onClick(View widget) {
        String url = getURL();
        ChatUtils.loadUrl(widget.getContext(), url);
    }
}
