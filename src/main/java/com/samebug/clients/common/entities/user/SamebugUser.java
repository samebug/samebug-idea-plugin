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
package com.samebug.clients.common.entities.user;

import com.samebug.clients.common.ui.component.profile.ConnectionStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;

public final class SamebugUser {
    public abstract static class Base {
        @Nullable
        private final URL url;
        @Nullable
        private final URL avatarUrl;
        @NotNull
        private final ConnectionStatus status;

        protected Base(@Nullable URL url, @Nullable URL avatarUrl, @NotNull ConnectionStatus status) {
            this.url = url;
            this.avatarUrl = avatarUrl;
            this.status = status;
        }

        @NotNull
        public abstract String getDisplayName();

        @Nullable
        public final URL getAvatarUrl() {
            return avatarUrl;
        }

        @Nullable
        public final URL getUrl() {
            return url;
        }

        @NotNull
        public ConnectionStatus getStatus() {
            return status;
        }

    }

    public static final class Visitor extends Base {
        private String visitorId;

        public Visitor(@Nullable URL url, @Nullable URL avatarUrl, @NotNull ConnectionStatus status, @NotNull String visitorId) {
            super(url, avatarUrl, status);
            this.visitorId = visitorId;
        }

        @NotNull
        public String getVisitorId() {
            return visitorId;
        }

        @NotNull
        @Override
        public String getDisplayName() {
            return "Unknown visitor";
        }
    }

    public static final class Registered extends Base {
        private Integer id;
        private String displayName;

        public Registered(@Nullable URL url, @Nullable URL avatarUrl, @NotNull ConnectionStatus status, @NotNull Integer id, @NotNull String displayName) {
            super(url, avatarUrl, status);
            this.id = id;
            this.displayName = displayName;
        }

        @NotNull
        public Integer getId() {
            return id;
        }

        @NotNull
        public String getDisplayName() {
            return displayName;
        }
    }

    private SamebugUser() {}
}
