package com.blive.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blive.R;
import com.blive.activity.ActivityAdvancedWV;
import com.blive.activity.ActivityEditProfile;
import com.blive.activity.ActivityNotification;
import com.blive.activity.ActivitySettings;
import com.blive.activity.ActivityTopFans;
import com.blive.activity.ActivityUsers;
import com.blive.activity.ActivityWebView;
import com.blive.adapter.AdapterSlidingImage;
import com.blive.chat.chatview.MyMessageActivity;
import com.blive.constant.Constants_api;
import com.blive.constant.Constants_app;
import com.blive.custom.AutoScrollViewPager;
import com.blive.model.ProfileResponse;
import com.blive.model.User;
import com.blive.service.ApiClientBase;
import com.blive.service.ApiInterface;
import com.blive.session.SessionManager;
import com.blive.session.SessionUser;
import com.blive.utils.AdvanceWebViewArun.webview;
import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public class FragmentProfile extends BaseFragment {

    @BindView(R.id.tv_userName)
    TextView tvUserName;
    @BindView(R.id.tv_fans)
    TextView tvFans;
    @BindView(R.id.tv_followers)
    TextView tvFollowers;
    @BindView(R.id.tv_friends)
    TextView tvFriends;
    @BindView(R.id.tv_bLiveId)
    TextView tvBLiveId;
    @BindView(R.id.iv_profile)
    ImageView iv_profilePic;
    @BindView(R.id.view_pager_images)
    AutoScrollViewPager slidingImageViewPager;
    ArrayList<String> profilePictureArray = new ArrayList<>();
    @BindView(R.id.tv_level)
    TextView tvLevel;
    @BindView(R.id.tv_gold)
    TextView tvOverAllGold;

    TextView tv_Notification_count;

    private User user;
    private ImageView ivEffects;
    private boolean isResume = false, isClicked = false;
    private String image = "", image1 = "", image2 = "", image3 = "";
    String friends = "", fans = "", followers = "";
    SessionManager sessionManager;

    public FragmentProfile() {

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview=inflater.inflate(R.layout.fragment_profile, container, false);
        tv_Notification_count=rootview.findViewById(R.id.tv_Notification_count);
        sessionManager=new SessionManager(getActivity());
        tv_Notification_count.setVisibility(View.GONE);

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                if (sessionManager.getSessionStringValue("notification","notification").equalsIgnoreCase("0")){
                    tv_Notification_count.setVisibility(View.GONE);
                }else {
                    tv_Notification_count.setVisibility(View.VISIBLE);

                }
                tv_Notification_count.setText(sessionManager.getSessionStringValue("notification","notification"));

                handler.postDelayed(this, 3000);
            }
        };
        handler.postDelayed(runnable, 3000);

        return rootview;
    }

    @Override
    public void onResume() {

        Log.d(TAG, "initUI: "+sessionManager.getSessionStringValue("notification","notification"));

        isClicked = false;

        if (isResume) {
            isResume = false;
            tvUserName.setText(SessionUser.getUser().getName());

            if (sessionManager.getSessionStringValue("notification","notification").equalsIgnoreCase("0")){
                tv_Notification_count.setVisibility(View.GONE);
            }else {
                tv_Notification_count.setVisibility(View.VISIBLE);

            }
            tv_Notification_count.setText(sessionManager.getSessionStringValue("notification","notification"));
           tvBLiveId.setText("BLive ID:" + SessionUser.getUser().getReference_user_id());
            loadProfilePictures();
        }

        try {
            if (friends.equals(SessionUser.getUser().getFriends()) || friends.equals(SessionUser.getUser().getFriends()) ||
                    friends.equals(SessionUser.getUser().getFriends())) {
                tvFans.setText(SessionUser.getUser().getFans());
                tvFollowers.setText(SessionUser.getUser().getFollowers());
                tvFriends.setText(SessionUser.getUser().getFriends());
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        super.onResume();
    }

    private void initUI() {
        user = SessionUser.getUser();
        tvUserName.setText(SessionUser.getUser().getName());
        tvBLiveId.setText("BLive ID:" + SessionUser.getUser().getReference_user_id());
        tvLevel.setText(" Level : " + SessionUser.getUser().getLevel() + " ");
        tvOverAllGold.setText(SessionUser.getUser().getOver_all_gold());
        if (sessionManager.getSessionStringValue("notification","notification").equalsIgnoreCase("0")){
            tv_Notification_count.setVisibility(View.GONE);
        }else {
            tv_Notification_count.setVisibility(View.VISIBLE);
        }
        tv_Notification_count.setText(sessionManager.getSessionStringValue("notification","notification"));
        friends = (user.getFriends());
        followers = (user.getFollowers());
        fans = (user.getFans());
        tvFans.setText(fans);
        tvFollowers.setText(followers);
        tvFriends.setText(friends);

        ivEffects = mActivity.findViewById(R.id.iv_effect);

        Glide.with(this)
               .load(SessionUser.getUser().getTools_applied())
                .into(ivEffects);

        // loading profile image in viewpager
        loadProfilePictures();

        getProfileData();

    }

    private void getProfileData() {
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<ProfileResponse> call = apiClient.getProfile(SessionUser.getUser().getUser_id(), SessionUser.getUser().getUser_id());
            call.enqueue(new retrofit2.Callback<ProfileResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {

                    ProfileResponse profileResponse = response.body();
                    Log.e(TAG, "onResponse: " + response.toString());
                    if (response.code() == 200) {
                        if (profileResponse != null) {
                            if (profileResponse.getStatus().equalsIgnoreCase("success")) {
                                Log.e(TAG, "onResponse: " + profileResponse.toString());
                                SessionUser.saveUser(profileResponse.getData().getUser());
                                user = SessionUser.getUser();
                                tvUserName.setText(SessionUser.getUser().getName());
                                tvBLiveId.setText("BLive ID:" + SessionUser.getUser().getReference_user_id());
                                tvLevel.setText(" Level : " + SessionUser.getUser().getLevel() + " ");
                                tvOverAllGold.setText(SessionUser.getUser().getOver_all_gold());
                                friends = (user.getFriendsCount());
                                followers = (user.getFollowersCount());
                                fans = (user.getFansCount());
                                tvFans.setText(fans);
                                tvFollowers.setText(followers);
                                tvFriends.setText(friends);
                            } else {
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
    }

    public void loadProfilePictures() {
        try {
            image1 = SessionUser.getUser().getProfile_pic();
            image2 = SessionUser.getUser().getProfile_pic1();
            image3 = SessionUser.getUser().getProfile_pic2();

            profilePictureArray.clear();

            if (image1.isEmpty())
                profilePictureArray.add("");
            else
                profilePictureArray.add(image1);

            if (image2.isEmpty())
                profilePictureArray.add("");
            else
                profilePictureArray.add(image2);

            if (image3.isEmpty())
                profilePictureArray.add("");
            else
                profilePictureArray.add(image3);

            if (image1 != null && !image1.isEmpty() && image2 != null && !image2.isEmpty() && image3 != null && !image3.isEmpty()) {
                Picasso.get().load(image1).fit().centerCrop().into(iv_profilePic);

                AdapterSlidingImage adapterSlidingImage = new AdapterSlidingImage(mActivity, profilePictureArray);
                slidingImageViewPager.startAutoScroll();
                slidingImageViewPager.startAutoScroll(3000);
                slidingImageViewPager.setAdapter(adapterSlidingImage);

            } else {
                Glide.with(this).load(R.drawable.user).into(iv_profilePic);
            }
        } catch (Exception ex) {
            Crashlytics.logException(ex);
        }
    }

    @OnClick(R.id.iv_qrCode)
    public void onClickScanCode() {
        if (!isClicked) {
            isClicked = true;
            showToast("Comming soon...");
            //  changeActivity(ActivityScanner.class);
        }
    }

    @OnClick(R.id.ll_message)
    public void onClickMessage() {
        startActivity(new Intent(mActivity, MyMessageActivity.class));
    }

    @OnClick(R.id.ll_games)
    public void onClickGames() {
        if (utils.isNetworkAvailable()) {
            Intent intent = new Intent(mActivity, webview.class);
            intent.putExtra("title", "games");
            intent.putExtra("url", Constants_api.games + SessionUser.getUser().getUser_id());
            startActivity(intent);
        }
    }

    @OnClick(R.id.ll_wallet)
    public void onClickWallet() {
        if (utils.isNetworkAvailable()) {
            if (!isClicked) {
                isClicked = true;
                Intent intent = new Intent(mActivity, ActivityAdvancedWV.class);
                intent.putExtra("title", "Wallet");
                intent.putExtra("from", "profile");
                intent.putExtra("url", Constants_api.wallet + SessionUser.getUser().getUser_id());
                startActivity(intent);
            }
        }
    }

    @OnClick(R.id.ll_offers)
    public void onClickDailyTAsk() {
        if (utils.isNetworkAvailable()) {
            Intent intent = new Intent(mActivity, ActivityNotification.class);
//            intent.putExtra("title", "Daily Task");
//            intent.putExtra("from", "profile");
//            intent.putExtra("url", Constants_api.dailyTask + SessionUser.getUser().getUser_id());
            startActivity(intent);
        }
    }

    @OnClick(R.id.ll_level)
    public void onClickLevel() {
        if (utils.isNetworkAvailable()) {
            if (!isClicked) {
                isClicked = true;
                Intent intent = new Intent(mActivity, webview.class);
                intent.putExtra("title", "Level");
                intent.putExtra("from", "profile");
                intent.putExtra("url", Constants_api.level + SessionUser.getUser().getUser_id());
                startActivity(intent);
            }
        }
    }

    @OnClick(R.id.ll_rewards)
    public void onClickRewards() {
        if (utils.isNetworkAvailable()) {
            if (!isClicked) {
                isClicked = true;
                Intent intent = new Intent(mActivity, webview.class);
                intent.putExtra("title", "Rewards");
                intent.putExtra("from", "profile");
                intent.putExtra("url", Constants_api.rewards + SessionUser.getUser().getUser_id());
                startActivity(intent);
            }
        }
    }

    @OnClick(R.id.ll_assets)
    public void onClickAssets() {
        if (utils.isNetworkAvailable()) {
            Intent intent = new Intent(mActivity, webview.class);
            intent.putExtra("title", "Assets");
            intent.putExtra("url", Constants_api.assets + SessionUser.getUser().getUser_id());
            Log.e(TAG, "onClickAssets: " + Constants_api.assets + SessionUser.getUser().getUser_id());
            startActivity(intent);
        }
    }

    @OnClick(R.id.ll_progress)
    public void onClickProgress() {
        if (utils.isNetworkAvailable()) {
            if (!isClicked) {
                isClicked = true;
                Intent intent = new Intent(mActivity, webview.class);
                intent.putExtra("title", "My Progress");
                intent.putExtra("from", "profile");
                intent.putExtra("url", Constants_api.progress + SessionUser.getUser().getUser_id());
                startActivity(intent);
            }
        }
    }

    @OnClick(R.id.ll_contributors)
    public void onClickContributors() {
        if (utils.isNetworkAvailable()) {
            if (!isClicked) {
                isClicked = true;
                Intent intent = new Intent(mActivity, ActivityTopFans.class);
                intent.putExtra("title", "Top Fans");
                intent.putExtra("activationToken", SessionUser.getUser().getActivation_code());
                intent.putExtra("user_id", SessionUser.getUser().getUser_id());
                startActivity(intent);
            }
        }
    }

   /* @OnClick(R.id.ll_game)
    public void onClickGame() {
        if (utils.isNetworkAvailable()) {
            if (!isClicked) {
                isClicked = true;
                Intent intent = new Intent(mActivity, ActivityGame.class);
                intent.putExtra("title", "Game Test");
                startActivity(intent);
            }
        }
    }*/

    @OnClick(R.id.ll_daily_check_In)
    public void onClickDailyCaheckIN() {
        if (utils.isNetworkAvailable()) {
            if (!isClicked) {
                isClicked = true;
                Intent intent = new Intent(mActivity, webview.class);
                intent.putExtra("title", "Daily CheckIN");
                intent.putExtra("from", "profile");
                intent.putExtra("url", Constants_api.DailyCheckin + SessionUser.getUser().getUser_id());
                Log.e(TAG, "onClickDailyCaheckIN: " + Constants_api.DailyCheckin + SessionUser.getUser().getUser_id());
                startActivity(intent);
            }
        }
    }

    @OnClick({R.id.ll_followers, R.id.tv_followers, R.id.tv_follower})
    public void onClickFollowers() {
        if (!isClicked) {
            isClicked = true;
            if (utils.isNetworkAvailable()) {
                Intent intent = new Intent(mActivity, ActivityUsers.class);
                intent.putExtra("title", "My Fans");
                intent.putExtra("type", "follow");
                startActivity(intent);
            }
        }
    }

    @OnClick({R.id.ll_friends, R.id.tv_friends, R.id.tv_friend})
    public void onClickFriends() {
        if (!isClicked) {
            isClicked = true;
            if (utils.isNetworkAvailable()) {
                Intent intent = new Intent(mActivity, ActivityUsers.class);
                intent.putExtra("title", "Friends");
                intent.putExtra("type", "friend");
                startActivity(intent);
            }
        }
    }

    @OnClick({R.id.ll_fans, R.id.tv_fan, R.id.tv_fans})
    public void onClickFans() {
        if (!isClicked) {
            isClicked = true;
            if (utils.isNetworkAvailable()) {
                Intent intent = new Intent(mActivity, ActivityUsers.class);
                intent.putExtra("title", "Following");
                intent.putExtra("type", "fans");
                startActivity(intent);
            }
        }
    }

    @OnClick(R.id.iv_settings)
    public void onClickSettings() {
        if (!isClicked) {
            isClicked = true;
            changeActivity(ActivitySettings.class);
        }
    }

    @OnClick(R.id.cv_image)
    public void onClickImage() {
        if (utils.isNetworkAvailable()) {
            if (!isClicked) {
                isClicked = true;
                isResume = true;
                Intent i = new Intent(mActivity, ActivityEditProfile.class);
                startActivityForResult(i, Constants_app.EDIT_PROFILE);
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void refreshProfile(String message) {
        Log.e(TAG, "refreshNotificationss: " + message);
        if (message.equals("refresh"))
            getProfileData();
    }
}