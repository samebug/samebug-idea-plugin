package com.samebug.clients.idea.components.application;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by poroszd on 4/19/16.
 */
@State(
        name = "SamebugTutorialConfiguration",
        storages = {
                @Storage(id = "SamebugTutorial", file = "$APP_CONFIG$/SamebugTutorial.xml")
        }
)
final public class TutorialApplicationComponent implements ApplicationComponent, PersistentStateComponent<TutorialSettings> {
    private TutorialSettings state = new TutorialSettings();

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return getClass().getSimpleName();
    }

    @Nullable
    @Override
    public TutorialSettings getState() {
        return this.state;
    }

    @Override
    public void loadState(TutorialSettings state) {
        this.state = state;
    }
}
