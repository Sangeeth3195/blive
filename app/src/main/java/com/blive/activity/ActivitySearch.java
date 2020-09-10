package com.blive.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.blive.adapter.AdapterSearch;
import com.blive.BLiveApplication;
import com.blive.model.FollowResponse;
import com.blive.model.User;
import com.blive.R;
import com.blive.model.UsersResponse;
import com.blive.service.ApiClientBase;
import com.blive.service.ApiInterface;
import com.blive.session.SessionUser;
import com.blive.utils.Utils;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public class ActivitySearch extends BaseBackActivity implements AdapterSearch.Listener {

    @BindView(R.id.rl_back)
    RelativeLayout rlBack;
    RelativeLayout rlFilter;
    TextView tvNoUsers;

    private RecyclerView rvSearch;
    private String queryText = "", filterMethod = "no", filterType = "";
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<User> users, tempUser;
    private AdapterSearch adapterSearch;
    private boolean isUserListEnd = false, isRefreshing = false, isAPICalled = false, isClicked = false;;
    private int page = 1, lastPage = 0;
    Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        utils = new Utils(this);
        rlFilter = findViewById(R.id.rl_filter_data);
        BLiveApplication.setCurrentActivity(this);
        changeStatusBarColor();
        initUI();
    }

    private void initUI() {

        rvSearch = findViewById(R.id.rv_search);
        tvNoUsers = findViewById(R.id.tv_no_users);
        SearchView searchView = findViewById(R.id.sv);

        users = new ArrayList<>();
        tempUser = new ArrayList<>();
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvSearch.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        tempUser.add(null);
        adapterSearch = new AdapterSearch(this, users);
        adapterSearch.setOnClickListener(this);
        rvSearch.setAdapter(adapterSearch);
        rvSearch.setVisibility(View.VISIBLE);
        tvNoUsers.setVisibility(View.GONE);

        searchView.setIconified(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e("", "onQueryTextSubmit: " + query);
                queryText = query;
                callSearchAPI(queryText, filterMethod, filterType, 1);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                queryText = newText;
                if (queryText.isEmpty()) {
                    callSearchAPI(queryText, filterMethod, filterType, 1);
                }
                return false;
            }
        });

        rvSearch.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (!isRefreshing) {
                if (!isUserListEnd) {
                    if (isLastItemDisplaying()) {
                        if (page < lastPage) {
                            page = page + 1;
                            callSearchAPI(queryText, filterMethod, filterType, page);
                        }
                    }
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            page = 1;
            isRefreshing = true;
            isUserListEnd = false;
            callSearchAPI(queryText, "no", "", page);
            swipeRefreshLayout.setRefreshing(false);
        });

        rlFilter.setOnClickListener(v -> onClickFilter());

        callSearchAPI(queryText, "no", "", page);
    }

    private boolean isLastItemDisplaying() {
        if (adapterSearch != null) {
            if (Objects.requireNonNull(rvSearch.getAdapter()).getItemCount() != 0) {
                if (!isUserListEnd) {
                    int lastVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull(rvSearch.getLayoutManager())).findLastCompletelyVisibleItemPosition();
                    return lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == rvSearch.getAdapter().getItemCount() - 1;
                }
            }
            return false;
        }
        return false;
    }

    @OnClick(R.id.rl_back)
    public void onClickBack() {
        finish();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    @Override
    public void onResume() {
        isClicked = false;
        super.onResume();
    }

    public void onClickFilter() {
        Log.e("", "onClickFilter:");
        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        View view = layoutInflater.inflate(R.layout.pop_up_filter, null);
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(ActivitySearch.this);
        alertDialogBuilder.setView(view);
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        RadioButton rbMale = view.findViewById(R.id.rb_male);
        RadioButton rbFemale = view.findViewById(R.id.rb_female);
        RadioButton rbCountry = view.findViewById(R.id.rb_secret);

        if (filterType.equalsIgnoreCase("Male"))
            rbMale.setChecked(true);
        else if (filterType.equalsIgnoreCase("Female"))
            rbFemale.setChecked(true);

        rbMale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                filterMethod = "gender";
                filterType = "Male";
                callSearchAPI(queryText, filterMethod, filterType, 1);
                alertDialog.dismiss();
            }
        });

        rbFemale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                filterMethod = "gender";
                filterType = "Female";
                callSearchAPI(queryText, filterMethod, filterType, 1);
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void callSearchAPI(String query, String filterMethod, String filterValue, int page) {
        if (!isAPICalled) {
            isAPICalled = true;
            if (utils.isNetworkAvailable()) {
                if (page > 1) {
                    adapterSearch.update(tempUser);
                } else {
                    swipeRefreshLayout.setRefreshing(true);
                }
                final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
                Call<UsersResponse> call = apiClient.search(SessionUser.getUser().getUser_id(), String.valueOf(page), query, filterMethod, filterValue);
                call.enqueue(new retrofit2.Callback<UsersResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<UsersResponse> call, @NonNull Response<UsersResponse> response) {
                        UsersResponse usersResponse = response.body();
//                        Log.e(TAG, "onResponse: "+ response.body());
                        if (response.code() == 200) {
                            if (usersResponse != null) {
                                if (usersResponse.getStatus().equalsIgnoreCase("success")) {
                                    lastPage = usersResponse.getData().getLast_page();
                                    isAPICalled = false;
                                    isRefreshing = false;
                                    if (page == 1) {
                                        swipeRefreshLayout.setRefreshing(false);
                                        if (usersResponse.getData().getUsers().size() > 0) {
                                            rvSearch.setVisibility(View.VISIBLE);
                                            tvNoUsers.setVisibility(View.GONE);
                                            adapterSearch.refresh(usersResponse.getData().getUsers());
                                        } else {
                                            users.clear();
                                            rvSearch.setVisibility(View.GONE);
                                            tvNoUsers.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        adapterSearch.removeLastItem();
                                        if (page == lastPage)
                                            isUserListEnd = true;
                                        adapterSearch.update(usersResponse.getData().getUsers());
                                    }
                                } else {
                                    if (swipeRefreshLayout.isRefreshing())
                                        swipeRefreshLayout.setRefreshing(false);
                                    showToast(usersResponse.getMessage());
                                    rvSearch.setVisibility(View.GONE);
                                    tvNoUsers.setVisibility(View.VISIBLE);
                                }
                            } else {
                                utils.showToast(getString(R.string.server_error));
                                rvSearch.setVisibility(View.GONE);
                                tvNoUsers.setVisibility(View.VISIBLE);
                            }
                        } else {
                            checkResponseCode(response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<UsersResponse> call, @NonNull Throwable t) {
                        showToast(t.getMessage());
                        if (swipeRefreshLayout.isRefreshing())
                            swipeRefreshLayout.setRefreshing(false);
                        else if (adapterSearch.getItemCount() > 0)
                            adapterSearch.removeLastItem();
                        rvSearch.setVisibility(View.GONE);
                        tvNoUsers.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        super.onBackPressed();
    }

    @Override
    public void OnClicked(User user) {
        if (!isClicked) {
            isClicked = true;
            if (!user.getUser_id().equalsIgnoreCase(SessionUser.getUser().getUser_id())) {
                Intent intent = new Intent(ActivitySearch.this, ActivityViewProfile.class);
                intent.putExtra("image", user.getProfile_pic());
                intent.putExtra("userId", user.getUser_id());
                intent.putExtra("from", "search");
                startActivity(intent);
            }
        }
    }

    @Override
    public void OnClickedFollow(User user,int adapterPosition) {
        if(!isClicked){
            isClicked = true;
            if (!user.getUser_id().equalsIgnoreCase(SessionUser.getUser().getUser_id())) {
                callFollowAPI(user.getUser_id(), adapterPosition);
            }
        }
    }

    private void callFollowAPI(String userId,final int adapterPosition) {
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Log.e(TAG, "callFollowAPI: "+ userId + SessionUser.getUser().getUser_id() );
            Call<FollowResponse> call = apiClient.follow("follow", userId, SessionUser.getUser().getUser_id());
            call.enqueue(new retrofit2.Callback<FollowResponse>() {
                @Override
                public void onResponse(@NonNull Call<FollowResponse> call, @NonNull Response<FollowResponse> response) {
                    isClicked = false;
                    FollowResponse followResponse = response.body();
                    if (response.code() == 200) {
                        if (followResponse != null) {
                            if (followResponse.getStatus().equalsIgnoreCase("success")) {
                                showToast(followResponse.getData().getMessage());
                                adapterSearch.updateFolowStatus(adapterPosition);
                            } else {
                                showToast(followResponse.getMessage());
                            }
                        } else {
                            utils.showToast(getString(R.string.server_error));
                        }
                    } else {
                        checkResponseCode(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<FollowResponse> call, @NonNull Throwable t) {
                    showToast(t.getMessage());
                    isClicked = false;
                }
            });
        }
    }
}