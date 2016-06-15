package com.projectplace.android.syncmanager;

import android.support.annotation.NonNull;

public abstract class SyncFetch extends SyncObject {

    private boolean mShouldReset;
    private int mRetries;

    /**
     * onReset should reset the state of the fetch object. All data that has already been downloaded should be set to
     * null and the sync object should be ready to start again.
     */
    public abstract void onReset();

    /**
     * Set number of tries this sync fetch should try again if it fails to sync.
     */
    public void setRetries(int retries) {
        mRetries = retries;
    }

    public int getRetries() {
        return mRetries;
    }

    @Override
    void reset() {
        super.reset();
        if (isFailed() || !isDone()) {
            throw new RuntimeException("Can not reset a fetch that is not done or failed");
        }
        mShouldReset = false;
        onReset();
    }

    /**
     * Calling this will cause the fetch to be reset and restarted.
     * As soon as an upload is added to the sync manager this will be called on all ongoing fetches.
     */
    void setShouldReset() {
        if (isStarted()) {
            mShouldReset = true;
        }
    }

    boolean shouldReset() {
        return mShouldReset;
    }

    /**
     * If two fetch objects of the same type is put in the fetch queue the same data will be fetched twice. If a fetch
     * object can take a long time to fetch this method can be overridden to make sure an identical fetch object is not
     * added to the fetch queue to same bandwidth and performance.
     *
     * @param object the sync object to check if it is identical
     * @return true if the sync object will fetch the same data
     */
    public boolean willFetchSameData(@NonNull SyncFetch object) {
        return false;
    }
}