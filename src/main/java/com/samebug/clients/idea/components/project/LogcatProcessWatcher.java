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
package com.samebug.clients.idea.components.project;

import com.android.ddmlib.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.samebug.clients.idea.processadapters.LogcatAdapter;
import com.samebug.clients.idea.util.AndroidSdkUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class LogcatProcessWatcher extends AbstractProjectComponent
        implements AndroidDebugBridge.IDeviceChangeListener {

    // AbstractProjectComponent overrides
    public LogcatProcessWatcher(Project project) {
        super(project);
    }

    @Override
    public void projectOpened() {
        this.scannerFactory = new StackTraceMatcherFactory(myProject);
        File adb = AndroidSdkUtil.getAdb(myProject);
        if (adb != null) {
            AndroidDebugBridge.initIfNeeded(false);
            AndroidDebugBridge bridge = AndroidDebugBridge.createBridge(adb.getPath(), false);
            AndroidDebugBridge.addDeviceChangeListener(this);
            if (bridge.isConnected()) {
                for (IDevice device : bridge.getDevices()) {
                    initReceiver(device);
                }
            }
        }
    }

    @Override
    public void projectClosed() {
        AndroidDebugBridge.removeDeviceChangeListener(this);
        for (LogcatAdapter listener : listeners.values()) {
            listener.finish();
        }
        listeners.clear();
    }

    // IDeviceChangeListener overrides
    @Override
    public void deviceConnected(IDevice device) {
        initReceiver(device);
    }


    @Override
    public void deviceDisconnected(IDevice device) {
        removeReceiver(device);
    }

    @Override
    public void deviceChanged(IDevice device, int changeMask) {
        initReceiver(device);
    }

    // implementation
    private synchronized void initReceiver(@NotNull IDevice device) {
        if (device.isOnline()) {
            Integer deviceHashCode = System.identityHashCode(device);
            if (listeners.get(deviceHashCode) == null) {
                createReceiver(device, deviceHashCode);
            }
        }
    }

    private LogcatAdapter createReceiver(@NotNull final IDevice device, Integer deviceHashCode) {
        final LogcatAdapter receiver = new LogcatAdapter(scannerFactory.createScanner());
        listeners.put(deviceHashCode, receiver);
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                try {
                    device.executeShellCommand("logcat -v long", receiver, 0L, TimeUnit.NANOSECONDS);
                } catch (TimeoutException e) {
                    LOGGER.warn("Unable to create receiver for device " + device.getName(), e);
                } catch (AdbCommandRejectedException e) {
                    LOGGER.warn("Unable to create receiver for device " + device.getName(), e);
                } catch (ShellCommandUnresponsiveException e) {
                    LOGGER.warn("Unable to create receiver for device " + device.getName(), e);
                } catch (IOException e) {
                    LOGGER.warn("Unable to create receiver for device " + device.getName(), e);
                }
            }
        });
        return receiver;
    }

    private synchronized void removeReceiver(@NotNull IDevice device) {
        Integer deviceHashCode = System.identityHashCode(device);
        LogcatAdapter receiver = listeners.get(deviceHashCode);
        if (receiver != null) {
            receiver.finish();
            listeners.remove(deviceHashCode);
        }
    }

    private StackTraceMatcherFactory scannerFactory;
    private final Map<Integer, LogcatAdapter> listeners = new HashMap<Integer, LogcatAdapter>();
    private final static Logger LOGGER = Logger.getInstance(LogcatProcessWatcher.class);
}
