package com.mypopsy.staticmaps.demo.adapter;

public abstract class BindingArrayRecyclerAdapter<T> extends ArrayRecyclerAdapter<T, BindableViewHolder<T>> {

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(BindableViewHolder holder, int position) {
        holder.bind(getItem(position));
    }
}