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
