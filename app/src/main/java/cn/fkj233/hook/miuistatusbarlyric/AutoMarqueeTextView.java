package cn.fkj233.hook.miuistatusbarlyric;


import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

public class AutoMarqueeTextView extends TextView {
    @Override
    public void onFocusChanged(boolean z, int i, Rect rect) {
        super.onFocusChanged(z, i, rect);
    }

    public AutoMarqueeTextView(Context context) {
        super(context);
        setFocusable(true);
    }

    public AutoMarqueeTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setFocusable(true);
    }

    public AutoMarqueeTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setFocusable(true);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}