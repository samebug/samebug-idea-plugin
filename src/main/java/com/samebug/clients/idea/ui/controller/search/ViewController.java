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
import com.samebug.clients.idea.messages.view.SearchGroupCardListener;
import com.samebug.clients.idea.messages.view.SearchTabsViewListener;
import com.samebug.clients.idea.messages.view.WriteTipListener;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.BrowserUtil;
import com.samebug.clients.idea.ui.component.WriteTip;
import com.samebug.clients.idea.ui.component.organism.MarkPanel;
import com.samebug.clients.idea.ui.controller.TabController;
import com.samebug.clients.search.api.entities.RestHit;
import com.samebug.clients.search.api.entities.SearchGroup;
import com.samebug.clients.search.api.exceptions.SamebugClientException;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;

final class ViewController
        implements SearchGroupCardListener, MarkViewListener, SearchTabsViewListener, WriteTipListener {
    final static Logger LOGGER = Logger.getInstance(ViewController.class);
    @NotNull
    final SearchTabController controller;

    public ViewController(@NotNull final SearchTabController controller) {
        this.controller = controller;

        MessageBusConnection projectMessageBus = controller.project.getMessageBus().connect(controller);
        projectMessageBus.subscribe(SearchGroupCardListener.TOPIC, this);
        projectMessageBus.subscribe(MarkViewListener.TOPIC, this);
        projectMessageBus.subscribe(SearchTabsViewListener.TOPIC, this);
        projectMessageBus.subscribe(WriteTipListener.TOPIC, this);
    }

    @Override
    public void titleClick(@NotNull TabController tab, SearchGroup searchGroup) {
        if (controller == tab) {
            BrowserUtil.browse(IdeaSamebugPlugin.getInstance().getUrlBuilder().search(searchGroup.getLastSearch().id));
        }
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
                        if (hit.markId == null) client.postMark(model.getSearchId(), hit.solutionId);
                        else client.retractMark(hit.markId);
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
    public void ctaClick(TabController tab) {
        if (tab == controller) {
            controller.view.showWriteTip();
        }
    }

    @Override
    public void cancelClick(TabController tab) {
        if (tab == controller) {
            controller.view.showWriteTipHint();
        }
    }

    @Override
    public void sendClick(final TabController tab, final String tip, final String rawSourceUrl) {
        if (tab == controller) {
            URL tmpSourceUrl = null;
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
                        tmpSourceUrl = new URL(rawSourceUrl);
                    } catch (MalformedURLException e1) {
                        errorKey = "samebug.tip.write.error.source.malformed";
                    }
                }
            }
            final URL sourceUrl = tmpSourceUrl;
            if (errorKey != null) {
                controller.view.tipPanel.finishPostTipWithError(SamebugBundle.message(errorKey));
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
}
