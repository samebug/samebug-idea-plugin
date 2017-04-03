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
package com.samebug.clients.idea.components.application;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.swing.ui.modules.TrackingService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        if (state.firstRun) {
            TrackingService.trace(Events.pluginInstall());
            state.firstRun = false;
        }
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
