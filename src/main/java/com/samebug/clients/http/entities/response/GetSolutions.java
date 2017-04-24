package com.samebug.clients.http.entities.response;

import com.samebug.clients.http.entities.jsonapi.JsonResourceWithMeta;
import com.samebug.clients.http.entities.jsonapi.TotalItems;
import com.samebug.clients.http.entities.search.SearchHit;
import com.samebug.clients.http.entities.solution.ExternalDocument;

import java.util.List;

public final class GetSolutions extends JsonResourceWithMeta<List<SearchHit<ExternalDocument>>, TotalItems> {
}
