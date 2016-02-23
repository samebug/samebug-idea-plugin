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

    public History getSearchHistory() throws SamebugClientException {
        return new ConnectionAwareHttpRequest<History>() {
            History request() throws SamebugTimeout, RemoteError, HttpError, UserUnauthorized, UnsuccessfulResponseStatus {
                return client.getSearchHistory();
            }
        }.execute();
    }

    public void trace(final TrackEvent event) throws SamebugClientException {
        new ConnectionAwareHttpRequest<Void>() {
            Void request() throws SamebugTimeout, RemoteError, HttpError, UserUnauthorized, UnsuccessfulResponseStatus {
                client.trace(event);
                return null;
            }
        }.execute();

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