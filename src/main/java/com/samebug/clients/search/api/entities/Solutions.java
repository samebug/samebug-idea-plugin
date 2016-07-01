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
package com.samebug.clients.search.api.entities;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Solutions {
    @NotNull
    public SearchGroup searchGroup;
    @NotNull
    public List<RestHit<Tip>> tips;
    @NotNull
    public List<RestHit<SolutionReference>> references;

    public Solutions(@NotNull final Solutions rhs) {
        if (rhs.searchGroup instanceof StackTraceSearchGroup) this.searchGroup = new StackTraceSearchGroup((StackTraceSearchGroup) rhs.searchGroup);
        else if (rhs.searchGroup instanceof TextSearchGroup) this.searchGroup = new TextSearchGroup((TextSearchGroup) rhs.searchGroup);
        else throw new UnsupportedOperationException("Unhandled subtype " + rhs.searchGroup.getClass().getSimpleName());

        this.tips = new ArrayList<RestHit<Tip>>(rhs.tips.size());
        for (RestHit<Tip> t : rhs.tips) {
            this.tips.add(new RestHit<Tip>(t));
        }
        this.references = new ArrayList<RestHit<SolutionReference>>(rhs.references.size());
        for (RestHit<SolutionReference> s : rhs.references) {
            this.references.add(new RestHit<SolutionReference>(s));
        }
    }
}
