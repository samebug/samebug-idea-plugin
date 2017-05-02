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

import com.samebug.clients.http.entities.user.RegisteredSamebugUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public final class ReadableSolution<D extends Document> extends SolutionSlot<D> {
    private Date modified;
    private String exceptionType;
    private String message;
    private String messageSlug;
    private RegisteredSamebugUser createdBy;
    private Integer workspaceId;
    private Integer marks;

    @NotNull
    public Date getModified() {
        return modified;
    }

    @NotNull
    public String getExceptionType() {
        return exceptionType;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    @Nullable
    public String getMessageSlug() {
        return messageSlug;
    }

    @NotNull
    public RegisteredSamebugUser getCreatedBy() {
        return createdBy;
    }

    @Nullable
    public Integer getWorkspaceId() {
        return workspaceId;
    }

    @NotNull
    public Integer getMarks() {
        return marks;
    }
}
