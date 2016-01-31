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
package com.samebug.clients.idea.intellij.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.Messages;
import com.samebug.clients.idea.SamebugIdeaPlugin;
import com.samebug.clients.exceptions.UnknownApiKey;
import com.samebug.clients.rest.exceptions.SamebugClientException;
import com.samebug.clients.idea.messages.SamebugBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.*;

public class SettingsDialog extends JDialog implements Configurable {
    private JPanel contentPane;
    private JTextField apiKeyTextField;
    private JTextPane descriptionTextPane;
    private JButton cancelButton;
    private JButton okButton;

    private SettingsDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(okButton);

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        try {
            apply();
            dispose();
        } catch (ConfigurationException e) {
            Messages.showErrorDialog(e.getMessage(), "Samebug Settings Error");
        }
    }

    private void onCancel() {
        dispose();
    }

    public static void setup(SamebugIdeaPlugin plugin) {
        SettingsDialog dialog = new SettingsDialog();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        dialog.pack();
        if (plugin != null && plugin.getApiKey() != null) {
            dialog.apiKeyTextField.setText(plugin.getApiKey());
        }
        dialog.setVisible(true);
    }

    @Nls
    @Override
    public String getDisplayName() {
        return SamebugBundle.message("samebug.settings.displayName");
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return null;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        SamebugIdeaPlugin plugin = SamebugIdeaPlugin.getInstance();
        try {
            String apiKey = apiKeyTextField.getText();
            plugin.setApiKey(apiKey);
        } catch (SamebugClientException e) {
            throw new ConfigurationException("Error during API Key validation. Samebug server is not available currently.");
        } catch (UnknownApiKey unknownApiKey) {
            throw new ConfigurationException("Unknown Samebug API Key.");
        }
    }

    @Override
    public void reset() {

    }

    @Override
    public void disposeUIResources() {

    }
}
