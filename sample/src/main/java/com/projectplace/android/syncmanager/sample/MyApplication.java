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

package com.projectplace.android.syncmanager.sample;

import android.app.Application;

import com.projectplace.android.syncmanager.sample.sync.MySyncManager;
import com.projectplace.android.syncmanager.sample.sync.TestApiService;

public class MyApplication extends Application {

    private static TestApiService sApiService;

    @Override
    public void onCreate() {
        super.onCreate();
        initSingletons();
    }

    private void initSingletons() {
        MySyncManager.initInstance(this);
        MySharedPreferences.initInstance(this);

        // This is probably a retrofit api service in a real app
        sApiService = new TestApiService();
    }

    public static TestApiService getApiService() {
        return sApiService;
    }
}
