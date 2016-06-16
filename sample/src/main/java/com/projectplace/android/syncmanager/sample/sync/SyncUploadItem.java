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
    public void prepare() {
        super.prepare();
        // Here you could save the item to your database directly before doing the api call instead of in onSave()
        // so the UI is uploaded directly.
    }

    @Override
    public void revert() {
        super.revert();
        // If prepare is used and the api call fails this will be called so you can revert the change
        // and the UI is reverted back to how i was before prepare.
    }

    @Override
    public void onSave() {
        super.onSave();
        // This is called after uploadSuccessful() has been called. Now you can save the item to the database.
        // Only use this method if prepare is not used.
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
