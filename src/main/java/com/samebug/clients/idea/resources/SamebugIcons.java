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
package com.samebug.clients.idea.resources;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;
import java.net.URL;

public class SamebugIcons {
    public final static Icon notification = IconLoader.getIcon("/com/samebug/icons/samebug-notification.png");
    public final static Icon tutorial = IconLoader.getIcon("/com/samebug/icons/tutorial.png");


    public final static Icon cogwheel = IconLoader.getIcon("/com/samebug/icons/cogwheel.png");
    public final static Icon cogwheelTodo = IconLoader.getIcon("/com/samebug/icons/cogwheel-todo.png");
    public final static Icon linkError = IconLoader.getIcon("/com/samebug/icons/link-error.png");
    public final static Icon linkActive = IconLoader.getIcon("/com/samebug/icons/link-active.png");
    public final static Icon reload = IconLoader.getIcon("/com/samebug/icons/reload.png");
    public final static Icon lightbulb = IconLoader.getIcon("/com/samebug/icons/filter-unknown.png");
    public final static Icon calendar = IconLoader.getIcon("/com/samebug/icons/filter-old.png");

    public final static Icon breadcrumbEnd = IconLoader.getIcon("/com/samebug/icons/bolt.png");
    public final static Icon breadcrumbDelimeter = IconLoader.getIcon("/com/samebug/icons/prev.png");

    public final static URL cogwheelTodoUrl = SamebugIcons.class.getClassLoader().getResource("/com/samebug/icons/cogwheel-todo.png");
    public final static URL lightbulbUrl = SamebugIcons.class.getClassLoader().getResource("/com/samebug/icons/filter-unknown.png");
    public final static URL calendarUrl = SamebugIcons.class.getClassLoader().getResource("/com/samebug/icons/filter-old.png");

}
