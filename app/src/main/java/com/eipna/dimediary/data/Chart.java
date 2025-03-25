package com.eipna.dimediary.data;

import android.graphics.Color;

public class Chart {
    private String color;
    private String name;
    private double sum;

    public Chart() {
        this.color = null;
        this.name = null;
        this.sum = -1;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }
}