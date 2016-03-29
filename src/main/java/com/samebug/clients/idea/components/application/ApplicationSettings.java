/**
 * Copyright 2016 Samebug, Inc.
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
package com.samebug.clients.idea.components.application;

import java.util.UUID;

/**
 * Created by poroszd on 2/12/16.
 */
public class ApplicationSettings {
    private String apiKey;
    private String instanceId = UUID.randomUUID().toString();
    private int userId;
    private boolean tutorialFirstRun = true;
    private boolean tutorialShowRecurringSearches = true;
    private boolean tutorialShowZeroSolutionSearches = true;
    private boolean tutorialShowMixedSearches = true;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public boolean isTutorialFirstRun() {
        return tutorialFirstRun;
    }

    public void setTutorialFirstRun(boolean tutorialFirstRun) {
        this.tutorialFirstRun = tutorialFirstRun;
    }

    public boolean isTutorialShowRecurringSearches() {
        return tutorialShowRecurringSearches;
    }

    public void setTutorialShowRecurringSearches(boolean tutorialShowRecurringSearches) {
        this.tutorialShowRecurringSearches = tutorialShowRecurringSearches;
    }

    public boolean isTutorialShowZeroSolutionSearches() {
        return tutorialShowZeroSolutionSearches;
    }

    public void setTutorialShowZeroSolutionSearches(boolean tutorialShowZeroSolutionSearches) {
        this.tutorialShowZeroSolutionSearches = tutorialShowZeroSolutionSearches;
    }

    public boolean isTutorialShowMixedSearches() {
        return tutorialShowMixedSearches;
    }

    public void setTutorialShowMixedSearches(boolean tutorialShowMixedSearches) {
        this.tutorialShowMixedSearches = tutorialShowMixedSearches;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
