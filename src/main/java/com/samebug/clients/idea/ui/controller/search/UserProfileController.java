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
package com.samebug.clients.idea.ui.controller.search;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.entities.user.Statistics;
import com.samebug.clients.common.entities.user.User;
import com.samebug.clients.idea.components.application.ApplicationCache;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.messages.controller.ProfileListener;
import com.samebug.clients.idea.ui.ImageUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class UserProfileController implements ProfileListener {
    final static Logger LOGGER = Logger.getInstance(UserProfileController.class);
    @NotNull
    final SearchTabController controller;

    public UserProfileController(@NotNull final SearchTabController controller) {
        this.controller = controller;

        MessageBusConnection projectConnection = controller.project.getMessageBus().connect(controller);
        projectConnection.subscribe(ProfileListener.TOPIC, this);
        ApplicationCache cache = IdeaSamebugPlugin.getInstance().getCache();
        if (cache != null) {
            profileChange(cache.getUser(), cache.getStatistics());
        }
    }


    @Override
    public void profileChange(final User user, final Statistics statistics) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                controller.view.collapsableUserPanel.updateUser(user);
                controller.view.collapsableUserPanel.updateStatistics(statistics);

            }
        });

        // TODO extract the logic of loading an image on background thread and update what is necessary on the ui thread.
        // When the avatar is not already loaded, we have to load it and update the view
        if (user != null && ImageUtil.get(user.getAvatarUrl()) == null) {
            ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                @Override
                public void run() {
                    ImageUtil.loadImages(Arrays.asList(user.getAvatarUrl()));
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        public void run() {
                            controller.view.collapsableUserPanel.updateUser(user);
                        }
                    });
                }
            });
        }
    }
}
