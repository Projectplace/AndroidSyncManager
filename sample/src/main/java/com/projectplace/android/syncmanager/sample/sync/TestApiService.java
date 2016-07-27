package com.projectplace.android.syncmanager.sample.sync;

import android.os.Handler;
import android.os.Looper;

import com.projectplace.android.syncmanager.sample.models.Item;
import com.projectplace.android.syncmanager.sample.models.LoginResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.client.Header;
import retrofit.client.Response;

public class TestApiService {
    private Response mOkResponse = new Response("", 200, "", new ArrayList<Header>(), null);
    private List<Item> mTestItems = new ArrayList<>();

    void getAccessToken(final String grantType, String clientId, String clientSecret, String refreshToken, String username, String password, final Callback<LoginResponse> cb) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Call oauth service in real app, here just return some test tokens
                final LoginResponse loginResponse = new LoginResponse();
                loginResponse.setAccessToken("abcdefghijklmno");
                loginResponse.setRefreshToken("jkndgjsdnfkrios");
                loginResponse.setExpiresInSeconds(TimeUnit.HOURS.toSeconds(5));

                // Simulate retrofit and post back on main thread
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        cb.success(loginResponse, mOkResponse);
                    }
                });
            }
        }).start();
    }

    void getItems(String accessToken, final Callback<List<Item>> cb) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Call server to get items here

                // Simulate retrofit and post back on main thread
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        cb.success(mTestItems, mOkResponse);
                    }
                });
            }
        }).start();
    }

    void addItem(final String itemName, final Callback<Item> cb) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Call server to add items here

                // Simulate retrofit and post back on main thread
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Item item = new Item(itemName);
                        mTestItems.add(item);
                        cb.success(item, mOkResponse);
                    }
                });
            }
        }).start();
    }
}
