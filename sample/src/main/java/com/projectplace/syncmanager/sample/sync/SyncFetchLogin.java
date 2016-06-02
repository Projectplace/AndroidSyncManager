package com.projectplace.syncmanager.sample.sync;

import com.projectplace.syncmanager.SyncFetchSimple;
import com.projectplace.syncmanager.sample.MyApplication;
import com.projectplace.syncmanager.sample.MySharedPreferences;
import com.projectplace.syncmanager.sample.models.LoginResponse;

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
