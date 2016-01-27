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

public class SetupDialog extends JDialog implements Configurable {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField apiKeyTextField;
    private JTextPane descriptionTextPane;

    public SetupDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
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
            Messages.showErrorDialog("Cannot configure: " + e.getMessage(), "Samebug Setup Error");
        }
    }

    private void onCancel() {
        dispose();
    }

    public static void setup() {
        SetupDialog dialog = new SetupDialog();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);

        dialog.pack();
        SamebugIdeaPlugin plugin = SamebugIdeaPlugin.getInstance();
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
        if (plugin != null) {
            try {
                String apiKey = apiKeyTextField.getText();
                plugin.setApiKey(apiKey);
            } catch (SamebugClientException e) {
                throw new ConfigurationException("Cannot save settings: Samebug Server Error");
            } catch (UnknownApiKey unknownApiKey) {
                throw new ConfigurationException("Cannot save settings: Unknown API Key");
            }
        } else {
            throw new ConfigurationException("Cannot save settings: No Plugin");
        }
    }

    @Override
    public void reset() {

    }

    @Override
    public void disposeUIResources() {

    }
}
