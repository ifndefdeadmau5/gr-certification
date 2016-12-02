package com.SeongMin.GoodProduct.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.SeongMin.GoodProduct.activity.Detail;
import com.SeongMin.GoodProduct.activity.R;
import com.SeongMin.GoodProduct.global.db;
import com.SeongMin.GoodProduct.recycler.SwipeAdapter;
import com.SeongMin.GoodProduct.util.RecyclerUtils;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.malinskiy.superrecyclerview.swipe.SparseItemRemoveAnimator;
import com.malinskiy.superrecyclerview.swipe.SwipeDismissRecyclerViewTouchListener;
import com.malinskiy.superrecyclerview.swipe.SwipeItemManagerInterface;

import java.util.ArrayList;

public class LikedStandardFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, OnMoreListener, SwipeDismissRecyclerViewTouchListener.DismissCallbacks {
    SuperRecyclerView mRecycler;
    SwipeAdapter mAdapter;
    Context thisContext = null;
    View mEmptyView;
    TextView EmptyText;
    private SparseItemRemoveAnimator mSparseAnimator;
    private db dbAdapter;
    private Cursor mCursor;

    public static LikedStandardFragment newInstance() {
        LikedStandardFragment f = new LikedStandardFragment();

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisContext = getActivity().getApplicationContext();


        init();

    }

    private void init() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.super_recycler_view, container, false);

        dbAdapter = new db(getActivity().getApplicationContext());
        dbAdapter.open();


        /* 관심표준 DB 로딩 */
        mCursor = dbAdapter.selectAllFavorite();

        final ArrayList<GRListData> list = new ArrayList<GRListData>();
        mAdapter = new SwipeAdapter(list);

        while (mCursor.moveToNext()) {
            list.add(new GRListData(mCursor.getString(0),
                    mCursor.getString(1),
                    mCursor.getString(2),
                    mCursor.getString(3),
                    mCursor.getString(4),
                    mCursor.getString(5),
                    mCursor.getString(6),
                    mCursor.getInt(7)
            ));
        }

        dbAdapter.close();


        mEmptyView = rootView.findViewById(R.id.emptycontainer);
        EmptyText = (TextView) mEmptyView.findViewById(R.id.textView);
        EmptyText.setText("관심표준이 없습니다");
        mRecycler = (SuperRecyclerView) rootView.findViewById(R.id.list);
        mRecycler.setLayoutManager(new LinearLayoutManager(thisContext));
        mRecycler.setAdapter(mAdapter);
        mRecycler.hideMoreProgress();
        mAdapter.setMode(SwipeItemManagerInterface.Mode.Multiple); // 스와이프 여러개 or 한개 설정
        mRecycler.addOnItemTouchListener(new RecyclerUtils.RecyclerItemClickListener(getActivity().getApplicationContext(), new RecyclerUtils.RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextView txt = (TextView) view.findViewById(R.id.code);

                Intent intent = new Intent(thisContext, Detail.class);
                intent.putExtra("areacode", list.get(position).areacode);
                intent.putExtra("standardcode", list.get(position).stdCode);
                intent.putExtra("standardname", list.get(position).stdName);
                intent.putExtra("orddate", list.get(position).orddate);
                intent.putExtra("udtdate", list.get(position).udtdate);
                intent.putExtra("attachfile", list.get(position).attachfile);
                intent.putExtra("attachfileUrl", list.get(position).attachfileUrl);

                startActivity(intent);
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


    @Override
    public void onRefresh() {
        Toast.makeText(getActivity().getApplicationContext(), "Refresh", Toast.LENGTH_LONG).show();

        mAdapter.closeAllExcept(null);
    }

    @Override
    public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
        Toast.makeText(getActivity().getApplicationContext(), "More", Toast.LENGTH_LONG).show();

        //mAdapter.add("More asked, more served");
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
}