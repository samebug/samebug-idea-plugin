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
import com.intellij.util.messages.MessageBus;
import com.samebug.clients.idea.messages.ConnectionStatusListener;
import com.samebug.clients.search.api.SamebugClient;
import com.samebug.clients.search.api.entities.GroupedHistory;
import com.samebug.clients.search.api.entities.MarkResponse;
import com.samebug.clients.search.api.entities.SearchResults;
import com.samebug.clients.search.api.entities.UserInfo;
import com.samebug.clients.search.api.entities.legacy.RestHit;
import com.samebug.clients.search.api.entities.legacy.Solutions;
import com.samebug.clients.search.api.entities.legacy.Tip;
import com.samebug.clients.search.api.entities.tracking.TrackEvent;
import com.samebug.clients.search.api.exceptions.*;

import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class IdeaClientService {
    private final SamebugClient client;
    private final MessageBus messageBus = ApplicationManager.getApplication().getMessageBus();
    private AtomicBoolean connected;
    private AtomicBoolean authenticated;
    private AtomicInteger nRequests;

    public IdeaClientService(final SamebugClient.Config config) {
        this.client = new SamebugClient(config);
        // Optimist initialization. If incorrect, that'll turn out soon
        this.connected = new AtomicBoolean(true);
        this.authenticated = new AtomicBoolean(true);
        this.nRequests = new AtomicInteger(0);
    }

    public SearchResults searchSolutions(final String stacktrace) throws SamebugClientException {
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

    public GroupedHistory getSearchHistory() throws SamebugClientException {
        return new ConnectionAwareHttpRequest<GroupedHistory>() {
            GroupedHistory request() throws SamebugClientException {
                return client.getSearchHistory();
            }
        }.execute();
    }

    public Solutions getSolutions(final int searchId) throws SamebugClientException {
        return new ConnectionAwareHttpRequest<Solutions>() {
            Solutions request() throws SamebugClientException {
                return client.getSolutions(searchId);
            }
        }.execute();
    }

    public RestHit<Tip> postTip(final int searchId, final String tip, final URL sourceUrl) throws SamebugClientException {
        return new ConnectionAwareHttpRequest<RestHit<Tip>>() {
            RestHit<Tip> request() throws SamebugClientException {
                return client.postTip(searchId, tip, sourceUrl);
            }
        }.execute();
    }

    public MarkResponse postMark(final int searchId, final int solutionId) throws SamebugClientException {
        return new ConnectionAwareHttpRequest<MarkResponse>() {
            MarkResponse request() throws SamebugClientException {
                return client.postMark(searchId, solutionId);
            }
        }.execute();
    }

    public MarkResponse retractMark(final int voteId) throws SamebugClientException {
        return new ConnectionAwareHttpRequest<MarkResponse>() {
            MarkResponse request() throws SamebugClientException {
                return client.retractMark(voteId);
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

    private abstract class ConnectionAwareHttpRequest<T> {
        abstract T request() throws SamebugClientException;

        public T execute() throws SamebugClientException {
            return execute(true);
        }

        public T executeUnauthenticated() throws SamebugClientException {
            return execute(false);
        }

        private T execute(boolean isAuthenticationRequired) throws SamebugClientException {
            try {
                nRequests.incrementAndGet();
                messageBus.syncPublisher(ConnectionStatusListener.CONNECTION_STATUS_TOPIC).startRequest();
                T result = request();
                connected.set(true);
                if (isAuthenticationRequired) authenticated.set(true);
                return result;
            } catch (SamebugTimeout e) {
                connected.set(false);
                throw e;
            } catch (RemoteError e) {
                connected.set(false);
                throw e;
            } catch (HttpError e) {
                connected.set(false);
                throw e;
            } catch (BadRequest e) {
                connected.set(true);
                if (isAuthenticationRequired) authenticated.set(true);
                throw e;
            } catch (UserUnauthenticated e) {
                connected.set(true);
                authenticated.set(false);
                throw e;
            } catch (UserUnauthorized e) {
                connected.set(true);
                authenticated.set(false);
                throw e;
            } catch (UnsuccessfulResponseStatus e) {
                connected.set(true);
                throw e;
            } finally {
                nRequests.decrementAndGet();
                messageBus.syncPublisher(ConnectionStatusListener.CONNECTION_STATUS_TOPIC).finishRequest(connected.get());
            }
        }
    }
}