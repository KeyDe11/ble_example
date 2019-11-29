package com.example.bleexample.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bleexample.models.Device;

import java.util.ArrayList;

public class ScanViewModel extends ViewModel{
    private String index;
    private String name;
    private String rssi;
    private String mac;
    private String timestampNanos;
    private boolean click = false;
    private boolean clickScan = true;
    private MutableLiveData<Device> device = new MutableLiveData<>();
    private MutableLiveData<Device> deviceMutableLiveData = new MutableLiveData<>();
    private ArrayList<ScanViewModel> devices ;
    private MutableLiveData<ArrayList<ScanViewModel>> liveData = new MutableLiveData<>();
    public Device getDevice(int index){
        ScanViewModel scanViewModel = devices.get(index);
        return new Device(scanViewModel.getIndex(),scanViewModel.getName(),scanViewModel.rssi,scanViewModel.getMac(),scanViewModel.getTimestampNanos());
    }
    public MutableLiveData<Device> getDeviceMutableLiveData() {
        return deviceMutableLiveData;
    }

    public boolean isClickScan() {
        return clickScan;
    }

    public void setClickScan(boolean clickScan) {
        this.clickScan = clickScan;
    }

    public MutableLiveData<Device> getDevice() {
        return device;
    }

    public MutableLiveData<ArrayList<ScanViewModel>> getData()
    {
        if(devices ==null || devices.isEmpty()) devices = new ArrayList<>();

        liveData.setValue(devices);

        return liveData;
    }
    public void setDevice(MutableLiveData<Device> device) {
        this.device = device;
    }

    public void setDevices(ArrayList<ScanViewModel> devices) {
        this.devices = devices;
    }

    public boolean isClick() {
        return click;
    }

    public void setClick(boolean click) {
        this.click = click;
    }

    public ScanViewModel(){

    }
    public ScanViewModel(Device device){
        this.index = device.getIndex();
        this.name = device.getName();
        this.rssi = device.getRssi();
        this.mac = device.getMac();
        this.timestampNanos = device.getTimestampNanos();
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
