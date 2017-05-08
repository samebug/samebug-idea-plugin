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

import com.samebug.clients.common.ui.component.bugmate.ConnectionStatus;
import com.samebug.clients.common.ui.component.bugmate.IBugmateHit;
import com.samebug.clients.common.ui.component.bugmate.IBugmateList;
import com.samebug.clients.common.ui.component.community.IAskForHelp;
import com.samebug.clients.common.ui.component.community.IHelpOthersCTA;
import com.samebug.clients.common.ui.component.helpRequest.IHelpRequest;
import com.samebug.clients.common.ui.component.helpRequest.IHelpRequestPreview;
import com.samebug.clients.common.ui.component.helpRequest.IMyHelpRequest;
import com.samebug.clients.common.ui.component.hit.IMarkButton;
import com.samebug.clients.common.ui.component.hit.ITipHit;
import com.samebug.clients.common.ui.component.hit.IWebHit;
import com.samebug.clients.common.ui.component.popup.IHelpRequestPopup;
import com.samebug.clients.common.ui.component.popup.IIncomingTipPopup;
import com.samebug.clients.common.ui.component.profile.IProfilePanel;
import com.samebug.clients.common.ui.frame.helpRequest.IHelpRequestFrame;
import com.samebug.clients.common.ui.frame.helpRequest.IHelpRequestHeader;
import com.samebug.clients.common.ui.frame.helpRequest.IHelpRequestTab;
import com.samebug.clients.common.ui.frame.helpRequest.IHelpRequestTabs;
import com.samebug.clients.common.ui.frame.helpRequestList.IHelpRequestList;
import com.samebug.clients.common.ui.frame.helpRequestList.IHelpRequestListFrame;
import com.samebug.clients.common.ui.frame.helpRequestList.IHelpRequestListHeader;
import com.samebug.clients.common.ui.frame.solution.*;
import com.samebug.clients.http.entities.bugmate.BugmateMatch;
import com.samebug.clients.http.entities.helprequest.HelpRequest;
import com.samebug.clients.http.entities.helprequest.HelpRequestMatch;
import com.samebug.clients.http.entities.jsonapi.BugmateList;
import com.samebug.clients.http.entities.jsonapi.IncomingHelpRequestList;
import com.samebug.clients.http.entities.notification.IncomingAnswer;
import com.samebug.clients.http.entities.notification.IncomingHelpRequest;
import com.samebug.clients.http.entities.profile.UserStats;
import com.samebug.clients.http.entities.search.ReadableSearchGroup;
import com.samebug.clients.http.entities.search.SearchGroup;
import com.samebug.clients.http.entities.search.SearchHit;
import com.samebug.clients.http.entities.search.StackTraceSearch;
import com.samebug.clients.http.entities.solution.ExternalDocument;
import com.samebug.clients.http.entities.solution.SamebugTip;
import com.samebug.clients.http.entities.solution.SolutionSlot;
import com.samebug.clients.http.entities.user.Me;
import com.samebug.clients.http.entities.user.RegisteredSamebugUser;
import com.samebug.clients.http.entities.user.SamebugUser;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class ConversionService {
    public ConversionService() {
    }

    /**
     * When showing the hits for a help request, then marking is disabled. This parameter flows through the whole conversion call tree.
     */
    public IMarkButton.Model convertMarkPanel(SearchHit hit, boolean disabled) {
        boolean userCanMark = disabled ? false : hit.getMarkable();
        Integer activeMarkId = hit.getActiveMark() == null ? null : hit.getActiveMark().getId();
        return new IMarkButton.Model(hit.getVotes().getVotesOnDocument(), activeMarkId, userCanMark);
    }

    public ITipHit.Model tipHit(SearchHit<SamebugTip> hit, boolean disabled) {
        SamebugTip tip = hit.getSolution().getDocument();
        IMarkButton.Model mark = convertMarkPanel(hit, disabled);
        RegisteredSamebugUser author = tip.getAuthor();
        return new ITipHit.Model(tip.getMessage(), hit.getSolution().getId(), tip.getCreatedAt(), author.getDisplayName(), author.getAvatarUrl(), mark);
    }

    public IWebResultsTab.Model webResultsTab(List<SearchHit<ExternalDocument>> solutions, boolean disabled) {
        final List<IWebHit.Model> webHits = new ArrayList<IWebHit.Model>(solutions.size());
        for (SearchHit<ExternalDocument> externalHit : solutions) {
            SolutionSlot<ExternalDocument> externalSolution = externalHit.getSolution();
            ExternalDocument doc = externalSolution.getDocument();
            IMarkButton.Model mark = convertMarkPanel(externalHit, disabled);
            IWebHit.Model webHit =
                    new IWebHit.Model(doc.getTitle(), doc.getUrl(), externalSolution.getId(),
                            externalSolution.getCreatedAt(), doc.getAuthor().getDisplayName(),
                            doc.getSource().getName(), doc.getSource().getIcon(),
                            mark);
            webHits.add(webHit);
        }
        return new IWebResultsTab.Model(webHits);
    }

    public ITipResultsTab.Model tipResultsTab(StackTraceSearch search, List<SearchHit<SamebugTip>> solutions, BugmateList bugmates, HelpRequest helpRequest, boolean disabled) {
        final List<ITipHit.Model> tipHits = new ArrayList<ITipHit.Model>(solutions.size());
        for (SearchHit<SamebugTip> tipSolution : solutions) {
            ITipHit.Model tipHit = tipHit(tipSolution, disabled);
            tipHits.add(tipHit);
        }
        final List<IBugmateHit.Model> bugmateHits = new ArrayList<IBugmateHit.Model>(bugmates.getData().size());
        for (BugmateMatch b : bugmates.getData()) {
            SamebugUser mate = b.getBugmate();
            Integer nMateHasSeenThisSearch = (b.getMatchingGroup() instanceof ReadableSearchGroup) ? ((ReadableSearchGroup) b.getMatchingGroup()).getNumberOfSearches() : null;
            Date lastTimeMateHasSeenThisSearch = (b.getMatchingGroup() instanceof ReadableSearchGroup) ? ((ReadableSearchGroup) b.getMatchingGroup()).getLastSeen() : null;
            ConnectionStatus status;
            if (b.getBugmate() instanceof RegisteredSamebugUser) {
                final RegisteredSamebugUser bugmate = (RegisteredSamebugUser) b.getBugmate();
                if (bugmate.getOnline() == null) status = ConnectionStatus.UNDEFINED;
                else if (bugmate.getOnline()) status = ConnectionStatus.ONLINE;
                else status = ConnectionStatus.UNDEFINED;
            } else {
                status = ConnectionStatus.UNDEFINED;
            }
            // TODO how to show the bugmate when his group is only searchable?
            IBugmateHit.Model model = new IBugmateHit.Model(mate.getDisplayName(), mate.getAvatarUrl(), nMateHasSeenThisSearch, lastTimeMateHasSeenThisSearch, status);
            bugmateHits.add(model);
        }
        String exceptionTitle = headLine(search);
        IBugmateList.Model bugmateList = new IBugmateList.Model(bugmateHits, bugmates.getMeta().getTotal());
        IAskForHelp.Model askForHelp = new IAskForHelp.Model(bugmates.getMeta().getTotal(), exceptionTitle);
        IMyHelpRequest.Model myHelpRequest = (helpRequest != null) ? new IMyHelpRequest.Model(helpRequest.getId(), helpRequest.getCreatedAt(), helpRequest.getContext()) : null;
        return new ITipResultsTab.Model(tipHits, bugmateList, askForHelp, myHelpRequest);
    }

    public IProfilePanel.Model profilePanel(IncomingHelpRequestList incomingRequests, Me user, UserStats statistics) {
        ConnectionStatus status = IdeaSamebugPlugin.getInstance().clientService.getWsClient().isConnected() ? ConnectionStatus.ONLINE : ConnectionStatus.OFFLINE;
        return new IProfilePanel.Model(incomingRequests.getMeta().getTotal(), statistics.getNumberOfVotes(), statistics.getNumberOfTips(), statistics.getNumberOfThanks(),
                user.getDisplayName(), user.getAvatarUrl(), status);
    }

    public ISolutionFrame.Model solutionFrame(StackTraceSearch search, List<SearchHit<SamebugTip>> tipHits, List<SearchHit<ExternalDocument>> webHits,
                                              BugmateList bugmates, HelpRequest helpRequest, IncomingHelpRequestList incomingRequests, Me user, UserStats statistics) {
        IWebResultsTab.Model webResults = webResultsTab(webHits, false);
        ITipResultsTab.Model tipResults = tipResultsTab(search, tipHits, bugmates, helpRequest, false);

        IHelpOthersCTA.Model cta = new IHelpOthersCTA.Model(bugmates.getMeta().getTotal());
        String exceptionTitle = headLine(search);
        IResultTabs.Model resultTabs = new IResultTabs.Model(webResults, tipResults, cta);
        ISearchHeaderPanel.Model header = new ISearchHeaderPanel.Model(exceptionTitle);
        IProfilePanel.Model profile = profilePanel(incomingRequests, user, statistics);
        return new ISolutionFrame.Model(resultTabs, header, profile);
    }

    public IHelpRequestHeader.Model helpRequestHeader(HelpRequestMatch helpRequestMatch) {
        HelpRequest helpRequest = helpRequestMatch.getHelpRequest();
        RegisteredSamebugUser requester = helpRequest.getRequester();
        return new IHelpRequestHeader.Model(headLine(helpRequestMatch), requester.getDisplayName(), requester.getAvatarUrl());
    }

    public IHelpRequestTab.Model helpRequestTab(List<SearchHit<SamebugTip>> tipHits, HelpRequestMatch helpRequestMatch) {
        HelpRequest helpRequest = helpRequestMatch.getHelpRequest();
        final List<ITipHit.Model> tipHitModels = new ArrayList<ITipHit.Model>(tipHits.size());
        for (SearchHit<SamebugTip> tipSolution : tipHits) {
            ITipHit.Model tipHit = tipHit(tipSolution, true);
            tipHitModels.add(tipHit);
        }
        RegisteredSamebugUser requester = helpRequest.getRequester();
        IHelpRequest.Model request = new IHelpRequest.Model(requester.getDisplayName(), requester.getAvatarUrl(), helpRequest.getCreatedAt(), helpRequest.getContext());
        return new IHelpRequestTab.Model(tipHitModels, request);
    }


    public IHelpRequestFrame.Model convertHelpRequestFrame(List<SearchHit<SamebugTip>> tipHits, List<SearchHit<ExternalDocument>> webHits, HelpRequestMatch helpRequestMatch,
                                                           IncomingHelpRequestList incomingRequests, Me user, UserStats statistics) {
        IWebResultsTab.Model webResults = webResultsTab(webHits, true);
        IHelpRequestTab.Model helpRequestTab = helpRequestTab(tipHits, helpRequestMatch);
        IHelpOthersCTA.Model cta = new IHelpOthersCTA.Model(0);
        IHelpRequestTabs.Model tabs = new IHelpRequestTabs.Model(webResults, helpRequestTab, cta);
        IHelpRequestHeader.Model header = helpRequestHeader(helpRequestMatch);
        IProfilePanel.Model profile = profilePanel(incomingRequests, user, statistics);

        return new IHelpRequestFrame.Model(tabs, header, profile);
    }

    public IHelpRequestListFrame.Model convertHelpRequestListFrame(IncomingHelpRequestList incomingRequests, Me user, UserStats statistics) {
        List<IHelpRequestPreview.Model> requestPreviews = new ArrayList<IHelpRequestPreview.Model>(incomingRequests.getData().size());
        for (HelpRequestMatch m : incomingRequests.getData()) {
            HelpRequest r = m.getHelpRequest();
            RegisteredSamebugUser requester = r.getRequester();
            String exceptionBody = headLine(m);
            IHelpRequestPreview.Model preview = new IHelpRequestPreview.Model(requester.getDisplayName(), requester.getAvatarUrl(), r.getCreatedAt(),
                    m.getViewedAt(), r.getContext(), r.getId(), exceptionBody);
            requestPreviews.add(preview);
        }
        IHelpRequestList.Model requestList = new IHelpRequestList.Model(requestPreviews);
        IHelpRequestListHeader.Model header = new IHelpRequestListHeader.Model(incomingRequests.getMeta().getTotal());
        IProfilePanel.Model profile = profilePanel(incomingRequests, user, statistics);

        return new IHelpRequestListFrame.Model(header, requestList, profile);
    }

    public IHelpRequestPopup.Model convertHelpRequestPopup(IncomingHelpRequest incomingRequest) {
        HelpRequest hr = incomingRequest.getMatch().getHelpRequest();
        return new IHelpRequestPopup.Model(hr.getContext(), hr.getRequester().getDisplayName(), hr.getRequester().getAvatarUrl());
    }

    public IIncomingTipPopup.Model convertIncomingTipPopup(IncomingAnswer incomingTip) {
        SamebugTip tip = incomingTip.getSolution().getDocument();
        RegisteredSamebugUser author = tip.getAuthor();
        return new IIncomingTipPopup.Model(tip.getMessage(), author.getDisplayName(), author.getAvatarUrl());
    }

    public static String headLine(StackTraceSearch search) {
        // TODO make sure the search is readable
        return headLine(search.getExceptionType(), search.getExceptionMessage());
    }

    public static String headLine(HelpRequestMatch helpRequestMatch) {
        ReadableSearchGroup readableSearchGroup;
        SearchGroup requestGroup = helpRequestMatch.getHelpRequest().getSearchGroup();
        if (requestGroup instanceof ReadableSearchGroup) readableSearchGroup = (ReadableSearchGroup) requestGroup;
        else readableSearchGroup = helpRequestMatch.getMatchingGroup();
        return headLine(readableSearchGroup.getExceptionType(), readableSearchGroup.getExceptionMessage());
    }

    public static String headLine(@NotNull String typeName, @Nullable String message) {
        return (message != null) ? typeName + ": " + message : typeName;
    }
}
