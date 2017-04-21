package com.samebug.clients.http.entities2.missing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class QualifiedCall {
    private String packageName;
    private String className;
    private String methodName;

    @Nullable
    public String getPackageName() {
        return packageName;
    }

    @NotNull
    public String getClassName() {
        return className;
    }

    @NotNull
    public String getMethodName() {
        return methodName;
    }
}
