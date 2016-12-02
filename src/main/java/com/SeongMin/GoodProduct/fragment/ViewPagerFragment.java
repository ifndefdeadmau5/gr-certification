package com.SeongMin.GoodProduct.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.SeongMin.GoodProduct.activity.R;
import com.SeongMin.GoodProduct.ui.SlidingTabLayout;

public class ViewPagerFragment extends Fragment {


    ViewPager pager;
    SlidingTabLayout slidingTabLayout;
    private ViewPagerAdapter mAdapter;
    private String titles[];


    public static ViewPagerFragment newInstance() {
        ViewPagerFragment f = new ViewPagerFragment();

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titles = getResources().getStringArray(R.array.planets_array);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_view_pager, container, false);
        pager = (ViewPager) rootView.findViewById(R.id.viewpager);
        slidingTabLayout = (SlidingTabLayout) rootView.findViewById(R.id.sliding_tabs);

        pager.setAdapter(mAdapter = new ViewPagerAdapter(getChildFragmentManager(), titles));

        slidingTabLayout.setViewPager(pager);
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.WHITE;
            }
        });
        //페이저에 뷰페이저 어답터 연결

        return rootView;
    }


}