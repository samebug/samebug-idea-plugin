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
package com.samebug.clients.idea.components.application;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.idea.messages.tracking.TrackingListener;
import com.samebug.clients.search.api.entities.tracking.TrackEvent;
import com.samebug.clients.search.api.exceptions.SamebugClientException;
import org.jetbrains.annotations.NotNull;

public class Tracking implements ApplicationComponent, TrackingListener {
    public void trace(final TrackEvent event) {
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                try {
                    ClientService client = IdeaSamebugPlugin.getInstance().getClient();
                    client.trace(event);
                } catch (SamebugClientException e) {
                    LOGGER.debug("Failed to send a track event to server", e);
                } catch (Exception e) {
                    LOGGER.warn("Error while tracking", e);
                }
            }
        });
    }

    public static TrackingListener appTracking() {
        try {
            return ApplicationManager.getApplication().getMessageBus().syncPublisher(TrackingListener.TOPIC);
        } catch (Throwable e) {
            // Likely we were called asynchronously, and the message bus is already closed. Np, it's just tracking.
            LOGGER.warn("Failed to get tracking listener, track event will be lost", e);
            return new NopTracking();
        }
    }

    public static TrackingListener projectTracking(Project project) {
        try {
            return project.getMessageBus().syncPublisher(TrackingListener.TOPIC);
        } catch (Throwable e) {
            LOGGER.warn("Failed to get tracking listener, track event will be lost", e);
            return new NopTracking();
        }
    }

    @Override
    public void initComponent() {
        messageBusConnection = ApplicationManager.getApplication().getMessageBus().connect();
        messageBusConnection.subscribe(TrackingListener.TOPIC, this);
    }

    @Override
    public void disposeComponent() {
        messageBusConnection.disconnect();
    }

    @NotNull
    @Override
    public String getComponentName() {
        return getClass().getSimpleName();
    }

    private MessageBusConnection messageBusConnection;
    private final static Logger LOGGER = Logger.getInstance(Tracking.class);
}

/**
 * Mock implementation of a TrackingListener
 * <p/>
 * This implementation does nothing.
 * It exists simply to avoid NPEs when the tracking message bus was closed, and we want to ignore the requested tracking event.
 */
class NopTracking implements TrackingListener {
    @Override
    public void trace(TrackEvent event) {
    }
}
