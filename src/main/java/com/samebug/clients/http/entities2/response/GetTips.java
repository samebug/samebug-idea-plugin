package com.samebug.clients.http.entities2.response;

import com.samebug.clients.http.entities2.jsonapi.JsonResourceWithMeta;
import com.samebug.clients.http.entities2.jsonapi.TotalItems;
import com.samebug.clients.http.entities2.search.SearchHit;
import com.samebug.clients.http.entities2.solution.SamebugTip;

import java.util.List;

public final class GetTips extends JsonResourceWithMeta<List<SearchHit<SamebugTip>>, TotalItems> {
}
