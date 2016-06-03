package com.projectplace.android.syncmanager.sample;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.joda.time.DateTime;

public class MySharedPreferences {
    private static final String PREFS_ACCESS_TOKEN = "accessToken";
    private static final String PREFS_ACCESS_TOKEN_EXPIRY = "accessTokenExpiry";
    private static final String PREFS_REFRESH_TOKEN = "refreshToken";

    private final SharedPreferences mSharedPreferences;
    private static MySharedPreferences sInstance;

    public static synchronized void initInstance(Context context) {
        if (sInstance == null) {
            sInstance = new MySharedPreferences(context.getApplicationContext());
        }
    }

    public static MySharedPreferences getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException(MySharedPreferences.class.getSimpleName()
                    + " is not initialized, call initializeInstance(..) method first.");
        }
        return sInstance;
    }

    private MySharedPreferences(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getAccessToken() {
        return mSharedPreferences.getString(PREFS_ACCESS_TOKEN, null);
    }

    public String getRefreshToken() {
        return mSharedPreferences.getString(PREFS_REFRESH_TOKEN, null);
    }

    public long getAccessTokenExpiresIn() {
        if (!mSharedPreferences.contains(PREFS_ACCESS_TOKEN_EXPIRY)) {
            return 0;
        }
        return mSharedPreferences.getLong(PREFS_ACCESS_TOKEN_EXPIRY, 0) - System.currentTimeMillis();
    }

    public void setTokens(String accessToken, String refreshToken, long expiresInSeconds) {
        DateTime expiryDate = new DateTime().plusSeconds((int) expiresInSeconds);

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PREFS_ACCESS_TOKEN, accessToken);
        editor.putLong(PREFS_ACCESS_TOKEN_EXPIRY, expiryDate.getMillis());
        editor.putString(PREFS_REFRESH_TOKEN, refreshToken);
        editor.apply();
    }

    public void clearTokens() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove(PREFS_ACCESS_TOKEN);
        editor.remove(PREFS_ACCESS_TOKEN_EXPIRY);
        editor.remove(PREFS_REFRESH_TOKEN);
        editor.apply();
    }
}
