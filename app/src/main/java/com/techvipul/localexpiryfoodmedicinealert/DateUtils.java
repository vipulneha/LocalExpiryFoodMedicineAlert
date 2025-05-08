package com.techvipul.localexpiryfoodmedicinealert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static boolean isDateBeforeToday(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date date = sdf.parse(dateStr);
            Date today = new Date();
            return date.before(today);
        } catch (ParseException e) {
            return false;
        }
    }

    public static long daysUntilExpiry(String expiryDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Calendar expiryCal = Calendar.getInstance();
            expiryCal.setTime(sdf.parse(expiryDate));
            Calendar todayCal = Calendar.getInstance();
            long diff = expiryCal.getTimeInMillis() - todayCal.getTimeInMillis();
            return diff / (1000 * 60 * 60 * 24);
        } catch (ParseException e) {
            return Long.MAX_VALUE;
        }
    }
}