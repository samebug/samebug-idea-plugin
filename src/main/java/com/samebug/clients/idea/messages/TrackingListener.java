package com.samebug.clients.idea.messages;

import com.intellij.util.messages.Topic;
import com.samebug.clients.search.api.entities.tracking.TrackEvent;

/**
 * Created by poroszd on 2/18/16.
 */
public interface TrackingListener {
    Topic<TrackingListener> TRACK_TOPIC = Topic.create("tracking", TrackingListener.class, Topic.BroadcastDirection.TO_PARENT);

    void trace(TrackEvent event);

}
