/**
 * Copyright 2016 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.idea.services.android;

import com.android.ddmlib.AndroidDebugBridge;
import com.intellij.openapi.project.Project;
import org.jetbrains.android.facet.AndroidFacet;

/**
 * This proxy class separates the behaviour of Logcat watcher.
 * <p>
 * If Android SDK is not present, it does nothing, so it will not crash in environments without Android SDK.
 */
public class LogcatServiceFactory {
    public static LogcatService createService(Project project) {
        boolean isAndroidSdkPresent;
        try {
            Class<?> resolveAndroidFacet = AndroidFacet.class;
            Class<?> resolveAndroidDebugBridge = AndroidDebugBridge.class;
            Class<?> resolveIDeviceChangeListener = AndroidDebugBridge.IDeviceChangeListener.class;
            isAndroidSdkPresent = true;
        } catch (NoClassDefFoundError e) {
            isAndroidSdkPresent = false;
        } catch (IllegalAccessError e) {
            isAndroidSdkPresent = false;
        }
        if (isAndroidSdkPresent) {
            return new LogcatProcessWatcher(project);
        } else {
            return new NopService();
        }
    }
}

