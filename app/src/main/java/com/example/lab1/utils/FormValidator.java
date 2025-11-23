package com.example.lab1.utils;

import android.content.Context;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormValidator {


    private static final SimpleDateFormat sdf =
            new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);

    public static boolean validateProductForm(
            Context context,
            TextInputEditText etName,
            TextInputEditText etPrice,
            TextInputEditText etExpiry,
            TextInputEditText etCategory,
            TextInputEditText etDescription,
            TextInputEditText etStore,
            TextInputEditText etPurchase

    ) {
        String name = safe(etName);
        String priceStr = safe(etPrice);
        String expiry = safe(etExpiry);
        String category = safe(etCategory);
        String description = safe(etDescription);
        String store = safe(etStore);
        String purchase = safe(etPurchase);

        // 1. Puste pola
        if (name.isEmpty() || priceStr.isEmpty() || expiry.isEmpty()
                || category.isEmpty() || description.isEmpty() || store.isEmpty() || purchase.isEmpty()) {

            Toast.makeText(context, "Wszystkie pola są wymagane", Toast.LENGTH_SHORT).show();
            return false;
        }


        // 2. Cena
        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price <= 0) {
                etPrice.setError("Cena musi być większa od 0");
                return false;
            }
        } catch (NumberFormatException e) {
            etPrice.setError("Niepoprawna cena");
            return false;
        }


        // 3. Sprawdzenie daty ważności
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date exp = sdf.parse(expiry);
            assert exp != null;
            if (exp.before(new Date())) {
                Toast.makeText(context, "Data ważności nie może być wcześniejsza niż dziś", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (ParseException e) {
            Toast.makeText(context, "Niepoprawny format daty (yyyy-MM-dd)", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private  static String safe(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
}
