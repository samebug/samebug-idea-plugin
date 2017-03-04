package com.samebug.clients.idea.ui.global;

import com.intellij.openapi.project.Project;
import com.samebug.clients.swing.ui.global.DataService;

public final class IdeaDataService {
    public static final DataService.Key<Project> Project = new DataService.Key<Project>("Project");
}
