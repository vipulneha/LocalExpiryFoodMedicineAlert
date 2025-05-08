package com.techvipul.localexpiryfoodmedicinealert;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.AlertDialog; // Import this

import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchNotifications, switchDarkMode;
    private Spinner spinnerReminderDays;
    private Button buttonExport, buttonClearData;
    private DatabaseHelper dbHelper;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");

        // Initialize components
        switchNotifications = findViewById(R.id.switchNotifications);
        switchDarkMode = findViewById(R.id.switchDarkMode);
        spinnerReminderDays = findViewById(R.id.spinnerReminderDays);
        buttonExport = findViewById(R.id.buttonExport);
        buttonClearData = findViewById(R.id.buttonClearData);
        dbHelper = new DatabaseHelper(this);
        prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);

        // Set up spinner
        Integer[] days = {1, 3, 5};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, days);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerReminderDays.setAdapter(adapter);
        spinnerReminderDays.setSelection(prefs.getInt("reminder_days", 3) / 2);

        // Load saved settings
        switchNotifications.setChecked(prefs.getBoolean("notifications_enabled", true));
        switchDarkMode.setChecked(prefs.getInt("theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) == AppCompatDelegate.MODE_NIGHT_YES);

        // Notification toggle
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("notifications_enabled", isChecked).apply();
            if (isChecked) {
                scheduleNotifications();
            } else {
                cancelNotifications();
            }
        });

        // Dark mode toggle
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int mode = isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
            AppCompatDelegate.setDefaultNightMode(mode);
            prefs.edit().putInt("theme", mode).apply();
        });

        // Reminder days
        spinnerReminderDays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int days = (Integer) parent.getItemAtPosition(position);
                prefs.edit().putInt("reminder_days", days).apply();
                if (switchNotifications.isChecked()) {
                    scheduleNotifications();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Export to CSV
        buttonExport.setOnClickListener(v -> exportToCsv());

        // Clear data with confirmation
        buttonClearData.setOnClickListener(v -> {
            // Show confirmation dialog
            new AlertDialog.Builder(SettingsActivity.this)
                    .setTitle("Clear All Data")
                    .setMessage("Are you sure you want to clear all data? This action cannot be undone.")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Clear data if user confirms
                            dbHelper.clearAllData();
                            Toast.makeText(SettingsActivity.this, "All data cleared", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void scheduleNotifications() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
    }

    private void cancelNotifications() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
    }

    private void exportToCsv() {
        try {
            File file = new File(getExternalFilesDir(null), "items_export.csv");
            FileWriter writer = new FileWriter(file);
            writer.append("ID,Name,Expiry Date,Description\n");
            for (ItemModel item : dbHelper.getAllItems()) {
                writer.append(String.format("%d,%s,%s,%s\n",
                        item.getId(),
                        item.getName().replace(",", ""),
                        item.getExpiryDate(),
                        item.getDescription() != null ? item.getDescription().replace(",", "") : ""));
            }
            writer.flush();
            writer.close();
            Toast.makeText(this, "Exported to " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
