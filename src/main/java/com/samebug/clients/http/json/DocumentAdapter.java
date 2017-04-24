package com.samebug.clients.http.json;

import com.google.common.collect.ImmutableMap;
import com.samebug.clients.http.entities.solution.Document;
import com.samebug.clients.http.entities.solution.ExternalDocument;
import com.samebug.clients.http.entities.solution.SamebugTip;

public class DocumentAdapter extends AbstractObjectAdapter<Document> {
    {
        typeClasses = ImmutableMap.<String, Class<? extends Document>>builder()
                .put("external-document", ExternalDocument.class)
                .put("samebug-tip", SamebugTip.class)
                .build();
    }
}
