package com.example.lab1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "eco_assistant.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_PRODUCTS = "products";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_PRICE = "price";
    public static final String COL_EXPIRY = "expiry_date";
    public static final String COL_CATEGORY = "category";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_STORE = "store";
    public static final String COL_PURCHASE = "purchase_date";

    private static final String CREATE_PRODUCTS =
            "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_NAME + " TEXT NOT NULL, " +
                    COL_PRICE + " REAL NOT NULL, " +
                    COL_EXPIRY + " TEXT, " +
                    COL_CATEGORY + " TEXT, " +
                    COL_DESCRIPTION + " TEXT, " +
                    COL_STORE + " TEXT, " +
                    COL_PURCHASE + " TEXT" +
                    ");";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PRODUCTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    public long insertProduct(Product p) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME, p.getName());
        cv.put(COL_PRICE, p.getPrice());
        cv.put(COL_EXPIRY, p.getExpiryDate());
        cv.put(COL_CATEGORY, p.getCategory());
        cv.put(COL_DESCRIPTION, p.getDescription());
        cv.put(COL_STORE, p.getStore());
        cv.put(COL_PURCHASE, p.getPurchaseDate());
        long id = db.insert(TABLE_PRODUCTS, null, cv);
        db.close();
        return id;
    }

    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_PRODUCTS, null, null, null, null, null, COL_EXPIRY + " ASC");

        if (c.moveToFirst()) {
            do {
                long id = c.getLong(c.getColumnIndexOrThrow(COL_ID));
                String name = c.getString(c.getColumnIndexOrThrow(COL_NAME));
                double price = c.getDouble(c.getColumnIndexOrThrow(COL_PRICE));
                String expiry = c.getString(c.getColumnIndexOrThrow(COL_EXPIRY));
                String category = c.getString(c.getColumnIndexOrThrow(COL_CATEGORY));
                String desc = c.getString(c.getColumnIndexOrThrow(COL_DESCRIPTION));
                String store = c.getString(c.getColumnIndexOrThrow(COL_STORE));
                String purchase = c.getString(c.getColumnIndexOrThrow(COL_PURCHASE));

                Product p = new Product(id, name, price, expiry, category, desc, store, purchase);
                list.add(p);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }

    public Product getProductById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_PRODUCTS, null, COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        Product p = null;
        if (c.moveToFirst()) {
            String name = c.getString(c.getColumnIndexOrThrow(COL_NAME));
            double price = c.getDouble(c.getColumnIndexOrThrow(COL_PRICE));
            String expiry = c.getString(c.getColumnIndexOrThrow(COL_EXPIRY));
            String category = c.getString(c.getColumnIndexOrThrow(COL_CATEGORY));
            String desc = c.getString(c.getColumnIndexOrThrow(COL_DESCRIPTION));
            String store = c.getString(c.getColumnIndexOrThrow(COL_STORE));
            String purchase = c.getString(c.getColumnIndexOrThrow(COL_PURCHASE));
            p = new Product(id, name, price, expiry, category, desc, store, purchase);
        }
        c.close();
        db.close();
        return p;
    }
}
