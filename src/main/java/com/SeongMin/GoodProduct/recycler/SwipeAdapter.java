package com.SeongMin.GoodProduct.recycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.SeongMin.GoodProduct.activity.R;
import com.SeongMin.GoodProduct.fragment.GRListData;
import com.SeongMin.GoodProduct.global.db;
import com.malinskiy.superrecyclerview.swipe.BaseSwipeAdapter;
import com.malinskiy.superrecyclerview.swipe.SwipeLayout;

import java.util.ArrayList;

public class SwipeAdapter extends BaseSwipeAdapter<SwipeAdapter.ViewHolder> {

    private ArrayList<GRListData> mData;
    private db dbAdapter;

    public SwipeAdapter(ArrayList<GRListData> mData) {
        this.mData = mData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();

        View view = LayoutInflater.from(context).inflate(R.layout.item_swipeable, parent, false);

        final ViewHolder viewHolder = new ViewHolder(view);
        SwipeLayout swipeLayout = viewHolder.swipeLayout;
        swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);
        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                Toast.makeText(context, "DoubleClick", Toast.LENGTH_SHORT).show();
            }
        });

        dbAdapter = new db(parent.getContext());


        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(viewHolder.getPosition());
                dbAdapter.open();
                Toast.makeText(v.getContext(), "Deleted " + viewHolder.code.getText().toString(), Toast.LENGTH_SHORT).show();
                dbAdapter.deleteFavorite(viewHolder.code.getText().toString());
                dbAdapter.close();
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);

        holder.code.setText(mData.get(position).stdCode);
        holder.name.setText(mData.get(position).stdName);
        if (0 == mData.get(position).CompanyCount)
            holder.count.setVisibility(View.INVISIBLE);
        else {
            holder.count.setVisibility(View.VISIBLE);
        }
        holder.count.setText(String.valueOf(mData.get(position).CompanyCount));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public void remove(int position) {
        mData.remove(position);

        closeItem(position);

        notifyItemRemoved(position);
    }

    public static class ViewHolder extends BaseSwipeAdapter.BaseSwipeableViewHolder {
        public TextView code;
        public TextView name;
        public TextView count;
        public Button deleteButton;


        public ViewHolder(View itemView) {
            super(itemView);
            code = (TextView) itemView.findViewById(R.id.code);
            name = (TextView) itemView.findViewById(R.id.name);
            count = (TextView) itemView.findViewById(R.id.count2);
            deleteButton = (Button) itemView.findViewById(R.id.delete);
        }
    }
}
