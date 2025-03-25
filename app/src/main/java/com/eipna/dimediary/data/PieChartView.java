package com.eipna.dimediary.data;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

public class PieChartView extends View {
    private List<PieSlice> slices;
    private Paint paint;
    private RectF rectF;

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        rectF = new RectF(100, 100, 800, 800);
    }

    public void setData(List<PieSlice> slices) {
        this.slices = slices;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (slices == null || slices.isEmpty()) return;

        float currentAngle = 0f;
        for (PieSlice slice : slices) {
            paint.setColor(slice.getColor());
            canvas.drawArc(rectF, currentAngle, slice.getValue(), true, paint);
            currentAngle += slice.getValue();
        }
    }

    public static class PieSlice {
        private double value;
        private int color;

        public PieSlice(double value, int color) {
            this.value = value;
            this.color = color;
        }

        public float getValue() {
            return (float) value;
        }

        public int getColor() {
            return color;
        }
    }
}