/*
 * Copyright 2018 Samebug, Inc.
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
package com.samebug.clients.idea.ui.modules;

import com.intellij.openapi.util.IconLoader;
import com.samebug.clients.swing.ui.modules.IconService;

import javax.swing.*;

public final class IdeaIconService extends IconService {
    @Override
    protected Icon getImage(String path) {
        return IconLoader.getIcon(path);
    }
}
