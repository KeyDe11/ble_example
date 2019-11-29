package com.example.bleexample.adapters;

import android.app.AlertDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bleexample.R;
import com.example.bleexample.callbacks.ClickServiceCallback;
import com.example.bleexample.databinding.AdapterServiceRow;
import com.example.bleexample.models.GattCharacteristicDetails;
import com.example.bleexample.viewmodels.DeviceDetailsViewModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceView> implements ClickServiceCallback {
    private List<DeviceDetailsViewModel> arrayList;
    private LayoutInflater layoutInflater;
    private DeviceDetailsViewModel viewModel;
    private Context context;
    private boolean clickDialog = false;

    public ServiceAdapter(Context context, List<DeviceDetailsViewModel> channels, DeviceDetailsViewModel viewModel) {
        this.arrayList = channels;
        this.viewModel = viewModel;
        this.context = context;
    }

    @Override
    public ServiceView onCreateViewHolder(final ViewGroup viewGroup, int i) {


        if(layoutInflater == null)
        {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        AdapterServiceRow adapterRow = DataBindingUtil.inflate(layoutInflater, R.layout.row_service_adapter,viewGroup,false);

        adapterRow.setHandler(this);
        return new ServiceView(adapterRow);
    }

    @Override
    public void onBindViewHolder(ServiceView viewHolder, int position) {

        DeviceDetailsViewModel deviceDetailsViewModel = arrayList.get(position);
        viewHolder.bind(deviceDetailsViewModel);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    private List<String> getArrayString(int charaProp){
        List<String> propNameList = new ArrayList<>();
        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            propNameList.add("Read");
        }
        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
            propNameList.add("Write");
        }
        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
            propNameList.add("Write No Response");
        }
        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            propNameList.add("Notify");
        }
        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
            propNameList.add("Indicate");
        }
        return propNameList;
    }
    @Override
    public void click(DeviceDetailsViewModel deviceDetailsViewModel) {
        if(clickDialog) return;
        clickDialog = true;
        List<GattCharacteristicDetails> gattCharacteristicDetails = deviceDetailsViewModel.getGattCharacteristicDetails();
        CharSequence items[] = new CharSequence[gattCharacteristicDetails.size()];
        for (int i = 0; i < gattCharacteristicDetails.size(); i++) {
            items[i] = (gattCharacteristicDetails.get(i).getUuid() + "\n") + getArrayString(gattCharacteristicDetails.get(i).getCharaProp());
        }
        ContextThemeWrapper cw = new ContextThemeWrapper( context, R.style.AlertDialogTheme );
        new AlertDialog.Builder(cw)
                .setTitle("Select characteristic uuid")
                .setSingleChoiceItems(items, 0, (dialogInterface, selectedIndex) -> {
                    List<String> list = new LinkedList<>();
                    list.add(deviceDetailsViewModel.getUuid());
                    list.add(gattCharacteristicDetails.get(selectedIndex).getUuid() + "");
                    viewModel.getListUuid().setValue(list);
                    clickDialog =false;
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> clickDialog =false)
                .setCancelable(false)
                .show();
//        builder.setItems(items, (dialog, which) -> {
//
//
//        });
//        builder.show();
    }


    public class ServiceView extends RecyclerView.ViewHolder {

        private AdapterServiceRow adapterRow;
        public ServiceView(AdapterServiceRow adapterRow) {
            super(adapterRow.getRoot());

            this.adapterRow = adapterRow;

        }

        public void bind(DeviceDetailsViewModel deviceDetailsViewModel)
        {
            this.adapterRow.setViewModel(deviceDetailsViewModel);
            adapterRow.executePendingBindings();
        }

        public AdapterServiceRow getAdapterRow()
        {
            return adapterRow;
        }

    }
}
