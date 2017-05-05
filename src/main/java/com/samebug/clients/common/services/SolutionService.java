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

import com.samebug.clients.http.client.SamebugClient;
import com.samebug.clients.http.entities.jsonapi.BugmateList;
import com.samebug.clients.http.entities.jsonapi.SolutionList;
import com.samebug.clients.http.entities.jsonapi.TipList;
import com.samebug.clients.http.entities.mark.Mark;
import com.samebug.clients.http.entities.mark.NewMark;
import com.samebug.clients.http.entities.search.NewSearchHit;
import com.samebug.clients.http.entities.search.SearchHit;
import com.samebug.clients.http.entities.solution.SamebugTip;
import com.samebug.clients.http.exceptions.SamebugClientException;
import com.samebug.clients.http.form.MarkCancel;
import com.samebug.clients.http.form.MarkCreate;
import com.samebug.clients.http.form.TipCreate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SolutionService {
    final SamebugClient client;
    final SolutionStore solutionStore;

    public SolutionService(SamebugClient client, SolutionStore solutionStore) {
        this.client = client;
        this.solutionStore = solutionStore;
    }

    public SolutionList loadWebHits(final int searchId) throws SamebugClientException {
        SolutionList result = client.getSolutions(searchId);
        solutionStore.externalSolutions.put(searchId, result);
        return result;
    }

    public TipList loadTipHits(final int searchId) throws SamebugClientException {
        TipList result = client.getTips(searchId);
        solutionStore.tips.put(searchId, result);
        return result;
    }

    public BugmateList loadBugmates(final int searchId) throws SamebugClientException {
        BugmateList result = client.getBugmates(searchId);
        solutionStore.bugmates.put(searchId, result);
        return result;
    }

    public SearchHit<SamebugTip> postTip(@NotNull final Integer searchId, @NotNull final NewSearchHit data)
            throws SamebugClientException, TipCreate.BadRequest {
        SearchHit<SamebugTip> response = client.createTip(searchId, data);
        solutionStore.tips.get(searchId).getData().add(0, null);
        return response;
    }

    @Nullable
    public SearchHit postMark(@NotNull final Integer searchId, @NotNull final NewMark data) throws SamebugClientException, MarkCreate.BadRequest {
        Mark mark = client.postMark(searchId, data);
        SearchHit hit = solutionStore.getHit(searchId, mark.getSolutionId());
        if (hit != null) hit.setActiveMark(mark);
        return hit;
    }

    public SearchHit retractMark(@NotNull final Integer searchId, @NotNull final Integer voteId) throws SamebugClientException, MarkCancel.BadRequest {
        Mark mark = client.cancelMark(voteId);
        SearchHit hit = solutionStore.getHit(searchId, mark.getSolutionId());
        if (hit != null) hit.setActiveMark(null);
        return hit;
    }
}
