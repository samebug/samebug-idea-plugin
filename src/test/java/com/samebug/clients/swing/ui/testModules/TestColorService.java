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
package com.samebug.clients.swing.ui.testModules;

import com.samebug.clients.swing.ui.modules.ColorService;

public final class TestColorService extends ColorService {
    final boolean isUnderDarcula = true;

    @Override
    protected <T> T internalForCurrentTheme(T[] objects) {
        if (objects == null) return null;
        else if (isUnderDarcula && objects.length > 1) return objects[1];
        else return objects[0];
    }
}
