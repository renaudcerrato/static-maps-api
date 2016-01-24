package com.mypopsy.staticmaps.demo.dagger;

import com.mypopsy.staticmaps.demo.MainActivity;
import com.mypopsy.staticmaps.demo.ui.DemoFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {RetrofitModule.class, SearchModule.class})
public interface AppComponent {
    void inject(MainActivity activity);
    void inject(DemoFragment fragment);
}