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
import com.samebug.clients.idea.messages.client.HistoryModelListener;
import com.samebug.clients.idea.messages.client.MarkModelListener;
import com.samebug.clients.idea.messages.client.SearchModelListener;
import com.samebug.clients.idea.messages.client.TipModelListener;
import com.samebug.clients.idea.messages.model.ConnectionStatusListener;
import com.samebug.clients.search.api.SamebugClient;
import com.samebug.clients.search.api.entities.*;
import com.samebug.clients.search.api.entities.tracking.TrackEvent;
import com.samebug.clients.search.api.exceptions.*;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientService implements ApplicationComponent {
    final MessageBus messageBus = ApplicationManager.getApplication().getMessageBus();
    SamebugClient client;
    AtomicBoolean connected;
    AtomicBoolean authenticated;
    AtomicInteger nRequests;

    public synchronized void configure(final SamebugClient.Config config) {
        this.client = new SamebugClient(config);
        this.connected = new AtomicBoolean(true);
        this.authenticated = new AtomicBoolean(true);
        this.nRequests = new AtomicInteger(0);
    }

    public SearchResults searchSolutions(final String stacktrace) throws SamebugClientException {
        // TODO notify somebody so we can update the history?
        return new ConnectionAwareHttpRequest<SearchResults>() {
            SearchResults request() throws SamebugClientException {
                return client.searchSolutions(stacktrace);
            }
        }.execute();
    }

    public UserInfo getUserInfo(final String apiKey) throws SamebugClientException {
        return new ConnectionAwareHttpRequest<UserInfo>() {
            UserInfo request() throws SamebugClientException {
                return client.getUserInfo(apiKey);
            }
        }.executeUnauthenticated();
    }

    public SearchHistory getSearchHistory() throws SamebugClientException {
        return new ConnectionAwareHttpRequest<SearchHistory>() {
            SearchHistory request() throws SamebugClientException {
                return client.getSearchHistory();
            }

            void start() {
                ApplicationManager.getApplication().getMessageBus().syncPublisher(HistoryModelListener.TOPIC).startLoadHistory();
            }

            void success(SearchHistory result) {
                ApplicationManager.getApplication().getMessageBus().syncPublisher(HistoryModelListener.TOPIC).successLoadHistory(result);
            }

            void fail(java.lang.Exception e) {
                ApplicationManager.getApplication().getMessageBus().syncPublisher(HistoryModelListener.TOPIC).failLoadHistory(e);
            }
        }.execute();
    }

    public Solutions getSolutions(final int searchId) throws SamebugClientException {
        return new ConnectionAwareHttpRequest<Solutions>() {
            Solutions request() throws SamebugClientException {
                return client.getSolutions(searchId);
            }

            void start() {
                ApplicationManager.getApplication().getMessageBus().syncPublisher(SearchModelListener.TOPIC).startLoadingSolutions(searchId);
            }

            void success(Solutions result) {
                ApplicationManager.getApplication().getMessageBus().syncPublisher(SearchModelListener.TOPIC).successLoadingSolutions(searchId, result);
            }

            void fail(java.lang.Exception e) {
                ApplicationManager.getApplication().getMessageBus().syncPublisher(SearchModelListener.TOPIC).failLoadingSolutions(searchId, e);
            }
        }.execute();
    }

    public RestHit<Tip> postTip(final int searchId, final String tip, final URL sourceUrl) throws SamebugClientException {
        return new ConnectionAwareHttpRequest<RestHit<Tip>>() {
            RestHit<Tip> request() throws SamebugClientException {
                return client.postTip(searchId, tip, sourceUrl);
            }

            void start() {
                ApplicationManager.getApplication().getMessageBus().syncPublisher(TipModelListener.TOPIC).startPostTip(searchId);
            }

            void success(RestHit<Tip> result) {
                ApplicationManager.getApplication().getMessageBus().syncPublisher(TipModelListener.TOPIC).successPostTip(searchId, result);
            }

            void fail(java.lang.Exception e) {
                ApplicationManager.getApplication().getMessageBus().syncPublisher(TipModelListener.TOPIC).failPostTip(searchId, e);
            }
        }.execute();
    }

    public MarkResponse postMark(final int searchId, final int solutionId) throws SamebugClientException {
        return new ConnectionAwareHttpRequest<MarkResponse>() {
            MarkResponse request() throws SamebugClientException {
                return client.postMark(searchId, solutionId);
            }

            void start() {
                ApplicationManager.getApplication().getMessageBus().syncPublisher(MarkModelListener.TOPIC).startPostingMark(searchId, solutionId);
            }

            void success(MarkResponse result) {
                ApplicationManager.getApplication().getMessageBus().syncPublisher(MarkModelListener.TOPIC).successPostingMark(searchId, solutionId, result);
            }

            void fail(java.lang.Exception e) {
                ApplicationManager.getApplication().getMessageBus().syncPublisher(MarkModelListener.TOPIC).failPostingMark(searchId, solutionId, e);
            }
        }.execute();
    }

    public MarkResponse retractMark(final int voteId) throws SamebugClientException {
        return new ConnectionAwareHttpRequest<MarkResponse>() {
            MarkResponse request() throws SamebugClientException {
                return client.retractMark(voteId);
            }

            void start() {
                ApplicationManager.getApplication().getMessageBus().syncPublisher(MarkModelListener.TOPIC).startRetractMark(voteId);
            }

            void success(MarkResponse result) {
                ApplicationManager.getApplication().getMessageBus().syncPublisher(MarkModelListener.TOPIC).successRetractMark(voteId, result);
            }

            void fail(java.lang.Exception e) {
                ApplicationManager.getApplication().getMessageBus().syncPublisher(MarkModelListener.TOPIC).failRetractMark(voteId, e);
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
        abstract T request() throws SamebugClientException;

        void start() {
        }

        void success(T result) {
        }

        void fail(java.lang.Exception e) {
        }

        void finish() {
        }

        public T execute() throws SamebugClientException {
            return execute(true);
        }

        public T executeUnauthenticated() throws SamebugClientException {
            return execute(false);
        }

        private T execute(boolean isAuthenticationRequired) throws SamebugClientException {
            start();
            try {
                nRequests.incrementAndGet();
                messageBus.syncPublisher(ConnectionStatusListener.TOPIC).startRequest();
                T result = request();
                connected.set(true);
                if (isAuthenticationRequired) authenticated.set(true);
                success(result);
                return result;
            } catch (SamebugTimeout e) {
                connected.set(false);
                fail(e);
                throw e;
            } catch (RemoteError e) {
                connected.set(false);
                fail(e);
                throw e;
            } catch (HttpError e) {
                connected.set(false);
                fail(e);
                throw e;
            } catch (BadRequest e) {
                connected.set(true);
                if (isAuthenticationRequired) authenticated.set(true);
                fail(e);
                throw e;
            } catch (UserUnauthenticated e) {
                connected.set(true);
                authenticated.set(false);
                fail(e);
                throw e;
            } catch (UserUnauthorized e) {
                connected.set(true);
                authenticated.set(false);
                fail(e);
                throw e;
            } catch (UnsuccessfulResponseStatus e) {
                connected.set(true);
                fail(e);
                throw e;
            } finally {
                nRequests.decrementAndGet();
                messageBus.syncPublisher(ConnectionStatusListener.TOPIC).finishRequest(connected.get());
                finish();
            }
        }
    }

}
