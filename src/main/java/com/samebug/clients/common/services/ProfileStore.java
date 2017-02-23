package com.samebug.clients.common.services;

import com.samebug.clients.common.search.api.entities.UserInfo;
import com.samebug.clients.common.search.api.entities.UserStats;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;

public final class ProfileStore {
    @NotNull
    final AtomicReference<UserInfo> user;

    @NotNull
    final AtomicReference<UserStats> statistics;

    public ProfileStore() {
        this.user = new AtomicReference<UserInfo>();
        this.statistics = new AtomicReference<UserStats>();
    }

    @Nullable
    public UserInfo getUser() {
        return user.get();
    }

    @Nullable
    public UserStats getUserStats() {
        return statistics.get();
    }
}
