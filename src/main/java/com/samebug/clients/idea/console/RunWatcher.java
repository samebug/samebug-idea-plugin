package com.samebug.clients.idea.console;

import com.intellij.execution.Executor;
import com.intellij.execution.console.DuplexConsoleView;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ExecutionConsole;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.execution.ui.RunContentWithExecutorListener;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.util.containers.ArrayListSet;
import com.intellij.util.containers.HashMap;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.entities.search.Requested;
import com.samebug.clients.common.entities.search.Saved;
import com.samebug.clients.common.entities.search.SearchRequest;
import com.samebug.clients.common.entities.search.Searched;
import com.samebug.clients.common.services.RequestService;
import com.samebug.clients.idea.components.project.SamebugProjectComponent;
import com.samebug.clients.idea.messages.console.SearchRequestListener;
import com.samebug.clients.idea.messages.view.FocusListener;
import com.samebug.clients.idea.resources.SamebugIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RunWatcher implements RunContentWithExecutorListener {
    private final Map<ProcessHandler, ConsoleWatcher> consoles;

    public RunWatcher() {
        consoles = new ConcurrentHashMap<ProcessHandler, ConsoleWatcher>();
    }

    @Override
    public void contentSelected(@Nullable RunContentDescriptor descriptor, @NotNull Executor executor) {
        if (descriptor != null && descriptor.getProcessHandler() != null) {
            // TODO this is really fragile as it won't work on custom console implementations
            // TODO bookkeep the RunDebugWatchers, so we can know to which process does a trace belong
            ExecutionConsole console = descriptor.getExecutionConsole();
            ConsoleViewImpl impl;
            if (console instanceof DuplexConsoleView && ((DuplexConsoleView) console).getPrimaryConsoleView() instanceof ConsoleViewImpl) {
                impl = (ConsoleViewImpl) ((DuplexConsoleView) console).getPrimaryConsoleView();
            } else if (console instanceof DuplexConsoleView && ((DuplexConsoleView) console).getSecondaryConsoleView() instanceof ConsoleViewImpl) {
                impl = (ConsoleViewImpl) ((DuplexConsoleView) console).getSecondaryConsoleView();
            } else if (console instanceof ConsoleViewImpl) {
                impl = (ConsoleViewImpl) console;
            } else {
                impl = null;
            }
            if (impl != null) {
                ConsoleWatcher watcher = new ConsoleWatcher(impl);
                consoles.put(descriptor.getProcessHandler(), watcher);
            }
        }
    }

    @Override
    public void contentRemoved(@Nullable RunContentDescriptor descriptor, @NotNull Executor executor) {
        if (descriptor != null) {
            consoles.remove(descriptor.getProcessHandler());
        }
    }
}

class ConsoleWatcher extends DocumentAdapter implements SearchRequestListener {
    private final Logger LOGGER = Logger.getInstance(ConsoleWatcher.class);

    private final Editor editor;
    private final ConsoleViewImpl console;
    private final RequestService requestService;
    private final Map<UUID, RangeHighlighter> highlights;

    public ConsoleWatcher(ConsoleViewImpl console) {
        this.editor = console.getEditor();
        Project project = editor.getProject();
        this.console = console;
        this.requestService = project.getComponent(SamebugProjectComponent.class).getRequestService();
        this.highlights = new ConcurrentHashMap<UUID, RangeHighlighter>();

        editor.getDocument().addDocumentListener(this, console);
        MessageBusConnection messageBusConnection = project.getMessageBus().connect(console);
        messageBusConnection.subscribe(SearchRequestListener.TOPIC, this);
    }

    @Override
    public void documentChanged(DocumentEvent e) {
        rebuildMarkers();
    }

    @Override
    public void saved(final UUID requestId, final Saved savedSearch) {
        final Document document = editor.getDocument();
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                RangeHighlighter highlightForRequest = highlights.get(requestId);
                if (highlightForRequest != null) {
                    // The stacktrace does not begins at the start of the fragment, we have to move the marker
                    int originalStartOffset = highlightForRequest.getStartOffset();
                    int originalStartLine = document.getLineNumber(originalStartOffset);

                    highlightForRequest.dispose();
                    RangeHighlighter newHighlighter = addSavedSearchMarker(originalStartLine, savedSearch);
                    highlights.put(requestId, newHighlighter);
                }
            }
        });
    }

    @Override
    public void searched(UUID requestId) {

    }

    @Override
    public void failed(final UUID requestId) {
        final RangeHighlighter highlightForRequest = highlights.get(requestId);
        if (highlightForRequest != null) {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    highlightForRequest.dispose();
                    highlights.remove(requestId);
                }
            });
        }
    }

    private synchronized void rebuildMarkers() {
        Document document = editor.getDocument();
        final Collection<UUID> lostRequests = new ArrayListSet<UUID>();
        final Map<Integer, UUID> foundRequests = new HashMap<Integer, UUID>();

        // Remove current highlights, and pretend that all traces are lost from the current document
        lostRequests.addAll(highlights.keySet());

        // Try to find traces requested for search in the document
        StringBuilder text = new StringBuilder(document.getText());
        for (Map.Entry<UUID, SearchRequest> traceEntry : requestService.getRequests().entrySet()) {
            final SearchRequest request = traceEntry.getValue();
            final String trace = request.getTrace();
            final int traceStartsAt = text.indexOf(trace);
            final UUID requestId = traceEntry.getKey();
            if (traceStartsAt >= 0) {
                final int traceLine = document.getLineNumber(traceStartsAt);

                // Save to cache that this request was found at that line
                foundRequests.put(traceLine, requestId);
                lostRequests.remove(requestId);

                // Make sure we will not find this part of the document again
                String blank = new String(new char[trace.length()]);
                text = text.replace(traceStartsAt, traceStartsAt + trace.length(), blank);
            }
        }

        for (UUID lostRequestId : lostRequests) {
            requestService.removeRequest(lostRequestId);
        }

        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                for (RangeHighlighter h : highlights.values()) {
                    h.dispose();
                }
                highlights.clear();
                editor.getSettings().setLineMarkerAreaShown(false);

                for (Map.Entry<Integer, UUID> foundRequest : foundRequests.entrySet()) {
                    final RangeHighlighter highlight;
                    int line = foundRequest.getKey();
                    UUID requestId = foundRequest.getValue();
                    SearchRequest request = requestService.getRequest(requestId);
                    if (request instanceof Requested) highlight = addRequestedSearchMarker(line, (Requested) request);
                    else if (request instanceof Saved) highlight = addSavedSearchMarker(line, (Saved) request);
                    else if (request instanceof Searched) highlight = addSearchedSearchMarker(line, (Searched) request);
                    else highlight = null;
                    if (highlight != null) highlights.put(requestId, highlight);
                }
            }
        });
    }

    private RangeHighlighter addRequestedSearchMarker(int line, Requested request) {
        ApplicationManager.getApplication().assertIsDispatchThread();

        editor.getSettings().setLineMarkerAreaShown(true);
        final MarkupModel markupModel = editor.getMarkupModel();
        RangeHighlighter highlighter;
        highlighter = markupModel.addLineHighlighter(line, HighlighterLayer.ADDITIONAL_SYNTAX, null);
        return highlighter;
    }


    private RangeHighlighter addSavedSearchMarker(int line, Saved request) {
        ApplicationManager.getApplication().assertIsDispatchThread();

        final MarkupModel markupModel = editor.getMarkupModel();
        RangeHighlighter highlighter;
        Integer traceLineOffset = request.getSavedSearch().getFirstLine();
        int correctedLine = traceLineOffset == null ? line : line + traceLineOffset;
        highlighter = markupModel.addLineHighlighter(correctedLine, HighlighterLayer.ADDITIONAL_SYNTAX, null);
        highlighter.setGutterIconRenderer(new SavedSearchMark(request));
        return highlighter;
    }

    private RangeHighlighter addSearchedSearchMarker(int line, Searched request) {
        ApplicationManager.getApplication().assertIsDispatchThread();

        final MarkupModel markupModel = editor.getMarkupModel();
        RangeHighlighter highlighter = null;
        return highlighter;
    }
}

class SavedSearchMark extends GutterIconRenderer implements DumbAware {
    private final Saved search;

    public SavedSearchMark(Saved search) {
        this.search = search;
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return SamebugIcons.gutterSamebug;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SavedSearchMark) {
            SavedSearchMark rhs = (SavedSearchMark) o;
            return rhs.search.equals(search);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return search.hashCode();
    }

    @Override
    public boolean isNavigateAction() {
        return true;
    }

    @Override
    @NotNull
    public String getTooltipText() {
        return "Search " + search.getSavedSearch().getSearchId() +
                "\nClick to show solutions.";
    }

    @NotNull
    public AnAction getClickAction() {
        return new AnAction() {
            @Override
            public void actionPerformed(AnActionEvent e) {
                getEventProject(e).getMessageBus().syncPublisher(FocusListener.TOPIC).focusOnSearch(search.getSavedSearch().getSearchId());
            }
        };
    }

}

