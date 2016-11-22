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
package com.samebug.clients.idea.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import com.samebug.clients.common.search.api.exceptions.SamebugClientException;
import com.samebug.clients.common.search.api.exceptions.UnknownApiKey;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.ui.controller.ConfigDialog;

import javax.swing.*;
import java.awt.event.*;

public class SettingsDialog extends JDialog {
    private JPanel contentPane;
    private JTextField apiKeyTextField;
    private JTextPane descriptionTextPane;
    private JButton cancelButton;
    private JButton okButton;
    public JButton detailsButton;

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

        detailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDetails();
            }
        });
        // call onClickCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onClickCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        ApplicationManager.getApplication().assertIsDispatchThread();
        dispose();
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                IdeaSamebugPlugin plugin = ApplicationManager.getApplication().getComponent(IdeaSamebugPlugin.class);
                String apiKey = apiKeyTextField.getText().trim();
                if (!apiKey.isEmpty()) {
                    try {
                        plugin.setApiKey(apiKey);
                    } catch (UnknownApiKey unknownApiKey) {
                        ApplicationManager.getApplication().invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                Messages.showErrorDialog("Unknown Samebug API Key.", "Samebug Settings Error");
                            }
                        });
                    } catch (SamebugClientException e) {
                        ApplicationManager.getApplication().invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                Messages.showErrorDialog("Error during API Key validation. Samebug server is not available currently.", "Samebug Settings Error");
                            }
                        });
                    }
                }
            }
        });
    }

    private void onCancel() {
        ApplicationManager.getApplication().assertIsDispatchThread();
        dispose();
    }

    private void onDetails() {
        ApplicationManager.getApplication().assertIsDispatchThread();
        dispose();
        ShowSettingsUtil.getInstance().showSettingsDialog(ProjectManager.getInstance().getDefaultProject(), ConfigDialog.class);
    }

    public static void setup(String apiKey) {
        SettingsDialog dialog = new SettingsDialog();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        dialog.pack();
        if (apiKey != null) {
            dialog.apiKeyTextField.setText(apiKey);
        }
        dialog.setVisible(true);
    }
}
