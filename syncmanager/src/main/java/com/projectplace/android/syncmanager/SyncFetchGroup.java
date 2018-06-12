/*
 * Copyright (C) 2017 Planview, Inc.
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

import android.support.annotation.NonNull;

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

    /**
     * Add the initial fetch objects to the group from this method.
     */
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

    /**
     * Override if you need to save something more after the individual fetch objects doSave methods has been called.
     */
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

    /**
     * Add fetch objects to the group. The group won't be done until all fetch objects in the group are done.
     */
    protected void add(SyncFetch fetch) {
        fetch.setIsGroupFetch(true);
        fetch.setSyncListener(new SyncObject.SyncListenerAdapter() {
            @Override
            public void onFetchDone(@NonNull SyncFetch syncFetch) {
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
