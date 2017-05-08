package com.samebug.clients.idea.messages;

import com.intellij.util.messages.Topic;
import com.samebug.clients.http.entities.profile.UserStats;

public interface ProfileUpdate {
    Topic<ProfileUpdate> TOPIC = Topic.create("profile update", ProfileUpdate.class);

    void updateProfileStatistics(UserStats stats);
}
