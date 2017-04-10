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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Solutions {
    @NotNull
    private List<RestHit<Tip>> tips;
    @NotNull
    private List<RestHit<SolutionReference>> references;

    private Solutions(@NotNull final Solutions rhs) {
        this.tips = new ArrayList<RestHit<Tip>>(rhs.tips);
        this.references = new ArrayList<RestHit<SolutionReference>>(rhs.references);
    }

    @NotNull
    public List<RestHit<Tip>> getTips() {
        return tips;
    }

    @NotNull
    public List<RestHit<SolutionReference>> getReferences() {
        return references;
    }


    @NotNull
    public Solutions addTip(@NotNull final RestHit<Tip> tip) {
        Solutions updated = new Solutions(this);
        updated.tips.add(0, tip);
        return updated;
    }

    @NotNull
    public Solutions asMarked(int solutionId, @NotNull final MarkResponse mark) {
        Solutions updated = new Solutions(this);
        for (int i = 0; i < references.size(); ++i) {
            if (references.get(i).getSolutionId() == solutionId) {
                updated.references.set(i, references.get(i).asMarked(mark));
            }
        }
        for (int i = 0; i < tips.size(); ++i) {
            if (tips.get(i).getSolutionId() == solutionId) {
                updated.tips.set(i, tips.get(i).asMarked(mark));
            }
        }
        return updated;
    }

    @NotNull
    public Solutions asUnmarked(int solutionId, @NotNull final MarkResponse mark) {
        Solutions updated = new Solutions(this);
        for (int i = 0; i < references.size(); ++i) {
            if (references.get(i).getSolutionId() == solutionId) {
                updated.references.set(i, references.get(i).asUnmarked(mark));
            }
        }
        for (int i = 0; i < tips.size(); ++i) {
            if (tips.get(i).getSolutionId() == solutionId) {
                updated.tips.set(i, tips.get(i).asUnmarked(mark));
            }
        }
        return updated;
    }

    @Nullable
    public RestHit getHitForSolutionId(int solutionId) {
        RestHit<Tip> t = getTipForSolutionId(solutionId);
        RestHit<SolutionReference> r = getReferenceForSolutionId(solutionId);
        return t == null ? r : t;
    }

    @Nullable
    private RestHit<Tip> getTipForSolutionId(int solutionId) {
        for (RestHit<Tip> tip : tips) {
            if (tip.getSolutionId() == solutionId) return tip;
        }
        return null;
    }

    @Nullable
    private RestHit<SolutionReference> getReferenceForSolutionId(int solutionId) {
        for (RestHit<SolutionReference> reference : references) {
            if (reference.getSolutionId() == solutionId) return reference;
        }
        return null;
    }
}
