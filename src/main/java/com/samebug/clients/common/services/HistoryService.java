package com.samebug.clients.common.services;

import com.samebug.clients.idea.components.application.ApplicationSettings;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.search.api.entities.SearchGroup;
import com.samebug.clients.search.api.entities.SearchHistory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

final public class HistoryService {
    @Nullable
    SearchHistory history;
    boolean showZeroSolutionSearches;
    boolean showRecurringSearches;

    public HistoryService() {
        ApplicationSettings settings = IdeaSamebugPlugin.getInstance().getState();
        history = null;
        showZeroSolutionSearches = settings.showZeroSolutions;
        showRecurringSearches = settings.showRecurring;
    }

    public boolean isShowZeroSolutionSearches() {
        return showZeroSolutionSearches;
    }

    public boolean isShowRecurringSearches() {
        return showRecurringSearches;
    }

    public void setShowZeroSolutionSearches(boolean showZeroSolutionSearches) {
        ApplicationSettings settings = IdeaSamebugPlugin.getInstance().getState();
        settings.showZeroSolutions = showZeroSolutionSearches;
        this.showZeroSolutionSearches = showZeroSolutionSearches;
    }

    public void setShowRecurringSearches(boolean showRecurringSearches) {
        ApplicationSettings settings = IdeaSamebugPlugin.getInstance().getState();
        settings.showRecurring = showRecurringSearches;
        this.showRecurringSearches = showRecurringSearches;
    }

    public int unfilteredHistoryLength() {
        if (history == null) {
            return 0;
        } else {
            return history.searchGroups.size();
        }
    }

    @Nullable
    public List<SearchGroup> getVisibleHistory() {
        if (history == null) return null;
        else {
            final Date now = new Date();
            final Calendar cal = Calendar.getInstance();
            cal.setTime(now);
            cal.add(Calendar.DAY_OF_YEAR, -1);
            final Date oneDayBefore = cal.getTime();
            final List<SearchGroup> result = new ArrayList<SearchGroup>();
            for (final SearchGroup group : history.searchGroups) {
                if (!showZeroSolutionSearches && group.numberOfHits == 0) {
                    // filtered because there is no solution for it
                } else if (!showRecurringSearches && group.firstSeen.before(oneDayBefore)) {
                    // filtered because it is old
                } else {
                    result.add(group);
                }
            }
            return result;
        }
    }

    public void setHistory(@Nullable SearchHistory history) {
        // TODO atomic reference?
        this.history = history;
    }
}
