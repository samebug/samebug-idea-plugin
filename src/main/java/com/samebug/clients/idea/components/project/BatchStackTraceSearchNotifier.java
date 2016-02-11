package com.samebug.clients.idea.components.project;

import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.idea.messages.BatchStackTraceSearchListener;
import com.samebug.clients.idea.messages.StackTraceSearchListener;
import com.samebug.clients.search.api.entities.SearchResults;
import com.samebug.clients.search.api.exceptions.SamebugClientException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by poroszd on 2/11/16.
 */
public class BatchStackTraceSearchNotifier extends AbstractProjectComponent implements StackTraceSearchListener {
    public BatchStackTraceSearchNotifier(Project project) {
        super(project);
    }

    @Override
    public void projectOpened() {
        super.projectOpened();
        messageBusConnection = myProject.getMessageBus().connect();
        messageBusConnection.subscribe(StackTraceSearchListener.SEARCH_TOPIC, this);
    }

    @Override
    public void projectClosed() {
        messageBusConnection.disconnect();
    }

    @Override
    synchronized public void searchStart(String id, String stackTrace) {
        if (started++ == 0) {
            timer = new Timer(500, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    batchFinished();
                }
            });
            timer.setRepeats(false);
            myProject.getMessageBus().syncPublisher(BatchStackTraceSearchListener.BATCH_SEARCH_TOPIC).batchStart();
        }
        ;
    }

    @Override
    synchronized public void searchSucceeded(String id, SearchResults results) {
        searches.add(results);
        timer.restart();
    }

    @Override
    synchronized public void timeout(String id) {
        failed++;
        timer.restart();
    }

    @Override
    synchronized public void unauthorized(String id) {
        failed++;
        timer.restart();
    }

    @Override
    synchronized public void searchFailed(String id, SamebugClientException error) {
        failed++;
        timer.restart();
    }

    private void batchFinished() {
        if (started <= searches.size() + failed) {
            myProject.getMessageBus().syncPublisher(BatchStackTraceSearchListener.BATCH_SEARCH_TOPIC).batchFinished(searches, failed);
            reset();
        }
    }

    private void reset() {
        started = 0;
        failed = 0;
        searches.clear();
        timer.stop();
        timer = null;
    }

    private MessageBusConnection messageBusConnection;

    private Timer timer;
    private final List<SearchResults> searches = new ArrayList<SearchResults>();
    private int started = 0;
    private int failed = 0;

}
