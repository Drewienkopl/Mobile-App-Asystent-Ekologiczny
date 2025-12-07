package com.example.lab1.ui.deposit;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.lab1.R;
import com.example.lab1.data.DBHelper;
import com.example.lab1.data.Deposit;
import com.example.lab1.databinding.FragmentAddDepositBinding;
import com.example.lab1.utils.SoundUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Objects;


public class AddDepositFragment extends Fragment {


    private FragmentAddDepositBinding binding;
    private AutoCompleteTextView actvType;
    private TextInputEditText etValue, etBarcode;
    private MaterialButton btnSave;
    private DBHelper dbHelper;


    public AddDepositFragment() {}


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAddDepositBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }


    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper = new DBHelper(requireContext());


        actvType = binding.actvType;
        etValue = binding.etValue;
        etBarcode = binding.etBarcode;


        binding.btnSaveDeposit.setOnClickListener(v -> saveDeposit());
        binding.btnScan.setOnClickListener(v -> {
            IntentIntegrator.forSupportFragment(this).setBeepEnabled(true).setPrompt("Skanuj kod kreskowy").initiateScan();
        });


        long depositId = getArguments() != null ? getArguments().getLong("depositId", -1) : -1;


        if (depositId != -1) {
            // TRYB EDYCJI
            Deposit d = dbHelper.getDepositById(depositId);
            fillForm(d);


            binding.btnSaveDeposit.setText("Zapisz zmiany");
            binding.btnSaveDeposit.setOnClickListener(v -> updateDeposit(d.getId()));
        } else {
            // TRYB DODAWANIA
            binding.btnSaveDeposit.setOnClickListener(v -> saveDeposit());
        }


        // lista typów z resources
        String[] types = getResources().getStringArray(R.array.deposit_types);




        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, types);
        actvType.setAdapter(adapter);
    }


    private void saveDeposit() {


        String type = actvType.getText().toString().trim();
        String valueStr = Objects.requireNonNull(etValue.getText()).toString().trim();
        String barcode = Objects.requireNonNull(etBarcode.getText()).toString().trim();


        if (TextUtils.isEmpty(type) || TextUtils.isEmpty(valueStr)) {
            Toast.makeText(requireContext(), "Wypełnij wszystkie wymagane pola", Toast.LENGTH_SHORT).show();
            return;
        }


        double value;
        try {
            value = Double.parseDouble(valueStr);
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Niepoprawna wartość kaucji", Toast.LENGTH_SHORT).show();
            return;
        }


        Deposit d = new Deposit();
        d.setType(type);
        d.setValue(value);
        d.setBarcode(barcode);
        d.setReturned(binding.switchReturned.isChecked());


        long id = dbHelper.insertDeposit(d);
        if (id > 0) {
            SoundUtil.playConfirmSound(requireContext());
            Toast.makeText(requireContext(), "Kaucja zapisana", Toast.LENGTH_SHORT).show();
            // wróć na listę depositow
            NavHostFragment.findNavController(AddDepositFragment.this).popBackStack();
        } else {
            Toast.makeText(requireContext(), "Błąd zapisu", Toast.LENGTH_SHORT).show();
        }
    }


    private void updateDeposit(long id) {


        String type = actvType.getText().toString().trim();
        String valueStr = Objects.requireNonNull(etValue.getText()).toString().trim();
        String barcode = Objects.requireNonNull(etBarcode.getText()).toString().trim();


        if (TextUtils.isEmpty(type) || TextUtils.isEmpty(valueStr)) {
            Toast.makeText(requireContext(), "Wypełnij wszystkie wymagane pola", Toast.LENGTH_SHORT).show();
            return;
        }


        double value;
        try {
            value = Double.parseDouble(valueStr);
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Niepoprawna wartość kaucji", Toast.LENGTH_SHORT).show();
            return;
        }


        Deposit d = new Deposit();
        d.setId(id);
        d.setType(type);
        d.setValue(value);
        d.setBarcode(barcode);
        d.setReturned(binding.switchReturned.isChecked());


        dbHelper.updateDeposit(d);


        SoundUtil.playConfirmSound(requireContext());
        Toast.makeText(requireContext(), "Zaktualizowano opakowanie", Toast.LENGTH_SHORT).show();
        NavHostFragment.findNavController(AddDepositFragment.this).popBackStack();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void fillForm(Deposit d) {
        actvType.setText(d.getType());
        etValue.setText(String.valueOf(d.getValue()));
        etBarcode.setText(d.getBarcode());
        binding.switchReturned.setChecked(d.isReturned());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() != null) {
                binding.etBarcode.setText(result.getContents());
            } else {
                Toast.makeText(requireContext(), "Anulowano skanowanie", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
