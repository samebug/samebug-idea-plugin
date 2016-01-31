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
package com.samebug.clients.idea.intellij.autosearch.android;

import com.android.ddmlib.*;
import com.intellij.execution.Executor;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.execution.ui.RunContentWithExecutorListener;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.samebug.clients.api.StackTraceListener;
import com.samebug.clients.idea.intellij.autosearch.StackTraceMatcherFactory;
import com.samebug.clients.idea.intellij.autosearch.android.exceptions.UnableToCreateReceiver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class LogcatScannerManager
        implements AndroidDebugBridge.IDeviceChangeListener, Disposable,
        RunContentWithExecutorListener, AndroidDebugBridge.IClientChangeListener,
        AndroidDebugBridge.IDebugBridgeChangeListener {

    public LogcatScannerManager(StackTraceListener stackTraceListener) {
        super();
        this.scannerFactory = new StackTraceMatcherFactory(stackTraceListener);
        initialize();
    }


    @Override
    public void deviceConnected(IDevice device) {
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
        try {
            if (device.isOnline()) {
                initReceiver(device);
            }
        } catch (UnableToCreateReceiver e) {
            LOGGER.error("Unable to connect device", e);
        }
    }

    private void initialize() {
        AndroidDebugBridge bridge = AndroidDebugBridge.getBridge();
        AndroidDebugBridge.addDeviceChangeListener(this);
        AndroidDebugBridge.addClientChangeListener(this);
        AndroidDebugBridge.addDebugBridgeChangeListener(this);


        if (bridge.isConnected()) {
            for (IDevice device : bridge.getDevices()) {
                deviceConnected(device);
            }
            initialized = true;
        } else {
            initialized = false;
        }
    }

    private final static Logger LOGGER = Logger.getInstance(LogcatScannerManager.class);
    private final StackTraceMatcherFactory scannerFactory;
    private volatile boolean initialized;


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
            OutputScanner receiver = new OutputScanner(scannerFactory.createScanner());
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
        OutputScanner receiver = receivers.get(descriptorHashCode);
        if (receiver != null) {
            receiver.finish();
            receivers.remove(descriptorHashCode);
        }
    }

    private final Map<Integer, OutputScanner> receivers = new HashMap<Integer, OutputScanner>();

    @Override
    public void dispose() {
        AndroidDebugBridge.terminate();
    }

    @Override
    public void contentSelected(@Nullable RunContentDescriptor runContentDescriptor, @NotNull Executor executor) {
        if (!initialized) initialize();
    }

    @Override
    public void contentRemoved(@Nullable RunContentDescriptor runContentDescriptor, @NotNull Executor executor) {

    }

    @Override
    public void clientChanged(Client client, int changeMask) {
        LOGGER.info("Client changed: " + client);

    }

    @Override
    public void bridgeChanged(AndroidDebugBridge bridge) {
        LOGGER.info("Bridge changed: " + bridge);
        for (IDevice device : bridge.getDevices()) {
            deviceConnected(device);
        }
    }
}
