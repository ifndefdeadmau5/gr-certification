package com.SeongMin.GoodProduct.activity;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.SeongMin.GoodProduct.fragment.GRListFragment;
import com.SeongMin.GoodProduct.fragment.LikedCompanyFragment;
import com.SeongMin.GoodProduct.fragment.LikedStandardFragment;
import com.SeongMin.GoodProduct.fragment.ViewPagerFragment;


public class MainActivity extends ActionBarActivity {

    // 슬라이딩탭 레이아웃과 뷰페이저는 한 셋트로 사용해

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ListView mDrawerList;
    private Toolbar toolbar;
    private SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        @Override
        public boolean onQueryTextSubmit(String query) {

            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            FragmentManager fragmentManager = getSupportFragmentManager();

            //fragmentManager.beginTransaction().replace(R.id.container, SearchFragment.newInstance(newText)).commit();
            fragmentManager.beginTransaction().replace(R.id.container, GRListFragment.newInstance(newText)).commit();
            fragmentManager.beginTransaction().commitAllowingStateLoss();
            return false;
        }
    };

    /*
     *
     *  Toolbar 에 SearchView 를 추가
     *
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.searchview, menu); //

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Log.i("i", "" + Build.VERSION.SDK_INT);
            Log.i("i", "" + Build.VERSION_CODES.HONEYCOMB_MR2);
            MenuItem searchItem = menu.findItem(R.id.menu_search);
            SearchView searchView = (SearchView) searchItem.getActionView();
            if (null != searchView) {
                searchView.setQueryHint("ex)GR M 3006");
                searchView.setOnQueryTextListener(queryTextListener);
                SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
                if (null != searchManager) {
                    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
                }
                searchView.setIconifiedByDefault(true);
            }

        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /* 리소스 아이디로 모든 뷰 얻어옴 */
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.navdra);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_ab_drawer);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_close, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(drawerToggle);
        String[] values = new String[]{
                "Category", "Favorite", "Android", "Settings..."
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        mDrawerList.setAdapter(adapter);

        //mDrawerList.setBackgroundColor(getResources().getColor(R.color.blue));
        //toolbar.setBackgroundColor(getResources().getColor(R.color.blue));

        /*서랍
        *       목록
        *               편집
        *                       ^^
        *
        * */
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                switch (position) {
                    case 0:
                        mDrawerLayout.closeDrawer(Gravity.START);
                        fragmentManager.beginTransaction().replace(R.id.container, ViewPagerFragment.newInstance()).commit();
                        break;

                    case 1:
                        mDrawerLayout.closeDrawer(Gravity.START);
                        fragmentManager.beginTransaction().replace(R.id.container, LikedStandardFragment.newInstance()).commit();
                        fragmentManager.beginTransaction().commitAllowingStateLoss();
                        break;
                    case 2:
                        fragmentManager.beginTransaction().replace(R.id.container, LikedCompanyFragment.newInstance()).commit();
                        fragmentManager.beginTransaction().commitAllowingStateLoss();

                        mDrawerLayout.closeDrawer(Gravity.START);

                        break;
                    case 3:
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);

                        startActivity(intent);

                        mDrawerLayout.closeDrawer(Gravity.START);

                        break;
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(Gravity.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

}
