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

import com.samebug.clients.http.entities.missing.Source;
import com.samebug.clients.http.entities.user.User;
import org.jetbrains.annotations.NotNull;

public final class ExternalDocument extends Document {
    private User author;
    private String documentType;
    private String title;
    private Source source;

    @NotNull
    public User getAuthor() {
        return author;
    }

    @NotNull
    public String getDocumentType() {
        return documentType;
    }

    @NotNull
    public String getTitle() {
        return title;
    }

    @NotNull
    public Source getSource() {
        return source;
    }
}
