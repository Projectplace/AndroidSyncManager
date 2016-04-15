package com.projectplace.syncmanager;

import android.content.Context;

public class SyncManager extends AbstractSyncManager {
    private static SyncManager sInstance;

    public static synchronized void initInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SyncManager(context);
        }
    }

    public static SyncManager getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException(SyncManager.class.getSimpleName()
                    + " is not initialized, call initializeInstance(..) method first.");
        }
        return sInstance;
    }

    protected SyncManager(Context context) {
        super(context);
    }

    @Override
    protected boolean shouldResetFetch(SyncUpload newUpload, SyncFetch fetchToReset) {
        return true;
    }

    @Override
    protected boolean shouldSyncObject(SyncObject sync) {
        return true;
    }

    @Override
    protected boolean shouldRefreshAccessToken(SyncObject sync) {
        return false;
    }

    @Override
    protected void startRefreshAccessToken(RefreshAccessTokenCallback callback) {
        callback.accessTokenRefreshed(true);
    }
}
