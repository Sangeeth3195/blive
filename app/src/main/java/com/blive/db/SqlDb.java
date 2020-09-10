package com.blive.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.blive.model.ChatMessage;
import com.blive.model.FCMModel;
import com.blive.model.Gift;
import com.blive.session.SessionManager;
import com.blive.session.SessionNotification;

import java.util.ArrayList;

import static io.fabric.sdk.android.Fabric.TAG;

public class SqlDb extends SQLiteOpenHelper {


    Context context;
    SessionManager sessionManager;
    public static String DATABASE_NAME = "gift_downloaded_data";
    public static int DATABASE_VERSION = 2;
    public static String TBALE_GIFT = "gift";

    private static final String giftTableId = "gift_auto_id";
    private static final String giftId = "gift_id";
    private static final String giftName = "gift_name";
    private static final String giftUrl = "gift_url";
    private static final String giftThumbanil = "gift_thumbnail";
    private static final String giftImage = "gift_image";
    private static String giftDuration = "gift_duration";
    private static String giftType = "gift_type";
    private static String giftKey = "gift_key";
    private static String giftPath = "gift_path";
    private static String giftPrice = "gift_price";
    private static String giftIcon = "gift_icon";

    public static String CHAT_TABLE = "chat_table";
    public static String MESSAGE_ORDER = "message_order";
    public static String MESSAGE_ID = "message_id";
    public static String CHANNEL_URL = "channel_url";
    public static String MESSAGE_TIME = "message_ts";
    public static String MESSAGE_DATA = "payload";
    public static String MESSAGE_PATH = "message_path";
    public static String FREE_GIFT_TABLE = "free_gift_table";
    public static String FREE_GIFT_COUNT = "free_gift_count";
    public static String FREE_GIFT_ID = "free_gift_id";
    public static String FREE_GIFT_TIME = "free_gift_time";

    public static String NOTIFICATION_TABLE = "notification_table";
    public static String NOTIFICATION_COUNT = "notification_count";
    public static String NOTIFICATION_TITLE = "notification_title";
    public static String NOTIFICATION_CONTENT = "notification_content";
    public static String NOTIFICATION_IMAGE = "notification_image";
    public static String NOTIFICATION_DATE = "notification_date";


    public String CREATE_TABLE_GIFT = " CREATE TABLE " + TBALE_GIFT + "( " + giftTableId + " integer primary key, "
            + giftId + " VARCHAR, " + giftName + " VARCHAR, " + giftUrl + " VARCHAR, " + giftThumbanil + " VARCHAR, " + giftIcon + " VARCHAR, " +
            giftImage + " BLOB, " + giftDuration + " int, " + giftType + " VARCHAR, " + giftPrice + " VARCHAR, "
            + giftKey + " VARCHAR, " + giftPath + " VARCHAR " + ") ";

//    public String CREATE_TABLE_CHAT = " CREATE TABLE " + CHAT_TABLE + "( " + MESSAGE_ORDER + " integer primary key, "
//            + MESSAGE_ID + " LONG, " + CHANNEL_URL + " VARCHAR, " + MESSAGE_PATH + " VARCHAR, " + MESSAGE_TIME + " LONG, " + MESSAGE_DATA + " BLOB "+ ") ";

    public String CREATE_TABLE_CHAT = " CREATE TABLE " + CHAT_TABLE + "( " + FREE_GIFT_ID + " integer primary key, " +
            FREE_GIFT_COUNT + " LONG, " +
            FREE_GIFT_TIME + " VARCHAR " + ") ";

    public String CREATE_TABLE_FREE_GIFT = " CREATE TABLE " + FREE_GIFT_TABLE + "( " + FREE_GIFT_COUNT + " integer primary key, " +
            MESSAGE_ID + " LONG, " +
            MESSAGE_PATH + " VARCHAR " + ") ";

    public String CREATE_TABLE_NOTIFICATION = " CREATE TABLE " + NOTIFICATION_TABLE + "( " + NOTIFICATION_COUNT + " integer primary key, " +
            NOTIFICATION_TITLE + " VARCHAR, " +
            NOTIFICATION_IMAGE + " VARCHAR, " +
            NOTIFICATION_DATE + " VARCHAR, " +
            NOTIFICATION_CONTENT + " VARCHAR " + ") ";


    public SqlDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        sessionManager=new SessionManager(context);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_GIFT);
        sqLiteDatabase.execSQL(CREATE_TABLE_CHAT);
        sqLiteDatabase.execSQL(CREATE_TABLE_FREE_GIFT);
        sqLiteDatabase.execSQL(CREATE_TABLE_NOTIFICATION);
        Log.e("Database OPERATIONS", "Table created...");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersionb, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TBALE_GIFT);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CHAT_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FREE_GIFT_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_NOTIFICATION);
        onCreate(sqLiteDatabase);
        Log.e("Database OPERATIONS", "Table Updated...");
    }

    public long insertGift(Gift giftDbData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(giftId, giftDbData.getGiftId());
        contentValues.put(giftName, giftDbData.getName());
        contentValues.put(giftUrl, giftDbData.getGiftUrl());
        contentValues.put(giftThumbanil, giftDbData.getThumbnail());
        contentValues.put(giftType, giftDbData.getType());
        contentValues.put(giftDuration, giftDbData.getDuration());
        contentValues.put(giftPrice, giftDbData.getPrice());
        contentValues.put(giftIcon, giftDbData.getGiftIcon());
//        contentValues.put(giftImage,"");
        //      contentValues.put(giftKey,"");
        contentValues.put(giftPath, giftDbData.getGiftpath());
        long inserted = db.insert(TBALE_GIFT, null, contentValues);
        return inserted;
    }

    public long insertMessageData(ChatMessage chatMessage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MESSAGE_ID, chatMessage.getMessageId());
       /*  contentValues.put(MESSAGE_DATA,chatMessage.getMessadeData());
        contentValues.put(CHANNEL_URL, chatMessage.getChannelUrl());
        contentValues.put(MESSAGE_TIME, chatMessage.getMessageTime());*/
        contentValues.put(MESSAGE_PATH, chatMessage.getMessagePath());
        long inserted = db.insert(CHAT_TABLE, null, contentValues);
        return inserted;
    }

    public long insertNotificationData(FCMModel fcmModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTIFICATION_IMAGE, fcmModel.getNotificationImage());
       /*  contentValues.put(MESSAGE_DATA,chatMessage.getMessadeData());
        contentValues.put(CHANNEL_URL, chatMessage.getChannelUrl());
        contentValues.put(MESSAGE_TIME, chatMessage.getMessageTime());*/
        contentValues.put(NOTIFICATION_TITLE, fcmModel.getNotificationTitle());
        contentValues.put(NOTIFICATION_DATE, fcmModel.getDate());
        contentValues.put(NOTIFICATION_CONTENT, fcmModel.getNotificationcontent());

        long inserted = db.insert(NOTIFICATION_TABLE, null, contentValues);



//        int count= Integer.parseInt(sessionManager.getSessionStringValue("notification","notification"));
//        count=count+1;
//        sessionManager.storeSessionStringvalue("notification","notification", String.valueOf(count));




        return inserted;
    }

    public long insertFreeGift(String giftCount, String giftTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FREE_GIFT_COUNT, giftCount);
        contentValues.put(FREE_GIFT_TIME, giftTime);
        long inserted = db.insert(FREE_GIFT_TABLE, null, contentValues);
        return inserted;
    }

    public ArrayList<Gift> getGiftData() {

        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Gift> livelist = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TBALE_GIFT;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Gift giftDbData = new Gift();
                giftDbData.setGiftId(cursor.getString(cursor.getColumnIndex(giftId)));
                giftDbData.setGiftUrl(cursor.getString(cursor.getColumnIndex(giftUrl)));
                giftDbData.setName(cursor.getString(cursor.getColumnIndex(giftName)));
                giftDbData.setThumbnail(cursor.getString(cursor.getColumnIndex(giftThumbanil)));
                giftDbData.setType(cursor.getString(cursor.getColumnIndex(giftType)));
                giftDbData.setDuration(cursor.getString(cursor.getColumnIndex(giftDuration)));
//                giftDbData.setGiftImage(cursor.getBlob(cursor.getColumnIndex(giftImage)));
//                giftDbData.setGiftKey(cursor.getString(cursor.getColumnIndex(giftKey)));
                giftDbData.setGiftpath(cursor.getString(cursor.getColumnIndex(giftPath)));
                giftDbData.setPrice(cursor.getString(cursor.getColumnIndex(giftPrice)));
                giftDbData.setIcon(cursor.getString(cursor.getColumnIndex(giftIcon)));

                livelist.add(giftDbData);

            } while (cursor.moveToNext());
        }
        db.close();
        return livelist;
    }


    public Gift getFreeGift() {
        SQLiteDatabase db = this.getReadableDatabase();
        Gift giftDbData = new Gift();
        String selectQuery = "SELECT * FROM " + FREE_GIFT_TIME + " ORDER BY " + FREE_GIFT_ID + "  DESC LIMIT 1";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                giftDbData.setFreeGiftId(cursor.getInt(cursor.getColumnIndex(FREE_GIFT_ID)));
                giftDbData.setFreeGiftTime(cursor.getString(cursor.getColumnIndex(FREE_GIFT_TIME)));
                giftDbData.setFreeGiftCount(cursor.getInt(cursor.getColumnIndex(FREE_GIFT_COUNT)));
            } while (cursor.moveToNext());
        }
        db.close();
        return giftDbData;
    }

    public FCMModel getNotification() {
        SQLiteDatabase db = this.getReadableDatabase();
        FCMModel fcmModel = new FCMModel();
        String selectQuery = "SELECT * FROM " + NOTIFICATION_TABLE + " ORDER BY " + NOTIFICATION_TITLE + "  DESC LIMIT 1";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                fcmModel.setNotificationTitle(cursor.getString(cursor.getColumnIndex(NOTIFICATION_TITLE)));
                fcmModel.setNotificationcontent(cursor.getString(cursor.getColumnIndex(NOTIFICATION_CONTENT)));
                fcmModel.setNotificationImage(cursor.getString(cursor.getColumnIndex(NOTIFICATION_IMAGE)));
                fcmModel.setDate(cursor.getString(cursor.getColumnIndex(NOTIFICATION_DATE)));
            } while (cursor.moveToNext());
        }
        db.close();
        return fcmModel;
    }


    public ArrayList<FCMModel> getNotificationData() {

        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<FCMModel> notificationlist = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + NOTIFICATION_TABLE;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                FCMModel fcmModel = new FCMModel();
                fcmModel.setNotificationTitle(cursor.getString(cursor.getColumnIndex(NOTIFICATION_TITLE)));
                fcmModel.setNotificationcontent(cursor.getString(cursor.getColumnIndex(NOTIFICATION_CONTENT)));
                fcmModel.setNotificationImage(cursor.getString(cursor.getColumnIndex(NOTIFICATION_IMAGE)));
                fcmModel.setDate(cursor.getString(cursor.getColumnIndex(NOTIFICATION_DATE)));
                notificationlist.add(fcmModel);

            } while (cursor.moveToNext());
        }
        db.close();
        return notificationlist;
    }


    public int deleteSelectedGift(String giftName) {
        SQLiteDatabase database = this.getReadableDatabase();
        String whereQueru = giftId + " = '" + giftName + "'";
        int deletedRow = database.delete(TBALE_GIFT, whereQueru, null);
        return deletedRow;
    }

    public void deleteNotificationdata() {
        SQLiteDatabase database = this.getReadableDatabase();
        database.execSQL("delete from " + NOTIFICATION_TABLE);
    }

}
