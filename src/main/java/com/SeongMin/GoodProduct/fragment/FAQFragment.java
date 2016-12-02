package com.SeongMin.GoodProduct.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.SeongMin.GoodProduct.activity.R;
import com.ms.square.android.expandabletextview.ExpandableTextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FAQFragment extends Fragment {


    public FAQFragment() {
        // Required empty public constructor
    }

    public static FAQFragment newInstance() {
        FAQFragment f = new FAQFragment();

        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String answers[];
        answers = getResources().getStringArray(R.array.faq);
        View rootView = inflater.inflate(R.layout.fragment_faq, container, false);

        ((TextView) rootView.findViewById(R.id.sample1).findViewById(R.id.title)).setText("1. GR 표준의 대상품목 선정 진행절차가 궁금합니다.");
        ((TextView) rootView.findViewById(R.id.sample2).findViewById(R.id.title)).setText("2. GR 인증을 신청하기 위한 필수 조건에는 어떤 것들이 있나요?");
        ((TextView) rootView.findViewById(R.id.sample3).findViewById(R.id.title)).setText("3. GR 인증을 신청 후 소요기간과 진행절차는 어떻게 되나요?");
        ((TextView) rootView.findViewById(R.id.sample4).findViewById(R.id.title)).setText("4. GR 인증의 유효기간과 연장 신청으로 보유할 수 있는 최대 기간은 얼마나 되나요?");
        ((TextView) rootView.findViewById(R.id.sample5).findViewById(R.id.title)).setText("5. GR 대상품목 선정 신청은 접수 기간이 별도로 존재하나요?");
        ((TextView) rootView.findViewById(R.id.sample6).findViewById(R.id.title)).setText("6. GR 인증 취득 후 재활용 원자재의 사용비율 변경 또는 원자재 종류의 변경이 있을 경우, 어떻게 해야 하나요?");

        ExpandableTextView expTv1 = (ExpandableTextView) rootView.findViewById(R.id.sample1)
                .findViewById(R.id.expand_text_view);
        final ExpandableTextView expTv2 = (ExpandableTextView) rootView.findViewById(R.id.sample2)
                .findViewById(R.id.expand_text_view);
        final ExpandableTextView expTv3 = (ExpandableTextView) rootView.findViewById(R.id.sample3)
                .findViewById(R.id.expand_text_view);
        final ExpandableTextView expTv4 = (ExpandableTextView) rootView.findViewById(R.id.sample4)
                .findViewById(R.id.expand_text_view);
        final ExpandableTextView expTv5 = (ExpandableTextView) rootView.findViewById(R.id.sample5)
                .findViewById(R.id.expand_text_view);
        final ExpandableTextView expTv6 = (ExpandableTextView) rootView.findViewById(R.id.sample6)
                .findViewById(R.id.expand_text_view);

        expTv1.setText(answers[0]);
        expTv2.setText(answers[1]);
        expTv3.setText(answers[2]);
        expTv4.setText(answers[3]);
        expTv5.setText(answers[4]);
        expTv6.setText(answers[5]);

        return rootView;
    }


}
