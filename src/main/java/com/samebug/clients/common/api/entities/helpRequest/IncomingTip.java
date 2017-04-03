/*
 * Copyright 2017 Samebug, Inc.
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
package com.samebug.clients.common.api.entities.helpRequest;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class IncomingTip {
    @NotNull
    public final Requester author;
    @NotNull
    public final String message;
    @NotNull
    public final Date createdAt;

    public IncomingTip(@NotNull Requester author, @NotNull String message, @NotNull Date createdAt) {
        this.author = author;
        this.message = message;
        this.createdAt = createdAt;
    }
}
