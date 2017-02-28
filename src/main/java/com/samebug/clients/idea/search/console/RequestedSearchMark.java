package com.samebug.clients.idea.search.console;

import com.samebug.clients.common.entities.search.RequestedSearch;
import com.samebug.clients.swing.ui.SamebugIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

final class RequestedSearchMark extends SearchMark {
    private final RequestedSearch search;

    public RequestedSearchMark(RequestedSearch search) {
        this.search = search;
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return SamebugIcons.gutterLoading;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RequestedSearchMark) {
            RequestedSearchMark rhs = (RequestedSearchMark) o;
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
        return true;
    }

    @Override
    @NotNull
    public String getTooltipText() {
        return "Search is under progress, soon you will see the result";
    }
}
