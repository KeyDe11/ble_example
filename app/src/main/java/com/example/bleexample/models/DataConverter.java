package com.example.bleexample.models;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class DataConverter implements Serializable {


    @TypeConverter // note this annotation
    public String fromOptionValuesList(ArrayList<GattCharacteristicDetails> optionValues) {
        if (optionValues == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<GattCharacteristicDetails>>() {
        }.getType();
        String json = gson.toJson(optionValues, type);
        return json;
    }

    @TypeConverter // note this annotation
    public ArrayList<GattCharacteristicDetails> toOptionValuesList(String optionValuesString) {
        if (optionValuesString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<GattCharacteristicDetails>>() {
        }.getType();
        ArrayList<GattCharacteristicDetails> productCategoriesList = gson.fromJson(optionValuesString, type);
        return productCategoriesList;
    }
}
