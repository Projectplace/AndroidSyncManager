package com.projectplace.syncmanager.sample;

import android.app.Application;

import com.projectplace.syncmanager.sample.sync.MySyncManager;
import com.projectplace.syncmanager.sample.sync.TestApiService;

public class MyApplication extends Application {

    private static TestApiService sApiService;

    @Override
    public void onCreate() {
        super.onCreate();
        initSingletons();
    }

    private void initSingletons() {
        MySyncManager.initInstance(this);
        MySharedPreferences.initInstance(this);

        // This is probably a retrofit api service in a real app
        sApiService = new TestApiService();
    }

    public static TestApiService getApiService() {
        return sApiService;
    }
}
