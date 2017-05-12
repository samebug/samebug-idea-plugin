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
package com.samebug.clients.http.entities.solution;

import com.samebug.clients.http.entities.search.StackTraceInfo;
import com.samebug.clients.http.entities.user.RegisteredSamebugUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public final class SolutionSlot<D extends Document> {
    private Integer id;
    private Date createdAt;
    private Date modified;
    private D document;
    private RegisteredSamebugUser createdBy;
    private Integer workspaceId;
    private StackTraceInfo stackTraceInfo;
    private String responseToHelpRequestId;

    @NotNull
    public Integer getId() {
        return id;
    }

    @NotNull
    public Date getCreatedAt() {
        return createdAt;
    }

    @NotNull
    public Date getModified() {
        return modified;
    }

    @NotNull
    public D getDocument() {
        return document;
    }

    @NotNull
    public RegisteredSamebugUser getCreatedBy() {
        return createdBy;
    }

    @Nullable
    public Integer getWorkspaceId() {
        return workspaceId;
    }

    @Nullable
    public StackTraceInfo getStackTraceInfo() {
        return stackTraceInfo;
    }

    @Nullable
    public String getResponseToHelpRequestId() {
        return responseToHelpRequestId;
    }
}
