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
package com.samebug.clients.swing.ui.modules;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public abstract class MessageService {
    private static MessageService INSTANCE = null;
    // NOTE we can slice it to parts, e.g. one for component labels, one for intro texts, etc.
    public static final String PATH_TO_BUNDLE = "com.samebug.messages.Messages";

    public static void install(MessageService instance) {
        assert INSTANCE == null : "MessageService has already been installed";
        INSTANCE = instance;
    }

    public static String message(@NotNull @PropertyKey(resourceBundle = PATH_TO_BUNDLE) String key, @NotNull Object... params) {
        return INSTANCE.internalMessage(key, params);
    }

    protected abstract String internalMessage(String key, Object... params);
}
