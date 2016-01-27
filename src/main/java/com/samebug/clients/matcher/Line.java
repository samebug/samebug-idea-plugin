package com.samebug.clients.matcher;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;

import static com.samebug.clients.matcher.LineType.*;

abstract class Line {
    @NotNull
    public LineType getType() {
        return type;
    }

    @NotNull
    public String getRaw() {
        return raw;
    }

    @NotNull
    private final String raw;

    @NotNull
    private final LineType type;

    protected Line(@NotNull LineType type, @NotNull String raw) {
        this.raw = raw;
        this.type = type;
    }
}


class StackFrame extends Line {
    public StackFrame(LineType type, Matcher matcher) {
        this(type, matcher.group(0), matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4));
    }

    private StackFrame(LineType type, @NotNull String line, @Nullable String packageName, @NotNull String className, @NotNull String methodName, @NotNull String location) {
        super(StackFrameType, line);
        this.packageName = packageName;
        this.className = className;
        this.methodName = methodName;
        this.location = location;
    }

    @Nullable
    public final String packageName;

    @NotNull
    public final String className;

    @NotNull
    public final String methodName;

    @NotNull
    public final String location;
}

class ExceptionStart extends Line {
    public ExceptionStart(LineType type, Matcher matcher) {
        this(type, matcher.group(0), matcher.group(1), matcher.group(2), type == ExceptionStartTypeWithMessage ? matcher.group(3) : null);
    }

    private ExceptionStart(LineType type,@NotNull String line, @NotNull String packageName, @NotNull String className, @Nullable String message) {
        super(message == null ? ExceptionStartTypeWithoutMessage : ExceptionStartTypeWithMessage, line);

        this.packageName = packageName;
        this.className = className;
        this.message = message;
    }

    @NotNull
    public final String packageName;

    @NotNull
    public final String className;

    @Nullable
    public final String message;

}

class CausedBy extends Line {
    public CausedBy(LineType type, Matcher matcher) {
        this(type, matcher.group(0), matcher.group(1), matcher.group(2), type == CausedByTypeWithMessage ? matcher.group(3) : null);
    }

    private CausedBy(LineType type, @NotNull String line, @NotNull String packageName, @NotNull String className, @Nullable String message) {
        super(type, line);
        this.packageName = packageName;
        this.className = className;
        this.message = message;
    }

    @NotNull
    public final String packageName;

    @NotNull
    public final String className;

    @Nullable
    public final String message;
}


class More extends Line {
    public final int commonFrames;

    private More(LineType type,@NotNull String line, int commonFrames) {
        super(MoreType, line);
        this.commonFrames = commonFrames;
    }

    public More(LineType type,Matcher matcher) {
        this(type, matcher.group(0), Integer.parseInt(matcher.group(1)));
    }
}

class Message extends Line {
    public Message(LineType type,Matcher matcher) {
        this(type, matcher.group(0));
    }
    private Message(LineType type,String text) {
        super(type, text);
    }
}