package com.example.lab1.ui.products;


import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.lab1.data.DBHelper;
import com.example.lab1.data.Product;
import com.example.lab1.databinding.FragmentAddProductBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class AddProductFragment extends Fragment {

    private FragmentAddProductBinding binding;
    private DBHelper dbHelper;
    private  final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public AddProductFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        binding = FragmentAddProductBinding.inflate(inflater, container, false);
        dbHelper = new DBHelper(requireContext());

        binding.etExpiry.setOnClickListener(v -> showDatePicker(binding.etExpiry));
        binding.btnSave.setOnClickListener(v -> saveProduct());

        return binding.getRoot();
    }

    private void showDatePicker(final TextInputEditText editText) {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog dpd = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar sel = Calendar.getInstance();
                    sel.set(year, month, dayOfMonth);
                    editText.setText(sdf.format(sel.getTime()));
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dpd.show();
    }

    private void saveProduct() {
        String name = binding.etName.getText() == null ? "" : binding.etName.getText().toString().trim();
        String priceStr = binding.etPrice.getText() == null ? "" : binding.etPrice.getText().toString().trim();
        String expiry = binding.etExpiry.getText() == null ? "" : binding.etExpiry.getText().toString().trim();
        String category = binding.etCategory.getText() == null ? "" : binding.etCategory.getText().toString().trim();
        String desc = binding.etDescription.getText() == null ? "" : binding.etDescription.getText().toString().trim();
        String store = binding.etStore.getText() == null ? "" : binding.etStore.getText().toString().trim();

        // Walidacja
        if (name.isEmpty()) {
            binding.tilName.setError("Podaj nazwę");
            return;
        } else {
            binding.tilName.setError(null);
        }

        double price = 0;
        try {
            price = Double.parseDouble(priceStr);
            if (price < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            binding.etPrice.setError("Niepoprawna cena");
        }

        // expiry format check
        if (!expiry.isEmpty() && expiry.length() != 10) {
            binding.etExpiry.setError("Wprowadź datę w formacie yyyy-MM-dd");
        }

        Product p = new Product();
        p.setName(name);
        p.setPrice(price);
        p.setExpiryDate(expiry);
        p.setCategory(category);
        p.setDescription(desc);
        p.setStore(store);
        p.setPurchaseDate("");


        long id = dbHelper.insertProduct(p);
        if (id > 0) {
            Toast.makeText(requireContext(), "Produkt zapisany", Toast.LENGTH_SHORT).show();
            // wróć na listę produktów
            NavHostFragment.findNavController(AddProductFragment.this).popBackStack();
        } else {
            Toast.makeText(requireContext(), "Błąd zapisu", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}