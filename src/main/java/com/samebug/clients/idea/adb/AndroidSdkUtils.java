package com.samebug.clients.idea.adb;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetConfiguration;
import com.intellij.facet.FacetTypeId;
import com.intellij.facet.ProjectFacetManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkAdditionalData;
import com.intellij.openapi.roots.ProjectRootManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.io.File;
import java.util.List;

public class AndroidSdkUtils {
    @Nullable
    public static File getAdb(@NotNull Project project) {
        Sdk sdk = getProjectSdkData(project);
        if (sdk == null) {
            sdk = getFirstAndroidModuleSdkData(project);
        }
        File adb = null; //sdk == null ? null : new File(sdk.getLocation(), platformToolPath(FN_ADB));
        return adb != null && adb.exists() ? adb : null;
    }

    @Nullable
    private static Sdk getProjectSdkData(Project project) {
        Sdk projectSdk = ProjectRootManager.getInstance(project).getProjectSdk();
        return projectSdk;
    }

    @Nullable
    private static Sdk getFirstAndroidModuleSdkData(Project project) {
        List<Facet> facets = ProjectFacetManager.getInstance(project).getFacets(new FacetTypeId<Facet>("android"));
        for (Facet facet : facets) {
            FacetConfiguration configuration = facet.getConfiguration();
        }
        return null;
    }
}
