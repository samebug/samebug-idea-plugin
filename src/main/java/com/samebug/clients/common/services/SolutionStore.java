package com.samebug.clients.common.services;

import com.samebug.clients.common.search.api.entities.Solutions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SolutionStore {
    final Map<Integer, Solutions> solutions;

    public SolutionStore() {
        this.solutions = new ConcurrentHashMap<Integer, Solutions>();
    }

}
