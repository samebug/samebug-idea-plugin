package com.samebug.clients.http.entities.response;

import com.samebug.clients.http.entities.jsonapi.JsonResourceWithMeta;
import com.samebug.clients.http.entities.jsonapi.TotalItems;
import com.samebug.clients.http.entities.search.SearchHit;
import com.samebug.clients.http.entities.solution.SamebugTip;

import java.util.List;

public final class GetTips extends JsonResourceWithMeta<List<SearchHit<SamebugTip>>, TotalItems> {
}
