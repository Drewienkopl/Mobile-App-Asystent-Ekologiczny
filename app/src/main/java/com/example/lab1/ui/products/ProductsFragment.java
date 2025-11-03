package com.example.lab1.ui.products;




import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
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
    private boolean sortAscending = true;
    private boolean isGrid = false;


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


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Setup SearchView
        androidx.appcompat.widget.SearchView searchView = binding.searchView;
        searchView.setQueryHint("Szukaj produktów...");
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                updateProductCount();
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                updateProductCount();
                return true;
            }
        });

        //Setup MenuProvider for menu items
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
                inflater.inflate(R.menu.menu_products, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.action_sort_price) {
                    // Toggle ascending/descending
                    sortAscending = !sortAscending;
                    List<Product> sorted = new ArrayList<>(adapter.getProductList());
                    sorted.sort((p1, p2) ->
                            sortAscending ? Double.compare(p1.getPrice(), p2.getPrice())
                                    : Double.compare(p2.getPrice(), p1.getPrice())
                    );
                    adapter.updateData(sorted);
                    return true;
                } else if (item.getItemId() == R.id.action_toggle_layout) {
                    toggleLayoutManager();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner());
    }

    private void toggleLayoutManager() {
        isGrid = !isGrid;
        RecyclerView recyclerView = binding.recyclerViewProducts;
        if (isGrid) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        recyclerView.setAdapter(adapter);
    }

    private void updateProductCount() {
        int count = adapter.getProductList().size();
        binding.tvProductCount.setText(getString(R.string.products_count, count));
    }

    // odswiez liste za kazdym razem gdy fragment wraca na pierwszy plan
    @Override
    public void onResume() {
        super.onResume();
        loadProductsFromDb();
    }

    private void loadProductsFromDb() {
        List<Product> products = dbHelper.getAllProducts();
        if (products == null) products = new ArrayList<>();
        adapter.updateData(products);

        //aktualizuj licznik produktow
        binding.tvProductCount.setText(getString(R.string.products_count, products.size()));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
