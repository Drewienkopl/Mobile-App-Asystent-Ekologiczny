package com.example.lab1.ui.reports;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.lab1.R;
import com.example.lab1.data.DBHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.components.Description;
import android.graphics.Color;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


import androidx.annotation.NonNull;

import com.example.lab1.databinding.FragmentReportsBinding;

public class ReportsFragment extends Fragment {

    TextView tvMonth, tvExpenses, tvDeposits;
    BarChart barChart;
    Button btnExport;

    DBHelper db;

    double expenses, deposits;
    String currentMonth;

    private FragmentReportsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_reports, container, false);

        tvMonth = v.findViewById(R.id.tvMonth);
        tvExpenses = v.findViewById(R.id.tvExpenses);
        tvDeposits = v.findViewById(R.id.tvDeposits);
        barChart = v.findViewById(R.id.barChart);
        btnExport = v.findViewById(R.id.btnExport);

        db = new DBHelper(getContext());

        currentMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault())
                .format(new Date());

        loadData();
        setupChart();

        btnExport.setOnClickListener(e -> exportCSV());

        return v;
    }

    private void loadData() {
        expenses = db.getMonthlyExpenses(currentMonth);
        deposits = db.getMonthlyReturnedDeposits(currentMonth);

        tvMonth.setText("Raport: " + currentMonth);
        tvExpenses.setText("Wydatki: " + expenses + " zł");
        tvDeposits.setText("Odzyskana kaucja: " + deposits + " zł");
    }

    private void setupChart() {

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, (float) expenses));
        entries.add(new BarEntry(1f, (float) deposits));

        BarDataSet set = new BarDataSet(entries, "Porównanie miesięczne");
        set.setValueTextSize(14f);           // liczby nad slupami
        set.setValueTextColor(R.color.black);

        BarData data = new BarData(set);
        data.setBarWidth(0.5f);

        barChart.setData(data);

        //NAZWY SŁUPKÓW (OŚ X)
        final String[] labels = new String[]{
                "Wydatki",
                "Odzyskana kaucja"
        };

        //OS X
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(12f);

        //OŚ Y
        float max = Math.max((float) expenses, (float) deposits);
        float axisMax = max * 1.2f;

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(axisMax);
        yAxis.setGranularity(10f);

        //OPIS WYKRESU
        Description desc = new Description();
        desc.setText("Porównanie wydatków i kaucji");
        desc.setTextSize(12f);
        barChart.setDescription(desc);

        //KOLORY
        set.setColors(
                Color.parseColor("#F44336"), // czerwony – wydatki
                Color.parseColor("#4CAF50")  // zielony – kaucja
        );



        //LEGENDA
        barChart.getLegend().setEnabled(true);

        barChart.getAxisRight().setEnabled(false); // prawa oś OFF
        barChart.animateY(800);

        barChart.invalidate();
    }


    private void exportCSV() {
        try {
            File file = new File(getContext().getExternalFilesDir(null),
                    "raport_" + currentMonth + ".csv");

            FileWriter writer = new FileWriter(file);
            writer.append("Miesiąc,Wydatki,Odzyskana kaucja\n");
            writer.append(currentMonth + "," + expenses + "," + deposits + "\n");
            writer.flush();
            writer.close();

            Toast.makeText(getContext(),
                    "Zapisano: " + file.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(getContext(), "Błąd zapisu CSV", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}