/**
 * Copyright 2017 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.idea.messages.client;

import com.intellij.util.messages.Topic;
import com.samebug.clients.common.search.api.entities.MarkResponse;

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
