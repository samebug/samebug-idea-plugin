package com.samebug.clients.http.entities.response;

import com.samebug.clients.http.entities.bugmate.BugmateMatch;
import com.samebug.clients.http.entities.jsonapi.JsonResourceWithMeta;
import com.samebug.clients.http.entities.jsonapi.TotalItems;

import java.util.List;

public final class GetBugmates extends JsonResourceWithMeta<List<BugmateMatch>, TotalItems> {
}
