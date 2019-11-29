package com.example.bleexample.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.bleexample.models.Characteristic;
import com.example.bleexample.models.ServiceCharacteristic;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;

@Dao
public interface ServiceCharacteristicDao {
    @Query("SELECT * FROM serviceCharacteristic")
    Maybe<List<ServiceCharacteristic>> getAll();
    //
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable setServiceCharacteristic(ServiceCharacteristic serviceCharacteristic);

    @Query("DELETE FROM serviceCharacteristic")
    Completable deleteAll();
}
