package com.mypopsy.staticmaps.demo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.mypopsy.staticmaps.demo.R;


public class AspectLockedImageView extends ImageView {

    private static final boolean DEBUG = false;
    private double mAspectRatio;

    public AspectLockedImageView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public AspectLockedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public AspectLockedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AspectLockedImageView, defStyle, 0);
        mAspectRatio = a.getFloat(R.styleable.AspectLockedImageView_aspectRatio, 0);
        a.recycle();
    }

    public void setAspectRatio(double ratio) {
        if (ratio != mAspectRatio) {
            mAspectRatio = ratio;
            requestLayout();
        }
    }

    public double getAspectRatio() {
        return mAspectRatio;
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {

        int widthMode = MeasureSpec.getMode(widthSpec);
        int heightMode = MeasureSpec.getMode(heightSpec);


        if (mAspectRatio == 0.0 || ((widthMode == MeasureSpec.EXACTLY &&
                heightMode == MeasureSpec.EXACTLY))) {
            super.onMeasure(widthSpec, heightSpec);
            return;
        }

        int lockedWidth=MeasureSpec.getSize(widthSpec);
        int lockedHeight=MeasureSpec.getSize(heightSpec);

        if(DEBUG) {
            Log.d(getClass().getSimpleName(), "{ratio=" + mAspectRatio + ", " +
                    "lockedWidth=" + lockedWidth + ", lockedHeight=" + lockedHeight + ", " +
                    "widthMode=" + (widthMode == MeasureSpec.EXACTLY ? "exactly" : widthMode == MeasureSpec.AT_MOST ? "at_most" : "unspecified") + ", " +
                    "heightMode=" + (heightMode == MeasureSpec.EXACTLY ? "exactly" : heightMode == MeasureSpec.AT_MOST ? "at_most" : "unspecified") + "}");
        }

        // Get the padding of the border background.
        int hPadding=getPaddingLeft() + getPaddingRight();
        int vPadding=getPaddingTop() + getPaddingBottom();

        // Resize the preview frame with correct aspect ratio.
        lockedWidth-=hPadding;
        lockedHeight-=vPadding;

        if (lockedHeight > 0 && ((lockedWidth > lockedHeight * mAspectRatio)
                || widthMode == MeasureSpec.UNSPECIFIED)) {
            lockedWidth=(int)(lockedHeight * mAspectRatio + .5);
        }
        else {
            lockedHeight=(int)(lockedWidth / mAspectRatio + .5);
        }

        // Add the padding of the border.
        lockedWidth+=hPadding;
        lockedHeight+=vPadding;

        // Ask children to follow the new preview dimension.
        super.onMeasure(MeasureSpec.makeMeasureSpec(lockedWidth,
                        MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(lockedHeight,
                        MeasureSpec.EXACTLY));
    }
}