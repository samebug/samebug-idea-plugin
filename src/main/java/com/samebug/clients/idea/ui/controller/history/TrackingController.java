/**
 * Copyright 2017 Samebug, Inc.
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
package com.samebug.clients.idea.ui.controller.history;

import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.idea.components.application.Tracking;
import com.samebug.clients.idea.messages.tracking.TrackingListener;
import org.jetbrains.annotations.NotNull;

final class TrackingController {
    @NotNull
    final HistoryTabController controller;
    @NotNull
    final Project myProject;
    @NotNull
    TrackingListener tracker;

    public TrackingController(@NotNull final HistoryTabController controller) {
        this.controller = controller;
        this.myProject = controller.myProject;
        tracker = Tracking.projectTracking(myProject);

        MessageBusConnection projectConnection = myProject.getMessageBus().connect(controller);
    }
}
