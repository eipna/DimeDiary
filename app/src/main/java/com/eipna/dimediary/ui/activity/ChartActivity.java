package com.eipna.dimediary.ui.activity;

import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.eipna.dimediary.R;
import com.eipna.dimediary.data.Category;
import com.eipna.dimediary.data.Chart;
import com.eipna.dimediary.data.Database;
import com.eipna.dimediary.data.PieChartView;
import com.eipna.dimediary.databinding.ActivityChartBinding;
import com.eipna.dimediary.ui.adapter.ChartAdapter;

import java.util.ArrayList;
import java.util.Objects;

public class ChartActivity extends AppCompatActivity {

    private ActivityChartBinding binding;
    private Database database;
    private ArrayList<Category> categories;
    private ArrayList<Double> categorySums;
    private ChartAdapter chartAdapter;

    private ArrayList<Chart> charts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChartBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        database = new Database(this);
        categories = new ArrayList<>(database.getCategories());
        categorySums = new ArrayList<>(database.getCategorySums());
        charts = new ArrayList<>();

        double allCategoriesSum = 0.0;
        for (int i = 0; i < categorySums.size(); i++) {
            allCategoriesSum += categorySums.get(i);
        }

        if (allCategoriesSum <= 0.0) {
            binding.chart.setVisibility(PieChartView.GONE);
            return;
        }

        ArrayList<Object> colors = new ArrayList<>();
        for (Category category : categories) {
            int r = Integer.parseInt(category.getColor().split(",")[0]);
            int g = Integer.parseInt(category.getColor().split(",")[1]);
            int b = Integer.parseInt(category.getColor().split(",")[2]);
            colors.add(Color.rgb(r, g, b));
        }

        ArrayList<PieChartView.PieSlice> pieSlices = new ArrayList<>();
        for (int i = 0; i <categories.size(); i++) {
            pieSlices.add(new PieChartView.PieSlice((categorySums.get(i) / allCategoriesSum) * 360, (Integer) colors.get(i)));
        }

        binding.chart.setData(pieSlices);

        for (int i = 0; i < categories.size(); i++) {
            Chart chart = new Chart();
            chart.setName(categories.get(i).getName());
            chart.setSum(categorySums.get(i));
            chart.setColor(categories.get(i).getColor());
            charts.add(chart);
        }

        chartAdapter = new ChartAdapter(this, charts);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(chartAdapter);
    }
}