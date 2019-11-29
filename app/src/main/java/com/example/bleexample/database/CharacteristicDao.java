package com.example.bleexample.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.bleexample.models.Characteristic;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;

@Dao
public interface CharacteristicDao {
    @Query("SELECT * FROM characteristic")
    Maybe<List<Characteristic>> getAll();
    //
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable setCharacteristic(Characteristic characteristic);

    @Query("DELETE FROM characteristic")
    Completable deleteAll();
}
