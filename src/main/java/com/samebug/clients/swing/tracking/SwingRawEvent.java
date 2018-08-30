/*
 * Copyright 2018 Samebug, Inc.
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
import com.samebug.clients.http.entities.authentication.AuthenticationResponse;
import com.samebug.clients.http.entities.search.Search;
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


    // Search funnel
    public static RawEvent searchHookTrigger(@Nullable final String transactionId, @NotNull final String hookId) {
        return new RawEvent("StackTraceSearch", "HookTriggered") {
            protected void lazyFields() {
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
}
