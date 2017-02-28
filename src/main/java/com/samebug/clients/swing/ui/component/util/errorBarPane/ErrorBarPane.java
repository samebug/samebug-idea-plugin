package com.samebug.clients.swing.ui.component.util.errorBarPane;

import com.samebug.clients.swing.ui.ColorUtil;

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
    protected Component errorBar;

    private Color[] backgroundColors;

    public ErrorBarPane() {
        setOpaque(true);
        setBackground(ColorUtil.Background);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                if (mainComponent != null) repositionMainComponent();
                if (errorBar != null) repositionErrorBar();
            }
        });

        updateUI();
    }

    public void addMainComponent(Component c) {
        if (mainComponent != null) remove(mainComponent);
        mainComponent = c;
        add(mainComponent, MainLayer);
        repositionMainComponent();
        revalidate();
        repaint();
    }

    public void addErrorBar(ErrorBar c) {
        // TODO animate
        if (errorBar != null) remove(errorBar);
        errorBar = c;
        add(errorBar, ErrorLayer);
        repositionErrorBar();
        revalidate();
        repaint();
    }

    public void popupErrorBar(final ErrorBar c) {
        addErrorBar(c);
        // TODO not sure if it will be garbage collected properly, or if there is any side effects
        Timer timer = new Timer(ShowPopupForMillis, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeErrorBar(c);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    public void removeErrorBar() {
        if (errorBar != null) {
            remove(errorBar);
            revalidate();
            repaint();
        }
    }

    public void removeErrorBar(ErrorBar c) {
        if (errorBar == c) {
            remove(errorBar);
            revalidate();
            repaint();
        }
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
        super.setBackground(ColorUtil.forCurrentTheme(backgroundColors));
    }

    @Override
    public void updateUI() {
        // JLayeredPane does not have default UI
        super.setBackground(ColorUtil.forCurrentTheme(backgroundColors));
    }

}
