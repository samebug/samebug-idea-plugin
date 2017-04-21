package com.samebug.clients.http.entities2.mark;

import com.samebug.clients.http.entities2.missing.QualifiedCall;
import org.jetbrains.annotations.NotNull;

public final class CrashDochMark extends Mark {
    private QualifiedCall crashedAt;

    @NotNull
    public QualifiedCall getCrashedAt() {
        return crashedAt;
    }
}
