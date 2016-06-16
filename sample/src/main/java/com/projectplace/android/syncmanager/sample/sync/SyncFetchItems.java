package com.projectplace.android.syncmanager.sample.sync;

import com.projectplace.android.syncmanager.SyncFetch;
import com.projectplace.android.syncmanager.sample.MyApplication;
import com.projectplace.android.syncmanager.sample.MySharedPreferences;
import com.projectplace.android.syncmanager.sample.models.Item;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SyncFetchItems extends SyncFetch {

    private List<Item> mItems;

    @Override
    public void onReset() {
        mItems = null;
    }

    @Override
    public boolean isDone() {
        return mItems != null;
    }

    @Override
    public void onSave() {
        // Here you should save the items to the database and that should trigger a loader to update the UI
    }

    @Override
    public void onStart() {
        MyApplication.getApiService().getItems(MySharedPreferences.getInstance().getAccessToken(), new Callback<List<Item>>() {
            @Override
            public void success(List<Item> items, Response response) {
                mItems = items;
                checkIfDone();
            }

            @Override
            public void failure(RetrofitError error) {
                setErrorAndMessage(error, error.getMessage());
            }
        });
    }

    public List<Item> getItems() {
        return mItems != null ? mItems : new ArrayList<Item>();
    }
}
