package com.mypopsy.staticmaps.demo.app;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mypopsy.staticmaps.demo.R;

import butterknife.Bind;

abstract public class RecyclerViewFragment extends BaseFragment {

    public enum Status {
        DEFAULT,
        LOADING,
        ERROR,
        EMPTY,
    }

    @Bind(android.R.id.list)
    RecyclerView mRecyclerView;

    @Bind(android.R.id.empty) @Nullable
    View mEmptyView;

    @Bind(R.id.loading) @Nullable
    View mLoadingView;

    @Bind(R.id.error) @Nullable
    View mErrorView;

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    protected void setAdapter(RecyclerView.Adapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }

    protected RecyclerView.Adapter getAdapter() {
        if(mRecyclerView == null) return null;
        return mRecyclerView.getAdapter();
    }

    protected boolean isListShown() {
        if(mRecyclerView == null) return false;
        return mRecyclerView.getVisibility() == View.VISIBLE;
    }

    protected void setListShown(boolean shown) {
        setListShown(shown ? Status.DEFAULT : Status.EMPTY);
    }

    protected void setListShown(Status status) {
        switch(status) {
            case DEFAULT:
                setVisibility(View.GONE, mLoadingView, mErrorView, mEmptyView);
                break;
            case LOADING:
                setVisibility(View.GONE, mEmptyView, mErrorView);
                setVisibility(View.VISIBLE, mLoadingView);
                break;
            case ERROR:
                setVisibility(View.GONE, mLoadingView, mEmptyView);
                setVisibility(View.VISIBLE, mErrorView);
                break;
            case EMPTY:
                setVisibility(View.GONE, mLoadingView, mErrorView);
                setVisibility(View.VISIBLE, mEmptyView);
                break;
        }

        setVisibility(status == Status.DEFAULT ? View.VISIBLE : View.INVISIBLE, mRecyclerView);
    }

    static private void setVisibility(int visibility, View ...views) {
        for(View v: views)
            if(v != null && v.getVisibility() != visibility)
                v.setVisibility(visibility);
    }
}
