package com.samebug.clients.idea.components.project;

import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

@State(
        name = "SamebugProjectConfiguration",
        storages = {
                @Storage(id = "Samebug", file = "Samebug.xml")
        }
)
public class Persistent extends AbstractProjectComponent implements PersistentStateComponent<Settings> {
    private Settings state = new Settings();

    protected Persistent(Project project) {
        super(project);
    }

    @Nullable
    @Override
    public Settings getState() {
        return this.state;
    }

    @Override
    public void loadState(Settings state) {
        this.state = state;
    }
}
