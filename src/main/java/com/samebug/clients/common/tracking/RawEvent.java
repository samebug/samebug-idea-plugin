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
package com.samebug.clients.common.tracking;

import com.samebug.clients.http.entities.authentication.AuthenticationResponse;
import com.samebug.clients.http.entities.tracking.TrackEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class RawEvent {
    protected final Map<String, Object> fields;
    protected final Map<String, Object> data;

    protected RawEvent(@NotNull String category, @NotNull String action) {
        fields = new HashMap<String, Object>();
        data = new HashMap<String, Object>();
        fields.put("category", category);
        fields.put("action", action);
    }

    protected void lazyFields() {
    }

    @NotNull
    public RawEvent withData(@NotNull String fieldName, @Nullable Object value) {
        if (value != null) data.put(fieldName, value);
        return this;
    }

    @NotNull
    public RawEvent withField(@NotNull String fieldName, @Nullable Object value) {
        if (value != null) fields.put(fieldName, value);
        return this;
    }

    public TrackEvent getEvent() {
        try {
            lazyFields();
        } catch (Exception ignored) {
        }
        fields.put("data", data);
        return new TrackEvent(fields);
    }

    @NotNull
    protected RawEvent withAuthenticationResponse(@NotNull AuthenticationResponse response) {
        Map<String, String> responseData = new HashMap<String, String>(2);
        responseData.put("type", response.getType());
        responseData.put("provider", response.getProvider());
        withData("response", responseData);
        return this;
    }

    @NotNull
    protected RawEvent withFunnel(@NotNull Funnels.Instance funnel) {
        withField("funnelId", funnel.funnelId);
        withField("transactionId", funnel.transactionId);
        return this;
    }


    @SuppressWarnings("unchecked")
    public String unsafePresentation() {
        StringBuilder b = new StringBuilder();
        b.append(fields.get("category"));
        b.append(" - ");
        b.append(fields.get("action"));
        if (!data.isEmpty()) {
            b.append(" [");
            for (Map.Entry<String, Object> dataField : data.entrySet()) {
                b.append(dataField.getKey());
                b.append(": ");
                b.append(dataField.getValue());
                b.append(", ");
            }
            b.setLength(b.length() - 2);
            b.append("]");
        }
        return b.toString();
    }
}
