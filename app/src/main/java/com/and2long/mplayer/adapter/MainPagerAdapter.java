package com.and2long.mplayer.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.and2long.mplayer.R;
import com.and2long.mplayer.base.App;

import java.util.ArrayList;

/**
 * Created by L on 2016/12/3.
 */

public class MainPagerAdapter extends FragmentPagerAdapter {
    //标题名
    private String[] tittles = App.appContext.getResources().getStringArray(R.array.tabs_tittle);
    private ArrayList<Fragment> fragments;

    public MainPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }


    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tittles[position];
    }
}
