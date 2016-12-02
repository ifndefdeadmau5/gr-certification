package com.SeongMin.GoodProduct.activity;

import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.SeongMin.GoodProduct.global.db;
import com.SeongMin.GoodProduct.ui.CheckableFrameLayout;
import com.SeongMin.GoodProduct.ui.ObservableScrollView;
import com.SeongMin.GoodProduct.util.LUtils;
import com.SeongMin.GoodProduct.util.UIUtils;
import com.dexafree.materialList.cards.BasicImageButtonsCard;
import com.dexafree.materialList.cards.SimpleCard;
import com.dexafree.materialList.cards.SmallImageCard;
import com.dexafree.materialList.model.Card;
import com.dexafree.materialList.view.IMaterialView;
import com.dexafree.materialList.view.MaterialListView;

/**
 * An activity that shows detail information for a session, including session title, abstract,
 * time information, speaker photos and bios, etc.
 */
public class CompanyDetailActivity extends ActionBarActivity implements
        ObservableScrollView.Callbacks {
    public static final String TRANSITION_NAME_PHOTO = "photo";
    private static final float PHOTO_ASPECT_RATIO = 1.7777777f;
    private LUtils mLUtils;
    private Toolbar mActionBarToolbar;
    private boolean mStarred;
    private ObservableScrollView mScrollView;
    private CheckableFrameLayout mAddScheduleButton;
    private int mAddScheduleButtonHeightPixels;
    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener
            = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            mAddScheduleButtonHeightPixels = mAddScheduleButton.getHeight();
            recomputePhotoAndScrollingMetrics();
        }
    };
    private View mHeaderBox;
    private boolean mHasPhoto;
    private ImageView mPhotoView;
    private View mPhotoViewContainer;
    private int mPhotoHeightPixels;
    private int mHeaderHeightPixels;
    private View mDetailsContainer;
    private View mScrollViewChild;
    private float mMaxHeaderElevation;
    private float mFABElevation;

    private TextView session_title;
    private TextView session_subtitle;
    private TextView session_requirements;


    private IMaterialView mListView;
    private db dbAdapter;
    private Cursor mCursor;

    protected Toolbar getActionBarToolbar() {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
            if (mActionBarToolbar != null) {
                setSupportActionBar(mActionBarToolbar);
            }
        }
        return mActionBarToolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_detail);

        mListView = (IMaterialView) findViewById(R.id.material_listview);
        mListView.setCardAnimation(MaterialListView.CardAnimation.SWING_BOTTOM_IN);

        boolean shouldBeFloatingWindow = shouldBeFloatingWindow();
        if (shouldBeFloatingWindow) {
            setupFloatingWindow();
        }
        mLUtils = LUtils.getInstance(this);
        mHasPhoto = true;

        final Toolbar toolbar = getActionBarToolbar();
        toolbar.setNavigationIcon(shouldBeFloatingWindow
                ? R.drawable.ic_ab_close : R.drawable.ic_up);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        session_title = (TextView) findViewById(R.id.session_title);
        session_subtitle = (TextView) findViewById(R.id.session_subtitle);
        session_requirements = (TextView) findViewById(R.id.session_requirements);
        mDetailsContainer = findViewById(R.id.details_container);
        mHeaderBox = findViewById(R.id.header_session);
        mPhotoViewContainer = findViewById(R.id.session_photo_container);
        mScrollViewChild = findViewById(R.id.scroll_view_child);
        mPhotoView = (ImageView) findViewById(R.id.session_photo);

        mScrollViewChild.setVisibility(View.VISIBLE);
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll_view);
        mScrollView.addCallbacks(this);
        ViewTreeObserver vto = mScrollView.getViewTreeObserver();
        if (vto.isAlive()) {
            vto.addOnGlobalLayoutListener(mGlobalLayoutListener);
        }

        mScrollViewChild = findViewById(R.id.scroll_view_child);
        mScrollViewChild.setVisibility(View.VISIBLE);
        mFABElevation = getResources().getDimensionPixelSize(R.dimen.fab_elevation);
        mMaxHeaderElevation = getResources().getDimensionPixelSize(
                R.dimen.session_detail_max_header_elevation);

        ViewCompat.setTransitionName(mPhotoView, TRANSITION_NAME_PHOTO);


        dbAdapter = new db(getApplicationContext());
        dbAdapter.open();


        mAddScheduleButton = (CheckableFrameLayout) findViewById(R.id.add_schedule_button);
        mAddScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean starred = !mStarred;
                dbAdapter = new db(getApplicationContext());
                dbAdapter.open();
                if (starred) {
                    dbAdapter.updateCompany(getIntent().getStringExtra("companyname"), true);
                } else {
                    dbAdapter.updateCompany(getIntent().getStringExtra("companyname"), false);
                }
                dbAdapter.close();

                showStarred(starred, true);
            }
        });


        if (dbAdapter.isCompanyLiked(getIntent().getStringExtra("companyname"))) {
            mStarred = true;
            showStarred(mStarred, true);
        }
        dbAdapter.close();

        ((ListView) mListView).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                mScrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        setupText();
        fillArray();
        mScrollView.smoothScrollTo(0, 0);
    }


    private void fillArray() {
        dbAdapter = new db(this);
        dbAdapter.open();
        mCursor = dbAdapter.getStandardListByCompanyName(getIntent().getStringExtra("companyname")); ///////// 해당 표준번호 취득한 모든 업체

        int i = 0;
        while (mCursor.moveToNext()) {
            Card card = getRandomCard(mCursor.getString(1),
                    mCursor.getString(2),
                    mCursor.getString(3),
                    mCursor.getString(4),
                    mCursor.getString(5), i++);
            mListView.add(card);
        }

        mCursor.close();
        dbAdapter.close();
    }

    private Card getRandomCard(String stdname, String stdcode, String area, String type, String enddate, final int position) {

        int Cardtype = 1;//position % 6;

        SimpleCard card;

        switch (Cardtype) {
            case 1:
                card = new SmallImageCard(this);
                card.setDescription(stdname + "\n" + area + "\n" + type + "\n" + enddate + "일자로 만료됩니다");
                card.setTitle(stdcode);
                card.setDrawable(R.drawable.ic_small);
                card.setDismissible(true);
                return card;
            default:
                return null;
        }

    }

    private Card generateNewCard() {
        SimpleCard card = new BasicImageButtonsCard(this);
        card.setDrawable(R.drawable.plus);
        card.setTitle("I'm new");
        card.setDescription("I've been generated on runtime!");

        return card;
    }


    private void setupText() {
        session_requirements.setText(getIntent().getStringExtra("address"));
        session_title.setText(getIntent().getStringExtra("companyname"));
        session_subtitle.setText(getIntent().getStringExtra("representative"));

    }

    private void setupFloatingWindow() {
        // configure this Activity as a floating window, dimming the background
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = getResources().getDimensionPixelSize(R.dimen.session_details_floating_width);
        params.height = getResources().getDimensionPixelSize(R.dimen.session_details_floating_height);
        params.alpha = 1;
        params.dimAmount = 0.4f;
        params.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        getWindow().setAttributes(params);
    }

    private boolean shouldBeFloatingWindow() {
        Resources.Theme theme = getTheme();
        TypedValue floatingWindowFlag = new TypedValue();
        if (theme == null || !theme.resolveAttribute(R.attr.isFloatingWindow, floatingWindowFlag, true)) {
            // isFloatingWindow flag is not defined in theme
            return false;
        }
        return (floatingWindowFlag.data != 0);
    }

    private void showStarred(boolean starred, boolean allowAnimate) {
        mStarred = starred;

        mAddScheduleButton.setChecked(mStarred, allowAnimate);

        ImageView iconView = (ImageView) mAddScheduleButton.findViewById(R.id.add_schedule_icon);
        getLUtils().setOrAnimatePlusCheckIcon(iconView, starred, allowAnimate);
    }

    private void recomputePhotoAndScrollingMetrics() {
        mHeaderHeightPixels = mHeaderBox.getHeight();

        mPhotoHeightPixels = 0;
        if (mHasPhoto) {
            mPhotoHeightPixels = (int) (mPhotoView.getWidth() / PHOTO_ASPECT_RATIO);
            mPhotoHeightPixels = Math.min(mPhotoHeightPixels, mScrollView.getHeight() * 2 / 3);
        }

        ViewGroup.LayoutParams lp;
        lp = mPhotoViewContainer.getLayoutParams();
        if (lp.height != mPhotoHeightPixels) {
            lp.height = mPhotoHeightPixels;
            mPhotoViewContainer.setLayoutParams(lp);
        }

        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)
                mDetailsContainer.getLayoutParams();
        if (mlp.topMargin != mHeaderHeightPixels + mPhotoHeightPixels) {
            mlp.topMargin = mHeaderHeightPixels + mPhotoHeightPixels;
            mDetailsContainer.setLayoutParams(mlp);
        }

        onScrollChanged(0, 0); // trigger scroll handling
    }

    public LUtils getLUtils() {
        return mLUtils;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mScrollView == null) {
            return;
        }

        ViewTreeObserver vto = mScrollView.getViewTreeObserver();
        if (vto.isAlive()) {
            vto.removeGlobalOnLayoutListener(mGlobalLayoutListener);
        }

    }


    @Override
    public void onScrollChanged(int deltaX, int deltaY) {
        int scrollY = mScrollView.getScrollY();

        float newTop = Math.max(mPhotoHeightPixels, scrollY);
        mHeaderBox.setTranslationY(newTop);
        mAddScheduleButton.setTranslationY(newTop + mHeaderHeightPixels
                - mAddScheduleButtonHeightPixels / 2);

        float gapFillProgress = 1;
        if (mPhotoHeightPixels != 0) {
            gapFillProgress = Math.min(Math.max(UIUtils.getProgress(scrollY,
                    0,
                    mPhotoHeightPixels), 0), 1);
        }

        ViewCompat.setElevation(mHeaderBox, gapFillProgress * mMaxHeaderElevation);
        ViewCompat.setElevation(mAddScheduleButton, gapFillProgress * mMaxHeaderElevation
                + mFABElevation);
        // Move background photo (parallax effect)
        mPhotoViewContainer.setTranslationY(scrollY * 0.5f);
    }


}
