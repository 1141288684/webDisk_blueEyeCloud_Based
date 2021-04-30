package com.koader.mywebdisk.httpUtils;

import retrofit2.Retrofit;

public class HttpUtil {

    public HttpMethods getMethods(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(HttpMethods.apiHost).build();
        return retrofit.create(HttpMethods.class);
    }
}
