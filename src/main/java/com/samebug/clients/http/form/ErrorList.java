package com.samebug.clients.http.form;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class ErrorList<Code extends Enum<Code>> {
    @NotNull
    public final List<Error<Code>> errors;

    public ErrorList(@NotNull List<Error<Code>> errors) {
        this.errors = errors;
    }

    @NotNull
    public List<Code> getErrorCodes() {
        List<Code> l = new ArrayList<Code>(errors.size());
        for (Error<Code> e : errors) {
            l.add(e.code);
        }
        return l;
    }
}
