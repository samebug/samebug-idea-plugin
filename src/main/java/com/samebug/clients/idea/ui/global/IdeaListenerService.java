package com.samebug.clients.idea.ui.global;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.Topic;
import com.samebug.clients.common.ui.component.profile.IProfilePanel;
import com.samebug.clients.swing.ui.global.DataService;
import com.samebug.clients.swing.ui.global.ListenerService;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;


public final class IdeaListenerService extends ListenerService {
    final static Logger LOGGER = Logger.getInstance(IdeaListenerService.class);

    public static final Topic<IProfilePanel.Listener> PROFILE_PANEL = Topic.create("", IProfilePanel.Listener.class);

    private static final Topic[] topics = {
            PROFILE_PANEL
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
