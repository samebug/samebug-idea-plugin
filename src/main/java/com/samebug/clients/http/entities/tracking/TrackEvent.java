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
package com.samebug.clients.http.entities.tracking;

import java.util.Map;

public final class TrackEvent {
    public TrackEvent(Map<String, Object> fields) {
        this.fields = fields;
    }

    public Map<String, Object> fields;

    @SuppressWarnings("unchecked")
    public String unsafePresentation() {
        StringBuilder b = new StringBuilder();
        b.append(fields.get("category"));
        b.append(" - ");
        b.append(fields.get("action"));
        Object data = fields.get("data");
        if (data != null) {
            assert data instanceof Map;
            Map<String, Object> dataMap = (Map<String, Object>) data;
            if (!dataMap.isEmpty()) {
                b.append(" [");
                for (Map.Entry<String, Object> dataField : dataMap.entrySet()) {
                    b.append(dataField.getKey());
                    b.append(": ");
                    b.append(dataField.getValue());
                    b.append(", ");
                }
                b.setLength(b.length() - 2);
                b.append("]");
            }
        }
        return b.toString();
    }
}
