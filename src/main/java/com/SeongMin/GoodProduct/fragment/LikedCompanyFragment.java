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
import android.widget.TextView;
import android.widget.Toast;

import com.SeongMin.GoodProduct.activity.CompanyDetailActivity;
import com.SeongMin.GoodProduct.activity.R;
import com.SeongMin.GoodProduct.global.db;
import com.SeongMin.GoodProduct.recycler.CompanySwipeAdapter;
import com.SeongMin.GoodProduct.util.RecyclerUtils;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.malinskiy.superrecyclerview.swipe.SparseItemRemoveAnimator;
import com.malinskiy.superrecyclerview.swipe.SwipeDismissRecyclerViewTouchListener;
import com.malinskiy.superrecyclerview.swipe.SwipeItemManagerInterface;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class LikedCompanyFragment extends Fragment implements SwipeDismissRecyclerViewTouchListener.DismissCallbacks {

    SuperRecyclerView mRecycler;
    CompanySwipeAdapter mAdapter;
    Context thisContext = null;
    View mEmptyView;
    TextView EmptyText;
    private SparseItemRemoveAnimator mSparseAnimator;
    private db dbAdapter;
    private Cursor mCursor;

    public LikedCompanyFragment() {
        // Required empty public constructor
    }

    public static LikedCompanyFragment newInstance() {
        LikedCompanyFragment f = new LikedCompanyFragment();

        return f;
    }


    @Override
    public boolean canDismiss(int i) {
        return false;
    }

    @Override
    public void onDismiss(RecyclerView recyclerView, int[] reverseSortedPositions) {
        for (int position : reverseSortedPositions) {
            mSparseAnimator.setSkipNext(true);
            mAdapter.remove(position);
            mAdapter.notifyDataSetChanged();
            checkAdapterIsEmpty();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dbAdapter = new db(getActivity().getApplicationContext());
        dbAdapter.open();


        /* 관심표준 DB 로딩 */
        mCursor = dbAdapter.SelectAllLikedCompany();

        final ArrayList<CompanyData> list = new ArrayList<CompanyData>();


        while (mCursor.moveToNext()) {
            list.add(new CompanyData(mCursor.getString(0),
                    mCursor.getString(1),
                    mCursor.getString(2),
                    dbAdapter.getStandardCountByCompanyName(mCursor.getString(0))
            ));
            Log.i("??", mCursor.getInt(4) + " ");

        }

        dbAdapter.close();
        mAdapter = new CompanySwipeAdapter(list);

        View rootView = inflater.inflate(R.layout.super_recycler_view, container, false);

        mRecycler = (SuperRecyclerView) rootView.findViewById(R.id.list);
        mRecycler.setLayoutManager(new LinearLayoutManager(thisContext));
        mRecycler.setAdapter(mAdapter);
        mRecycler.hideMoreProgress();
        mAdapter.setMode(SwipeItemManagerInterface.Mode.Single); // 스와이프 여러개 or 한개 설정
        mRecycler.addOnItemTouchListener(new RecyclerUtils.RecyclerItemClickListener(getActivity().getApplicationContext(), new RecyclerUtils.RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getActivity().getApplicationContext(), "Clicked " + position, Toast.LENGTH_SHORT).show();
                TextView txt = (TextView) view.findViewById(R.id.code);

                Intent intent = new Intent(getActivity().getApplicationContext(), CompanyDetailActivity.class);
                intent.putExtra("companyname", list.get(position).compname);
                intent.putExtra("representative", list.get(position).repre);
                intent.putExtra("address", list.get(position).address);
                intent.putExtra("stdcount", list.get(position).stdcount);

                startActivity(intent);
            }
        }));
        mEmptyView = rootView.findViewById(R.id.emptycontainer);
        EmptyText = (TextView) mEmptyView.findViewById(R.id.textView);
        EmptyText.setText("관심업체가 없습니다");

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkAdapterIsEmpty();
            }
        });
        checkAdapterIsEmpty();


        mRecycler.setupSwipeToDismiss(this);
        mSparseAnimator = new SparseItemRemoveAnimator();
        mRecycler.getRecyclerView().setItemAnimator(mSparseAnimator);

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

}
