package cn.fkj233.hook.miuistatusbarlyric;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class ScrollTextView extends androidx.appcompat.widget.AppCompatTextView {
    private static final String TAG = "ScrollTextView";
    private int mOffsetX;
    private Rect mRect;
    private final int mSpeed;
    private String mText;
    private String mText2;
    private Timer mTimer;
    private TimerTask mTimerTask;

    public ScrollTextView(Context context) {
        this(context, null);
    }

    public ScrollTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mText = "";
        this.mText2 = "";
        this.mOffsetX = 0;
        this.mSpeed = -1;
        this.mRect = new Rect();
        this.mTimer = new Timer();
        this.mTimerTask = new MyTimerTask();
        getPaint().setColor(getCurrentTextColor());
        this.mTimer.schedule(this.mTimerTask, 1000, 16);
    }

    /* access modifiers changed from: protected */
    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {
        if (!this.mText.equals(this.mText2)) {
            this.mOffsetX = 0;
            this.mRect = new Rect();
            this.mTimerTask.cancel();
            this.mTimerTask = new MyTimerTask();
            this.mTimer.cancel();
            this.mTimer = new Timer();
            this.mTimer.schedule(this.mTimerTask, 1000, 16);
        }
        this.mText = this.mText2;
        Rect rect = new Rect();
        getPaint().getTextBounds(this.mText, 0, this.mText.length(), rect);
        TextPaint paint = getPaint();
        paint.setColor(getCurrentTextColor());
        paint.getTextBounds(this.mText, 0, this.mText.length(), this.mRect);
        paint.setColor(getCurrentTextColor());
        if (this.mRect.right < getWidth()) {
            canvas.drawText(this.mText, (float) 0, (float) ((getHeight() / 2) - (rect.top / 2)), paint);
        } else {
            canvas.drawText(this.mText + "        " + this.mText + "        " + this.mText + "        " + this.mText + "        " + this.mText, (float) this.mOffsetX, (float) ((getHeight() / 2) - (rect.top / 2)), paint);
        }
    }

    private class MyTimerTask extends TimerTask {
        public MyTimerTask() {
        }

        @Override
        public void run() {
            if (mRect.right >= getWidth()) {
                if (mOffsetX < ((-mRect.right) * 5) - getPaddingEnd()) {
                    mOffsetX = getPaddingStart();
                } else if (mOffsetX > getPaddingStart()) {
                    mOffsetX = -mRect.right;
                }
                mOffsetX += mSpeed;
                postInvalidate();
            }
        }
    }

    public void setText(String str) {
        this.mText2 = str;
    }

    @Override
    public String getText() {
        return this.mText2;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.e(TAG, "killTimer");
        if (this.mTimerTask != null) {
            this.mTimerTask.cancel();
            this.mTimerTask = null;
        }
        if (this.mTimer != null) {
            this.mTimer.cancel();
            this.mTimer = null;
        }
    }
}