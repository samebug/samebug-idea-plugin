package com.samebug.clients.idea.components.application;

/**
 * Created by poroszd on 2/12/16.
 */
public class Settings {
    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    private Integer userId;
    private String userDisplayName;
    private String apiKey;

    public boolean isInitialized() {
        return getApiKey() != null && getUserId() != null;
    }
}
