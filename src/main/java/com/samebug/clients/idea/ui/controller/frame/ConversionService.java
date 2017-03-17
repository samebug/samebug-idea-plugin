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
package com.samebug.clients.idea.ui.controller.frame;

import com.samebug.clients.common.api.WebUrlBuilder;
import com.samebug.clients.common.api.entities.UserInfo;
import com.samebug.clients.common.api.entities.UserReference;
import com.samebug.clients.common.api.entities.UserStats;
import com.samebug.clients.common.api.entities.bugmate.Bugmate;
import com.samebug.clients.common.api.entities.bugmate.BugmatesResult;
import com.samebug.clients.common.api.entities.helpRequest.HelpRequest;
import com.samebug.clients.common.api.entities.helpRequest.IncomingHelpRequests;
import com.samebug.clients.common.api.entities.helpRequest.MatchingHelpRequest;
import com.samebug.clients.common.api.entities.helpRequest.Requester;
import com.samebug.clients.common.api.entities.search.SearchDetails;
import com.samebug.clients.common.api.entities.search.SearchInfo;
import com.samebug.clients.common.api.entities.solution.*;
import com.samebug.clients.common.ui.component.bugmate.IBugmateHit;
import com.samebug.clients.common.ui.component.bugmate.IBugmateList;
import com.samebug.clients.common.ui.component.community.IHelpOthersCTA;
import com.samebug.clients.common.ui.component.helpRequest.IHelpRequest;
import com.samebug.clients.common.ui.component.helpRequest.IHelpRequestPreview;
import com.samebug.clients.common.ui.component.hit.IMarkButton;
import com.samebug.clients.common.ui.component.hit.ITipHit;
import com.samebug.clients.common.ui.component.hit.IWebHit;
import com.samebug.clients.common.ui.component.popup.IHelpRequestPopup;
import com.samebug.clients.common.ui.component.profile.IProfilePanel;
import com.samebug.clients.common.ui.frame.helpRequest.IHelpRequestFrame;
import com.samebug.clients.common.ui.frame.helpRequest.IHelpRequestHeader;
import com.samebug.clients.common.ui.frame.helpRequest.IHelpRequestTab;
import com.samebug.clients.common.ui.frame.helpRequest.IHelpRequestTabs;
import com.samebug.clients.common.ui.frame.helpRequestList.IHelpRequestList;
import com.samebug.clients.common.ui.frame.helpRequestList.IHelpRequestListFrame;
import com.samebug.clients.common.ui.frame.helpRequestList.IHelpRequestListHeader;
import com.samebug.clients.common.ui.frame.solution.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public final class ConversionService {
    private final WebUrlBuilder urlBuilder;

    public ConversionService(WebUrlBuilder urlBuilder) {
        this.urlBuilder = urlBuilder;
    }

    public IMarkButton.Model convertMarkResponse(MarkResponse response) {
        return new IMarkButton.Model(response.getDocumentVotes(), response.getId(), true);
    }

    public IMarkButton.Model convertRetractedMarkResponse(MarkResponse response) {
        return new IMarkButton.Model(response.getDocumentVotes(), null, true);
    }

    /**
     *  When showing the hits for a tip request, then marking is disabled. This parameter flows through the whole conversion call tree.
     */
    public IMarkButton.Model convertMarkPanel(RestHit hit, boolean disabled) {
        boolean userCanMark = disabled ? false : hit.getMarkable();
        return new IMarkButton.Model(hit.getScore(), hit.getMarkId(), userCanMark);
    }

    public ITipHit.Model tipHit(RestHit<Tip> hit, boolean disabled) {
        Tip tip = hit.getSolution();
        IMarkButton.Model mark = convertMarkPanel(hit, disabled);
        UserReference author = hit.getCreatedBy();
        return new ITipHit.Model(tip.getTip(), hit.getSolutionId(), tip.getCreatedAt(), author.getDisplayName(), author.getAvatarUrl(), mark);
    }

    public IWebResultsTab.Model webResultsTab(Solutions solutions, boolean disabled) {
        final List<IWebHit.Model> webHits = new ArrayList<IWebHit.Model>(solutions.getReferences().size());
        for (RestHit<SolutionReference> externalHit : solutions.getReferences()) {
            SolutionReference externalSolution = externalHit.getSolution();
            IMarkButton.Model mark = convertMarkPanel(externalHit, disabled);
            final String sourceIconName = externalSolution.getSource().getIcon();
            final URL sourceIconUrl = urlBuilder.sourceIcon(sourceIconName);

            final String createdBy;
            if (externalSolution.getAuthor() != null) createdBy = externalSolution.getAuthor().getName();
            else createdBy = null;
            IWebHit.Model webHit =
                    new IWebHit.Model(externalSolution.getTitle(), externalSolution.getUrl(), externalHit.getSolutionId(),
                            externalSolution.getCreatedAt(), createdBy,
                            externalSolution.getSource().getName(), sourceIconUrl,
                            mark);
            webHits.add(webHit);
        }
        return new IWebResultsTab.Model(webHits);
    }

    public ITipResultsTab.Model tipResultsTab(SearchDetails search, Solutions solutions, BugmatesResult bugmates, boolean disabled) {
        final List<ITipHit.Model> tipHits = new ArrayList<ITipHit.Model>(solutions.getTips().size());
        for (RestHit<Tip> tipSolution : solutions.getTips()) {
            ITipHit.Model tipHit = tipHit(tipSolution, disabled);
            tipHits.add(tipHit);
        }
        final List<IBugmateHit.Model> bugmateHits = new ArrayList<IBugmateHit.Model>(bugmates.getBugmates().size());
        for (Bugmate b : bugmates.getBugmates()) {
            IBugmateHit.Model model = new IBugmateHit.Model(b.getUserId(), b.getDisplayName(), b.getAvatarUrl(), b.getNumberOfSearches(), b.getLastSeen());
            bugmateHits.add(model);
        }
        String exceptionTitle = headLine(search);
        IBugmateList.Model bugmateList = new IBugmateList.Model(bugmateHits, bugmates.getNumberOfOtherBugmates(), bugmates.isEvenMoreExists(), exceptionTitle, search.group.helpRequest);
        return new ITipResultsTab.Model(tipHits, bugmateList);
    }

    public IProfilePanel.Model profilePanel(UserInfo user, UserStats statistics) {
        return new IProfilePanel.Model(0, statistics.getNumberOfMarks(), statistics.getNumberOfTips(), statistics.getNumberOfThanks(),
                user.getDisplayName(), user.getAvatarUrl());
    }

    public ISolutionFrame.Model solutionFrame(SearchDetails search, Solutions solutions, BugmatesResult bugmates, UserInfo user, UserStats statistics) {
        IWebResultsTab.Model webResults = webResultsTab(solutions, false);
        ITipResultsTab.Model tipResults = tipResultsTab(search, solutions, bugmates, false);

        IHelpOthersCTA.Model cta = new IHelpOthersCTA.Model(0);
        String exceptionTitle = headLine(search);
        IResultTabs.Model resultTabs = new IResultTabs.Model(webResults, tipResults, cta);
        ISearchHeaderPanel.Model header = new ISearchHeaderPanel.Model(exceptionTitle);
        IProfilePanel.Model profile = profilePanel(user, statistics);
        return new ISolutionFrame.Model(resultTabs, header, profile);
    }

    public IHelpRequestHeader.Model helpRequestHeader(MatchingHelpRequest helpRequest) {
        Requester requester = helpRequest.helpRequest.requester;
        SearchInfo accessibleSearchInfo = helpRequest.accessibleSearchInfo();
        return new IHelpRequestHeader.Model(headLine(accessibleSearchInfo), requester.displayName, requester.avatarUrl);
    }

    public IHelpRequestTab.Model helpRequestTab(Solutions solutions, MatchingHelpRequest helpRequest) {
        final List<ITipHit.Model> tipHits = new ArrayList<ITipHit.Model>(solutions.getTips().size());
        for (RestHit<Tip> tipSolution : solutions.getTips()) {
            ITipHit.Model tipHit = tipHit(tipSolution, true);
            tipHits.add(tipHit);
        }
        Requester requester = helpRequest.helpRequest.requester;
        HelpRequest hr = helpRequest.helpRequest;
        IHelpRequest.Model request = new IHelpRequest.Model(requester.displayName, requester.avatarUrl, hr.createdAt, hr.context);
        return new IHelpRequestTab.Model(tipHits, request);
    }


    public IHelpRequestFrame.Model convertHelpRequestFrame(Solutions solutions, MatchingHelpRequest helpRequest, UserInfo user, UserStats statistics) {
        IWebResultsTab.Model webResults = webResultsTab(solutions, true);
        IHelpRequestTab.Model helpRequestTab = helpRequestTab(solutions, helpRequest);
        IHelpOthersCTA.Model cta = new IHelpOthersCTA.Model(0);
        IHelpRequestTabs.Model tabs = new IHelpRequestTabs.Model(webResults, helpRequestTab, cta);
        IHelpRequestHeader.Model header = helpRequestHeader(helpRequest);
        IProfilePanel.Model profile = profilePanel(user, statistics);

        return new IHelpRequestFrame.Model(tabs, header, profile);
    }

    public IHelpRequestListFrame.Model convertHelpRequestListFrame(IncomingHelpRequests incomingRequests, UserInfo user, UserStats statistics) {
        List<IHelpRequestPreview.Model> requestPreviews = new ArrayList<IHelpRequestPreview.Model>(incomingRequests.matches.size());
        for (MatchingHelpRequest r : incomingRequests.matches) {
            Requester requester = r.helpRequest.requester;
            HelpRequest hr = r.helpRequest;
            String exceptionBody = headLine(r.accessibleSearchInfo());
            IHelpRequestPreview.Model preview = new IHelpRequestPreview.Model(requester.displayName, requester.avatarUrl, hr.createdAt, r.viewedAt, hr.context, hr.id, exceptionBody);
            requestPreviews.add(preview);
        }
        IHelpRequestList.Model requestList = new IHelpRequestList.Model(requestPreviews);
        IHelpRequestListHeader.Model header = new IHelpRequestListHeader.Model(incomingRequests.matches.size());
        IProfilePanel.Model profile = profilePanel(user, statistics);

        return new IHelpRequestListFrame.Model(header, requestList, profile);
    }

    public IHelpRequestPopup.Model convertHelpRequestPopup(HelpRequest incomingRequest) {
        return new IHelpRequestPopup.Model(incomingRequest.context, incomingRequest.requester.displayName, incomingRequest.requester.avatarUrl);
    }

    public static String headLine(SearchDetails search) {
        return headLine(search.typeName, search.message);
    }

    public static String headLine(SearchInfo search) {
        return headLine(search.typeName, search.message);
    }

    public static String headLine(@NotNull String typeName, @Nullable String message) {
        return (message != null) ? typeName + ": " + message : typeName;
    }
}
