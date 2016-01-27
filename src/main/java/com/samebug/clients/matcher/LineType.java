package com.samebug.clients.matcher;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

enum LineType {
    StackFrameType(StackFrame.class, "\\s*at ([\\w\\.]*\\.)?([\\w\\$]+\\.)([\\w\\$<>]+)(\\([\\w\\s\\:\\.]*\\)).*"),
    CausedByTypeWithMessage(CausedBy.class, "\\s*Caused by\\:\\s([\\w\\.]*\\.)(\\p{Lu}[\\w\\$]+\\:)\\s(.*)"),
    CausedByTypeWithoutMessage(CausedBy.class, "\\s*Caused by\\:\\s([\\w\\.]*\\.)(\\p{Lu}[\\w\\$]+)(?:\\s*)"),
    ExceptionStartTypeWithMessage(ExceptionStart.class, "(?:.+\\s)?([\\w\\.]*\\.)(\\p{Lu}[\\w\\$]+\\:)\\s(.*)"),
    ExceptionStartTypeWithoutMessage(ExceptionStart.class, "(?:.+\\s)?([\\w\\.]*\\.)(\\p{Lu}[\\w\\$]+)(?:\\s*)"),
    MoreType(More.class, "(?:.+\\s)?\\.{3} (\\d+) more\\s*"),
    MessageType(Message.class, ".*");

    private final Pattern pattern;
    private final Class<? extends Line> lineClass;

    LineType(Class<? extends Line> lineClass, String regex) {
        this.lineClass = lineClass;
        this.pattern = Pattern.compile(regex, Pattern.DOTALL);
    }

    public Line match(String line) {
        Matcher matcher = this.pattern.matcher(line);
        if (!matcher.matches()) return null;
        return convertMatch(matcher);
    }

    @NotNull
    private Line convertMatch(Matcher matcher) {
        try {
            return lineClass.getConstructor(LineType.class, Matcher.class).newInstance(this, matcher);
        } catch (InstantiationException e) {
            throw new Error("Unable to create line " + lineClass.getSimpleName(), e);
        } catch (IllegalAccessException e) {
            throw new Error("Unable to create line " + lineClass.getSimpleName(), e);
        } catch (InvocationTargetException e) {
            throw new Error("Unable to create line " + lineClass.getSimpleName(), e);
        } catch (NoSuchMethodException e) {
            throw new Error("Unable to create line " + lineClass.getSimpleName(), e);
        }
    }
}
