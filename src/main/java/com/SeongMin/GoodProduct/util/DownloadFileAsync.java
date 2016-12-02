package com.SeongMin.GoodProduct.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadFileAsync extends AsyncTask<String, String, String> {

    private ProgressDialog mDlg;
    private Context mContext;

    public DownloadFileAsync(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {

        mDlg = new ProgressDialog(mContext);
        mDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDlg.setMessage("다운로드 중");
        mDlg.show();

        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        int count = 0;

        try {
            URL url = new URL(params[0].toString());
            URLConnection conexion = url.openConnection();
            conexion.connect();

            int lenghtOfFile = conexion.getContentLength();

            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream("/sdcard/Download/" + params[3].toString() + ".pdf");

            byte data[] = new byte[1024];

            long total = 0;
            publishProgress("max", Integer.toString(lenghtOfFile));
            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress("progress", Long.toString(total), Integer.toString((int) ((total * 100) / lenghtOfFile)) + "%");
                output.write(data, 0, count);
                Thread.sleep(5);
            }
            output.flush();
            output.close();
            input.close();


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        if (progress[0].equals("progress")) {
            mDlg.setProgress(Integer.parseInt(progress[1]));
            mDlg.setMessage(progress[2]);
        } else if (progress[0].equals("max")) {
            mDlg.setMax(Integer.parseInt(progress[1]));
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onPostExecute(String unused) {
        mDlg.dismiss();

    }
}
