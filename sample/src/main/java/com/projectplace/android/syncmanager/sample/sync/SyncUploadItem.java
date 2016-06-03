package com.projectplace.android.syncmanager.sample.sync;

import com.projectplace.android.syncmanager.SyncUpload;
import com.projectplace.android.syncmanager.sample.MyApplication;
import com.projectplace.android.syncmanager.sample.models.Item;

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
