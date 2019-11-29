package com.example.bleexample.viewmodels;


import android.bluetooth.BluetoothGattCharacteristic;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bleexample.models.GattCharacteristicDetails;
import com.example.bleexample.models.ServiceCharacteristic;

import java.util.ArrayList;
import java.util.List;


public class DeviceDetailsViewModel extends ViewModel {

    private MutableLiveData<List<String>> listUuid = new MutableLiveData<>();
    private String title;
    private String uuid;
    private List<BluetoothGattCharacteristic> bluetoothGattCharacteristics;
    private ArrayList<GattCharacteristicDetails> gattCharacteristicDetails = new ArrayList<>();

    public DeviceDetailsViewModel() {
    }

    public List<GattCharacteristicDetails> getGattCharacteristicDetails() {
        return gattCharacteristicDetails;
    }

    public void setGattCharacteristicDetails(ArrayList<GattCharacteristicDetails> gattCharacteristicDetails) {
        this.gattCharacteristicDetails = gattCharacteristicDetails;
    }

    public DeviceDetailsViewModel(ServiceCharacteristic service) {
        title = "Service " + service.getIndex();
        uuid = service.getUuid();
        this.bluetoothGattCharacteristics = service.getBluetoothGattCharacteristics();
        getList(service.getGattCharacteristicDetails());

    }
    private void getList(ArrayList<GattCharacteristicDetails> mGattCharacteristicDetails){
        gattCharacteristicDetails.addAll(mGattCharacteristicDetails);
    }
    public MutableLiveData<List<String>> getListUuid() {
        return listUuid;
    }

    public void setListUuid(MutableLiveData<List<String>> listUuid) {
        this.listUuid = listUuid;
    }

    public String getTitle() {
        return title;
    }

    public List<BluetoothGattCharacteristic> getBluetoothGattCharacteristics() {
        return bluetoothGattCharacteristics;
    }

    public void setBluetoothGattCharacteristics(List<BluetoothGattCharacteristic> bluetoothGattCharacteristics) {
        this.bluetoothGattCharacteristics = bluetoothGattCharacteristics;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
