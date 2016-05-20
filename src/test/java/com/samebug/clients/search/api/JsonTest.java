package com.samebug.clients.search.api;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.samebug.clients.search.api.entities.*;
import com.samebug.clients.search.api.json.Json;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class JsonTest {
    Gson gson;

    @Before
    public void initClient() {
        gson = Json.gson;
    }

    // curl 'http://nightly.samebug.com/rest/0.9/checkApiKey?apiKey=355be195-c10b-11e5-a334-000d3a317492' |\
    // jq . > src/test/resources/com/samebug/clients/search/api/jsontest/checkApiKey-1.json
    @Test
    public void getUserInfoValid() throws MalformedURLException {
        UserInfo x = gson.fromJson(stream("checkApiKey-1"), UserInfo.class);
        checkFields(x);
        Assert.assertEquals(new URL("https://samebug.io/avatars/1/3"), x.avatarUrl);
        Assert.assertEquals("poroszd", x.displayName);
        Assert.assertEquals(Integer.valueOf(1), x.userId);
    }

    // curl 'http://nightly.samebug.com/rest/0.11/checkApiKey?apiKey=x' | jq . > src/test/resources/com/samebug/clients/search/api/jsontest/checkApiKey-2.json
    @Test
    public void getUserInfoInvalid() {
        UserInfo x = gson.fromJson(stream("checkApiKey-2"), UserInfo.class);
        checkFields(x);
        Assert.assertEquals(false, x.isUserExist);
    }

    // curl 'http://nightly.samebug.com/rest/0.9/history' -H'X-Samebug-ApiKey: 355be195-c10b-11e5-a334-000d3a317492' ||
    // jq . > src/test/resources/com/samebug/clients/search/api/jsontest/history.json
    @Test
    public void getSearchHistory() {
        SearchHistory x = gson.fromJson(stream("history"), SearchHistory.class);
        checkFields(x);
        Assert.assertEquals(50, x.searchGroups.size());
        for (SearchGroup e : x.searchGroups) {
            Assert.assertTrue(e.lastSearch.id > 0);
            Assert.assertTrue(e.lastSearch.stackTrace.breadCrumbs.size() > 0);
        }
    }

    // curl 'http://nightly.samebug.com/rest/0.9/search/673467' -H'X-Samebug-ApiKey: 355be195-c10b-11e5-a334-000d3a317492' |\
    // jq . > src/test/resources/com/samebug/clients/search/api/jsontest/search-1.json
    @Test
    public void getSolutions() {
        Solutions x = gson.fromJson(stream("search-1"), Solutions.class);
        checkFields(x);
        Assert.assertTrue(x.searchGroup.lastSearch.id > 0);
        Assert.assertTrue(x.tips.size() > 0);
        Assert.assertTrue(x.references.size() > 0);
    }

    void checkFields(@NotNull Object o) {
        if (nullableFieldsForEntities.containsKey(o.getClass())) {
            List<String> nullableFields = nullableFieldsForEntities.get(o.getClass());
            for (Field field : o.getClass().getFields()) {
                try {
                    Object value = field.get(o);
                    if (!nullableFields.contains(field.getName())) Assert.assertNotNull("Field " + field.getName() + " of " + o, value);
                    if (value != null) checkFields(value);
                } catch (IllegalAccessException e) {
                    throw new Error(e);
                }
            }
        }
    }

    InputStreamReader stream(final String fn) {
        return new InputStreamReader(getClass().getResourceAsStream("/com/samebug/clients/search/api/jsontest/" + fn + ".json"));
    }

    Map<Class<?>, List<String>> nullableFieldsForEntities = ImmutableMap.<Class<?>, List<String>>builder()
            .put(Author.class, ImmutableList.<String>of("url", "avatarUrl"))
            .put(BreadCrumb.class, ImmutableList.<String>of())
            .put(ComponentReference.class, ImmutableList.<String>of())
            .put(com.samebug.clients.search.api.entities.Exception.class, ImmutableList.<String>of("message"))
            .put(MarkResponse.class, ImmutableList.<String>of("id"))
            .put(QualifiedCall.class, ImmutableList.<String>of("packageName"))
            .put(RestError.class, ImmutableList.<String>of())
            .put(RestHit.class, ImmutableList.<String>of("markId", "createdBy", "exception"))
            .put(RestSolution.class, ImmutableList.<String>of())
            .put(Search.class, ImmutableList.<String>of("visitorId", "userId", "teamId"))
            .put(SearchGroup.class, ImmutableList.<String>of("visitorId", "userId", "teamId"))
            .put(SearchHistory.class, ImmutableList.<String>of())
            .put(SearchResults.class, ImmutableList.<String>of())
            .put(SolutionReference.class, ImmutableList.<String>of("author"))
            .put(Solutions.class, ImmutableList.<String>of())
            .put(Source.class, ImmutableList.<String>of())
            .put(StackTraceSearch.class, ImmutableList.<String>of("visitorId", "userId", "teamId"))
            .put(StackTraceSearchGroup.class, ImmutableList.<String>of("visitorId", "userId", "teamId"))
            .put(StackTraceWithBreadCrumbs.class, ImmutableList.<String>of())
            .put(TextSearch.class, ImmutableList.<String>of("visitorId", "userId", "teamId", "errorType"))
            .put(Tip.class, ImmutableList.<String>of("via"))
            .put(UserInfo.class, ImmutableList.<String>of("userId", "displayName", "avatarUrl"))
            .put(UserReference.class, ImmutableList.<String>of("avatarUrl"))
            .build();
}
