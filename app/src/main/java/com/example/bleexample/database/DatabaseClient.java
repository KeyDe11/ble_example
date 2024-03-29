package com.example.bleexample.database;

import android.content.Context;

import androidx.room.Room;

public class DatabaseClient {

    private Context context;
    private static DatabaseClient mInstance;

    //our app database object
    private AppDatabase appDatabase;

    private DatabaseClient(Context context) {
        this.context = context;
        appDatabase = Room.databaseBuilder(context, AppDatabase.class, "DatabaseBle")
                .allowMainThreadQueries()
                // recreate the database if necessary
                .fallbackToDestructiveMigration()
                .build();
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseClient(context);
        }
        return mInstance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
