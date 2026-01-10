package com.example.lab1.ui.reports;


import android.app.DatePickerDialog;
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
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.components.Description;
import android.graphics.Color;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import androidx.annotation.NonNull;

import com.example.lab1.databinding.FragmentReportsBinding;
import com.github.mikephil.charting.utils.ColorTemplate;

public class ReportsFragment extends Fragment {

    TextView tvMonth, tvExpenses, tvDeposits;
    BarChart barChart;
    Button btnExport;

    TextView tvAverage, tvExpired;
    TextView tvPeriodExpenses;
    TextView tvFromDate, tvToDate;
    Button btnApplyPeriod;
    String fromDate, toDate;
    TextView tvCategoryList;
    Button btnExportHtml;

    PieChart pieChart;
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
        tvAverage = v.findViewById(R.id.tvAverage);
        tvExpired = v.findViewById(R.id.tvExpired);
        tvPeriodExpenses = v.findViewById(R.id.tvPeriodExpenses);
        btnExportHtml = v.findViewById(R.id.btnExportHtml);
        pieChart = v.findViewById(R.id.pieChart);
        tvFromDate = v.findViewById(R.id.tvFromDate);
        tvToDate = v.findViewById(R.id.tvToDate);
        tvCategoryList = v.findViewById(R.id.tvCategoryList);

        btnApplyPeriod = v.findViewById(R.id.btnApplyPeriod);

        btnExportHtml.setOnClickListener(e -> exportHtmlReport());
        tvFromDate.setOnClickListener(e -> pickDate(true));
        tvToDate.setOnClickListener(e -> pickDate(false));

        btnApplyPeriod.setOnClickListener(e -> {
            if (fromDate != null && toDate != null) {
                double sum = db.getExpensesInPeriod(fromDate, toDate);
                tvPeriodExpenses.setText("Wydatki: " + sum + " zł");
                setupCategoryChart(fromDate, toDate);
            }
        });


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

        double avg = db.getAveragePriceForMonth(currentMonth);
        int expired = db.getExpiredProductsCount();

        String toDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -30);

        String fromDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(cal.getTime());

        double last30Days = db.getExpensesInPeriod(fromDate, toDate);

        tvMonth.setText("Raport: " + currentMonth);
        tvExpenses.setText("Wydatki: " + expenses + " zł");
        tvDeposits.setText("Odzyskana kaucja: " + deposits + " zł");

        tvAverage.setText("Srednia cena produktu: " + String.format(Locale.getDefault(), "%.2f zł", avg));
        tvExpired.setText("Przeterminowane produkty: " + expired);
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


    private void setupCategoryChart(String fromDate, String toDate) {

        List<DBHelper.CategorySum> list =
                db.getExpenseByCategory(fromDate, toDate);

        ArrayList<PieEntry> entries = new ArrayList<>();
        StringBuilder text = new StringBuilder();

        for (DBHelper.CategorySum cs : list) {
            entries.add(new PieEntry((float) cs.sum, cs.category));

            text.append("• ")
                    .append(cs.category)
                    .append(": ")
                    .append(String.format(Locale.getDefault(), "%.2f zł", cs.sum))
                    .append("\n");
        }

        // TEKSTOWY RAPORT SUM
        tvCategoryList.setText(text.toString());

        // WYKRES
        PieDataSet set = new PieDataSet(entries, "Kategorie wydatków");
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        set.setValueTextSize(12f);

        PieData data = new PieData(set);

        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setText("Udział kategorii w wydatkach");
        pieChart.animateY(800);
        pieChart.invalidate();
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

    private void exportHtmlReport() {
        String html = "<html><head><style>" +
                "body{font-family:sans-serif;padding:16px;}" +
                "h1{color: #AAAAAA;}" +
                "table{width: 100%; border-collapse:collapse;}" +
                "td,th{border:1px solid #ccc;padding:8px;}" +
                "</style></head><body>" +

                "<h1>Raport wydatków</h1>" +
                "<p>Miesiąc: " + currentMonth + "</p>" +
                "<p>Wydatki: " + expenses + " zł</p>" +
                "<p>Kaucja: " + deposits + " zł</p>" +

                "</body></html>";

        try {
            File file = new File(getContext().getExternalFilesDir(null),
                    "raport_" + currentMonth + ".html");

            FileWriter writer = new FileWriter(file);
            writer.write(html);
            writer.close();

            Toast.makeText(getContext(),
                    "Zapisano HTML: " + file.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(getContext(), "Błąd zapisu HTML", Toast.LENGTH_SHORT).show();
        }
    }

    private void pickDate(boolean isFrom) {
        Calendar c = Calendar.getInstance();

        new DatePickerDialog(getContext(),
                (view, year, month, day) -> {
                    String date = String.format(Locale.getDefault(),
                            "%04d-%02d-%02d", year, month + 1, day);

                    if (isFrom) {
                        fromDate = date;
                        tvFromDate.setText("Od: " + date);
                    } else {
                        toDate = date;
                        tvToDate.setText("Do: " + date);
                    }
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        ).show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}