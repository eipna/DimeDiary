package com.eipna.dimediary.data;

public class Category {
    private int ID;
    private String name;
    private String color;

    public Category() {
        this.ID = -1;
        this.name = null;
        this.color = null;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
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
}