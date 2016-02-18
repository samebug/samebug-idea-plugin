package com.samebug.clients.search.api.entities;

import java.util.Map;

/**
 * Created by poroszd on 2/18/16.
 */
public class TrackEvent {
    public TrackEvent(Map<String, String> fields) {
        this.fields = fields;
    }

    public Map<String, String> fields;
}
