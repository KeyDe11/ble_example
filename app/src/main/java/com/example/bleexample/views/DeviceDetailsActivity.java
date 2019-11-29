package com.example.bleexample.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;
import com.example.bleexample.R;
import com.example.bleexample.adapters.DeviceAdapter;
import com.example.bleexample.adapters.ServiceAdapter;
import com.example.bleexample.comm.Observer;
import com.example.bleexample.comm.ObserverManager;
import com.example.bleexample.database.DatabaseClient;
import com.example.bleexample.databinding.BindingDeviceDetailsActivity;
import com.example.bleexample.models.ServiceCharacteristic;
import com.example.bleexample.viewmodels.DeviceDetailsViewModel;
import com.example.bleexample.viewmodels.ScanViewModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;

public class DeviceDetailsActivity extends AppCompatActivity implements Observer {

    public static final String DETAILS_KEY = "bleDevice";
    private BleDevice bleDevice;
    private BindingDeviceDetailsActivity binding;
    private BluetoothGattService bluetoothGattService;
    private BluetoothGattCharacteristic characteristic;
    private DeviceDetailsViewModel viewModel;
    private RecyclerView recyclerView;
    private ServiceAdapter adapter;
    private Disposable disposable;
    private ArrayList<DeviceDetailsViewModel> detailsViewModels;
    private List<ServiceCharacteristic> serviceCharacteristicsList;
    private String mac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_device_details);
        serviceCharacteristicsList = new LinkedList<>();
        uiThread = new Handler();
        detailsViewModels = new ArrayList<>();
        getDevice();
        getDataWithDatabase();
        if (!(mac != null && !mac.equals(""))) {
            ObserverManager.getInstance().addObserver(this);
            if (bleDevice.getName() != null) {
                binding.tvTitle.setText(bleDevice.getName());
            } else {
                binding.tvTitle.setText(bleDevice.getMac());
            }
            init();
            if (!BleManager.getInstance().isConnected(bleDevice)) {
                finish();
            }
        }

    }

    private void getServiceCharacteristic() {
        boolean isSet = false;
        for (ServiceCharacteristic serviceCharacteristic : serviceCharacteristicsList) {
            if (serviceCharacteristic.getMac().equals(mac)) {
                detailsViewModels.add(new DeviceDetailsViewModel(serviceCharacteristic));
                if(!isSet) {
                    if (serviceCharacteristic.getName() != null) {
                        binding.tvTitle.setText(serviceCharacteristic.getName());
                    } else {
                        binding.tvTitle.setText(serviceCharacteristic.getMac());
                    }
                    isSet = true;
                }
            }
        }

    }

    private boolean isEmpty() {
        return serviceCharacteristicsList.size() == 0;
    }

    private Handler uiThread;

    public void showToast(final String text) {
        uiThread.post(() -> Toast.makeText(this, text, Toast.LENGTH_LONG).show());
    }

    private void setDataToDatabase(ServiceCharacteristic serviceCharacteristic) {
        disposable = DatabaseClient
                .getInstance(this)
                .getAppDatabase()
                .serviceCharacteristicDao()
                .setServiceCharacteristic(serviceCharacteristic)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Log.d("", "complete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("", e.getMessage());
                    }
                });
    }

    private void getDevice() {
        Object object = getIntent().getParcelableExtra(DETAILS_KEY);
        if (object instanceof BleDevice) {
            bleDevice = (BleDevice) object;
        } else {
            String mac = getIntent().getStringExtra("mac");
            if (mac != null && !mac.equals("")) {
                this.mac = mac;
            }
        }
    }

    private void init() {
        viewModel = ViewModelProviders.of(this).get(DeviceDetailsViewModel.class);
        binding.setViewModel(viewModel);
        binding.progressBar.setVisibility(View.GONE);
        recyclerView = binding.recyclerView;
        adapter = new ServiceAdapter(this, detailsViewModels, viewModel);
        viewModel.getListUuid().observe(this, list -> {
            if (mac != null && !mac.equals("")) {
                startDeviceDetailsActivity(list,mac);
            } else {
                startDeviceDetailsActivity(list);
            }
        });
        actualData(detailsViewModels);
    }

    private void startDeviceDetailsActivity(List<String> list) {
        if (BleManager.getInstance().isConnected(bleDevice)) {
            Intent intent = new Intent(DeviceDetailsActivity.this, CharacteristicActivity.class);
            intent.putExtra("service_uuid", list.get(0));
            intent.putExtra("characteristic_uuid", list.get(1));
            intent.putExtra("bleDevice", bleDevice);
            startActivity(intent);
        }
    }

    private void startDeviceDetailsActivity(List<String> list, String mac) {
        Intent intent = new Intent(DeviceDetailsActivity.this, CharacteristicActivity.class);
        intent.putExtra("mac", mac);
        intent.putExtra("service_uuid", list.get(0));
        intent.putExtra("characteristic_uuid", list.get(1));
        startActivity(intent);
    }

    private boolean isService(ServiceCharacteristic serviceCharacteristic) {
        if (isEmpty()) return false;
        for (ServiceCharacteristic mServiceCharacteristic : serviceCharacteristicsList) {
            if (mServiceCharacteristic.getUuid().equals(serviceCharacteristic.getUuid())) {
                return true;
            }
        }
        return false;
    }

    private void setDatabaseData() {

        getServiceCharacteristic();
        init();
    }

    private void getDataWithDatabase() {
        disposable = DatabaseClient
                .getInstance(this)
                .getAppDatabase()
                .serviceCharacteristicDao()
                .getAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableMaybeObserver<List<ServiceCharacteristic>>() {

                    @Override
                    public void onSuccess(List<ServiceCharacteristic> serviceCharacteristics) {
                        serviceCharacteristicsList.clear();
                        serviceCharacteristicsList.addAll(serviceCharacteristics);
                        if (mac != null && !mac.equals("")) {
                            setDatabaseData();
                        } else {

                            getService();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }

                });
    }

    private void getService() {
        int i = 0;
        BluetoothGatt gatt = BleManager.getInstance().getBluetoothGatt(bleDevice);
        for (BluetoothGattService service : gatt.getServices()) {
            ServiceCharacteristic serviceCharacteristic = new ServiceCharacteristic(i, service.getUuid() + "", service.getCharacteristics());
            if (!isService(serviceCharacteristic)) {
                serviceCharacteristic.setMac(bleDevice.getMac());
                serviceCharacteristic.setName(bleDevice.getName() + "");
                setDataToDatabase(serviceCharacteristic);
            }
            detailsViewModels.add(new DeviceDetailsViewModel(serviceCharacteristic));
            i++;
        }
        actualData(detailsViewModels);
    }

    private void actualData(ArrayList<DeviceDetailsViewModel> deviceDetailsViewModels) {
        adapter = new ServiceAdapter(this, deviceDetailsViewModels, viewModel);
        recyclerView.setLayoutManager(new LinearLayoutManager(DeviceDetailsActivity.this));

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void disConnected(BleDevice bleDevice) {
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}
