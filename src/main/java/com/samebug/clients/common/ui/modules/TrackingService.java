/*
 * Copyright 2018 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *    http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.common.ui.modules;

import com.samebug.clients.common.tracking.RawEvent;

import java.util.UUID;

public abstract class TrackingService {
    private static TrackingService INSTANCE = new TrackingService() {
        @Override
        protected void internalTrace(RawEvent event) {
        }
    };
    protected static final long SESSION_TIMEOUT_MS = 30 * 60 * 1000;


    public static void install(TrackingService instance) {
        INSTANCE = instance;
    }

    public static void trace(RawEvent event) {
        INSTANCE.internalTrace(event);
    }

    public static String newPageViewId() {
        return UUID.randomUUID().toString();
    }

    public static String newSessionId() {
        return UUID.randomUUID().toString();
    }

    public static String newEventId() {
        return UUID.randomUUID().toString();
    }


    protected abstract void internalTrace(RawEvent event);
}
