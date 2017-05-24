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

import com.samebug.clients.common.tracking.Funnels;
import com.samebug.clients.common.tracking.Locations;
import com.samebug.clients.common.tracking.RawEvent;
import com.samebug.clients.common.tracking.TrackedUser;
import com.samebug.clients.http.entities.authentication.AuthenticationResponse;
import com.samebug.clients.http.entities.search.Search;
import com.samebug.clients.http.entities.search.SearchHit;
import com.samebug.clients.http.entities.solution.SamebugTip;
import com.samebug.clients.swing.ui.modules.DataService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.net.URI;
import java.net.URL;

public class SwingRawEvent extends RawEvent {
    // Funnels
    // Authentication funnel
    public static RawEvent authenticationHookTriggered(@Nullable final String transactionId, @NotNull final String hookId) {
        return new RawEvent("Authentication", "HookTriggered") {
            protected void lazyFields() {
                withData("hookId", hookId);
                withFunnel(new Funnels.Instance(Funnels.AUTHENTICATION, transactionId));
            }
        };
    }

    public static RawEvent authenticationSubmit(@NotNull final JComponent source, @Nullable final String transactionId) {
        return new SwingRawEvent("Authentication", "Submitted", source) {
            protected void myLazyFields() {
                withFunnel(new Funnels.Instance(Funnels.AUTHENTICATION, transactionId));
            }
        };
    }

    public static RawEvent authenticationSucceeded(@NotNull final JComponent source, @Nullable final String transactionId, @NotNull final AuthenticationResponse response) {
        return new SwingRawEvent("Authentication", "Succeeded", source) {
            protected void myLazyFields() {
                withAuthenticationResponse(response);
                withFunnel(new Funnels.Instance(Funnels.AUTHENTICATION, transactionId));
            }
        };
    }


    // Help request funnel
    public static RawEvent helpRequestHookTrigger(@NotNull final JComponent source, @Nullable final String transactionId, @NotNull final String hookId) {
        return new SwingRawEvent("HelpRequest", "HookTriggered", source) {
            protected void myLazyFields() {
                withFunnel(new Funnels.Instance(Funnels.HELP_REQUEST, transactionId));
                withData("hookId", hookId);
            }
        };
    }

    public static RawEvent helpRequestSubmit(@NotNull final JComponent source, @Nullable final String transactionId) {
        return new SwingRawEvent("HelpRequest", "Submitted", source) {
            protected void myLazyFields() {
                withFunnel(new Funnels.Instance(Funnels.HELP_REQUEST, transactionId));
            }
        };
    }

    public static RawEvent helpRequestCreate(@NotNull final JComponent source, @Nullable final String transactionId, @NotNull final String helpRequestId) {
        return new SwingRawEvent("HelpRequest", "Created", source) {
            protected void myLazyFields() {
                withFunnel(new Funnels.Instance(Funnels.HELP_REQUEST, transactionId));
                withData("helpRequestId", helpRequestId);
            }
        };
    }

    public static RawEvent helpRequestDisplay(@NotNull final JComponent source, @NotNull final String helpRequestId) {
        return new SwingRawEvent("HelpRequest", "Displayed", source) {
            protected void myLazyFields() {
                withData("helpRequestId", helpRequestId);
            }
        };
    }


    // Write tip funnel
    public static RawEvent writeTipHookTrigger(@NotNull final JComponent source, @Nullable final String transactionId,
                                               @Nullable final String helpRequestId, @NotNull final String hookId) {
        return new SwingRawEvent("WriteTip", "HookTriggered", source) {
            protected void myLazyFields() {
                withFunnel(new Funnels.Instance(Funnels.WRITE_TIP, transactionId));
                withData("helpRequestId", helpRequestId);
                withData("hookId", hookId);
            }
        };
    }

    public static RawEvent writeTipSubmit(@NotNull final JComponent source, @Nullable final String transactionId, @Nullable final String helpRequestId) {
        return new SwingRawEvent("WriteTip", "Submitted", source) {
            protected void myLazyFields() {
                withFunnel(new Funnels.Instance(Funnels.WRITE_TIP, transactionId));
                withData("helpRequestId", helpRequestId);
            }
        };
    }

    public static RawEvent writeTipCreate(@NotNull final JComponent source, @Nullable final String transactionId,
                                          @Nullable final String helpRequestId, @NotNull final SearchHit<SamebugTip> response) {
        return new SwingRawEvent("WriteTip", "Created", source) {
            protected void myLazyFields() {
                withFunnel(new Funnels.Instance(Funnels.WRITE_TIP, transactionId));
                withData("solutionId", response.getSolution().getId());
                withData("documentId", response.getSolution().getDocument().getDocumentId());
                withData("helpRequestId", helpRequestId);
            }
        };
    }

    // Solution funnel
    public static RawEvent solutionDisplay(@NotNull final JComponent source, @Nullable final String transactionId) {
        return new SwingRawEvent("Solution", "Displayed", source) {
            protected void myLazyFields() {
                withFunnel(new Funnels.Instance(Funnels.SOLUTION, transactionId));
                withHit();
            }
        };
    }

    public static RawEvent solutionClick(@NotNull final JComponent source, @Nullable final String transactionId) {
        return new SwingRawEvent("Solution", "Clicked", source) {
            protected void myLazyFields() {
                withFunnel(new Funnels.Instance(Funnels.SOLUTION, transactionId));
                withHit();
            }
        };
    }

    public static RawEvent markSubmit(@NotNull final JComponent source, @Nullable final String transactionId) {
        return new SwingRawEvent("Mark", "Submitted", source) {
            protected void myLazyFields() {
                withFunnel(new Funnels.Instance(Funnels.SOLUTION, transactionId));
                withHit();
            }
        };
    }

    public static RawEvent markCreate(@NotNull final JComponent source, @Nullable final String transactionId, @NotNull final Integer markId) {
        return new SwingRawEvent("Mark", "Created", source) {
            protected void myLazyFields() {
                withFunnel(new Funnels.Instance(Funnels.SOLUTION, transactionId));
                withData("markId", markId);
                withHit();
            }
        };
    }

    public static RawEvent markCancel(@NotNull final JComponent source, @Nullable final String transactionId, @NotNull final Integer markId) {
        return new SwingRawEvent("Mark", "Cancelled", source) {
            protected void myLazyFields() {
                withFunnel(new Funnels.Instance(Funnels.SOLUTION, transactionId));
                withHit();
                withData("cancelledMarkId", markId);
            }
        };
    }

    // Search funnel
    public static RawEvent searchHookTrigger(@Nullable final String transactionId, @NotNull final String hookId) {
        return new RawEvent("StackTraceSearch", "HookTriggered") {
            protected void myLazyFields() {
                withFunnel(new Funnels.Instance(Funnels.SEARCH, transactionId));
                withData("hookId", hookId);
            }
        };
    }

    public static RawEvent searchQueryChanged(@NotNull final JComponent source, @Nullable final String transactionId, final Boolean isValid) {
        return new SwingRawEvent("StackTraceSearch", "QueryChanged", source) {
            protected void myLazyFields() {
                withFunnel(new Funnels.Instance(Funnels.SEARCH, transactionId));
                withData("isValid", isValid);
            }
        };
    }

    public static RawEvent searchSubmit(@NotNull final JComponent source, @Nullable final String transactionId) {
        return new SwingRawEvent("StackTraceSearch", "Submitted", source) {
            protected void myLazyFields() {
                withFunnel(new Funnels.Instance(Funnels.SEARCH, transactionId));
            }
        };
    }

    public static RawEvent searchCreate(@NotNull final JComponent source, @Nullable final String transactionId, @NotNull final Search search) {
        return new SwingRawEvent("StackTraceSearch", "Created", source) {
            protected void myLazyFields() {
                withFunnel(new Funnels.Instance(Funnels.SEARCH, transactionId));
                withData("searchId", search.getId());
            }
        };
    }

    // Bugmates
    public static RawEvent bugmateDisplay(@NotNull final JComponent source,
                                          @NotNull final TrackedUser bugmate, @NotNull final Integer level, @NotNull final String matchingGroupId) {
        return new SwingRawEvent("Bugmate", "Displayed", source) {
            protected void myLazyFields() {
                withData("bugmate", bugmate);
                withData("position", DataService.getData(eventSource, TrackingKeys.BugmateHitIndex));
                withData("level", level);
                withData("matchingGroupId", matchingGroupId);
            }
        };
    }


    // Generic interactions
    public static RawEvent buttonClick(@NotNull final JComponent source) {
        return new SwingRawEvent("Interaction", "ButtonClicked", source) {
            protected void myLazyFields() {
                withData("label", DataService.getData(eventSource, TrackingKeys.Label));
            }
        };
    }

    public static RawEvent tabSwitch(@NotNull final JTabbedPane source) {
        return new SwingRawEvent("Interaction", "TabSwitched", source) {
            protected void myLazyFields() {
                final String tabId = DataService.getData((JComponent) source.getComponentAt(source.getSelectedIndex()), TrackingKeys.PageTab);
                final String tabLabel = DataService.getData((JComponent) source.getTabComponentAt(source.getSelectedIndex()), TrackingKeys.Label);
                withData("tabId", tabId);
                withData("label", tabLabel);
            }
        };
    }

    public static RawEvent linkClick(@NotNull final JComponent source, @NotNull final URI uri) {
        return new SwingRawEvent("Interaction", "LinkClicked", source) {
            protected void myLazyFields() {
                withData("to", uri.toString());
                withData("label", DataService.getData(eventSource, TrackingKeys.Label));
            }
        };
    }

    public static RawEvent linkClick(@NotNull final JComponent source, @NotNull final URL url) {
        return new SwingRawEvent("Interaction", "LinkClicked", source) {
            protected void myLazyFields() {
                withData("to", url.toString());
                withData("label", DataService.getData(eventSource, TrackingKeys.Label));
            }
        };
    }


    // Notifications
    public static RawEvent notificationShow(@NotNull final JComponent source) {
        return new SwingRawEvent("Notification", "Showed", source) {
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
        Locations.Base location = DataService.getData(eventSource, TrackingKeys.Location);
        if (location != null) location.tabId = DataService.getData(eventSource, TrackingKeys.PageTab);
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
