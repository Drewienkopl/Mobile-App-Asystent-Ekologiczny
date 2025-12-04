package com.example.lab1.ui.deposit;


import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.example.lab1.R;
import com.example.lab1.data.DBHelper;
import com.example.lab1.data.Deposit;
import com.example.lab1.databinding.FragmentDepositBinding;


import java.util.List;


public class DepositFragment extends Fragment {


    private FragmentDepositBinding binding;
    private DepositAdapter adapter;
    private DBHelper db;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        binding = FragmentDepositBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        db = new DBHelper(requireContext());


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


        binding.fabAddDeposit.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_deposit_to_addDeposit);
        });


        refreshData();
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
}
