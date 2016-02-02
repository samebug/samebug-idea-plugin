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
package com.samebug.clients.idea.android;

import com.intellij.facet.ProjectFacetManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.sdk.AndroidPlatform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

import static com.android.SdkConstants.FN_ADB;
import static org.jetbrains.android.util.AndroidCommonUtils.platformToolPath;

public class AndroidSdkUtil {
    @Nullable
    public static File getAdb(@NotNull Project project) {
        AndroidPlatform platform = getProjectPlatform(project);
        if (platform == null) {
            platform = getFirstAndroidModulePlatform(project);
        }
        File adb = platform != null ? new File(platform.getSdkData().getLocation(), platformToolPath(FN_ADB)) : null;
        return adb != null && adb.exists() ? adb : null;
    }

    @Nullable
    private static AndroidPlatform getProjectPlatform(Project project) {
        Sdk projectSdk = ProjectRootManager.getInstance(project).getProjectSdk();
        if (projectSdk != null) {
            return AndroidPlatform.getInstance(projectSdk);
        }
        return null;
    }

    @Nullable
    private static AndroidPlatform getFirstAndroidModulePlatform(Project project) {
        List<AndroidFacet> facets = ProjectFacetManager.getInstance(project).getFacets(AndroidFacet.ID);
        for (AndroidFacet facet : facets) {
            if (facet.getConfiguration().getAndroidPlatform() != null)
                return facet.getConfiguration().getAndroidPlatform();
        }
        return null;
    }
}
