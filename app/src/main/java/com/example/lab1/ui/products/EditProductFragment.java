package com.example.lab1.ui.products;




import android.app.DatePickerDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.example.lab1.R;
import com.example.lab1.data.DBHelper;
import com.example.lab1.data.Product;
import com.example.lab1.databinding.FragmentAddProductBinding;
import com.example.lab1.utils.FormValidator;
import com.google.android.material.textfield.TextInputEditText;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;




public class EditProductFragment extends Fragment {




    private FragmentAddProductBinding binding;
    private DBHelper dbHelper;
    private Product product;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddProductBinding.inflate(inflater, container, false);
        dbHelper = new DBHelper(requireContext());


        long productId = getArguments().getLong("productId");
        product = dbHelper.getProductById(productId);


        fillForm(product);


        binding.btnSave.setText("Zapisz zmiany");
        binding.btnSave.setOnClickListener(v -> updateProduct());


        // kliknięcie w wybór daty zakupu
        binding.etPurchase.setOnClickListener(v -> pickDate(binding.etPurchase));

        // kliknięcie w wybór daty ważności
        binding.etExpiry.setOnClickListener(v -> pickDate(binding.etExpiry));


        return binding.getRoot();
    }


    private void fillForm(Product p) {
        binding.etName.setText(p.getName());
        binding.etPrice.setText(String.valueOf(p.getPrice()));
        binding.etExpiry.setText(p.getExpiryDate());
        binding.etCategory.setText(p.getCategory());
        binding.etDescription.setText(p.getDescription());
        binding.etStore.setText(p.getStore());
        binding.etPurchase.setText(p.getPurchaseDate());
        binding.cbUsed.setChecked(p.isUsed());
    }

    private void pickDate(TextInputEditText target) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(requireContext(),
                (view, year, month, day) -> {
                    String date = year + "-" + (month + 1) + "-" + day;
                    target.setText(date);
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void updateProduct() {
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

        product.setName(binding.etName.getText().toString());
        product.setPrice(Double.parseDouble(binding.etPrice.getText().toString()));
        product.setExpiryDate(binding.etExpiry.getText().toString());
        product.setCategory(binding.etCategory.getText().toString());
        product.setDescription(binding.etDescription.getText().toString());
        product.setStore(binding.etStore.getText().toString());
        product.setPurchaseDate(binding.etPurchase.getText().toString());
        product.setUsed(binding.cbUsed.isChecked());


        dbHelper.updateProduct(product);
        playConfirmSound();
        Toast.makeText(getContext(), "Zaktualizowano produkt", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(requireView()).popBackStack();
    }

    private void playConfirmSound() {
        MediaPlayer mp = MediaPlayer.create(requireContext(), R.raw.confirm_lightsaber);
        mp.setOnCompletionListener(MediaPlayer::release);
        mp.start();
    }
}
