/*
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

package com.projectplace.android.syncmanager;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

/**
 * Top class of both fetch and upload sync objects.
 */
public abstract class SyncObject {
    public interface SyncListener {
        void onFetchDone(@NonNull SyncFetch syncFetch);

        void onUploadDone(@NonNull SyncUpload syncUpload);

        void onSyncAborted(@NonNull SyncObject syncObject);
    }

    public static class SyncListenerAdapter implements SyncListener {
        @Override
        public void onFetchDone(@NonNull SyncFetch syncFetch) {
        }

        @Override
        public void onUploadDone(@NonNull SyncUpload syncUpload) {
        }

        @Override
        public void onSyncAborted(@NonNull SyncObject syncObject) {
        }
    }

    private SyncListener mManagerSyncListener;
    private SyncListener mSyncListener;
    private Object mError;
    private String mErrorMessage;
    private boolean mFailed;
    private boolean mStarted;
    private boolean mListenerCalled;
    private boolean mNeedsAccessToken = true;
    private boolean mIsBackgroundSync = true;

    /**
     * Called when the sync object should save it synced data. This will always be called on a background thread
     * so it is safe to call a database here.
     */
    public abstract void onSave();

    /**
     * Called when this sync object should start. The sync that is started should always be started on a
     * new thread to not block other sync objects to run in parallel as this is called from the sync loop thread.
     */
    public abstract void onStart();

    /**
     * Should return true when the sync object is done with all syncing. After {@link #checkIfDone()} has been called this
     * will be called to see if the sync object is done with its syncing.
     */
    public abstract boolean isDone();

    void setManagerSyncListener(@NonNull SyncListener listener) {
        mManagerSyncListener = listener;
    }

    void reset() {
        mError = null;
        mErrorMessage = null;
        mFailed = false;
        mStarted = false;
        mListenerCalled = false;
    }

    void start() {
        mStarted = true;
        onStart();
    }

    private void setFailed(boolean failed) {
        mFailed = failed;
        checkIfDone();
    }

    /**
     * Same as {@link #setError(Object)} but with a provided error message that will be shown as a toast.
     *
     * @param error        An object of any kind that describes the error.
     * @param errorMessage A message to be shown as a toast.
     */
    public void setErrorAndMessage(Object error, String errorMessage) {
        mErrorMessage = errorMessage;
        setError(error);
    }

    /**
     * If a sync fails an error object should be set which can later be retrieved with {@link #getError()}
     * from a sync listener callback.
     *
     * @param error An object of any kind that describes the error.
     */
    public void setError(Object error) {
        mError = error;
        setFailed(true);
    }

    /**
     * When an api call to the server is done this method should be called to see if the fetch object is done.
     * One fetch object can consist of several api calls and the fetch object is not done until all of them are complete.
     */
    protected void checkIfDone() {
        if ((isFailed() || isDone()) && !mListenerCalled) {
            mListenerCalled = true;
            // If test listener is set then don't post to main thread as that will cause deadlock in tests
            if (Looper.myLooper() == Looper.getMainLooper() || SyncManager.getTestListener() != null) {
                syncDone();
            } else {
                // Callback should always be done on the main thread to be able to manipulate the UI in the callback
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        syncDone();
                    }
                });
            }
        }
    }

    private void syncDone() {
        if (mManagerSyncListener == null) return;

        if (this instanceof SyncFetch) {
            mManagerSyncListener.onFetchDone((SyncFetch) this);
        } else {
            mManagerSyncListener.onUploadDone((SyncUpload) this);
        }
    }

    public void setSyncListener(SyncListener listener) {
        mSyncListener = listener;
    }

    public SyncListener getSyncListener() {
        return mSyncListener;
    }

    /**
     * Set if this sync object needs an access token to be able to sync. Default is true.
     */
    protected void setNeedsAccessToken(boolean needsAccessToken) {
        mNeedsAccessToken = needsAccessToken;
    }

    public boolean needsAccessToken() {
        return mNeedsAccessToken;
    }

    public void setIsBackgroundSync(boolean background) {
        mIsBackgroundSync = background;
    }

    public boolean isBackgroundSync() {
        return mIsBackgroundSync;
    }

    public boolean isStarted() {
        // It is seen as started if set to started or set to failed
        return mStarted || mFailed;
    }

    public boolean isFailed() {
        return mFailed;
    }

    public boolean isSuccess() {
        return !mFailed;
    }

    public <T> T getError() {
        // This will crash if the called tries to cast to wrong class type, but that is the callers responsibility
        //noinspection unchecked
        return (T) mError;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }
}
