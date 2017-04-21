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
import com.samebug.clients.http.entities.solution.MarkResponse;
import com.samebug.clients.http.entities.solution.RestHit;
import com.samebug.clients.http.entities.solution.Solutions;
import com.samebug.clients.http.entities.solution.Tip;
import com.samebug.clients.http.exceptions.SamebugClientException;
import com.samebug.clients.http.form.CancelMark;
import com.samebug.clients.http.form.CreateMark;
import com.samebug.clients.http.form.CreateTip;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SolutionService {
    final SamebugClient client;
    final SolutionStore solutionStore;

    public SolutionService(SamebugClient client, SolutionStore solutionStore) {
        this.client = client;
        this.solutionStore = solutionStore;
    }

    public Solutions loadSolutions(final int searchId) throws SamebugClientException {
        // TODO
        Solutions result = null; client.getSolutions(searchId);
        solutionStore.solutions.put(searchId, result);
        return result;
    }

    public RestHit<Tip> postTip(@NotNull final int searchId, @NotNull final String tip, @Nullable final String sourceUrl, @Nullable final String helpRequestId)
            throws SamebugClientException, CreateTip.BadRequest {
        RestHit<Tip> response = client.createTip(searchId, tip, sourceUrl, helpRequestId);
        solutionStore.solutions.get(searchId).getTips().add(0, response);
        return response;
    }

    public MarkResponse postMark(final int searchId, final int solutionId) throws SamebugClientException, CreateMark.BadRequest {
        return client.postMark(searchId, solutionId);
    }

    public MarkResponse retractMark(final int voteId) throws SamebugClientException, CancelMark.BadRequest {
        return client.retractMark(voteId);
    }
}
