/**
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
