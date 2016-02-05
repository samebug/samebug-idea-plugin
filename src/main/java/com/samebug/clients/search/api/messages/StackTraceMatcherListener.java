package com.samebug.clients.search.api.messages;

import com.intellij.openapi.project.Project;
import com.intellij.util.messages.Topic;

/**
 * Created by poroszd on 2/5/16.
 */
public interface StackTraceMatcherListener {
    Topic<StackTraceMatcherListener> FOUND_TOPIC = Topic.create("stacktrace found", StackTraceMatcherListener.class, Topic.BroadcastDirection.TO_PARENT);

    void stackTraceFound(Project project, String stackTrace);
}
