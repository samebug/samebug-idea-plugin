package com.samebug.clients.search.api.json;

import com.google.common.collect.ImmutableMap;
import com.samebug.clients.search.api.entities.RestSolution;
import com.samebug.clients.search.api.entities.SolutionReference;
import com.samebug.clients.search.api.entities.Tip;

final class RestSolutionAdapter extends AbstractObjectAdapter<RestSolution> {
    {
        typeClasses = ImmutableMap.<String, Class<? extends RestSolution>>builder()
                .put("reference", SolutionReference.class)
                .put("tip", Tip.class)
                .build();
    }
}
