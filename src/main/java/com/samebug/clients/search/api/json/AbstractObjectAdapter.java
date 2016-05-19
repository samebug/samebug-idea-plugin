package com.samebug.clients.search.api.json;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Map;

class AbstractObjectAdapter<T> implements JsonDeserializer<T> {
    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json == null) return null;
        else {
            JsonObject jsonObject = json.getAsJsonObject();
            String type = jsonObject.get(typeField).getAsString();

            for (Map.Entry<String, Class<? extends T>> entry : typeClasses.entrySet()) {
                if (entry.getKey().equals(type)) return context.deserialize(jsonObject, entry.getValue());
            }
            throw new JsonParseException("Cannot serialize an abstract " + typeOfT + " with the type " + type);
        }
    }

    protected String typeField = "type";
    protected Map<String, Class<? extends T>> typeClasses;
}
