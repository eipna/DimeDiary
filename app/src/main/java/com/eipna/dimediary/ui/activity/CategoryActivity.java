package com.eipna.dimediary.ui.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.eipna.dimediary.R;
import com.eipna.dimediary.data.Category;
import com.eipna.dimediary.data.Database;
import com.eipna.dimediary.databinding.ActivityCategoryBinding;
import com.eipna.dimediary.ui.adapter.CategoryAdapter;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class CategoryActivity extends AppCompatActivity implements CategoryAdapter.CategoryListener {

    private ActivityCategoryBinding binding;
    private Database database;
    private ArrayList<Category> categories;
    private CategoryAdapter categoryAdapter;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        binding.appBar.setStatusBarForegroundColor(
                MaterialColors.getColor(binding.appBar, com.google.android.material.R.attr.colorSurface)
        );

        random = new Random();

        database = new Database(this);
        categories = new ArrayList<>(database.getCategories());
        categoryAdapter = new CategoryAdapter(this, this, categories);

        binding.emptyIndicator.setVisibility(categories.isEmpty() ? View.VISIBLE : View.GONE);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(categoryAdapter);

        binding.fab.setOnClickListener(view -> showAddDialog());
    }

    @SuppressLint("DefaultLocale")
    private void showAddDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null, false);

        TextInputLayout layoutCategoryName = view.findViewById(R.id.layout_category_name);
        TextInputEditText inputCategoryName = view.findViewById(R.id.input_category_name);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle("Add category")
                .setView(view)
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view1 -> {
            String name = Objects.requireNonNull(inputCategoryName.getText()).toString();
            for (Category category : categories) {
                if (name.equalsIgnoreCase(category.getName())) {
                    Toast.makeText(this, "The category already exists", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            
            Category category = new Category();
            category.setName(name);
            category.setColor(String.format("%d,%d,%d", random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            database.createCategory(category);
            refreshList();
            dialog.dismiss();
        }));
        dialog.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void refreshList() {
        categories.clear();
        categories.addAll(database.getCategories());
        binding.emptyIndicator.setVisibility(categories.isEmpty() ? View.VISIBLE : View.GONE);
        categoryAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onClick(int position) {
        Category selectedCategory = categories.get(position);

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null, false);

        TextInputLayout layoutCategoryName = view.findViewById(R.id.layout_category_name);
        TextInputEditText inputCategoryName = view.findViewById(R.id.input_category_name);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle("Edit category")
                .setView(view)
                .setPositiveButton("Edit", null)
                .setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            inputCategoryName.setText(selectedCategory.getName());

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view1 -> {
                String name = Objects.requireNonNull(inputCategoryName.getText()).toString();
                Category category = new Category();
                category.setID(selectedCategory.getID());
                category.setName(name);
                database.editCategory(category);
                refreshList();
                dialog.dismiss();
            });
        });
        dialog.show();
    }

    @Override
    public void onOverflowClick(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_category_popup, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.delete) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(CategoryActivity.this)
                        .setTitle("Delete category?")
                        .setMessage("This will permanently delete the selected category.")
                        .setPositiveButton("Delete", (dialogInterface, i) -> {
                            database.deleteCategory(categories.get(position).getID());
                            refreshList();
                        })
                        .setNegativeButton("Cancel", null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
            return true;
        });

        popupMenu.show();
    }
}