package com.samebug.clients.http.json;

import com.google.common.collect.ImmutableMap;
import com.samebug.clients.http.entities2.solution.Document;
import com.samebug.clients.http.entities2.solution.ExternalDocument;
import com.samebug.clients.http.entities2.solution.SamebugTip;

public class DocumentAdapter extends AbstractObjectAdapter<Document> {
    {
        typeClasses = ImmutableMap.<String, Class<? extends Document>>builder()
                .put("external-document", ExternalDocument.class)
                .put("samebug-tip", SamebugTip.class)
                .build();
    }
}
