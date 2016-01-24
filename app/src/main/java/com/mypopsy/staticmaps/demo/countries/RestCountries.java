package com.mypopsy.staticmaps.demo.countries;


import java.util.List;

import retrofit.http.GET;
import rx.Observable;

public interface RestCountries {

    String BASE_URL = "https://restcountries.eu/rest/v1/";

    @GET("all")
    Observable<List<Country>> all();

}
