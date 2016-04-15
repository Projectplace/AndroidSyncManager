package com.projectplace.syncmanager.sample.sync;

import com.projectplace.syncmanager.sample.models.Item;
import com.projectplace.syncmanager.sample.models.LoginResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.client.Header;
import retrofit.client.Response;

public class TestApiService {
    private Response mOkResponse = new Response("", 200, "", new ArrayList<Header>(), null);
    private List<Item> mTestItems = new ArrayList<>();

    void getAccessToken(String grantType, String clientId, String clientSecret, String refreshToken, String username, String password, Callback<LoginResponse> cb) {
        // Call oauth service in real app, here just return some test tokens
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccessToken("abcdefghijklmno");
        loginResponse.setRefreshToken("jkndgjsdnfkrios");
        loginResponse.setExpiresInSeconds(TimeUnit.HOURS.toSeconds(5));

        cb.success(loginResponse, mOkResponse);
    }

    void getItems(String accessToken, Callback<List<Item>> cb) {
        // Return the test items
        cb.success(mTestItems, mOkResponse);
    }

    void addItem(String itemName, Callback<Item> cb) {
        Item item = new Item(itemName);
        mTestItems.add(item);
        cb.success(item, mOkResponse);
    }
}
