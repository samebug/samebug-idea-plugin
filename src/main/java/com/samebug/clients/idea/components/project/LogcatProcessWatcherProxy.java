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
import com.android.tools.idea.logcat.AndroidLogcatReceiver;
import com.android.tools.idea.logcat.AndroidLogcatView;
import com.android.tools.idea.monitor.AndroidToolWindowFactory;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.samebug.clients.idea.components.application.Tracking;
import com.samebug.clients.idea.console.ConsoleWatcher;
import com.samebug.clients.idea.processadapters.LogcatWriter;
import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.idea.util.AndroidSdkUtil;
import com.samebug.clients.search.api.entities.tracking.DebugSessionInfo;
import org.jetbrains.android.actions.AndroidEnableAdbServiceAction;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * This proxy class separates the behaviour of Logcat watcher.
 * <p>
 * If Android SDK is not present, it does nothing, so it will not crash in environments without Android SDK.
 */
public class LogcatProcessWatcherProxy extends AbstractProjectComponent {
    final AbstractProjectComponent implementation;

    public LogcatProcessWatcherProxy(Project project) {
        super(project);
        boolean isAndroidSdkPresent;
        try {
            Class<?> resolveAndroidFacet = AndroidFacet.class;
            isAndroidSdkPresent = true;
        } catch (NoClassDefFoundError e) {
            isAndroidSdkPresent = false;
        }
        if (isAndroidSdkPresent) {
            implementation = new LogcatProcessWatcher(project);
        } else {
            implementation = new NopProjectComponent(project);
        }
    }

    @Override
    public void projectOpened() {
        implementation.projectOpened();
    }

    @Override
    public void projectClosed() {
        implementation.projectClosed();
    }
}

class LogcatProcessWatcher extends AbstractProjectComponent
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
            AndroidDebugBridge.initIfNeeded(AndroidEnableAdbServiceAction.isAdbServiceEnabled());
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
        for (Integer deviceHash : listeners.keySet()) {
            removeReceiver(deviceHash);
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
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                myProject.getComponent(ToolWindowController.class).changeToolwindowIcon(false);
            }
        });
        if (device.isOnline()) {
            Integer deviceHashCode = System.identityHashCode(device);
            if (listeners.get(deviceHashCode) == null) {
                createReceiver(device, deviceHashCode);
            }
        }
    }

    private AndroidLogcatReceiver createReceiver(@NotNull final IDevice device, Integer deviceHashCode) {
        final DebugSessionInfo sessionInfo = new DebugSessionInfo("logcat");
        final AndroidLogcatReceiver receiver = new AndroidLogcatReceiver(device, new LogcatWriter(myProject, scannerFactory.createScanner(sessionInfo)));
        listeners.put(deviceHashCode, receiver);
        debugSessionInfos.put(deviceHashCode, sessionInfo);
        ToolWindow t = ToolWindowManager.getInstance(myProject).getToolWindow(AndroidToolWindowFactory.TOOL_WINDOW_ID);
        for (Content content : t.getContentManager().getContents()) {
            final AndroidLogcatView view = content.getUserData(AndroidLogcatView.ANDROID_LOGCAT_VIEW_KEY);

            if (view != null) {
                ConsoleView c = view.getLogConsole().getConsole();
                new ConsoleWatcher((ConsoleViewImpl) c);
            }
        }
        Tracking.projectTracking(myProject).trace(Events.debugStart(myProject, sessionInfo));
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                try {
                    device.executeShellCommand("logcat -v long", receiver, 0L, TimeUnit.NANOSECONDS);
                } catch (TimeoutException e) {
                    LOGGER.warn("Unable to create receiver for device " + device.getName(), e);
                    Notifications.Bus.notify(new Notification("samebug", "Adb connection failure",
                            "Unable to create receiver for device " + device.getName(), NotificationType.WARNING));
                } catch (AdbCommandRejectedException e) {
                    LOGGER.warn("Unable to create receiver for device " + device.getName(), e);
                    Notifications.Bus.notify(new Notification("samebug", "Adb connection failure",
                            "Unable to create receiver for device " + device.getName(), NotificationType.WARNING));
                } catch (ShellCommandUnresponsiveException e) {
                    LOGGER.warn("Unable to create receiver for device " + device.getName(), e);
                    Notifications.Bus.notify(new Notification("samebug", "Adb connection failure",
                            "Unable to create receiver for device " + device.getName(), NotificationType.WARNING));
                } catch (IOException e) {
                    LOGGER.warn("Unable to create receiver for device " + device.getName(), e);
                    Notifications.Bus.notify(new Notification("samebug", "Adb connection failure",
                            "Unable to create receiver for device " + device.getName(), NotificationType.WARNING));
                }

            }
        });
        return receiver;
    }

    private synchronized void removeReceiver(@NotNull IDevice device) {
        removeReceiver(System.identityHashCode(device));
    }

    private void removeReceiver(Integer deviceHashCode) {
        AndroidLogcatReceiver receiver = listeners.get(deviceHashCode);
        if (receiver != null) {
            receiver.processNewLine("\n");
            receiver.done();
            Tracking.projectTracking(myProject).trace(Events.debugStop(myProject, debugSessionInfos.get(deviceHashCode)));
            debugSessionInfos.remove(deviceHashCode);
            listeners.remove(deviceHashCode);
        }
    }

    private StackTraceMatcherFactory scannerFactory;
    private final Map<Integer, AndroidLogcatReceiver> listeners = new HashMap<Integer, AndroidLogcatReceiver>();
    private final Map<Integer, DebugSessionInfo> debugSessionInfos = new HashMap<Integer, DebugSessionInfo>();
    private final static Logger LOGGER = Logger.getInstance(LogcatProcessWatcher.class);
}

class NopProjectComponent extends AbstractProjectComponent {
    NopProjectComponent(Project project) {
        super(project);
    }
}