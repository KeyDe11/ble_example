package com.example.bleexample.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "GattCharacteristicDetails")
public class GattCharacteristicDetails {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String uuid;
    private int charaProp;

    @Ignore
    public GattCharacteristicDetails(String uuid, int charaProp) {
        this.uuid = uuid;
        this.charaProp = charaProp;
    }

    public GattCharacteristicDetails(int id, String uuid, int charaProp) {
        this.id = id;
        this.uuid = uuid;
        this.charaProp = charaProp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getCharaProp() {
        return charaProp;
    }

    public void setCharaProp(int charaProp) {
        this.charaProp = charaProp;
    }
}
