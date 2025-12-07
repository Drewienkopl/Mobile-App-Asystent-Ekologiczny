package com.example.lab1.ui.deposit;


import android.app.AlertDialog;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.util.Log;
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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.SearchView;



import com.example.lab1.R;
import com.example.lab1.data.DBHelper;
import com.example.lab1.data.Deposit;
import com.example.lab1.data.Product;
import com.example.lab1.databinding.FragmentDepositBinding;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class DepositFragment extends Fragment {


    private FragmentDepositBinding binding;
    private DepositAdapter adapter;
    private DBHelper db;

    private  boolean sortAscending = true;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        db = new DBHelper(requireContext());

        binding = FragmentDepositBinding.inflate(inflater, container, false);
        binding.btnExportCsvDeposit.setOnClickListener(v -> exportToCSVDeposit());
        binding.btnImportCsvDeposit.setOnClickListener(v -> importFromCSVDeposit());
        binding.btnExportPdfDeposit.setOnClickListener(v -> {
            File file = new File(requireContext().getExternalFilesDir(null), "deposits.pdf");
            List<Deposit> deposits = db.getAllDeposits();
            boolean ok = createDepositPdf(file, deposits);

            Toast.makeText(requireContext(),
                    ok ? "PDF zapisany: " + file.getAbsolutePath()
                            : "Błąd zapisu PDF!",
                    Toast.LENGTH_LONG).show();
        });

        setupRecycler();
        setupAddButton();

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Setup MenuProvider for menu items
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
                inflater.inflate(R.menu.menu_deposits, menu);
                MenuItem searchItem = menu.findItem(R.id.filterByDepositType);
                if (searchItem == null) {
                    Log.e("DepositFragment", "filterByDepositType NOT FOUND IN MENU!");
                    return;
                }

                SearchView searchView = (SearchView) searchItem.getActionView();
                searchView.setQueryHint("Filtruj po typie");

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        filterByType(query);
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        filterByType(newText);
                        return false;
                    }
                });
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.sortByDepositValue) {
                    sortAscending = !sortAscending;
                    sortByValue();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner());

        refreshData();
    }

    private void  setupRecycler(){
        adapter = new DepositAdapter(requireContext(), new DepositAdapter.OnDepositActionListener() {
            @Override
            public void onEditDeposit(Deposit deposit) {
                Bundle args = new Bundle();
                args.putLong("depositId", deposit.getId());
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_deposit_to_addDeposit, args);
            }

            @Override
            public void onDeleteDeposit(Deposit deposit) {
                showDeleteDialog(deposit);
            }
        });


        binding.recyclerViewDeposits.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewDeposits.setAdapter(adapter);
    }

    private void setupAddButton() {
        binding.fabAddDeposit.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_deposit_to_addDeposit);
        });
    }

    private void sortByValue() {
        List<Deposit> list = new ArrayList<>(db.getAllDeposits());
        list.sort((d1, d2) -> sortAscending ? Double.compare(d1.getValue(), d2.getValue()) : Double.compare(d2.getValue(), d1.getValue()));
        adapter.setDeposits(list);
    }

    private void filterByType(String text) {
        List<Deposit> all = db.getAllDeposits();
        if (text == null || text.trim().isEmpty()) {
            adapter.setDeposits(all);
            return;
        }
        String q = text.toLowerCase();
        List<Deposit> filtered = new ArrayList<>();

        for (Deposit d : all) {
            if (d.getType().toLowerCase().contains(q)) {
                filtered.add(d);
            }
        }
        adapter.setDeposits(filtered);
    }


        private void refreshData() {
        List<Deposit> list = db.getAllDeposits();
        adapter.setDeposits(list);


        double activeSum = 0;
        int count = 0;


        for (Deposit d : list) {
            if (!d.isReturned()) {
                activeSum += d.getValue();
                count++;
            }
        }

        binding.tvActiveSum.setText(String.format("Aktywna suma: %.2f zł", activeSum));
        binding.tvCount.setText(String.format("Liczba: %d", count));
    }



    private void showDeleteDialog(Deposit deposit) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Usuń opakowanie")
                .setMessage("Czy na pewno chcesz usunąć ten depozyt?")
                .setPositiveButton("Usuń", (dialog, which) -> {
                    db.deleteDeposit(deposit.getId());
                    refreshData();
                    Toast.makeText(requireContext(), "Usunięto", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Anuluj", null)
                .show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void exportToCSVDeposit() {
        DBHelper db = new DBHelper(requireContext());
        List<Deposit> deposits = db.getAllDeposits();

        StringBuilder sb = new StringBuilder();
        sb.append("id,type,value,barcode,returned\n");

        for(Deposit d : deposits) {
            sb.append(d.getId()).append(",");
            sb.append(d.getType()).append(",");
            sb.append(d.getValue()).append(",");
            sb.append(d.getBarcode()).append(",");
            sb.append(d.isReturned() ? "1" : "0").append("\n");
        }
        try {
            File file = new File(requireContext().getExternalFilesDir(null), "deposits.csv");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(sb.toString().getBytes());
            fos.close();

            Toast.makeText(requireContext(), "Zapisano do: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Błąd zapisu CSV!", Toast.LENGTH_SHORT).show();
        }
    }

    private void importFromCSVDeposit() {
        DBHelper db = new DBHelper(requireContext());

        try {
            File file = new File(requireContext().getExternalFilesDir(null), "deposits.csv");
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                Deposit d = new Deposit();
                d.setType(values[1]);
                d.setValue(Double.parseDouble(values[2]));
                d.setBarcode(values[3]);
                d.setReturned(values[4].equals("1"));

                db.insertDeposit(d);
            }

            br.close();
            refreshData();
            Toast.makeText(requireContext(), "Zaimportowano CSV!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Błąd importu CSV!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean createDepositPdf(File file, List<Deposit> deposits) {
        PdfDocument pdf = new PdfDocument();
        Paint paint = new Paint();
        int pageNumber = 1;
        int y = 30;
        int pageHeight = 1120;
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, pageHeight, pageNumber).create();

        PdfDocument.Page page = pdf.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        paint.setTextSize(14);
        canvas.drawText("Lista opakowań", 20, y, paint);
        y += 30;

        for (Deposit d : deposits) {
            if (y > pageHeight - 40) {
                pdf.finishPage(page);
                pageNumber++;
                pageInfo = new PdfDocument.PageInfo.Builder(595, pageHeight, pageNumber).create();
                page = pdf.startPage(pageInfo);
                canvas = page.getCanvas();
                y = 30;
            }
            String line = String.format(Locale.getDefault(),
                    "%s — %.2f zł — %s %s",
                    d.getType(),
                    d.getValue(),
                    d.getBarcode() != null ? d.getBarcode() : "",
                    d.isReturned() ? "(zwrócone)" : ""
            );
            paint.setTextSize(12);
            canvas.drawText(line, 20, y, paint);
            y += 22;
        }
        pdf.finishPage(page);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            pdf.writeTo(fos);
            pdf.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            pdf.close();
            return false;
        }
    }

}
