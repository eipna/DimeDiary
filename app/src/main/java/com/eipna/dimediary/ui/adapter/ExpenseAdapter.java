package com.eipna.dimediary.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eipna.dimediary.R;
import com.eipna.dimediary.data.Database;
import com.eipna.dimediary.data.Expense;
import com.eipna.dimediary.util.DateUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.textview.MaterialTextView;

import java.text.NumberFormat;
import java.util.ArrayList;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    private final Context context;
    private ArrayList<Expense> expenses;
    private final Database database;
    private final ExpenseListener expenseListener;

    public interface ExpenseListener {
        void onCLick(int position);
        void onDeleteClick(int position);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void search(ArrayList<Expense> searchedExpenses) {
        expenses = searchedExpenses;
        notifyDataSetChanged();
    }

    public ExpenseAdapter(Context context, ExpenseListener expenseListener, ArrayList<Expense> expenses) {
        this.context = context;
        this.expenses = expenses;
        this.expenseListener = expenseListener;
        this.database = new Database(context);
    }

    @NonNull
    @Override
    public ExpenseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_expense, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ExpenseAdapter.ViewHolder holder, int position) {
        Expense expense = expenses.get(position);
        holder.name.setText(expense.getName());
        holder.date.setText(DateUtil.getString(expense.getDate()));
        holder.amount.setText("PHP " + NumberFormat.getNumberInstance().format(expense.getAmount()));
        holder.category.setText(database.getCategoryName(expense.getCategoryID()));

        holder.itemView.setOnClickListener(view -> expenseListener.onCLick(position));

        holder.delete.setOnClickListener(view -> expenseListener.onDeleteClick(position));
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        MaterialButton delete;
        MaterialTextView name, date, amount;
        Chip category;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.expense_name);
            date = itemView.findViewById(R.id.expense_date);
            amount = itemView.findViewById(R.id.expense_amount);
            category = itemView.findViewById(R.id.category_chip);
            delete = itemView.findViewById(R.id.expense_delete);
        }
    }
}