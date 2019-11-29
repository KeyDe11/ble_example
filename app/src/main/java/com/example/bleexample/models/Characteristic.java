package com.example.bleexample.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Characteristic {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private byte[] data;
    private String serviceUuid;
    private String characteristicUuid;
    private String mac;
    @ColumnInfo(name = "current")
    private long timestamp;

    @Ignore
    public Characteristic(byte[] data, String serviceUuid, String characteristicUuid, long timestamp) {
        this.data = data;
        this.serviceUuid = serviceUuid;
        this.characteristicUuid = characteristicUuid;
        this.timestamp = timestamp;
    }

    public Characteristic(int id, byte[] data, String serviceUuid, String characteristicUuid, String mac, long timestamp) {
        this.id = id;
        this.data = data;
        this.serviceUuid = serviceUuid;
        this.characteristicUuid = characteristicUuid;
        this.mac = mac;
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public byte[] getData() {
        return data;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setData(byte[] data) {
        this.data = data;
    }

    public String getServiceUuid() {
        return serviceUuid;
    }

    public void setServiceUuid(String serviceUuid) {
        this.serviceUuid = serviceUuid;
    }

    public String getCharacteristicUuid() {
        return characteristicUuid;
    }

    public void setCharacteristicUuid(String characteristicUuid) {
        this.characteristicUuid = characteristicUuid;
    }
}
