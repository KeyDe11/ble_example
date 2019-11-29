package com.example.bleexample.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bleexample.R;
import com.example.bleexample.databinding.AdapterCharacterRow;
import com.example.bleexample.viewmodels.CharacteristicViewModel;

import java.util.List;

public class CharacteristicAdapter extends RecyclerView.Adapter<CharacteristicAdapter.CharacteristicView>{
    private List<CharacteristicViewModel> arrayList;
    private LayoutInflater layoutInflater;
    private CharacteristicViewModel viewModel;

    public CharacteristicAdapter(List<CharacteristicViewModel> channels, CharacteristicViewModel viewModel) {
        this.arrayList = channels;
        this.viewModel = viewModel;
    }

    @Override
    public CharacteristicView onCreateViewHolder(final ViewGroup viewGroup, int i) {


        if(layoutInflater == null)
        {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        AdapterCharacterRow adapterRow = DataBindingUtil.inflate(layoutInflater, R.layout.row_character_adapter,viewGroup,false);

        return new CharacteristicView(adapterRow);
    }

    @Override
    public void onBindViewHolder(CharacteristicView viewHolder, int position) {

        CharacteristicViewModel characteristicViewModel = arrayList.get(position);
        viewHolder.bind(characteristicViewModel);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class CharacteristicView extends RecyclerView.ViewHolder {

        private AdapterCharacterRow adapterRow;
        public CharacteristicView(AdapterCharacterRow adapterRow) {
            super(adapterRow.getRoot());

            this.adapterRow = adapterRow;

        }

        public void bind(CharacteristicViewModel characteristicViewModel)
        {
            this.adapterRow.setViewModel(characteristicViewModel);
            adapterRow.executePendingBindings();
        }

        public AdapterCharacterRow getAdapterRow()
        {
            return adapterRow;
        }

    }
}
