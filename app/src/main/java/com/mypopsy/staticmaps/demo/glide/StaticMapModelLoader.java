package com.mypopsy.staticmaps.demo.glide;

import android.content.Context;

import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;
import com.mypopsy.maps.StaticMap;

public class StaticMapModelLoader extends BaseGlideUrlLoader<StaticMap> {

    public StaticMapModelLoader(Context context) {
        super(context);
    }

    @Override
    protected String getUrl(StaticMap model, int width, int height) {
        int scale = model.scale();

        // if default scale, use scale(2) to get crisper images.
        // scale(4) is reserved to premium static map users.
        if(scale == 1) {
            model.scale(scale = 2);
        }

        return model.size(width/scale, height/scale).toString();
    }
}
