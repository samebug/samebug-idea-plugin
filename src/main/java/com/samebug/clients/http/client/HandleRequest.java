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
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;

interface HandleRequest<T> {
    HttpRequestBase createRequest();

    T onSuccess(HttpResponse response);

    T onBadRequest(HttpResponse response);

    T onError(SamebugClientException exception);
}

final class Builder {
    private final Config config;
    private final Gson gson;

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

        public HasUrl parameter(@NotNull final String key, @NotNull final String value) {
            try {
                return new HasUrl(new URIBuilder(uri).addParameter(key, value).build());
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("Could not add parameter (" + key + " - " + value + ") to uri " + uri, e);
            }
        }

        public HasMethodType<HttpGet> get() {
            return new HasMethodType<HttpGet>(new HttpGet(uri));
        }

        public HasMethodType<HttpPost> post() {
            HttpPost request = new HttpPost(uri);
            return new HasMethodType<HttpPost>(request);
        }

        public HasMethodType<HttpPost> post(Object data) {
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(gson.toJson(data), Consts.UTF_8));
            return new HasMethodType<HttpPost>(request);
        }

        public HasMethodType<HttpPut> put() {
            HttpPut request = new HttpPut(uri);
            request.setHeader("Content-Type", "application/json");
            return new HasMethodType<HttpPut>(request);
        }

        public HasMethodType<HttpDelete> delete() {
            HttpDelete request = new HttpDelete(uri);
            return new HasMethodType<HttpDelete>(request);
        }

    }

    public final class HasMethodType<Method extends HttpRequestBase> {
        private final Method request;

        HasMethodType(Method method) {
            request = method;
            request.setHeader("Accept", "application/json");
            request.setHeader("X-Samebug-ApiKey", config.apiKey);
            if (config.workspaceId != null) request.setHeader("X-Samebug-WorkspaceId", config.workspaceId.toString());
        }

        public HasMethodType<Method> unauthenticated() {
            request.removeHeaders("X-Samebug-ApiKey");
            request.removeHeaders("X-Samebug-WorkspaceId");
            return this;
        }

        public <ResponseType> SimpleResponseHandler<ResponseType> withResponseType(Type responseType) {
            return new SimpleResponseHandler<ResponseType>(request, responseType);
        }

        public <ErrorType> HasErrorType<Method, ErrorType> withFormErrorType(Type formErrorType) {
            return new HasErrorType<Method, ErrorType>(request, formErrorType);
        }
    }

    public final class HasErrorType<Method extends HttpRequestBase, ErrorType> {
        private final Method request;
        private final Type formErrorType;

        HasErrorType(Method request, Type formErrorType) {
            this.request = request;
            this.formErrorType = formErrorType;
        }

        public <ResponseType> BadRequestCapableResponseJson<ResponseType, ErrorType> withResponseType(Type responseType) {
            return new BadRequestCapableResponseJson<ResponseType, ErrorType>(request, responseType, formErrorType);
        }
    }


    private abstract class HandleRequestBase {
        protected HttpRequestBase request;
        protected Type responseType;

        protected HandleRequestBase(HttpRequestBase request, Type responseType) {
            this.request = request;
            this.responseType = responseType;
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

    public final class SimpleResponseHandler<ResponseType> extends HandleRequestBase implements HandleRequest<GetResponse<ResponseType>> {
        private SimpleResponseHandler(HttpRequestBase request, Type responseType) {
            super(request, responseType);
        }

        @Override
        public HttpRequestBase createRequest() {
            return request;
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

    public final class BadRequestCapableResponseJson<ResponseType, ErrorType> extends HandleRequestBase implements HandleRequest<PostFormResponse<ResponseType, ErrorType>> {
        private final Type formErrorType;

        private BadRequestCapableResponseJson(HttpRequestBase request, Type responseType, Type formErrorType) {
            super(request, responseType);
            this.formErrorType = formErrorType;
        }

        @Override
        public HttpRequestBase createRequest() {
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
