package com.example.bleexample.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.bleexample.models.Device;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

@Dao
public interface DeviceDao {
    @Query("SELECT * FROM Device")
    Maybe<List<Device>> getAll();
//
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable setDevice(Device device);

    @Query("DELETE FROM Device")
    Completable deleteAll();
}
