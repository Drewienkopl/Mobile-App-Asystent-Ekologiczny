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
    private static final int DB_VERSION = 3;


    public static final String TABLE_PRODUCTS = "products";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_PRICE = "price";
    public static final String COL_EXPIRY = "expiry_date";
    public static final String COL_CATEGORY = "category";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_STORE = "store";
    public static final String COL_PURCHASE = "purchase_date";

    public static final String COL_USED = "used";

    public static final String TABLE_DEPOSITS = "deposits";
    public static final String COL_D_TYPE = "type";
    public static final String COL_D_VALUE = "value";
    public static final String COL_D_BARCODE = "barcode";
    public static final String COL_D_RETURNED = "returned";

    private static final String CREATE_DEPOSITS =
            "CREATE TABLE " + TABLE_DEPOSITS + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_D_TYPE + " TEXT NOT NULL, " +
                    COL_D_VALUE + " REAL NOT NULL, " +
                    COL_D_BARCODE + " TEXT, " +
                    COL_D_RETURNED + " INTEGER DEFAULT 0" +
                    ");";

    private static final String CREATE_PRODUCTS =
            "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_NAME + " TEXT NOT NULL, " +
                    COL_PRICE + " REAL NOT NULL, " +
                    COL_EXPIRY + " TEXT, " +
                    COL_CATEGORY + " TEXT, " +
                    COL_DESCRIPTION + " TEXT, " +
                    COL_STORE + " TEXT, " +
                    COL_PURCHASE + " TEXT, " +
                    COL_USED + " INTEGER DEFAULT 0" +
                    ");";


    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PRODUCTS);
        db.execSQL(CREATE_DEPOSITS);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEPOSITS);
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
        cv.put(COL_USED, p.isUsed() ? 1 : 0);

        long id = db.insert(TABLE_PRODUCTS, null, cv);
        db.close();
        return id;
    }

    public long insertDeposit(Deposit d) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_D_TYPE, d.getType());
        cv.put(COL_D_VALUE, d.getValue());
        cv.put(COL_D_BARCODE, d.getBarcode());
        cv.put(COL_D_RETURNED, d.isReturned() ? 1 : 0);

        long id = db.insert(TABLE_DEPOSITS, null, cv);
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
                boolean used = c.getInt(c.getColumnIndexOrThrow(COL_USED)) == 1;

                Product p = new Product(id, name, price, expiry, category, desc, store, purchase, used);
                list.add(p);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }

    public List<Deposit> getAllDeposits() {
        List<Deposit> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_DEPOSITS, null, null, null, null, null, COL_ID + " DESC");

        if (c.moveToFirst()) {
            do {
                long id = c.getLong(c.getColumnIndexOrThrow(COL_ID));
                String type = c.getString(c.getColumnIndexOrThrow(COL_D_TYPE));
                double value = c.getDouble(c.getColumnIndexOrThrow(COL_D_VALUE));
                String barcode = c.getString(c.getColumnIndexOrThrow(COL_D_BARCODE));
                boolean returned = c.getInt(c.getColumnIndexOrThrow(COL_D_RETURNED)) == 1;

                Deposit d = new Deposit(id, type, value, barcode, returned);
                list.add(d);
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
            boolean used = c.getInt(c.getColumnIndexOrThrow(COL_USED)) == 1;
            p = new Product(id, name, price, expiry, category, desc, store, purchase, used);
        }
        c.close();
        db.close();
        return p;
    }

    public Deposit getDepositById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_DEPOSITS, null, COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        Deposit d = null;

        if (c.moveToFirst()) {
            String type = c.getString(c.getColumnIndexOrThrow(COL_D_TYPE));
            double value = c.getDouble(c.getColumnIndexOrThrow(COL_D_VALUE));
            String barcode = c.getString(c.getColumnIndexOrThrow(COL_D_BARCODE));
            boolean returned = c.getInt(c.getColumnIndexOrThrow(COL_D_RETURNED)) == 1;

            d = new Deposit(id, type, value, barcode, returned);
        }
        c.close();
        db.close();
        return d;
    }

    public void updateProduct(Product p) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_NAME, p.getName());
        values.put(COL_PRICE, p.getPrice());
        values.put(COL_EXPIRY, p.getExpiryDate());
        values.put(COL_CATEGORY, p.getCategory());
        values.put(COL_DESCRIPTION, p.getDescription());
        values.put(COL_STORE, p.getStore());
        values.put(COL_PURCHASE, p.getPurchaseDate());
        values.put(COL_USED, p.isUsed() ? 1 : 0);

        db.update(TABLE_PRODUCTS, values, COL_ID + "=?", new String[]{String.valueOf(p.getId())});
        db.close();
    }

    public void updateDeposit(Deposit d) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_D_TYPE, d.getType());
        values.put(COL_D_VALUE, d.getValue());
        values.put(COL_D_BARCODE, d.getBarcode());
        values.put(COL_D_RETURNED, d.isReturned()? 1 : 0) ;

        db.update(TABLE_DEPOSITS, values, COL_ID + "=?", new String[]{String.valueOf(d.getId())});
        db.close();
    }

    public void deleteProduct(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("products", "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteDeposit(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("deposits", "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }


}
