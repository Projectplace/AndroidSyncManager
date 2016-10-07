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

import com.projectplace.android.syncmanager.SyncFetchSimple;
import com.projectplace.android.syncmanager.sample.MyApplication;
import com.projectplace.android.syncmanager.sample.MySharedPreferences;
import com.projectplace.android.syncmanager.sample.models.LoginResponse;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SyncFetchLogin extends SyncFetchSimple<LoginResponse> {

    public SyncFetchLogin() {
        super();
        setNeedsAccessToken(false);
    }

    @Override
    public void onSave() {
        MySharedPreferences.getInstance().setTokens(getData().getAccessToken(), getData().getRefreshToken(), getData().getExpiresInSeconds());
    }

    @Override
    public void onStart() {
        MyApplication.getApiService().getAccessToken("password", "clientId", "clientSecret", null, "testUsername", "testPassword", new Callback<LoginResponse>() {
            @Override
            public void success(LoginResponse loginResponse, Response response) {
                setData(loginResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                setError(error);
            }
        });
    }
}
