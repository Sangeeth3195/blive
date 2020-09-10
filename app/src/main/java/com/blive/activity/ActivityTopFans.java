package com.blive.activity;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.blive.adapter.AdapterTopFans;
import com.blive.BLiveApplication;
import com.blive.model.TopFansResponse;
import com.blive.model.User;
import com.blive.R;
import com.blive.service.ApiClientBase;
import com.blive.service.ApiInterface;
import com.blive.session.SessionUser;

import java.util.ArrayList;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Response;

public class ActivityTopFans extends BaseBackActivity implements AdapterTopFans.Listener {

    @BindView(R.id.rv_topfans)
    RecyclerView rvFollowers;
    @BindView(R.id.tv_no_topfans)
    TextView tvNoFollowers;

    private SwipeRefreshLayout swipeRefreshLayout;
    String activationToken, userId, title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_fans);
        BLiveApplication.setCurrentActivity(this);
        initUI();
    }

    private void initUI() {
        Intent intent = getIntent();
        if (intent != null) {
            activationToken = intent.getStringExtra("activationToken");
            userId = intent.getStringExtra("user_id");
            title = intent.getStringExtra("title");
        }

        swipeRefreshLayout = mActivity.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        if(title!=null) {
            setTitle(title);
            tvNoFollowers.setText("No Contributors Available");
        }else
            setTitle("Top Fans");

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        rvFollowers.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            getTopFans();
            swipeRefreshLayout.setRefreshing(false);
        });
        getTopFans();
    }

    public void getTopFans() {
        if (utils.isNetworkAvailable()) {
            utils.showProgress();
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<TopFansResponse> call = apiClient.getTopFans(userId);
            call.enqueue(new retrofit2.Callback<TopFansResponse>() {
                @Override
                public void onResponse(@NonNull Call<TopFansResponse> call, @NonNull Response<TopFansResponse> response) {
                    utils.hideProgress();
                    TopFansResponse topFansResponse = response.body();
                    Log.e(TAG, "onResponse: "+ response.body());
                    if (response.code() == 200) {
                        if (topFansResponse != null) {
                            if (topFansResponse.getStatus().equalsIgnoreCase("success")) {
                                onTopListSuccess(topFansResponse.getData().getWeeklyUsers());
                            } else {
                                rvFollowers.setVisibility(View.GONE);
                                tvNoFollowers.setVisibility(View.VISIBLE);
                                showToast(topFansResponse.getMessage());
                            }
                        } else {
                            rvFollowers.setVisibility(View.GONE);
                            tvNoFollowers.setVisibility(View.VISIBLE);
                            showToast(getString(R.string.server_error));
                        }
                    } else {
                        rvFollowers.setVisibility(View.GONE);
                        tvNoFollowers.setVisibility(View.VISIBLE);
                        checkResponseCode(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<TopFansResponse> call, @NonNull Throwable t) {
                    showToast(t.getMessage());
                }
            });
        }
    }

    public void onTopListSuccess(ArrayList<User> weeklyUsersGold) {
        try {
            if (weeklyUsersGold.size() == 0) {
                rvFollowers.setVisibility(View.GONE);
                tvNoFollowers.setVisibility(View.VISIBLE);
            } else {
                AdapterTopFans adapterTopFans = new AdapterTopFans(this, weeklyUsersGold);
                adapterTopFans.setOnClickListener(this);
                rvFollowers.setAdapter(adapterTopFans);
                rvFollowers.setVisibility(View.VISIBLE);
                tvNoFollowers.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast(e.getMessage());
        }
    }

    @Override
    public void OnClicked(User user) {
        if(!user.getUser_id().equalsIgnoreCase(SessionUser.getUser().getUser_id())){
            Intent intent = new Intent(mActivity, ActivityViewProfile.class);
            intent.putExtra("image", user.getProfile_pic());
            intent.putExtra("userId", user.getUser_id());
            intent.putExtra("from", "fans");
            startActivity(intent);
        }
    }
}
