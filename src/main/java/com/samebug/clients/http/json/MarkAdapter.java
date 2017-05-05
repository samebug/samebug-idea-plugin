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
package com.samebug.clients.http.json;

import com.google.common.collect.ImmutableMap;
import com.samebug.clients.http.entities.mark.*;

public class MarkAdapter extends AbstractObjectAdapter<Mark> {
    {
        typeClasses = ImmutableMap.<String, Class<? extends Mark>>builder()
                .put("crash-doc--mark", CrashDochMark.class)
                .put("solution--mark", SolutionViewMark.class)
                .put("text-search--mark", TextSearchMark.class)
                .put("stack-trace-search--mark", StackTraceSearchMark.class)
                .build();
    }
}
