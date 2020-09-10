package com.blive.activity;

import android.annotation.SuppressLint;;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.widget.Button;

import com.blive.BLiveApplication;
import com.blive.fragment.FragmentGlobal24Hour;
import com.blive.fragment.FragmentGlobalHour;
import com.blive.fragment.FragmentGlobalWeek;
import com.blive.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ActivityLeaderBoard extends BaseBackActivity {

    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.b_receive)
    Button bReceive;
    @BindView(R.id.b_send)
    Button bSend;

    boolean isReceived = false, isSend = false, isClicked = false;
    FragmentGlobalHour fragmentGlobalHour = new FragmentGlobalHour();
    FragmentGlobal24Hour fragmentGlobal24Hour = new FragmentGlobal24Hour();
    FragmentGlobalWeek fragmentGlobalWeek = new FragmentGlobalWeek();

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        BLiveApplication.setCurrentActivity(this);
        initUI();
    }

    protected void initUI() {

        setTitle("Global");

        isReceived = true;
        bSend.setTextColor(getResources().getColor(R.color.black));
        bReceive.setTextColor(getResources().getColor(R.color.colorAccent));
        setupViewPagerReceive(viewPager);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPagerReceive(ViewPager viewPager) {

        Bundle bundle = new Bundle();
        bundle.putString("type", "Receive");

        fragmentGlobalHour.setArguments(bundle);
        fragmentGlobal24Hour.setArguments(bundle);
        fragmentGlobalWeek.setArguments(bundle);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(fragmentGlobalHour, "This hour");
        adapter.addFragment(fragmentGlobal24Hour, "24 Hour");
        adapter.addFragment(fragmentGlobalWeek, "This Week");
        viewPager.setAdapter(adapter);
    }

    private void setupViewPagerSend(ViewPager viewPager) {

        Bundle bundle = new Bundle();
        bundle.putString("type", "Send");

        fragmentGlobalHour.setArguments(bundle);
        fragmentGlobal24Hour.setArguments(bundle);
        fragmentGlobalWeek.setArguments(bundle);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(fragmentGlobalHour, "This hour");
        adapter.addFragment(fragmentGlobal24Hour, "24 Hour");
        adapter.addFragment(fragmentGlobalWeek, "This Week");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
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

    @OnClick(R.id.b_receive)
    public void onClickReceive() {

        if (!isReceived) {
            isReceived = true;
            isSend = false;
            if (!isClicked) {
                isClicked = true;
                setupViewPagerReceive(viewPager);
                viewPager.setOffscreenPageLimit(2);
                tabLayout.setupWithViewPager(viewPager);

                bReceive.setTextColor(getResources().getColor(R.color.colorAccent));
                bSend.setTextColor(getResources().getColor(R.color.black));
                isClicked = false;
            }
        }
    }

    @OnClick(R.id.b_send)
    public void onClickSend() {
        if (!isSend) {
            isSend = true;
            isReceived = false;
            if (!isClicked) {
                isClicked = true;
                setupViewPagerSend(viewPager);
                viewPager.setOffscreenPageLimit(2);
                tabLayout.setupWithViewPager(viewPager);

                bSend.setTextColor(getResources().getColor(R.color.colorAccent));
                bReceive.setTextColor(getResources().getColor(R.color.black));
                isClicked = false;
            }
        }
    }
}
