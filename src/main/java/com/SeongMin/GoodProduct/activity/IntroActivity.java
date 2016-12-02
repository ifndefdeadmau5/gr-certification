package com.SeongMin.GoodProduct.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class IntroActivity extends Activity {
    Handler handler;
    Runnable run = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            Intent intent = new Intent(IntroActivity.this, SecondActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            //페이드인,페이드아웃,

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        handler = new Handler();
        handler.postDelayed(run, 1000); //4초후에 헨들러전송

        /*ProgressBarCircular Prog = (ProgressBarCircular)findViewById(R.id.progress);
        Prog.setOnClickListener(new View.OnClickListener(){
            public void onClick( View v ){

            };
        });
*/
        Log.i("my_tag", "app started");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public void finish() {

        super.finish();

    }

    @Override
    protected void onDestroy() {
        Log.i("test", "IntroActivity - onDestory()");
        handler.removeCallbacks(run);
        super.onDestroy();
    }


}
