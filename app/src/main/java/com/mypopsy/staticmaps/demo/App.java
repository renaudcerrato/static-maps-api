package com.mypopsy.staticmaps.demo;

import android.app.Application;
import android.content.Context;

import com.mypopsy.staticmaps.demo.dagger.AppComponent;
import com.mypopsy.staticmaps.demo.dagger.DaggerAppComponent;

public class App extends Application {

    private AppComponent mComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mComponent = DaggerAppComponent.builder().build();
    }

    public AppComponent component() {
        return mComponent;
    }

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }
}
