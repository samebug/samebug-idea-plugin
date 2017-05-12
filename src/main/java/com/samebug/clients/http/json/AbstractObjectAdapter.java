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

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Map;

class AbstractObjectAdapter<T> implements JsonDeserializer<T> {
    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json == null) return null;
        else {
            JsonObject jsonObject = json.getAsJsonObject();
            JsonElement typeFieldValue = jsonObject.get(typeField);
            if (typeFieldValue == null) throw new JsonParseException("Cannot serialize " + typeOfT + " because the type field" + typeField + " is missing");
            else {
                String type = jsonObject.get(typeField).getAsString();

                for (Map.Entry<String, Class<? extends T>> entry : typeClasses.entrySet()) {
                    if (entry.getKey().equals(type)) return context.deserialize(jsonObject, entry.getValue());
                }
                throw new JsonParseException("Cannot serialize an abstract " + typeOfT + " with the type " + type);
            }
        }
    }

    protected String typeField = "type";
    protected Map<String, Class<? extends T>> typeClasses;
}
