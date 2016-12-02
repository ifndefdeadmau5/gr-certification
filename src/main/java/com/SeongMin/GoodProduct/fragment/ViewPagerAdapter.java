package com.SeongMin.GoodProduct.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 17;
    private String titles[];

    public ViewPagerAdapter(FragmentManager fm, String[] titles2) {
        super(fm);
        titles = titles2;
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public Fragment getItem(int position) {
        Log.i("mytag", "0번으로" + position);
        return GRListFragment.newInstance(position + 1);
    }

    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

}