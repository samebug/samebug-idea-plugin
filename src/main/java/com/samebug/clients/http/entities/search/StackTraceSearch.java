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
package com.samebug.clients.http.entities.search;

import com.samebug.clients.http.entities.user.SamebugUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public final class StackTraceSearch extends Search {
    private SearchGroup group;
    private Date timestamp;
    private String exceptionType;
    private String exceptionMessage;
    private String exceptionMessageSlug;
    private SamebugUser user;

    @NotNull
    public SearchGroup getGroup() {
        return group;
    }

    @NotNull
    public Date getTimestamp() {
        return timestamp;
    }

    @Nullable
    public String getExceptionType() {
        return exceptionType;
    }

    @Nullable
    public String getExceptionMessage() {
        return exceptionMessage;
    }

    @Nullable
    public String getExceptionMessageSlug() {
        return exceptionMessageSlug;
    }

    @NotNull
    public SamebugUser getUser() {
        return user;
    }
}