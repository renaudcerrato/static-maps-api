package com.mypopsy.staticmaps.demo.countries;

import java.io.Serializable;

public class Country implements Serializable {
    public String name, nativeName;
    public String capital;
    public String region, subregion;
    public int population;
    public double location[];
    public String[] languages;

}
