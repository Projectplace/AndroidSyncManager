# SyncManager
This sync manager is design to make short lived api calls, it is not designed to download huge documents that will take an hour to download.

## Features
* Decouple sync handling from UI
* Works great together with Retrofit and Android loaders
* Run many uploads and fetches in parallel
* Get callbacks to anywhere in the app about the sync object results
* Revert handling if an upload fails
* Conflict handling between uploads and fetches
* Callbacks to refresh access tokens if needed before the sync object is run

## Download lib with gradle

```gradle
    repositories {
        mavenCentral()
    }

    dependencies {
        compile 'com.projectplace.android:syncmanager:1.0'
    }
```

## Usage
TODO

## License
    Copyright 2016 Projectplace

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
