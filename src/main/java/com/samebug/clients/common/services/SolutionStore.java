/*
 * Copyright 2017 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *    http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.common.services;

import com.samebug.clients.http.entities.jsonapi.BugmateList;
import com.samebug.clients.http.entities.jsonapi.SolutionList;
import com.samebug.clients.http.entities.jsonapi.TipList;
import com.samebug.clients.http.entities.search.SearchHit;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SolutionStore {
    final Map<Integer, SolutionList> externalSolutions;
    final Map<Integer, TipList> tips;
    final Map<Integer, BugmateList> bugmates;

    public SolutionStore() {
        this.externalSolutions = new ConcurrentHashMap<Integer, SolutionList>();
        this.tips = new ConcurrentHashMap<Integer, TipList>();
        this.bugmates = new ConcurrentHashMap<Integer, BugmateList>();
    }

    public SolutionList getWebHits(int searchId) {
        return externalSolutions.get(searchId);
    }

    @Nullable
    public SearchHit getHit(int searchId, int solutionId) {
        SearchHit result = null;
        SolutionList list = externalSolutions.get(searchId);
        if (list != null) {
            for (SearchHit h : list.getData()) {
                if (h.getSolution().getId() == solutionId) result = h;
            }
        }
        TipList tipsList = tips.get(searchId);
        if (list != null) {
            for (SearchHit h : tipsList.getData()) {
                if (h.getSolution().getId() == solutionId) result = h;
            }
        }
        return result;
    }

    public TipList getTipHits(int searchId) {
        return tips.get(searchId);
    }

    public BugmateList getBugmates(int searchId) {
        return bugmates.get(searchId);
    }
}
