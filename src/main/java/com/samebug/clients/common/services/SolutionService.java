package com.samebug.clients.common.services;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.common.messages.MarkModelListener;
import com.samebug.clients.common.messages.SolutionModelListener;
import com.samebug.clients.common.messages.TipModelListener;
import com.samebug.clients.common.search.api.client.ClientResponse;
import com.samebug.clients.common.search.api.client.SamebugClient;
import com.samebug.clients.common.search.api.entities.*;
import com.samebug.clients.common.search.api.entities.Exception;
import com.samebug.clients.common.search.api.exceptions.SamebugClientException;

public final class SolutionService {
    final MessageBus messageBus;
    final ClientService clientService;

    public SolutionService(MessageBus messageBus, ClientService clientService) {
        this.messageBus = messageBus;
        this.clientService = clientService;
    }

    public Solutions getSolutions(final int searchId) throws SamebugClientException {
        final SamebugClient client = clientService.client;

        ClientService.ConnectionAwareHttpRequest<Solutions> requestHandler =
                new ClientService.ConnectionAwareHttpRequest<Solutions>() {
                    ClientResponse<Solutions> request() {
                        return client.getSolutions(searchId);
                    }

                    protected void start() {
                        messageBus.syncPublisher(SolutionModelListener.TOPIC).startLoadingSolutions(searchId);
                    }

                    protected void success(Solutions result) {
                        messageBus.syncPublisher(SolutionModelListener.TOPIC).successLoadingSolutions(searchId, result);
                    }

                    protected void fail(SamebugClientException e) {
                        messageBus.syncPublisher(SolutionModelListener.TOPIC).failLoadingSolutions(searchId, e);
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

                    protected void start() {
                        messageBus.syncPublisher(TipModelListener.TOPIC).startPostTip(searchId);
                    }

                    protected void success(RestHit<Tip> result) {
                        messageBus.syncPublisher(TipModelListener.TOPIC).successPostTip(searchId, result);
                    }

                    protected void fail(SamebugClientException e) {
                        messageBus.syncPublisher(TipModelListener.TOPIC).failPostTip(searchId, e);
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

                    protected void start() {
                        messageBus.syncPublisher(MarkModelListener.TOPIC).startPostingMark(searchId, solutionId);
                    }

                    protected void success(MarkResponse result) {
                        messageBus.syncPublisher(MarkModelListener.TOPIC).successPostingMark(searchId, solutionId, result);
                    }

                    protected void fail(SamebugClientException e) {
                        messageBus.syncPublisher(MarkModelListener.TOPIC).failPostingMark(searchId, solutionId, e);
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

                    protected void start() {
                        messageBus.syncPublisher(MarkModelListener.TOPIC).startRetractMark(voteId);
                    }

                    protected void success(MarkResponse result) {
                        messageBus.syncPublisher(MarkModelListener.TOPIC).successRetractMark(voteId, result);
                    }

                    protected void fail(SamebugClientException e) {
                        messageBus.syncPublisher(MarkModelListener.TOPIC).failRetractMark(voteId, e);
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
