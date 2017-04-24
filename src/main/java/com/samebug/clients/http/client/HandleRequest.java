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
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URL;

interface HandleRequest<T> {
    HttpRequestBase createRequest();

    T onSuccess(final HttpResponse response);

    T onBadRequest(final HttpResponse response);

    T onError(final SamebugClientException exception);
}

final class Builder {
    private final Config config;
    private final Gson gson;

    private final static boolean defaultAuthenticated = true;
    private final static Object defaultPostData = null;

    Builder(Config config) {
        this.config = config;
        gson = Json.gson;
    }

    public HasUrl at(URL url) {
        return new HasUrl(url);
    }


    public class HasUrl {
        private final URL url;

        private HasUrl(URL url) {
            this.url = url;
        }

        public HasAuth authenticated() {
            return new HasAuth(url, true);
        }

        public HasAuth unauthenticated() {
            return new HasAuth(url, false);
        }

        public <PostDataType> HasPostData<PostDataType> posting(PostDataType postData) {
            return new HasPostData<PostDataType>(url, defaultAuthenticated, postData);
        }

        public <ResponseType> HasResponseType<Object, ResponseType> withResponse(Type responseType) {
            return new HasResponseType<Object, ResponseType>(url, defaultAuthenticated, defaultPostData, responseType);
        }
    }

    public class HasAuth {
        private final URL url;
        private final boolean isAuthenticated;

        private HasAuth(URL url, boolean isAuthenticated) {
            this.url = url;
            this.isAuthenticated = isAuthenticated;
        }

        public <PostDataType> HasPostData<PostDataType> posting(PostDataType postData) {
            return new HasPostData<PostDataType>(url, isAuthenticated, postData);
        }

        public <ResponseType> HasResponseType<Object, ResponseType> withResponse(Type responseType) {
            return new HasResponseType<Object, ResponseType>(url, isAuthenticated, defaultPostData, responseType);
        }
    }

    public class HasPostData<PostDataType> {
        private final URL url;
        private final boolean isAuthenticated;
        private final PostDataType postData;

        private HasPostData(URL url, boolean isAuthenticated, PostDataType postData) {
            this.url = url;
            this.isAuthenticated = isAuthenticated;
            this.postData = postData;
        }

        public <ResponseType> HasResponseType<PostDataType, ResponseType> withResponse(Type responseType) {
            return new HasResponseType<PostDataType, ResponseType>(url, isAuthenticated, postData, responseType);
        }
    }

    public class HasResponseType<PostDataType, ResponseType> {
        private final URL url;
        private final boolean isAuthenticated;
        private final PostDataType postData;
        private final Type responseType;

        private HasResponseType(URL url, boolean isAuthenticated, PostDataType postData, Type responseType) {
            this.url = url;
            this.isAuthenticated = isAuthenticated;
            this.postData = postData;
            this.responseType = responseType;
        }

        public <ErrorType> HasErrorType<PostDataType, ResponseType, ErrorType> withErrors(Type errorType) {
            return new HasErrorType<PostDataType, ResponseType, ErrorType>(url, isAuthenticated, postData, responseType, errorType);
        }

        public HandleGetJson<PostDataType, ResponseType> build() {
            return new HandleGetJson<PostDataType, ResponseType>(url, isAuthenticated, postData, responseType);
        }
    }

    public class HasErrorType<PostDataType, ResponseType, ErrorType> {
        private final URL url;
        private final boolean isAuthenticated;
        private final PostDataType postData;
        private final Type responseType;
        private final Type errorType;

        private HasErrorType(URL url, boolean isAuthenticated, PostDataType postData, Type responseType, Type errorType) {
            this.url = url;
            this.isAuthenticated = isAuthenticated;
            this.postData = postData;
            this.responseType = responseType;
            this.errorType = errorType;
        }

        public HandlePostResponseJson<PostDataType, ResponseType, ErrorType> build() {
            return new HandlePostResponseJson<PostDataType, ResponseType, ErrorType>(url, isAuthenticated, postData, responseType, errorType);
        }
    }


    private abstract class HandleRequestBase<PostDataType, ResponseType> {
        protected URL url;
        protected boolean isAuthenticated;
        protected PostDataType postData;
        protected Type responseType;

        protected HandleRequestBase(URL url, boolean isAuthenticated, PostDataType postData, Type responseType) {
            this.url = url;
            this.isAuthenticated = isAuthenticated;
            this.postData = postData;
            this.responseType = responseType;
        }

        protected HttpRequestBase createRequest() {
            final HttpRequestBase request;
            if (postData == null) {
                request = new HttpGet(url.toString());
            } else {
                request = new HttpPost(url.toString());
                ((HttpPost) request).setEntity(new StringEntity(gson.toJson(postData), Consts.UTF_8));
                request.setHeader("Content-Type", "application/json");
            }
            request.setHeader("Accept", "application/json");

            if (isAuthenticated) {
                request.addHeader("X-Samebug-ApiKey", config.apiKey);
                if (config.workspaceId != null) request.addHeader("X-Samebug-WorkspaceId", config.workspaceId.toString());
            }

            return request;
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

    public final class HandleGetJson<PostDataType, ResponseType> extends HandleRequestBase<PostDataType, ResponseType> implements HandleRequest<GetResponse<ResponseType>> {
        private HandleGetJson(URL url, boolean isAuthenticated, PostDataType postData, Type responseType) {
            super(url, isAuthenticated, postData, responseType);
        }

        @Override
        public HttpRequestBase createRequest() {
            return super.createRequest();
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

    public final class HandlePostResponseJson<PostDataType, ResponseType, ErrorType>
            extends HandleRequestBase<PostDataType, ResponseType> implements HandleRequest<PostFormResponse<ResponseType, ErrorType>> {
        private final Type formErrorType;

        private HandlePostResponseJson(URL url, boolean isAuthenticated, PostDataType postData, Type responseType, Type formErrorType) {
            super(url, isAuthenticated, postData, responseType);
            this.formErrorType = formErrorType;
        }

        @Override
        public HttpRequestBase createRequest() {
            return super.createRequest();
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
