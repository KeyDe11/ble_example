package com.example.bleexample.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.example.bleexample.R;
import com.example.bleexample.adapters.CharacteristicAdapter;
import com.example.bleexample.comm.Observer;
import com.example.bleexample.comm.ObserverManager;
import com.example.bleexample.database.DatabaseClient;
import com.example.bleexample.databinding.CharacteristicBinding;
import com.example.bleexample.models.Characteristic;
import com.example.bleexample.models.ServiceCharacteristic;
import com.example.bleexample.viewmodels.CharacteristicViewModel;
import com.example.bleexample.viewmodels.ScanViewModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;

public class CharacteristicActivity extends AppCompatActivity implements Observer {
    private CharacteristicBinding binding;
    private BleDevice bleDevice;
    private String serviceUuid;
    private String characteristicUuid;
    private Disposable disposable;
    private List<Characteristic> characteristicsList;
    private String mac;
    private CharacteristicAdapter adapter;
    private RecyclerView recyclerView;
    private CharacteristicViewModel viewModel;
    private ArrayList<CharacteristicViewModel> viewModelArrayList;
    private static final String TAG = "CharacteristicActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_characteristic);
        recyclerView = binding.recyclerView;

        viewModel = ViewModelProviders.of(this).get(CharacteristicViewModel.class);
        characteristicsList = new LinkedList<>();
        viewModelArrayList = new ArrayList<>();
        adapter = new CharacteristicAdapter(viewModelArrayList, viewModel);
        uiThread = new Handler();
        getDataWithDatabase();
        getDevice();
        if (!(mac != null && !mac.equals(""))) {
            getUuid();
            ObserverManager.getInstance().addObserver(this);
        }
    }

    private void getServiceCharacteristic() {
        for (Characteristic characteristic : characteristicsList) {
            if (characteristic.getMac().equals(mac)) {
                if (characteristic.getServiceUuid().equals(serviceUuid)) {
                    if (characteristic.getCharacteristicUuid().equals(characteristicUuid)) {
                        viewModelArrayList.add(new CharacteristicViewModel(characteristic));
                    }
                }
            }
        }
    }

    private void getDevice() {
        Object object = getIntent().getParcelableExtra("bleDevice");
        if (object instanceof BleDevice) {
            bleDevice = (BleDevice) object;
            serviceUuid = getIntent().getStringExtra("service_uuid");
            characteristicUuid = getIntent().getStringExtra("characteristic_uuid");
        } else {
            String mac = getIntent().getStringExtra("mac");
            if (mac != null && !mac.equals("")) {
                this.mac = mac;
                serviceUuid = getIntent().getStringExtra("service_uuid");
                characteristicUuid = getIntent().getStringExtra("characteristic_uuid");
            }
        }
    }
    private boolean isEmpty(){
        return viewModelArrayList.size() == 0;
    }
    private long getTimestamp(){
        return System.currentTimeMillis();
    }
    private void getUuid() {
        if (!BleManager.getInstance().isConnected(bleDevice)) {
            finish();
        }
        BleManager.getInstance().read(
                bleDevice,
                serviceUuid,
                characteristicUuid,
                new BleReadCallback() {
                    @Override
                    public void onReadSuccess(final byte[] data) {
                        runOnUiThread(() -> {
                            Characteristic characteristic = new Characteristic(data, serviceUuid, characteristicUuid, getTimestamp());
                            characteristic.setMac(bleDevice.getMac());
                            setDataToDatabase(characteristic);
                            String newString = new String(data);
                            Log.d(TAG, newString);

                            viewModelArrayList.add(new CharacteristicViewModel(characteristic));
                            actualData(viewModelArrayList);
                        });
                    }

                    @Override
                    public void onReadFailure(final BleException exception) {
                        runOnUiThread(() -> Log.d(TAG, exception.getDescription()));
                    }
                });
    }

    private void actualData(ArrayList<CharacteristicViewModel> characteristicViewModels){
        adapter = new CharacteristicAdapter(characteristicViewModels,viewModel);
        recyclerView.setLayoutManager(new LinearLayoutManager(CharacteristicActivity.this));
        recyclerView.setAdapter(adapter);
    }
    private void getDataWithDatabase() {
        disposable = DatabaseClient
                .getInstance(this)
                .getAppDatabase()
                .characteristicDao()
                .getAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableMaybeObserver<List<Characteristic>>() {

                    @Override
                    public void onSuccess(List<Characteristic> characteristics) {
                        characteristicsList.clear();
                        characteristicsList.addAll(characteristics);

                        if (mac != null && !mac.equals("")) {
                            if (characteristics.isEmpty()) {
                                showToast(getString(R.string.isEmpty));
                                return;
                            }
                            getServiceCharacteristic();
                            if(isEmpty()){
                                showToast(getString(R.string.isEmpty));
                            }
                            actualData(viewModelArrayList);
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

    private Handler uiThread;

    public void showToast(final String text) {
        uiThread.post(() -> Toast.makeText(this, text, Toast.LENGTH_LONG).show());
    }

    private void setDataToDatabase(Characteristic characteristic) {
        disposable = DatabaseClient
                .getInstance(this)
                .getAppDatabase()
                .characteristicDao()
                .setCharacteristic(characteristic)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Log.d("", "complete");
                        showToast(getString(R.string.save));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("", e.getMessage());
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    @Override
    public void disConnected(BleDevice bleDevice) {
        finish();
    }
}
