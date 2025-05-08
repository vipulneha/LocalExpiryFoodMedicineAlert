package com.techvipul.localexpiryfoodmedicinealert;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.core.app.NotificationCompat;

import java.util.List;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "ExpiryAlerts";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        if (!prefs.getBoolean("notifications_enabled", true)) {
            return;
        }

        int reminderDays = prefs.getInt("reminder_days", 3);
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        List<ItemModel> expiringItems = dbHelper.getItemsExpiringSoon(reminderDays);

        if (!expiringItems.isEmpty()) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Create notification channel for Android O+
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "Expiry Alerts",
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                notificationManager.createNotificationChannel(channel);
            }

            // Build notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.ic_dialog_alert)
                    .setContentTitle("Expiry Alert")
                    .setContentText(expiringItems.size() + " item(s) expiring soon")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            // Issue notification
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}