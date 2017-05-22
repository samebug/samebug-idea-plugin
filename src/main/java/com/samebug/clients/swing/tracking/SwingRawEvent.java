/*
 * Copyright 2017 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *    http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.swing.tracking;

import com.samebug.clients.common.tracking.Funnel;
import com.samebug.clients.common.tracking.Location;
import com.samebug.clients.common.tracking.RawEvent;
import com.samebug.clients.http.entities.authentication.AuthenticationResponse;
import com.samebug.clients.http.entities.search.Search;
import com.samebug.clients.http.entities.search.SearchHit;
import com.samebug.clients.http.entities.solution.SamebugTip;
import com.samebug.clients.swing.ui.modules.DataService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.net.URI;

public class SwingRawEvent extends RawEvent {
    // Funnels
    // Authentication funnel
    public static RawEvent authenticationHookTriggered(@Nullable final JComponent source, @Nullable final String transactionId) {
        if (source != null) {
            return new SwingRawEvent("Authentication", "HookTrigger", source) {
                protected void myLazyFields() {
                    withFunnel(new Funnel.Instance(Funnel.AUTHENTICATION, transactionId));
                }
            };
        } else {
            return new RawEvent("Authentication", "HookTrigger") {
                protected void lazyFields() {
                    withFunnel(new Funnel.Instance(Funnel.AUTHENTICATION, transactionId));
                }
            };
        }
    }

    public static RawEvent authenticationSubmit(@NotNull final JComponent source, @Nullable final String transactionId) {
        return new SwingRawEvent("Authentication", "HookTrigger", source) {
            protected void myLazyFields() {
                withFunnel(new Funnel.Instance(Funnel.AUTHENTICATION, transactionId));
            }
        };
    }

    public static RawEvent authenticationSucceeded(@NotNull final JComponent source, @Nullable final String transactionId, @NotNull final AuthenticationResponse response) {
        return new SwingRawEvent("Authentication", "HookTrigger", source) {
            protected void myLazyFields() {
                withAuthenticationResponse(response);
                withFunnel(new Funnel.Instance(Funnel.AUTHENTICATION, transactionId));
            }
        };
    }


    // Help request funnel
    public static RawEvent helpRequestHookTrigger(@NotNull final JComponent source, @Nullable final String transactionId) {
        return new SwingRawEvent("HelpRequest", "HookTrigger", source) {
            protected void myLazyFields() {
                withFunnel(new Funnel.Instance(Funnel.HELP_REQUEST, transactionId));
            }
        };
    }

    public static RawEvent helpRequestSubmit(@NotNull final JComponent source, @Nullable final String transactionId) {
        return new SwingRawEvent("HelpRequest", "Submit", source) {
            protected void myLazyFields() {
                withFunnel(new Funnel.Instance(Funnel.HELP_REQUEST, transactionId));
            }
        };
    }

    public static RawEvent helpRequestCreate(@NotNull final JComponent source, @Nullable final String transactionId, @NotNull final String helpRequestId) {
        return new SwingRawEvent("HelpRequest", "Create", source) {
            protected void myLazyFields() {
                withFunnel(new Funnel.Instance(Funnel.HELP_REQUEST, transactionId));
                withData("helpRequestId", helpRequestId);
            }
        };
    }


    // Write tip funnel
    public static RawEvent writeTipHookTrigger(@NotNull final JComponent source, @Nullable final String transactionId, @Nullable final String helpRequestId) {
        return new SwingRawEvent("WriteTip", "HookTrigger", source) {
            protected void myLazyFields() {
                withFunnel(new Funnel.Instance(Funnel.WRITE_TIP, transactionId));
                withData("helpRequestId", helpRequestId);
            }
        };
    }

    public static RawEvent writeTipSubmit(@NotNull final JComponent source, @Nullable final String transactionId) {
        return new SwingRawEvent("WriteTip", "Submit", source) {
            protected void myLazyFields() {
                withFunnel(new Funnel.Instance(Funnel.WRITE_TIP, transactionId));
            }
        };
    }

    public static RawEvent writeTipCreate(@NotNull final JComponent source, @Nullable final String transactionId, @NotNull final SearchHit<SamebugTip> response) {
        return new SwingRawEvent("WriteTip", "Submit", source) {
            protected void myLazyFields() {
                withFunnel(new Funnel.Instance(Funnel.WRITE_TIP, transactionId));
                withData("solutionId", response.getSolution().getId());
                withData("documentId", response.getSolution().getDocument().getId());
            }
        };
    }

    // Solution funnel
    public static RawEvent solutionDisplay(@NotNull final JComponent source, @Nullable final String transactionId) {
        return new SwingRawEvent("Solution", "Display", source) {
            protected void myLazyFields() {
                withFunnel(new Funnel.Instance(Funnel.SOLUTION, transactionId));
                withHit();
            }
        };
    }

    public static RawEvent solutionClick(@NotNull final JComponent source, @Nullable final String transactionId) {
        return new SwingRawEvent("Solution", "Click", source) {
            protected void myLazyFields() {
                withFunnel(new Funnel.Instance(Funnel.SOLUTION, transactionId));
                withHit();
            }
        };
    }

    public static RawEvent markSubmit(@NotNull final JComponent source, @Nullable final String transactionId) {
        return new SwingRawEvent("Mark", "Submit", source) {
            protected void myLazyFields() {
                withFunnel(new Funnel.Instance(Funnel.SOLUTION, transactionId));
                withHit();
            }
        };
    }

    public static RawEvent markCreate(@NotNull final JComponent source, @Nullable final String transactionId, @NotNull final Integer markId) {
        return new SwingRawEvent("Mark", "Create", source) {
            protected void myLazyFields() {
                withFunnel(new Funnel.Instance(Funnel.SOLUTION, transactionId));
                withHit();
            }
        };
    }

    public static RawEvent markCancelSubmit(@NotNull final JComponent source, @Nullable final String transactionId, @NotNull final Integer markId) {
        return new SwingRawEvent("Mark", "Cancel", source) {
            protected void myLazyFields() {
                withFunnel(new Funnel.Instance(Funnel.SOLUTION, transactionId));
                withHit();
                withData("cancelledMarkId", markId);
            }
        };
    }

    public static RawEvent markCancel(@NotNull final JComponent source, @Nullable final String transactionId, @NotNull final Integer markId) {
        return new SwingRawEvent("Mark", "Cancel", source) {
            protected void myLazyFields() {
                withFunnel(new Funnel.Instance(Funnel.SOLUTION, transactionId));
                withHit();
                withData("cancelledMarkId", markId);
            }
        };
    }

    // Search funnel
    public static RawEvent searchHookTrigger(@Nullable final String transactionId) {
        return new RawEvent("StackTraceSearch", "HookTrigger") {
            protected void myLazyFields() {
                withFunnel(new Funnel.Instance(Funnel.SEARCH, transactionId));
            }
        };
    }

    public static RawEvent searchQueryChanged(@NotNull final JComponent source, @Nullable final String transactionId, final Boolean isValid) {
        return new SwingRawEvent("StackTraceSearch", "QueryChange", source) {
            protected void myLazyFields() {
                withFunnel(new Funnel.Instance(Funnel.SEARCH, transactionId));
                withData("isValid", isValid);
            }
        };
    }

    public static RawEvent searchSubmit(@NotNull final JComponent source, @Nullable final String transactionId) {
        return new SwingRawEvent("StackTraceSearch", "Submit", source) {
            protected void myLazyFields() {
                withFunnel(new Funnel.Instance(Funnel.SEARCH, transactionId));
            }
        };
    }

    public static RawEvent searchCreate(@NotNull final JComponent source, @Nullable final String transactionId, @NotNull final Search search) {
        return new SwingRawEvent("StackTraceSearch", "Create", source) {
            protected void myLazyFields() {
                withFunnel(new Funnel.Instance(Funnel.SEARCH, transactionId));
                withData("searchId", search.getId());
            }
        };
    }


    // Generic interactions
    public static RawEvent buttonClick(@NotNull final JComponent source) {
        return new SwingRawEvent("Interaction", "ButtonClick", source) {
            protected void myLazyFields() {
                withData("label", DataService.getData(eventSource, TrackingKeys.Label));
            }
        };
    }

    public static RawEvent tabSwitch(@NotNull final JTabbedPane source) {
        return new SwingRawEvent("Interaction", "TabSwitch", source) {
            protected void myLazyFields() {
                final String tabName = DataService.getData((JComponent) source.getComponentAt(source.getSelectedIndex()), TrackingKeys.PageTab);
                withData("tab", tabName);
            }
        };
    }

    public static RawEvent linkClick(@NotNull final JComponent source, @NotNull final URI uri) {
        return new SwingRawEvent("Interaction", "LinkClick", source) {
            protected void myLazyFields() {
                withData("url", uri.toString());
            }
        };
    }


    // Notifications
    public static RawEvent notificationShow(@NotNull final JComponent source) {
        return new SwingRawEvent("Notification", "Show", source) {
            protected void myLazyFields() {
            }
        };
    }


    @NotNull
    protected final JComponent eventSource;

    public SwingRawEvent(@NotNull String category, @NotNull String action, @NotNull JComponent component) {
        super(category, action);
        eventSource = component;
    }

    @Override
    protected final void lazyFields() {
        // swing track events will always have a source
        withField("pageViewId", DataService.getData(eventSource, TrackingKeys.PageViewId));
        Location.Base location = DataService.getData(eventSource, TrackingKeys.Location);
        if (location != null) location.tab = DataService.getData(eventSource, TrackingKeys.PageTab);
        withField("location", location);

        myLazyFields();
    }

    protected void myLazyFields() {

    }

    @NotNull
    SwingRawEvent withHit() {
        withData("hit", DataService.getData(eventSource, TrackingKeys.SolutionHit));
        return this;
    }
}
