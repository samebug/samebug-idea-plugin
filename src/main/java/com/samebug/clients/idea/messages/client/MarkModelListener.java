package com.samebug.clients.idea.messages.client;

import com.intellij.util.messages.Topic;
import com.samebug.clients.search.api.entities.MarkResponse;

public interface MarkModelListener {
    Topic<MarkModelListener> TOPIC = Topic.create("search model changes from mark", MarkModelListener.class);

    void startPostingMark(int searchId, int solutionId);

    void successPostingMark(int searchId, int solutionId, MarkResponse result);

    void failPostingMark(int searchId, int solutionId, java.lang.Exception e);

    void finishPostingMark(int searchId, int solutionId);

    void startRetractMark(int voteId);

    void successRetractMark(int voteId, MarkResponse result);

    void failRetractMark(int voteId, java.lang.Exception e);

    void finishRetractMark(int voteId);

}
