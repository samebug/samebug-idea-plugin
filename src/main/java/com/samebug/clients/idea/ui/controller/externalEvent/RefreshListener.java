/*
 * Copyright 2018 Samebug, Inc.
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
package com.samebug.clients.idea.ui.controller.externalEvent;

import com.samebug.clients.idea.messages.RefreshTimestampsListener;
import com.samebug.clients.idea.ui.controller.frame.BaseFrameController;
import com.samebug.clients.swing.ui.base.label.TimestampLabel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;

public final class RefreshListener implements RefreshTimestampsListener {
    @NotNull
    final BaseFrameController controller;

    public RefreshListener(@NotNull final BaseFrameController controller) {
        this.controller = controller;
    }

    @Override
    public void refreshDateLabels() {
        final Component view = (JComponent) controller.view;
        java.util.List<Component> components = new LinkedList<Component>();
        components.add(view);
        while (!components.isEmpty()) {
            Component c = components.get(0);
            if (c instanceof TimestampLabel) ((TimestampLabel) c).updateRelativeTimestamp();
            else if (c instanceof JComponent) components.addAll(Arrays.asList(((JComponent) c).getComponents()));
            components.remove(0);
        }
    }
}
