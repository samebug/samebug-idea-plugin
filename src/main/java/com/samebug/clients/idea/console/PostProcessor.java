package com.samebug.clients.idea.console;

import com.intellij.execution.ExecutionManager;
import com.intellij.execution.RunManager;
import com.intellij.execution.actions.ConsoleActionsPostProcessor;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorGutterAction;
import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.vcs.actions.ActiveAnnotationGutter;
import com.samebug.clients.common.services.SearchStore;
import com.samebug.clients.idea.components.project.BatchStackTraceSearchNotifier;
import com.samebug.clients.idea.components.project.SamebugProjectComponent;
import com.samebug.clients.search.api.entities.SearchResults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PostProcessor extends ConsoleActionsPostProcessor {
    public AnAction[] postProcess(@NotNull ConsoleView console, @NotNull AnAction[] actions) {

        Editor editor = ((ConsoleViewImpl) console).getEditor();
        ConsoleWatcher consoleWatcher = new ConsoleWatcher();
        Annotation annotation = new Annotation(consoleWatcher);

        ProcessHandler[] x = ExecutionManager.getInstance(((ConsoleViewImpl) console).getProject()).getRunningProcesses();
        Logger.getInstance(this.getClass()).warn(x.length + " running processes");
        for (ProcessHandler i : x) { Logger.getInstance(this.getClass()).warn(i.toString()); }
        editor.getDocument().addDocumentListener(consoleWatcher);
        editor.getGutter().registerTextAnnotation(annotation, annotation);
        return actions;
    }
}

class ConsoleWatcher extends DocumentAdapter {
    private final Logger LOGGER = Logger.getInstance(ConsoleWatcher.class);

    List<Integer> exceptionLines = new ArrayList<Integer>();
    @Override
    public void documentChanged(DocumentEvent e) {
        LOGGER.info("Document change from offset " + e.getOffset());
        String x = e.getNewFragment().toString();
        if (x.length() > 60) {
            LOGGER.info(x.substring(0, 30) + "<... " + x.length() + " characters total ...>" + x.substring(x.length() - 30));
        } else {
            LOGGER.info(x);
        }
        if (e.isWholeTextReplaced()) LOGGER.info("whole text replaced");

        exceptionLines.clear();
        Document document = e.getDocument();
        int i = document.getText().indexOf("Exception");
        while (i > 0) {
            exceptionLines.add(document.getLineNumber(i));
            i = document.getText().indexOf("Exception", i + 1);
        }
    }
}

class Annotation implements ActiveAnnotationGutter, EditorGutterAction {
    private final ConsoleWatcher watcher;
    public Annotation(ConsoleWatcher watcher) {
        this.watcher = watcher;
    }

    @Override
    public void doAction(int i) {

    }

    @Override
    public Cursor getCursor(int i) {
        return null;
    }

    @Nullable
    @Override
    public String getLineText(int i, Editor editor) {
        SearchStore searchStore = editor.getProject().getComponent(SamebugProjectComponent.class).getSearchStore();

        Document document = editor.getDocument();
        String text = document.getText().substring(document.getLineStartOffset(i));
        for(Map.Entry<UUID, String> traceEntry : searchStore.getTraces().entrySet()) {
            if (text.startsWith(traceEntry.getValue())) {
                return "X";
            } else {
                continue;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public String getToolTip(int i, Editor editor) {
        SearchStore searchStore = editor.getProject().getComponent(SamebugProjectComponent.class).getSearchStore();

        Document document = editor.getDocument();
        String text = document.getText().substring(document.getLineStartOffset(i));
        for(Map.Entry<UUID, String> traceEntry : searchStore.getTraces().entrySet()) {
            if (text.startsWith(traceEntry.getValue())) {
                SearchResults searchResult = searchStore.getResult(traceEntry.getKey());
                if (searchResult != null) {
                    return "Search " + searchResult.getSearchId();
                } else {
                    return "Search in progress...";
                }
            } else {
                continue;
            }
        }
        return null;
    }

    @Override
    public EditorFontType getStyle(int i, Editor editor) {
        return null;
    }

    @Nullable
    @Override
    public ColorKey getColor(int i, Editor editor) {
        return null;
    }

    @Nullable
    @Override
    public Color getBgColor(int i, Editor editor) {
        return null;
    }

    @Override
    public List<AnAction> getPopupActions(int i, Editor editor) {
        return null;
    }

    @Override
    public void gutterClosed() {

    }
}