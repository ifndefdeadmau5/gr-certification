package com.SeongMin.GoodProduct.fragment;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.SeongMin.GoodProduct.activity.CompanyDetailActivity;
import com.SeongMin.GoodProduct.activity.R;
import com.SeongMin.GoodProduct.global.db;
import com.SeongMin.GoodProduct.util.RecyclerUtils;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.malinskiy.superrecyclerview.swipe.SparseItemRemoveAnimator;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompanyListFragment extends Fragment {

    private static String QueryString;
    SuperRecyclerView mRecycler;
    ListViewAdapter mAdapter;
    Context thisContext = null;
    View mEmptyView;
    TextView EmptyText;
    private SparseItemRemoveAnimator mSparseAnimator;
    private Cursor mCursor;
    private db dbAdapter;
    private boolean isSearch;

    public CompanyListFragment() {
        // Required empty public constructor
    }

    public static CompanyListFragment newInstance(String QueryString) {
        CompanyListFragment f = new CompanyListFragment();
        f.isSearch = true;
        f.QueryString = QueryString;
        return f;
    }

    public static CompanyListFragment newInstance() {
        CompanyListFragment f = new CompanyListFragment();

        return f;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisContext = getActivity().getApplicationContext();
        mAdapter = new ListViewAdapter(getActivity().getApplication());
        fillList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.super_recycler_view, container, false);

        mEmptyView = rootView.findViewById(R.id.emptycontainer);
        EmptyText = (TextView) mEmptyView.findViewById(R.id.textView);
        EmptyText.setText(isSearch ? "검색결과가 없습니다" : "인증기업이 없습니다");
        mRecycler = (SuperRecyclerView) rootView.findViewById(R.id.list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(thisContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecycler.setLayoutManager(layoutManager);
        mRecycler.setPadding(0, 100, 0, 0);

        mRecycler.addOnItemTouchListener(new RecyclerUtils.RecyclerItemClickListener(getActivity().getApplicationContext(), new RecyclerUtils.RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getActivity().getApplicationContext(), "Clicked " + position, Toast.LENGTH_SHORT).show();
            }
        }));

        mSparseAnimator = new SparseItemRemoveAnimator();
        mRecycler.getRecyclerView().setItemAnimator(mSparseAnimator);


        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkAdapterIsEmpty();
            }
        });
        mRecycler.setAdapter(mAdapter);
        checkAdapterIsEmpty();

        return rootView;
    }

    private void checkAdapterIsEmpty() {
        if (mAdapter.getItemCount() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            Log.w("mAdapter", "Empty");
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }


    public void fillList() {
        dbAdapter = new db(getActivity().getApplicationContext());
        dbAdapter.open();
        if (isSearch) {
            mCursor = dbAdapter.SelectCompanyByCompanyname(QueryString);
        } else {
            mCursor = dbAdapter.SelectAllCompanyByCode(getActivity().getIntent().getStringExtra("standardcode")); ///////// 해당 표준번호 취득한 모든 업체

        }
        while (mCursor.moveToNext()) {
            mAdapter.addItem(mCursor.getString(0),
                    mCursor.getString(1),
                    mCursor.getString(2),
                    dbAdapter.getStandardCountByCompanyName(mCursor.getString(0)));
        }

        mCursor.close();
        dbAdapter.close();
    }

    public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.ListItemViewHolder> {
        private Context mContext = null;
        private ArrayList<Company> mListData = new ArrayList<Company>();

        public ListViewAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        @Override
        public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final ListItemViewHolder holder;
            View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gr_listview_item, parent, false);
            holder = new ListItemViewHolder(convertView);
            ImageButton imgButton = (ImageButton) convertView.findViewById(R.id.imgbutton);
            imgButton.setVisibility(View.INVISIBLE);

            TextView count = (TextView) convertView.findViewById(R.id.count);
            if (0 == mListData.get(holder.position).standardcount) {
                count.setVisibility(View.INVISIBLE);
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), CompanyDetailActivity.class);
                    intent.putExtra("companyname", mListData.get(holder.position).companyname);
                    intent.putExtra("representative", mListData.get(holder.position).representative);
                    intent.putExtra("address", mListData.get(holder.position).address);


                    startActivity(intent);
                    getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                }
            });

            return holder;
        }

        @Override
        public void onBindViewHolder(ListViewAdapter.ListItemViewHolder holder, int position) {
            String setCompanynameString = mListData.get(position).companyname;
            if (setCompanynameString.length() > 11) {
                setCompanynameString = mListData.get(position).companyname.substring(0, 10);
                setCompanynameString.concat("...");
            }

            holder.name.setText(setCompanynameString);
            holder.repre.setText(mListData.get(position).representative);
            holder.stdcnt.setText(String.valueOf(mListData.get(position).standardcount));
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

        public void addItem(String companyname, String representative, String address, int standardcount) {
            Company addInfo = new Company();
            addInfo.companyname = companyname;
            addInfo.representative = representative;
            addInfo.address = address;
            addInfo.standardcount = standardcount;

            mListData.add(addInfo);
        }

        public final class ListItemViewHolder extends RecyclerView.ViewHolder {
            public TextView name;
            public TextView repre;
            public TextView stdcnt;
            public int position;


            public ListItemViewHolder(View itemView) {
                super(itemView);

                name = (TextView) itemView.findViewById(R.id.stcode);
                name.setSingleLine();
                repre = (TextView) itemView.findViewById(R.id.stname);
                repre.setSingleLine();
                stdcnt = (TextView) itemView.findViewById(R.id.count);
            }
        }
    }

    public class Company {

        public String companyname;
        public String representative;
        public String address;
        public int standardcount;


        public Company() {

        }

        public Company(String companyname, String representative, String address, int standardcount) {
            this.companyname = companyname;
            this.representative = representative;
            this.address = address;
            this.standardcount = standardcount;
        }
    }
}
