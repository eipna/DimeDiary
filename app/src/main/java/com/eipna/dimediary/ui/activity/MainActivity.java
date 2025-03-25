package com.eipna.dimediary.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.eipna.dimediary.R;
import com.eipna.dimediary.data.Database;
import com.eipna.dimediary.data.Expense;
import com.eipna.dimediary.databinding.ActivityMainBinding;
import com.eipna.dimediary.ui.adapter.ExpenseAdapter;
import com.eipna.dimediary.util.DateUtil;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ExpenseAdapter.ExpenseListener {

    private ActivityMainBinding binding;
    private Database database;
    private ArrayList<Expense> expenses;
    private ExpenseAdapter expenseAdapter;

    private String expenseName = "";
    private double expenseAmount = 0.0;
    private long expenseDate = 0L;

    private long selectedExpenseDate = 0L;

    private String expenseCategory = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        binding.appBar.setStatusBarForegroundColor(
                MaterialColors.getColor(binding.appBar, com.google.android.material.R.attr.colorSurface)
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "Daily Reminder";
            String description = "Notification channel for daily reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel("channel_reminder", name, importance);
            channel.setDescription(description);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }

        database = new Database(this);
        expenses = new ArrayList<>(database.getExpenses());
        expenseAdapter = new ExpenseAdapter(this, this, expenses);

        binding.emptyIndicator.setVisibility(expenses.isEmpty() ? View.VISIBLE : View.GONE);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(expenseAdapter);

        binding.fab.setOnClickListener(view -> showAddDialog());
    }

    private void showAddDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_expense, null, false);

        TextInputEditText inputName = view.findViewById(R.id.input_name);
        TextInputEditText inputAmount = view.findViewById(R.id.input_amount);

        TextInputLayout layoutCategory = view.findViewById(R.id.layout_category);
        MaterialAutoCompleteTextView inputCategory = view.findViewById(R.id.input_category);

        inputCategory.setSimpleItems(database.getCategoryNames());

        TextInputLayout layoutDate = view.findViewById(R.id.layout_date);
        TextInputEditText inputDate = view.findViewById(R.id.input_date);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle("Create expense")
                .setView(view)
                .setPositiveButton("Create", null)
                .setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            if (database.getCategories().isEmpty()) {
                layoutCategory.setEnabled(false);
                inputCategory.setEnabled(false);
                layoutCategory.setHint("No categories found");
            }

            inputDate.setOnClickListener(view1 -> {
                MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select date")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();

                datePicker.show(getSupportFragmentManager(), "DATE_PICKER");

                datePicker.addOnPositiveButtonClickListener(selection -> {
                    selectedExpenseDate = selection;
                    layoutDate.setHint(DateUtil.getString(selection));
                });
            });

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view2 -> {
                if (inputName.getText().toString().isEmpty() ||
                        inputAmount.getText().toString().isEmpty() ||
                        selectedExpenseDate == 0L) {
                    Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                expenseName = inputName.getText().toString();
                expenseAmount = Double.parseDouble(inputAmount.getText().toString());
                expenseCategory = inputCategory.getText().toString();

                expenseCategory = (expenseCategory.isEmpty()) ? "uncategorized" : inputCategory.getText().toString();
                expenseDate = (expenseDate == 0L) ? System.currentTimeMillis() : selectedExpenseDate;

                Expense expense = new Expense();
                expense.setName(expenseName);
                expense.setDate(expenseDate);
                expense.setAmount(expenseAmount);
                expense.setCategoryID(database.getCategoryID(expenseCategory));

                database.createExpense(expense);
                refreshList();
                dialog.dismiss();
            });
        });
        dialog.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void refreshList() {
        expenses.clear();
        expenses.addAll(database.getExpenses());
        binding.emptyIndicator.setVisibility(expenses.isEmpty() ? View.VISIBLE : View.GONE);
        expenseAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar_main, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        assert searchView != null;
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return true;
            }
        });
        return true;
    }

    private void search(String search) {
        ArrayList<Expense> filtered = new ArrayList<>();
        for (Expense expense : expenses) {
            if (expense.getName().toLowerCase().contains(search.toLowerCase())) {
                filtered.add(expense);
            }
        }
        expenseAdapter.search(filtered);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.category) startActivity(new Intent(MainActivity.this, CategoryActivity.class));
        if (item.getItemId() == R.id.analytics) startActivity(new Intent(MainActivity.this, ChartActivity.class));
        if (item.getItemId() == R.id.settings) startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onCLick(int position) {
        Expense selectedExpense = expenses.get(position);

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_expense, null, false);

        TextInputEditText inputName = view.findViewById(R.id.input_name);
        TextInputEditText inputAmount = view.findViewById(R.id.input_amount);
        MaterialAutoCompleteTextView inputCategory = view.findViewById(R.id.input_category);

        TextInputLayout layoutDate = view.findViewById(R.id.layout_date);
        TextInputEditText inputDate = view.findViewById(R.id.input_date);

        String categoryName = database.getCategoryName(selectedExpense.getCategoryID());
        inputCategory.setSimpleItems(database.getCategoryNames());

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle("Edit expense")
                .setView(view)
                .setPositiveButton("Edit", null)
                .setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            inputName.setText(selectedExpense.getName());
            inputAmount.setText(String.valueOf(selectedExpense.getAmount()));
            layoutDate.setHint(DateUtil.getString(selectedExpense.getDate()));
            inputCategory.setText(categoryName, false);

            inputDate.setOnClickListener(view1 -> {
                MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select date")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();

                datePicker.show(getSupportFragmentManager(), "DATE_PICKER");

                datePicker.addOnPositiveButtonClickListener(selection -> {
                    selectedExpenseDate = selection;
                    layoutDate.setHint(DateUtil.getString(selection));
                });
            });

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view2 -> {
                expenseName = inputName.getText().toString();
                expenseAmount = Double.parseDouble(inputAmount.getText().toString());
                expenseCategory = inputCategory.getText().toString();
                expenseCategory = (expenseCategory.isEmpty()) ? "uncategorized" : inputCategory.getText().toString();
                expenseDate = (expenseDate == 0L) ? System.currentTimeMillis() : selectedExpenseDate;

                Expense expense = new Expense();
                expense.setID(selectedExpense.getID());
                expense.setName(expenseName);
                expense.setDate(expenseDate);
                expense.setAmount(expenseAmount);
                expense.setCategoryID(database.getCategoryID(expenseCategory));

                database.editExpense(expense);
                refreshList();
                dialog.dismiss();
            });
        });
        dialog.show();
    }

    @Override
    public void onDeleteClick(int position) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle("Delete expense?")
                .setMessage("This action will permanently delete the expense.")
                .setPositiveButton("Delete", (dialogInterface, i) -> {
                    database.deleteExpense(expenses.get(position).getID());
                    refreshList();
                })
                .setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}