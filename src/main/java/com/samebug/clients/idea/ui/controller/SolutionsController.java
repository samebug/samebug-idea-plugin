package com.samebug.clients.idea.ui.controller;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.services.SolutionsService;
import com.samebug.clients.idea.components.application.ClientService;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.messages.client.SolutionsModelListener;
import com.samebug.clients.idea.messages.view.FocusListener;
import com.samebug.clients.idea.messages.view.MarkViewListener;
import com.samebug.clients.idea.messages.view.SearchViewListener;
import com.samebug.clients.idea.messages.view.SolutionsViewListener;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.component.card.ExternalSolutionView;
import com.samebug.clients.idea.ui.component.card.SamebugTipView;
import com.samebug.clients.idea.ui.component.organism.MarkPanel;
import com.samebug.clients.idea.ui.component.tab.SearchTabView;
import com.samebug.clients.search.api.entities.*;
import com.samebug.clients.search.api.exceptions.BadRequest;
import com.samebug.clients.search.api.exceptions.SamebugClientException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SolutionsController implements SolutionsModelListener, SolutionsViewListener, SearchViewListener, MarkViewListener {
    final static Logger LOGGER = Logger.getInstance(SearchTabController.class);
    @NotNull
    final Project project;
    @NotNull
    final SolutionsService solutionsService;

    @NotNull
    final ConcurrentMap<Integer, SearchTabView> tabs;

    public SolutionsController(@NotNull final Project project) {
        this.project = project;
        // TODO status updater
        solutionsService = ServiceManager.getService(project, SolutionsService.class);
        MessageBusConnection projectMessageBus = project.getMessageBus().connect(project);
        projectMessageBus.subscribe(SolutionsViewListener.TOPIC, this);
        projectMessageBus.subscribe(SolutionsModelListener.TOPIC, this);
        projectMessageBus.subscribe(SearchViewListener.TOPIC, this);
        projectMessageBus.subscribe(MarkViewListener.TOPIC, this);
        tabs = new ConcurrentHashMap<Integer, SearchTabView>();
    }


    @Override
    public void start(final int searchId) {

    }

    @Override
    public void success(final int searchId, final Solutions result) {
        solutionsService.setSolutions(searchId, result);
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                refreshTab(searchId);
            }
        });
    }

    @Override
    public void fail(final int searchId, final java.lang.Exception e) {

    }

    @Override
    public void finish(final int searchId) {
    }

    @Nullable
    public SearchTabView getTab(final int searchId) {
        return tabs.get(searchId);
    }

    public void open(final int searchId) {
        if (!tabs.containsKey(searchId)) {
            tabs.put(searchId, new SearchTabView());
            ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                @Override
                public void run() {
                    ClientService client = IdeaSamebugPlugin.getInstance().getClient();
                    try {
                        client.getSolutions(searchId);
                    } catch (SamebugClientException e1) {
                        // TODO log?
                    }
                }
            });
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    project.getMessageBus().syncPublisher(FocusListener.TOPIC).focusOnSearch(searchId);
                }
            });
        }
    }

    public void close(final int searchId) {
        tabs.remove(searchId);
    }


    @Override
    public void reload() {
        // TODO
    }

    void refreshTab(final int searchId) {
        ApplicationManager.getApplication().assertIsDispatchThread();
        final SearchTabView view = tabs.get(searchId);
        final Solutions solutions = solutionsService.getSolutions(searchId);

        // TODO connection status stuff
        if (solutions != null) {
            view.setSolutions(convertSolutions(solutions));
        } else {
            view.setWarningOther();
        }

        view.revalidate();
        view.repaint();
    }

    private SearchTabView.Model convertSolutions(@NotNull final Solutions solutions) {
        return new SearchTabView.Model() {

            @Override
            public SearchGroup getSearch() {
                return solutions.searchGroup;
            }

            @Override
            public List<ExternalSolutionView.Model> getReferences() {
                List<ExternalSolutionView.Model> result = new ArrayList<ExternalSolutionView.Model>(solutions.references.size());
                for (RestHit<SolutionReference> reference : solutions.references) {
                    result.add(convertReference(solutions.searchGroup, reference));
                }
                return result;
            }

            @Override
            public List<SamebugTipView.Model> getTips() {
                List<SamebugTipView.Model> result = new ArrayList<SamebugTipView.Model>(solutions.tips.size());
                for (RestHit<Tip> tip : solutions.tips) {
                    result.add(convertTip(solutions.searchGroup, tip));
                }
                return result;
            }
        };
    }

    // TODO reuse the two specifications
    private MarkPanel.Model convertHit(final SearchGroup search, final RestHit hit) {
        return new MarkPanel.Model() {

            @NotNull
            @Override
            public RestHit getHit() {
                return hit;
            }

            @NotNull
            @Override
            public int getSearchId() {
                return search.getLastSearch().id;
            }

            @Override
            public boolean canBeMarked() {
                return solutionsService.canBeMarked(IdeaSamebugPlugin.getInstance().getState().userId, search, hit);
            }

            @Override
            public boolean createdByCurrentUser() {
                // TODO
                return false;
            }
        };
    }

    private SamebugTipView.Model convertTip(final SearchGroup search, final RestHit<Tip> hit) {
        return new SamebugTipView.Model() {
            @NotNull
            @Override
            public RestHit<Tip> getHit() {
                return hit;
            }

            @NotNull
            @Override
            public int getSearchId() {
                return search.getLastSearch().id;
            }

            @NotNull
            @Override
            public List<BreadCrumb> getMatchingBreadCrumb() {
                if (search instanceof StackTraceSearchGroup) {
                    return ((StackTraceSearchGroup) search).lastSearch.stackTrace.breadCrumbs.subList(0, hit.matchLevel);
                } else {
                    return Collections.emptyList();
                }
            }

            @Override
            public boolean canBeMarked() {
                return solutionsService.canBeMarked(IdeaSamebugPlugin.getInstance().getState().userId, search, hit);
            }

            @Override
            public boolean createdByCurrentUser() {
                // a tip should always have a creator
                // TODO createdBy or author?
                assert hit.createdBy != null;
                return hit.createdBy.id.equals(IdeaSamebugPlugin.getInstance().getState().userId);
            }
        };
    }

    private ExternalSolutionView.Model convertReference(final SearchGroup search, final RestHit<SolutionReference> hit) {
        return new ExternalSolutionView.Model() {

            @NotNull
            @Override
            public RestHit<SolutionReference> getHit() {
                return hit;
            }

            @NotNull
            @Override
            public int getSearchId() {
                return search.getLastSearch().id;
            }

            @Override
            public boolean canBeMarked() {
                return solutionsService.canBeMarked(IdeaSamebugPlugin.getInstance().getState().userId, search, hit);
            }

            @Override
            public boolean createdByCurrentUser() {
                return false;
            }

            @NotNull
            @Override
            public List<BreadCrumb> getMatchingBreadCrumb() {
                if (search instanceof StackTraceSearchGroup) {
                    return ((StackTraceSearchGroup) search).lastSearch.stackTrace.breadCrumbs.subList(0, hit.matchLevel);
                } else {
                    return Collections.emptyList();
                }
            }
        };
    }

    @Override
    public void mark(final int searchId, final int solutionId, final boolean up, final MarkPanel markPanel) {
        ApplicationManager.getApplication().assertIsDispatchThread();
        markPanel.beginPostMark();
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                try {
                    ClientService client = IdeaSamebugPlugin.getInstance().getClient();
                    final MarkResponse mark;
                    final RestHit hit = null;
                    final SearchGroup search = null;
                    if (up) {
                        mark = client.postMark(0, solutionId);
                    } else {
                        mark = client.retractMark(hit.markId);
                    }
                    // TODO update model
                    //Tracking.projectTracking(project).trace(Events.markSubmit(project, search.id, hit.solutionId, hit.markId == null ? "null" : hit.markId.toString()));
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            markPanel.finishPostMarkWithSuccess(convertHit(search, hit));
                        }
                    });
                } catch (final BadRequest e) {
                    final String errorMessageKey;
                    final String markErrorCode = e.getRestError().code;
                    if ("ALREADY_MARKED".equals(markErrorCode)) errorMessageKey = "samebug.mark.error.alreadyMarked";
                    else if ("NOT_YOUR_SEARCH".equals(markErrorCode)) errorMessageKey = "samebug.mark.error.notYourSearch";
                    else if ("NOT_YOUR_MARK".equals(markErrorCode)) errorMessageKey = "samebug.mark.error.notYourMark";
                    else if ("ALREADY_CANCELLED".equals(markErrorCode)) errorMessageKey = "samebug.mark.error.alreadyCancelled";
                    else errorMessageKey = "samebug.mark.error.unhandledBadRequest";
//                    Tracking.projectTracking(project).trace(Events.markSubmit(project, search.id, hit.solutionId, errorMessageKey));
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            markPanel.finishPostMarkWithError(SamebugBundle.message(errorMessageKey));
                        }
                    });

                } catch (final SamebugClientException e) {
//                    Tracking.projectTracking(project).trace(Events.markSubmit(project, search.id, hit.solutionId, "samebug.mark.error.unhandled"));
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            markPanel.finishPostMarkWithError(SamebugBundle.message("samebug.mark.error.unhandled"));
                        }
                    });
                }
            }
        });

    }
}
