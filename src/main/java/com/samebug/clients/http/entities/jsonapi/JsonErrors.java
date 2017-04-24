package com.samebug.clients.http.entities.jsonapi;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class JsonErrors<Code extends Enum<Code>> {
    @NotNull
    public final List<SamebugError<Code>> errors;

    public JsonErrors(@NotNull List<SamebugError<Code>> errors) {
        this.errors = errors;
    }

    @NotNull
    public List<Code> getErrorCodes() {
        List<Code> l = new ArrayList<Code>(errors.size());
        for (SamebugError<Code> e : errors) {
            l.add(e.code);
        }
        return l;
    }
}
