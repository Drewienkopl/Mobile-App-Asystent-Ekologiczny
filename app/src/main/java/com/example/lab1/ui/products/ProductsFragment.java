package com.example.lab1.ui.products;


import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


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


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
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


        binding.btnExportCSV.setOnClickListener(v -> exportToCSV());
        binding.btnImportCSV.setOnClickListener(v -> importFromCSV());
        binding.btnExportJSON.setOnClickListener(v -> exportToJSON());
        binding.btnImportJSON.setOnClickListener(v -> importFromJSON());


        dbHelper = new DBHelper(requireContext());


        RecyclerView recyclerView = binding.recyclerViewProducts;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(adapter);

        // adapter z pusta lista na start
        adapter = new ProductsAdapter(new ArrayList<>(), new ProductsAdapter.OnProductActionListener() {
            @Override
            public void onProductClick(Product product) {
                Bundle args = new Bundle();
                args.putLong("productId", product.getId());
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_products_to_productDetails, args);
            }

            @Override
            public void onEditProduct(Product product) {
                Bundle args = new Bundle();
                args.putLong("productId", product.getId());
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_products_to_editProduct, args);
            }

            @Override
            public void onDeleteProduct(Product product) {
                showDeleteDialog(product);
            }
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
                } else if (item.getItemId() == R.id.action_filter_all) {
                    adapter.updateData(dbHelper.getAllProducts());
                    updateProductCount();
                    return true;
                } else if (item.getItemId() == R.id.action_filter_active) {
                    List<Product> active = new ArrayList<>();
                    for (Product p : dbHelper.getAllProducts()) {
                        if (!p.isUsed()) active.add(p);
                    }
                    adapter.updateData(active);
                    updateProductCount();
                    return true;
                } else if (item.getItemId() == R.id.action_filter_used) {
                    List<Product> used = new ArrayList<>();
                    for (Product p : dbHelper.getAllProducts()) {
                        if (p.isUsed()) used.add(p);
                    }
                    adapter.updateData(used);
                    updateProductCount();
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


    private void showDeleteDialog(Product product) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Usuń produkt")
                .setMessage("Czy na pewno chcesz usunąć " + product.getName() + "?")
                .setPositiveButton("Usuń", (dialog, which) -> {
                    dbHelper.deleteProduct(product.getId());
                    loadProductsFromDb(); // automatyczne odświeżenie
                    Toast.makeText(getContext(), "Produkt usunięty", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Anuluj", null)
                .show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void exportToCSV() {
        DBHelper db = new DBHelper(requireContext());
        List<Product> products = db.getAllProducts();

        StringBuilder sb = new StringBuilder();
        sb.append("id,name,price,expiry,category,description,store,purchase,used\n");

        for(Product p : products) {
            sb.append(p.getId()).append(",");
            sb.append(p.getName()).append(",");
            sb.append(p.getPrice()).append(",");
            sb.append(p.getExpiryDate()).append(",");
            sb.append(p.getCategory()).append(",");
            sb.append(p.getDescription()).append(",");
            sb.append(p.getStore()).append(",");
            sb.append(p.getPurchaseDate()).append(",");
            sb.append(p.isUsed() ? "1" : "0").append("\n");
        }
        try {
            File file = new File(requireContext().getExternalFilesDir(null), "products.csv");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(sb.toString().getBytes());
            fos.close();

            Toast.makeText(requireContext(), "Zapisano do: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Błąd zapisu CSV!", Toast.LENGTH_SHORT).show();
        }
    }

    private void importFromCSV() {
        DBHelper db = new DBHelper(requireContext());

        try {
            File file = new File(requireContext().getExternalFilesDir(null), "products.csv");
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                Product p = new Product();
                p.setName(values[1]);
                p.setPrice(Double.parseDouble(values[2]));
                p.setExpiryDate(values[3]);
                p.setCategory(values[4]);
                p.setDescription(values[5]);
                p.setStore(values[6]);
                p.setPurchaseDate(values[7]);
                p.setUsed(values[8].equals("1"));

                db.insertProduct(p);
            }

            br.close();
            loadProductsFromDb();
            Toast.makeText(requireContext(), "Zaimportowano CSV!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Błąd importu CSV!", Toast.LENGTH_SHORT).show();
        }
    }

    private void exportToJSON() {
        DBHelper db = new DBHelper(requireContext());
        List<Product> products = db.getAllProducts();

        JSONArray array = new JSONArray();

        try {
            for (Product p : products) {
                JSONObject obj = new JSONObject();
                obj.put("name", p.getName());
                obj.put("price", p.getPrice());
                obj.put("expiry", p.getExpiryDate());
                obj.put("category", p.getCategory());
                obj.put("description", p.getDescription());
                obj.put("store", p.getStore());
                obj.put("purchase", p.getPurchaseDate());
                obj.put("used", p.isUsed());

                array.put(obj);
            }
            File file = new File(requireContext().getExternalFilesDir(null), "products.json");
            FileWriter writer = new FileWriter(file);
            writer.write(array.toString(2));
            writer.close();

            Toast.makeText(requireContext(), "JSON zapisany: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Błąd zapisu JSON!", Toast.LENGTH_SHORT).show();
        }
    }

    private void importFromJSON() {
        DBHelper db = new DBHelper(requireContext());

        try {
            File file = new File(requireContext().getExternalFilesDir(null), "products.json");
            FileReader reader = new FileReader(file);

            StringBuilder sb = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                sb.append((char) c);
            }
            reader.close();

            JSONArray array = new JSONArray(sb.toString());

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                Product p = new Product();
                p.setName(obj.getString("name"));
                p.setPrice(obj.getDouble("price"));
                p.setExpiryDate(obj.getString("expiry"));
                p.setCategory(obj.getString("category"));
                p.setDescription(obj.getString("description"));
                p.setStore(obj.getString("store"));
                p.setPurchaseDate(obj.getString("purchase"));
                p.setUsed(obj.getBoolean("used"));

                db.insertProduct(p);
            }

            loadProductsFromDb();
            Toast.makeText(requireContext(), "Zaimportowano JSON!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Błąd importu JSON!", Toast.LENGTH_SHORT).show();
        }
    }
}
