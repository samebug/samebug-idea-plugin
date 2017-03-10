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
package com.samebug.clients.common.entities.user;

import org.jetbrains.annotations.NotNull;

public class Statistics {
    @NotNull
    Integer numberOfTips;
    @NotNull
    Integer numberOfMarks;
    @NotNull
    Integer numberOfThanks;


    public Statistics(@NotNull Integer numberOfTips, @NotNull Integer numberOfMarks, @NotNull Integer numberOfThanks) {
        this.numberOfTips = numberOfTips;
        this.numberOfMarks = numberOfMarks;
        this.numberOfThanks = numberOfThanks;
    }

    @Override
    public int hashCode() {
        return ((31 + numberOfTips.hashCode()) * 31 + numberOfMarks.hashCode()) * 31 + numberOfThanks.hashCode();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        else if (!(that instanceof Statistics)) return false;
        else {
            final Statistics rhs = (Statistics) that;
            return rhs.numberOfTips.equals(numberOfTips)
                    && rhs.numberOfMarks.equals(numberOfMarks)
                    && rhs.numberOfThanks.equals(numberOfThanks);
        }
    }

    @NotNull
    public Integer getNumberOfTips() {
        return numberOfTips;
    }

    @NotNull
    public Integer getNumberOfMarks() {
        return numberOfMarks;
    }

    @NotNull
    public Integer getNumberOfThanks() {
        return numberOfThanks;
    }
}
