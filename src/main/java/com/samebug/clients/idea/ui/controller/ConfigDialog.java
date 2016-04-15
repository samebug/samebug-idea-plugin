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
