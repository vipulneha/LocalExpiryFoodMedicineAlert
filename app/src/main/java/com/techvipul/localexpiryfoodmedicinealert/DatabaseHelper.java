package com.techvipul.localexpiryfoodmedicinealert;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ExpiryAlert.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_ITEMS = "items";
    private static final String TABLE_DELETED_ITEMS = "deleted_items";

    // Items table columns
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_EXPIRY_DATE = "expiry_date";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_IMAGE_URI = "image_uri";

    // Deleted items table columns
    private static final String COL_DELETED_TIMESTAMP = "deleted_timestamp";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createItemsTable = "CREATE TABLE " + TABLE_ITEMS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_EXPIRY_DATE + " TEXT NOT NULL, " +
                COL_DESCRIPTION + " TEXT, " +
                COL_IMAGE_URI + " TEXT)";
        db.execSQL(createItemsTable);

        String createDeletedItemsTable = "CREATE TABLE " + TABLE_DELETED_ITEMS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_EXPIRY_DATE + " TEXT NOT NULL, " +
                COL_DESCRIPTION + " TEXT, " +
                COL_IMAGE_URI + " TEXT, " +
                COL_DELETED_TIMESTAMP + " TEXT NOT NULL)";
        db.execSQL(createDeletedItemsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DELETED_ITEMS);
        onCreate(db);
    }

    public boolean addItem(ItemModel item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, item.getName());
        values.put(COL_EXPIRY_DATE, item.getExpiryDate());
        values.put(COL_DESCRIPTION, item.getDescription());
        values.put(COL_IMAGE_URI, item.getImageUri());
        long result = db.insert(TABLE_ITEMS, null, values);
        db.close();
        return result != -1;
    }

    public boolean updateItem(ItemModel item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, item.getName());
        values.put(COL_EXPIRY_DATE, item.getExpiryDate());
        values.put(COL_DESCRIPTION, item.getDescription());
        values.put(COL_IMAGE_URI, item.getImageUri());
        int result = db.update(TABLE_ITEMS, values, COL_ID + "=?", new String[]{String.valueOf(item.getId())});
        db.close();
        return result > 0;
    }

    public ItemModel getItemById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ITEMS + " WHERE " + COL_ID + "=?", new String[]{String.valueOf(id)});
        ItemModel item = null;
        if (cursor.moveToFirst()) {
            item = new ItemModel(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPIRY_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE_URI))
            );
        }
        cursor.close();
        db.close();
        return item;
    }

    public List<ItemModel> getAllItems() {
        List<ItemModel> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ITEMS, null);
        if (cursor.moveToFirst()) {
            do {
                ItemModel item = new ItemModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPIRY_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE_URI))
                );
                itemList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return itemList;
    }

    public void deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Move item to deleted_items table
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ITEMS + " WHERE " + COL_ID + "=?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            ContentValues values = new ContentValues();
            values.put(COL_NAME, cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)));
            values.put(COL_EXPIRY_DATE, cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPIRY_DATE)));
            values.put(COL_DESCRIPTION, cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)));
            values.put(COL_IMAGE_URI, cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE_URI)));
            values.put(COL_DELETED_TIMESTAMP, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date()));
            db.insert(TABLE_DELETED_ITEMS, null, values);
        }
        cursor.close();
        // Delete from items table
        db.delete(TABLE_ITEMS, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<DeletedItemModel> getAllDeletedItems() {
        List<DeletedItemModel> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DELETED_ITEMS, null);
        if (cursor.moveToFirst()) {
            do {
                DeletedItemModel item = new DeletedItemModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPIRY_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE_URI)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DELETED_TIMESTAMP))
                );
                itemList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return itemList;
    }

    public void restoreItem(DeletedItemModel deletedItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Add back to items table
        ContentValues values = new ContentValues();
        values.put(COL_NAME, deletedItem.getName());
        values.put(COL_EXPIRY_DATE, deletedItem.getExpiryDate());
        values.put(COL_DESCRIPTION, deletedItem.getDescription());
        values.put(COL_IMAGE_URI, deletedItem.getImageUri());
        db.insert(TABLE_ITEMS, null, values);
        // Delete from deleted_items table
        db.delete(TABLE_DELETED_ITEMS, COL_ID + "=?", new String[]{String.valueOf(deletedItem.getId())});
        db.close();
    }

    public void permanentlyDeleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DELETED_ITEMS, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void clearAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_ITEMS);
        db.execSQL("DELETE FROM " + TABLE_DELETED_ITEMS);
        db.close();
    }

    public List<ItemModel> getItemsExpiringSoon(int days) {
        List<ItemModel> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, days);
        String maxDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.getTime());
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ITEMS + " WHERE " + COL_EXPIRY_DATE + " <= ?", new String[]{maxDate});
        if (cursor.moveToFirst()) {
            do {
                ItemModel item = new ItemModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPIRY_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE_URI))
                );
                itemList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return itemList;
    }
}