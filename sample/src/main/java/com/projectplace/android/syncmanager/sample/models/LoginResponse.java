package com.projectplace.android.syncmanager.sample.models;

public class LoginResponse {
    private String mAccessToken;
    private String mRefreshToken;
    private long mExpiresInSeconds;

    public void setAccessToken(String accessToken) {
        mAccessToken = accessToken;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public void setRefreshToken(String refreshToken) {
        mRefreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return mRefreshToken;
    }

    public void setExpiresInSeconds(long expiresIn) {
        mExpiresInSeconds = expiresIn;
    }

    public long getExpiresInSeconds() {
        return mExpiresInSeconds;
    }
}
