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
package com.samebug.clients.idea.ui.controller;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.samebug.clients.idea.ui.layout.ConfigDialogPanel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by poroszd on 4/7/16.
 */
public class ConfigDialog implements Configurable {
    final private static Logger LOGGER = Logger.getInstance(ConfigDialog.class);

    ConfigDialogPanel dialog = null;

    @Nls
    @Override
    public String getDisplayName() {
        return "Samebug";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        dialog = new ConfigDialogPanel();
        return dialog.controlPanel;
    }

    @Override
    public boolean isModified() {
        return dialog.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        dialog.apply();
    }

    @Override
    public void reset() {
        dialog.reset();

    }

    @Override
    public void disposeUIResources() {

    }
}