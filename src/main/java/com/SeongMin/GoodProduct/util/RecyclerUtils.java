package com.SeongMin.GoodProduct.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerUtils {

    public static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        GestureDetector mGestureDetector;
        private OnItemClickListener mListener;

        public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
            mListener = listener;
            Log.i("dd", "RecyclerItemClickListener");
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {

                    return true;
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            // delete 버튼 클릭 시에도 액티비티가 넘어가서 임시적으로 X 값으로 왼쪽 오른쪽 영역을 나눔..
            if (e.getX() > 532)
                return false;

            View childView = view.findChildViewUnder(e.getX(), e.getY());
            Log.i("dd", "onInterceptTouchEvent");
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {

                mListener.onItemClick(childView, view.getChildPosition(childView));
                Log.i("dd", "onItemClick");

            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
            Log.i("dd", "onTouchEvent");

        }

        public interface OnItemClickListener {

            public void onItemClick(View view, int position);
        }
    }
}
