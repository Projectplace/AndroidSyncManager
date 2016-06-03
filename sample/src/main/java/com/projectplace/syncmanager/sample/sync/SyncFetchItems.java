package com.projectplace.syncmanager.sample.sync;

import com.projectplace.syncmanager.SyncFetch;
import com.projectplace.syncmanager.sample.MyApplication;
import com.projectplace.syncmanager.sample.MySharedPreferences;
import com.projectplace.syncmanager.sample.models.Item;

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
        // Save to database for example
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
