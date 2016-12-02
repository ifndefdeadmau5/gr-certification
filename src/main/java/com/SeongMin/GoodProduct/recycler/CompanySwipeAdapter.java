package com.SeongMin.GoodProduct.recycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.SeongMin.GoodProduct.activity.R;
import com.SeongMin.GoodProduct.fragment.CompanyData;
import com.SeongMin.GoodProduct.global.db;
import com.malinskiy.superrecyclerview.swipe.BaseSwipeAdapter;
import com.malinskiy.superrecyclerview.swipe.SwipeLayout;

import java.util.ArrayList;

public class CompanySwipeAdapter extends BaseSwipeAdapter<CompanySwipeAdapter.ViewHolder> {

    private ArrayList<CompanyData> mData;
    private db dbAdapter;

    public CompanySwipeAdapter(ArrayList<CompanyData> mData) {
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
     /*   dbAdapter.open();
        viewHolder.stdcnt.setText( dbAdapter.getStandardCountByCompanyName( viewHolder.name.getText().toString() ));

        dbAdapter.close();*/
        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(viewHolder.getPosition());
                dbAdapter.open();
                Toast.makeText(v.getContext(), "Deleted " + viewHolder.name.getText().toString(), Toast.LENGTH_SHORT).show();
                dbAdapter.updateCompany(viewHolder.name.getText().toString(), false);
                dbAdapter.close();
            }
        });


        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);


        holder.name.setText(mData.get(position).compname);
        holder.repre.setText(mData.get(position).repre);
        holder.stdcnt.setText(String.valueOf(mData.get(position).stdcount));
        holder.position = position;


/*
        if(0 == mData.get(position).stdcount )
            holder.stdcnt.setVisibility(View.INVISIBLE);
        else{
            holder.stdcnt.setVisibility(View.VISIBLE);
        }*/

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
        public TextView name;
        public TextView repre;
        public TextView stdcnt;
        public int position;
        public Button deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.code);
            name.setSingleLine();
            repre = (TextView) itemView.findViewById(R.id.name);
            repre.setSingleLine();
            stdcnt = (TextView) itemView.findViewById(R.id.count2);
            deleteButton = (Button) itemView.findViewById(R.id.delete);

        }
    }
}
