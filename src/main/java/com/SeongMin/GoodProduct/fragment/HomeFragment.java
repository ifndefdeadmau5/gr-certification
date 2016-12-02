package com.SeongMin.GoodProduct.fragment;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.SeongMin.GoodProduct.activity.R;
import com.dexafree.materialList.cards.BasicImageButtonsCard;
import com.dexafree.materialList.cards.OnButtonPressListener;
import com.dexafree.materialList.cards.SimpleCard;
import com.dexafree.materialList.cards.WelcomeCard;
import com.dexafree.materialList.controller.OnDismissCallback;
import com.dexafree.materialList.model.Card;
import com.dexafree.materialList.view.MaterialListView;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    private MaterialListView mListView;
    private Context thisContext;
    private String tips[];

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tips = getResources().getStringArray(R.array.tips_array);

        thisContext = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.material_list, container, false);
        mListView = (MaterialListView) rootView.findViewById(R.id.material_listview);
        mListView.setCardAnimation(MaterialListView.CardAnimation.SWING_BOTTOM_IN);
        mListView.setOnDismissCallback(new OnDismissCallback() {
            @Override
            public void onDismiss(Card card, int position) {
                Log.i("tag", "Dimissed");

                mListView.notifyDataSetChanged();
                mListView.getAdapter().clear();
                fillArray();

            }
        });


        fillArray();

        return rootView;
    }


    private void fillArray() {
        //for (int i = 0; i < 35; i++) {
//        Card card = getRandomCard(i);
        Card card = getRandomCard(0);
        mListView.add(card);
        //}
    }

    private Card getRandomCard(final int position) {
        String title = "Card number " + (position + 1);
        String description = "Lorem ipsum dolor sit amet";

        int type = 0;

        SimpleCard card;
        Drawable icon;


        card = new WelcomeCard(thisContext);
        ((WelcomeCard) card).setBackgroundColor(getResources().getColor(R.color.charred_olive));
        card.setTitle("Welcome");
        ((WelcomeCard) card).setSubtitle("Tips");

        int randindex = (int) (Math.random() * 11);
        card.setDescriptionColor(getResources().getColor(R.color.hell_oliv));
        card.setDescription(tips[randindex]);

        ((WelcomeCard) card).setButtonText("확인");
        ((WelcomeCard) card).setDividerColor(getResources().getColor(R.color.green_pallet_5));
        card.setDismissible(true);

        ((WelcomeCard) card).setOnButtonPressedListener(new OnButtonPressListener() {
            @Override
            public void onButtonPressedListener(View view, Card card) {
                mListView.remove(card);
            }
        });

        return card;
    }


    private Card generateNewCard() {
        SimpleCard card = new BasicImageButtonsCard(getActivity());
        card.setDrawable(R.drawable.plus);
        card.setTitle("I'm new");
        card.setDescription("I've been generated on runtime!");

        return card;
    }

}
