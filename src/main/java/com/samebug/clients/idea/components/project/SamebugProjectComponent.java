/**
 * Copyright 2016 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.idea.components.project;

import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.samebug.clients.common.services.SearchStore;

public class SamebugProjectComponent extends AbstractProjectComponent {
    private final SearchStore searchStore;

    public SamebugProjectComponent(Project project) {
        super(project);
        this.searchStore = new SearchStore(project);
    }

    public SearchStore getSearchStore() {
        return searchStore;
    }

}