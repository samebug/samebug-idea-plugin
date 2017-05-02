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
package com.samebug.clients.http.client;

import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.samebug.clients.http.exceptions.BadRequest;
import com.samebug.clients.http.exceptions.HttpError;
import com.samebug.clients.http.exceptions.JsonParseException;
import com.samebug.clients.http.exceptions.SamebugClientException;
import com.samebug.clients.http.json.Json;
import com.samebug.clients.http.response.GetResponse;
import com.samebug.clients.http.response.PostFormResponse;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URI;

interface HandleRequest<T> {
    HttpRequestBase createRequest();

    T onSuccess(HttpResponse response);

    T onBadRequest(HttpResponse response);

    T onError(SamebugClientException exception);
}

final class Builder {
    private final Config config;
    private final Gson gson;

    private static final boolean defaultAuthenticated = true;
    private static final Object defaultPostData = null;

    Builder(Config config) {
        this.config = config;
        gson = Json.gson;
    }

    public HasUrl at(URI uri) {
        return new HasUrl(uri);
    }


    public final class HasUrl {
        private final URI uri;

        private HasUrl(URI uri) {
            this.uri = uri;
        }

        public HasAuth authenticated() {
            return new HasAuth(uri, true);
        }

        public HasAuth unauthenticated() {
            return new HasAuth(uri, false);
        }

        public <ResponseType> HasResponseType<ResponseType> withResponse(Type responseType) {
            return new HasResponseType<ResponseType>(uri, defaultAuthenticated, responseType);
        }
    }

    public final class HasAuth {
        private final URI uri;
        private final boolean isAuthenticated;

        private HasAuth(URI uri, boolean isAuthenticated) {
            this.uri = uri;
            this.isAuthenticated = isAuthenticated;
        }

        public <ResponseType> HasResponseType<ResponseType> withResponse(Type responseType) {
            return new HasResponseType<ResponseType>(uri, isAuthenticated, responseType);
        }
    }

    public final class HasResponseType<ResponseType> {
        private final URI uri;
        private final boolean isAuthenticated;
        private final Type responseType;

        private HasResponseType(URI uri, boolean isAuthenticated, Type responseType) {
            this.uri = uri;
            this.isAuthenticated = isAuthenticated;
            this.responseType = responseType;
        }

        public NotSendingHandler<ResponseType> buildGet() {
            return new NotSendingHandler<ResponseType>(uri, isAuthenticated, responseType, new HttpGet());
        }

        public NotSendingHandler<ResponseType> buildPost() {
            return new NotSendingHandler<ResponseType>(uri, isAuthenticated, responseType, new HttpPost());
        }

        public NotSendingHandler<ResponseType> buildPut() {
            return new NotSendingHandler<ResponseType>(uri, isAuthenticated, responseType, new HttpPut());
        }

        public <PostDataType> HasPostData<ResponseType, PostDataType> posting(PostDataType postData) {
            return new HasPostData<ResponseType, PostDataType>(uri, isAuthenticated, responseType, postData);
        }
    }

    public final class HasPostData<ResponseType, PostDataType> {
        private final URI uri;
        private final boolean isAuthenticated;
        private final Type responseType;
        private final PostDataType postData;

        private HasPostData(URI uri, boolean isAuthenticated, Type responseType, PostDataType postData) {
            this.uri = uri;
            this.isAuthenticated = isAuthenticated;
            this.responseType = responseType;
            this.postData = postData;
        }

        public SimplePostHandler<ResponseType, PostDataType> build() {
            return new SimplePostHandler<ResponseType, PostDataType>(uri, isAuthenticated, responseType, postData);
        }

        public <ErrorType> HasErrorType<PostDataType, ResponseType, ErrorType> withErrors(Type errorType) {
            return new HasErrorType<PostDataType, ResponseType, ErrorType>(uri, isAuthenticated, responseType, postData, errorType);
        }
    }

    public final class HasErrorType<PostDataType, ResponseType, ErrorType> {
        private final URI uri;
        private final boolean isAuthenticated;
        private final Type responseType;
        private final PostDataType postData;
        private final Type errorType;

        private HasErrorType(URI uri, boolean isAuthenticated, Type responseType, PostDataType postData, Type errorType) {
            this.uri = uri;
            this.isAuthenticated = isAuthenticated;
            this.responseType = responseType;
            this.postData = postData;
            this.errorType = errorType;
        }

        public HandlePostResponseJson<PostDataType, ResponseType, ErrorType> buildPut() {
            return new HandlePostResponseJson<PostDataType, ResponseType, ErrorType>(uri, isAuthenticated, postData, responseType, errorType, "PUT");
        }

        public HandlePostResponseJson<PostDataType, ResponseType, ErrorType> buildPost() {
            return new HandlePostResponseJson<PostDataType, ResponseType, ErrorType>(uri, isAuthenticated, postData, responseType, errorType, "POST");
        }
    }


    private abstract class HandleRequestBase<ResponseType> {
        protected URI uri;
        protected boolean isAuthenticated;
        protected Type responseType;

        protected HandleRequestBase(URI uri, boolean isAuthenticated, Type responseType) {
            this.uri = uri;
            this.isAuthenticated = isAuthenticated;
            this.responseType = responseType;
        }

        public final HttpRequestBase createRequest() {
            HttpRequestBase request = initiateRequest();
            changeRequest(request);
            return request;
        }

        protected abstract HttpRequestBase initiateRequest();

        protected final void changeRequest(HttpRequestBase request) {
            request.setHeader("Accept", "application/json");

            if (isAuthenticated) {
                request.addHeader("X-Samebug-ApiKey", config.apiKey);
                if (config.workspaceId != null) request.addHeader("X-Samebug-WorkspaceId", config.workspaceId.toString());
            }
        }


        protected <T> T readJsonResponse(HttpResponse response, Type classOfT) throws HttpError, JsonParseException {
            InputStream content = null;
            Reader reader = null;
            String json = null;
            try {
                content = response.getEntity().getContent();
                reader = new InputStreamReader(content, "UTF-8");
                if (config.isJsonDebugEnabled) {
                    json = CharStreams.toString(reader);
                    return gson.fromJson(json, classOfT);
                } else {
                    return gson.fromJson(reader, classOfT);
                }
            } catch (com.google.gson.JsonParseException e) {
                throw new JsonParseException(json, e);
            } catch (IOException e) {
                throw new HttpError(e);
            } finally {
                try {
                    if (content != null) content.close();
                    if (reader != null) reader.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public abstract class SimpleResponseHandler<ResponseType> extends HandleRequestBase<ResponseType> implements HandleRequest<GetResponse<ResponseType>> {
        private SimpleResponseHandler(URI uri, boolean isAuthenticated, Type responseType) {
            super(uri, isAuthenticated, responseType);
        }

        @Override
        public GetResponse<ResponseType> onSuccess(HttpResponse httpResponse) {
            try {
                ResponseType response = readJsonResponse(httpResponse, responseType);
                return new GetResponse<ResponseType>(response);
            } catch (JsonParseException e) {
                return new GetResponse<ResponseType>(new JsonParseException("Failed to parse json response", e));
            } catch (HttpError httpError) {
                return new GetResponse<ResponseType>(httpError);
            }
        }

        @Override
        public GetResponse<ResponseType> onBadRequest(HttpResponse response) {
            return new GetResponse<ResponseType>(new BadRequest());
        }

        @Override
        public GetResponse<ResponseType> onError(SamebugClientException exception) {
            return new GetResponse<ResponseType>(exception);
        }
    }

    public final class NotSendingHandler<ResponseType> extends SimpleResponseHandler<ResponseType> {
        HttpRequestBase requestMethod;

        private NotSendingHandler(URI uri, boolean isAuthenticated, Type responseType, HttpRequestBase requestMethod) {
            super(uri, isAuthenticated, responseType);
            this.requestMethod = requestMethod;
        }

        public HttpRequestBase initiateRequest() {
            requestMethod.setURI(uri);
            return requestMethod;
        }
    }

    public final class SimplePostHandler<ResponseType, PostDataType> extends SimpleResponseHandler<ResponseType> {
        PostDataType postData;

        private SimplePostHandler(URI uri, boolean isAuthenticated, Type responseType, PostDataType postData) {
            super(uri, isAuthenticated, responseType);
            this.postData = postData;
        }

        public HttpRequestBase initiateRequest() {
            HttpRequestBase request = new HttpPost(uri);
            ((HttpPost) request).setEntity(new StringEntity(gson.toJson(postData), Consts.UTF_8));
            request.setHeader("Content-Type", "application/json");
            return request;
        }
    }

    public final class HandlePostResponseJson<PostDataType, ResponseType, ErrorType>
            extends HandleRequestBase<ResponseType> implements HandleRequest<PostFormResponse<ResponseType, ErrorType>> {
        private final Type formErrorType;
        private final PostDataType postData;
        private final HttpRequestBase request;

        private HandlePostResponseJson(URI uri, boolean isAuthenticated, PostDataType postData, Type responseType, Type formErrorType, String requestMethod) {
            super(uri, isAuthenticated, responseType);
            this.formErrorType = formErrorType;
            this.postData = postData;
            if ("POST".equals(requestMethod)) {
                request = new HttpPost(uri);
                if (postData != null) ((HttpPost) request).setEntity(new StringEntity(gson.toJson(postData), Consts.UTF_8));
            } else if ("PUT".equals(requestMethod)) {
                request = new HttpPut(uri);
                if (postData != null) ((HttpPut) request).setEntity(new StringEntity(gson.toJson(postData), Consts.UTF_8));
            } else throw new IllegalArgumentException("Change requestMethod parameter so it's not stringly typed");
            if (postData != null) request.setHeader("Content-Type", "application/json");
        }

        @Override
        public HttpRequestBase initiateRequest() {
            return request;
        }

        @Override
        public PostFormResponse<ResponseType, ErrorType> onSuccess(HttpResponse httpResponse) {
            try {
                ResponseType response = readJsonResponse(httpResponse, responseType);
                return PostFormResponse.fromResult(response);
            } catch (JsonParseException e) {
                SamebugClientException exception = new JsonParseException("Failed to parse json response", e);
                return PostFormResponse.fromException(exception);
            } catch (HttpError httpError) {
                return PostFormResponse.fromException(httpError);
            }
        }

        @Override
        public PostFormResponse<ResponseType, ErrorType> onBadRequest(HttpResponse httpResponse) {
            try {
                ErrorType response = readJsonResponse(httpResponse, formErrorType);
                return PostFormResponse.fromFormError(response);
            } catch (JsonParseException e) {
                SamebugClientException exception = new JsonParseException("Failed to parse json response", e);
                return PostFormResponse.fromException(exception);
            } catch (HttpError httpError) {
                return PostFormResponse.fromException(httpError);
            }
        }

        @Override
        public PostFormResponse<ResponseType, ErrorType> onError(SamebugClientException exception) {
            return PostFormResponse.fromException(exception);
        }
    }
}
