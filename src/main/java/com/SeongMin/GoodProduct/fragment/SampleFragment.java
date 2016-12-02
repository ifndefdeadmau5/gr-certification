package com.SeongMin.GoodProduct.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.internal.widget.CompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.SeongMin.GoodProduct.activity.R;
import com.SeongMin.GoodProduct.ui.FloatingActionButton;
import com.SeongMin.GoodProduct.ui.ProgressBarCircular;

public class SampleFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    private int position;

    public static SampleFragment newInstance(int position) {
        SampleFragment f = new SampleFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        position = getArguments().getInt(ARG_POSITION);
        View rootView = inflater.inflate(R.layout.page, container, false);

        ProgressBarCircular progressBarCircular = (ProgressBarCircular) rootView.findViewById(R.id.progress);
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fabButton);
        CompatTextView text = (CompatTextView) rootView.findViewById(R.id.tintedittxt);
        fab.setDrawableIcon(getResources().getDrawable(R.drawable.plus));
        switch (position) {
            case 0:
                fab.setBackgroundColor(getResources().getColor(R.color.material_deep_teal_500));
                progressBarCircular.setBackgroundColor(getResources().getColor(R.color.material_deep_teal_500));

                break;
            case 1:
                fab.setBackgroundColor(getResources().getColor(R.color.red));
                progressBarCircular.setBackgroundColor(getResources().getColor(R.color.red));

                break;
            case 2:
                progressBarCircular.setBackgroundColor(getResources().getColor(R.color.blue));
                fab.setBackgroundColor(getResources().getColor(R.color.blue));

                break;
            case 3:
                fab.setBackgroundColor(getResources().getColor(R.color.material_blue_grey_800));
                progressBarCircular.setBackgroundColor(getResources().getColor(R.color.light));

                break;
            default:
                break;
        }
        text.setText(position + "번 페이지 로딩중..");
        return rootView;
    }


}