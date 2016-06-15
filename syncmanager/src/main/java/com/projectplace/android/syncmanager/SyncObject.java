package com.projectplace.android.syncmanager;

import android.support.annotation.NonNull;

/**
 * Top class of both fetch and upload sync objects.
 */
public abstract class SyncObject {
    public interface SyncListener {
        void onFetchDone(SyncFetch syncFetch);

        void onUploadDone(SyncUpload syncUpload);
    }

    public static class SyncListenerAdapter implements SyncListener {
        @Override
        public void onFetchDone(SyncFetch syncFetch) {
        }

        @Override
        public void onUploadDone(SyncUpload syncUpload) {
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
     * @param error An object of any kind that describes the error.
     * @param errorMessage A message to be shown as a toast.
     */
    protected void setErrorAndMessage(Object error, String errorMessage) {
        mErrorMessage = errorMessage;
        setError(error);
    }

    /**
     * If a sync fails an error object should be set which can later be retrieved with {@link #getError()}
     * from a sync listener callback.
     *
     * @param error An object of any kind that describes the error.
     */
    protected void setError(Object error) {
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
            if (this instanceof SyncFetch) {
                mManagerSyncListener.onFetchDone((SyncFetch) this);
            } else {
                mManagerSyncListener.onUploadDone((SyncUpload) this);
            }
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

    public boolean isStarted() {
        return mStarted;
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
