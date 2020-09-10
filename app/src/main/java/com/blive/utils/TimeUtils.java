package com.blive.utils;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

/**
 * Created by sans on 14/02/18.
 **/

public abstract class TimeUtils {
    static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    private static final int SECOND = 1000;
    private static final int MINUTE = 60 * SECOND;
    private static final int HOUR = 60 * MINUTE;
    public static final int DAY = 24 * HOUR;
    private static final SimpleDateFormat[] ACCEPTED_TIMESTAMP_FORMATS = {
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US),
            new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.US),
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US),
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss Z", Locale.US)
    };
    private static final SimpleDateFormat VALID_IFMODIFIEDSINCE_FORMAT =
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);

    public static Date parseTimestamp(String timestamp) {
        //it may be an integer - milliseconds
        try {
            if (timestamp.matches("[0-9]+")) {
                long longDate = Long.parseLong(timestamp);
                return new Date(longDate);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        for (SimpleDateFormat format : ACCEPTED_TIMESTAMP_FORMATS) {
            // TODO: We shouldn't be forcing the time zone when parsing dates.
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
            try {
                return format.parse(timestamp);
            } catch (ParseException ex) {
                continue;
            }
        }
        // didn't match any format
        return null;
    }

    public static boolean isSameDay(long time1, long time2, Context context) {
        TimeZone displayTimeZone = TimeUtils.getTimeZone();
        Calendar cal1 = Calendar.getInstance(displayTimeZone);
        Calendar cal2 = Calendar.getInstance(displayTimeZone);
        cal1.setTimeInMillis(time1);
        cal2.setTimeInMillis(time2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null)
            throw new IllegalArgumentException("Calender object cannot be null");
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isValidFormatForIfModifiedSinceHeader(String timestamp) {
        try {
            return VALID_IFMODIFIEDSINCE_FORMAT.parse(timestamp) != null;
        } catch (Exception ex) {
            return false;
        }
    }

    public static long timestampToMillis(String timestamp, long defaultValue) {
        if (TextUtils.isEmpty(timestamp)) {
            return defaultValue;
        }
        Date d = parseTimestamp(timestamp);
        return d == null ? defaultValue : d.getTime();
    }

    /**
     * Format a {@code date_picker_blue} honoring the app preference for using device timezone.
     * {@code Context} is used to lookup the shared preference settings.
     */
    public static String formatShortDate(Context context, Date date) {
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb);
        return DateUtils.formatDateRange(context, formatter, date.getTime(), date.getTime(),
                DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_NO_YEAR,
                getDisplayTimeZone(context).getID()).toString();
    }

    public static String formatShortTime(Context context, Date time) {
        // Android DateFormatter will honor the user's current settings.
        DateFormat format = android.text.format.DateFormat.getTimeFormat(context);
        // Override with Timezone based on settings since users can override their phone's timezone
        // with Pacific time zones.
        TimeZone tz = getDisplayTimeZone(context);
        if (tz != null) {
            format.setTimeZone(tz);
        }
        return format.format(time);
    }

    public static TimeZone getTimeZone() {
        return TimeZone.getDefault();
    }

    public static long getCurrentTimeInMs() {
        return System.currentTimeMillis();
    }

    /**
     * Returns "Today", "Tomorrow", "Yesterday", or a short date_picker_blue format.
     */
    public static String formatHumanFriendlyShortDate(final Context context, long timestamp) {
        long localTimestamp, localTime;
        long now = TimeUtils.getCurrentTimeInMs();

        TimeZone tz = getDisplayTimeZone(context);
        localTimestamp = timestamp + tz.getOffset(timestamp);
        localTime = now + tz.getOffset(now);

        long dayOrd = localTimestamp / 86400000L;
        long nowOrd = localTime / 86400000L;

        if (dayOrd == nowOrd) {
            //return context.getString(R.string.day_title_today);
            return new SimpleDateFormat("hh:mm a", Locale.getDefault())
                    .format(new Date(timestamp))
                    .replace("am", "AM")
                    .replace("pm", "PM");
        } else if (dayOrd == nowOrd - 1) {
            return "Yesterday";
        } else if (dayOrd == nowOrd + 1) {
            return "Tomorrow";
        } else {
            return formatShortDate(context, new Date(timestamp));
        }
    }

    public static String formatToShortTime(final Context context, long timestamp) {
        return new SimpleDateFormat("hh:mm a", Locale.getDefault())
                .format(new Date(timestamp))
                .replace("am", "AM")
                .replace("pm", "PM");
    }

    public static TimeZone getDisplayTimeZone(Context context) {
        return TimeZone.getDefault();
    }

    public static long diff(Date date1, Date date2) {
        return date1.getTime() - date2.getTime();
    }

    public static long diffInSeconds(Date date1, Date date2) {
        return TimeUnit.MILLISECONDS.toSeconds(date1.getTime() - date2.getTime());
    }

    public static long diffInMinutes(Date date1, Date date2) {
        return TimeUnit.MILLISECONDS.toMinutes(date1.getTime() - date2.getTime());
    }

    public static long diffInHours(Date date1, Date date2) {
        return TimeUnit.MILLISECONDS.toHours(date1.getTime() - date2.getTime());
    }

    public static long diffInDays(Date date1, Date date2) {
        return TimeUnit.MILLISECONDS.toDays(date1.getTime() - date2.getTime());
    }

    public static Date getCurrentTime() {
        return Calendar.getInstance().getTime();
    }

    public static boolean isToday(Date date) {
        if (date == null)
            throw new IllegalArgumentException("Date object cannot be null");
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);

        Calendar cal2 = Calendar.getInstance();

        return isSameDay(cal1, cal2);
    }
    public static boolean isAfter(String dateStr, String format) {
        DateFormat fromFormat = new SimpleDateFormat(format, Locale.getDefault());
        Date date = null;
        try {
            date = fromFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null)
            throw new IllegalArgumentException("Date object cannot be null");
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);

        Calendar cal2 = Calendar.getInstance();

        return cal1.after(cal2);
    }


    public static boolean isToday(String dateStr, String format) {
        DateFormat fromFormat = new SimpleDateFormat(format, Locale.getDefault());
        Date date = null;
        try {
            date = fromFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null)
            throw new IllegalArgumentException("Date object cannot be null");
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);

        Calendar cal2 = Calendar.getInstance();

        return isSameDay(cal1, cal2);
    }

    public static String getDateString(String string) {
        DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        DateFormat toFormat = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault());
        Date date = null;
        try {
            date = fromFormat.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return toFormat.format(date);
    }

    public static String getYearString(String string) {
        DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        DateFormat toFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        Date date = null;
        try {
            date = fromFormat.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return toFormat.format(date);
    }

    public static String getDateStringProfile(String string) {
        DateFormat fromFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        DateFormat toFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        Date date = null;
        try {
            date = fromFormat.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return toFormat.format(date);
    }

    public static String getTimeString(String dateTimeString) {
        DateFormat inputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        DateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        String convertedTime = null;
        try {
            convertedTime = outputFormat.format(inputFormat.parse(dateTimeString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedTime != null ? convertedTime
                .replace("am", "AM")
                .replace("pm", "PM") : null;
    }

    public static String getFormattedDateString(String dateTimeString,String inputFrmt, String outputFrmt) {
        DateFormat inputFormat = new SimpleDateFormat(inputFrmt, Locale.getDefault());
        DateFormat outputFormat = new SimpleDateFormat(outputFrmt, Locale.getDefault());

        String convertedDate = null;
        try {
            convertedDate = outputFormat.format(inputFormat.parse(dateTimeString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }

    public static Date getDateFromString(String dateString) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date myDate = null;
        try {
            myDate = simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return myDate;
    }

    public static String printDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        Log.e(TAG, "printDifference: startDate : " + startDate);
        Log.e(TAG, "printDifference: endDate : "+ endDate);
        Log.e(TAG, "printDifference: different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        Log.e(TAG, "%d days, %d hours, %d minutes, %d seconds%n" +elapsedDays+" "+ elapsedHours+" "+ elapsedMinutes);
        String diff = String.valueOf(elapsedHours+"h")+" "+String.valueOf(elapsedMinutes+"m");
        diff = diff.replace("-","");

        return diff;
    }

    public static String getTimeFromString(String iDF, String oDF, String dateTimeString) {
        DateFormat inputFormat = new SimpleDateFormat(iDF, Locale.getDefault());
        DateFormat outputFormat = new SimpleDateFormat(oDF, Locale.getDefault());

        String convertedTime = null;
        try {
            convertedTime = outputFormat.format(inputFormat.parse(dateTimeString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedTime != null ? convertedTime
                .replace("am", "AM")
                .replace("pm", "PM") : null;
    }

    public static String getTimeFromStringIn24Hours(String dateTimeString){

        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        DateFormat outputFormat = new SimpleDateFormat("HH:mm ", Locale.getDefault());

        String convertedTime = null;
        try {
            convertedTime = outputFormat.format(inputFormat.parse(dateTimeString));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return convertedTime;
    }

    public static String getDate(String dateTimeString){

        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy ", Locale.getDefault());

        String convertedTime = null;
        try {
            convertedTime = outputFormat.format(inputFormat.parse(dateTimeString));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return convertedTime;
    }


    public static List<Date> getDates(String dateString1, String dateString2, String sdf) {
        ArrayList<Date> dates = new ArrayList<>();
        DateFormat df1 = new SimpleDateFormat(sdf, Locale.getDefault());

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1 .parse(dateString1);
            date2 = df1 .parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);


        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        while(!cal1.after(cal2))
        {
            dates.add(cal1.getTime());
            cal1.add(Calendar.DATE, 1);
        }

        return dates;
    }

    public static boolean isTodayBetween(String format, String fromDate, String toDate) {
        DateFormat df1 = new SimpleDateFormat(format, Locale.getDefault());
        Date date1 = null;
        Date date2 = null;
        Date today=null;

        try {
            date1 = df1 .parse(fromDate);
            date2 = df1 .parse(toDate);
            today=df1.parse(getCurrentDateTime(format));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date1.compareTo(today) * today.compareTo(date2) >= 0;

    }

    private static String getCurrentDateTime(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        String dateTime = sdf.format(new Date());
        return dateTime;
    }

    public static String getTodaysDate(String format){
        Date d = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat(format,Locale.getDefault());
        String date = df.format(d);
        return date;
    }

    public static String addDays(String format,String date,int days,String outputFormat){
        SimpleDateFormat sdf = new SimpleDateFormat(format,Locale.getDefault());
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, days);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
        SimpleDateFormat sdf1 = new SimpleDateFormat(outputFormat,Locale.getDefault());
        String addedDate = sdf1.format(c.getTime());
        return addedDate;
    }

    public static String addHours(String format,String time,int hours){
        SimpleDateFormat sdf = new SimpleDateFormat(format,Locale.getDefault());
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.HOUR, hours);
        SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm a",Locale.getDefault());
        String changedTime = sdf1.format(c.getTime());
        return changedTime;
    }

    public static String addHoursToTime(String format,String time,int hours){
        SimpleDateFormat sdf = new SimpleDateFormat(format,Locale.getDefault());
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.HOUR, hours);
        SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm a",Locale.getDefault());
        String changedTime = sdf1.format(c.getTime());
        return changedTime;
    }

    public static String Convert12to24(String time) {
        String convertedTime ="";
        try {
            SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a",Locale.getDefault());
            SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm:ss",Locale.getDefault());
            Date date = parseFormat.parse(time);
            convertedTime = displayFormat.format(date);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return convertedTime;
    }
}
