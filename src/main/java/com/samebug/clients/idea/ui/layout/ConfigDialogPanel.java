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
package com.samebug.clients.idea.ui.layout;

import com.intellij.openapi.options.ConfigurationException;
import com.samebug.clients.http.exceptions.SamebugClientException;
import com.samebug.clients.http.exceptions.UserUnauthenticated;
import com.samebug.clients.http.exceptions.UserUnauthorized;
import com.samebug.clients.idea.components.application.ApplicationSettings;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;

import javax.swing.*;
import java.net.URI;

public class ConfigDialogPanel {
    ApplicationSettings currentConfig;
    public JPanel controlPanel;

    public JTextField serverRoot;
    public JTextField trackingRoot;
    public JCheckBox tracking;
    public JSpinner connectTimeout;
    public JSpinner requestTimeout;
    public JSpinner workspaceId;
    public JTextField apiKey;
    public JCheckBox apacheLogging;


    public ConfigDialogPanel() {
        currentConfig = new ApplicationSettings(IdeaSamebugPlugin.getInstance().getState());
    }

    public boolean isModified() {
        return !currentConfig.equals(fromUI());
    }

    public void apply() throws ConfigurationException {
        // TODO move messages to property file
        final ApplicationSettings settings = fromUI();
        try {
            try {
                final String serverRoot = settings.serverRoot;
                // Silently remove trailing slash
                if (serverRoot.endsWith("/")) {
                    settings.serverRoot = serverRoot.substring(0, serverRoot.length() - 1);
                }
                URI.create(settings.serverRoot);
            } catch (Exception e) {
                throw new ConfigurationException(settings.serverRoot + " is not a valid URI");
            }
            try {
                final String trackingRoot = settings.trackingRoot;
                // Silently remove trailing slash
                if (trackingRoot.endsWith("/")) {
                    settings.trackingRoot = trackingRoot.substring(0, trackingRoot.length() - 1);
                }
                URI.create(settings.trackingRoot);
            } catch (Exception e) {
                throw new ConfigurationException(settings.trackingRoot + " is not a valid URI");
            }
            IdeaSamebugPlugin.getInstance().saveSettings(settings);
            currentConfig = settings;
            try {
                // IMPROVE: this is an http call on the UI thread. It would be nice to do this in the background and show a progress indicator.
                IdeaSamebugPlugin.getInstance().authenticationService.apiKeyAuthentication();
            } catch (UserUnauthenticated e) {
                throw new ConfigurationException("This is an invalid API key! Set it to empty and use your username-password to log in, or check it on the website.");
            } catch (UserUnauthorized e) {
                throw new ConfigurationException("You don't have permission to this workspace! Set it to 0 to use your default workspace.");
            } catch (SamebugClientException ignored) {
            }
        } catch (Exception e) {
            throw new ConfigurationException("Failed to save configuration: " + e.getMessage());
        }
    }

    public void reset() {
        toUI(currentConfig);
    }

    ApplicationSettings fromUI() {
        final ApplicationSettings settings = new ApplicationSettings(currentConfig);
        settings.apiKey = apiKey.getText().isEmpty() ? null : apiKey.getText();
        settings.workspaceId = (Integer) workspaceId.getValue() <= 0 ? null : ((Integer) workspaceId.getValue());
        settings.serverRoot = serverRoot.getText();
        settings.trackingRoot = trackingRoot.getText();
        settings.connectTimeout = (Integer) connectTimeout.getValue();
        settings.requestTimeout = (Integer) requestTimeout.getValue();
        settings.isApacheLoggingEnabled = apacheLogging.isSelected();
        settings.isTrackingEnabled = tracking.isSelected();
        return settings;
    }

    void toUI(final ApplicationSettings settings) {
        apiKey.setText(settings.apiKey);
        workspaceId.setValue(settings.workspaceId == null ? 0 : settings.workspaceId);
        serverRoot.setText(settings.serverRoot);
        trackingRoot.setText(settings.trackingRoot);
        connectTimeout.setValue(settings.connectTimeout);
        requestTimeout.setValue(settings.requestTimeout);
        apacheLogging.setSelected(settings.isApacheLoggingEnabled);
        tracking.setSelected(settings.isTrackingEnabled);
    }
}
