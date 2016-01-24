package com.mypopsy.staticmaps.demo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class BindableViewHolder<T> extends RecyclerView.ViewHolder {
    public BindableViewHolder(View itemView) {
        super(itemView);
    }
    protected abstract void bind(T object);
}