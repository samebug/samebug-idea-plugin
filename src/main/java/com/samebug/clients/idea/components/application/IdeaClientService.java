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
import com.samebug.clients.search.api.entities.tracking.TrackEvent;
import com.samebug.clients.search.api.exceptions.*;

/**
 * Created by poroszd on 2/23/16.
 */
public class IdeaClientService {
    private final SamebugClient client;
    private final MessageBus messageBus = ApplicationManager.getApplication().getMessageBus();

    public IdeaClientService(final String apiKey) {
        this.client = new SamebugClient(apiKey);
    }

    public SearchResults searchSolutions(final String stacktrace)
            throws SamebugTimeout, RemoteError, HttpError, UserUnauthorized, UnsuccessfulResponseStatus {
        return new ConnectionAwareHttpRequest<SearchResults>() {
            SearchResults request() throws SamebugTimeout, RemoteError, HttpError, UserUnauthorized, UnsuccessfulResponseStatus {
                return client.searchSolutions(stacktrace);
            }
        }.execute();
    }

    public UserInfo getUserInfo(final String apiKey) throws SamebugClientException {
        return new ConnectionAwareHttpRequest<UserInfo>() {
            UserInfo request() throws SamebugTimeout, RemoteError, HttpError, UserUnauthorized, UnsuccessfulResponseStatus {
                return client.getUserInfo(apiKey);
            }
        }.execute();
    }

    public History getSearchHistory(final boolean recentFilterOn) throws SamebugClientException {
        return new ConnectionAwareHttpRequest<History>() {
            History request() throws SamebugTimeout, RemoteError, HttpError, UserUnauthorized, UnsuccessfulResponseStatus {
                return client.getSearchHistory(recentFilterOn);
            }
        }.execute();
    }

    public void trace(final TrackEvent event) throws SamebugClientException {
        client.trace(event);
    }

    private abstract class ConnectionAwareHttpRequest<T> {
        abstract T request() throws SamebugTimeout, RemoteError, HttpError, UserUnauthorized, UnsuccessfulResponseStatus;

        public T execute() throws SamebugTimeout, RemoteError, HttpError, UserUnauthorized, UnsuccessfulResponseStatus {
            try {
                messageBus.syncPublisher(ConnectionStatusListener.CONNECTION_STATUS_TOPIC).startRequest();
                T result = request();
                messageBus.syncPublisher(ConnectionStatusListener.CONNECTION_STATUS_TOPIC).finishRequest(true);
                return result;
            } catch (SamebugTimeout e) {
                messageBus.syncPublisher(ConnectionStatusListener.CONNECTION_STATUS_TOPIC).finishRequest(false);
                throw e;
            } catch (RemoteError e) {
                messageBus.syncPublisher(ConnectionStatusListener.CONNECTION_STATUS_TOPIC).finishRequest(false);
                throw e;
            } catch (HttpError e) {
                messageBus.syncPublisher(ConnectionStatusListener.CONNECTION_STATUS_TOPIC).finishRequest(false);
                throw e;
            } catch (UnsuccessfulResponseStatus e) {
                messageBus.syncPublisher(ConnectionStatusListener.CONNECTION_STATUS_TOPIC).finishRequest(false);
                throw e;
            } catch (UserUnauthorized e) {
                messageBus.syncPublisher(ConnectionStatusListener.CONNECTION_STATUS_TOPIC).finishRequest(true);
                messageBus.syncPublisher(ConnectionStatusListener.CONNECTION_STATUS_TOPIC).authorizationChange(false);
                throw e;
            }
        }
    }
}