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

import com.samebug.clients.http.entities.response.GetBugmates;
import com.samebug.clients.http.entities.response.GetSolutions;
import com.samebug.clients.http.entities.response.GetTips;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SolutionStore {
    final Map<Integer, GetSolutions> externalSolutions;
    final Map<Integer, GetTips> tips;
    final Map<Integer, GetBugmates> bugmates;

    public SolutionStore() {
        this.externalSolutions = new ConcurrentHashMap<Integer, GetSolutions>();
        this.tips = new ConcurrentHashMap<Integer, GetTips>();
        this.bugmates = new ConcurrentHashMap<Integer, GetBugmates>();
    }

    public GetSolutions getWebHits(int searchId) {
        return externalSolutions.get(searchId);
    }

    public GetTips getTipHits(int searchId) {
        return tips.get(searchId);
    }

    public GetBugmates getBugmates(int searchId) {
        return bugmates.get(searchId);
    }

}
