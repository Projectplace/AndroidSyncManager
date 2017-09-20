package com.projectplace.android.syncmanager;

import java.util.ArrayList;
import java.util.List;

/**
 * This class should be subclassed when you want a number of fetches to be fetched and then saved together.
 * When extending this class you should add all the onAddFetches method but you can also add more to the group later on.
 * The group's onSaveGroup() will be called when all the fetches in the group is done. All the fetches onSave() has been
 * called just before the group's onSaveGroup().
 */
public abstract class SyncFetchGroup extends SyncFetch {

    private List<SyncFetch> mFetches = new ArrayList<>();
    private SyncManager mSyncManager;

    protected abstract void onAddFetches();

    public SyncFetchGroup(SyncManager syncManager) {
        mSyncManager = syncManager;
    }

    @Override
    public final void onSave() {
        for (SyncFetch fetch : mFetches) {
            fetch.onSave();
        }
        onSaveGroup();
    }

    protected void onSaveGroup() {
    }

    @Override
    public final void onReset() {
        mFetches.clear();
        onResetGroup();
    }

    protected void onResetGroup() {
    }

    @Override
    public final void onStart() {
        onAddFetches();

        if (mFetches.size() == 0) {
            throw new RuntimeException("Can not start fetch group with no fetches added");
        }
    }

    @Override
    public final boolean isDone() {
        // If no fetches it means the group was just reset and will be restarted
        if (mFetches.size() == 0) {
            return false;
        }

        for (SyncFetch fetch : mFetches) {
            if (!fetch.isDone()) {
                return false;
            }
        }
        return true;
    }

    protected void add(SyncFetch fetch) {
        fetch.setIsGroupFetch(true);
        fetch.setSyncListener(new SyncObject.SyncListenerAdapter() {
            @Override
            public void onFetchDone(SyncFetch syncFetch) {
                if (syncFetch.isFailed()) {
                    setErrorAndMessage(syncFetch.getError(), syncFetch.getErrorMessage());
                }
                checkIfDone();
            }
        });
        mFetches.add(fetch);
        mSyncManager.fetch(fetch);
    }
}
