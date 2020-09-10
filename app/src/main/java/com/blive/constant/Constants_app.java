package com.blive.constant;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;

import com.blive.BLiveApplication;
import com.blive.agora.MessageListBean;
import com.blive.BuildConfig;
import com.blive.model.Country;
import com.blive.model.MessageBean;
import com.blive.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.agora.rtc.Constants;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.BeautyOptions;
import io.agora.rtc.video.VideoEncoderConfiguration;

import static com.blive.BLiveApplication.TAG;

public class Constants_app {

    //Pagination Limit
    public static final int pageLimit = 20;

    //API TimeOut
    public static int timeOut = 20000; //20 secs
    //Agora App ID
    public static final String appId = BuildConfig.private_app_id;

    public static final int TAKE_PHOTO = 101, SELECT_PHOTO = 102, EDIT_PROFILE = 103, SELECT_VIDEO = 104;

    public static final int BASE_VALUE_PERMISSION = 0X0001;
    public static final int PERMISSION_REQ_ID_RECORD_AUDIO = BASE_VALUE_PERMISSION + 1;
    public static final int PERMISSION_REQ_ID_CAMERA = BASE_VALUE_PERMISSION + 2;
    public static final int PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE = BASE_VALUE_PERMISSION + 3;

    public static final int MAX_PEER_COUNT = 3;

   /* public static int[] VIDEO_PROFILES = new int[]{
            Constants.VIDEO_PROFILE_120P,
            Constants.VIDEO_PROFILE_180P,
            Constants.VIDEO_PROFILE_240P,
            Constants.VIDEO_PROFILE_360P,
            Constants.VIDEO_PROFILE_480P,
            Constants.VIDEO_PROFILE_720P};*/

    public static VideoEncoderConfiguration.VideoDimensions[] VIDEO_DIMENSIONS = new VideoEncoderConfiguration.VideoDimensions[] {
            VideoEncoderConfiguration.VD_160x120,
            VideoEncoderConfiguration.VD_320x180,
            VideoEncoderConfiguration.VD_320x240,
            VideoEncoderConfiguration.VD_640x360,
            VideoEncoderConfiguration.VD_640x480,
            VideoEncoderConfiguration.VD_1280x720
    };

    public static int[] VIDEO_PROFILES = new int[]{
            Constants.VIDEO_PROFILE_120P,
            Constants.VIDEO_PROFILE_180P,
            Constants.VIDEO_PROFILE_240P,
            Constants.VIDEO_PROFILE_360P,
            Constants.VIDEO_PROFILE_480P,
            Constants.VIDEO_PROFILE_720P};


    public static final int DEFAULT_PROFILE_IDX = 4; // default use 480P
    public static  int BAudiance = 0;


    public static class PrefManager {
        public static final String PREF_PROPERTY_PROFILE_IDX = "pref_profile_index";
        public static final String PREF_PROPERTY_UID = "pOCXx_uid";
    }

    public static final String ACTION_KEY_CROLE = "C_Role";
    public static final String ACTION_KEY_ROOM_NAME = "ecHANEL";

    public static final String MEDIA_SDK_VERSION;

    static {
        String sdk = "undefined";
        try {
            sdk = RtcEngine.getSdkVersion();
        } catch (Throwable e) {
        }
        MEDIA_SDK_VERSION = sdk;
    }

    public static boolean BEAUTY_EFFECT_ENABLED = true;

    public static final int BEAUTY_EFFECT_DEFAULT_CONTRAST = 1;
    public static final float BEAUTY_EFFECT_DEFAULT_LIGHTNESS = .20f;
    public static final float BEAUTY_EFFECT_DEFAULT_SMOOTHNESS = .20f;
    public static final float BEAUTY_EFFECT_DEFAULT_REDNESS = .10f;

    public static final BeautyOptions BEAUTY_OPTIONS = new BeautyOptions(BEAUTY_EFFECT_DEFAULT_CONTRAST, BEAUTY_EFFECT_DEFAULT_LIGHTNESS, BEAUTY_EFFECT_DEFAULT_SMOOTHNESS, BEAUTY_EFFECT_DEFAULT_REDNESS);

    public static final float BEAUTY_EFFECT_MAX_LIGHTNESS = 1.0f;
    public static final float BEAUTY_EFFECT_MAX_SMOOTHNESS = 1.0f;
    public static final float BEAUTY_EFFECT_MAX_REDNESS = 1.0f;

    public static Random RANDOM = new Random();

    public static final int[] COLOR_ARRAY = new int[]{R.drawable.shape_circle_black, R.drawable.shape_circle_blue, R.drawable.shape_circle_pink,
            R.drawable.shape_circle_pink_dark, R.drawable.shape_circle_yellow, R.drawable.shape_circle_red};

    private static List<MessageListBean> messageListBeanList = new ArrayList<>();

    public static void addMessageListBeanList(MessageListBean messageListBean) {
        messageListBeanList.add(messageListBean);

    }

    //logout clean list
    public static void cleanMessageListBeanList() {
        messageListBeanList.clear();
    }

    public static MessageListBean getExistMesageListBean(String accountOther) {
        int ret = existMessageListBean(accountOther);
        if (ret > -1) {

            return messageListBeanList.remove(ret);
        }
        return null;
    }

    //return exist list position
    private static int existMessageListBean(String accountOther) {
        int size = messageListBeanList.size();

        for (int i = 0; i < size; i++) {
            if (messageListBeanList.get(i).getAccountOther().equals(accountOther)) {

                return i;
            }
        }
        return -1;
    }

    public static void addMessageBean(String account, String msg) {
        MessageBean messageBean = new MessageBean(account, msg, false, false, false);

        int ret = existMessageListBean(account);

        if (ret == -1) {

            //account not exist new messagelistbean
            messageBean.setBackground(Constants_app.COLOR_ARRAY[RANDOM.nextInt(Constants_app.COLOR_ARRAY.length)]);
            List<MessageBean> messageBeanList = new ArrayList<>();
            messageBeanList.add(messageBean);
            messageListBeanList.add(new MessageListBean(account, messageBeanList));
        } else {

            //account exist get messagelistbean
            MessageListBean bean = messageListBeanList.remove(ret);
            List<MessageBean> messageBeanList = bean.getMessageBeanList();
            if (messageBeanList.size() > 0) {
                messageBean.setBackground(messageBeanList.get(0).getBackground());
            } else {
                messageBean.setBackground(Constants_app.COLOR_ARRAY[RANDOM.nextInt(Constants_app.COLOR_ARRAY.length)]);
            }
            messageBeanList.add(messageBean);
            bean.setMessageBeanList(messageBeanList);
            messageListBeanList.add(bean);
        }
    }

    public static String decodeImage(String base64) {
        String image = "";
        try {
            image = URLDecoder.decode(base64, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return image;
    }

    public static String getMoon_level(String moonValue) {
        String moonLevel = "";
        int newMoonValue = Integer.parseInt(moonValue);

        if (newMoonValue == 0) {
            moonLevel = "0";
        } else if (newMoonValue < 100 && newMoonValue > 0) {
            moonLevel = "0";
        } else if (newMoonValue < 300 && newMoonValue > 99) {
            moonLevel = "1";
        } else if (newMoonValue < 900 && newMoonValue > 299) {
            moonLevel = "2";
        } else if (newMoonValue < 2700 && newMoonValue > 899) {
            moonLevel = "3";
        } else if (newMoonValue < 8100 && newMoonValue > 2699) {
            moonLevel = "4";
        } else if (newMoonValue == 8100) {
            moonLevel = "5";
        }

        return moonLevel;
    }

    public static String loadBroadCasterStar(String moonValue) {

        int times = Integer.valueOf(moonValue) / 8100;
        if (times > 0) {
            int moonVal = Integer.valueOf(moonValue) - (8100 * times);
            moonValue = String.valueOf(moonVal);
        }

        String moonLevel = "";
        double moonPercentage = 0.0;

        int newMoonValue = Integer.parseInt(moonValue);

        if (newMoonValue == 0) {
            moonPercentage = 0;
            moonLevel = "0";
        } else if (newMoonValue < 100 && newMoonValue > 0) {
            moonPercentage = (newMoonValue * 100) / 100;
            moonLevel = "0";
        } else if (newMoonValue < 300 && newMoonValue >= 100) {
            newMoonValue = newMoonValue - 100;
            moonPercentage = (newMoonValue * 100) / 200;
            moonLevel = "1";
        } else if (newMoonValue < 900 && newMoonValue >= 300) {
            newMoonValue = newMoonValue - 300;
            moonPercentage = (newMoonValue * 100) / 600;
            moonLevel = "2";
        } else if (newMoonValue < 2700 && newMoonValue >= 900) {
            newMoonValue = newMoonValue - 900;
            moonPercentage = (newMoonValue * 100) / 1800;
            moonLevel = "3";
        } else if (newMoonValue < 8100 && newMoonValue >= 2700) {
            newMoonValue = newMoonValue - 2700;
            moonPercentage = (newMoonValue * 100) / 5400;
            moonLevel = "4";
        } else if (newMoonValue == 8100) {
            moonPercentage = (newMoonValue * 100) / 8100;
            moonLevel = "5";
        }

        int moonValuePercent = (int) moonPercentage;
        return "@drawable/level_0" + moonValuePercent;
    }

    public static String loadStarLevel(String moonValue) {

        int times = Integer.valueOf(moonValue) / 8100;
        if (times > 0) {
            int moonVal = Integer.valueOf(moonValue) - (8100 * times);
            moonValue = String.valueOf(moonVal);
        }

        String moonLevel = "";

        int newMoonValue = Integer.parseInt(moonValue);

        if (newMoonValue == 0) {
            moonLevel = "0";
        } else if (newMoonValue < 100 && newMoonValue > 0) {
            moonLevel = "0";
        } else if (newMoonValue < 300 && newMoonValue >= 100) {
            moonLevel = "1";
        } else if (newMoonValue < 900 && newMoonValue >= 300) {
            moonLevel = "2";
        } else if (newMoonValue < 2700 && newMoonValue >= 900) {
            moonLevel = "3";
        } else if (newMoonValue < 8100 && newMoonValue >= 2700) {
            moonLevel = "4";
        } else if (newMoonValue == 8100) {
            moonLevel = "5";
        }

        return moonLevel;
    }

    private String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("countrycode.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public ArrayList<Country> getCountry(Context context) {

        ArrayList<Country> countryList = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset(context));
            JSONArray m_jArry = obj.getJSONArray("countryCode");
            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                String countryDialCode = jo_inside.getString("dial_code");
                String countryCode = jo_inside.getString("code");
                String countryName = jo_inside.getString("name");
                Country country = new Country();
                country.setCode(countryCode);
                country.setName(countryName);
                country.setDialCode(countryDialCode);
                countryList.add(country);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return countryList;
    }
}