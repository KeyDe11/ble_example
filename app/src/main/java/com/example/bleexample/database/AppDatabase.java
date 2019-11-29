package com.example.bleexample.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.bleexample.models.Characteristic;
import com.example.bleexample.models.DataConverter;
import com.example.bleexample.models.Device;
import com.example.bleexample.models.ServiceCharacteristic;

@Database(entities = {Device.class, Characteristic.class, ServiceCharacteristic.class}, version = 3)
@TypeConverters(DataConverter.class)   // add here
//@Database(entities = {Device.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CharacteristicDao characteristicDao();
    public abstract DeviceDao deviceDao();
    public abstract ServiceCharacteristicDao serviceCharacteristicDao();
}


