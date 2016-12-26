package com.and2long.mplayer.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.and2long.mplayer.R;
import com.and2long.mplayer.adapter.MainPagerAdapter;
import com.and2long.mplayer.service.AudioPlayService;
import com.and2long.mplayer.ui.fragment.AudioFragment;
import com.and2long.mplayer.ui.fragment.VideoFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tablayout)
    TabLayout mTabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        ArrayList<Fragment> list = new ArrayList<>();
        VideoFragment videoFragment = new VideoFragment();
        AudioFragment musicFragment = new AudioFragment();
        list.add(videoFragment);
        list.add(musicFragment);

        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), list);
        mViewPager.setAdapter(mainPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, AudioPlayService.class);
        stopService(intent);
    }
}
