package com.samebug.clients.idea.components.application;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.idea.messages.TrackingListener;
import com.samebug.clients.search.api.SamebugClient;
import com.samebug.clients.search.api.entities.tracking.TrackEvent;
import com.samebug.clients.search.api.exceptions.SamebugClientException;
import org.jetbrains.annotations.NotNull;

/**
 * Created by poroszd on 2/18/16.
 */
public class Tracking implements ApplicationComponent, TrackingListener {
    public void trace(final TrackEvent event) {
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                SamebugClient client = IdeaSamebugPlugin.getInstance().getClient();
                try {
                    client.trace(event);
                } catch (SamebugClientException e) {
                    LOGGER.debug("Failed to send a track event to server", e);
                }
            }
        });
    }

    @Override
    public void initComponent() {
        messageBusConnection = ApplicationManager.getApplication().getMessageBus().connect();
        messageBusConnection.subscribe(TrackingListener.TRACK_TOPIC, this);
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
