package com.mypopsy.staticmaps.demo.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class PaddingItemDecoration extends RecyclerView.ItemDecoration {

    private final int mLeft, mTop, mRight, mBottom;

    public PaddingItemDecoration(int left, int top, int right, int bottom) {
        mLeft = left;
        mTop = top;
        mRight = right;
        mBottom = bottom;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(mLeft, mTop, mRight, mBottom);
    }
}