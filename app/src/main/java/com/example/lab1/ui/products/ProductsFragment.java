package com.example.lab1.ui.products;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab1.R;
import com.example.lab1.data.DBHelper;
import com.example.lab1.data.Product;
import com.example.lab1.databinding.FragmentProductsBinding;

import java.util.ArrayList;
import java.util.List;

public class ProductsFragment extends Fragment {

    private FragmentProductsBinding binding;
    private ProductsAdapter adapter;
    private DBHelper dbHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProductsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dbHelper = new DBHelper(requireContext());

        RecyclerView recyclerView = binding.recyclerViewProducts;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        // adapter z pusta lista na start
        adapter = new ProductsAdapter(new ArrayList<>(), product -> {
            Bundle args = new Bundle();
            args.putLong("productId", product.getId());
            Navigation.findNavController(requireView()).navigate(R.id.action_products_to_productDetails, args);
        });
        recyclerView.setAdapter(adapter);

        // navigation do AddProductFragment
        binding.fabAddProduct.setOnClickListener(
                v -> Navigation.findNavController(v).navigate(R.id.action_products_to_addProduct)
        );

        // wczytujemy produkty
        loadProductsFromDb();

        return root;
    }

    // odświez liste za kazdym razem gdy fragment wraca na pierwszy plan
    @Override
    public void onResume() {
        super.onResume();
        loadProductsFromDb();
    }

    private void loadProductsFromDb() {
        List<Product> products = dbHelper.getAllProducts();
        if (products == null) products = new ArrayList<>();
        adapter.updateData(products);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}