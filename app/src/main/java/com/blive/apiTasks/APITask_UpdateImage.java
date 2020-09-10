package com.blive.apiTasks;

import android.content.Context;
import android.util.Log;

import com.blive.constant.Constants_api;
import com.blive.model.User;
import com.blive.session.SessionUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by sans on 31-08-2018.
 **/

public class APITask_UpdateImage {
    private static final String MODULE = "APITask_UpdateImage";
    private static String TAG = "APITask_UpdateImage";
    private String Str_Url = Constants_api.updateImage;
    private Context mContext;
    private Listener mCallBack;

    public interface Listener {
        void onUpdateSuccess(User user);
        void onUpdateFailure(String mes);
    }

    public APITask_UpdateImage(Listener mCallBack, Context mContext) {
        this.mContext = mContext;
        this.mCallBack = mCallBack;
    }

    public void update(User user) {
        TAG = "update";
        try {
            Gson gson = new Gson();
            String strObj = gson.toJson(user);
            JSONObject obj = new JSONObject(strObj);
            Log.e(TAG, obj.toString());

            final Map<String, String> retMap = new Gson().fromJson(
                    strObj, new TypeToken<HashMap<String, String>>() {
                    }.getType()
            );

            MultipartBody.Builder builder = new MultipartBody.Builder();
            if(user.getProfile_pic()!=null){
                if(!user.getProfile_pic().isEmpty()){
                    final MediaType MEDIA_TYPE = MediaType.parse("image/png");
                    builder.setType(MultipartBody.FORM);
                    builder.addFormDataPart("profile_pic", user.getProfile_pic(), RequestBody.create(MEDIA_TYPE,new File(user.getProfile_pic())));
                }
            }

            if(user.getProfile_pic1()!=null){
                if(!user.getProfile_pic1().isEmpty()){
                    final MediaType MEDIA_TYPE = MediaType.parse("image/png");
                    builder.setType(MultipartBody.FORM);
                    builder.addFormDataPart("profile_pic1", user.getProfile_pic1(), RequestBody.create(MEDIA_TYPE,new File(user.getProfile_pic1())));
                }
            }

            if(user.getProfile_pic2()!=null){
                if(!user.getProfile_pic2().isEmpty()){
                    final MediaType MEDIA_TYPE = MediaType.parse("image/png");
                    builder.setType(MultipartBody.FORM);
                    builder.addFormDataPart("profile_pic2", user.getProfile_pic2(), RequestBody.create(MEDIA_TYPE,new File(user.getProfile_pic2())));
                }
            }

            for (String key : retMap.keySet()) {
                Log.e(TAG, "updatePic: "+key + " " + retMap.get(key));
                builder.addFormDataPart(key, retMap.get(key));
            }
            builder.addFormDataPart("user_id",SessionUser.getUser().getUser_id());
            builder.addFormDataPart("token",SessionUser.getUser().getActivation_code());

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
                JSONObject jsonObject = object.getJSONObject("data");
                JSONObject userObj = jsonObject.getJSONObject("user_details");
                User user1 = gson.fromJson(userObj.toString(),User.class);
                mCallBack.onUpdateSuccess(user1);
            } else {
                String message = object.getString("message");
                mCallBack.onUpdateFailure(message);
            }

        } catch (Exception e) {
            Log.e(MODULE, TAG + " Exception Occurs - " + e);

            String error = e.toString();
            if (error.contains(":")) {
                error = error.substring(error.indexOf(":"), error.length());
                mCallBack.onUpdateFailure(error);
            } else {
                mCallBack.onUpdateFailure(error);
            }
        }
    }
}