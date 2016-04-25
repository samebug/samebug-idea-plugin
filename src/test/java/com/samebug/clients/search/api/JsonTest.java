package com.samebug.clients.search.api;

import com.google.gson.Gson;
import com.samebug.clients.search.api.entities.GroupedExceptionSearch;
import com.samebug.clients.search.api.entities.GroupedHistory;
import com.samebug.clients.search.api.entities.UserInfo;
import com.samebug.clients.search.api.entities.legacy.Solutions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by poroszd on 4/25/16.
 */
public class JsonTest {
    Gson gson;

    @Before
    public void initClient() {
        gson = Json.gson;
    }

    // curl 'http://nightly.samebug.com/rest/0.9/checkApiKey?apiKey=355be195-c10b-11e5-a334-000d3a317492' | jq . > src/test/resources/com/samebug/clients/search/api/jsontest/checkApiKey-1.json
    @Test
    public void getUserInfo_Valid() throws MalformedURLException {
        UserInfo x = gson.fromJson(stream("checkApiKey-1"), UserInfo.class);
        Assert.assertEquals(new URL("https://samebug.io/avatars/1/3"), x.avatarUrl);
        Assert.assertEquals("poroszd", x.displayName);
        Assert.assertEquals(Integer.valueOf(1), x.userId);
    }

    // curl 'http://nightly.samebug.com/rest/0.9/checkApiKey?apiKey=x' | jq . > src/test/resources/com/samebug/clients/search/api/jsontest/checkApiKey-2.json
    @Test
    public void getUserInfo_Invalid() {
        UserInfo x = gson.fromJson(stream("checkApiKey-2"), UserInfo.class);
        Assert.assertEquals(false, x.isUserExist);
    }

    // curl 'http://nightly.samebug.com/rest/0.9/history' -H'X-Samebug-ApiKey: 355be195-c10b-11e5-a334-000d3a317492' | jq . > src/test/resources/com/samebug/clients/search/api/jsontest/history.json
    @Test
    public void getSearchHistory() {
        GroupedHistory x = gson.fromJson(stream("history"), GroupedHistory.class);
        Assert.assertEquals(50, x.searchGroups.size());
        for (GroupedExceptionSearch e : x.searchGroups) {
            Assert.assertTrue(e.lastSearch.searchId > 0);
            Assert.assertTrue(e.lastSearch.componentStack.size() > 0);
        }
    }

    // curl 'http://nightly.samebug.com/rest/0.9/search/673467' -H'X-Samebug-ApiKey: 355be195-c10b-11e5-a334-000d3a317492' | jq . > src/test/resources/com/samebug/clients/search/api/jsontest/search-1.json
    @Test
    public void getSolutions() {
        Solutions x = gson.fromJson(stream("search-1"), Solutions.class);
        Assert.assertTrue(x.search._id > 0);
        Assert.assertTrue(x.breadcrumb.size() > 0);
        Assert.assertTrue(x.searchGroup.lastSearch._id > 0);
        Assert.assertTrue(x.tips.size() > 0);
        Assert.assertTrue(x.references.size() > 0);
    }

    InputStreamReader stream(final String fn) {
        return new InputStreamReader(getClass().getResourceAsStream("/com/samebug/clients/search/api/jsontest/" + fn + ".json"));
    }
}
