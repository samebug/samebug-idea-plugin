/**
 * Copyright 2017 Samebug, Inc.
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
package com.samebug.clients.common.services;

import com.samebug.clients.common.search.api.entities.SearchGroup;
import com.samebug.clients.common.search.api.entities.SearchHistory;
import com.samebug.clients.idea.components.application.ApplicationSettings;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

final public class HistoryService {
    AtomicReference<SearchHistory> history;
    AtomicBoolean showZeroSolutionSearches;
    AtomicBoolean showRecurringSearches;

    public HistoryService() {
        ApplicationSettings settings = IdeaSamebugPlugin.getInstance().getState();
        history = new AtomicReference<SearchHistory>(null);
        showZeroSolutionSearches = new AtomicBoolean(settings.showZeroSolutions);
        showRecurringSearches = new AtomicBoolean(settings.showRecurring);
    }

    public boolean isShowZeroSolutionSearches() {
        return showZeroSolutionSearches.get();
    }

    public boolean isShowRecurringSearches() {
        return showRecurringSearches.get();
    }

    public void setShowZeroSolutionSearches(boolean showZeroSolutionSearches) {
        ApplicationSettings settings = IdeaSamebugPlugin.getInstance().getState();
        settings.showZeroSolutions = showZeroSolutionSearches;
        this.showZeroSolutionSearches.set(showZeroSolutionSearches);
    }

    public void setShowRecurringSearches(boolean showRecurringSearches) {
        ApplicationSettings settings = IdeaSamebugPlugin.getInstance().getState();
        settings.showRecurring = showRecurringSearches;
        this.showRecurringSearches.set(showRecurringSearches);
    }

    public int unfilteredHistoryLength() {
        @Nullable SearchHistory currentHistory = history.get();
        if (currentHistory == null) {
            return 0;
        } else {
            return currentHistory.getSearchGroups().size();
        }
    }

    @Nullable
    public List<SearchGroup> getVisibleHistory() {
        @Nullable SearchHistory currentHistory = history.get();
        if (currentHistory == null) return null;
        else {
            final Date now = new Date();
            final Calendar cal = Calendar.getInstance();
            cal.setTime(now);
            cal.add(Calendar.DAY_OF_YEAR, -1);
            final Date oneDayBefore = cal.getTime();
            final List<SearchGroup> result = new ArrayList<SearchGroup>();
            for (final SearchGroup group : currentHistory.getSearchGroups()) {
                if (!showZeroSolutionSearches.get() && group.getNumberOfHits() == 0) {
                    // filtered because there is no solution for it
                } else if (!showRecurringSearches.get() && group.getFirstSeen().before(oneDayBefore)) {
                    // filtered because it is old
                } else {
                    result.add(group);
                }
            }
            return result;
        }
    }

    public void setHistory(@Nullable SearchHistory history) {
        this.history.set(history);
    }
}
