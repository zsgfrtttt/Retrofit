package com.csz.retrofit.code;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpCall implements Call {
    private final ServiceMethod serviceMethod;
    private final Object[] args;
    private final Call rawCall;

    public OkHttpCall(ServiceMethod serviceMethod, Object[] args) {
        this.serviceMethod = serviceMethod;
        this.args = args;
        this.rawCall =  serviceMethod.toCall(args);
    }

    @Override
    public Response execute() throws IOException {
        return rawCall.execute();
    }

    @Override
    public void enqueue(Callback callback) {
        rawCall.enqueue(callback);
    }

    @Override
    public boolean isExecuted() {
        return rawCall.isExecuted();
    }

    @Override
    public void cancel() {
        rawCall.cancel();
    }

    @Override
    public boolean isCanceled() {
        return rawCall.isCanceled();
    }

    @Override
    public Call clone() {
        return rawCall.clone();
    }

    @Override
    public Request request() {
        return rawCall.request();
    }

}
