package com.example.bleexample.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bleexample.R;
import com.example.bleexample.callbacks.ClickDeviceCallback;
import com.example.bleexample.databinding.AdapterRow;
import com.example.bleexample.models.Device;
import com.example.bleexample.viewmodels.ScanViewModel;
import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceView> implements ClickDeviceCallback {
    private List<ScanViewModel> arrayList;
    private LayoutInflater layoutInflater;
    private ScanViewModel viewModel;

    public DeviceAdapter(List<ScanViewModel> channels, ScanViewModel viewModel) {
        this.arrayList = channels;
        this.viewModel = viewModel;
    }

    @Override
    public DeviceView onCreateViewHolder(final ViewGroup viewGroup, int i) {


        if(layoutInflater == null)
        {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        AdapterRow adapterRow = DataBindingUtil.inflate(layoutInflater, R.layout.row_scan_adapter,viewGroup,false);

        adapterRow.setHandler(this);
        return new DeviceView(adapterRow);
    }

    @Override
    public void onBindViewHolder(DeviceView viewHolder, int position) {

        ScanViewModel scanViewModel = arrayList.get(position);
        viewHolder.bind(scanViewModel);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public void click(ScanViewModel scanViewModel) {
        viewModel.getDevice().setValue(new Device(scanViewModel.getIndex(),scanViewModel.getName(),scanViewModel.getRssi(), scanViewModel.getMac(),scanViewModel.getTimestampNanos()));
    }

    @Override
    public void clickInfo(ScanViewModel scanViewModel) {
        viewModel.getDeviceMutableLiveData().setValue(new Device(scanViewModel.getIndex(),scanViewModel.getName(),scanViewModel.getRssi(), scanViewModel.getMac(),scanViewModel.getTimestampNanos()));
      }


    public class DeviceView extends RecyclerView.ViewHolder {

        private AdapterRow adapterRow;
        public DeviceView(AdapterRow adapterRow) {
            super(adapterRow.getRoot());

            this.adapterRow = adapterRow;

        }

        public void bind(ScanViewModel scanViewModel)
        {
            this.adapterRow.setViewModel(scanViewModel);
            adapterRow.executePendingBindings();
        }

        public AdapterRow getAdapterRow()
        {
            return adapterRow;
        }

    }
}
