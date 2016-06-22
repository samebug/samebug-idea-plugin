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
package com.samebug.clients.idea.components.project;

import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.idea.messages.model.BatchStackTraceSearchListener;
import com.samebug.clients.idea.messages.model.StackTraceSearchListener;
import com.samebug.clients.search.api.entities.SearchResults;
import com.samebug.clients.search.api.entities.tracking.SearchInfo;
import com.samebug.clients.search.api.exceptions.SamebugClientException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class BatchStackTraceSearchNotifier extends AbstractProjectComponent implements StackTraceSearchListener {
    public BatchStackTraceSearchNotifier(Project project) {
        super(project);
    }

    @Override
    public void projectOpened() {
        super.projectOpened();
        messageBusConnection = myProject.getMessageBus().connect();
        messageBusConnection.subscribe(StackTraceSearchListener.TOPIC, this);
    }

    @Override
    public void projectClosed() {
        messageBusConnection.disconnect();
    }

    @Override
    synchronized public void searchStart(SearchInfo searchInfo, String stackTrace) {
        if (started++ == 0) {
            timer = new Timer(500, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    batchFinished();
                }
            });
            timer.setRepeats(false);
            if (!myProject.isDisposed()) myProject.getMessageBus().syncPublisher(BatchStackTraceSearchListener.TOPIC).batchStart();
        }
    }

    @Override
    synchronized public void searchSucceeded(SearchInfo searchInfo, SearchResults results) {
        if (timer != null) {
            searches.add(results);
            timer.restart();
        }
    }

    @Override
    synchronized public void timeout(SearchInfo searchInfo) {
        if (timer != null) {
            failed++;
            timer.restart();
        }
    }

    @Override
    synchronized public void unauthorized(SearchInfo searchInfo) {
        if (timer != null) {
            failed++;
            timer.restart();
        }
    }

    @Override
    synchronized public void searchFailed(SearchInfo searchInfo, SamebugClientException error) {
        if (timer != null) {
            failed++;
            timer.restart();
        }
    }

    private void batchFinished() {
        if (started <= searches.size() + failed) {
            if (!myProject.isDisposed()) myProject.getMessageBus().syncPublisher(BatchStackTraceSearchListener.TOPIC).batchFinished(searches, failed);
            reset();
        }
    }

    private void reset() {
        if (timer != null) {
            started = 0;
            failed = 0;
            searches.clear();
            timer.stop();
            timer = null;
        }
    }

    private MessageBusConnection messageBusConnection;

    private Timer timer;
    private final List<SearchResults> searches = new ArrayList<SearchResults>();
    private int started = 0;
    private int failed = 0;

}
