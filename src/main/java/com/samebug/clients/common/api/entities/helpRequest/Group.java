package com.samebug.clients.common.api.entities.helpRequest;

import com.samebug.clients.common.api.entities.search.SearchInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

public final class Group {
    @NotNull
    public final String id;
    @Nullable
    public final SearchInfo lastSearchInfo;
    @NotNull
    public final Integer numberOfSearches;
    @NotNull
    public final Date firstSeen;
    @NotNull
    public final Date lastSeen;
    @NotNull
    public final String helpRequestId;
    // TODO enum
    @NotNull
    public final String visibility;
    // TODO enum
    @NotNull
    public final List<String> permissions;

    public Group(@NotNull String id,
                 @NotNull Integer lastSearchId,
                 @Nullable SearchInfo lastSearchInfo,
                 @NotNull Integer numberOfSearches,
                 @NotNull Date firstSeen,
                 @NotNull Date lastSeen,
                 @NotNull String helpRequestId,
                 @NotNull String visibility,
                 @NotNull List<String> permissions) {
        this.id = id;
        this.lastSearchInfo = lastSearchInfo;
        this.numberOfSearches = numberOfSearches;
        this.firstSeen = firstSeen;
        this.lastSeen = lastSeen;
        this.helpRequestId = helpRequestId;
        this.visibility = visibility;
        this.permissions = permissions;
    }
}
