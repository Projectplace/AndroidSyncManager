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
