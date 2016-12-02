package com.SeongMin.GoodProduct.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.SeongMin.GoodProduct.activity.Detail;
import com.SeongMin.GoodProduct.activity.R;
import com.SeongMin.GoodProduct.global.db;
import com.SeongMin.GoodProduct.ui.ProgressBarCircular;
import com.SeongMin.GoodProduct.util.RecyclerUtils;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.malinskiy.superrecyclerview.swipe.SparseItemRemoveAnimator;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by SeongMin on 2015-01-20.
 */
public class GRListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static String QueryString;
    SuperRecyclerView mRecycler;
    ListViewAdapter mAdapter;
    Context thisContext = null;
    View mNetworkDisabledView;
    TextView NetworkText;
    String order;
    View mEmptyView;
    TextView EmptyText;
    ProgressBarCircular progress;
    private SparseItemRemoveAnimator mSparseAnimator;
    private int GRNumber;
    private db dbAdapter;
    private boolean isSearch;

    // 디폴트 생성자
    public GRListFragment() {

    }

    // ListFragment 인스턴스 생성 후 반환
    public static GRListFragment newInstance(int position) {
        GRListFragment f = new GRListFragment();
        f.GRNumber = position;
        f.isSearch = false;

        return f;
    }

    public static GRListFragment newInstance(String QueryString) {
        GRListFragment f = new GRListFragment();
        f.isSearch = true;
        f.QueryString = QueryString;
        return f;
    }

    public static InputStream getInputStreamFromUrl(String url) {
        InputStream contentStream = null;
        try {

            Log.i("my_tag", "getInputStreamFromUrl");
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(new HttpGet(url));
            contentStream = response.getEntity().getContent();
        } catch (Exception e) {
            Log.i("NETWORK_ERROR", "NETWORK DISABLED");

            e.printStackTrace();

        }

        return contentStream;
    }

    private void setOrder() {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getActivity());
        order = preference.getString("example_list", "soso");
    }


    private boolean isOnline() { // network 연결 상태 확인
        try {
            ConnectivityManager conMan = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo.State wifi = conMan.getNetworkInfo(1).getState(); // wifi
            if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
                return true;
            }

            NetworkInfo.State mobile = conMan.getNetworkInfo(0).getState(); // mobile ConnectivityManager.TYPE_MOBILE
            if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING) {
                return true;
            }

        } catch (NullPointerException e) {
            return false;
        }

        return false;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisContext = getActivity().getApplicationContext();
        mAdapter = new ListViewAdapter(getActivity().getApplication());
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        showNetworkStatus();
    }

    private void showNetworkStatus() {
        if (!isOnline()) {
            mNetworkDisabledView.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.GONE);
        } else {
            mNetworkDisabledView.setVisibility(View.GONE);
            setOrder();
            new JsonLoadingTask().execute();

        }
    }

    private void checkAdapterIsEmpty() {
        if (mAdapter.getItemCount() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            if (isSearch) {
                progress.setVisibility(View.GONE);
            } else {
                progress.setVisibility(View.VISIBLE);
            }
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    // 분야별코드에 따른 리스트 출력
    {
        View rootView = inflater.inflate(R.layout.super_recycler_view, container, false);
        mRecycler = (SuperRecyclerView) rootView.findViewById(R.id.list);
        mNetworkDisabledView = rootView.findViewById(R.id.networkdisabled);
        NetworkText = (TextView) mNetworkDisabledView.findViewById(R.id.networktext);

        NetworkText.setText("네트워크 연결 확인후 다시 시도해주세요\n아래로 당겨서 새로고침");

        mEmptyView = rootView.findViewById(R.id.emptycontainer);


        EmptyText = (TextView) mEmptyView.findViewById(R.id.textView);
        EmptyText.setText(isSearch ? "검색결과가 없습니다" : "");
        progress = (ProgressBarCircular) mEmptyView.findViewById(R.id.progress);
        progress.setBackgroundColor(getActivity().getResources().getColor(R.color.light_green_500));

        LinearLayoutManager layoutManager = new LinearLayoutManager(thisContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecycler.setLayoutManager(layoutManager);
        mRecycler.setPadding(0, 100, 0, 0);
        mRecycler.setAdapter(mAdapter);
        mRecycler.addOnItemTouchListener(new RecyclerUtils.RecyclerItemClickListener(getActivity().getApplicationContext(), new RecyclerUtils.RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        }));
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkAdapterIsEmpty();
            }
        });
        mRecycler.setAdapter(mAdapter);
        checkAdapterIsEmpty();

        mSparseAnimator = new SparseItemRemoveAnimator();
        mRecycler.getRecyclerView().setItemAnimator(mSparseAnimator);
        mRecycler.getSolidColor();
        mRecycler.setRefreshListener(this);
        mRecycler.setRefreshingColorResources(
                android.R.color.darker_gray,
                android.R.color.darker_gray,
                android.R.color.darker_gray,
                android.R.color.darker_gray);


        return rootView;
    }

    @Override
    public void onRefresh() {

        if (0 < mAdapter.getItemCount()) {
            mRecycler.hideProgress();
            mAdapter.notifyDataSetChanged();
            // 이상하게 hideProgress 만으로는 refresh 애니메이션이 사라지지 않음..
            // notifyDataSetchanged 를 꼭 호출해줘야함
            return;
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Log.i("itcnt", mAdapter.getItemCount() + " item");
                if (isOnline()) {
                    Log.i("itcnt", mAdapter.getItemCount() + " item but executed");
                    new JsonLoadingTask().execute();
                } else {
                    mRecycler.hideProgress();
                    mAdapter.notifyDataSetChanged();
                    showNetworkStatus();
                }

            }
        }, 2000);
        //mAdapter.closeAllExcept(null);
    }

    public String getJsonText() {
        StringBuffer sb = new StringBuffer();

        try {

            String GRNumberString = GRNumber < 10 ? "0" + GRNumber : String.valueOf(GRNumber);
            String URLString = null;

            if (isSearch) {
                URLString = "http://www.ibtk.kr/grStandard_api/206f2a77df6a26971d5088af8b791511?model_query_pageable="
                        + URLEncoder.encode("{'pageSize':222,'sortOrders':[{'property':'" + order + "','direction':1}]}")// 페이지 사이즈 222, 정렬은 옵션에 설정된 값으로 오름차순.
                        + "&model_query="
                        //+ URLEncoder.encode("{$or:[{'standardcode':{\"$regex\":'"+QueryString+"'}},{'standardname':{\"$regex\":'"+QueryString+"'}}]}")
                        + URLEncoder.encode("{$or:[{'standardcode':{\"$regex\":'" + QueryString + "'}},{'standardname':{\"$regex\":'" + QueryString + "'}}]}"); //표준명과 표준코드 둘 중에 QueryString 을 포함하는 필드를 가져온다

            } else {
                URLString = "http://www.ibtk.kr/grStandard_api/206f2a77df6a26971d5088af8b791511?model_query_pageable="
                        + URLEncoder.encode("{'pageSize':222,'sortOrders':[{'property':'" + order + "','direction':1}]}") // 페이지 사이즈 222, 정렬은 옵션에 설정된 값으로 오름차순.
                        + "&model_query="
                        + URLEncoder.encode("{'areacode':{'$regex':'GR" + GRNumberString + "'}}"); //분야별코드가 GR + "GRNumberString" 인 것
            }

            String line = getStringFromUrl(URLString);
            JSONObject object = new JSONObject(line);
            JSONArray Array = new JSONArray(object.getString("content"));

            dbAdapter = new db(getActivity());
            dbAdapter.open();


            for (int i = 0; i < Array.length(); i++) {
                JSONObject insideObject = Array.getJSONObject(i);
                int companyCount = dbAdapter.getCompanyCountByCode(insideObject.getString("standardcode")); ///////// 해당 표준번호 취득한 모든 업체

                mAdapter.addItem(insideObject.getString("standardcode"),
                        insideObject.getString("standardname"),
                        companyCount,
                        insideObject.getString("orddate"),
                        insideObject.getString("udtdate"),
                        insideObject.getString("attachfile"),
                        insideObject.getString("attachfileUrl"),
                        insideObject.getString("areacode")
                );

            } // for
            dbAdapter.close();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    } // getJsonText

    public String getStringFromUrl(String url)
            throws UnsupportedEncodingException {

        BufferedReader br = new BufferedReader(new InputStreamReader(
                getInputStreamFromUrl(url), "UTF-8"));

        StringBuffer sb = new StringBuffer();

        try {
            Log.i("my_tag", "getStringFromUrl");
            String line = null;


            while ((line = br.readLine()) != null) {
                sb.append(line);
                Log.i("my_tag", line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    } // getStringFromUrl

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("mytag", GRListFragment.this.toString() + "뷰가지워졌어염");
        mAdapter.mListData.clear();//Adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("mytag", GRListFragment.this.toString() + "쥬금");

    }

    /*
   * AsyncTask : UI 처리 및 Background 작업 등을 하나의 클래스에서 작업 할 수 있게 지원
   * onPreExecute : Bacground 작업 시작전에 UI 작업을 진행
   * doInBackground : Background 작업을 진행
   * onPostExecute : Bacground 작업 후에 UI 작업을 진행
   */
    private class JsonLoadingTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strs) {

            Log.i("Background task count", "+++");
            return getJsonText();
        }

        @Override
        protected void onPostExecute(String result) {

            Log.i("my_tag", result);
            mRecycler.hideProgress();
            mNetworkDisabledView.setVisibility(View.GONE);
            mAdapter.notifyDataSetChanged();
        }
    }

    public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.ListItemViewHolder> {
        private Context mContext = null;
        private ArrayList<GRListData> mListData = new ArrayList<GRListData>();

        public ListViewAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        @Override
        public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final ListItemViewHolder holder;

            View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gr_listview_item, parent, false);
            holder = new ListItemViewHolder(convertView);

            final ImageButton imgButton = (ImageButton) convertView.findViewById(R.id.imgbutton);


            imgButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Animation rotateAnimation = (Animation) AnimationUtils.loadAnimation(thisContext, R.anim.rotate);
                    imgButton.startAnimation(rotateAnimation);

                    dbAdapter = new db(getActivity().getApplicationContext());
                    dbAdapter.open();
                    if (!dbAdapter.isDuplicate(mListData.get(holder.position).stdCode)) {
                        dbAdapter.insertFavorite(
                                mListData.get(holder.position).areacode,
                                mListData.get(holder.position).stdCode,
                                mListData.get(holder.position).stdName,
                                mListData.get(holder.position).orddate,
                                mListData.get(holder.position).udtdate,
                                mListData.get(holder.position).attachfile,
                                mListData.get(holder.position).attachfileUrl,
                                mListData.get(holder.position).CompanyCount);
                        Toast.makeText(getActivity().getApplicationContext(), "\"" + holder.code.getText() + "\"" + "관심표준에 추가되었습니다", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "이미 추가된 항목입니다", Toast.LENGTH_SHORT).show();

                    }

                    dbAdapter.close();

                }
            });

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), Detail.class);
                    intent.putExtra("standardcode", mListData.get(holder.position).stdCode);
                    intent.putExtra("standardname", mListData.get(holder.position).stdName);
                    intent.putExtra("orddate", mListData.get(holder.position).orddate);
                    intent.putExtra("udtdate", mListData.get(holder.position).udtdate);
                    intent.putExtra("attachfile", mListData.get(holder.position).attachfile);
                    intent.putExtra("attachfileUrl", mListData.get(holder.position).attachfileUrl);
                    intent.putExtra("areacode", mListData.get(holder.position).areacode);
                    intent.putExtra("companycount", mListData.get(holder.position).CompanyCount);


                    startActivity(intent);
                    getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                }
            });

            return holder;
        }

        @Override
        public void onBindViewHolder(ListViewAdapter.ListItemViewHolder holder, int position) {
            holder.code.setText(mListData.get(position).stdCode);
            holder.name.setText(mListData.get(position).stdName);
            holder.count.setText(String.valueOf(mListData.get(position).CompanyCount));
            if (0 == mListData.get(position).CompanyCount)
                holder.count.setVisibility(View.INVISIBLE);
            else {
                holder.count.setVisibility(View.VISIBLE);
            }
            holder.position = position;


        }

        @Override
        public int getItemCount() {
            return mListData.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void addItem(String stdcode, String stdname, int companycount, String orddate, String udtdate, String attachfile, String attachfileUrl, String areacode) {
            GRListData addInfo = new GRListData();
            addInfo.areacode = areacode;
            addInfo.stdCode = stdcode;
            addInfo.stdName = stdname;
            addInfo.CompanyCount = companycount;
            addInfo.orddate = orddate;
            addInfo.udtdate = udtdate;
            addInfo.attachfile = attachfile;
            addInfo.attachfileUrl = attachfileUrl;

            mListData.add(addInfo);
        }

        public final class ListItemViewHolder extends RecyclerView.ViewHolder {
            public TextView code;
            public TextView name;
            public TextView count;
            public ImageButton imgButton;
            public int position;


            public ListItemViewHolder(View itemView) {
                super(itemView);

                code = (TextView) itemView.findViewById(R.id.stcode);
                name = (TextView) itemView.findViewById(R.id.stname);
                count = (TextView) itemView.findViewById(R.id.count);
                imgButton = (ImageButton) itemView.findViewById(R.id.imgbutton);


            }
        }
    }
}

