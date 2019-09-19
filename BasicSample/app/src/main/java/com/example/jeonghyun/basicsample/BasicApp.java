package com.example.jeonghyun.basicsample;

import android.app.Application;

import com.example.jeonghyun.basicsample.db.AppDatabase;

public class BasicApp extends Application {

    private AppExecutors mAppExecutors;

    @Override
    public void onCreate() {
        super.onCreate();

        mAppExecutors = new AppExecutors();
    }

    public AppDatabase getDatabase(){
        return AppDatabase.getInstance(this, mAppExecutors);
    }

    public DataRepository getRepository(){
        return DataRepository.getInstance(getDatabase());
    }
}
