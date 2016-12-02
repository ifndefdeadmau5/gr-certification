package com.SeongMin.GoodProduct.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.SeongMin.GoodProduct.activity.R;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

/**
 * Created by neokree on 16/12/14.
 */
public class FragmentText extends Fragment {

    private int pageNum;

    public static FragmentText newInstance(int page) {
        FragmentText fragment = new FragmentText();
        fragment.pageNum = page;

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_largeimage, container, false);
        SubsamplingScaleImageView imageView = (SubsamplingScaleImageView) rootView.findViewById(R.id.imageView);
        switch (pageNum) {
            case 0:
                imageView.setImageResource(R.drawable.gayo);
                break;
            case 1:
                imageView.setImageResource(R.drawable.process);
                break;
            default:
                imageView.setImageResource(R.drawable.gayo);
        }


        return rootView;
    }
}
