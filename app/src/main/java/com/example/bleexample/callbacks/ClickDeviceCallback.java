package com.example.bleexample.callbacks;

import com.example.bleexample.viewmodels.ScanViewModel;

public interface ClickDeviceCallback {
    void click(ScanViewModel scanViewModel);
    void clickInfo(ScanViewModel scanViewModel);
}
