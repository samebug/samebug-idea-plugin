package com.samebug.clients.common.services;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.common.search.api.client.ClientResponse;
import com.samebug.clients.common.search.api.client.SamebugClient;
import com.samebug.clients.common.search.api.entities.Exception;
import com.samebug.clients.common.search.api.entities.*;
import com.samebug.clients.common.search.api.exceptions.SamebugClientException;

public final class SolutionService {
    final MessageBus messageBus;
    final ClientService clientService;
    final SolutionStore solutionStore;

    public SolutionService(MessageBus messageBus, ClientService clientService, SolutionStore solutionStore) {
        this.messageBus = messageBus;
        this.clientService = clientService;
        this.solutionStore = solutionStore;
    }

    public Solutions loadSolutions(final int searchId) throws SamebugClientException {
        final SamebugClient client = clientService.client;

        ClientService.ConnectionAwareHttpRequest<Solutions> requestHandler =
                new ClientService.ConnectionAwareHttpRequest<Solutions>() {
                    ClientResponse<Solutions> request() {
                        return client.getSolutions(searchId);
                    }

                    protected void success(Solutions result) {
                        solutionStore.solutions.put(searchId, result);
                    }

                    protected void fail(SamebugClientException e) {
                        solutionStore.solutions.remove(searchId);
                    }
                };
        return clientService.execute(requestHandler);
    }

    public RestHit<Tip> postTip(final int searchId, final String tip, final String sourceUrl) throws SamebugClientException {
        final SamebugClient client = clientService.client;

        ClientService.ConnectionAwareHttpRequest<RestHit<Tip>> requestHandler =
                new ClientService.ConnectionAwareHttpRequest<RestHit<Tip>>() {
                    ClientResponse<RestHit<Tip>> request() {
                        return client.postTip(searchId, tip, sourceUrl);
                    }
                };
        return clientService.execute(requestHandler);
    }

    public MarkResponse postMark(final int searchId, final int solutionId) throws SamebugClientException {
        final SamebugClient client = clientService.client;

        ClientService.ConnectionAwareHttpRequest<MarkResponse> requestHandler =
                new ClientService.ConnectionAwareHttpRequest<MarkResponse>() {
                    ClientResponse<MarkResponse> request() {
                        return client.postMark(searchId, solutionId);
                    }
                };
        return clientService.execute(requestHandler);
    }

    public MarkResponse retractMark(final int voteId) throws SamebugClientException {
        final SamebugClient client = clientService.client;

        ClientService.ConnectionAwareHttpRequest<MarkResponse> requestHandler =
                new ClientService.ConnectionAwareHttpRequest<MarkResponse>() {
                    ClientResponse<MarkResponse> request() {
                        return client.retractMark(voteId);
                    }
                };
        return clientService.execute(requestHandler);
    }


    public static String headLine(Search search) {
        if (search instanceof TextSearch) {
            return "Not parseable stacktrace";
        } else {
            StackTraceSearch s = (StackTraceSearch) search;
            Exception trace = s.getStackTrace().getTrace();
            String headLine = trace.getTypeName();
            if (trace.getMessage() != null) {
                headLine += ": " + trace.getMessage();
            }
            return headLine;
        }
    }
}
