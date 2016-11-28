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
package com.samebug.clients.common.search.api.entities;

import org.jetbrains.annotations.NotNull;

public class UserStats {
    @NotNull
    private Integer numberOfCrashes;
    @NotNull
    private Integer numberOfMarks;
    @NotNull
    private Integer numberOfTips;
    @NotNull
    private Integer numberOfThanks;

    @NotNull
    public Integer getNumberOfCrashes() {
        return numberOfCrashes;
    }

    @NotNull
    public Integer getNumberOfMarks() {
        return numberOfMarks;
    }

    @NotNull
    public Integer getNumberOfTips() {
        return numberOfTips;
    }

    @NotNull
    public Integer getNumberOfThanks() {
        return numberOfThanks;
    }
}
