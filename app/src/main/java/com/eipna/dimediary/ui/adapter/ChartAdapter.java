package com.eipna.dimediary.ui.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eipna.dimediary.R;
import com.eipna.dimediary.data.Category;
import com.eipna.dimediary.data.Chart;
import com.eipna.dimediary.data.Database;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.text.NumberFormat;
import java.util.ArrayList;

public class ChartAdapter extends RecyclerView.Adapter<ChartAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Chart> charts;
    private final Database database;

    public ChartAdapter(Context context, ArrayList<Chart> charts) {
        this.context = context;
        this.charts= charts;
        this.database = new Database(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_chart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chart chart = charts.get(position);

        String[] colors = chart.getColor().split(",");

        int red = Integer.parseInt(colors[0]);
        int green= Integer.parseInt(colors[1]);
        int blue = Integer.parseInt(colors[2]);

        ColorStateList colorStateList = ColorStateList.valueOf(Color.rgb(red, green, blue));

        holder.color.setBackgroundTintList(colorStateList);

        holder.name.setText(chart.getName());
        holder.sum.setText("PHP " + NumberFormat.getNumberInstance().format(chart.getSum()));
    }

    @Override
    public int getItemCount() {
        return charts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView color;
        MaterialTextView name, sum;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            color = itemView.findViewById(R.id.chart_color);
            name = itemView.findViewById(R.id.chart_item_name);
            sum = itemView.findViewById(R.id.chart_item_sum);
        }
    }
}
