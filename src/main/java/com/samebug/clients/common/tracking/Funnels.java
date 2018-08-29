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
package com.samebug.clients.common.tracking;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class Funnels {
    public static final String AUTHENTICATION = "authentication";
    public static final String HELP_REQUEST = "help-request";
    public static final String WRITE_TIP = "write-tip";
    public static final String SOLUTION = "solution";
    public static final String SEARCH = "search";

    public static final class Instance {
        @NotNull
        public final String funnelId;
        @Nullable
        public final String transactionId;


        public Instance(@NotNull String funnelId, @Nullable String transactionId) {
            this.funnelId = funnelId;
            this.transactionId = transactionId;
        }
    }

    public static String newTransactionId() {
        return UUID.randomUUID().toString();
    }

    private Funnels() {}
}
