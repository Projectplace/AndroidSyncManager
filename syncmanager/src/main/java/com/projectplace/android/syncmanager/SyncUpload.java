package com.projectplace.android.syncmanager;

/**
 * This class should be subclassed for all sync requests that uploads data.
 */
public abstract class SyncUpload extends SyncObject {
    private Runnable mExtraPrepare;
    private Runnable mExtraRevert;
    private boolean mUploadSucceeded;

    /**
     * An extra prepare runnable can be added to the sync object to be able to run prepare operations outside the sync
     * object. This will be called directly after the sync object has been added to the sync manager.
     */
    public void setExtraPrepare(Runnable extraPrepare) {
        mExtraPrepare = extraPrepare;
    }

    /**
     * An extra revert runnable can be added to the sync object to be able to run revert operations outside the sync
     * object. This will be called after an upload fails.
     */
    public void setExtraRevert(Runnable extraRevert) {
        mExtraRevert = extraRevert;
    }

    /**
     * Prepare will be called directly after the upload has been added to the sync manager. This can be overridden to update
     * the database directly before uploading to the server to make the UI update immediately. If prepare is overridden,
     * revert must also be overridden to be able to revert the prepare changes if the api call fails.
     */
    public void prepare() {
        if (mExtraPrepare != null) {
            mExtraPrepare.run();
        }
    }

    /**
     * If the upload api call fails, revert will be called to revert the prepare operations that was executed before the api call.
     */
    public void revert() {
        if (mExtraRevert != null) {
            mExtraRevert.run();
        }
    }

    /**
     * After an upload is done this will be called. This can be overridden if any database operations is needed after the upload.
     */
    @Override
    public void onSave() {
    }

    /**
     * If the upload has failed or succeeded the sync object is done.
     */
    @Override
    public boolean isDone() {
        return isFailed() || mUploadSucceeded;
    }

    /**
     * Checks if this upload is in conflict with another upload.
     * Default behaviour is no conflict. If other more complex rules apply this method should be overridden.
     *
     * @param syncUpload the upload to check for conflicts against.
     * @return true if the upload conflicts with this upload.
     */
    public boolean hasConflict(SyncUpload syncUpload) {
        return false;
    }

    /**
     * This method could be overridden in uploads that use the revert mechanism.
     * If several upload operations of the same type is running the revert values will change depending on what happens.
     * <p/>
     * For example, if the a text is changed two time in a row quickly on a slow network this is what will happen.
     * <p/>
     * 1. Upload 1: Text goes from "a" to "ab". Revert value is "a".
     * 2. Upload 2: Text goes from "ab" to "abc". Revert value will be "ab" at the beginning as that is what is visible in the UI.
     * But since the first upload has not finished and might fail the revert value should be "a".
     * 3. When upload 1 finished the real value is now "ab" so that is what all revert values should be of that upload type.
     *
     * @param upload       the upload with the new revert value.
     * @param useOldValues true if the revert value should be replace with the old value. False if the new value should be used.
     *                     When an upload is successful it should replace all other uploads of the same type with the new value.
     */
    public void updateRevertValues(SyncUpload upload, boolean useOldValues) {
    }

    /**
     * Whenever an upload is added all current fetches will be reset. This is done to prevent any conflicts from happening.
     * Overriding this method you can check for specific fetches that you know will never conflict with this upload. This
     * should only be done for fetches which is very heavy so you gain lots of performance by not resetting it.
     *
     * @param fetch The fetch to check if it should be reset.
     * @return true if the fetch should be reset, otherwise false.
     */
    protected boolean shouldResetFetch(SyncFetch fetch) {
        return true;
    }

    /**
     * This should be called after an upload is successfully finished.
     */
    protected void uploadSuccessful() {
        mUploadSucceeded = true;
        checkIfDone();
    }
}
