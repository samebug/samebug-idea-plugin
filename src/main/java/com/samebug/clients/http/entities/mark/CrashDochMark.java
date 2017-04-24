package com.samebug.clients.http.entities.mark;

import com.samebug.clients.http.entities.missing.QualifiedCall;
import org.jetbrains.annotations.NotNull;

public final class CrashDochMark extends Mark {
    private QualifiedCall crashedAt;

    @NotNull
    public QualifiedCall getCrashedAt() {
        return crashedAt;
    }
}
