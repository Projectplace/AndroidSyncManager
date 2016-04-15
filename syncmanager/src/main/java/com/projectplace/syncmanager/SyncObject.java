package com.projectplace.syncmanager;

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

    public abstract void onSave();

    public abstract void onStart();

    public abstract boolean isDone();

    void setManagerSyncListener(SyncListener listener) {
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

    protected void setErrorAndMessage(Object error, String errorMessage) {
        mErrorMessage = errorMessage;
        setError(error);
    }

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
        return (T) mError;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }
}
