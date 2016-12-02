package com.SeongMin.GoodProduct.activity;


import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.SeongMin.GoodProduct.fragment.CompanyListFragment;
import com.SeongMin.GoodProduct.fragment.FAQFragment;
import com.SeongMin.GoodProduct.fragment.GRListFragment;
import com.SeongMin.GoodProduct.fragment.HomeFragment;
import com.SeongMin.GoodProduct.fragment.IntroduceFragment;
import com.SeongMin.GoodProduct.fragment.LikedCompanyFragment;
import com.SeongMin.GoodProduct.fragment.LikedStandardFragment;
import com.SeongMin.GoodProduct.fragment.ViewPagerFragment;

import java.util.ArrayList;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by neokree on 18/01/15.
 */


public class SecondActivity extends MaterialNavigationDrawer {
    ArrayList<String> arraylist;
    SharedPreferences preference;
    private Toolbar toolbar;
    private Context thisContext;
    private SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        @Override
        public boolean onQueryTextSubmit(String query) {

            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {

            String targetString = preference.getString("searchTarget", "기본");
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (targetString.equals("표준")) {
                ft.replace(R.id.frame_container, GRListFragment.newInstance(newText)).commit();
            } else {
                ft.replace(R.id.frame_container, CompanyListFragment.newInstance(newText)).commit();
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisContext = this;
        preference = PreferenceManager.getDefaultSharedPreferences(this);
        arraylist = new ArrayList<String>();
        arraylist.add("표준");
        arraylist.add("업체");
        closeDrawer();
    }

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
        inflater.inflate(R.menu.spinnerview, menu); //
//        inflater.inflate(R.menu.menu_website, menu);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Log.i("i", "" + Build.VERSION.SDK_INT);
            Log.i("i", "" + Build.VERSION_CODES.HONEYCOMB_MR2);
            MenuItem searchItem = menu.findItem(R.id.menu_search);
            SearchView searchView = (SearchView) searchItem.getActionView();

            MenuItem spinnerItem = menu.findItem(R.id.spinner_nav);
            Spinner spinner = (Spinner) spinnerItem.getActionView();

         /*   MenuItem ButtonItem = menu.findItem(R.id.gotowebsite);
            ImageButton imgbutton =  (ImageButton)ButtonItem.getActionView();

            imgbutton.setImageResource(R.drawable.web);
            imgbutton.setBackgroundColor(getResources().getColor(R.color.transparent));
            imgbutton.setScaleType(ImageButton.ScaleType.FIT_END);


            if( null != imgbutton ){
                imgbutton.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri url = Uri.parse("http://www.gr.or.kr/index.php?");
                        Intent it = new Intent(Intent.ACTION_VIEW, url );
                        startActivity(it);
                    }
                });
            }
*/

            final SharedPreferences.Editor edit = preference.edit();

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_my_spinner, arraylist);
            if (null != spinner) {
                spinner.setAdapter(adapter);
//                spinner.setSelection(1);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int index, long arg3) {
                        edit.putString("searchTarget", arraylist.get(index));
                        edit.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
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
    public void init(Bundle savedInstanceState) {


        setUsername("GR인증현황 1.0");
        setUsernameTextColor(Color.parseColor("#FFFFFF"));

        this.setDrawerBackgroundColor(Color.parseColor("#FFFFFF"));
        // set the header image
        this.setDrawerHeaderImage(R.drawable.photo6);

        // create sections
        this.addSection(newSection("홈", R.drawable.ic_home_grey600_48dp, HomeFragment.newInstance()).setSectionColor(getResources().getColor(R.color.colorPrimary)));
        this.addSection(newSection("GR인증제도", R.drawable.web, IntroduceFragment.newInstance()).setSectionColor(getResources().getColor(R.color.colorPrimary)));
        this.addSection(newSection("분야별보기", R.drawable.ic_apps_grey600_48dp, ViewPagerFragment.newInstance()).setSectionColor(getResources().getColor(R.color.colorPrimary)));
        this.addSection(newSection("관심표준", R.drawable.ic_grade_grey600_36dp, LikedStandardFragment.newInstance()).setSectionColor(getResources().getColor(R.color.colorPrimary)));
        this.addSection(newSection("관심업체", R.drawable.ic_location_city_white_48dp, LikedCompanyFragment.newInstance()).setSectionColor(getResources().getColor(R.color.colorPrimary)));
        this.addSection(newSection("FAQ", R.drawable.ic_help_grey600_48dp, FAQFragment.newInstance()).setSectionColor(getResources().getColor(R.color.colorPrimary)));
        // create bottom section
        this.addBottomSection(newSection("설정", R.drawable.ic_settings_applications_grey600_48dp, new Intent(this, SettingsActivity.class)));

    }
}
