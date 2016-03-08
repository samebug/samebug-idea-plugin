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
import com.samebug.clients.search.api.entities.SearchResults;
import com.samebug.clients.search.api.entities.UserInfo;
import com.samebug.clients.search.api.entities.tracking.Solutions;
import com.samebug.clients.search.api.entities.tracking.TrackEvent;
import com.samebug.clients.search.api.exceptions.*;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by poroszd on 2/23/16.
 */
public class IdeaClientService {
    private final SamebugClient client;
    private final MessageBus messageBus = ApplicationManager.getApplication().getMessageBus();
    private AtomicBoolean connected;
    private AtomicBoolean authenticated;
    private AtomicInteger nRequests;

    public IdeaClientService(final String apiKey, boolean authenticated) {
        this.client = new SamebugClient(apiKey);
        // Optimist initialization. If incorrect, that'll turn out soon
        this.connected = new AtomicBoolean(true);
        this.authenticated = new AtomicBoolean(authenticated);
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
        UserInfo userInfo = new ConnectionAwareHttpRequest<UserInfo>() {
            UserInfo request() throws SamebugClientException {
                return client.getUserInfo(apiKey);
            }
        }.execute();

        // TODO it smells bad. Successful authentication can happen at any request.
        if (authenticated.getAndSet(userInfo.isUserExist) != userInfo.isUserExist) {
            messageBus.syncPublisher(ConnectionStatusListener.CONNECTION_STATUS_TOPIC).authenticationChange(authenticated.get());
        }
        return userInfo;
    }

    public GroupedHistory getSearchHistory() throws SamebugClientException {
        return new ConnectionAwareHttpRequest<GroupedHistory>() {
            GroupedHistory request() throws SamebugClientException {
                return client.getSearchHistory();
            }
        }.execute();
    }

    public Solutions getSolutions(final String searchId) throws SamebugClientException {
        return new ConnectionAwareHttpRequest<Solutions>() {
            Solutions request() throws SamebugClientException {
                return client.getSolutions(searchId);
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
            try {
                nRequests.incrementAndGet();
                messageBus.syncPublisher(ConnectionStatusListener.CONNECTION_STATUS_TOPIC).startRequest();
                T result = request();
                messageBus.syncPublisher(ConnectionStatusListener.CONNECTION_STATUS_TOPIC).finishRequest(true);
                connected.set(true);
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
            } catch (UnsuccessfulResponseStatus e) {
                connected.set(false);
                throw e;
            } catch (UserUnauthenticated e) {
                connected.set(true);
                if (authenticated.getAndSet(false)) {
                    messageBus.syncPublisher(ConnectionStatusListener.CONNECTION_STATUS_TOPIC).authenticationChange(authenticated.get());
                }
                throw e;
            } catch (UserUnauthorized e) {
                connected.set(true);
                if (!authenticated.getAndSet(true)) {
                    messageBus.syncPublisher(ConnectionStatusListener.CONNECTION_STATUS_TOPIC).authenticationChange(authenticated.get());
                }
                throw e;
            } finally {
                nRequests.decrementAndGet();
                messageBus.syncPublisher(ConnectionStatusListener.CONNECTION_STATUS_TOPIC).finishRequest(connected.get());
            }
        }
    }
}