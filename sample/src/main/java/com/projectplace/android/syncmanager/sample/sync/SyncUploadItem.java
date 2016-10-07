/**
 * Copyright (C) 2016 Planview, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
