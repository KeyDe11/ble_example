package com.example.bleexample.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.example.bleexample.LocationPermission;
import com.example.bleexample.R;
import com.example.bleexample.adapters.DeviceAdapter;
import com.example.bleexample.callbacks.ScanButtonCallback;
import com.example.bleexample.database.DatabaseClient;
import com.example.bleexample.databinding.BindingScanActivity;
import com.example.bleexample.models.Device;
import com.example.bleexample.viewmodels.ScanViewModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;

public class ScanActivity extends AppCompatActivity implements ScanButtonCallback {
    private static final int REQUEST_CODE_OPEN_GPS = 1;
    private final static int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_CODE_PERMISSION_LOCATION = 2;
    private static final String TAG = "ScanActivityTag";


    private ArrayList<ScanViewModel> devices;
    private RecyclerView recyclerView;
    private DeviceAdapter adapter;
    private BindingScanActivity binding;
    private ScanViewModel viewModel;
    private List<BleDevice> bleDevices;
    private ProgressDialog progressDialog;
    private Disposable disposable;
    private List<Device> devicesWithDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        uiThread = new Handler();
        init();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading");

        BleManager.getInstance().init(getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);
    }
    private void init() {
        viewModel = ViewModelProviders.of(this).get(ScanViewModel.class);
        binding.setHandler(this);
        binding.setViewModel(viewModel);
        binding.progressBar.setVisibility(View.INVISIBLE);
        devices = new ArrayList<>();
        bleDevices = new ArrayList<>();
        recyclerView = binding.recyclerView;
        adapter = new DeviceAdapter(devices,viewModel);
        observeViewModel();
        devicesWithDatabase = new LinkedList<>();
        getDataWithDatabase();
    }

    private boolean isDeviceInDatabase(Device device){
        if (isEmpty()) return false;
        for(Device mDevice: devicesWithDatabase){
            if(mDevice.getMac().equals(device.getMac())){
                return true;
            }
        }
        return false;
    }
    private void clickScan(Device device){
        if(!progressDialog.isShowing() && binding.progressBar.getVisibility() == View.INVISIBLE) {
            progressDialog.show();
            for (BleDevice bleDevice : bleDevices) {
                if (bleDevice.getMac().equals(device.getMac())) {
                    if (!BleManager.getInstance().isConnected(bleDevice)) {
                        BleManager.getInstance().cancelScan();
                        connect(bleDevice, Integer.parseInt(device.getIndex()));
                        return;
                    }else if (BleManager.getInstance().isConnected(bleDevice)){
                        BleManager.getInstance().disconnect(bleDevice);
                        devices.get(Integer.parseInt(device.getIndex())).setClick(false);
                        actualData(devices);
                        progressDialog.dismiss();
                        return;
                    }
                }
            }
        }
    }
    private void observeViewModel(){
        viewModel.getDevice().observe(this, device -> {
            if(viewModel.isClickScan()){
                clickScan(device);
            }else{
                for (Device mDevice: devicesWithDatabase) {
                    if(mDevice.getMac().equals(device.getMac())) {
                        startDeviceDetailsActivity(mDevice.getMac());
                        return;
                    }

                }
            }
        });
        viewModel.getDeviceMutableLiveData().observe(this, device -> {
            for (BleDevice bleDevice : bleDevices) {
                if(bleDevice.getMac().equals(device.getMac())) {
                    startDeviceDetailsActivity(bleDevice);
                    return;
                }

            }
        });
        viewModel.getData().observe(this, this::actualData);
    }
    private void startDeviceDetailsActivity(String mac){
            Intent intent = new Intent(ScanActivity.this, DeviceDetailsActivity.class);
            intent.putExtra("mac", mac);
            startActivity(intent);
    }
    private void startDeviceDetailsActivity(BleDevice bleDevice){
        if (BleManager.getInstance().isConnected(bleDevice)) {
            Intent intent = new Intent(ScanActivity.this, DeviceDetailsActivity.class);
            intent.putExtra("bleDevice", bleDevice);
            startActivity(intent);
        }
    }

    private void setDataToDatabase(Device device){
        disposable = DatabaseClient
                .getInstance(this)
                .getAppDatabase()
                .deviceDao()
                .setDevice(device)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Log.d(TAG,"complete");
                        showToast(getString(R.string.save));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, e.getMessage());
                    }
                });
    }

    private void deleteAllDatabase(){
        disposable = DatabaseClient
                .getInstance(this)
                .getAppDatabase()
                .deviceDao()
                .deleteAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Log.d("","");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("","");

                    }
                });

    }
    private boolean isEmpty(){
        return devicesWithDatabase.size() == 0;
    }

    private void getDataWithDatabase(){
        disposable = DatabaseClient
                .getInstance(this)
                .getAppDatabase()
                .deviceDao()
                .getAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableMaybeObserver<List<Device>>() {

                    @Override
                    public void onSuccess(List<Device> devices) {
                        Log.d(TAG,"complete");
                        devicesWithDatabase.clear();
                        devicesWithDatabase.addAll(devices);

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG,"complete");

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG,"complete");

                    }

                });
    }

    @Override
    public void click() {
        if(binding.progressBar.getVisibility() == View.INVISIBLE) {
            viewModel.setClickScan(true);
            devices.clear();
            actualData(devices);
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                // Device does not support Bluetooth
            } else if (!mBluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            } else {
                if (LocationPermission.isLocationPermissionGranted(this)) {
                    startScan();
                } else {
                    LocationPermission.requestLocationPermission(this);
                }
            }
        }


    }

    @Override
    public void databaseClick() {
        if(binding.progressBar.getVisibility() != View.VISIBLE){
            if(isEmpty()){
                showToast(getString(R.string.isEmpty));
            }else{
                viewModel.setClickScan(false);
                devices.clear();
                actualData(devices);
                for (Device device: devicesWithDatabase) {
                    devices.add(new ScanViewModel(device));
                }
                actualData(devices);
            }
        }
    }

    private Handler uiThread;
    public void showToast(final String text) {
        uiThread.post(() -> Toast.makeText(this, text, Toast.LENGTH_LONG).show());
    }
    private void connect(final BleDevice bleDevice, int index) {
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                Log.d(TAG,"onStartConnect");
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                Log.d(TAG,"onConnectFail");
                showToast(exception.getDescription());
                progressDialog.dismiss();
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                Log.d(TAG,"onConnectSuccess");
                devices.get(index).setClick(true);
                actualData(devices);
                progressDialog.dismiss();
                showToast("Connect with " + devices.get(index).getMac());
                if(!isDeviceInDatabase(viewModel.getDevice(index))) {
                    setDataToDatabase(viewModel.getDevice(index));
                }
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                Log.d(TAG,"onDisConnected");
                devices.get(index).setClick(false);
                progressDialog.dismiss();
                actualData(devices);


                if (isActiveDisConnected) {
                    Log.d(TAG,"isActiveDisConnected");
                } else {
                    Log.d(TAG,"!isActiveDisConnected");
                }

            }
        });
    }
    private void startScan() {
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                Log.d(TAG,"onScanStarted");
                binding.progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
//                bleDevice.
                if(devices!=null) {
                    for (ScanViewModel scanViewModel : devices) {
                        if (bleDevice.getMac().equals(scanViewModel.getMac())) return;
                    }
                }
                Device device = new Device(devices.size() + "",bleDevice.getName(),bleDevice.getRssi() + "", bleDevice.getMac(),bleDevice.getTimestampNanos()+ "");
                devices.add(new ScanViewModel(device));
                viewModel.setDevices(devices);
                actualData(devices);
                Log.d(TAG,"onLeScan");
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                Log.d(TAG,"onScanning");
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                Log.d(TAG,"onScanFinished");
                bleDevices.clear();
                bleDevices.addAll(scanResultList);
                binding.progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
    private void actualData(ArrayList<ScanViewModel> devices){
        adapter = new DeviceAdapter(devices,viewModel);
        recyclerView.setLayoutManager(new LinearLayoutManager(ScanActivity.this));
        recyclerView.setAdapter(adapter);
    }
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        if (LocationPermission.isRequestLocationPermissionGranted(requestCode, permissions, grantResults)
                ) {
            startScan();
        }
    }

}
