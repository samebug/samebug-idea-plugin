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
package com.samebug.clients.idea.ui.modules;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.Topic;
import com.samebug.clients.common.ui.component.bugmate.IBugmateHit;
import com.samebug.clients.common.ui.component.bugmate.IBugmateList;
import com.samebug.clients.common.ui.component.community.IHelpOthersCTA;
import com.samebug.clients.common.ui.component.helpRequest.IHelpRequestPreview;
import com.samebug.clients.common.ui.component.hit.IMarkButton;
import com.samebug.clients.common.ui.component.hit.IWebHit;
import com.samebug.clients.common.ui.component.popup.IHelpRequestPopup;
import com.samebug.clients.common.ui.component.profile.IProfilePanel;
import com.samebug.clients.common.ui.frame.IAuthenticationFrame;
import com.samebug.clients.common.ui.frame.IIntroFrame;
import com.samebug.clients.common.ui.frame.helpRequest.IHelpRequestFrame;
import com.samebug.clients.common.ui.frame.helpRequest.IHelpRequestHeader;
import com.samebug.clients.common.ui.frame.helpRequest.IHelpRequestTab;
import com.samebug.clients.common.ui.frame.helpRequestList.IHelpRequestList;
import com.samebug.clients.common.ui.frame.helpRequestList.IHelpRequestListFrame;
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
    public static final Topic<IHelpRequestPreview.Listener> HelpRequestPreview = Topic.create("IHelpRequestPreview", IHelpRequestPreview.Listener.class);
    public static final Topic<IHelpRequestPopup.Listener> HelpRequestPopup = Topic.create("IHelpRequestPopup", IHelpRequestPopup.Listener.class);
    public static final Topic<IAuthenticationFrame.Listener> AuthenticationFrame = Topic.create("IAuthenticationFrame", IAuthenticationFrame.Listener.class);
    public static final Topic<IHelpRequestList.Listener> HelpRequestList = Topic.create("IHelpRequestList", IHelpRequestList.Listener.class);
    public static final Topic<IHelpRequestListFrame.Listener> HelpRequestListFrame = Topic.create("IHelpRequestListFrame", IHelpRequestListFrame.Listener.class);
    public static final Topic<IHelpRequestTab.Listener> HelpRequestTab = Topic.create("IHelpRequestTab", IHelpRequestTab.Listener.class);
    public static final Topic<IHelpRequestHeader.Listener> HelpRequestHeader = Topic.create("IHelpRequestHeader", IHelpRequestHeader.Listener.class);
    public static final Topic<IHelpRequestFrame.Listener> HelpRequestFrame = Topic.create("IHelpRequestFrame", IHelpRequestFrame.Listener.class);
    public static final Topic<IIntroFrame.Listener> IntroFrame = Topic.create("IIntroFrame", IIntroFrame.Listener.class);

    private static final Topic[] topics = {
            ProfilePanel,
            BugmateHit,
            BugmateList,
            ExceptionHeaderPanel,
            MarkButton,
            HelpOthersCTA,
            SolutionFrame,
            WebHit,
            WebResultsTab,
            HelpRequestPreview,
            HelpRequestPopup,
            AuthenticationFrame,
            HelpRequestList,
            HelpRequestListFrame,
            HelpRequestTab,
            HelpRequestHeader,
            HelpRequestFrame,
            IntroFrame
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
