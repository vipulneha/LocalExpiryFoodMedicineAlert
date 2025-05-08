package com.techvipul.localexpiryfoodmedicinealert;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private DatabaseHelper dbHelper;
    private List<ItemModel> itemList;
    private static final int SORT_NAME_ASC = 1;
    private static final int SORT_NAME_DESC = 2;
    private static final int SORT_EXPIRY_SOON = 3;

    private ImageView imageEmpty;
    private TextView textEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        setTitle("All Items");

        dbHelper = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.recyclerViewItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        imageEmpty = findViewById(R.id.imageEmpty);
        textEmpty = findViewById(R.id.textEmpty);

        itemList = dbHelper.getAllItems();
        itemAdapter = new ItemAdapter(this, itemList, item -> {
            Intent intent = new Intent(ItemListActivity.this, UpdateItemActivity.class);
            intent.putExtra("item_id", item.getId());
            startActivity(intent);
        }, item -> {
            // OnLongClick: Show confirmation dialog before deletion
            showDeleteConfirmationDialog(item);
            return true;
        });
        recyclerView.setAdapter(itemAdapter);

        toggleEmptyState();

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterItems(newText);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sort_name_asc) {
            sortItems(SORT_NAME_ASC);
        } else if (id == R.id.sort_name_desc) {
            sortItems(SORT_NAME_DESC);
        } else if (id == R.id.sort_expiry_soon) {
            sortItems(SORT_EXPIRY_SOON);
        }
        return super.onOptionsItemSelected(item);
    }

    private void sortItems(int sortType) {
        Collections.sort(itemList, (item1, item2) -> {
            switch (sortType) {
                case SORT_NAME_ASC:
                    return item1.getName().compareToIgnoreCase(item2.getName());
                case SORT_NAME_DESC:
                    return item2.getName().compareToIgnoreCase(item1.getName());
                case SORT_EXPIRY_SOON:
                    return item1.getExpiryDate().compareTo(item2.getExpiryDate());
                default:
                    return 0;
            }
        });
        itemAdapter.notifyDataSetChanged();
    }

    private void filterItems(String query) {
        List<ItemModel> filteredList = new ArrayList<>();
        for (ItemModel item : dbHelper.getAllItems()) {
            if (item.getName().toLowerCase().contains(query.toLowerCase()) ||
                    (item.getDescription() != null && item.getDescription().toLowerCase().contains(query.toLowerCase()))) {
                filteredList.add(item);
            }
        }
        itemList.clear();
        itemList.addAll(filteredList);
        itemAdapter.notifyDataSetChanged();
        toggleEmptyState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        itemList.clear();
        itemList.addAll(dbHelper.getAllItems());
        itemAdapter.notifyDataSetChanged();
        toggleEmptyState();
    }

    private void toggleEmptyState() {
        boolean isEmpty = itemList.isEmpty();
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        imageEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        textEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private void showDeleteConfirmationDialog(final ItemModel item) {
        // Create and show a confirmation dialog
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to delete this item?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // On clicking "Yes", delete the item
                        dbHelper.deleteItem(item.getId());
                        itemList.remove(item);
                        itemAdapter.notifyDataSetChanged();
                        toggleEmptyState();
                    }
                })
                .setNegativeButton("No", null) // On clicking "No", do nothing
                .show();
    }
}
