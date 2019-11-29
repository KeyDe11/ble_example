package com.example.bleexample.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "Device")
public class Device{
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String index;
    private String name;
    private String rssi;
    private String mac;
    private String timestampNanos;
    @Ignore
    public Device() {
    }

    @Ignore
    public Device(String index, String name, String rssi, String mac, String timestampNanos) {
        this.index = index;
        this.name = name;
        this.rssi = rssi;
        this.mac = mac;
        this.timestampNanos = timestampNanos;
    }

    public Device(int id, String index, String name, String rssi, String mac, String timestampNanos) {
        this.id = id;
        this.index = index;
        this.name = name;
        this.rssi = rssi;
        this.mac = mac;
        this.timestampNanos = timestampNanos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getTimestampNanos() {
        return timestampNanos;
    }

    public void setTimestampNanos(String timestampNanos) {
        this.timestampNanos = timestampNanos;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }
}
