package com.example.bleexample.models;

import android.bluetooth.BluetoothGattCharacteristic;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ServiceCharacteristic implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int index;
    private String uuid;
    private String mac;
    private String name;
    @TypeConverters(DataConverter.class) // add here
    private ArrayList<GattCharacteristicDetails> gattCharacteristicDetails = new ArrayList<>();
    @Ignore
    private List<BluetoothGattCharacteristic> bluetoothGattCharacteristics = new ArrayList<>();

    public ArrayList<GattCharacteristicDetails> getGattCharacteristicDetails() {
        if (gattCharacteristicDetails==null){
            return new ArrayList<>();
        }
        return gattCharacteristicDetails;
    }

    public void setGattCharacteristicDetails(ArrayList<GattCharacteristicDetails> gattCharacteristicDetails) {
        this.gattCharacteristicDetails = gattCharacteristicDetails;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    @Ignore
    public ServiceCharacteristic(int index, String uuid, List<BluetoothGattCharacteristic> bluetoothGattCharacteristics) {
        this.index = index;
        this.uuid = uuid;
        this.bluetoothGattCharacteristics = bluetoothGattCharacteristics;
        getList(bluetoothGattCharacteristics);
    }
    private void getList(List<BluetoothGattCharacteristic> bluetoothGattCharacteristics){
        for (BluetoothGattCharacteristic mBluetoothGattCharacteristic : bluetoothGattCharacteristics){
            gattCharacteristicDetails.add(new GattCharacteristicDetails(mBluetoothGattCharacteristic.getUuid() + "",mBluetoothGattCharacteristic.getProperties()));
        }
    }

    public ServiceCharacteristic(int id, int index, String uuid, String mac, String name, ArrayList<GattCharacteristicDetails> gattCharacteristicDetails) {
        this.id = id;
        this.index = index;
        this.uuid = uuid;
        this.mac = mac;
        this.name = name;
        this.gattCharacteristicDetails = gattCharacteristicDetails;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public List<BluetoothGattCharacteristic> getBluetoothGattCharacteristics() {
        return bluetoothGattCharacteristics;
    }

    public void setBluetoothGattCharacteristics(List<BluetoothGattCharacteristic> bluetoothGattCharacteristics) {
        this.bluetoothGattCharacteristics = bluetoothGattCharacteristics;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
