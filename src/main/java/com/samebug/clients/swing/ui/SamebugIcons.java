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
package com.samebug.clients.swing.ui;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public abstract class SamebugIcons {
    protected static SamebugIcons INSTANCE;

    protected final static String alertPath = "/com/samebug/images/alert.png";
    protected final static String alertErrorBarPath = "/com/samebug/images/alertErrorBar.png";

    public static void install(SamebugIcons instance) {
        assert INSTANCE == null : "SamebugIcons has already been installed";
        INSTANCE = instance;
    }

    public static Icon alert() {
        return INSTANCE.getImage(alertPath);
    }

    public static Icon alertErrorBar() {
        return INSTANCE.getImage(alertErrorBarPath);
    }

    protected abstract Icon getImage(String path);

    public final static Icon twSamebug = IconLoader.getIcon("/com/samebug/toolwindow/samebug.png");
    public final static Icon twBolt = IconLoader.getIcon("/com/samebug/toolwindow/bolt.png");

    public final static Icon gutterSamebug = IconLoader.getIcon("/com/samebug/icons/gutter/samebug-15x15.png");
    public final static Icon gutterLoading = IconLoader.getIcon("/com/samebug/icons/gutter/loading.gif");

    public final static Icon cogwheel = IconLoader.getIcon("/com/samebug/icons/cogwheel.png");
    public final static Icon cogwheelTodo = IconLoader.getIcon("/com/samebug/icons/cogwheel-todo.png");
}
