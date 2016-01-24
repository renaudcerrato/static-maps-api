package com.mypopsy.staticmaps.demo.dagger;

import com.mypopsy.staticmaps.demo.countries.RestCountries;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.Retrofit;

@Module
public class SearchModule {

    @Provides
    @Singleton
    RestCountries provideRestCountries(Retrofit.Builder builder) {
        return builder.baseUrl(RestCountries.BASE_URL).build().create(RestCountries.class);
    }
}