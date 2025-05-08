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

public class UpdateItemActivity extends AppCompatActivity {

    private TextInputEditText editTextName, editTextExpiryDate, editTextDescription;
    private TextInputLayout layoutName, layoutExpiryDate, layoutDescription;
    private MaterialButton buttonUpdateItem;
    private DatabaseHelper dbHelper;
    private Calendar calendar;
    private ItemModel item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_item);
        setTitle("Update Item");

        // Initialize UIme components
        editTextName = findViewById(R.id.editTextName);
        editTextExpiryDate = findViewById(R.id.editTextExpiryDate);
        editTextDescription = findViewById(R.id.editTextDescription);
        layoutName = findViewById(R.id.layoutName);
        layoutExpiryDate = findViewById(R.id.layoutExpiryDate);
        layoutDescription = findViewById(R.id.layoutDescription);
        buttonUpdateItem = findViewById(R.id.buttonUpdateItem);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Initialize Calendar
        calendar = Calendar.getInstance();

        // Get item ID from Intent
        int itemId = getIntent().getIntExtra("item_id", -1);
        item = dbHelper.getItemById(itemId);

        if (item != null) {
            // Pre-fill form
            editTextName.setText(item.getName());
            editTextExpiryDate.setText(item.getExpiryDate());
            editTextDescription.setText(item.getDescription());
        } else {
            Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Set up DatePickerDialog
        editTextExpiryDate.setOnClickListener(v -> showDatePickerDialog());

        // Update button listener
        buttonUpdateItem.setOnClickListener(v -> {
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction(() ->
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100)).start();
            updateItem();
        });
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
        datePickerDialog.show();
    }

    private void updateItem() {
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
            // Check if expiry date is before today
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

        // Update item
        item.setName(name);
        item.setExpiryDate(expiryDate);
        item.setDescription(description);
        boolean success = dbHelper.updateItem(item);

        if (success) {
            Toast.makeText(this, "Item updated successfully", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Failed to update item", Toast.LENGTH_SHORT).show();
        }
    }
}