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

import com.samebug.clients.common.tracking.Locations;
import com.samebug.clients.common.tracking.SolutionHit;
import com.samebug.clients.swing.ui.modules.DataService;

public final class TrackingKeys {
    public static final DataService.Key<String> AuthenticationTransaction = new DataService.Key<String>("track.AuthenticationTransaction");
    public static final DataService.Key<String> HelpRequestTransaction = new DataService.Key<String>("track.HelpRequestTransaction");
    public static final DataService.Key<String> SolutionTransaction = new DataService.Key<String>("track.SolutionTransaction");
    public static final DataService.Key<String> WriteTipTransaction = new DataService.Key<String>("track.WriteTipTransaction");
    public static final DataService.Key<String> SearchTransaction = new DataService.Key<String>("track.SearchTransaction");

    public static final DataService.Key<String> PageViewId = new DataService.Key<String>("track.PageViewId");
    public static final DataService.Key<String> PageTab = new DataService.Key<String>("track.PageTab");
    public static final DataService.Key<Locations.Base> Location = new DataService.Key<Locations.Base>("track.Location");
    public static final DataService.Key<String> Label = new DataService.Key<String>("track.Label");
    public static final DataService.Key<SolutionHit> SolutionHit = new DataService.Key<SolutionHit>("track.SolutionHit");
    public static final DataService.Key<Integer> SolutionHitIndex = new DataService.Key<Integer>("SolutionHitIndex");
    public static final DataService.Key<Integer> BugmateHitIndex = new DataService.Key<Integer>("BugmateHitIndex");

    private TrackingKeys() {}
}
