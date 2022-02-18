package com.csz.retrofit.code;

import com.csz.retrofit.anno.Field;
import com.csz.retrofit.anno.GET;
import com.csz.retrofit.anno.POST;
import com.csz.retrofit.anno.Query;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import okhttp3.Call;
import okhttp3.HttpUrl;

public class ServiceMethod {

    private final Call.Factory callFactory;
    private final HttpUrl baseUrl;
    private String httpMethod;
    private boolean hasBody;
    private String realUrl;
    private ParameterHandler[] parameterHandlers;

    ServiceMethod(Builder builder) {
        this.callFactory = builder.retrofit.callFactory();
        this.baseUrl = builder.retrofit.baseUrl();
        this.httpMethod = builder.httpMethod;
        this.realUrl = builder.realUrl;
        this.hasBody = builder.hasBody;
        this.parameterHandlers = builder.parameterHandlers;
    }

    public Call toCall(Object[] args) {
        RequestBuilder requestBuilder = new RequestBuilder(httpMethod, baseUrl, realUrl, hasBody);
        ParameterHandler[] handlers = parameterHandlers;

        int argumentCount = args != null ? args.length : 0;
        if (argumentCount != handlers.length) {
            throw new IllegalArgumentException("Argument count (" + argumentCount + ") doesn't match expected count (" + handlers.length + ")");
        }
        for (int p = 0; p < argumentCount; p++) {
            handlers[p].apply(requestBuilder, args[p].toString());
        }
        return callFactory.newCall(requestBuilder.build());
    }

    static final class Builder {
        final Retrofit retrofit;
        final Method method;
        final Annotation[] methodAnnotation;
        final Annotation[][] parameterAnnotationsArray;
        String httpMethod;
        boolean hasBody;
        String realUrl;
        ParameterHandler[] parameterHandlers;

        Builder(Retrofit retrofit, Method method) {
            this.retrofit = retrofit;
            this.method = method;
            this.methodAnnotation = method.getAnnotations();
            this.parameterAnnotationsArray = method.getParameterAnnotations();
        }

        public ServiceMethod build() {
            for (Annotation annotation : methodAnnotation) {
                parseMethodAnnotation(annotation);
            }
            int parameterCount = parameterAnnotationsArray.length;
            parameterHandlers = new ParameterHandler[parameterCount];
            for (int p = 0; p < parameterCount; p++) {
                Annotation[] parameterAnnotations = parameterAnnotationsArray[p];
                if (parameterAnnotations == null) {
                    throw new IllegalArgumentException("No Retrofit annotation found.");
                }
                parameterHandlers[p] = parseParameter(p, parameterAnnotations);
            }
            return new ServiceMethod(this);
        }

        private ParameterHandler parseParameter(int p, Annotation[] annotations) {
            ParameterHandler result = null;
            for (Annotation annotation : annotations) {
                ParameterHandler annotationAction = parseParameterAnnotation(annotation);
                if (annotationAction == null) {
                    continue;
                }
                if (result != null) {
                    //每个参数只允许一个Retrofit的注解
                    throw new IllegalArgumentException("Multiple Retrofit annotations found, only one allowed.");
                }
                result = annotationAction;
            }
            // 每个参数必须包含Retrofit的注解
            if (result == null) {
                throw new NullPointerException("No Retrofit annotation found.");
            }
            return result;
        }

        private ParameterHandler parseParameterAnnotation(Annotation annotation) {
            ParameterHandler handler = null;
            if (annotation instanceof Query) {
                String value = ((Query) annotation).value();
                handler = new ParameterHandler.Query(value);
            } else if (annotation instanceof Field) {
                String value = ((Field) annotation).value();
                handler = new ParameterHandler.Query(value);
            }
            return handler;
        }

        private void parseMethodAnnotation(Annotation annotation) {
            if (annotation instanceof GET) {
                parseHttpMethodPath("GET", ((GET) annotation).value(), false);
            } else if (annotation instanceof POST) {
                parseHttpMethodPath("POST", ((POST) annotation).value(), true);
            }
        }

        private void parseHttpMethodPath(String httpMethod, String value, boolean hasBody) {
            this.httpMethod = httpMethod;
            this.realUrl = value;
            this.hasBody = hasBody;
        }
    }
}
