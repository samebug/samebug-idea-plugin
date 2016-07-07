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
import com.samebug.clients.common.ui.TextUtil;
import com.samebug.clients.idea.components.application.ClientService;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.messages.view.MarkViewListener;
import com.samebug.clients.idea.messages.view.RefreshTimestampsListener;
import com.samebug.clients.idea.messages.view.SearchTabsViewListener;
import com.samebug.clients.idea.messages.view.WriteTipListener;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.component.WriteTip;
import com.samebug.clients.idea.ui.component.organism.MarkPanel;
import com.samebug.clients.idea.ui.controller.TabController;
import com.samebug.clients.search.api.entities.RestHit;
import com.samebug.clients.search.api.exceptions.SamebugClientException;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

final class ViewController implements MarkViewListener, SearchTabsViewListener, WriteTipListener, RefreshTimestampsListener {
    final static Logger LOGGER = Logger.getInstance(ViewController.class);
    @NotNull
    final SearchTabController controller;

    public ViewController(@NotNull final SearchTabController controller) {
        this.controller = controller;

        MessageBusConnection projectConnection = controller.project.getMessageBus().connect(controller);
        projectConnection.subscribe(MarkViewListener.TOPIC, this);
        projectConnection.subscribe(SearchTabsViewListener.TOPIC, this);
        projectConnection.subscribe(WriteTipListener.TOPIC, this);
        projectConnection.subscribe(RefreshTimestampsListener.TOPIC, this);
    }

    @Override
    public void mark(final TabController tab, final MarkPanel.Model model) {
        if (controller == tab) {
            ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                @Override
                public void run() {
                    final ClientService client = IdeaSamebugPlugin.getInstance().getClient();
                    final RestHit hit = model.getHit();
                    try {
                        if (hit.getMarkId() == null) client.postMark(model.getSearchId(), hit.getSolutionId());
                        else client.retractMark(hit.getMarkId());
                    } catch (SamebugClientException e) {
                        LOGGER.warn("Failed to execute mark.", e);
                    }
                }
            });
        }
    }

    @Override
    public void reloadActiveSearchTab(@NotNull TabController tab) {
        if (tab == controller) {
            controller.reload();
        }
    }

    @Override
    public void openWriteTip(TabController tab) {
    }

    @Override
    public void cancelWriteTip(TabController tab) {
    }

    @Override
    public void submitTip(final TabController tab, final String tip, final String rawSourceUrl) {
        if (tab == controller) {
            URI tmpSourceUrl = null;
            String errorKey = null;
            if (tip.length() < WriteTip.minCharacters) {
                errorKey = "samebug.tip.write.error.tip.short";
            } else if (tip.length() > WriteTip.maxCharacters) {
                errorKey = "samebug.tip.write.error.tip.long";
            } else if (StringUtils.countMatches(tip, TextUtil.lineSeparator) >= WriteTip.maxLines) {
                errorKey = "samebug.tip.write.error.tip.tooManyLines";
            } else {
                if (rawSourceUrl != null && !rawSourceUrl.trim().isEmpty()) {
                    try {
                        tmpSourceUrl = new URL(rawSourceUrl).toURI();
                    } catch (MalformedURLException e1) {
                        errorKey = "samebug.tip.write.error.source.malformed";
                    } catch (URISyntaxException e) {
                        errorKey = "samebug.tip.write.error.source.malformed";
                    }
                }
            }
            final String sourceUrl = tmpSourceUrl == null ? null : tmpSourceUrl.toString();
            if (errorKey != null) {
                controller.view.finishPostTipWithError(SamebugBundle.message(errorKey));
            } else {
                ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            IdeaSamebugPlugin.getInstance().getClient().postTip(controller.mySearchId, tip, sourceUrl);
                        } catch (SamebugClientException e) {
                            LOGGER.warn("Failed to send tip", e);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void refreshDateLabels() {
        controller.view.refreshDateLabels();
    }
}
