package com.eipna.dimediary.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.eipna.dimediary.ui.adapter.CategoryAdapter;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {

    private final Context context;

    private static final String DATABASE_NAME = "dimediary.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_EXPENSE = "expenses";
    private static final String COL_EXPENSE_ID = "id";
    private static final String COL_EXPENSE_NAME = "name";
    private static final String COL_EXPENSE_AMOUNT = "amount";
    private static final String COL_EXPENSE_CATEGORY = "category";
    private static final String COL_EXPENSE_DATE = "date";

    private static final String TABLE_CATEGORY = "categories";
    private static final String COL_CATEGORY_ID = "id";
    private static final String COL_CATEGORY_NAME = "name";
    private static final String COL_CATEGORY_COLOR = "color";

    public Database(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createExpenseTable = "CREATE TABLE IF NOT EXISTS " + TABLE_EXPENSE + "(" +
                COL_EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EXPENSE_NAME + " TEXT, " +
                COL_EXPENSE_AMOUNT + " REAL, " +
                COL_EXPENSE_DATE + " INTEGER, " +
                COL_EXPENSE_CATEGORY + " INTEGER DEFAULT 0);";

        String createCategoryTable = "CREATE TABLE IF NOT EXISTS " + TABLE_CATEGORY + "(" +
                COL_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CATEGORY_COLOR + " TEXT NOT NULL, " +
                COL_CATEGORY_NAME + " TEXT NOT NULL);";

        sqLiteDatabase.execSQL(createExpenseTable);
        sqLiteDatabase.execSQL(createCategoryTable);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        onCreate(sqLiteDatabase);
    }

    public void createExpense(Expense expense) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_EXPENSE_NAME, expense.getName());
        values.put(COL_EXPENSE_AMOUNT, expense.getAmount());
        values.put(COL_EXPENSE_DATE, expense.getDate());
        values.put(COL_EXPENSE_CATEGORY, expense.getCategoryID());

        database.insert(TABLE_EXPENSE, null, values);
        database.close();
    }

    @SuppressLint("Range")
    public ArrayList<Expense> getExpenses() {
        ArrayList<Expense> list = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_EXPENSE;
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setID(cursor.getInt(cursor.getColumnIndex(COL_EXPENSE_ID)));
                expense.setName(cursor.getString(cursor.getColumnIndex(COL_EXPENSE_NAME)));
                expense.setAmount(cursor.getDouble(cursor.getColumnIndex(COL_EXPENSE_AMOUNT)));
                expense.setDate(cursor.getLong(cursor.getColumnIndex(COL_EXPENSE_DATE)));
                expense.setCategoryID(cursor.getInt(cursor.getColumnIndex(COL_EXPENSE_CATEGORY)));
                list.add(expense);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return list;
    }

    @SuppressLint("Range")
    public ArrayList<Expense> getExpenses(int categoryId) {
        ArrayList<Expense> list = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_EXPENSE + " WHERE " + COL_EXPENSE_CATEGORY + " = ?";
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(categoryId)});

        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setID(cursor.getInt(cursor.getColumnIndex(COL_EXPENSE_ID)));
                expense.setName(cursor.getString(cursor.getColumnIndex(COL_EXPENSE_NAME)));
                expense.setAmount(cursor.getDouble(cursor.getColumnIndex(COL_EXPENSE_AMOUNT)));
                expense.setDate(cursor.getLong(cursor.getColumnIndex(COL_EXPENSE_DATE)));
                expense.setCategoryID(cursor.getInt(cursor.getColumnIndex(COL_EXPENSE_CATEGORY)));
                list.add(expense);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return list;
    }

    public void createCategory(Category category) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_CATEGORY_NAME, category.getName());
        values.put(COL_CATEGORY_COLOR, category.getColor());
        database.insert(TABLE_CATEGORY, null, values);
        database.close();
    }

    @SuppressLint("Range")
    public ArrayList<Category> getCategories() {
        ArrayList<Category> list = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CATEGORY;
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setID(cursor.getInt(cursor.getColumnIndex(COL_CATEGORY_ID)));
                category.setName(cursor.getString(cursor.getColumnIndex(COL_CATEGORY_NAME)));
                category.setColor(cursor.getString(cursor.getColumnIndex(COL_CATEGORY_COLOR)));
                list.add(category);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return list;
    }

    public String[] getCategoryNames() {
        ArrayList<Category> categories = getCategories();
        String[] names = new String[categories.size()];
        for (int i = 0; i < categories.size(); i++) {
            names[i] = categories.get(i).getName();
        }
        return names;
    }

    public int getCategoryID(String name) {
        if (name.equals("uncategorized")) {
            return -1;
        }

        ArrayList<Category> categories = getCategories();
        for (Category category : categories) {
            if (category.getName().equals(name)) {
                return category.getID();
            }
        }
        return -1;
    }

    public void deleteExpense(int ID) {
        SQLiteDatabase database = getWritableDatabase();
        database.delete(TABLE_EXPENSE, COL_EXPENSE_ID + " = ?", new String[]{String.valueOf(ID)});
        database.close();
    }

    public void deleteCategory(int ID) {
        SQLiteDatabase database = getWritableDatabase();
        database.delete(TABLE_CATEGORY, COL_CATEGORY_ID + " = ?", new String[]{String.valueOf(ID)});
        database.close();
    }

    public void editCategory(Category category) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_CATEGORY_NAME, category.getName());
        database.update(TABLE_CATEGORY, values, COL_CATEGORY_ID + " = ?", new String[]{String.valueOf(category.getID())});
        database.close();
    }

    public void editExpense(Expense expense) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_EXPENSE_NAME, expense.getName());
        values.put(COL_EXPENSE_AMOUNT, expense.getAmount());
        values.put(COL_EXPENSE_DATE, expense.getDate());
        values.put(COL_EXPENSE_CATEGORY, expense.getCategoryID());
        database.update(TABLE_EXPENSE, values, COL_EXPENSE_ID + " = ?", new String[]{String.valueOf(expense.getID())});
        database.close();
    }

    public String getCategoryName(int ID) {
        ArrayList<Category> categories = getCategories();
        for (Category category : categories) {
            if (category.getID() == ID) {
                return category.getName();
            }
        }
        return "Uncategorized";
    }

    public ArrayList<Double> getCategorySums() {
        ArrayList<Double> list = new ArrayList<>();
        ArrayList<Integer> categoryIDs = new ArrayList<>();
        ArrayList<Category> categories = getCategories();

        for (int i = 0; i < categories.size(); i++) {
            categoryIDs.add(categories.get(i).getID());
        }

        for (int i = 0; i < categoryIDs.size(); i++) {
            ArrayList<Expense> expenses = getExpenses(categoryIDs.get(i));

            double sum = 0;
            for (int j = 0; j < expenses.size(); j++) {
                sum += expenses.get(j).getAmount();
            }

            list.add(sum);
        }

        return list;
    }
}