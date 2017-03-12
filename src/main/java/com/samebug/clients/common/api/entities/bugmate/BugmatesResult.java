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
package com.samebug.clients.common.api.entities.bugmate;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BugmatesResult {
    @NotNull
    private List<Bugmate> bugmates;
    @NotNull
    private int numberOfOtherBugmates;
    @NotNull
    private boolean evenMoreExists;

    public BugmatesResult(@NotNull List<Bugmate> bugmates, @NotNull int numberOfOtherBugmates, @NotNull boolean evenMoreExists) {
        this.bugmates = bugmates;
        this.numberOfOtherBugmates = numberOfOtherBugmates;
        this.evenMoreExists = evenMoreExists;
    }

    @NotNull
    public List<Bugmate> getBugmates() {
        return bugmates;
    }

    @NotNull
    public int getNumberOfOtherBugmates() {
        return numberOfOtherBugmates;
    }

    @NotNull
    public boolean isEvenMoreExists() {
        return evenMoreExists;
    }
}
