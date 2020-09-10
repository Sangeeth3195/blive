package com.blive.chat.chatutil;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import com.blive.R;
import com.blive.chat.chatmodels.Chat;
import com.blive.chat.chatmodels.ChatUser;
import com.blive.chat.chatmodels.Message;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class ChatUtils {

    public static boolean CHAT_CAB = false;
    private HashMap<String, ChatUser> myUsersNameInPhoneMap;
    private SharedPreferenceHelper sharedPreferenceHelper;
    public static final String BROADCAST_DOWNLOAD_EVENT = "com.blive.DOWNLOAD_EVENT";
    public static final String BROADCAST_MY_CONTACTS = "com.blive.MY_CONTACTS";
    public static final String BROADCAST_MY_USERS = "com.blive.MY_USERS";
    private Gson gson;
    private static final String USER_NAME_CACHE = "usercachemap";
    public static final String BROADCAST_USER = "com.blive.services.USER";
    public static final String UPLOAD_AND_SEND = "com.blive.services.UPLOAD_N_SEND";
    public static final String BROADCAST_LOGOUT = "com.blive.services.LOGOUT";
    private static final String USER = "USER";
    public static final String REF_DATA = "data";
    public static final String REF_USER = "users";
    public static final String REF_CHAT = "chats";
    public static String CURRENT_CHAT_ID;

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    private static final String USER_MUTE = "USER_MUTE";

    public ChatUtils(Context context) {
        sharedPreferenceHelper = new SharedPreferenceHelper(context);
        gson = new Gson();
    }

    public void setLoggedInUser(ChatUser user) {
        sharedPreferenceHelper.setStringPreference(USER, gson.toJson(user, new TypeToken<ChatUser>() {
        }.getType()));
    }

    public ChatUser getLoggedInUser() {
        String savedUserPref = sharedPreferenceHelper.getStringPreference(USER);
        if (savedUserPref != null)
            return gson.fromJson(savedUserPref, new TypeToken<ChatUser>() {
            }.getType());
        return null;
    }

    public static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static String getTime(Long milliseconds) {
        return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date(milliseconds));
    }

    public HashMap<String, ChatUser> getCacheMyUsers() {
        if (this.myUsersNameInPhoneMap != null) {
            return this.myUsersNameInPhoneMap;
        } else {
            String inPrefs = sharedPreferenceHelper.getStringPreference(USER_NAME_CACHE);
            if (inPrefs != null) {
                this.myUsersNameInPhoneMap = new Gson().fromJson(inPrefs, new TypeToken<HashMap<String, ChatUser>>() {
                }.getType());
                return this.myUsersNameInPhoneMap;
            } else {
                return null;
            }
        }
    }

    public void setCacheMyUsers(ArrayList<ChatUser> myUsers) {
        if (this.myUsersNameInPhoneMap == null) {
            this.myUsersNameInPhoneMap = new HashMap<>();
        }
        this.myUsersNameInPhoneMap.clear();
        Log.i("autolog", "getLoggedInUser(): " + getLoggedInUser());
        ChatUser me = getLoggedInUser();
        me.setNameToDisplay("You");
        this.myUsersNameInPhoneMap.put(me.getId(), me);
        for (ChatUser user : myUsers) {
            this.myUsersNameInPhoneMap.put(user.getId(), user);
        }
        sharedPreferenceHelper.setStringPreference(USER_NAME_CACHE, new Gson().toJson(this.myUsersNameInPhoneMap, new TypeToken<HashMap<String, ChatUser>>() {
        }.getType()));
    }

    public static String getMimeType(Context context, Uri uri) {
        String mimeType;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = context.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    public static void loadUrl(Context context, String url) {
        Uri uri = Uri.parse(url);
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
        intentBuilder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary));
        intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        intentBuilder.addDefaultShareMenuItem();
        intentBuilder.enableUrlBarHiding();
        CustomTabsIntent customTabsIntent = intentBuilder.build();
        customTabsIntent.launchUrl(context, uri);
    }

    public static Realm getRealmInstance() {
        RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        return Realm.getInstance(config);
    }

    public static String getChatChild(String userId, String myId) {
        String[] temp = {userId, myId};
        Arrays.sort(temp);
        return temp[0] + "-" + temp[1];
    }

    public static int getDisplayWidth(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static RealmQuery<Chat> getChat(Realm rChatDb, String myId, String userId) {
        return rChatDb.where(Chat.class).equalTo("myId", myId)
                .equalTo("userId", userId);
    }

    public static String getTimeAgoLastSeen(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }
        long now = System.currentTimeMillis();

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            return formatter.format(calendar.getTime());
        }
    }

    public static String timeFormater(float time) {
        long secs = (long) (time / 1000);
        long mins = (long) ((time / 1000) / 60);
        long hrs = (long) (((time / 1000) / 60) / 60);
        secs = secs % 60;
        String seconds = String.valueOf(secs);
        if (secs == 0) {
            seconds = "00";
        }
        if (secs < 10 && secs > 0) {
            seconds = "0" + seconds;
        }
        mins = mins % 60;
        String minutes = String.valueOf(mins);
        if (mins == 0) {
            minutes = "00";
        }
        if (mins < 10 && mins > 0) {
            minutes = "0" + minutes;
        }
        String hours = String.valueOf(hrs);
        if (hrs == 0) {
            hours = "00";
        }
        if (hrs < 10 && hrs > 0) {
            hours = "0" + hours;
        }
        return hours + ":" + minutes + ":" + seconds;
    }

    public static void deleteMessageFromRealm(Realm rChatDb, String msgId) {
        final Message result = rChatDb.where(Message.class).equalTo("id", msgId).findFirst();
        if (result != null) {
            rChatDb.executeTransaction(realm ->
                    RealmObject.deleteFromRealm(result));
        }
    }

    public static void closeKeyboard(Context context, View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public boolean isUserMute(String userId) {
        String muteUsersPref = sharedPreferenceHelper.getStringPreference(USER_MUTE);
        if (muteUsersPref != null) {
            HashSet<String> muteUsersSet = gson.fromJson(muteUsersPref, new TypeToken<HashSet<String>>() {
            }.getType());
            return muteUsersSet.contains(userId);
        } else {
            return false;
        }
    }

    public boolean isLoggedIn() {
        return sharedPreferenceHelper.getStringPreference(USER) != null;
    }

    public static String getChatFormattedDate(long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "h:mm aa";
        final String dateTimeFormatString = "dd/MM/yyyy";
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            return "" + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
            return "Yesterday";
        } else {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        }
    }
}