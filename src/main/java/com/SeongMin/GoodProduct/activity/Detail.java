package com.SeongMin.GoodProduct.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.SeongMin.GoodProduct.fragment.DetailViewPagerAdapter;
import com.SeongMin.GoodProduct.ui.SlidingTabLayout;

public class Detail extends ActionBarActivity {
    ViewPager pager;
    SlidingTabLayout slidingTabLayout;
    private DetailViewPagerAdapter mAdapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grdetail);
        pager = (ViewPager) findViewById(R.id.viewpager);
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        String[] titles = {"                           " +
                "정보" +
                "                           ",
                "                        " +
                        "인증기업" +
                        "                        "};
        pager.setAdapter(mAdapter = new DetailViewPagerAdapter(getSupportFragmentManager(), titles));


        slidingTabLayout.setViewPager(pager);
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.WHITE;
            }
        });

        //페이저에 뷰페이저 어답터 연결

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }


    }
}
