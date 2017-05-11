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
package com.samebug.clients.idea.search.console;

import com.intellij.openapi.project.DumbAware;
import com.samebug.clients.common.entities.search.RequestedSearch;
import com.samebug.clients.swing.ui.modules.IconService;
import com.samebug.clients.swing.ui.modules.MessageService;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

final class RequestedSearchGutterIcon extends SearchMark implements DumbAware {
    private final RequestedSearch search;

    RequestedSearchGutterIcon(RequestedSearch search) {
        this.search = search;
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return IconService.gutterLoading;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RequestedSearchGutterIcon) {
            RequestedSearchGutterIcon rhs = (RequestedSearchGutterIcon) o;
            return rhs.search.equals(search);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return search.hashCode();
    }

    @Override
    public boolean isNavigateAction() {
        return false;
    }

    @Override
    @NotNull
    public String getTooltipText() {
        return MessageService.message("samebug.gutter.requested.tooltip");
    }
}
