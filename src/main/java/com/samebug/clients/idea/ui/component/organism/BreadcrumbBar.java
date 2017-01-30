/**
 * Copyright 2017 Samebug, Inc.
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
package com.samebug.clients.idea.ui.component.organism;

import com.samebug.clients.common.search.api.entities.BreadCrumb;
import com.samebug.clients.common.search.api.entities.QualifiedCall;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.resources.SamebugIcons;
import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.component.TransparentPanel;
import com.samebug.clients.idea.ui.listeners.LinkOpener;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

final public class BreadcrumbBar extends TransparentPanel {
    public BreadcrumbBar(@NotNull java.util.List<BreadCrumb> breadcrumbs) {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(new BreadcrumbEndLabel());
        if (!breadcrumbs.isEmpty()) add(new BreadcrumbLabel(breadcrumbs.get(0)));
        for (int i = 1; i < breadcrumbs.size(); ++i) {
            add(new BreadcrumbDelimeterLabel());
            add(new BreadcrumbLabel(breadcrumbs.get(i)));
        }

    }

    final class BreadcrumbEndLabel extends JLabel {
        @Override
        public Icon getIcon() {
            return SamebugIcons.breadcrumbEnd;
        }
    }

    final class BreadcrumbDelimeterLabel extends JLabel {
        @Override
        public Icon getIcon() {
            return SamebugIcons.breadcrumbDelimeter;
        }
    }

    final class BreadcrumbLabel extends JLabel {
        BreadCrumb breadCrumb;

        public BreadcrumbLabel(@NotNull final BreadCrumb breadCrumb) {
            this.breadCrumb = breadCrumb;
            final URL link = IdeaSamebugPlugin.getInstance().getUrlBuilder().crashdoc(breadCrumb);
            if (link != null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addMouseListener(new LinkOpener(link));
            }
            final QualifiedCall e = breadCrumb.getEntry();
            if (e.getPackageName() != null) {
                setToolTipText(String.format("%s.%s.%s()", e.getPackageName(), e.getClassName(), e.getMethodName()));
            }
        }

        @Override
        public String getText() {
            if (breadCrumb == null) return super.getText();
            else return breadCrumb.getComponent().getName();
        }

        @Override
        public Color getForeground() {
            if (breadCrumb == null) return super.getForeground();
            else return ColorUtil.componentColors(breadCrumb.getComponent().getColor());
        }
    }
}
