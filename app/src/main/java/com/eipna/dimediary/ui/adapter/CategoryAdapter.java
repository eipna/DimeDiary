package com.eipna.dimediary.ui.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eipna.dimediary.R;
import com.eipna.dimediary.data.Category;
import com.eipna.dimediary.data.Database;
import com.eipna.dimediary.data.Expense;
import com.eipna.dimediary.util.DateUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Category> categories;

    private final CategoryListener categoryListener;

    public interface CategoryListener {
        void onClick(int position);
        void onOverflowClick(View view, int position);
    }

    public CategoryAdapter(Context context, CategoryListener categoryListener, ArrayList<Category> categories) {
        this.context = context;
        this.categories = categories;
        this.categoryListener = categoryListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.name.setText(category.getName());

        holder.itemView.setOnClickListener(view -> categoryListener.onClick(position));

        holder.overflow.setOnClickListener(view -> categoryListener.onOverflowClick(view, position));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        MaterialTextView name;
        MaterialButton overflow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.category_name);
            overflow = itemView.findViewById(R.id.category_overflow);
        }
    }
}
