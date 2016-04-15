package com.projectplace.syncmanager.sample.sync;

import com.projectplace.syncmanager.SyncUpload;
import com.projectplace.syncmanager.sample.MyApplication;
import com.projectplace.syncmanager.sample.models.Item;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SyncUploadItem extends SyncUpload {

    private String mItemName;
    private Item mItem;

    public SyncUploadItem(String itemName) {
        mItemName = itemName;
    }

    @Override
    public void onStart() {
        MyApplication.getApiService().addItem(mItemName, new Callback<Item>() {
            @Override
            public void success(Item item, Response response) {
                mItem = item;
                uploadSuccessful();
            }

            @Override
            public void failure(RetrofitError error) {
                setError(error);
            }
        });
    }

    public Item getItem() {
        return mItem;
    }
}
