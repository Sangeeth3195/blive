package com.blive.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.blive.model.Gift;
import com.blive.model.GiftResponse;
import com.blive.session.SessionUser;
import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class DownloadGifts extends Service {

    Context mContext;
    Activity activity;
    private PowerManager.WakeLock mWakeLock;
    private List<Gift> giftSecureDataList;
    private int giftDownloadingCount = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public DownloadGifts() {

    }

    public DownloadGifts(Context context, Activity activity) {
        this.mContext = context;
        this.activity = activity;

    }

    public void downloadGiftFromServer() {

        final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
        Call<GiftResponse> call = apiClient.getGifts("all", SessionUser.getUser().getUser_id());
        call.enqueue(new retrofit2.Callback<GiftResponse>() {
            @Override
            public void onResponse(@NonNull Call<GiftResponse> call, @NonNull Response<GiftResponse> response) {
                GiftResponse giftResponse = response.body();
                if (response.code() == 200) {
                    if (giftResponse != null) {
                        if (giftResponse.getStatus().equalsIgnoreCase("success")) {
                            handleResponse(giftResponse.getData().getGifts());
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GiftResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void handleResponse(ArrayList<Gift> response) {
        try {
            giftSecureDataList = response;
            checkDownloadingGift();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkDownloadingGift() {
        try{
            File giftOldHiddenFolder = new File(Environment.getExternalStorageDirectory() + "/.webPStorage");
            if(giftOldHiddenFolder.exists()){
                giftOldHiddenFolder.delete();
            }
            File giftOldFolder = new File(Environment.getExternalStorageDirectory() + "/.webPStorage");
            if(giftOldFolder.exists()){
                giftOldFolder.delete();
            }
            File giftFolder = new File(mContext.getFilesDir() + "/.webPStorage");
            if (!giftFolder.mkdirs()) {
                giftFolder.mkdir();
            }

            if(giftSecureDataList.size() >0){
                if(giftDownloadingCount != giftSecureDataList.size() ){
                    File giftFile = new File(mContext.getFilesDir() + "/.webPStorage/" + giftSecureDataList.get(giftDownloadingCount).getName() + ".webp");
                    if(giftFile.exists()){
                        giftDownloadingCount = ++giftDownloadingCount;
                        checkDownloadingGift();
                    }else{
                        new DownloadFileFromURL(giftSecureDataList.get(giftDownloadingCount).getGif())
                                .execute();
                    }
                }

            }
        }catch (Exception e){
            Log.e("giftDownloadErrorPos",""+ giftSecureDataList.get(giftDownloadingCount).getName() + " "+
                    giftDownloadingCount +" "+  e.getMessage());
        }
    }

    @SuppressLint("StaticFieldLeak")
    class DownloadFileFromURL extends AsyncTask<String, String, String> {
        String downloadUrl,filePath;
        DownloadFileFromURL(String url) {
            downloadUrl = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
            assert pm != null;
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire(10*60*1000L /*10 minutes*/);

        }

        @Override
        protected String doInBackground(String... f_url) {

            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(downloadUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();

                File root = new File(mContext.getFilesDir() + "/.webPStorage/");
                if (!root.exists()) {
                    root.mkdirs();
                }
                output = new FileOutputStream(mContext.getFilesDir()+ "/.webPStorage/" + giftSecureDataList.get(giftDownloadingCount).getName() + ".webp");
                File downloadDeletedpath = new File(mContext.getFilesDir() + "/.webPStorage/" + giftSecureDataList.get(giftDownloadingCount).getName() + ".webp");
                filePath = downloadDeletedpath.getPath();
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        //    publishProgress((int) (total * 100 / fileLength));
                        output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(String file_url) {
            File fileDownloaded = new File(mContext.getFilesDir()+"/.webPStorage/"+ giftSecureDataList.get(giftDownloadingCount).getName() + ".webp");
            String filePath = fileDownloaded.getPath();
            if (filePath.isEmpty()) {
                Log.e("fileDownloadPath", "Download error " + filePath);
            } else {
                Log.e("fileDownloadPath", "Download success " + filePath);
                try {
                    mWakeLock.release();
                }catch (Exception e){
                    Crashlytics.logException(e);
                }
            }
            giftDownloadingCount = ++giftDownloadingCount;
            checkDownloadingGift();
        }
    }

}
