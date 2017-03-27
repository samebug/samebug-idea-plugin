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
package com.samebug.clients.idea.ui.controller;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowType;
import com.intellij.openapi.wm.impl.ToolWindowImpl;
import com.intellij.openapi.wm.impl.ToolWindowManagerImpl;
import com.intellij.openapi.wm.impl.WindowInfoImpl;
import com.samebug.clients.idea.components.project.SamebugProjectComponent;
import com.samebug.clients.idea.ui.controller.toolwindow.ToolWindowController;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;


final public class SamebugToolWindowFactory implements ToolWindowFactory, DumbAware {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ToolWindowController twc = project.getComponent(SamebugProjectComponent.class).getToolWindowController();
        twc.initToolWindow(toolWindow);

        // do not start in docked mode
        toolWindow.setDefaultState(null, ToolWindowType.SLIDING, null);

        // there is no public interface to change the default autohide property, so we use reflection and don't care if it fails
        try {
            ToolWindowManagerImpl twm = ((ToolWindowImpl) toolWindow).getToolWindowManager();
            Method twmGetInfo = twm.getClass().getDeclaredMethod("getInfo", String.class);
            twmGetInfo.setAccessible(true);
            final WindowInfoImpl windowInfo = (WindowInfoImpl) twmGetInfo.invoke(twm, "Samebug");
            Method windowInfoWasRead = windowInfo.getClass().getDeclaredMethod("wasRead");
            windowInfoWasRead.setAccessible(true);
            boolean wasRead = (Boolean) windowInfoWasRead.invoke(windowInfo);
            if (!wasRead) {
                toolWindow.setAutoHide(true);

                // NOTE IntelliJ sets the width of the toolwindow after it is opened, and we can't hook there,
                // so the width will be the default one after opening it for first, but will be the enlarged after the second.
                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Method setWeight = windowInfo.getClass().getDeclaredMethod("setWeight", float.class);
                            setWeight.setAccessible(true);
                            setWeight.invoke(windowInfo, 0.42f);
                        } catch (Exception ignored) {
                        }
                    }
                });
            }
        } catch (Exception ignored) {
        }

        // TODO for now, we also force the behaviour of the toolwindow, which will override the settings even if the user has changed it. Remove it asap.
        try {
            toolWindow.setType(ToolWindowType.SLIDING, null);
            toolWindow.setAutoHide(true);
        } catch (Exception ignored) {
        }
    }
}
