package com.blive.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blive.activity.ActivitySearch;
import com.blive.adapter.AdapterNotification;
import com.blive.model.GenericResponse;
import com.blive.model.Notification;
import com.blive.model.NotificationResponse;
import com.blive.R;
import com.blive.model.UsersResponse;
import com.blive.service.ApiClientBase;
import com.blive.service.ApiInterface;
import com.blive.session.SessionManager;
import com.blive.session.SessionUser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sans on 14-08-2018.
 **/

public class FragmentHome extends BaseFragment implements AdapterNotification.Listener {

    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.notification_image)
    ImageView ivNotification;
    @BindView(R.id.rv_notification)
    RecyclerView rvNotifications;
    @BindView(R.id.tv_notification)
    TextView tvNotifications;
    @BindView(R.id.clearAllNotification)
    Button bClearAllNotification;
    @BindView(R.id.iv_search)
    ImageView ivSearch;

    Dialog alertDialog;
    ActionBarDrawerToggle actionBarDrawerToggle;
    private FragmentLive fragmentLive;
    private ArrayList<Notification> notificationListData;
    private String notificationUserId = "";
    private boolean refreshNotification = false;
    SessionManager sessionManager;
    private TextView tv_count;

    public FragmentHome() {
    }

    @SuppressLint("ValidFragment")
    public FragmentHome(String userId) {
        this.notificationUserId = userId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void callGetUserData(String notificationUserId) {
        if (utils.isNetworkAvailable()) {
            utils.showProgress();
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<UsersResponse> call = apiClient.getUserData(SessionUser.getUser().getUser_id(), notificationUserId);
            call.enqueue(new retrofit2.Callback<UsersResponse>() {
                @Override
                public void onResponse(@NonNull Call<UsersResponse> call, @NonNull Response<UsersResponse> response) {
                    utils.hideProgress();
                    UsersResponse usersResponse = response.body();
                    if (response.code() == 200) {
                        if (usersResponse != null) {
                            if (usersResponse.getStatus().equalsIgnoreCase("success")) {
                                if (usersResponse.getData().getUser().getStatus().equalsIgnoreCase("INACTIVE")) {
                                    showToast("Currently " + usersResponse.getData().getUser().getName() + " is Offline!");
                                } else {
                                    if (!usersResponse.getData().getUser().getBroadcast_type().isEmpty())
                                        fragmentLive.moveNotificationUserToLive(usersResponse.getData().getUser());
                                    else
                                        showToast("Currently " + usersResponse.getData().getUser().getName() + " is Offline!");
                                }
                            } else {
                                showToast("Currently the user is Offline!");
                            }
                        } else {
                            utils.showToast(getString(R.string.server_error));
                        }
                    } else {
                        checkResponseCode(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UsersResponse> call, @NonNull Throwable t) {
                    showToast(t.getMessage());
                }
            });
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI();
    }

    private void initUI() {
        if (!notificationUserId.isEmpty()) {
            callGetUserData(notificationUserId);
        }
        drawerLayout.addDrawerListener(actionBarDrawerToggle);


        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {
                Log.d(TAG, "onDrawerSlide: ");
                sessionManager.storeSessionStringvalue("livenotify","livenotify","0");

            }

            @Override
            public void onDrawerOpened(@NonNull View view) {
                sessionManager.storeSessionStringvalue("livenotify","livenotify","0");
                Log.d(TAG, "onDrawerOpened: ");

            }

            @Override
            public void onDrawerClosed(@NonNull View view) {
                Log.d(TAG, "onDrawerClosed: ");
                sessionManager.storeSessionStringvalue("livenotify","livenotify","0");

            }

            @Override
            public void onDrawerStateChanged(int i) {
                Log.d(TAG, "onDrawerStateChanged: ");

            }
        });

        alertDialog = new Dialog(mActivity);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.alert_menu);
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LinearLayout llSearch = alertDialog.findViewById(R.id.ll_search);
        LinearLayout llAll = alertDialog.findViewById(R.id.ll_all);
        LinearLayout llSolo = alertDialog.findViewById(R.id.ll_solo);
        LinearLayout llMulti = alertDialog.findViewById(R.id.ll_multi);
        LinearLayout llScreenSharing = alertDialog.findViewById(R.id.ll_screenSharing);
        LinearLayout llKaraoke = alertDialog.findViewById(R.id.ll_karaoke);

        llSearch.setOnClickListener(v -> {
            alertDialog.dismiss();
            changeActivity(ActivitySearch.class);
            Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        });

        llAll.setOnClickListener(v -> {
            alertDialog.dismiss();
            fragmentLive.getActiveUsers(1, "all");
        });

        llSolo.setOnClickListener(v -> {
            alertDialog.dismiss();
            fragmentLive.getActiveUsers(1, "solo");
        });

        llMulti.setOnClickListener(v -> {
            alertDialog.dismiss();
            fragmentLive.getActiveUsers(1, "group");
        });

        llScreenSharing.setOnClickListener(v -> {
            alertDialog.dismiss();
            fragmentLive.getActiveUsers(1, "screenSharing");
        });

        llKaraoke.setOnClickListener(v -> {
            alertDialog.dismiss();
            fragmentLive.getActiveUsers(1, "pk");
            /*Toast.makeText(mActivity, "Coming Soon...", Toast.LENGTH_SHORT).show();*/
        });

        setupViewPager(viewPager);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position != 0)
                    ivSearch.setVisibility(View.GONE);
                else
                    ivSearch.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        setDataToNotification();
    }

    @OnClick(R.id.iv_search)
    public void onClickedSearch() {
        alertDialog.show();
    }

    public void setDataToNotification() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        rvNotifications.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        rvNotifications.setVisibility(View.GONE);
        tvNotifications.setVisibility(View.GONE);

        callNotificationsAPI();
    }

    private void callNotificationsAPI() {
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<NotificationResponse> call = apiClient.getNotifications(SessionUser.getUser().getUser_id());
            call.enqueue(new retrofit2.Callback<NotificationResponse>() {
                @Override
                public void onResponse(@NonNull Call<NotificationResponse> call, @NonNull Response<NotificationResponse> response) {
                    NotificationResponse notificationResponse = response.body();
                    if (response.code() == 200) {
                        if (notificationResponse != null) {
                            if (notificationResponse.getStatus().equalsIgnoreCase("success")) {
                                setNotifications(notificationResponse.getData().getNotifications());
                            } else {
                                rvNotifications.setVisibility(View.GONE);
                                tvNotifications.setVisibility(View.VISIBLE);
                                bClearAllNotification.setVisibility(View.GONE);
                                showToast(notificationResponse.getMessage());
                            }
                        } else {
                            rvNotifications.setVisibility(View.GONE);
                            tvNotifications.setVisibility(View.VISIBLE);
                            bClearAllNotification.setVisibility(View.GONE);
                            utils.showToast(getString(R.string.server_error));
                        }
                    } else {
                        checkResponseCode(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<NotificationResponse> call, @NonNull Throwable t) {
                    showToast(t.getMessage());
                }
            });
        }
    }

    @OnClick(R.id.clearAllNotification)
    public void clearNotification() {
        if (notificationListData.size() > 0) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<GenericResponse> call = apiClient.clearNotification(SessionUser.getUser().getUser_id());
            call.enqueue(new retrofit2.Callback<GenericResponse>() {
                @Override
                public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                    GenericResponse genericResponse = response.body();
                    if (response.code() == 200) {
                        if (genericResponse != null) {
                            if (genericResponse.getStatus().equalsIgnoreCase("success")) {
                                rvNotifications.setVisibility(View.GONE);
                                tvNotifications.setVisibility(View.VISIBLE);
                                notificationListData.clear();
                                showToast(genericResponse.getData().getMessage());
                            } else {
                                showToast(genericResponse.getMessage());
                            }
                        } else {
                            utils.showToast(getString(R.string.server_error));
                        }
                    } else {
                        checkResponseCode(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                    showToast(t.getMessage());
                }
            });
        } else {
            showToast("No Notifications Available");
        }
    }

    @OnClick(R.id.rl_notification)
    public void onNotificationIconClicked() {
        if (!drawerLayout.isDrawerOpen(navigationView)){
            drawerLayout.openDrawer(Gravity.END);
         sessionManager.storeSessionStringvalue("livenotify","livenotify","0");
        }
        else{
            drawerLayout.closeDrawer(Gravity.END);

        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview=inflater.inflate(R.layout.fragment_home, container, false);
        sessionManager=new SessionManager(getActivity());



        tv_count=rootview.findViewById(R.id.tv_count);
        drawerLayout=rootview.findViewById(R.id.drawer_layout);



        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                if (sessionManager.getSessionStringValue("livenotify","livenotify").equalsIgnoreCase("0")){
                    tv_count.setVisibility(View.GONE);
                }else {
                    tv_count.setVisibility(View.VISIBLE);

                }


                handler.postDelayed(this, 3000);
            }
        };
        handler.postDelayed(runnable, 3000);
        return rootview;
    }

    private void setupViewPager(ViewPager viewPager) {

        fragmentLive = new FragmentLive();

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(fragmentLive, "Live Now");
        adapter.addFragment(new FragmentUniverse(), "Universe");
        adapter.addFragment(new FragmentVideo(), "Videos");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void OnClicked(Notification notification) {
        if (utils.isNetworkAvailable()) {
            if (!notification.getUser_id().isEmpty()) {
                callGetUserData(notification.getUser_id());
            } else {
                showToast("User not online!");
            }
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void setNotifications(ArrayList<Notification> notifications) {
        refreshNotification = false;
        notificationListData = notifications;
        if (notifications.size() > 0) {
            AdapterNotification adapterNotification = new AdapterNotification(getActivity(), notifications);
            rvNotifications.setAdapter(adapterNotification);
            adapterNotification.setOnClickListener(this);
            rvNotifications.setVisibility(View.VISIBLE);
            tvNotifications.setVisibility(View.GONE);
            bClearAllNotification.setVisibility(View.VISIBLE);
            int count= Integer.parseInt(sessionManager.getSessionStringValue("livenotify","livenotify"));
            count=count+1;
            sessionManager.storeSessionStringvalue("livenotify","livenotify", String.valueOf(count));
        } else {
            rvNotifications.setVisibility(View.GONE);
            tvNotifications.setVisibility(View.VISIBLE);
            bClearAllNotification.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void refreshNotification(String message) {

        if (message.equals("homeTab")) {
            Objects.requireNonNull(tabLayout.getTabAt(0)).select();
        } else if (message.equalsIgnoreCase("searchHide")) {
            ivSearch.setVisibility(View.GONE);
            ivNotification.setVisibility(View.GONE);
            if (drawerLayout.isDrawerOpen(navigationView))
                drawerLayout.closeDrawer(Gravity.END);
            drawerLayout.setVisibility(View.GONE);
        } else if (message.equalsIgnoreCase("searchShow")) {
            ivSearch.setVisibility(View.VISIBLE);
            ivNotification.setVisibility(View.VISIBLE);
            drawerLayout.setVisibility(View.VISIBLE);
        } else {
            refreshNotification = true;
            callNotificationsAPI();
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
}