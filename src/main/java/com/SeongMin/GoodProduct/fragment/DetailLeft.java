package com.SeongMin.GoodProduct.fragment;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.SeongMin.GoodProduct.activity.R;
import com.SeongMin.GoodProduct.global.db;
import com.SeongMin.GoodProduct.ui.AnimatedPathView;
import com.SeongMin.GoodProduct.util.DownloadFileAsync;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * <p/>
 * to handle interaction events.
 * Use the {@link DetailLeft#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailLeft extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    TextView textView;
    View rootView;
    // TODO: Rename and change types of parameters
    private String standardcode;
    private db dbAdapter;
    private Cursor mCursor;

    private DownloadManager mDownloadManager; //다운로드 매니저.
    private long mDownloadQueueId; //다운로드 큐 아이디..
    private String mFileName; //파일다운로드 완료후...파일을 열기 위해 저장된 위치를 입력해둔다.

    public DetailLeft() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailLeft newInstance() {
        DetailLeft fragment = new DetailLeft();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        standardcode = getActivity().getIntent().getStringExtra("standardcode"); // GRLisftFragment 에서 넘겨준 intent 객체의 String을 받아옴

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //colorize(photo);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_left_detail, container, false);
        // Inflate the layout for this fragment
        textView = (TextView) rootView.findViewById(R.id.grtext);
        TextView textViewDesc = (TextView) rootView.findViewById(R.id.description);
        final ImageButton ImgButton = (ImageButton) rootView.findViewById(R.id.star);
        final ImageButton ImgButtonInfo = (ImageButton) rootView.findViewById(R.id.info);

        Intent fromGRList = getActivity().getIntent();
        final String areacode = fromGRList.getStringExtra("areacode");
        final String standardname = fromGRList.getStringExtra("standardname");
        final String orddate = fromGRList.getStringExtra("orddate");
        final String udtdate = fromGRList.getStringExtra("udtdate");
        final String standardcode = fromGRList.getStringExtra("standardcode");
        final String attachfile = fromGRList.getStringExtra("attachfile");
        final String attachfileUrl = fromGRList.getStringExtra("attachfileUrl");
        final int companycount = fromGRList.getIntExtra("companycount", 0);

        textView.setText(standardcode);
        textView.setTextColor(Color.DKGRAY);
        textViewDesc.setTextColor(Color.GRAY);
        textViewDesc.setText("");
        textViewDesc.append("\n" +
                        "분야 : " + areacode + "\n" +
                        "표준명 : " + standardname + "\n" +
                        "제정일자 : " + orddate + "\n" +
                        "개정일자 : " + udtdate
        );

        ImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStar(ImgButton);
                dbAdapter = new db(getActivity().getApplicationContext());
                dbAdapter.open();
                if (!dbAdapter.isDuplicate(standardcode)) {
                    dbAdapter.insertFavorite(
                            areacode,
                            standardcode,
                            standardname,
                            orddate,
                            udtdate,
                            attachfile,
                            attachfileUrl,
                            companycount);
                }

                dbAdapter.close();

            }
        });
        ImgButtonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity().getIntent().getStringExtra("attachfile").equals("N")) {
                    Toast.makeText(getActivity().getApplicationContext(), "준비중 입니다", Toast.LENGTH_SHORT).show();
                } else {
                    showInformation(ImgButtonInfo);
                }

            }
        });




      /*  Bitmap photo = null;
        try {
            photo = setupPhoto();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (photo != null) {
            Log.w("photo", "bitmap not null");
        } else {
            Log.w("photo", "bitmap null");
        }
*/
        return rootView;
    }


    private void colorize(Bitmap photo) {
        Palette palette = Palette.generate(photo);
        applyPalette(palette);
    }

    private void applyPalette(Palette palette) {
        getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(palette.getDarkMutedColor().getRgb()));

        TextView titleView = (TextView) rootView.findViewById(R.id.grtext);
        titleView.setTextColor(palette.getVibrantColor().getRgb());

        TextView descriptionView = (TextView) rootView.findViewById(R.id.description);
        descriptionView.setTextColor(palette.getLightVibrantColor().getRgb());


        View infoView = rootView.findViewById(R.id.information_container);
        infoView.setBackgroundColor(palette.getLightMutedColor().getRgb());

        AnimatedPathView star = (AnimatedPathView) rootView.findViewById(R.id.star_container);
        star.setFillColor(palette.getVibrantColor().getRgb());
        star.setStrokeColor(palette.getLightVibrantColor().getRgb());
    }


    private Bitmap setupPhoto() throws Exception {
        int photoNumber = (int) (Math.random() * 128) + 1;
//        AssetManager AM = getResources().getAssets();
        AssetManager AM = getResources().getAssets();

        Bitmap bitmap = null;
        InputStream is = null;

        try {
            is = AM.open("mat2/material" + photoNumber + ".png", AssetManager.ACCESS_BUFFER);
        } catch (Exception e) {
            e.printStackTrace();

        }
        try {
            bitmap = BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            e.printStackTrace();

        }


        ((ImageView) rootView.findViewById(R.id.photo)).setImageBitmap(bitmap);

        return bitmap;

    }

    public void showInformation(View view) {
        startDownload();
    }

    private void startDownload() {
        final String urlString = getActivity().getIntent().getStringExtra("attachfileUrl");
        final String filename = getActivity().getIntent().getStringExtra("standardname");
        int lenghtOfFile = 0;


        try {
            URL url = new URL(urlString);

            URLConnection conexion = url.openConnection();
            conexion.connect();

            lenghtOfFile = conexion.getContentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DecimalFormat DF = new DecimalFormat("#,##0.00"); // 천단위 콤마


        new MaterialDialog.Builder(getActivity())
                .iconRes(R.drawable.pdf_file)
                .title(filename)
                .content("파일 크기 : " + DF.format((double) lenghtOfFile / 1000) + "KB\n다운로드 하시겠습니까?")
                .positiveText("네")
                .negativeText("아니요")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        new DownloadFileAsync(getActivity()).execute(urlString, "1", "1", filename);
                    }
                })
                .show();
    }

    public void showStar(View view) {
        toggleStarView();
    }

    private void toggleStarView() {
        final AnimatedPathView starContainer = (AnimatedPathView) rootView.findViewById(R.id.star_container);

        if (starContainer.getVisibility() == View.INVISIBLE) {
            rootView.findViewById(R.id.photo).animate().alpha(0.2f);
            starContainer.setAlpha(1.0f);
            starContainer.setVisibility(View.VISIBLE);
            starContainer.reveal();
        } else {
            rootView.findViewById(R.id.photo).animate().alpha(1.0f);
            starContainer.animate().alpha(0.0f).withEndAction(new Runnable() {
                @Override
                public void run() {
                    starContainer.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    private void setupText() {
        TextView titleView = (TextView) rootView.findViewById(R.id.grtext);


        TextView descriptionView = (TextView) rootView.findViewById(R.id.description);
        descriptionView.setText("Descrption");

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    public class CompanyData {
        String areacode;
        String standardcode;
        String standardname;
        String name;
        String representative;
        String enddate;
        String address;

        public CompanyData() {

        }

        public CompanyData(String areacode, String standardcode, String standardname, String name, String representative, String enddate, String address) {
            this.areacode = areacode;
            this.standardcode = standardcode;
            this.standardname = standardname;
            this.name = name;
            this.representative = representative;
            this.enddate = enddate;
            this.address = address;
        }

    }

}
