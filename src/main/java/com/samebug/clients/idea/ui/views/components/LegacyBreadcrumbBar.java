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
package com.samebug.clients.idea.ui.views.components;

import com.intellij.ide.BrowserUtil;
import com.samebug.clients.idea.resources.SamebugIcons;
import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.search.api.entities.legacy.BreadCrumb;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by poroszd on 4/1/16.
 */
public class LegacyBreadcrumbBar extends TransparentPanel {
    public LegacyBreadcrumbBar(java.util.List<BreadCrumb> breadcrumbs) {
        assert (breadcrumbs.size() > 0);
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(new BreadcrumbEndLabel());
        add(new BreadcrumbLabel(breadcrumbs.get(0)));
        for (int i = 1; i < breadcrumbs.size(); ++i) {
            add(new BreadcrumbDelimeterLabel());
            add(new BreadcrumbLabel(breadcrumbs.get(i)));
        }

    }

    public class BreadcrumbEndLabel extends JLabel {
        @Override
        public Icon getIcon() {
            return SamebugIcons.breadcrumbEnd;
        }
    }

    public class BreadcrumbDelimeterLabel extends JLabel {
        @Override
        public Icon getIcon() {
            return SamebugIcons.breadcrumbDelimeter;
        }
    }

    public class BreadcrumbLabel extends JLabel {
        BreadCrumb breadCrumb;

        public BreadcrumbLabel(final BreadCrumb breadCrumb) {
            this.breadCrumb = breadCrumb;
            if (breadCrumb.detailsUrl != null) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        BrowserUtil.browse(breadCrumb.detailsUrl);

                    }
                });
            }
        }

        @Override
        public String getText() {
            if (breadCrumb == null) return super.getText();
            else return breadCrumb.component.shortName;
        }

        @Override
        public Color getForeground() {
            if (breadCrumb == null) return super.getForeground();
            else return ColorUtil.componentColors(breadCrumb.component.color);
        }
    }
}
