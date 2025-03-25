package com.eipna.dimediary.data;

public class Expense {
    private int ID;
    private String name;
    private double amount;
    private long date;
    private int categoryID;

    public Expense() {
        this.ID = -1;
        this.name = null;
        this.amount = -1;
        this.date = -1;
        this.categoryID = -1;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }
}