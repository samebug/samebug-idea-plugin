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
package com.samebug.clients.idea.ui.modules;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.Topic;
import com.samebug.clients.common.ui.component.bugmate.IBugmateHit;
import com.samebug.clients.common.ui.component.bugmate.IBugmateList;
import com.samebug.clients.common.ui.component.community.IHelpOthersCTA;
import com.samebug.clients.common.ui.component.hit.IMarkButton;
import com.samebug.clients.common.ui.component.hit.IWebHit;
import com.samebug.clients.common.ui.component.profile.IProfilePanel;
import com.samebug.clients.common.ui.frame.solution.ISearchHeaderPanel;
import com.samebug.clients.common.ui.frame.solution.ISolutionFrame;
import com.samebug.clients.common.ui.frame.solution.IWebResultsTab;
import com.samebug.clients.swing.ui.modules.DataService;
import com.samebug.clients.swing.ui.modules.ListenerService;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;


public final class IdeaListenerService extends ListenerService {
    final static Logger LOGGER = Logger.getInstance(IdeaListenerService.class);

    public static final Topic<IProfilePanel.Listener> ProfilePanel = Topic.create("IProfilePanel", IProfilePanel.Listener.class);
    public static final Topic<IBugmateHit.Listener> BugmateHit = Topic.create("IBugmateHit", IBugmateHit.Listener.class);
    public static final Topic<IBugmateList.Listener> BugmateList = Topic.create("IBugmateList", IBugmateList.Listener.class);
    public static final Topic<ISearchHeaderPanel.Listener> ExceptionHeaderPanel = Topic.create("IExceptionHeaderPanel", ISearchHeaderPanel.Listener.class);
    public static final Topic<IMarkButton.Listener> MarkButton = Topic.create("IMarkButton", IMarkButton.Listener.class);
    public static final Topic<IHelpOthersCTA.Listener> HelpOthersCTA = Topic.create("IHelpOthersCTA", IHelpOthersCTA.Listener.class);
    public static final Topic<ISolutionFrame.Listener> SolutionFrame = Topic.create("ISolutionFrame", ISolutionFrame.Listener.class);
    public static final Topic<IWebHit.Listener> WebHit = Topic.create("IWebHit", IWebHit.Listener.class);
    public static final Topic<IWebResultsTab.Listener> WebResultsTab = Topic.create("IWebResultsTab", IWebResultsTab.Listener.class);

    private static final Topic[] topics = {
            ProfilePanel,
            BugmateHit,
            BugmateList,
            ExceptionHeaderPanel,
            MarkButton,
            HelpOthersCTA,
            SolutionFrame,
            WebHit,
            WebResultsTab
    };

    private static final Map<Class, Topic> topicMap = new HashMap<Class, Topic>();

    static {
        for (Topic t : topics) {
            topicMap.put(t.getListenerClass(), t);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T> T internalGetListener(JComponent component, Class<T> listenerClass) {
        Project contextProject = DataService.getData(component, IdeaDataService.Project);
        if (contextProject != null) {
            MessageBus messageBus = contextProject.getMessageBus();
            Topic topic = topicMap.get(listenerClass);
            return (T) messageBus.syncPublisher(topic);
        } else {
            LOGGER.warn("Failed to create listener for " + listenerClass + " as context project of component " + component + " was null!");
            throw new IllegalArgumentException("Component does not have project!");
        }
    }
}
