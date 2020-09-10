package com.blive.apiTasks;

import android.content.Context;
import android.util.Log;

import com.blive.constant.Constants_api;
import com.blive.session.SessionUser;

import org.json.JSONObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by sans on 24-09-2018.
 **/

public class APITask_UploadVideo {
    private static final String MODULE = "APITask_UploadVideo";
    private static String TAG = "";
    private String Str_Url = Constants_api.uploadVideo;
    private Context mContext;
    private Listener mCallBack;

    public interface Listener {
        void onUploadSuccess(String msg);
        void onUploadFailure(String mes);
    }

    public APITask_UploadVideo(Listener mCallBack, Context mContext) {
        this.mContext = mContext;
        this.mCallBack = mCallBack;
    }

    public void upload(String video) {
        TAG = "upload";
        try {
            Log.e(TAG, video);

            MultipartBody.Builder builder = new MultipartBody.Builder();
            if(video!=null){
                if(!video.isEmpty()){
                    final MediaType MEDIA_TYPE = MediaType.parse("video/mp4");
                    builder.setType(MultipartBody.FORM);
                    builder.addFormDataPart("videoFile", video, RequestBody.create(MEDIA_TYPE,new File(video)));
                }
            }

            builder.addFormDataPart("user_id", SessionUser.getUser().getUser_id());
            builder.addFormDataPart("token", SessionUser.getUser().getActivation_code());
            Log.e("videoupAct",SessionUser.getUser().getActivation_code());
            Log.e("videoUpId",SessionUser.getUser().getUser_id());
            File file = new File(video);
            final long totalSize = file.length();

           /* CountingFileRequestBody countingFileRequestBody = new CountingFileRequestBody(new File(video), "video/mp4", num -> {
                float progress = (num / (float) totalSize) * 100;
                Log.e(TAG, "transferred: "+progress );
            });*/

            RequestBody body = builder.build();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(Str_Url)
                    .addHeader("authorization", "Bearer "+ SessionUser.getUser().getActivation_code())
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            assert response.body() != null;
            String res = response.body().string();
            JSONObject object = new JSONObject(res);

            if (object.getString("status").equalsIgnoreCase("success")) {

                JSONObject obj = object.getJSONObject("data");
                String message = obj.getString("message");
                mCallBack.onUploadSuccess(message);
            } else {

                String message = object.getString("message");
                mCallBack.onUploadFailure(message);
            }

        } catch (Exception e) {

            Log.e(MODULE, TAG + " Exception Occurs - " + e);

            String error = e.toString();
            if (error.contains(":")) {
                error = error.substring(error.indexOf(":"), error.length());
                mCallBack.onUploadFailure(error);
            } else {
                mCallBack.onUploadFailure(error);
            }
        }
    }
}