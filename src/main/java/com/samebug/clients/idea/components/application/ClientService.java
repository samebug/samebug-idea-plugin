/**
 * Copyright 2016 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.idea.components.application;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.util.messages.MessageBus;
import com.samebug.clients.idea.messages.client.*;
import com.samebug.clients.idea.messages.model.ConnectionStatusListener;
import com.samebug.clients.search.api.client.*;
import com.samebug.clients.search.api.entities.*;
import com.samebug.clients.search.api.entities.tracking.TrackEvent;
import com.samebug.clients.search.api.exceptions.SamebugClientException;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientService implements ApplicationComponent {
    final MessageBus messageBus = ApplicationManager.getApplication().getMessageBus();
    SamebugClient client;
    AtomicBoolean connected;
    AtomicBoolean authenticated;
    AtomicInteger nRequests;

    public synchronized void configure(final Config config) {
        this.client = new SamebugClient(config);
        this.connected = new AtomicBoolean(true);
        this.authenticated = new AtomicBoolean(true);
        this.nRequests = new AtomicInteger(0);
    }

    public UserInfo getUserInfo(final String apiKey) throws SamebugClientException {
        return new ConnectionAwareHttpRequest<UserInfo>() {
            ClientResponse<UserInfo> request() {
                return client.getUserInfo(apiKey);
            }

            void success(UserInfo result) {
                messageBus.syncPublisher(UserModelListener.TOPIC).successLoadUserInfo(result);
            }

            void fail(SamebugClientException e) {
                messageBus.syncPublisher(UserModelListener.TOPIC).failLoadUserInfo(e);
            }

            void finish() {
                messageBus.syncPublisher(UserModelListener.TOPIC).finishLoadHistory();
            }
        }.execute();
    }

    public SearchResults searchSolutions(final String stacktrace) throws SamebugClientException {
        return new ConnectionAwareHttpRequest<SearchResults>() {
            ClientResponse<SearchResults> request() {
                return client.searchSolutions(stacktrace);
            }
        }.execute();
    }

    public SearchHistory getSearchHistory() throws SamebugClientException {
        return new ConnectionAwareHttpRequest<SearchHistory>() {
            ClientResponse<SearchHistory> request() {
                return client.getSearchHistory();
            }

            void start() {
                messageBus.syncPublisher(HistoryModelListener.TOPIC).startLoadHistory();
            }

            void success(SearchHistory result) {
                messageBus.syncPublisher(HistoryModelListener.TOPIC).successLoadHistory(result);
            }

            void fail(SamebugClientException e) {
                messageBus.syncPublisher(HistoryModelListener.TOPIC).failLoadHistory(e);
            }
        }.execute();
    }

    public Solutions getSolutions(final int searchId) throws SamebugClientException {
        return new ConnectionAwareHttpRequest<Solutions>() {
            ClientResponse<Solutions> request() {
                return client.getSolutions(searchId);
            }

            void start() {
                messageBus.syncPublisher(SearchModelListener.TOPIC).startLoadingSolutions(searchId);
            }

            void success(Solutions result) {
                messageBus.syncPublisher(SearchModelListener.TOPIC).successLoadingSolutions(searchId, result);
            }

            void fail(SamebugClientException e) {
                messageBus.syncPublisher(SearchModelListener.TOPIC).failLoadingSolutions(searchId, e);
            }
        }.execute();
    }

    public RestHit<Tip> postTip(final int searchId, final String tip, final String sourceUrl) throws SamebugClientException {
        return new ConnectionAwareHttpRequest<RestHit<Tip>>() {
            ClientResponse<RestHit<Tip>> request() {
                return client.postTip(searchId, tip, sourceUrl);
            }

            void start() {
                messageBus.syncPublisher(TipModelListener.TOPIC).startPostTip(searchId);
            }

            void success(RestHit<Tip> result) {
                messageBus.syncPublisher(TipModelListener.TOPIC).successPostTip(searchId, result);
            }

            void fail(SamebugClientException e) {
                messageBus.syncPublisher(TipModelListener.TOPIC).failPostTip(searchId, e);
            }
        }.execute();
    }

    public MarkResponse postMark(final int searchId, final int solutionId) throws SamebugClientException {
        return new ConnectionAwareHttpRequest<MarkResponse>() {
            ClientResponse<MarkResponse> request() {
                return client.postMark(searchId, solutionId);
            }

            void start() {
                messageBus.syncPublisher(MarkModelListener.TOPIC).startPostingMark(searchId, solutionId);
            }

            void success(MarkResponse result) {
                messageBus.syncPublisher(MarkModelListener.TOPIC).successPostingMark(searchId, solutionId, result);
            }

            void fail(SamebugClientException e) {
                messageBus.syncPublisher(MarkModelListener.TOPIC).failPostingMark(searchId, solutionId, e);
            }
        }.execute();
    }

    public MarkResponse retractMark(final int voteId) throws SamebugClientException {
        return new ConnectionAwareHttpRequest<MarkResponse>() {
            ClientResponse<MarkResponse> request() {
                return client.retractMark(voteId);
            }

            void start() {
                messageBus.syncPublisher(MarkModelListener.TOPIC).startRetractMark(voteId);
            }

            void success(MarkResponse result) {
                messageBus.syncPublisher(MarkModelListener.TOPIC).successRetractMark(voteId, result);
            }

            void fail(SamebugClientException e) {
                messageBus.syncPublisher(MarkModelListener.TOPIC).failRetractMark(voteId, e);
            }
        }.execute();
    }

    public UserStats getUserStats() throws SamebugClientException {
        return new ConnectionAwareHttpRequest<UserStats>() {
            ClientResponse<UserStats> request() {
                return client.getUserStats();
            }

            void success(UserStats result) {
                messageBus.syncPublisher(UserStatsListener.TOPIC).successGetUserStats(result);
            }

            void fail(SamebugClientException e) {
                messageBus.syncPublisher(UserStatsListener.TOPIC).failGetUserStats(e);
            }
        }.execute();
    }

    public void trace(final TrackEvent event) throws SamebugClientException {
        // Trace bypasses connection status handling.
        client.trace(event);
    }

    public boolean isConnected() {
        return connected.get();
    }

    public boolean isAuthenticated() {
        return authenticated.get();
    }

    public int getNumberOfActiveRequests() {
        return nRequests.get();
    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "";
    }

    private abstract class ConnectionAwareHttpRequest<T> {
        abstract ClientResponse<T> request();

        void start() {
        }

        void success(T result) {
        }

        void fail(SamebugClientException e) {
        }

        void finish(ClientResponse<T> response) {
        }

        T execute() throws SamebugClientException {
            final ClientResponse<T> response;

            start();
            nRequests.incrementAndGet();
            messageBus.syncPublisher(ConnectionStatusListener.TOPIC).startRequest();
            response = request();

            ConnectionStatus connectionStatus = response.getConnectionStatus();
            if (connectionStatus.attemptToConnect && connected.get() != connectionStatus.successfullyConnected) {
                connected.set(connectionStatus.successfullyConnected);
                messageBus.syncPublisher(ConnectionStatusListener.TOPIC).connectionChange(connectionStatus.successfullyConnected);
            }
            if (connectionStatus.attemptToAuthenticate && authenticated.get() != connectionStatus.successfullyAuthenticated) {
                authenticated.set(connectionStatus.successfullyAuthenticated);
                messageBus.syncPublisher(ConnectionStatusListener.TOPIC).authenticationChange(connectionStatus.successfullyAuthenticated);
            }
            if (connectionStatus.apiStatus != null) {
                messageBus.syncPublisher(ConnectionStatusListener.TOPIC).apiStatusChange(connectionStatus.apiStatus);
            }

            try {
                if (response instanceof Success) {
                    T result = ((Success<T>) response).getResponse();
                    success(result);
                    return result;
                } else {
                    SamebugClientException exception = ((Failure<T>) response).getException();
                    fail(exception);
                    throw exception;
                }
            } finally {
                nRequests.decrementAndGet();
                messageBus.syncPublisher(ConnectionStatusListener.TOPIC).finishRequest(connectionStatus);
                finish(response);
            }
        }
    }

}
