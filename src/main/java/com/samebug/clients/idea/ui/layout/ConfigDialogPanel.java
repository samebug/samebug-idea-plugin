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
package com.samebug.clients.idea.ui.layout;

import com.samebug.clients.idea.components.application.ApplicationSettings;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;

import javax.swing.*;
import java.net.URI;

/**
 * Created by poroszd on 4/8/16.
 */
public class ConfigDialogPanel {
    final ApplicationSettings settings;

    public JTextField serverRoot;
    public JTextField trackingRoot;
    public JCheckBox tracking;
    public JSpinner connectTimeout;
    public JSpinner requestTimeout;
    public JTextField apiKey;
    public JPanel controlPanel;
    public JCheckBox writeTips;
    public JCheckBox markSolutions;
    public JCheckBox apacheLogging;


    // TODO too much boilerplate
    public ConfigDialogPanel() {
        settings = new ApplicationSettings(IdeaSamebugPlugin.getInstance().getState());
        serverRoot.setText(settings.serverRoot.toString());
        trackingRoot.setText(settings.trackingRoot.toString());
        connectTimeout.setValue(settings.connectTimeout);
        requestTimeout.setValue(settings.requestTimeout);
        apacheLogging.setSelected(settings.isApacheLoggingEnabled);
        writeTips.setSelected(settings.isWriteTipsEnabled);
        markSolutions.setSelected(settings.isMarkSolutionsEnabled);
        tracking.setSelected(settings.isTrackingEnabled);
    }

    public boolean isModified() {
        return !(settings.apiKey.equals(apiKey.getText())
                && settings.serverRoot.equals(URI.create(serverRoot.getText()))
                && settings.trackingRoot.equals(URI.create(trackingRoot.getText()))
                && settings.connectTimeout == (Integer) connectTimeout.getValue()
                && settings.requestTimeout == (Integer) requestTimeout.getValue()
                && settings.isApacheLoggingEnabled == apacheLogging.isSelected()
                && settings.isWriteTipsEnabled == writeTips.isSelected()
                && settings.isMarkSolutionsEnabled == markSolutions.isSelected()
                && settings.isTrackingEnabled == tracking.isSelected());
    }

    public void apply() {
        settings.apiKey = apiKey.getText();
        settings.serverRoot = URI.create(serverRoot.getText());
        settings.trackingRoot = URI.create(trackingRoot.getText());
        settings.connectTimeout = (Integer) connectTimeout.getValue();
        settings.requestTimeout = (Integer) requestTimeout.getValue();
        settings.isApacheLoggingEnabled = apacheLogging.isSelected();
        settings.isWriteTipsEnabled = writeTips.isSelected();
        settings.isMarkSolutionsEnabled = markSolutions.isSelected();
        settings.isTrackingEnabled = tracking.isSelected();
        IdeaSamebugPlugin.getInstance().saveSettings(settings);
    }

    public void reset() {
        // TODO reset to default values
        apiKey.setText(settings.apiKey);
    }
}
