package com.blive.custom;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;

import com.blive.R;

/**
 * Created by sans on 20-08-2018.
 **/

public class Loader extends androidx.appcompat.widget.AppCompatImageView {

    public Loader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public Loader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Loader(Context context) {
        super(context);
        init();
    }

    private void init() {
        setBackgroundResource(R.drawable.running_super_car);
        final AnimationDrawable frameAnimation = (AnimationDrawable) getBackground();
        post(new Runnable(){
            public void run(){
                frameAnimation.start();
            }
        });

    }
}
