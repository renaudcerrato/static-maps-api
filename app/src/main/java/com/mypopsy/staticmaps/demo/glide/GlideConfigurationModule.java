package com.mypopsy.staticmaps.demo.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.module.GlideModule;
import com.mypopsy.maps.StaticMap;

import java.io.InputStream;

public class GlideConfigurationModule implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {

    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        glide.register(StaticMap.class, InputStream.class, new ModelLoaderFactory<StaticMap, InputStream>() {
            @Override
            public ModelLoader<StaticMap, InputStream> build(Context context, GenericLoaderFactory factories) {
                return new StaticMapModelLoader(context);
            }

            @Override
            public void teardown() {
                //nothing to do
            }
        });
    }
}
