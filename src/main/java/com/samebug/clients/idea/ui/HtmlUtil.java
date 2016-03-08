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

import com.intellij.util.ui.UIUtil;
import com.samebug.clients.search.api.entities.ComponentStack;

import java.net.URL;
import java.util.List;

/**
 * Created by poroszd on 3/8/16.
 */
public class HtmlUtil {
    static public String html(String s) {
        return String.format("<html>%s</html>", s);
    }

    static public String link(String label, URL link) {
        return String.format("<html><a href=\"%s\">%s</a></html>", link, label);
    }

    static public String breadcrumbs(List<ComponentStack> stacks) {
        assert !stacks.isEmpty();
        StringBuilder breadcrumbs = new StringBuilder();
        breadcrumbs.append("<html>");
        breadcrumbs.append(breadcrumb(stacks.get(0)));
        for (int i = 1; i < stacks.size(); ++i) {
            ComponentStack stack = stacks.get(i);
            breadcrumbs.append(" &lsaquo; ");
            breadcrumbs.append(breadcrumb(stack));
        }
        breadcrumbs.append("</html>");
        return breadcrumbs.toString();
    }

    static private String breadcrumb(ComponentStack stack) {
        String componentName = stack.shortName != null ? stack.shortName : stack.name;
        if (stack.crashDocUrl == null) {
            return String.format("<span style=\"color:%s\">%s</span>", color(stack.color), componentName);
        } else {
            return String.format("<a style=\"color:%s\" href=\"%s\">%s</a>", color(stack.color), stack.crashDocUrl, componentName);
        }
    }

    static private String color(int componentColorCode) {
        if (UIUtil.isUnderDarcula()) {
            return DARCULA_COLORS[componentColorCode];
        } else {
            return DEFAULT_COLORS[componentColorCode];
        }
    }

    static private final String[] DEFAULT_COLORS = {"#9A9A9A", "#14E3CF", "#8BC349", "#00384F",
            "#9C27B0", "#FF00EB", "#03B8D4", "#79141D",
            "#FFB600", "#3000E7", "#3EABFF", "#D50000",
            "#443328", "#E91D63", "#029688", "#B0BF16", "#FF5621"};

    static private final String[] DARCULA_COLORS = {"#9A9A9A", "#14E3CF", "#8BC349", "#0080B5",
            "#BC37D3", "#FF00EB", "#03B8D4", "#D12232",
            "#FFB600", "#9B81FF", "#3EABFF", "#D50000",
            "#A37C62", "#E91D63", "#029688", "#B0BF16", "#FF5621"};
}
