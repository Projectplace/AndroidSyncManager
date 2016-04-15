package com.projectplace.syncmanager;

/**
 * A simple variant of a fetch. This class can be overridden if the fetch only downloads one object
 * with the help of a long id or no id at all.
 *
 * @param <T> The type of object that will be fetched.
 */
public abstract class SyncFetchSimple<T> extends SyncFetch {
    private T mData;
    private long mId;

    public SyncFetchSimple() {
    }

    public SyncFetchSimple(long id) {
        mId = id;
    }

    @Override
    public void onReset() {
        mData = null;
    }

    @Override
    public boolean isDone() {
        return mData != null;
    }

    public long getId() {
        return mId;
    }

    /**
     * When the fetch is done this method should be called to set the data.
     */
    protected void setData(T data) {
        mData = data;
        checkIfDone();
    }

    public T getData() {
        return mData;
    }
}
