package com.example.bleexample.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bleexample.models.Characteristic;

import java.util.ArrayList;

public class CharacteristicViewModel extends ViewModel {
    private byte[] data;
    private String serviceUuid;
    private String characteristicUuid;
    private String mac;
    private long timestamp;
    private MutableLiveData<ArrayList<CharacteristicViewModel>> liveData = new MutableLiveData<>();
    private ArrayList<CharacteristicViewModel> characteristicViewModels ;

    public CharacteristicViewModel(byte[] data, String serviceUuid, String characteristicUuid, String mac, long timestamp) {
        this.data = data;
        this.serviceUuid = serviceUuid;
        this.characteristicUuid = characteristicUuid;
        this.mac = mac;
        this.timestamp = timestamp;
    }

    public CharacteristicViewModel() {
    }

    public CharacteristicViewModel(Characteristic characteristic) {
        this.data = characteristic.getData();
        this.serviceUuid = characteristic.getServiceUuid();
        this.characteristicUuid = characteristic.getCharacteristicUuid();
        this.mac = characteristic.getMac();
        this.timestamp = characteristic.getTimestamp();
    }

    public MutableLiveData<ArrayList<CharacteristicViewModel>> getDataCharacteristic()
    {
        if(characteristicViewModels ==null || characteristicViewModels.isEmpty()) characteristicViewModels = new ArrayList<>();

        liveData.setValue(characteristicViewModels);

        return liveData;
    }
    public MutableLiveData<ArrayList<CharacteristicViewModel>> getLiveData() {
        return liveData;
    }

    public void setLiveData(MutableLiveData<ArrayList<CharacteristicViewModel>> liveData) {
        this.liveData = liveData;
    }

    public ArrayList<CharacteristicViewModel> getCharacteristicViewModels() {
        return characteristicViewModels;
    }

    public void setCharacteristicViewModels(ArrayList<CharacteristicViewModel> characteristicViewModels) {
        this.characteristicViewModels = characteristicViewModels;
    }

    public byte[] getData() {
        return data;
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

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
