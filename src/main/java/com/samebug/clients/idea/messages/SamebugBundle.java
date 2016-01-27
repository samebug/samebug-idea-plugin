package com.samebug.clients.idea.messages;


import com.intellij.AbstractBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public class SamebugBundle extends AbstractBundle {
    private static final SamebugBundle BUNDLE = new SamebugBundle();

    public SamebugBundle() {
        super(PATH_TO_BUNDLE);
    }

    public static final String PATH_TO_BUNDLE = "messages.SamebugBundle";

    public static String message(@NotNull @PropertyKey(resourceBundle = PATH_TO_BUNDLE) String key, @NotNull Object... params) {
        return BUNDLE.getMessage(key, params);
    }
}
