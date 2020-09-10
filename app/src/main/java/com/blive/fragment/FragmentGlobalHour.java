package com.blive.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blive.activity.ActivityViewProfile;
import com.blive.adapter.AdapterGlobal;
import com.blive.constant.Constants_api;

import com.blive.model.LeaderBoardResponse;
import com.blive.model.User;
import com.blive.R;
import com.blive.service.ApiClientBase;
import com.blive.service.ApiInterface;
import com.blive.session.SessionUser;
import com.blive.utils.DividerLine;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Response;

public class FragmentGlobalHour extends BaseFragment implements AdapterGlobal.Listener {

    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.tv_no_users)
    TextView tvNoUsers;
    @BindView(R.id.iv_profile_1)
    ImageView iv_profile_1;
    @BindView(R.id.iv_profile_2)
    ImageView iv_profile_2;
    @BindView(R.id.iv_profile_3)
    ImageView iv_profile_3;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.tv_name_1)
    TextView tv_name_1;
    @BindView(R.id.tv_name_2)
    TextView tv_name_2;
    @BindView(R.id.tv_received)
    TextView tv_received;
    @BindView(R.id.tv_received_1)
    TextView tv_received_1;
    @BindView(R.id.tv_received_2)
    TextView tv_received_2;
    @BindView(R.id.tv_level)
    TextView tv_level;
    @BindView(R.id.tv_level_1)
    TextView tv_level_1;
    @BindView(R.id.tv_level_2)
    TextView tv_level_2;
    @BindView(R.id.id_1)
    LinearLayout id1;
    @BindView(R.id.id_2)
    LinearLayout id2;
    @BindView(R.id.id_3)
    LinearLayout id3;
    @BindView(R.id.iv_topperDiamond)
    ImageView ivTopperDiamond;
    @BindView(R.id.iv_topperGold)
    ImageView ivTopperGold;
    @BindView(R.id.iv_topperSilver)
    ImageView ivTopperSilver;

    private SwipeRefreshLayout swipeRefreshLayout;
    String type = "", image = "", image1 = "", image2 = "";
    ArrayList<LinearLayout> linearLayouts;
    ArrayList<String> images;
    ArrayList<ImageView> imageViews;
    private ArrayList<User> hourlyUsers;

    public FragmentGlobalHour() {
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

        swipeRefreshLayout = mActivity.findViewById(R.id.swipeRefreshGHour);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        images = new ArrayList<>();
        linearLayouts = new ArrayList<>();
        imageViews = new ArrayList<>();

        images.add(image);
        images.add(image1);
        images.add(image2);

        linearLayouts.add(id1);
        linearLayouts.add(id2);
        linearLayouts.add(id3);

        imageViews.add(iv_profile_1);
        imageViews.add(iv_profile_2);
        imageViews.add(iv_profile_3);

        Glide.with(this)
                .load(Constants_api.topper_1)
                .into(ivTopperDiamond);

        Glide.with(this)
                .load(Constants_api.topper_2)
                .into(ivTopperGold);

        Glide.with(this)
                .load(Constants_api.topper_3)
                .into(ivTopperSilver);

        assert this.getArguments() != null;
        type = this.getArguments().getString("type");

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        rv.setLayoutManager(layoutManager);
        rv.addItemDecoration(new DividerLine(mActivity));
        layoutManager.setOrientation(OrientationHelper.VERTICAL);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            globalLeaderBoard(type);
            swipeRefreshLayout.setRefreshing(false);
        });

        globalLeaderBoard(type);
    }

    public void globalLeaderBoard(String type) {
        if (utils.isNetworkAvailable()) {
            swipeRefreshLayout.setRefreshing(true);
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<LeaderBoardResponse> call = apiClient.getLeaderBoard(type, "",SessionUser.getUser().getUser_id());
            call.enqueue(new retrofit2.Callback<LeaderBoardResponse>() {
                @Override
                public void onResponse(@NonNull Call<LeaderBoardResponse> call, @NonNull Response<LeaderBoardResponse> response) {
                    swipeRefreshLayout.setRefreshing(false);
                    LeaderBoardResponse leaderBoardResponse = response.body();
                    if (leaderBoardResponse != null) {
                        if (leaderBoardResponse.getStatus().equalsIgnoreCase("success")) {
                            setData(leaderBoardResponse.getData().getHourlyUsers());
                        }
                    } else {
                        utils.showToast(getString(R.string.server_error));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<LeaderBoardResponse> call, @NonNull Throwable t) {
                    swipeRefreshLayout.setRefreshing(false);
                    tvNoUsers.setVisibility(View.VISIBLE);
                    rv.setVisibility(View.GONE);
                    showToast(t.getMessage());
                }
            });
        }
    }

    private void setData(ArrayList<User> mHourlyUsers) {
        hourlyUsers = mHourlyUsers;
        if (hourlyUsers.size() > 0) {
            int size = hourlyUsers.size();
            if (size < 4) {
                switch (size) {
                    case 1:
                        id1.setVisibility(View.VISIBLE);
                        displayProfilePic(1);
                        tv_name.setText(hourlyUsers.get(0).getName());
                        tv_received.setText(hourlyUsers.get(0).getGift_value());
                        tv_level.setText(" Lv :" + hourlyUsers.get(0).getLevel() + " ");
                        break;
                    case 2:
                        id1.setVisibility(View.VISIBLE);
                        id2.setVisibility(View.VISIBLE);
                        displayProfilePic(2);
                        tv_name.setText(hourlyUsers.get(0).getName());
                        tv_received.setText(hourlyUsers.get(0).getGift_value());
                        tv_level.setText(" Lv :" + hourlyUsers.get(0).getLevel() + " ");

                        tv_name_1.setText(hourlyUsers.get(1).getName());
                        tv_received_1.setText(hourlyUsers.get(1).getGift_value());
                        tv_level_1.setText(" Lv :" + hourlyUsers.get(1).getLevel() + " ");
                        break;
                    case 3:
                        id1.setVisibility(View.VISIBLE);
                        id2.setVisibility(View.VISIBLE);
                        id3.setVisibility(View.VISIBLE);

                        displayProfilePic(3);

                        tv_name.setText(hourlyUsers.get(0).getName());
                        tv_received.setText(hourlyUsers.get(0).getGift_value());
                        tv_level.setText(" Lv :" + hourlyUsers.get(0).getLevel() + " ");

                        tv_name_1.setText(hourlyUsers.get(1).getName());
                        tv_received_1.setText(hourlyUsers.get(1).getGift_value());
                        tv_level_1.setText(" Lv :" + hourlyUsers.get(1).getLevel() + " ");

                        tv_name_2.setText(hourlyUsers.get(2).getName());
                        tv_received_2.setText(hourlyUsers.get(2).getGift_value());
                        tv_level_2.setText(" Lv :" + hourlyUsers.get(2).getLevel() + " ");
                        break;
                }
            } else {
                id1.setVisibility(View.VISIBLE);
                id2.setVisibility(View.VISIBLE);
                id3.setVisibility(View.VISIBLE);

                displayProfilePic(3);

                tv_name.setText(hourlyUsers.get(0).getName());
                tv_received.setText(hourlyUsers.get(0).getGift_value());
                tv_level.setText(" Lv :" + hourlyUsers.get(0).getLevel() + " ");

                tv_name_1.setText(hourlyUsers.get(1).getName());
                tv_received_1.setText(hourlyUsers.get(1).getGift_value());
                tv_level_1.setText(" Lv :" + hourlyUsers.get(1).getLevel() + " ");

                tv_name_2.setText(hourlyUsers.get(2).getName());
                tv_received_2.setText(hourlyUsers.get(2).getGift_value());
                tv_level_2.setText(" Lv :" + hourlyUsers.get(2).getLevel() + " ");

                ArrayList<User> users = new ArrayList<>();
                for (int i = 4; i < hourlyUsers.size(); i++) {
                    users.add(hourlyUsers.get(i));
                }
                AdapterGlobal adapterGlobal = new AdapterGlobal(mActivity, users);
                adapterGlobal.setOnClickListener(this);
                rv.setAdapter(adapterGlobal);
                rv.setVisibility(View.VISIBLE);
                tvNoUsers.setVisibility(View.GONE);
            }
        }else {
            id1.setVisibility(View.GONE);
            id2.setVisibility(View.GONE);
            id3.setVisibility(View.GONE);

            rv.setVisibility(View.GONE);
            tvNoUsers.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_global_hour, container, false);
    }

    private void displayProfilePic(int size) {
        for (int i = 0; i < size; i++) {
            linearLayouts.get(i).setVisibility(View.VISIBLE);

            int finalI = i;
            linearLayouts.get(i).setOnClickListener(view -> {
                if (!SessionUser.getUser().getUser_id().equals(hourlyUsers.get(finalI).getUser_id())) {
                    Intent intent = new Intent(mActivity, ActivityViewProfile.class);
                    intent.putExtra("image", hourlyUsers.get(finalI).getProfile_pic());
                    intent.putExtra("userId", hourlyUsers.get(finalI).getUser_id());
                    intent.putExtra("from", "list");
                    startActivity(intent);
                }
            });

            String image = hourlyUsers.get(finalI).getProfile_pic();
            if (image != null && !image.isEmpty()) {
                Picasso.get().load(image).fit().centerCrop().into(imageViews.get(finalI));
            } else {
                Picasso.get().load(R.drawable.user).fit().centerCrop().into(imageViews.get(finalI));
            }
        }
    }

    @Override
    public void OnClicked(User user) {
        Intent intent = new Intent(mActivity, ActivityViewProfile.class);
        intent.putExtra("image", user.getProfile_pic());
        intent.putExtra("userId", user.getUser_id());
        intent.putExtra("from", "list");
        startActivity(intent);
    }
}
