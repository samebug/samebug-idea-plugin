/**
 * Copyright 2017 Samebug, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.common.services;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.idea.messages.HistoryModelListener;
import com.samebug.clients.common.search.api.client.ClientResponse;
import com.samebug.clients.common.search.api.client.SamebugClient;
import com.samebug.clients.common.search.api.entities.SearchGroup;
import com.samebug.clients.common.search.api.entities.SearchHistory;
import com.samebug.clients.common.search.api.exceptions.SamebugClientException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

final public class HistoryService {
    @NotNull
    final MessageBus messageBus;
    @NotNull
    final ClientService clientService;

    AtomicReference<SearchHistory> history;

    public HistoryService(@NotNull MessageBus messageBus, @NotNull ClientService clientService) {
        this.messageBus = messageBus;
        this.clientService = clientService;
        history = new AtomicReference<SearchHistory>(null);
    }

    @Nullable
    public List<SearchGroup> getHistory() {
        @Nullable SearchHistory currentHistory = history.get();
        if (currentHistory == null) return null;
        else return currentHistory.getSearchGroups();
    }

    public SearchHistory loadSearchHistory() throws SamebugClientException {
        final SamebugClient client = clientService.client;

        ClientService.ConnectionAwareHttpRequest<SearchHistory> requestHandler =
                new ClientService.ConnectionAwareHttpRequest<SearchHistory>() {
                    ClientResponse<SearchHistory> request() {
                        return client.getSearchHistory();
                    }

                    protected void start() {
                        messageBus.syncPublisher(HistoryModelListener.TOPIC).startLoadHistory();
                    }

                    protected void success(SearchHistory result) {
                        messageBus.syncPublisher(HistoryModelListener.TOPIC).successLoadHistory(result);
                    }

                    protected void fail(SamebugClientException e) {
                        messageBus.syncPublisher(HistoryModelListener.TOPIC).failLoadHistory(e);
                    }
                };
        return clientService.execute(requestHandler);
    }

}
