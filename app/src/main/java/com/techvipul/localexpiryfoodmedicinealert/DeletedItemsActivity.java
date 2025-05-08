package com.techvipul.localexpiryfoodmedicinealert;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DeletedItemsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DeletedItemAdapter deletedItemAdapter;
    private DatabaseHelper dbHelper;
    private List<DeletedItemModel> deletedItemList;
    private ImageView noDataImageView; // Reference for the no-data image view
    private TextView noDataTextView;  // Reference for the "No data available" text

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deleted_items);
        setTitle("Deleted Items");

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewDeletedItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the no-data ImageView and TextView
        noDataImageView = findViewById(R.id.imageViewNoData);
        noDataTextView = findViewById(R.id.textNoDataAvailable);

        // Load deleted items
        deletedItemList = dbHelper.getAllDeletedItems();
        deletedItemAdapter = new DeletedItemAdapter(this, deletedItemList, item -> {
            // Restore item
            dbHelper.restoreItem(item);
            deletedItemList.remove(item);
            deletedItemAdapter.notifyDataSetChanged();
            checkIfDataIsEmpty(); // Check if data is empty after removal
        }, item -> {
            // Show confirmation dialog before permanently deleting item
            showDeleteConfirmationDialog(item);
            return ;
        });
        recyclerView.setAdapter(deletedItemAdapter);

        // Set up SearchView
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterDeletedItems(newText);
                return true;
            }
        });

        // Initial check if data is empty
        checkIfDataIsEmpty();
    }

    private void filterDeletedItems(String query) {
        List<DeletedItemModel> filteredList = new ArrayList<>();
        for (DeletedItemModel item : dbHelper.getAllDeletedItems()) {
            if (item.getName().toLowerCase().contains(query.toLowerCase()) ||
                    (item.getDescription() != null && item.getDescription().toLowerCase().contains(query.toLowerCase()))) {
                filteredList.add(item);
            }
        }
        deletedItemList.clear();
        deletedItemList.addAll(filteredList);
        deletedItemAdapter.notifyDataSetChanged();

        // Check if data is empty after filtering
        checkIfDataIsEmpty();
    }

    private void checkIfDataIsEmpty() {
        if (deletedItemList.isEmpty()) {
            // Show the no-data image and text
            noDataImageView.setVisibility(View.VISIBLE);
            noDataTextView.setVisibility(View.VISIBLE);
        } else {
            // Hide the no-data image and text
            noDataImageView.setVisibility(View.GONE);
            noDataTextView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh list
        deletedItemList.clear();
        deletedItemList.addAll(dbHelper.getAllDeletedItems());
        deletedItemAdapter.notifyDataSetChanged();

        // Check if data is empty after refresh
        checkIfDataIsEmpty();
    }

    private void showDeleteConfirmationDialog(final DeletedItemModel item) {
        // Create and show a confirmation dialog before permanently deleting
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to permanently delete this item?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Permanently delete the item
                        dbHelper.permanentlyDeleteItem(item.getId());
                        deletedItemList.remove(item);
                        deletedItemAdapter.notifyDataSetChanged();

                        // Check if data is empty after deletion
                        checkIfDataIsEmpty();
                    }
                })
                .setNegativeButton("No", null) // On clicking "No", do nothing
                .show();
    }
}
