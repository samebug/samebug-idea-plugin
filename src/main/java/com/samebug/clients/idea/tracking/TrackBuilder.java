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
package com.samebug.clients.idea.tracking;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.samebug.clients.common.api.entities.tracking.TrackEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Use TrackBuilder to make sure that the creation of a TrackEvent will not leak exception to the caller.
 */
public class TrackBuilder {
    private final static Logger LOGGER = Logger.getInstance(TrackBuilder.class);

    private final Map<String, Object> fields;
    final Map<String, Object> data;

    public TrackBuilder(String category, String action) {
        this(category, action, null);
    }

    public TrackBuilder(String category, String action, Project project) {
        fields = new HashMap<String, Object>();
        data = new HashMap<String, Object>();
        fields.put("category", category);
        fields.put("action", action);
        if (project != null) fields.put("project", projectData(project));
    }

    protected void initDataFields() {
    }

    protected void add(String fieldName, Object value) {
        data.put(fieldName, value);
    }

    final public TrackEvent getEvent() {
        try {
            initDataFields();
            fields.put("data", data);
            return new TrackEvent(fields);
        } catch (Exception e) {
            LOGGER.debug("Failed to send tracking event", e);
            return null;
        }
    }

    public static Map<String, Object> projectData(Project project) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("name", project.getName());
        return m;
    }
}
