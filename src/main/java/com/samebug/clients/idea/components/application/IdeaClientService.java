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
import com.samebug.clients.search.api.entities.History;
import com.samebug.clients.search.api.entities.SearchResults;
import com.samebug.clients.search.api.entities.UserInfo;
import com.samebug.clients.search.api.entities.tracking.Solutions;
import com.samebug.clients.search.api.entities.tracking.TrackEvent;
import com.samebug.clients.search.api.exceptions.*;

/**
 * Created by poroszd on 2/23/16.
 */
public class IdeaClientService {
    private final SamebugClient client;
    private final MessageBus messageBus = ApplicationManager.getApplication().getMessageBus();
    private boolean connected;
    private boolean authenticated;
    private int nRequests;

    public IdeaClientService(final String apiKey) {
        this.client = new SamebugClient(apiKey);
        // Optimist initialization. If incorrect, that'll turn out soon
        this.connected = true;
        this.authenticated = true;
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
        authenticated = userInfo.isUserExist;
        messageBus.syncPublisher(ConnectionStatusListener.CONNECTION_STATUS_TOPIC).authorizationChange(authenticated);
        return userInfo;
    }

    public History getSearchHistory(final boolean recentFilterOn) throws SamebugClientException {
        return new ConnectionAwareHttpRequest<History>() {
            History request() throws SamebugClientException {
                return client.getSearchHistory(recentFilterOn);
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
        return connected;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public int getNumberOfActiveRequests() {
        return nRequests;
    }

    private abstract class ConnectionAwareHttpRequest<T> {
        abstract T request() throws SamebugClientException;

        public T execute() throws SamebugClientException {
            try {
                ++nRequests;
                messageBus.syncPublisher(ConnectionStatusListener.CONNECTION_STATUS_TOPIC).startRequest();
                T result = request();
                messageBus.syncPublisher(ConnectionStatusListener.CONNECTION_STATUS_TOPIC).finishRequest(true);
                connected = true;
                return result;
            } catch (SamebugTimeout e) {
                connected = false;
                throw e;
            } catch (RemoteError e) {
                connected = false;
                throw e;
            } catch (HttpError e) {
                connected = false;
                throw e;
            } catch (UnsuccessfulResponseStatus e) {
                connected = false;
                throw e;
            } catch (UserUnauthenticated e) {
                connected = true;
                authenticated = false;
                messageBus.syncPublisher(ConnectionStatusListener.CONNECTION_STATUS_TOPIC).authorizationChange(authenticated);
                throw e;
            } catch (UserUnauthorized e) {
                connected = true;
                authenticated = true;
                messageBus.syncPublisher(ConnectionStatusListener.CONNECTION_STATUS_TOPIC).authorizationChange(authenticated);
                throw e;
            } finally {
                --nRequests;
                messageBus.syncPublisher(ConnectionStatusListener.CONNECTION_STATUS_TOPIC).finishRequest(connected);
            }
        }
    }
}