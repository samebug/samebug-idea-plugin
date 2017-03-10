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
package com.samebug.clients.swing.ui.base.errorBarPane;

import com.samebug.clients.swing.ui.modules.ColorService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class ErrorBarPane extends JLayeredPane {
    protected final Integer MainLayer = 0;
    protected final Integer ErrorLayer = 100;
    protected final int ErrorBarGap = 50;
    protected final int ShowPopupForMillis = 5000;

    protected Component mainComponent;
    // TODO probably we will need multiple error bars in a vertical flow
    protected ErrorBar errorBar;
    protected Timer timer;

    private Color[] backgroundColors;

    public ErrorBarPane() {
        setOpaque(true);
        setBackground(ColorService.Background);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                if (mainComponent != null) repositionMainComponent();
                if (errorBar != null) repositionErrorBar();
            }
        });

        timer = new Timer(ShowPopupForMillis, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeErrorBar(errorBar);
            }
        });
        timer.setRepeats(false);
        updateUI();
    }

    public void addMainComponent(Component c) {
        assert EventQueue.isDispatchThread();
        if (mainComponent != null) remove(mainComponent);
        mainComponent = c;
        add(mainComponent, MainLayer);
        repositionMainComponent();
        revalidate();
        repaint();
    }

    public void addErrorBar(ErrorBar c) {
        assert EventQueue.isDispatchThread();
        // TODO animate
        if (errorBar != null) remove(errorBar);
        errorBar = c;
        add(errorBar, ErrorLayer);
        repositionErrorBar();
        revalidate();
        repaint();
    }

    public void popupErrorBar(final ErrorBar c) {
        assert EventQueue.isDispatchThread();
        if (errorBar != null && !timer.isRunning()) {
            // if there is a non-temporary popup, keep that
            return;
        } else {
            // if there is already a temporary popup, remove that and show this new one
            // if there is no popup, also show the new one
            timer.stop();
            addErrorBar(c);
            timer.restart();
        }
    }

    public void removeErrorBar() {
        assert EventQueue.isDispatchThread();
        if (errorBar != null) {
            remove(errorBar);
            errorBar = null;
            revalidate();
            repaint();
        }
    }

    public void removeErrorBar(ErrorBar c) {
        assert EventQueue.isDispatchThread();
        if (errorBar == c) removeErrorBar();
    }

    protected void repositionMainComponent() {
        mainComponent.setBounds(0, 0, getWidth(), getHeight());
    }

    protected void repositionErrorBar() {
        Dimension errorBarSize = errorBar.getPreferredSize();
        Dimension mySize = getSize();

        errorBar.setBounds((mySize.width - errorBarSize.width) / 2, ErrorBarGap, errorBarSize.width, errorBarSize.height);
    }

    public void setBackground(Color[] c) {
        backgroundColors = c;
        super.setBackground(ColorService.forCurrentTheme(backgroundColors));
    }

    @Override
    public void updateUI() {
        // JLayeredPane does not have default UI
        super.setBackground(ColorService.forCurrentTheme(backgroundColors));
    }

}
