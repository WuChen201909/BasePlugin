package com.example.baseplugin.http

import com.harrison.plugin.http.RetrofitManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit

class TestServer : RetrofitManager<ApiServer>()
{

    override fun getAPIClass(): Class<ApiServer> {
        return ApiServer::class.java
    }
}