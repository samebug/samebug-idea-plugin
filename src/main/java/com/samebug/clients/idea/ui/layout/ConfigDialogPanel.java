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

import com.intellij.openapi.options.ConfigurationException;
import com.samebug.clients.idea.components.application.ApplicationSettings;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;

import javax.swing.*;
import java.net.URI;

/**
 * Created by poroszd on 4/8/16.
 */
public class ConfigDialogPanel {
    ApplicationSettings currentConfig;
    public JPanel controlPanel;

    public JTextField serverRoot;
    public JTextField trackingRoot;
    public JCheckBox tracking;
    public JSpinner connectTimeout;
    public JSpinner requestTimeout;
    public JTextField apiKey;
    public JCheckBox writeTips;
    public JCheckBox markSolutions;
    public JCheckBox apacheLogging;


    public ConfigDialogPanel() {
        currentConfig = new ApplicationSettings(IdeaSamebugPlugin.getInstance().getState());
    }

    public boolean isModified() {
        return !currentConfig.equals(fromUI());
    }

    public void apply() throws ConfigurationException {
        final ApplicationSettings settings = fromUI();
        try {
            try {
                URI.create(settings.serverRoot);
            } catch (Exception e) {
                throw new ConfigurationException(settings.serverRoot + " is not a valid URI");
            }
            try {
                URI.create(settings.trackingRoot);
            } catch (Exception e) {
                throw new ConfigurationException(settings.trackingRoot + " is not a valid URI");
            }
            IdeaSamebugPlugin.getInstance().saveSettings(settings);
            currentConfig = settings;
        } catch (Exception e) {
            throw new ConfigurationException("Failed to save configuration: " + e.getMessage());
        }
    }

    public void reset() {
        toUI(currentConfig);
    }

    // Apparently, IntelliJ's way to reset to defaults seems to be simply deleting the config files.
    public void resetToDefaults() {
        final ApplicationSettings settings = currentConfig;
        settings.serverRoot = ApplicationSettings.defaultServerRoot;
        settings.trackingRoot = ApplicationSettings.defaultTrackingRoot;
        settings.isTrackingEnabled = ApplicationSettings.defaultIsTrackingEnabled;
        settings.connectTimeout = ApplicationSettings.defaultConnectTimeout;
        settings.requestTimeout = ApplicationSettings.defaultRequestTimeout;
        settings.isApacheLoggingEnabled = ApplicationSettings.defaultIsApacheLoggingEnabled;
        settings.isWriteTipsEnabled = ApplicationSettings.defaultIsWriteTipsEnabled;
        settings.isMarkSolutionsEnabled = ApplicationSettings.defaultIsMarkSolutionsEnabled;
        toUI(settings);
    }

    ApplicationSettings fromUI() {
        final ApplicationSettings settings = new ApplicationSettings(currentConfig);
        settings.apiKey = apiKey.getText();
        settings.serverRoot = serverRoot.getText();
        settings.trackingRoot = trackingRoot.getText();
        settings.connectTimeout = (Integer) connectTimeout.getValue();
        settings.requestTimeout = (Integer) requestTimeout.getValue();
        settings.isApacheLoggingEnabled = apacheLogging.isSelected();
        settings.isWriteTipsEnabled = writeTips.isSelected();
        settings.isMarkSolutionsEnabled = markSolutions.isSelected();
        settings.isTrackingEnabled = tracking.isSelected();
        return settings;
    }

    void toUI(final ApplicationSettings settings) {
        apiKey.setText(settings.apiKey);
        serverRoot.setText(settings.serverRoot);
        trackingRoot.setText(settings.trackingRoot);
        connectTimeout.setValue(settings.connectTimeout);
        requestTimeout.setValue(settings.requestTimeout);
        apacheLogging.setSelected(settings.isApacheLoggingEnabled);
        writeTips.setSelected(settings.isWriteTipsEnabled);
        markSolutions.setSelected(settings.isMarkSolutionsEnabled);
        tracking.setSelected(settings.isTrackingEnabled);
    }
}
