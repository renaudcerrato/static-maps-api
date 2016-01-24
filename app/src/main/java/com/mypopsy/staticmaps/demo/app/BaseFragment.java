package com.mypopsy.staticmaps.demo.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import butterknife.ButterKnife;

abstract public class BaseFragment extends Fragment {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
