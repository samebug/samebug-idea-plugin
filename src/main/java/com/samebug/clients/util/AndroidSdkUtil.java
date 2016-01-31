package com.samebug.clients.util;

import com.intellij.facet.ProjectFacetManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.sdk.AndroidPlatform;
import org.jetbrains.android.sdk.AndroidSdkData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.List;

import static com.android.SdkConstants.FN_ADB;
import static org.jetbrains.android.util.AndroidCommonUtils.platformToolPath;

public class AndroidSdkUtil {
    @Nullable
    public static File getAdb(@Nonnull Project project) {
        AndroidSdkData data = getProjectSdkData(project);
        if (data == null) {
            data = getFirstAndroidModuleSdkData(project);
        }
        File adb = data == null ? null : new File(data.getLocation(), platformToolPath(FN_ADB));
        return adb != null && adb.exists() ? adb : null;
    }

    @Nullable
    private static AndroidSdkData getProjectSdkData(Project project) {
        Sdk projectSdk = ProjectRootManager.getInstance(project).getProjectSdk();
        if (projectSdk != null) {
            AndroidPlatform platform = AndroidPlatform.getInstance(projectSdk);
            return platform != null ? platform.getSdkData() : null;
        }
        return null;
    }

    @Nullable
    private static AndroidSdkData getFirstAndroidModuleSdkData(Project project) {
        List<AndroidFacet> facets = ProjectFacetManager.getInstance(project).getFacets(AndroidFacet.ID);
        for (AndroidFacet facet : facets) {
            AndroidPlatform androidPlatform = facet.getConfiguration().getAndroidPlatform();
            if (androidPlatform != null) {
                return androidPlatform.getSdkData();
            }
        }
        return null;
    }
}
