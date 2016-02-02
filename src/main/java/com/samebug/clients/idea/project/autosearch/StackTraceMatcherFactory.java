/**
 * Copyright 2016 Samebug, Inc.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.idea.project.autosearch;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.api.LogScanner;
import com.samebug.clients.api.LogScannerFactory;
import com.samebug.clients.api.StackTraceListener;
import com.samebug.clients.idea.messaging.StackTraceMessageListener;
import com.samebug.clients.matcher.StackTraceMatcher;

public class StackTraceMatcherFactory implements LogScannerFactory {
    private final StackTraceListener listener;

    public StackTraceMatcherFactory(MessageBus messageBus) {
        this.listener = new PublisherListener(messageBus);
    }

    @Override
    public LogScanner createScanner() {
        return new StackTraceMatcher(listener);
    }


    static class PublisherListener implements StackTraceListener {
        private final MessageBus messageBus;

        PublisherListener(MessageBus messageBus) {

            this.messageBus = messageBus;
        }

        @Override
        public void stacktraceFound(String stacktrace) {
            messageBus.syncPublisher(StackTraceMessageListener.FOUND_TOPIC).stackTraceFound(stacktrace);
        }
    }
}