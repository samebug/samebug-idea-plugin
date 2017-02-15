package com.samebug.clients.common.services;

import com.samebug.clients.common.search.api.entities.BugmatesResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class BugmateStore {
    final Map<Integer, BugmatesResult> bugmates;

    public BugmateStore() {
        this.bugmates = new ConcurrentHashMap<Integer, BugmatesResult>();
    }
}
