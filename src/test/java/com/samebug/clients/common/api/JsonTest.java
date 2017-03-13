package com.samebug.clients.common.api;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.samebug.clients.common.api.client.FormRestError;
import com.samebug.clients.common.api.client.BasicRestError;
import com.samebug.clients.common.api.client.RestError;
import com.samebug.clients.common.api.entities.*;
import com.samebug.clients.common.api.entities.Exception;
import com.samebug.clients.common.api.entities.search.*;
import com.samebug.clients.common.api.entities.solution.*;
import com.samebug.clients.common.api.json.Json;
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

    // curl 'http://nightly.samebug.com/rest/0.11/checkApiKey?apiKey=355be195-c10b-11e5-a334-000d3a317492' |\
    // jq . > src/test/resources/com/samebug/clients/common/search/api/jsontest/checkApiKey-1.json
    @Test
    public void getUserInfoValid() throws MalformedURLException {
        UserInfo x = gson.fromJson(stream("checkApiKey-1"), UserInfo.class);
        checkFields(x);
        Assert.assertEquals(new URL("https://samebug.io/avatars/1/3"), x.getAvatarUrl());
        Assert.assertEquals("poroszd", x.getDisplayName());
        Assert.assertEquals(Integer.valueOf(1), x.getUserId());
    }

    // curl 'http://nightly.samebug.com/rest/0.11/checkApiKey?apiKey=x' | jq . > src/test/resources/com/samebug/clients/common/search/api/jsontest/checkApiKey-2.json
    @Test
    public void getUserInfoInvalid() {
        UserInfo x = gson.fromJson(stream("checkApiKey-2"), UserInfo.class);
        checkFields(x);
        Assert.assertEquals(false, x.getUserExist());
    }

    // curl 'http://nightly.samebug.com/rest/0.11/search/673467' -H'X-Samebug-ApiKey: 355be195-c10b-11e5-a334-000d3a317492' |\
    // jq . > src/test/resources/com/samebug/clients/common/search/api/jsontest/search-1.json
    @Test
    public void getSolutions() {
        Solutions x = gson.fromJson(stream("search-1"), Solutions.class);
        checkFields(x);
        Assert.assertTrue(x.getTips().size() >= 0);
        Assert.assertTrue(x.getReferences().size() > 0);
    }

    // curl -XPOST 'http://nightly.samebug.com/rest/0.11/tip' -H'X-Samebug-ApiKey: 355be195-c10b-11e5-a334-000d3a317492' \
    // -H'Content-Type: application/x-www-form-urlencoded; charset=UTF-8' -H'Accept: application/json' -H'User-Agent: Samebug-Idea-Client/2.0.0' \
    // -d'searchId=5510034&message=good_job_curl' |\
    // jq . > src/test/resources/com/samebug/clients/common/search/api/jsontest/post-tip-1.json
    @Test
    public void postTip() {
        TypeToken<RestHit<Tip>> typeToken = new TypeToken<RestHit<Tip>>() {
        };
        RestHit<Tip> x = gson.fromJson(stream("post-tip-1"), typeToken.getType());
        checkFields(x);
        Assert.assertTrue(x.getCreatedBy().getId() == 1);
    }


    @Test
    public void restError() {
        BasicRestError x = gson.fromJson(stream("unknownApiKey"), BasicRestError.class);
        Assert.assertTrue(x instanceof RestError);
        checkFields(x);
    }

    @Test
    public void formError() {
        BasicRestError x = gson.fromJson(stream("formError"), BasicRestError.class);
        Assert.assertTrue(x instanceof FormRestError);
        checkFields(x);
    }

    /**
     * Check if any of the fields are null that should not be null.
     * <p>
     * Ideally, this would be handled by the NotNull annotations, but that has a compile time retention.
     * On the other hand, IntelliJ can instrument the code to add runtime assertions based on those annotations,
     * but that only works if you run the test from IntelliJ, and won't work anyway when the object fields
     * are initialized by gson.
     * <p>
     * This solution has a serious drawback of double bookkeeping. Maybe we should check annotation processors,
     * or use an other NotNull annotation with runtime retention.
     */
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
        return new InputStreamReader(getClass().getResourceAsStream("/com/samebug/clients/common/api/jsontest/" + fn + ".json"));
    }

    Map<Class<?>, List<String>> nullableFieldsForEntities = ImmutableMap.<Class<?>, List<String>>builder()
            .put(Author.class, ImmutableList.<String>of("url", "avatarUrl"))
            .put(Exception.class, ImmutableList.<String>of("message"))
            .put(MarkResponse.class, ImmutableList.<String>of("id"))
            .put(BasicRestError.class, ImmutableList.<String>of())
            .put(RestHit.class, ImmutableList.<String>of("markId"))
            .put(RestSolution.class, ImmutableList.<String>of())
            .put(Search.class, ImmutableList.<String>of("visitorId", "userId", "teamId"))
            .put(SearchGroup.class, ImmutableList.<String>of("visitorId", "userId", "teamId"))
            .put(CreatedSearch.class, ImmutableList.<String>of())
            .put(SolutionReference.class, ImmutableList.<String>of("author"))
            .put(Solutions.class, ImmutableList.<String>of())
            .put(Source.class, ImmutableList.<String>of())
            .put(StackTraceSearch.class, ImmutableList.<String>of("visitorId", "userId", "teamId"))
            .put(StackTraceSearchGroup.class, ImmutableList.<String>of("visitorId", "userId", "teamId"))
            .put(StackTraceWithBreadCrumbs.class, ImmutableList.<String>of())
            .put(TextSearch.class, ImmutableList.<String>of("visitorId", "userId", "teamId", "errorType"))
            .put(TextSearchGroup.class, ImmutableList.<String>of("visitorId", "userId", "teamId"))
            .put(Tip.class, ImmutableList.<String>of("via"))
            .put(UserInfo.class, ImmutableList.<String>of("userId", "displayName", "avatarUrl", "defaultWorkspaceId"))
            .put(UserReference.class, ImmutableList.<String>of("avatarUrl"))
            .build();
}
