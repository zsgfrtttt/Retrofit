package com.csz.retrofit.code;

import androidx.annotation.Nullable;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RequestBuilder {

    private final String method;
    private final HttpUrl baseUrl;
    private @Nullable String relativeUrl;
    private @Nullable HttpUrl.Builder urlBuilder;

    private final Request.Builder requestBuilder;

    private final boolean hasBody;
    private @Nullable FormBody.Builder formBuilder;

    public RequestBuilder(String httpMethod, HttpUrl baseUrl, String realUrl, boolean hasBody) {
        this.method = httpMethod;
        this.baseUrl = baseUrl;
        this.relativeUrl = realUrl;
        this.requestBuilder = new Request.Builder();
        this.hasBody = hasBody;
        if (hasBody) {
            formBuilder = new FormBody.Builder();
        }
    }

    public void addQueryParam(String name, String value) {
        if (relativeUrl != null) {
            urlBuilder = baseUrl.newBuilder(relativeUrl);
            if (urlBuilder == null) {
                throw new IllegalArgumentException(
                        "Malformed URL. Base: " + baseUrl + ", Relative: " + relativeUrl);
            }
            relativeUrl = null;
        }
        urlBuilder.addQueryParameter(name, value);
    }

    public void addFormField(String name, String value) {
        formBuilder.add(name, value);
    }

    public Request build() {
        HttpUrl url;
        HttpUrl.Builder urlBuilder = this.urlBuilder;
        if (urlBuilder != null) {
            url = urlBuilder.build();
        } else {
            url = baseUrl.resolve(relativeUrl);
            if (url == null) {
                throw new IllegalArgumentException(
                        "Malformed URL. Base: " + baseUrl + ", Relative: " + relativeUrl);
            }
        }

        RequestBody body = null;
        if (formBuilder != null) {
            body = formBuilder.build();
        }
        return requestBuilder
                .url(url)
                .method(method, body)
                .build();
    }
}
