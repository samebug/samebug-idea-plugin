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
package com.samebug.clients.http.entities.chat;

import com.samebug.clients.http.entities.user.SamebugUser;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public final class ChatRoom {
    private String id;
    private SamebugUser createdBy;
    private Date created;
    private Date updated;
    private String title;
    private ChatOnSearch source;

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public SamebugUser getCreatedBy() {
        return createdBy;
    }

    @NotNull
    public Date getCreated() {
        return created;
    }

    @NotNull
    public Date getUpdated() {
        return updated;
    }

    @NotNull
    public String getTitle() {
        return title;
    }

    @NotNull
    public ChatOnSearch getSource() {
        return source;
    }
}
