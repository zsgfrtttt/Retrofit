package com.csz.retrofit.code;

abstract class ParameterHandler {
    abstract void apply(RequestBuilder builder,String value);

    static class Query extends ParameterHandler{

        final String name;

        Query(String name) {
            this.name = name;
        }

        @Override
        void apply(RequestBuilder builder, String value) {
            builder.addQueryParam(name,value);
        }
    }

    static class Field extends ParameterHandler{

        final String name;

        Field(String name) {
            this.name = name;
        }

        @Override
        void apply(RequestBuilder builder, String value) {
            builder.addFormField(name,value);
        }
    }
}
