package cn.fkj233.hook.miuistatusbarlrcy;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CTextView extends ViewGroup {
    private AnimationTools at = new AnimationTools();
    private Context context;

    public CTextView(Context context) {
        super(context);
        this.context = context;
    }

    public CTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
    }

    public CTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.context = context;
    }

    public void setText(String str, Context context, int i, float f, Typeface typeface, ColorStateList colorStateList) {
        if (!(str == null || str.isEmpty())) {
            removeAllViews();
            char[] charArray = str.toCharArray();
            TextView[] viewArr = new TextView[charArray.length];
            for (int i2 = 0; i2 < charArray.length; i2++) {
                viewArr[i2] = new TextView(context);
                viewArr[i2].setTextColor(colorStateList);
                viewArr[i2].setHeight(i);
                viewArr[i2].setTypeface(typeface);
                viewArr[i2].setGravity(19);
                viewArr[i2].setText(charArray[i2] + "");
                viewArr[i2].setTextSize(0, f);
                addView(viewArr[i2]);
                viewArr[i2].startAnimation(this.at.animRotate(i2));
            }
        }
    }

    public int getW() {
        int i = 0;
        int childCount = getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            i += getChildAt(i2).getMeasuredWidth();
        }
        return i;
    }

    public void setTextColor(ColorStateList colorStateList) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            ((TextView) getChildAt(i)).setTextColor(colorStateList);
        }
    }

    public void onMeasure(int i, int i2) {
        int measureWidth = measureWidth(i);
        int measureHeight = measureHeight(i2);
        measureChildren(i, i2);
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5 = 5;
        int childCount = getChildCount();
        for (int i6 = 0; i6 < childCount; i6++) {
            View childAt = getChildAt(i6);
            int measuredHeight = childAt.getMeasuredHeight();
            int measuredWidth = childAt.getMeasuredWidth();
            childAt.layout(i5, 0, i5 + measuredWidth, measuredHeight);
            i5 += measuredWidth;
        }
    }

    private int measureWidth(int i) {
        int i2 = 0;
        int mode = View.MeasureSpec.getMode(i);
        int size = View.MeasureSpec.getSize(i);
        switch (mode) {
            case Integer.MIN_VALUE:
            case 1073741824:
                i2 = size;
                break;
        }
        return i2;
    }

    private int measureHeight(int i) {
        int i2 = 0;
        int mode = View.MeasureSpec.getMode(i);
        int size = View.MeasureSpec.getSize(i);
        switch (mode) {
            case Integer.MIN_VALUE:
            case 1073741824:
                i2 = size;
                break;
        }
        return i2;
    }
}