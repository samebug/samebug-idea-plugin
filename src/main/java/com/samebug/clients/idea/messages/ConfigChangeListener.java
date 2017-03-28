package com.samebug.clients.idea.messages;

import com.intellij.util.messages.Topic;
import com.samebug.clients.idea.components.application.ApplicationSettings;

public interface ConfigChangeListener {
    Topic<ConfigChangeListener> TOPIC = Topic.create("config change", ConfigChangeListener.class);

    void configChange(ApplicationSettings oldSettings, ApplicationSettings newSettings);
}
