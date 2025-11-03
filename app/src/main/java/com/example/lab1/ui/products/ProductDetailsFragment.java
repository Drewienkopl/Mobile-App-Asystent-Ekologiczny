package com.example.lab1.ui.products;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lab1.R;
import com.example.lab1.data.DBHelper;
import com.example.lab1.data.Product;
import com.example.lab1.databinding.FragmentProductDetailsBinding;


public class ProductDetailsFragment extends Fragment {

    private FragmentProductDetailsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductDetailsBinding.inflate(inflater, container, false);
        DBHelper db = new DBHelper(requireContext());

        long id = 0;
        if (getArguments() != null) {
            id = getArguments().getLong("productId", 0);
        }
        if (id != 0) {
            Product p = db.getProductById(id);
            if (p != null) {
                binding.tvPrice.setText(getString(R.string.price_label, p.getPrice()));
                binding.tvExpiry.setText(getString(R.string.expiry_label, p.getExpiryDate()));
                binding.tvCategory.setText(getString(R.string.category_label, p.getCategory()));
                binding.tvDescription.setText(getString(R.string.description_label, p.getDescription()));
                binding.tvStore.setText(getString(R.string.store_label, p.getStore()));
                binding.tvPurchaseDate.setText(getString(R.string.purchase_date_label, p.getPurchaseDate()));

            }
        }
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}