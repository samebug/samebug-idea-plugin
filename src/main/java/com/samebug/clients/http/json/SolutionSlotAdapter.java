package com.samebug.clients.http.json;

import com.google.common.collect.ImmutableMap;
import com.google.gson.reflect.TypeToken;
import com.samebug.clients.http.entities.solution.Document;
import com.samebug.clients.http.entities.solution.ReadableSolution;
import com.samebug.clients.http.entities.solution.SearchableSolution;
import com.samebug.clients.http.entities.solution.SolutionSlot;

public class SolutionSlotAdapter extends AbstractGenericObjectAdapter<SolutionSlot<Document>> {
    {
        typeClasses = ImmutableMap.<String, TypeToken<? extends SolutionSlot<Document>>>builder()
                .put("solution-readable", new TypeToken<ReadableSolution<Document>>() {})
                .put("solution-searchable", new TypeToken<SearchableSolution<Document>>() {})
                .build();
    }
}
