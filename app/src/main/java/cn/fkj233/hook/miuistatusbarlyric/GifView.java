package cn.fkj233.hook.miuistatusbarlyric;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

public class GifView extends View {
    private int mCurrentAnimationTime;
    private float mLeft;
    private int mMeasuredMovieHeight;
    private int mMeasuredMovieWidth;
    private Movie mMovie;
    private long mMovieStart;
    private volatile boolean mPaused;
    private float mScale;
    private float mTop;
    private boolean mVisible;

    public GifView(Context context) {
        this(context, null);
    }

    public GifView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public GifView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mCurrentAnimationTime = 0;
        this.mVisible = true;
        this.mPaused = false;
        setViewAttributes(context, attributeSet);
    }

    @SuppressLint({"NewApi", "WrongConstant"})
    private void setViewAttributes(Context context, AttributeSet attributeSet) {
        setLayerType(1, null);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, new int[]{2130771989, 2130771990});
        int mMovieResourceId = obtainStyledAttributes.getResourceId(0, -1);
        this.mPaused = obtainStyledAttributes.getBoolean(1, false);
        obtainStyledAttributes.recycle();
        if (mMovieResourceId != -1) {
            this.mMovie = Movie.decodeStream(getResources().openRawResource(mMovieResourceId));
        }
    }

    public void setMovieResource(String str) {
        this.mMovie = Movie.decodeFile(str);
        requestLayout();
    }

    @Override
    public void onMeasure(int i, int i2) {
        if (this.mMovie != null) {
            int width = this.mMovie.width();
            int height = this.mMovie.height();
            int size = View.MeasureSpec.getSize(i);
            this.mScale = 1.0f / (((float) width) / ((float) size));
            this.mMeasuredMovieWidth = size;
            this.mMeasuredMovieHeight = (int) (((float) height) * this.mScale);
            setMeasuredDimension(this.mMeasuredMovieWidth, this.mMeasuredMovieHeight);
            return;
        }
        setMeasuredDimension(getSuggestedMinimumWidth(), getSuggestedMinimumHeight());
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.mLeft = ((float) (getWidth() - this.mMeasuredMovieWidth)) / 2.0f;
        this.mTop = ((float) (getHeight() - this.mMeasuredMovieHeight)) / 2.0f;
        this.mVisible = getVisibility() == 0;
    }
    
    @Override
    public void onDraw(Canvas canvas) {
        if (this.mMovie == null) {
            return;
        }
        if (!this.mPaused) {
            updateAnimationTime();
            drawMovieFrame(canvas);
            invalidateView();
            return;
        }
        drawMovieFrame(canvas);
    }

    @SuppressLint("NewApi")
    private void invalidateView() {
        if (!this.mVisible) {
            return;
        }
        postInvalidateOnAnimation();
    }

    private void updateAnimationTime() {
        long uptimeMillis = SystemClock.uptimeMillis();
        if (this.mMovieStart == ((long) 0)) {
            this.mMovieStart = uptimeMillis;
        }
        int duration = this.mMovie.duration();
        if (duration == 0) {
            duration = 1000;
        }
        this.mCurrentAnimationTime = (int) ((uptimeMillis - this.mMovieStart) % ((long) duration));
    }

    private void drawMovieFrame(Canvas canvas) {
        this.mMovie.setTime(this.mCurrentAnimationTime);
        canvas.save();
        canvas.scale(this.mScale, this.mScale);
        this.mMovie.draw(canvas, this.mLeft / this.mScale, this.mTop / this.mScale);
        canvas.restore();
    }

    @Override
    @SuppressLint("NewApi")
    public void onScreenStateChanged(int i) {
        super.onScreenStateChanged(i);
        this.mVisible = i == 1;
        invalidateView();
    }

    @Override
    @SuppressLint("NewApi")
    public void onVisibilityChanged(View view, int i) {
        super.onVisibilityChanged(view, i);
        this.mVisible = i == 0;
        invalidateView();
    }

    @Override
    public void onWindowVisibilityChanged(int i) {
        super.onWindowVisibilityChanged(i);
        this.mVisible = i == 0;
        invalidateView();
    }
}