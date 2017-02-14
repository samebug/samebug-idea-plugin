package com.samebug.clients.common.services;

import com.samebug.clients.common.entities.user.Statistics;
import com.samebug.clients.common.entities.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;

public final class ProfileStore {
    final AtomicReference<Integer> userId;
    final AtomicReference<Integer> workspaceId;

    @NotNull
    final AtomicReference<User> user;

    @NotNull
    final AtomicReference<Statistics> statistics;

    public ProfileStore() {
        this.user = new AtomicReference<User>();
        this.statistics = new AtomicReference<Statistics>();
        this.userId = new AtomicReference<Integer>();
        this.workspaceId = new AtomicReference<Integer>();
    }

    @Nullable
    public User getUser() {
        return user.get();
    }

    @Nullable
    public Statistics getUserStats() {
        return statistics.get();
    }

    // TODO Settings dialog should call this before it tries to authenticate
    public void changeUserSettings(@Nullable Integer userId, @Nullable Integer workspaceId) {
        this.userId.set(userId);
        this.workspaceId.set(workspaceId);
    }

}
