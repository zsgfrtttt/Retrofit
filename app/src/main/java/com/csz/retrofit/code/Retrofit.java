package com.csz.retrofit.code;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import androidx.annotation.Nullable;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class Retrofit {
    private final Map<Method, ServiceMethod> serviceMethodCache = new ConcurrentHashMap<>();
    private Call.Factory callFactory;
    private HttpUrl baseUrl;

    public Retrofit(Builder builder) {
        this.callFactory = builder.callFactory;
        this.baseUrl = builder.baseUrl;
    }

    public Call.Factory  callFactory(){
        return callFactory;
    }

    public HttpUrl baseUrl(){
        return baseUrl;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(final Class<T> service) {
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[]{service}, new InvocationHandler() {

            @Override
            public @Nullable
            Object invoke(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
                ServiceMethod serviceMethod = loadServiceMethod(method);
                return new OkHttpCall(serviceMethod,args);
            }
        });
    }

    ServiceMethod loadServiceMethod(Method method) {
        ServiceMethod  result = serviceMethodCache.get(method);
        if (result != null) return result;

        synchronized (serviceMethodCache) {
            result = serviceMethodCache.get(method);
            if (result == null) {
                result = new ServiceMethod.Builder(this,method).build();
                serviceMethodCache.put(method, result);
            }
        }
        return result;
    }


    public static class Builder {
        private Call.Factory callFactory;
        private HttpUrl baseUrl;

        public Retrofit.Builder callFactory(okhttp3.Call.Factory factory) {
            this.callFactory = factory;
            return this;
        }

        public Retrofit.Builder baseUrl(String url) throws MalformedURLException {
            baseUrl = HttpUrl.get(new URL(url));
            return this;
        }

        public Retrofit build() {
            if (callFactory == null) {
                callFactory = new OkHttpClient();
            }

            return new Retrofit(this);
        }
    }
}
