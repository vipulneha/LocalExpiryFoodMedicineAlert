package com.techvipul.localexpiryfoodmedicinealert;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText editTextName, editTextExpiryDate, editTextDescription;
    private TextInputLayout layoutName, layoutExpiryDate, layoutDescription;
    private MaterialButton buttonAddItem, buttonViewAllItems, buttonDeletedItems, buttonSettings;
    private DatabaseHelper dbHelper;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Add Expiry Item");

        // Set status bar appearance
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        // Initialize UI components
        editTextName = findViewById(R.id.editTextName);
        editTextExpiryDate = findViewById(R.id.editTextExpiryDate);
        editTextDescription = findViewById(R.id.editTextDescription);
        layoutName = findViewById(R.id.layoutName);
        layoutExpiryDate = findViewById(R.id.layoutExpiryDate);
        layoutDescription = findViewById(R.id.layoutDescription);
        buttonAddItem = findViewById(R.id.buttonAddItem);
        buttonViewAllItems = findViewById(R.id.buttonViewAllItems);
        buttonDeletedItems = findViewById(R.id.buttonDeletedItems);
        buttonSettings = findViewById(R.id.buttonSettings);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Initialize Calendar for DatePicker
        calendar = Calendar.getInstance();

        // Set up DatePickerDialog for Expiry Date
        editTextExpiryDate.setOnClickListener(v -> showDatePickerDialog());

        // Add Item button click listener
        buttonAddItem.setOnClickListener(v -> {
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction(() ->
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100)).start();
            addItem();
        });

        // Navigation button listeners
        buttonViewAllItems.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ItemListActivity.class)));
        buttonDeletedItems.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DeletedItemsActivity.class)));
        buttonSettings.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SettingsActivity.class)));
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    editTextExpiryDate.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        // Set minimum date to today to disable past dates
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); // Subtract 1000ms to include today
        datePickerDialog.show();
    }

    private void addItem() {
        String name = editTextName.getText().toString().trim();
        String expiryDate = editTextExpiryDate.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        // Validation
        if (name.isEmpty()) {
            layoutName.setError("Item name is required");
            return;
        } else {
            layoutName.setError(null);
        }

        if (expiryDate.isEmpty()) {
            layoutExpiryDate.setError("Expiry date is required");
            return;
        } else {
            layoutExpiryDate.setError(null);
            // Check if expiry date is before today (as a fallback)
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            try {
                Date selectedDate = sdf.parse(expiryDate);
                Date today = new Date();
                if (selectedDate.before(today)) {
                    Toast.makeText(this, "Expiry date cannot be in the past", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (Exception e) {
                layoutExpiryDate.setError("Invalid date format");
                return;
            }
        }

        // Create ItemModel and save to database
        ItemModel item = new ItemModel(-1, name, expiryDate, description, null);
        boolean success = dbHelper.addItem(item);

        if (success) {
            Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show();
            clearFields();
        } else {
            Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        editTextName.setText("");
        editTextExpiryDate.setText("");
        editTextDescription.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}