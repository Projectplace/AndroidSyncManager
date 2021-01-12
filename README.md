# SyncManager
This sync manager is design to make short lived api calls, it is not designed to download huge documents that will take an hour to download.
By default the sync manager is enabled to assume that an access token is used to be able to do api calls. If that is not the case for
you it is possible to disable that.

## Features
* Decouples sync handling from UI
* Works great together with Retrofit and Android loaders
* Run many uploads and fetches in parallel
* Get callbacks to anywhere in the app about the sync object results
* Revert handling if an upload fails
* Conflict handling between uploads and fetches
* Callbacks to refresh access tokens if needed before the sync object is run

## Download lib with gradle

```gradle
    repositories {
        jcenter()
    }

    dependencies {
        compile 'com.projectplace.android:syncmanager:1.1.4'
    }
```

## Usage
To understand how to use the SyncManager best is to look at the sample app and to check the java doc on the SyncManager, SyncUpload and SyncFetch.
But below is the main things to think about.

Start by subclassing SyncManager and implement the following three abstract methods. If your sync manager does not use an access token just
return false in shouldRefreshAccessToken() and leave startRefreshAccessToken() empty.

```java
    /**
     * Called just before a sync object is started to check for special conditions if the sync object
     * should be started. This is always called on a background thread.
     *
     * @param sync The sync object that is about to start.
     * @return True if the sync object should be started, otherwise False.
     */
    @WorkerThread
    protected abstract boolean shouldSyncObject(@NonNull SyncObject sync);

    /**
     * Called just before a sync object that needs an access token is started to check if the access token needs to be
     * refreshed first. If that is needed, {@link SyncManager#startRefreshAccessToken(RefreshAccessTokenCallback callback)}
     * will be called directly and the sync object will be set on hold until that is done. This is always called on a
     * background thread.
     *
     * @return True if the access token needs to be refreshed first.
     */
    @WorkerThread
    protected abstract boolean shouldRefreshAccessToken();

    /**
     * Called when the access token needs to be refreshed. This is always called on a background thread.
     *
     * @param callback to be used to indicate that a refresh was successful or not. If the refresh failed
     *                 another try will take place directly after.
     */
    @WorkerThread
    protected abstract void startRefreshAccessToken(@NonNull RefreshAccessTokenCallback callback);
```

Next create your sync objects.
#### SyncUpload
For an upload the main methods to implement are.
```java
    /**
     * Prepare will be called directly after the upload has been added to the sync manager. This can be overridden to update
     * the database directly before uploading to the server to make the UI update immediately. If prepare is overridden,
     * revert must also be overridden to be able to revert the prepare changes if the api call fails.
     */
    public void prepare();

    /**
     * If the upload api call fails, revert will be called to revert the prepare operations that was executed before the api call.
     */
    public void revert();

    /**
     * After an upload is done this will be called. This can be overridden if any database operations is needed after the upload.
     */
    public void onSave();

    /**
     * Called when this sync object should start. The sync that is started should always be started on a
     * new thread to not block other sync objects to run in parallel as this is called from the sync loop thread.
     */
    public abstract void onStart();
```

#### SyncFetch
For a fetch the main methods to implement are.
```java
    /**
     * Called when the sync object should save it synced data. This will always be called on a background thread
     * so it is safe to call a database here.
     */
    public abstract void onSave();

    /**
     * Should return true when the sync object is done with all syncing. After {@link #checkIfDone()} has been called this
     * will be called to see if the sync object is done with its syncing.
     */
    public abstract boolean isDone();

    /**
     * Called when this sync object should start. The sync that is started should always be started on a
     * new thread to not block other sync objects to run in parallel as this is called from the sync loop thread.
     */
    public abstract void onStart();
```

#### SyncFetchGroup
To run several fetch objects together but with a synchronized saved operation you can use a SyncFetchGroup.
Start by extending the SyncFetchGroup class and then implementing the onAddFetches() method to add the initial fetches.
```java

    /**
    * Add the initial fetch objects to the group from this method.
    */
    protected abstract void onAddFetches()

    /**
    * Add fetch objects to the group. The group won't be done until all fetch objects in the group are done.
    */
    protected void add(SyncFetch fetch)

    /**
    * Override if you need to save something more after the individual fetch objects doSave methods has been called.
    */
    protected void onSaveGroup()
```


#### Queue and listen
To queue a sync object and listen to the result do the following
```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ...
        MySyncManager.getInstance().fetch(new SyncFetchItems());
        MySyncManager.getInstance().registerSyncListener(this);
    }

    @Override
    public void onFetchDone(SyncFetch syncFetch) {
        if (syncFetch instanceof SyncFetchItems) {
            ...
        }
    }
```

## Read the source documentation
All important methods are documented. Check them out here.<br/>
[com.projectplace.android.syncmanager.SyncManager](https://github.com/Projectplace/AndroidSyncManager/blob/master/syncmanager/src/main/java/com/projectplace/android/syncmanager/SyncManager.java)<br/>
[com.projectplace.android.syncmanager.SyncObject](https://github.com/Projectplace/AndroidSyncManager/blob/master/syncmanager/src/main/java/com/projectplace/android/syncmanager/SyncObject.java)<br/>
[com.projectplace.android.syncmanager.SyncUpload](https://github.com/Projectplace/AndroidSyncManager/blob/master/syncmanager/src/main/java/com/projectplace/android/syncmanager/SyncUpload.java)<br/>
[com.projectplace.android.syncmanager.SyncFetch](https://github.com/Projectplace/AndroidSyncManager/blob/master/syncmanager/src/main/java/com/projectplace/android/syncmanager/SyncFetch.java)<br/>
[com.projectplace.android.syncmanager.SyncFetchGroup](https://github.com/Projectplace/AndroidSyncManager/blob/master/syncmanager/src/main/java/com/projectplace/android/syncmanager/SyncFetchGroup.java)<br/>
[com.projectplace.android.syncmanager.SyncFetchSimple](https://github.com/Projectplace/AndroidSyncManager/blob/master/syncmanager/src/main/java/com/projectplace/android/syncmanager/SyncFetchSimple.java)<br/>

Check out the sample app<br/>
[Sample app](https://github.com/Projectplace/AndroidSyncManager/tree/master/sample/src/main/java/com/projectplace/android/syncmanager/sample)<br/>

## License
    Copyright (C) 2016 Planview, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
