/**
 * Copyright 2017 Samebug, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.swing.ui.frame.intro;

import com.intellij.openapi.ui.popup.*;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.popup.AbstractPopup;
import com.intellij.ui.popup.FramelessNotificationPopup;
import com.samebug.clients.common.ui.frame.IIntroFrame;
import com.samebug.clients.swing.ui.base.errorBarPane.BasicFrame;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.panel.SamebugPanel;
import com.samebug.clients.swing.ui.base.panel.TransparentPanel;
import com.samebug.clients.swing.ui.modules.DrawService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

// TODO
public final class IntroFrame extends BasicFrame implements IIntroFrame {
    public IntroFrame() {
        SamebugLabel l = new SamebugLabel("TODO intro screen");
        addMainComponent(l);
        l.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                TransparentPanel c = new TransparentPanel() {
                    {
                        setBackground(Color.red);
                        setLayout(new MigLayout());
                        add(new SamebugLabel("hello"));
                    }
                    @Override
                    public void paintBorder(Graphics g) {
                        Graphics2D g2 = DrawService.init(g);
                        g2.setColor(getBackground());
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                    }
                };
                BalloonBuilder bb = JBPopupFactory.getInstance().createBalloonBuilder(c);
                bb.setFillColor(Color.blue);
                bb.setContentInsets(new Insets(10,10,10,10));
                bb.createBalloon().show(RelativePoint.getCenterOf(IntroFrame.this), Balloon.Position.above);
            }
        });
    }
}
