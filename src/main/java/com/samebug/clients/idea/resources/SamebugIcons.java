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

public class SamebugIcons {
    public final static Icon notification = IconLoader.getIcon("/com/samebug/icons/samebug-32x32.png");

    public final static Icon tab = IconLoader.getIcon("/com/samebug/icons/samebug-r270-13x13.png");

    public final static Icon statusOk = IconLoader.getIcon("/com/samebug/icons/status-ok-16x16.png");
    public final static Icon statusNotConnected = IconLoader.getIcon("/com/samebug/icons/status-not-connected-16x16.png");
    public final static Icon statusInvalidApiKey = IconLoader.getIcon("/com/samebug/icons/status-invalid-apikey-16x16.png");
}
