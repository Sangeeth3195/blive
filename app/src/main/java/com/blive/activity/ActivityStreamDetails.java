package com.blive.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blive.BLiveApplication;
import com.blive.R;
import com.blive.session.SessionUser;
import com.blive.utils.Utils;
import com.bumptech.glide.Glide;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.OnClick;
import jp.wasabeef.picasso.transformations.BlurTransformation;

/**
 * Created by sans on 25-08-2018.
 **/

public class ActivityStreamDetails extends BaseBackActivity {

    @BindView(R.id.bLikes)
    ShineButton bLikes;
    @BindView(R.id.tv_likes)
    TextView tvLikes;
    @BindView(R.id.tv_gold)
    TextView tvGold;
    @BindView(R.id.tv_broad_time)
    TextView tvBroadTime;
    @BindView(R.id.tv_idel_time)
    TextView tvIdelTime;
    @BindView(R.id.tv_viewers)
    TextView tvViewers;
    @BindView(R.id.tv_duration)
    TextView tvTime;
    @BindView(R.id.iv)
    ImageView iv;
    @BindView(R.id.iv_blur)
    ImageView ivBlur;
    @BindView(R.id.ll_broadTime)
    LinearLayout llBroadTime;

    private ImageView ivEffect;

    private String gold = "",viewers = "",likes="",time = "", from = "",guestProfilePc="",guestName ="",idelTime="",broadTime="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_details);
        BLiveApplication.setCurrentActivity(this);
        initUI();
    }

    private void initUI() {

        ivEffect = findViewById(R.id.iv_effect);

        Intent intent = getIntent();
        if(intent!=null){
            gold = intent.getStringExtra("gold");
            viewers = intent.getStringExtra("viewers");
            likes = intent.getStringExtra("likes");
            time = intent.getStringExtra("time");

            Log.e(TAG, "initUI: "+ time);

            from = intent.getStringExtra("from");

            Log.e(TAG, "from: " + from );
            if(!from.equals("screenShare")){
                idelTime = intent.getStringExtra("idelTime");
                broadTime = intent.getStringExtra("broadTime");
            }else{
                llBroadTime.setVisibility(View.GONE);
            }
        }

        Glide.with(getApplicationContext())
                .load(SessionUser.getUser().getTools_applied())
                .into(ivEffect);

        bLikes.init(this);
        bLikes.setChecked(true);
        bLikes.setEnabled(false);

        tvGold.setText(gold);
        tvViewers.setText(viewers);
        tvLikes.setText(likes);
        tvTime.setText(broadTime);

        if(!from.equals("screenShare")){
            tvBroadTime.setText(time);
            tvIdelTime.setText(idelTime);
        }else{
            tvTime.setText(time);
            tvBroadTime.setVisibility(View.GONE);
            tvIdelTime.setVisibility(View.GONE);
        }

        if(!SessionUser.getUser().getProfile_pic().isEmpty()){
            Picasso.get().load(SessionUser.getUser().getProfile_pic()).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).
                    placeholder(R.drawable.user).into(iv);
            Picasso.get().load(SessionUser.getUser().getProfile_pic()).fit().transform(new BlurTransformation(mActivity)).centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).
                    placeholder(R.drawable.user).into(ivBlur);
        }else {
            Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).
                    placeholder(R.drawable.user).into(iv);
            Picasso.get().load(R.drawable.user).fit().transform(new BlurTransformation(mActivity)).centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).
                    placeholder(R.drawable.user).into(ivBlur);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        utils.deleteCache(this);
    }

    @OnClick(R.id.cv_ok)
    public void onClickOk(){
        finishAffinity();
        changeActivity(ActivityHome.class);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        changeActivity(ActivityHome.class);
    }
}