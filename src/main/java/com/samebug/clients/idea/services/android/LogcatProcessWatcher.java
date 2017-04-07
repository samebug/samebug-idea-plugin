/*
 * Copyright 2017 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *    http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.idea.services.android;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.android.tools.idea.logcat.AndroidLogcatPreferences;
import com.android.tools.idea.logcat.AndroidLogcatService;
import com.android.tools.idea.logcat.AndroidLogcatView;
import com.android.tools.idea.monitor.AndroidToolWindowFactory;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.samebug.clients.common.entities.search.DebugSessionInfo;
import com.samebug.clients.common.search.LogScannerFactory;
import com.samebug.clients.common.ui.modules.TrackingService;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.processadapters.LogcatWriter;
import com.samebug.clients.idea.search.StackTraceMatcherFactory;
import com.samebug.clients.idea.search.console.ConsoleWatcher;
import com.samebug.clients.idea.tracking.IdeaRawEvent;
import org.jetbrains.android.sdk.AndroidSdkUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class LogcatProcessWatcher implements AndroidDebugBridge.IDeviceChangeListener {
    private final Project myProject;

    public LogcatProcessWatcher(Project project) {
        myProject = project;
    }

    public void projectOpened() {
        AndroidDebugBridge.addDeviceChangeListener(this);

        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                AndroidDebugBridge bridge = AndroidSdkUtils.getDebugBridge(myProject);
                if (bridge != null && bridge.isConnected()) {
                    for (IDevice device : bridge.getDevices()) {
                        initReceiver(device);
                    }
                }
            }
        });
    }

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
        if (device.isOnline()) {
            Integer deviceHashCode = System.identityHashCode(device);
            if (listeners.get(deviceHashCode) == null) {
                createListener(device, deviceHashCode);
            }
        }
    }

    private void createListener(@NotNull final IDevice device, Integer deviceHashCode) {
        final DebugSessionInfo sessionInfo = new DebugSessionInfo("logcat");

        final LogScannerFactory scannerFactory = new StackTraceMatcherFactory(myProject, sessionInfo);
        AndroidLogcatPreferences logcatPreferences = AndroidLogcatPreferences.getInstance(myProject);
        final AndroidLogcatService.LogLineListener receiver = new LogcatWriter(logcatPreferences, scannerFactory.createScanner());
        listeners.put(deviceHashCode, receiver);
        debugSessionInfos.put(deviceHashCode, sessionInfo);

        ToolWindow t = ToolWindowManager.getInstance(myProject).getToolWindow(AndroidToolWindowFactory.TOOL_WINDOW_ID);
        for (Content content : t.getContentManager().getContents()) {
            final AndroidLogcatView view = content.getUserData(AndroidLogcatView.ANDROID_LOGCAT_VIEW_KEY);

            if (view != null) {
                ConsoleView c = view.getLogConsole().getConsole();
                if (c instanceof ConsoleViewImpl) {
                    // do we have to keep this reference?
                    new ConsoleWatcher(myProject, (ConsoleViewImpl) c, sessionInfo);
                }
            }
        }

        AndroidLogcatService.getInstance().addListener(device, receiver, true);

        TrackingService.trace(IdeaRawEvent.debugStart(myProject, sessionInfo));
    }

    private synchronized void removeReceiver(@NotNull IDevice device) {
        removeReceiver(System.identityHashCode(device));
    }

    private void removeReceiver(Integer deviceHashCode) {
        AndroidLogcatService.LogLineListener receiver = listeners.get(deviceHashCode);
        if (receiver != null) {
            DebugSessionInfo sessionInfo = debugSessionInfos.get(deviceHashCode);
            debugSessionInfos.remove(deviceHashCode);
            listeners.remove(deviceHashCode);
            IdeaSamebugPlugin.getInstance().searchRequestStore.removeSession(sessionInfo);

            TrackingService.trace(IdeaRawEvent.debugStop(myProject, debugSessionInfos.get(deviceHashCode)));
        }
    }

    private final Map<Integer, AndroidLogcatService.LogLineListener> listeners = new HashMap<Integer, AndroidLogcatService.LogLineListener>();
    private final Map<Integer, DebugSessionInfo> debugSessionInfos = new HashMap<Integer, DebugSessionInfo>();
    private static final Logger LOGGER = Logger.getInstance(LogcatProcessWatcher.class);
}
