package com.example.lab1.ui.products;


import android.app.DatePickerDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.lab1.R;
import com.example.lab1.data.DBHelper;
import com.example.lab1.ui.products.EditProductFragment;
import com.example.lab1.data.Product;
import com.example.lab1.databinding.FragmentAddProductBinding;
import com.example.lab1.utils.FormValidator;
import com.example.lab1.utils.SoundUtil;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;


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
        binding.etPurchase.setOnClickListener(v -> showDatePicker(binding.etPurchase));
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

        if (!FormValidator.validateProductForm(
                requireContext(),
                binding.etName,
                binding.etPrice,
                binding.etExpiry,
                binding.etCategory,
                binding.etDescription,
                binding.etStore,
                binding.etPurchase
        )) return;

        String name = Objects.requireNonNull(binding.etName.getText()).toString().trim();
        double price = Double.parseDouble(Objects.requireNonNull(binding.etPrice.getText()).toString().trim());
        String expiry = Objects.requireNonNull(binding.etExpiry.getText()).toString().trim();
        String category = Objects.requireNonNull(binding.etCategory.getText()).toString().trim();
        String desc = Objects.requireNonNull(binding.etDescription.getText()).toString().trim();
        String store = Objects.requireNonNull(binding.etStore.getText()).toString().trim();
        String purchase = Objects.requireNonNull(binding.etPurchase.getText()).toString().trim();

        Product p = new Product();
        p.setName(name);
        p.setPrice(price);
        p.setExpiryDate(expiry);
        p.setCategory(category);
        p.setDescription(desc);
        p.setStore(store);
        p.setPurchaseDate(purchase);
        p.setUsed(binding.cbUsed.isChecked());

        long id = dbHelper.insertProduct(p);
        if (id > 0) {
            SoundUtil.playConfirmSound(requireContext());
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