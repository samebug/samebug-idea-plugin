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
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.samebug.clients.idea.scanners.LogcatScanner;
import com.samebug.clients.idea.util.AndroidSdkUtil;
import com.samebug.clients.idea.scanners.StackTraceMatcherFactory;
import com.samebug.clients.idea.scanners.exceptions.UnableToCreateReceiver;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class LogcatProcessWatcher extends AbstractProjectComponent
        implements AndroidDebugBridge.IDeviceChangeListener, Disposable,
        AndroidDebugBridge.IDebugBridgeChangeListener {

    public LogcatProcessWatcher(Project project) {
        super(project);
    }

    @Override
    public void projectOpened() {
        this.scannerFactory = new StackTraceMatcherFactory(myProject);
        initializeDebugBridge();
    }


    @Override
    public void deviceConnected(IDevice device) {
        initReceiverIfDeviceIsOnline(device);
    }

    public void initReceiverIfDeviceIsOnline(IDevice device) {
        try {
            if (device.isOnline()) {
                initReceiver(device);
            }
        } catch (UnableToCreateReceiver e) {
            LOGGER.error("Unable to connect device", e);
        }
    }

    @Override
    public void deviceDisconnected(IDevice device) {
        removeReceiver(device);
    }

    @Override
    public void deviceChanged(IDevice device, int changeMask) {
        initReceiverIfDeviceIsOnline(device);
    }

    private void initializeDebugBridge() {
        File adb = AndroidSdkUtil.getAdb(myProject);
        if (adb != null) {
            AndroidDebugBridge.initIfNeeded(false);
            AndroidDebugBridge bridge = AndroidDebugBridge.createBridge(adb.getPath(), false);

            AndroidDebugBridge.addDeviceChangeListener(this);
            AndroidDebugBridge.addDebugBridgeChangeListener(this);

            if (bridge.isConnected()) {
                for (IDevice device : bridge.getDevices()) {
                    initReceiverIfDeviceIsOnline(device);
                }
            }
        }
    }


    @Override
    public void dispose() {
        AndroidDebugBridge.addDeviceChangeListener(this);
        AndroidDebugBridge.addDebugBridgeChangeListener(this);
        for (Map.Entry<Integer, LogcatScanner> entry : receivers.entrySet()) {
            entry.getValue().finish();
        }
        receivers.clear();
    }

    private final static Logger LOGGER = Logger.getInstance(LogcatProcessWatcher.class);
    private StackTraceMatcherFactory scannerFactory;

    private synchronized IShellOutputReceiver initReceiver(@NotNull IDevice device) throws UnableToCreateReceiver {
        Integer deviceHashCode = System.identityHashCode(device);
        IShellOutputReceiver receiver = receivers.get(deviceHashCode);
        if (receiver == null) {
            receiver = createReceiver(device, deviceHashCode);
        }
        return receiver;
    }

    private IShellOutputReceiver createReceiver(@NotNull IDevice device, Integer deviceHashCode) throws UnableToCreateReceiver {
        try {
            LogcatScanner receiver = new LogcatScanner(scannerFactory.createScanner());
            device.executeShellCommand("logcat -v long", receiver, 0L, TimeUnit.NANOSECONDS);
            receivers.put(deviceHashCode, receiver);
            return receiver;
        } catch (TimeoutException e) {
            throw new UnableToCreateReceiver("Unable to create receiver for device " + device.getName(), e);
        } catch (AdbCommandRejectedException e) {
            throw new UnableToCreateReceiver("Unable to create receiver for device " + device.getName(), e);
        } catch (ShellCommandUnresponsiveException e) {
            throw new UnableToCreateReceiver("Unable to create receiver for device " + device.getName(), e);
        } catch (IOException e) {
            throw new UnableToCreateReceiver("Unable to create receiver for device " + device.getName(), e);
        }
    }

    private synchronized void removeReceiver(@NotNull IDevice device) {
        Integer descriptorHashCode = System.identityHashCode(device);
        LogcatScanner receiver = receivers.get(descriptorHashCode);
        if (receiver != null) {
            receiver.finish();
            receivers.remove(descriptorHashCode);
        }
    }

    private final Map<Integer, LogcatScanner> receivers = new HashMap<Integer, LogcatScanner>();

    @Override
    public void bridgeChanged(AndroidDebugBridge bridge) {
        for (IDevice device : bridge.getDevices()) {
            deviceConnected(device);
        }
    }
}
