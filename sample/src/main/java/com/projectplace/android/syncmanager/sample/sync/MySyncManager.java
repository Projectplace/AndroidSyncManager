package com.projectplace.android.syncmanager.sample.sync;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.projectplace.android.syncmanager.SyncManager;
import com.projectplace.android.syncmanager.SyncObject;
import com.projectplace.android.syncmanager.sample.MyApplication;
import com.projectplace.android.syncmanager.sample.MySharedPreferences;
import com.projectplace.android.syncmanager.sample.R;
import com.projectplace.android.syncmanager.sample.models.LoginResponse;

import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MySyncManager extends SyncManager {

    private static MySyncManager sInstance;

    public static synchronized void initInstance(Context context) {
        if (sInstance == null) {
            sInstance = new MySyncManager(context);
        }
    }

    public static MySyncManager getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException(MySyncManager.class.getSimpleName()
                    + " is not initialized, call initializeInstance(..) method first.");
        }
        return sInstance;
    }

    private MySyncManager(Context context) {
        super(context);
    }

    @Override
    protected boolean shouldSyncObject(@NonNull SyncObject sync) {
        // Here you can check for specific conditions or if a specific sync object is allowed to sync.
        // If the access tokens has been cleared no sync objects should be allowed to sync for example unless
        // it is a sync object which doesn't need an access token for example.
        if (sync.needsAccessToken() && MySharedPreferences.getInstance().getAccessToken() == null) {
            stopSync();
            // Could also trigger a log out of the app here as if you
            // have no tokens you will probably need to log in again.

            // Need to post toast on main looper as this is called on background thread
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mApplicationContext, "No access token", Toast.LENGTH_SHORT).show();
                }
            });
            return false;
        }
        return true;
    }

    @Override
    protected boolean shouldRefreshAccessToken() {
        // Check if the access token has expired
        return MySharedPreferences.getInstance().getAccessTokenExpiresIn() < TimeUnit.HOURS.toSeconds(2);
    }

    @Override
    protected void startRefreshAccessToken(@NonNull final RefreshAccessTokenCallback callback) {
        // If the access token needs refreshing this method will be called, so refresh it here
        MyApplication.getApiService().getAccessToken("refresh_token", "clientId", "clientSecret",
                MySharedPreferences.getInstance().getRefreshToken(), null, null, new Callback<LoginResponse>() {
                    @Override
                    public void success(LoginResponse loginResponse, Response response) {
                        MySharedPreferences.getInstance().setTokens(loginResponse.getAccessToken(), loginResponse.getRefreshToken(), loginResponse.getExpiresInSeconds());
                        callback.refreshAccessTokenSuccess();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        callback.refreshAccessTokenFailed(error);
                    }
                });
    }

    @Override
    protected void showError(@NonNull SyncObject syncObject) {
        String errorMessage = syncObject.getErrorMessage();
        // If no internet override the error message
        if (!isNetworkAvailable(mApplicationContext)) {
            errorMessage = mApplicationContext.getString(R.string.error_no_internet_connection);
        }
        if (errorMessage != null) {
            Toast.makeText(mApplicationContext, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
