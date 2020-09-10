package com.blive.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.blive.adapter.AdapterBlockList;
import com.blive.BLiveApplication;
import com.blive.model.User;
import com.blive.R;
import com.blive.model.UsersResponse;
import com.blive.service.ApiClientBase;
import com.blive.service.ApiInterface;
import com.blive.session.SessionUser;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Response;

public class ActivityBlockList extends BaseBackActivity implements AdapterBlockList.Listener {

    @BindView(R.id.rv_blockList)
    RecyclerView rvBlockList;
    @BindView(R.id.tv_no_blockList)
    TextView tvNoBlockList;

    private ArrayList<User> users, tempUser;
    private AdapterBlockList adapterBlockList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int page = 1, lastPage = 1;
    private boolean isUserListEnd = false, isRefreshing = false, isAPICalled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_list);
        BLiveApplication.setCurrentActivity(this);
        initUI();
    }

    private void initUI() {
        setTitle("Blocked List");
        users = new ArrayList<>();
        tempUser = new ArrayList<>();
        swipeRefreshLayout = mActivity.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        rvBlockList.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        tempUser.add(null);
        users.add(null);
        adapterBlockList = new AdapterBlockList(this, users);
        adapterBlockList.setOnClickListener(this);
        rvBlockList.setAdapter(adapterBlockList);
        rvBlockList.setVisibility(View.VISIBLE);
        tvNoBlockList.setVisibility(View.GONE);

        rvBlockList.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (!isRefreshing) {
                if (!isUserListEnd) {
                    if (isLastItemDisplaying(rvBlockList)) {
                        if (page < lastPage) {
                            page = page + 1;
                            getBlockList(page);
                        }
                    }
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            page = 1;
            isUserListEnd = false;
            isRefreshing = true;
            getBlockList(page);
            swipeRefreshLayout.setRefreshing(false);
        });

        getBlockList(page);
    }

    public void getBlockList(int page) {
        if (!isAPICalled) {
            isAPICalled = true;
            if (utils.isNetworkAvailable()) {
                if (page > 1) {
                    adapterBlockList.update(tempUser);
                }else
                    swipeRefreshLayout.setRefreshing(false);
                final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
                Call<UsersResponse> call = apiClient.getBlockedUsers(String.valueOf(page), SessionUser.getUser().getUser_id());
                call.enqueue(new retrofit2.Callback<UsersResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<UsersResponse> call, @NonNull Response<UsersResponse> response) {
                        UsersResponse usersResponse = response.body();
                        if (response.code() == 200) {
                            if (usersResponse != null) {
                                if (usersResponse.getStatus().equalsIgnoreCase("success")) {
                                    setData(usersResponse.getData().getUsers(), usersResponse.getData().getLast_page());
                                }
                            } else {
                                utils.showToast(getString(R.string.server_error));
                            }
                        } else {
                            rvBlockList.setVisibility(View.GONE);
                            tvNoBlockList.setVisibility(View.VISIBLE);
                            checkResponseCode(response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<UsersResponse> call, @NonNull Throwable t) {
                        utils.hideProgress();
                        rvBlockList.setVisibility(View.GONE);
                        tvNoBlockList.setVisibility(View.VISIBLE);
                        showToast(t.getMessage());
                    }
                });
            }
        }
    }

    private void setData(ArrayList<User> mUsers, int mPage) {
        lastPage = mPage;
        isAPICalled = false;
        isRefreshing = false;
        if (page == 1) {
            swipeRefreshLayout.setRefreshing(false);
            if ( mUsers.size() > 0) {
                rvBlockList.setVisibility(View.VISIBLE);
                tvNoBlockList.setVisibility(View.GONE);
                adapterBlockList.refresh(mUsers);
            } else {
                rvBlockList.setVisibility(View.GONE);
                tvNoBlockList.setVisibility(View.VISIBLE);
            }
        } else {
            adapterBlockList.removeLastItem();
            if (page == lastPage)
                isUserListEnd = true;
            adapterBlockList.update(mUsers);
        }
    }

    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (adapterBlockList != null) {
            if (Objects.requireNonNull(recyclerView.getAdapter()).getItemCount() != 0) {
                if (!isUserListEnd) {
                    int lastVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findLastCompletelyVisibleItemPosition();
                    return lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1;
                }
            }
            return false;
        }
        return false;
    }

    @Override
    public void OnClicked(User user) {
        Intent intent = new Intent(mActivity, ActivityViewProfile.class);
        intent.putExtra("image", user.getProfile_pic());
        intent.putExtra("userId", user.getUser_id());
        intent.putExtra("from", "blockedList");
        startActivity(intent);
    }
}
