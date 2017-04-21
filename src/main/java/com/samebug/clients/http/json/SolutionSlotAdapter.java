package com.samebug.clients.http.json;

import com.google.common.collect.ImmutableMap;
import com.samebug.clients.http.entities2.solution.*;

public class SolutionSlotAdapter extends AbstractObjectAdapter<SolutionSlot> {
    {
        typeClasses = ImmutableMap.<String, Class<? extends SolutionSlot>>builder()
                .put("solution-clone", SolutionClone.class)
                .put("solution-readable", ReadableSolution.class)
                .put("solution-searchable", SearchableSolution.class)
                .put("solution-nonsearchable", NonSearchableSolution.class)
                .put("solution-nonaccessible", NonAccessibleSolution.class)
                .build();
    }
}
