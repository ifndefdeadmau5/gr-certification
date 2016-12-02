package com.SeongMin.GoodProduct.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class DetailViewPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 2;
    private String titles[];

    public DetailViewPagerAdapter(FragmentManager fm, String[] titles2) {
        super(fm);
        titles = titles2;
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return DetailLeft.newInstance();
            case 1:
                return CompanyListFragment.newInstance();
        }
        return null;
    }

    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

}