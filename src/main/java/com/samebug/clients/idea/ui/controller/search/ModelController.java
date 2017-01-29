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
package com.samebug.clients.idea.ui.controller.search;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.search.api.entities.Solutions;
import com.samebug.clients.idea.messages.client.SearchModelListener;
import org.jetbrains.annotations.NotNull;

final class ModelController implements SearchModelListener {
    final static Logger LOGGER = Logger.getInstance(ModelController.class);
    @NotNull
    final SearchTabController controller;
    final int mySearchId;

    public ModelController(@NotNull final SearchTabController controller) {
        this.controller = controller;
        this.mySearchId = controller.mySearchId;

        MessageBusConnection projectConnection = controller.project.getMessageBus().connect(controller);
        projectConnection.subscribe(SearchModelListener.TOPIC, this);
    }

    @Override
    public void startLoadingSolutions(final int searchId) {
        if (mySearchId == searchId) {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    controller.view.setWarningLoading();
                }
            });
        }
    }

    @Override
    public void successLoadingSolutions(final int searchId, final Solutions result) {
        if (mySearchId == searchId) {
            controller.service.setSolutions(result);
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    controller.refreshTab();
                }
            });
        }
    }

    @Override
    public void failLoadingSolutions(final int searchId, final java.lang.Exception e) {
        if (mySearchId == searchId) {
            controller.service.setSolutions(null);
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    controller.refreshTab();
                }
            });
        }
    }

    @Override
    public void finishLoadingSolutions(final int searchId) {
    }


}
